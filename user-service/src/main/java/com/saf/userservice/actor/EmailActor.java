package com.saf.userservice.actor;

import com.saf.core.Actor;
import com.saf.core.ActorContext;
import com.saf.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Actor responsable de l'envoi d'emails
 * Isolé pour gérer les erreurs d'envoi sans impacter les autres acteurs
 */
public class EmailActor implements Actor {

    private static final Logger logger = LoggerFactory.getLogger(EmailActor.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailActor(JavaMailSender mailSender, String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    public record SendVerificationEmail(String to, String username, String token) {
    }

    public record SendNotification(String to, String subject, String body) {
    }

    @Override
    public void onReceive(Message message, ActorContext context) {
        Object payload = message.getPayload();

        try {
            if (payload instanceof SendVerificationEmail msg) {
                sendVerificationEmailInternal(msg);
            } else if (payload instanceof SendNotification msg) {
                sendNotificationInternal(msg);
            }
        } catch (Exception e) {
            logger.error("Erreur envoi email: " + e.getMessage());
            throw new RuntimeException("Erreur envoi email", e);
        }
    }

    private void sendVerificationEmailInternal(SendVerificationEmail msg) {
        String verificationUrl = "http://localhost:8081/api/auth/verify?token=" + msg.token();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(msg.to());
        message.setSubject("Vérification de votre compte");
        message.setText("Bonjour " + msg.username() + ",\n\n" +
                "Merci de vous être inscrit ! Veuillez cliquer sur le lien ci-dessous pour vérifier votre email :\n\n" +
                verificationUrl + "\n\n" +
                "Ce lien expirera dans 24 heures.\n\n" +
                "Cordialement,\nL'équipe Marketplace Disques");

        mailSender.send(message);
        logger.info("Email de vérification envoyé à: " + msg.to());
    }

    private void sendNotificationInternal(SendNotification msg) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(msg.to());
        message.setSubject(msg.subject());
        message.setText(msg.body());

        mailSender.send(message);
        logger.info("Notification envoyée à: " + msg.to());
    }
}
