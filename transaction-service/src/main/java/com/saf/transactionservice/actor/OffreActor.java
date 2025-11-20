package com.saf.transactionservice.actor;

import com.saf.core.Actor;
import com.saf.core.ActorContext;
import com.saf.core.Message;
import com.saf.transactionservice.actor.messages.OffreMessages.*;
import com.saf.transactionservice.client.UserServiceClient;
import com.saf.transactionservice.dto.AnnonceDTO;
import com.saf.transactionservice.model.Offre;
import com.saf.transactionservice.model.Transaction;
import com.saf.transactionservice.repository.OffreRepository;
import com.saf.transactionservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Actor gérant le système d'offres
 * Gère la création, acceptation et refus d'offres
 */
public class OffreActor implements Actor {

    private static final Logger logger = LoggerFactory.getLogger(OffreActor.class);

    private final OffreRepository offreRepository;
    private final TransactionRepository transactionRepository;
    private final UserServiceClient userServiceClient;
    private final com.saf.core.ActorRef notificationActor;

    public OffreActor(OffreRepository offreRepository,
            TransactionRepository transactionRepository,
            UserServiceClient userServiceClient,
            com.saf.core.ActorRef notificationActor) {
        this.offreRepository = offreRepository;
        this.transactionRepository = transactionRepository;
        this.userServiceClient = userServiceClient;
        this.notificationActor = notificationActor;
    }

    @Override
    public void onReceive(Message message, ActorContext context) {
        Object payload = message.getPayload();

        try {
            if (payload instanceof FaireOffre msg) {
                handleFaireOffre(msg, message);
            } else if (payload instanceof AccepterOffre msg) {
                handleAccepterOffre(msg, message);
            } else if (payload instanceof RefuserOffre msg) {
                handleRefuserOffre(msg, message);
            } else if (payload instanceof GetOffresPendingForVendeur msg) {
                handleGetOffresPendingForVendeur(msg, message);
            } else if (payload instanceof GetOffresForAcheteur msg) {
                handleGetOffresForAcheteur(msg, message);
            }
        } catch (Exception e) {
            logger.error("Erreur dans OffreActor: " + e.getMessage(), e);
            message.reply(new OffreOperationError("Erreur: " + e.getMessage()));
        }
    }

    private void handleFaireOffre(FaireOffre msg, Message originalMessage) {
        try {
            AnnonceDTO annonce = userServiceClient.getAnnonce(msg.annonceId());

            if (annonce == null) {
                originalMessage.reply(new OffreOperationError("Annonce introuvable"));
                return;
            }

            if (!annonce.isDisponible()) {
                originalMessage.reply(new OffreOperationError("Annonce déjà vendue"));
                return;
            }

            Offre offre = new Offre();
            offre.setAnnonceId(annonce.getId());
            offre.setAnnonceTitre(annonce.getTitre() + " - " + annonce.getArtiste());
            offre.setAcheteurId(msg.acheteurId());
            offre.setVendeurId(annonce.getVendeurId());
            offre.setVendeurUsername(annonce.getVendeurUsername());
            offre.setPrixPropose(msg.prixPropose());
            offre.setPrixInitial(annonce.getPrix());
            offre.setMessage(msg.message());
            offre.setStatut("PENDING");

            Offre saved = offreRepository.save(offre);

            notificationActor.send(
                    new NotificationActor.NotifyVendeurNouvelleOffre(
                            annonce.getVendeurUsername(),
                            annonce.getTitre() + " - " + annonce.getArtiste(),
                            msg.prixPropose(),
                            annonce.getPrix()),
                    originalMessage.getSender());

            originalMessage.reply(new OffreCreated(saved));

        } catch (Exception e) {
            logger.error("Erreur création offre: " + e.getMessage(), e);
            originalMessage.reply(new OffreOperationError("Erreur lors de la création de l'offre"));
        }
    }

