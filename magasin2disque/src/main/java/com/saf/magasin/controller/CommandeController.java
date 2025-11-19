package com.saf.magasin.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.saf.magasin.model.Commande;
import com.saf.magasin.service.CommandeService;
import com.saf.magasin.service.DisqueService;

/**
 * Contrôleur REST pour gérer les commandes d'achat.
 * 
 * Endpoints :
 * - GET /api/commandes : lister toutes les commandes
 * - GET /api/commandes/{id} : détail d'une commande
 * - POST /api/commandes : créer une commande
 * - POST /api/commandes/acheter : acheter un disque (crée une transaction + met à jour disque)
 * - PUT /api/commandes/{id} : mettre à jour une commande
 * 
 * @author SAF - Team
 * @version 1.0
 * @since 2025-11-19
 */
@RestController
@RequestMapping("/api/commandes")
@CrossOrigin(origins = "*")
public class CommandeController {
    
    @Autowired
    private CommandeService commandeService;
    
    @Autowired
    private DisqueService disqueService;
    
    /**
     * RestTemplate pour faire des appels HTTP vers le transaction-service
     */
    private final RestTemplate restTemplate = new RestTemplate();
    
    @GetMapping
    public ResponseEntity<List<Commande>> getAllCommandes() {
        return ResponseEntity.ok(commandeService.getAllCommandes());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommandeById(@PathVariable Long id) {
        return commandeService.getCommandeById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createCommande(@RequestBody Commande commande) {
        try {
            Commande created = commandeService.createCommande(commande);
            return ResponseEntity.ok(Map.of(
                "message", "Commande créée avec succès",
                "commandeId", created.getId(),
                "numeroCommande", created.getNumeroCommande()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Endpoint d'achat d'un disque via le transaction-service.
     * 
     * Procédure :
     * 1. Valide l'existence de l'acheteur via user-service
     * 2. Valide l'existence du disque
     * 3. Appelle le transaction-service pour créer la transaction
     * 4. Met à jour le disque (disponible = false)
     * 5. Retourne un reçu avec les détails de la transaction
     * 
     * Endpoint : POST /api/commandes/acheter
     * 
     * Corps JSON requis :
     * {
     *   "buyerId": 1,
     *   "disqueId": 5
     * }
     * 
     * @param body Map contenant buyerId et disqueId
     * @return ResponseEntity contenant les détails de la transaction ou un message d'erreur
     */
    @PostMapping("/acheter")
    public ResponseEntity<?> acheterDisque(@RequestBody Map<String, Object> body) {
        try {
            // Extraire et valider les paramètres
            Long buyerId = Long.valueOf(body.get("buyerId").toString());
            Long disqueId = Long.valueOf(body.get("disqueId").toString());
            
            if (buyerId <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "buyerId invalide"));
            }
            if (disqueId <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "disqueId invalide"));
            }
            
            // Vérifier que l'acheteur existe via user-service
            String userServiceUrl = "http://localhost:8081/api/users/" + buyerId;
            try {
                restTemplate.getForObject(userServiceUrl, Map.class);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Acheteur introuvable (id=" + buyerId + ")"));
            }
            
            // Vérifier que le disque existe
            var disque = disqueService.getDisqueById(disqueId);
            if (disque.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Disque introuvable"));
            }
            
            // Récupérer le prix du disque
            BigDecimal prix = disque.get().getPrix();
            
            // Appeler le transaction-service pour créer la transaction
            Map<String, Object> transactionRequest = Map.of(
                    "buyerId", buyerId,
                    "disqueId", disqueId,
                    "amount", prix,
                    "description", "Achat du disque : " + disque.get().getTitre()
            );
            
            // URL du transaction-service (localhost:8082 par défaut)
            String transactionServiceUrl = "http://localhost:8082/api/transactions";
            
            Map<String, Object> transactionResponse = restTemplate.postForObject(
                    transactionServiceUrl,
                    transactionRequest,
                    Map.class
            );
            
            if (transactionResponse == null || !transactionResponse.containsKey("transactionId")) {
                return ResponseEntity.status(500).body(Map.of("error", "Erreur lors de la création de la transaction"));
            }
            
            // Mettre à jour le disque : le marquer comme indisponible
            disque.get().setDisponible(false);
            disqueService.updateDisque(disque.get());
            
            // Retourner un reçu d'achat
            return ResponseEntity.ok(Map.of(
                    "message", "Achat effectué avec succès",
                    "transactionId", transactionResponse.get("transactionId"),
                    "disque", Map.of(
                            "id", disque.get().getId(),
                            "titre", disque.get().getTitre(),
                            "artiste", disque.get().getArtiste(),
                            "prix", disque.get().getPrix()
                    ),
                    "buyer", Map.of(
                            "id", buyerId
                    )
            ));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Format de données invalide"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur : " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCommande(@PathVariable Long id, @RequestBody Commande commande) {
        return commandeService.getCommandeById(id)
            .map(existing -> {
                commande.setId(id);
                commandeService.updateCommande(commande);
                return ResponseEntity.ok(Map.of("message", "Commande mise à jour"));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
