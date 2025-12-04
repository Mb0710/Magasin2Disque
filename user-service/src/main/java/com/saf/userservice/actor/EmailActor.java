package com.saf.userservice.actor;

import com.saf.core.Actor;
import com.saf.core.ActorContext;
import com.saf.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;

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
        try {
            String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + msg.token();

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(msg.to());
            helper.setSubject("🎵 Vérification de votre compte Magasin2Disque");
            
            String htmlContent = String.format(
                "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='margin:0;padding:0;font-family:Arial,sans-serif;'>" +
                "<div style='background:linear-gradient(135deg,#667eea 0%%,#764ba2 100%%);padding:40px 20px;'>" +
                "<div style='max-width:600px;margin:0 auto;background:white;border-radius:15px;overflow:hidden;box-shadow:0 10px 30px rgba(0,0,0,0.2);'>" +
                "<div style='background:linear-gradient(135deg,#667eea 0%%,#764ba2 100%%);padding:30px;text-align:center;'>" +
                "<h1 style='color:white;margin:0;font-size:32px;'>🎵 Magasin2Disque</h1>" +
                "</div>" +
                "<div style='padding:40px 30px;'>" +
                "<h2 style='color:#333;margin-top:0;'>Bonjour %s !</h2>" +
                "<p style='color:#666;font-size:16px;line-height:1.6;'>" +
                "Merci de vous être inscrit sur notre marketplace de disques vinyles. " +
                "Pour activer votre compte, veuillez cliquer sur le bouton ci-dessous :" +
                "</p>" +
                "<div style='text-align:center;margin:30px 0;'>" +
                "<a href='%s' style='display:inline-block;background:linear-gradient(135deg,#667eea 0%%,#764ba2 100%%);color:white;padding:15px 40px;text-decoration:none;border-radius:8px;font-weight:bold;font-size:16px;'>" +
                "Vérifier mon email" +
                "</a>" +
                "</div>" +
                "<p style='color:#999;font-size:14px;line-height:1.6;'>" +
                "Ce lien expirera dans 24 heures. Si vous n'avez pas créé de compte, ignorez cet email." +
                "</p>" +
                "</div>" +
                "<div style='background:#f5f5f5;padding:20px;text-align:center;color:#999;font-size:12px;'>" +
                "© 2025 Magasin2Disque - Votre marketplace de vinyles" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>",
                msg.username(), verificationUrl
            );
            
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            logger.info("Email de vérification envoyé à: " + msg.to());
        } catch (Exception e) {
            logger.error("Erreur envoi email vérification: " + e.getMessage());
            throw new RuntimeException("Erreur envoi email", e);
        }
    }

    private void sendNotificationInternal(SendNotification msg) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(msg.to());
            helper.setSubject(msg.subject());
            
            String htmlContent = String.format(
                "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='margin:0;padding:0;font-family:Arial,sans-serif;'>" +
                "<div style='background:linear-gradient(135deg,#667eea 0%%,#764ba2 100%%);padding:40px 20px;'>" +
                "<div style='max-width:600px;margin:0 auto;background:white;border-radius:15px;overflow:hidden;box-shadow:0 10px 30px rgba(0,0,0,0.2);'>" +
                "<div style='background:linear-gradient(135deg,#667eea 0%%,#764ba2 100%%);padding:30px;text-align:center;'>" +
                "<h1 style='color:white;margin:0;font-size:32px;'>🎵 Magasin2Disque</h1>" +
                "</div>" +
                "<div style='padding:40px 30px;'>" +
                "<div style='color:#333;font-size:16px;line-height:1.8;white-space:pre-line;'>%s</div>" +
                "</div>" +
                "<div style='background:#f5f5f5;padding:20px;text-align:center;color:#999;font-size:12px;'>" +
                "© 2025 Magasin2Disque - Votre marketplace de vinyles" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>",
                msg.body()
            );
            
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            logger.info("Notification envoyée à: " + msg.to());
        } catch (Exception e) {
            logger.error("Erreur envoi notification: " + e.getMessage());
            throw new RuntimeException("Erreur envoi notification", e);
        }
    }
}