    private void handleAccepterOffre(AccepterOffre msg, Message originalMessage) {
        try {
            Optional<Offre> offreOpt = offreRepository.findById(msg.offreId());

            if (offreOpt.isEmpty()) {
                originalMessage.reply(new OffreOperationError("Offre introuvable"));
                return;
            }

            Offre offre = offreOpt.get();

            if (!offre.getStatut().equals("PENDING")) {
                originalMessage.reply(new OffreOperationError("Offre déjà traitée"));
                return;
            }

            AnnonceDTO annonce = userServiceClient.getAnnonce(offre.getAnnonceId());
            if (annonce == null || !annonce.isDisponible()) {
                originalMessage.reply(new OffreOperationError("Annonce non disponible"));
                return;
            }

            offre.setStatut("ACCEPTED");
            offre.setRespondedAt(LocalDateTime.now());
            offreRepository.save(offre);

            Transaction transaction = new Transaction();
            transaction.setAnnonceId(offre.getAnnonceId());
            transaction.setAnnonceTitre(offre.getAnnonceTitre());
            transaction.setAcheteurId(offre.getAcheteurId());
            transaction.setAcheteurUsername(offre.getAcheteurUsername());
            transaction.setVendeurId(offre.getVendeurId());
            transaction.setVendeurUsername(offre.getVendeurUsername());
            transaction.setPrix(offre.getPrixPropose());
            transaction.setStatut("COMPLETED");
            transaction.setTypeAchat("OFFRE_ACCEPTEE");
            transaction.setCompletedAt(LocalDateTime.now());

            transactionRepository.save(transaction);

            try {
                userServiceClient.deleteAnnonce(offre.getAnnonceId());
            } catch (Exception e) {
                logger.warn("Impossible de supprimer l'annonce: " + e.getMessage());
            }

            List<Offre> autresOffres = offreRepository.findByAnnonceIdAndStatut(offre.getAnnonceId(), "PENDING");
            for (Offre autre : autresOffres) {
                if (!autre.getId().equals(offre.getId())) {
                    autre.setStatut("REFUSED");
                    autre.setRespondedAt(LocalDateTime.now());
                    offreRepository.save(autre);
                }
            }

            notificationActor.send(
                    new NotificationActor.NotifyAcheteurOffreAcceptee(
                            offre.getAcheteurUsername(),
                            offre.getAnnonceTitre(),
                            offre.getPrixPropose()),
                    originalMessage.getSender());

            originalMessage.reply(new OffreAccepted(offre));

        } catch (Exception e) {
            logger.error("Erreur acceptation offre: " + e.getMessage(), e);
            originalMessage.reply(new OffreOperationError("Erreur lors de l'acceptation de l'offre"));
        }
    }

    private void handleRefuserOffre(RefuserOffre msg, Message originalMessage) {
        try {
            Optional<Offre> offreOpt = offreRepository.findById(msg.offreId());

            if (offreOpt.isEmpty()) {
                originalMessage.reply(new OffreOperationError("Offre introuvable"));
                return;
            }

            Offre offre = offreOpt.get();

            if (!offre.getStatut().equals("PENDING")) {
                originalMessage.reply(new OffreOperationError("Offre déjà traitée"));
                return;
            }

            offre.setStatut("REFUSED");
            offre.setRespondedAt(LocalDateTime.now());
            offreRepository.save(offre);

            notificationActor.send(
                    new NotificationActor.NotifyAcheteurOffreRefusee(
                            offre.getAcheteurUsername(),
                            offre.getAnnonceTitre()),
                    originalMessage.getSender());

            originalMessage.reply(new OffreRefused(offre));

        } catch (Exception e) {
            logger.error("Erreur refus offre: " + e.getMessage(), e);
            originalMessage.reply(new OffreOperationError("Erreur lors du refus de l'offre"));
        }
    }

    private void handleGetOffresPendingForVendeur(GetOffresPendingForVendeur msg, Message originalMessage) {
        List<Offre> offres = offreRepository.findByVendeurIdAndStatut(msg.vendeurId(), "PENDING");
        originalMessage.reply(new OffresList(offres));
    }

    private void handleGetOffresForAcheteur(GetOffresForAcheteur msg, Message originalMessage) {
        List<Offre> offres = offreRepository.findByAcheteurId(msg.acheteurId());
        originalMessage.reply(new OffresList(offres));
    }
}
