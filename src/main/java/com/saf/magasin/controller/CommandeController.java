package com.saf.magasin.controller;

import com.saf.magasin.model.Commande;
import com.saf.magasin.service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commandes")
@CrossOrigin(origins = "*")
public class CommandeController {
    
    @Autowired
    private CommandeService commandeService;
    
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
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCommande(@PathVariable Long id, @RequestBody Commande commande) {
        return commandeService.getCommandeById(id)
            .map(existing -> {
                commande.setId(id);
                Commande updated = commandeService.updateCommande(commande);
                return ResponseEntity.ok(Map.of("message", "Commande mise à jour"));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
