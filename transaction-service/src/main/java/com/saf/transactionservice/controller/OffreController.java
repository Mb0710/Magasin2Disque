package com.saf.transactionservice.controller;

import com.saf.core.ActorRef;
import com.saf.transactionservice.actor.messages.OffreMessages.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/offres")
@CrossOrigin(origins = "*")
public class OffreController {

    @Autowired
    private ActorRef offreActor;

    @PostMapping
    public ResponseEntity<?> faireOffre(@RequestBody Map<String, Object> request) {
        try {
            Long annonceId = ((Number) request.get("annonceId")).longValue();
            Long acheteurId = ((Number) request.get("acheteurId")).longValue();
            BigDecimal prixPropose = new BigDecimal(request.get("prixPropose").toString());
            String message = (String) request.get("message");

            Object response = offreActor.ask(
                    new FaireOffre(annonceId, acheteurId, prixPropose, message),
                    Duration.ofSeconds(5)).get(5, TimeUnit.SECONDS);

            if (response instanceof OffreCreated result) {
                return ResponseEntity.ok(Map.of(
                        "message", "Offre envoyée ! Le vendeur a été notifié.",
                        "offreId", result.offre().getId()));
            } else if (response instanceof OffreOperationError error) {
                return ResponseEntity.badRequest().body(Map.of("error", error.error()));
            }

            return ResponseEntity.status(500).body(Map.of("error", "Erreur inconnue"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/accepter")
    public ResponseEntity<?> accepterOffre(@PathVariable Long id) {
        try {
            Object response = offreActor.ask(
                    new AccepterOffre(id),
                    Duration.ofSeconds(10)).get(10, TimeUnit.SECONDS);

            if (response instanceof OffreAccepted result) {
                return ResponseEntity.ok(Map.of(
                        "message", "Offre acceptée ! L'acheteur a été notifié.",
                        "offre", result.offre()));
            } else if (response instanceof OffreOperationError error) {
                return ResponseEntity.badRequest().body(Map.of("error", error.error()));
            }

            return ResponseEntity.status(500).body(Map.of("error", "Erreur inconnue"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/refuser")
    public ResponseEntity<?> refuserOffre(@PathVariable Long id) {
        try {
            Object response = offreActor.ask(new RefuserOffre(id), Duration.ofSeconds(5))
                    .get(5, TimeUnit.SECONDS);

            if (response instanceof OffreRefused result) {
                return ResponseEntity.ok(Map.of(
                        "message", "Offre refusée. L'acheteur a été notifié.",
                        "offre", result.offre()));
            } else if (response instanceof OffreOperationError error) {
                return ResponseEntity.badRequest().body(Map.of("error", error.error()));
            }

            return ResponseEntity.status(500).body(Map.of("error", "Erreur inconnue"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }

    @GetMapping("/vendeur/{vendeurId}/pending")
    public ResponseEntity<?> getOffresPendingForVendeur(@PathVariable Long vendeurId) {
        try {
            Object response = offreActor.ask(
                    new GetOffresPendingForVendeur(vendeurId),
                    Duration.ofSeconds(5)).get(5, TimeUnit.SECONDS);

            if (response instanceof OffresList result) {
                return ResponseEntity.ok(result.offres());
            }

            return ResponseEntity.ok(java.util.List.of());

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }

    @GetMapping("/acheteur/{acheteurId}")
    public ResponseEntity<?> getOffresForAcheteur(@PathVariable Long acheteurId) {
        try {
            Object response = offreActor.ask(
                    new GetOffresForAcheteur(acheteurId),
                    Duration.ofSeconds(5)).get(5, TimeUnit.SECONDS);

            if (response instanceof OffresList result) {
                return ResponseEntity.ok(result.offres());
            }

            return ResponseEntity.ok(java.util.List.of());

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }
}
