package com.saf.transactionservice.controller;

import com.saf.core.ActorRef;
import com.saf.transactionservice.actor.messages.TransactionMessages.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private ActorRef transactionActor;

    @PostMapping("/acheter")
    public ResponseEntity<?> acheterDirect(@RequestBody Map<String, Long> request) {
        try {
            Long annonceId = request.get("annonceId");
            Long acheteurId = request.get("acheteurId");

            if (annonceId == null || acheteurId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "annonceId et acheteurId requis"));
            }

            Object response = transactionActor.ask(
                    new AchatDirect(annonceId, acheteurId),
                    Duration.ofSeconds(10)).get(10, TimeUnit.SECONDS);

            if (response instanceof TransactionCreated result) {
                return ResponseEntity.ok(Map.of(
                        "message", "Achat réussi ! Le vendeur a été notifié.",
                        "transactionId", result.transaction().getId(),
                        "prix", result.transaction().getPrix()));
            } else if (response instanceof TransactionOperationError error) {
                return ResponseEntity.badRequest().body(Map.of("error", error.error()));
            }

            return ResponseEntity.status(500).body(Map.of("error", "Erreur inconnue"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable Long id) {
        try {
            Object response = transactionActor.ask(new GetTransaction(id), Duration.ofSeconds(5))
                    .get(5, TimeUnit.SECONDS);

            if (response instanceof TransactionResult result) {
                return ResponseEntity.ok(result.transaction());
            } else if (response instanceof TransactionOperationError error) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTransactionsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "false") boolean asVendeur) {
        try {
            Object response = transactionActor.ask(
                    new GetTransactionsByUser(userId, asVendeur),
                    Duration.ofSeconds(5)).get(5, TimeUnit.SECONDS);

            if (response instanceof TransactionsList result) {
                return ResponseEntity.ok(result.transactions());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }

    @GetMapping("/acheteur/{acheteurId}")
    public ResponseEntity<?> getTransactionsByAcheteur(@PathVariable Long acheteurId) {
        try {
            Object response = transactionActor.ask(
                    new GetTransactionsByUser(acheteurId, false),
                    Duration.ofSeconds(5)).get(5, TimeUnit.SECONDS);

            if (response instanceof TransactionsList result) {
                return ResponseEntity.ok(result.transactions());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }

    @GetMapping("/vendeur/{vendeurId}")
    public ResponseEntity<?> getTransactionsByVendeur(@PathVariable Long vendeurId) {
        try {
            Object response = transactionActor.ask(
                    new GetTransactionsByUser(vendeurId, true),
                    Duration.ofSeconds(5)).get(5, TimeUnit.SECONDS);

            if (response instanceof TransactionsList result) {
                return ResponseEntity.ok(result.transactions());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }
}
