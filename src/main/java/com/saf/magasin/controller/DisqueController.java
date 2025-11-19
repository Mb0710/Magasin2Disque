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

import com.saf.magasin.model.Disque;
import com.saf.magasin.model.User;
import com.saf.magasin.service.DisqueService;
import com.saf.magasin.service.UserService;

@RestController
@RequestMapping("/api/disques")
@CrossOrigin(origins = "*")
public class DisqueController {
    
    @Autowired
    private DisqueService disqueService;

    @Autowired
    private UserService userService;
    
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
    
    @PostMapping
    public ResponseEntity<?> createDisque(@RequestBody Disque disque) {
        try {
            // Vérifier et attacher le vendeur via UserService pour éviter l'erreur de valeur transiente
            final Long vendeurId = disque.getVendeurId();

            if (vendeurId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le champ 'vendeurId' est requis."));
            }

            User vendeur = userService.findById(vendeurId)
                .orElseThrow(() -> new RuntimeException("Vendeur introuvable (id=" + vendeurId + ")"));

            disque.setVendeur(vendeur);

            Disque created = disqueService.createDisque(disque);
            return ResponseEntity.ok(Map.of(
                "message", "Disque créé avec succès",
                "disqueId", created.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
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
                    // Gérer disponible (booléen) : metttre à jour même si false (c'est un champ important)
                    existing.setDisponible(disque.isDisponible());
                    
                    disqueService.updateDisque(existing);
                    return ResponseEntity.ok(Map.of("message", "Disque mis à jour"));
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDisque(@PathVariable Long id) {
        disqueService.deleteDisque(id);
        return ResponseEntity.ok(Map.of("message", "Disque supprimé"));
    }
}
