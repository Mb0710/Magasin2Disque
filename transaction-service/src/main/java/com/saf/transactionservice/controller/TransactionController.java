package com.saf.transactionservice.controller;

import com.saf.transactionservice.model.Transaction;
import com.saf.transactionservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour les opérations sur les transactions.
 * 
 * Expose des endpoints HTTP pour :
 * - Créer une transaction d'achat
 * - Récupérer les transactions
 * - Mettre à jour le statut
 * - Supprimer une transaction
 * 
 * Tous les endpoints retournent des réponses JSON.
 * Les routes sont accessibles via /api/transactions/...
 * 
 * @author SAF - Team
 * @version 1.0
 * @since 2025-11-19
 */
@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    /**
     * Service injecté pour accéder à la logique métier des transactions
     */
    @Autowired
    private TransactionService transactionService;
    
    // ============ ENDPOINTS DE CRÉATION ============
    
    /**
     * Crée une nouvelle transaction d'achat.
     * 
     * Endpoint : POST /api/transactions
     * 
     * Exemple de requête JSON :
     * {
     *   "buyerId": 1,
     *   "disqueId": 5,
     *   "amount": 19.99,
     *   "description": "Achat du disque 'Album XYZ'"
     * }
     * 
     * @param requestBody Map contenant les paramètres : buyerId, disqueId, amount, description
     * @return ResponseEntity contenant :
     *         - 200 OK avec l'ID de la transaction créée si succès
     *         - 400 Bad Request avec un message d'erreur si données invalides
     */
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Map<String, Object> requestBody) {
        try {
            // Extraire les paramètres du body JSON
            Long buyerId = Long.valueOf(requestBody.get("buyerId").toString());
            Long disqueId = Long.valueOf(requestBody.get("disqueId").toString());
            double amountDouble = Double.parseDouble(requestBody.get("amount").toString());
            String description = (String) requestBody.getOrDefault("description", "");
            
            // Créer la transaction via le service
            Transaction transaction = transactionService.createTransaction(
                    buyerId, 
                    disqueId, 
                    java.math.BigDecimal.valueOf(amountDouble), 
                    description
            );
            
            // Retourner la réponse de succès
            return ResponseEntity.ok(Map.of(
                    "message", "Transaction créée avec succès",
                    "transactionId", transaction.getId(),
                    "status", transaction.getStatus()
            ));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Erreur lors de la création : " + e.getMessage()));
        }
    }
    
    // ============ ENDPOINTS DE LECTURE ============
    
    /**
     * Récupère toutes les transactions enregistrées.
     * 
     * Endpoint : GET /api/transactions
     * 
     * @return ResponseEntity contenant la liste de toutes les transactions
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Récupère une transaction spécifique par son ID.
     * 
     * Endpoint : GET /api/transactions/{id}
     * 
     * @param id l'ID de la transaction à récupérer
     * @return ResponseEntity contenant :
     *         - 200 OK avec la Transaction si trouvée
     *         - 404 Not Found si non trouvée
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Récupère l'historique d'achat d'un utilisateur (acheteur).
     * 
     * Endpoint : GET /api/transactions/buyer/{buyerId}
     * 
     * @param buyerId l'ID de l'acheteur
     * @return ResponseEntity contenant la liste des transactions de cet acheteur
     */
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<Transaction>> getTransactionsByBuyer(@PathVariable Long buyerId) {
        List<Transaction> transactions = transactionService.getTransactionsByBuyer(buyerId);
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Récupère les transactions associées à un disque (nombre de fois acheté).
     * 
     * Endpoint : GET /api/transactions/disque/{disqueId}
     * 
     * @param disqueId l'ID du disque
     * @return ResponseEntity contenant la liste des transactions sur ce disque
     */
    @GetMapping("/disque/{disqueId}")
    public ResponseEntity<List<Transaction>> getTransactionsByDisque(@PathVariable Long disqueId) {
        List<Transaction> transactions = transactionService.getTransactionsByDisque(disqueId);
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Récupère les transactions avec un statut spécifique.
     * 
     * Endpoint : GET /api/transactions/status/{status}
     * 
     * @param status le statut à rechercher (ex: COMPLETED, PENDING, CANCELLED)
     * @return ResponseEntity contenant la liste des transactions avec ce statut
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Transaction>> getTransactionsByStatus(@PathVariable String status) {
        List<Transaction> transactions = transactionService.getTransactionsByStatus(status);
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Récupère les transactions d'un acheteur avec un statut donné.
     * 
     * Endpoint : GET /api/transactions/buyer/{buyerId}/status/{status}
     * 
     * @param buyerId l'ID de l'acheteur
     * @param status le statut à rechercher
     * @return ResponseEntity contenant la liste des transactions correspondantes
     */
    @GetMapping("/buyer/{buyerId}/status/{status}")
    public ResponseEntity<List<Transaction>> getTransactionsByBuyerAndStatus(
            @PathVariable Long buyerId, 
            @PathVariable String status) {
        List<Transaction> transactions = transactionService.getTransactionsByBuyerAndStatus(buyerId, status);
        return ResponseEntity.ok(transactions);
    }
    
    // ============ ENDPOINTS DE MISE À JOUR ============
    
    /**
     * Marque une transaction comme complétée (statut = COMPLETED).
     * 
     * Endpoint : PATCH /api/transactions/{id}/complete
     * 
     * @param id l'ID de la transaction à compléter
     * @return ResponseEntity contenant :
     *         - 200 OK avec la Transaction mise à jour si trouvée
     *         - 404 Not Found si non trouvée
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completeTransaction(@PathVariable Long id) {
        return transactionService.completeTransaction(id)
                .map(transaction -> ResponseEntity.ok(Map.of(
                        "message", "Transaction complétée",
                        "transactionId", transaction.getId(),
                        "status", transaction.getStatus()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Marque une transaction comme annulée (statut = CANCELLED).
     * 
     * Endpoint : PATCH /api/transactions/{id}/cancel
     * 
     * @param id l'ID de la transaction à annuler
     * @return ResponseEntity contenant :
     *         - 200 OK avec la Transaction mise à jour si trouvée
     *         - 404 Not Found si non trouvée
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTransaction(@PathVariable Long id) {
        return transactionService.cancelTransaction(id)
                .map(transaction -> ResponseEntity.ok(Map.of(
                        "message", "Transaction annulée",
                        "transactionId", transaction.getId(),
                        "status", transaction.getStatus()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Met à jour le statut d'une transaction à une valeur arbitraire.
     * 
     * Endpoint : PATCH /api/transactions/{id}/status
     * 
     * Exemple de requête JSON :
     * {
     *   "status": "PENDING"
     * }
     * 
     * @param id l'ID de la transaction à mettre à jour
     * @param body Map contenant le nouveau statut
     * @return ResponseEntity contenant :
     *         - 200 OK avec la Transaction mise à jour si trouvée
     *         - 400 Bad Request si le body est invalide
     *         - 404 Not Found si non trouvée
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTransactionStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> body) {
        try {
            String newStatus = body.get("status");
            if (newStatus == null || newStatus.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le champ 'status' est requis"));
            }
            
            return transactionService.updateTransactionStatus(id, newStatus)
                    .map(transaction -> ResponseEntity.ok(Map.of(
                            "message", "Statut de la transaction mis à jour",
                            "transactionId", transaction.getId(),
                            "status", transaction.getStatus()
                    )))
                    .orElse(ResponseEntity.notFound().build());
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ============ ENDPOINTS DE SUPPRESSION ============
    
    /**
     * Supprime une transaction par son ID.
     * 
     * Endpoint : DELETE /api/transactions/{id}
     * 
     * Attention : cette opération est irréversible. 
     * En production, préférez annuler une transaction plutôt que la supprimer.
     * 
     * @param id l'ID de la transaction à supprimer
     * @return ResponseEntity contenant un message de confirmation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok(Map.of("message", "Transaction supprimée"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
