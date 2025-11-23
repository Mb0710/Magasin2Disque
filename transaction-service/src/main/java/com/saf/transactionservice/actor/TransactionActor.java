package com.saf.transactionservice.actor;

import com.saf.core.Actor;
import com.saf.core.ActorContext;
import com.saf.core.Message;
import com.saf.transactionservice.actor.messages.TransactionMessages.*;
import com.saf.transactionservice.client.UserServiceClient;
import com.saf.transactionservice.dto.AnnonceDTO;
import com.saf.transactionservice.model.Transaction;
import com.saf.transactionservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Actor gérant les achats directs (au prix affiché)
 * Utilise le framework Actor pour la résilience
 */
public class TransactionActor implements Actor {

    private static final Logger logger = LoggerFactory.getLogger(TransactionActor.class);

    private final TransactionRepository transactionRepository;
    private final UserServiceClient userServiceClient;
    private final com.saf.core.ActorRef notificationActor;

    public TransactionActor(TransactionRepository transactionRepository,
            UserServiceClient userServiceClient,
            com.saf.core.ActorRef notificationActor) {
        this.transactionRepository = transactionRepository;
        this.userServiceClient = userServiceClient;
        this.notificationActor = notificationActor;
    }

    @Override
    public void onReceive(Message message, ActorContext context) {
        Object payload = message.getPayload();

        try {
            if (payload instanceof AchatDirect msg) {
                handleAchatDirect(msg, message);
            } else if (payload instanceof GetTransaction msg) {
                handleGetTransaction(msg, message);
            } else if (payload instanceof GetTransactionsByUser msg) {
                handleGetTransactionsByUser(msg, message);
            }
        } catch (Exception e) {
            logger.error("Erreur dans TransactionActor: " + e.getMessage(), e);
            message.reply(new TransactionOperationError("Erreur: " + e.getMessage()));
        }
    }

    private void handleAchatDirect(AchatDirect msg, Message originalMessage) {
        try {
            AnnonceDTO annonce = userServiceClient.getAnnonce(msg.annonceId());

            if (annonce == null) {
                originalMessage.reply(new TransactionOperationError("Annonce introuvable"));
                return;
            }

            if (!annonce.isDisponible()) {
                originalMessage.reply(new TransactionOperationError("Annonce déjà vendue"));
                return;
            }

            Transaction transaction = new Transaction();
            transaction.setAnnonceId(annonce.getId());
            transaction.setAnnonceTitre(annonce.getTitre() + " - " + annonce.getArtiste());
            transaction.setAcheteurId(msg.acheteurId());
            transaction.setVendeurId(annonce.getVendeurId());
            transaction.setVendeurUsername(annonce.getVendeurUsername());
            transaction.setPrix(annonce.getPrix());
            transaction.setStatut("COMPLETED");
            transaction.setTypeAchat("DIRECT");
            transaction.setCompletedAt(LocalDateTime.now());

            Transaction saved = transactionRepository.save(transaction);

            try {
                userServiceClient.deleteAnnonce(msg.annonceId());
            } catch (Exception e) {
                logger.warn("Impossible de supprimer l'annonce: " + e.getMessage());
            }

            // Récupérer l'email réel du vendeur
            try {
                var vendeur = userServiceClient.getUser(annonce.getVendeurId());
                if (vendeur != null && vendeur.getEmail() != null) {
                    logger.info("Envoi notification au vendeur: " + vendeur.getEmail());
                    notificationActor.send(
                            new NotificationActor.NotifyVendeurAchatDirect(
                                    vendeur.getEmail(),
                                    annonce.getTitre() + " - " + annonce.getArtiste(),
                                    annonce.getPrix()),
                            originalMessage.getSender());
                } else {
                    logger.warn("Vendeur ou email vendeur null. Vendeur: " + vendeur);
                }
            } catch (Exception e) {
                logger.error("Erreur lors de l'envoi de la notification au vendeur", e);
            }

            originalMessage.reply(new TransactionCreated(saved));

        } catch (Exception e) {
            logger.error("Erreur achat direct: " + e.getMessage(), e);
            originalMessage.reply(new TransactionOperationError("Erreur lors de l'achat: " + e.getMessage()));
        }
    }

    private void handleGetTransaction(GetTransaction msg, Message originalMessage) {
        transactionRepository.findById(msg.transactionId())
                .ifPresentOrElse(
                        transaction -> originalMessage.reply(new TransactionResult(transaction)),
                        () -> originalMessage.reply(new TransactionOperationError("Transaction introuvable")));
    }

    private void handleGetTransactionsByUser(GetTransactionsByUser msg, Message originalMessage) {
        List<Transaction> transactions = msg.isVendeur()
                ? transactionRepository.findByVendeurId(msg.userId())
                : transactionRepository.findByAcheteurId(msg.userId());

        originalMessage.reply(new TransactionsList(transactions));
    }
}
