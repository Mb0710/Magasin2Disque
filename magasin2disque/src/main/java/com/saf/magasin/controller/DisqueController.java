package com.saf.magasin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.saf.magasin.model.Disque;
import com.saf.magasin.service.DisqueService;

/**
 * Contrôleur REST pour la gestion des disques.
 * Délègue les appels à user-service pour récupérer les vendeurs.
 * 
 * @author SAF - Team
 * @version 2.0
 */
@RestController
@RequestMapping("/api/disques")
@CrossOrigin(origins = "*")
public class DisqueController {
    
    @Autowired
    private DisqueService disqueService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String USER_SERVICE_URL = "http://localhost:8081/api/users";
    
    @GetMapping
    public ResponseEntity<List<Disque>> getAllDisques() {
        return ResponseEntity.ok(disqueService.getDisquesDisponibles());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getDisqueById(@PathVariable Long id) {
        return disqueService.getDisqueById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Disque>> searchDisques(@RequestParam String q) {
        return ResponseEntity.ok(disqueService.searchByTitre(q));
    }
    
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Disque>> getDisquesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(disqueService.getDisquesByGenre(genre));
    }
    
    /**
     * Crée un nouveau disque.
     * Récupère le vendeur via user-service en utilisant vendeurId.
     * 
     * @param disque les données du disque (doit inclure vendeurId)
     * @return ResponseEntity avec l'ID du disque créé
     */
    @PostMapping
    public ResponseEntity<?> createDisque(@RequestBody Disque disque) {
        try {
            final Long vendeurId = disque.getVendeurId();

            if (vendeurId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le champ 'vendeurId' est requis. (ici ==null)"));
            }

            // Appeler user-service pour récupérer le vendeur
            ResponseEntity<Map> userResponse = restTemplate.getForEntity(
                USER_SERVICE_URL + "/" + vendeurId,
                Map.class
            );
            
            if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Vendeur introuvable (id=" + vendeurId + ")"));
            }
            
            // Récupérer les données du vendeur depuis la réponse
            Map<String, Object> vendeurData = userResponse.getBody();
            // Note: On stocke juste vendeurId dans le disque pour éviter la sérialisation circulaire
            // Le vendeur complet sera récupéré au besoin via user-service
            disque.setVendeurId(vendeurId);
            Disque created = disqueService.createDisque(disque);
            return ResponseEntity.ok(Map.of(
                "message", "Disque créé avec succès",
                "disqueId", created.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Met à jour un disque existant.
     * Fusionne les champs fournis avec les champs existants.
     * 
     * @param id l'ID du disque
     * @param disque les données à mettre à jour
     * @return ResponseEntity avec un message de succès
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDisque(@PathVariable Long id, @RequestBody Disque disque) {
        try {
            return disqueService.getDisqueById(id)
                .map(existing -> {
                    // Fusionner les champs : mettre à jour seulement les champs non-null du disque reçu
                    if (disque.getTitre() != null) existing.setTitre(disque.getTitre());
                    if (disque.getArtiste() != null) existing.setArtiste(disque.getArtiste());
                    if (disque.getGenre() != null) existing.setGenre(disque.getGenre());
                    if (disque.getAnneeSortie() != null) existing.setAnneeSortie(disque.getAnneeSortie());
                    if (disque.getPrix() != null) existing.setPrix(disque.getPrix());
                    if (disque.getStock() != null) existing.setStock(disque.getStock());
                    if (disque.getDescription() != null) existing.setDescription(disque.getDescription());
                    if (disque.getImageUrl() != null) existing.setImageUrl(disque.getImageUrl());
                    if (disque.getEtat() != null) existing.setEtat(disque.getEtat());
                    // Gérer disponible (booléen)
                    existing.setDisponible(disque.isDisponible());
                    
                    disqueService.updateDisque(existing);
                    return ResponseEntity.ok(Map.of("message", "Disque mis à jour"));
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Supprime un disque.
     * 
     * @param id l'ID du disque
     * @return ResponseEntity avec un message de succès
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDisque(@PathVariable Long id) {
        disqueService.deleteDisque(id);
        return ResponseEntity.ok(Map.of("message", "Disque supprimé"));
    }
}
