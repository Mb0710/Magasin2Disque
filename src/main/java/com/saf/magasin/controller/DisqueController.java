package com.saf.magasin.controller;

import com.saf.magasin.model.Disque;
import com.saf.magasin.service.DisqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/disques")
@CrossOrigin(origins = "*")
public class DisqueController {
    
    @Autowired
    private DisqueService disqueService;
    
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
        return disqueService.getDisqueById(id)
            .map(existing -> {
                disque.setId(id);
                Disque updated = disqueService.updateDisque(disque);
                return ResponseEntity.ok(Map.of("message", "Disque mis à jour"));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDisque(@PathVariable Long id) {
        disqueService.deleteDisque(id);
        return ResponseEntity.ok(Map.of("message", "Disque supprimé"));
    }
}
