package com.saf.transactionservice.actor;

import com.saf.core.Actor;
import com.saf.core.ActorContext;
import com.saf.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;

/**
 * Actor responsable des notifications par email
 * Isolé pour gérer les erreurs d'envoi sans impacter les autres acteurs
 */
public class NotificationActor implements Actor {

    private static final Logger logger = LoggerFactory.getLogger(NotificationActor.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public NotificationActor(JavaMailSender mailSender, String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    public record NotifyVendeurAchatDirect(String vendeurEmail, String annonceTitre, BigDecimal prix) {
    }

    public record NotifyVendeurNouvelleOffre(String vendeurEmail, String annonceTitre, BigDecimal prixPropose,
            BigDecimal prixInitial) {
    }

    public record NotifyAcheteurOffreAcceptee(String acheteurEmail, String annonceTitre, BigDecimal prix) {
    }

    public record NotifyAcheteurOffreRefusee(String acheteurEmail, String annonceTitre) {
    }

    @Override
    public void onReceive(Message message, ActorContext context) {
        Object payload = message.getPayload();

        try {
            if (payload instanceof NotifyVendeurAchatDirect msg) {
                sendVendeurAchatDirectEmail(msg);
            } else if (payload instanceof NotifyVendeurNouvelleOffre msg) {
                sendVendeurNouvelleOffreEmail(msg);
            } else if (payload instanceof NotifyAcheteurOffreAcceptee msg) {
                sendAcheteurOffreAccepteeEmail(msg);
            } else if (payload instanceof NotifyAcheteurOffreRefusee msg) {
                sendAcheteurOffreRefuseeEmail(msg);
            }
        } catch (Exception e) {
            logger.error("Erreur envoi notification: " + e.getMessage());
        }
    }

    private void sendVendeurAchatDirectEmail(NotifyVendeurAchatDirect msg) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(msg.vendeurEmail());
        message.setSubject("🎉 Votre disque a été vendu !");
        message.setText(String.format(
                "Félicitations !\n\n" +
                        "Votre annonce '%s' a été achetée au prix de %.2f€.\n\n" +
                        "L'annonce a été automatiquement supprimée.\n\n" +
                        "Cordialement,\nL'équipe Marketplace Disques",
                msg.annonceTitre(), msg.prix()));

        mailSender.send(message);
        logger.info("Notification achat direct envoyée à: " + msg.vendeurEmail());
    }

    private void sendVendeurNouvelleOffreEmail(NotifyVendeurNouvelleOffre msg) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(msg.vendeurEmail());
        message.setSubject("💰 Nouvelle offre sur votre annonce");
        message.setText(String.format(
                "Bonjour,\n\n" +
                        "Vous avez reçu une nouvelle offre sur votre annonce '%s'.\n\n" +
                        "Prix initial: %.2f€\n" +
                        "Offre proposée: %.2f€\n\n" +
                        "Connectez-vous pour accepter ou refuser cette offre.\n\n" +
                        "Cordialement,\nL'équipe Marketplace Disques",
                msg.annonceTitre(), msg.prixInitial(), msg.prixPropose()));

        mailSender.send(message);
        logger.info("Notification nouvelle offre envoyée à: " + msg.vendeurEmail());
    }

    private void sendAcheteurOffreAccepteeEmail(NotifyAcheteurOffreAcceptee msg) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(msg.acheteurEmail());
        message.setSubject("✅ Votre offre a été acceptée !");
        message.setText(String.format(
                "Bonne nouvelle !\n\n" +
                        "Votre offre de %.2f€ pour '%s' a été acceptée par le vendeur.\n\n" +
                        "L'annonce a été supprimée et la transaction est complétée.\n\n" +
                        "Cordialement,\nL'équipe Marketplace Disques",
                msg.prix(), msg.annonceTitre()));

        mailSender.send(message);
        logger.info("Notification offre acceptée envoyée à: " + msg.acheteurEmail());
    }

    private void sendAcheteurOffreRefuseeEmail(NotifyAcheteurOffreRefusee msg) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(msg.acheteurEmail());
        message.setSubject("❌ Votre offre a été refusée");
        message.setText(String.format(
                "Bonjour,\n\n" +
                        "Malheureusement, votre offre pour '%s' a été refusée par le vendeur.\n\n" +
                        "Vous pouvez faire une nouvelle offre si l'annonce est toujours disponible.\n\n" +
                        "Cordialement,\nL'équipe Marketplace Disques",
                msg.annonceTitre()));

        mailSender.send(message);
        logger.info("Notification offre refusée envoyée à: " + msg.acheteurEmail());
    }
}
