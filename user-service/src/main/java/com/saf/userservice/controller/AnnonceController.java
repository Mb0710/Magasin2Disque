package com.saf.userservice.controller;

import com.saf.core.ActorRef;
import com.saf.userservice.actor.messages.AnnonceMessages.*;
import com.saf.userservice.model.Annonce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/annonces")
@CrossOrigin(origins = "*")
public class AnnonceController {

    @Autowired
    private ActorRef annonceActor;

    @GetMapping
    public ResponseEntity<?> getAllAnnonces() {
        try {
            Object response = annonceActor.ask(
                    new GetAllAnnoncesDisponibles(),
                    Duration.ofSeconds(5)).get(5, TimeUnit.SECONDS);

            if (response instanceof AnnoncesList result) {
                return ResponseEntity.ok(result.annonces());
            }

            return ResponseEntity.ok(List.of());

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAnnonceById(@PathVariable Long id) {
        try {
            Object response = annonceActor.ask(new GetAnnonce(id), Duration.ofSeconds(5))
                    .get(5, TimeUnit.SECONDS);

            if (response instanceof AnnonceResult result) {
                return ResponseEntity.ok(result.annonce());
            } else if (response instanceof AnnonceOperationError error) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAnnonces(@RequestParam String q) {
        try {
            Object response = annonceActor.ask(new SearchAnnonces(q), Duration.ofSeconds(5))
                    .get(5, TimeUnit.SECONDS);

            if (response instanceof AnnoncesList result) {
                return ResponseEntity.ok(result.annonces());
            }

            return ResponseEntity.ok(List.of());

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<?> getAnnoncesByGenre(@PathVariable String genre) {
        try {
            Object response = annonceActor.ask(new GetAnnoncesByGenre(genre), Duration.ofSeconds(5))
                    .get(5, TimeUnit.SECONDS);

            if (response instanceof AnnoncesList result) {
                return ResponseEntity.ok(result.annonces());
            }

            return ResponseEntity.ok(List.of());

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createAnnonce(@RequestBody Annonce annonce) {
        try {
            Object response = annonceActor.ask(new CreateAnnonce(annonce), Duration.ofSeconds(5))
                    .get(5, TimeUnit.SECONDS);

            if (response instanceof AnnonceCreated result) {
                return ResponseEntity.ok(Map.of(
                        "message", "Annonce créée avec succès",
                        "annonceId", result.annonceId()));
            } else if (response instanceof AnnonceOperationError error) {
                return ResponseEntity.badRequest().body(Map.of("error", error.error()));
            }

            return ResponseEntity.status(500).body(Map.of("error", "Erreur inconnue"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnnonce(@PathVariable Long id, @RequestBody Annonce annonce) {
        try {
            Object response = annonceActor.ask(
                    new UpdateAnnonce(id, annonce),
                    Duration.ofSeconds(5)).get(5, TimeUnit.SECONDS);

            if (response instanceof AnnonceOperationSuccess result) {
                return ResponseEntity.ok(Map.of("message", result.message()));
            } else if (response instanceof AnnonceOperationError error) {
                return ResponseEntity.badRequest().body(Map.of("error", error.error()));
            }

            return ResponseEntity.status(500).body(Map.of("error", "Erreur inconnue"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnnonce(@PathVariable Long id) {
        try {
            Object response = annonceActor.ask(new DeleteAnnonce(id), Duration.ofSeconds(5))
                    .get(5, TimeUnit.SECONDS);

            if (response instanceof AnnonceOperationSuccess result) {
                return ResponseEntity.ok(Map.of("message", result.message()));
            }

            return ResponseEntity.ok(Map.of("message", "Annonce supprimée"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }
}
