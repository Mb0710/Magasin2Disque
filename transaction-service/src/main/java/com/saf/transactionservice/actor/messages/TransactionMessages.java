package com.saf.transactionservice.actor.messages;

import com.saf.transactionservice.model.Transaction;
import java.util.List;

/**
 * Messages pour les opérations de transaction (achat direct)
 */
public class TransactionMessages {

    // Commandes
    public record AchatDirect(Long annonceId, Long acheteurId) {
    }

    public record GetTransaction(Long transactionId) {
    }

    public record GetTransactionsByUser(Long userId, boolean isVendeur) {
    }

    public record GetAllTransactions() {
    }

    // Réponses
    public record TransactionCreated(Transaction transaction) {
    }

    public record TransactionResult(Transaction transaction) {
    }

    public record TransactionsList(List<Transaction> transactions) {
    }

    public record TransactionOperationError(String error) {
    }
}
