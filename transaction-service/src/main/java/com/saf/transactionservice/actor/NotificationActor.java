package com.saf.transactionservice.actor;

import com.saf.core.Actor;
import com.saf.core.ActorContext;
import com.saf.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;

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
                logger.info("Réception message NotifyVendeurAchatDirect pour: " + msg.vendeurEmail());
                sendVendeurAchatDirectEmail(msg);
            } else if (payload instanceof NotifyVendeurNouvelleOffre msg) {
                logger.info("Réception message NotifyVendeurNouvelleOffre pour: " + msg.vendeurEmail());
                sendVendeurNouvelleOffreEmail(msg);
            } else if (payload instanceof NotifyAcheteurOffreAcceptee msg) {
                logger.info("Réception message NotifyAcheteurOffreAcceptee pour: " + msg.acheteurEmail());
                sendAcheteurOffreAccepteeEmail(msg);
            } else if (payload instanceof NotifyAcheteurOffreRefusee msg) {
                logger.info("Réception message NotifyAcheteurOffreRefusee pour: " + msg.acheteurEmail());
                sendAcheteurOffreRefuseeEmail(msg);
            }
        } catch (Exception e) {
            logger.error("Erreur envoi notification: " + e.getMessage(), e);
        }
    }

    private void sendVendeurAchatDirectEmail(NotifyVendeurAchatDirect msg) {
        try {
            logger.info("Tentative d'envoi email achat direct à: " + msg.vendeurEmail());
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(msg.vendeurEmail());
            helper.setSubject("🎉 Votre disque a été vendu !");
            
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
                "<div style='padding:40px 30px;text-align:center;'>" +
                "<div style='font-size:60px;margin-bottom:20px;'>🎉</div>" +
                "<h2 style='color:#333;margin:0 0 20px 0;'>Félicitations !</h2>" +
                "<p style='color:#666;font-size:16px;line-height:1.6;'>" +
                "Votre annonce <strong style='color:#667eea;'>%s</strong> a été vendue !" +
                "</p>" +
                "<div style='background:#f0f4ff;padding:20px;border-radius:10px;margin:25px 0;'>" +
                "<div style='color:#999;font-size:14px;margin-bottom:5px;'>Prix de vente</div>" +
                "<div style='color:#667eea;font-size:36px;font-weight:bold;'>%.2f €</div>" +
                "</div>" +
                "<p style='color:#999;font-size:14px;'>" +
                "L'annonce a été automatiquement supprimée de la plateforme." +
                "</p>" +
                "</div>" +
                "<div style='background:#f5f5f5;padding:20px;text-align:center;color:#999;font-size:12px;'>" +
                "© 2025 Magasin2Disque - Votre marketplace de vinyles" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>",
                msg.annonceTitre(), msg.prix()
            );
            
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            logger.info("✅ Notification achat direct envoyée avec succès à: " + msg.vendeurEmail());
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'envoi de l'email achat direct à " + msg.vendeurEmail(), e);
            throw new RuntimeException("Erreur envoi email", e);
        }
    }

    private void sendVendeurNouvelleOffreEmail(NotifyVendeurNouvelleOffre msg) {
        try {
            logger.info("Tentative d'envoi email nouvelle offre à: " + msg.vendeurEmail());
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(msg.vendeurEmail());
            helper.setSubject("💰 Nouvelle offre sur votre annonce");
            
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
                "<div style='font-size:50px;text-align:center;margin-bottom:20px;'>💰</div>" +
                "<h2 style='color:#333;margin:0 0 20px 0;text-align:center;'>Nouvelle offre reçue !</h2>" +
                "<p style='color:#666;font-size:16px;line-height:1.6;text-align:center;'>" +
                "Vous avez reçu une nouvelle offre pour <strong style='color:#667eea;'>%s</strong>" +
                "</p>" +
                "<div style='background:#f0f4ff;padding:25px;border-radius:10px;margin:25px 0;'>" +
                "<div style='display:flex;justify-content:space-between;margin-bottom:15px;'>" +
                "<div style='flex:1;text-align:center;'>" +
                "<div style='color:#999;font-size:12px;margin-bottom:5px;'>PRIX INITIAL</div>" +
                "<div style='color:#333;font-size:24px;font-weight:bold;'>%.2f €</div>" +
                "</div>" +
                "<div style='color:#667eea;font-size:24px;align-self:center;'>→</div>" +
                "<div style='flex:1;text-align:center;'>" +
                "<div style='color:#999;font-size:12px;margin-bottom:5px;'>OFFRE PROPOSÉE</div>" +
                "<div style='color:#667eea;font-size:24px;font-weight:bold;'>%.2f €</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "<div style='text-align:center;margin-top:30px;'>" +
                "<a href='http://localhost:8080/mes-offres.html' style='display:inline-block;background:linear-gradient(135deg,#667eea 0%%,#764ba2 100%%);color:white;padding:15px 40px;text-decoration:none;border-radius:8px;font-weight:bold;font-size:16px;'>" +
                "Gérer mes offres" +
                "</a>" +
                "</div>" +
                "</div>" +
                "<div style='background:#f5f5f5;padding:20px;text-align:center;color:#999;font-size:12px;'>" +
                "© 2025 Magasin2Disque - Votre marketplace de vinyles" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>",
                msg.annonceTitre(), msg.prixInitial(), msg.prixPropose()
            );
            
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            logger.info("✅ Notification nouvelle offre envoyée avec succès à: " + msg.vendeurEmail());
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'envoi de l'email nouvelle offre à " + msg.vendeurEmail(), e);
            throw new RuntimeException("Erreur envoi email", e);
        }
    }

    private void sendAcheteurOffreAccepteeEmail(NotifyAcheteurOffreAcceptee msg) {
        try {
            logger.info("Tentative d'envoi email offre acceptée à: " + msg.acheteurEmail());
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(msg.acheteurEmail());
            helper.setSubject("✅ Votre offre a été acceptée !");
            
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
                "<div style='padding:40px 30px;text-align:center;'>" +
                "<div style='font-size:60px;margin-bottom:20px;'>✅</div>" +
                "<h2 style='color:#333;margin:0 0 20px 0;'>Bonne nouvelle !</h2>" +
                "<p style='color:#666;font-size:16px;line-height:1.6;'>" +
                "Votre offre pour <strong style='color:#667eea;'>%s</strong> a été acceptée par le vendeur." +
                "</p>" +
                "<div style='background:#f0f4ff;padding:20px;border-radius:10px;margin:25px 0;'>" +
                "<div style='color:#999;font-size:14px;margin-bottom:5px;'>Prix final</div>" +
                "<div style='color:#667eea;font-size:36px;font-weight:bold;'>%.2f €</div>" +
                "</div>" +
                "<p style='color:#27ae60;font-size:16px;font-weight:bold;margin:20px 0;'>" +
                "✓ Transaction complétée" +
                "</p>" +
                "<p style='color:#999;font-size:14px;'>" +
                "L'annonce a été supprimée de la plateforme." +
                "</p>" +
                "</div>" +
                "<div style='background:#f5f5f5;padding:20px;text-align:center;color:#999;font-size:12px;'>" +
                "© 2025 Magasin2Disque - Votre marketplace de vinyles" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>",
                msg.annonceTitre(), msg.prix()
            );
            
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            logger.info("✅ Notification offre acceptée envoyée avec succès à: " + msg.acheteurEmail());
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'envoi de l'email offre acceptée à " + msg.acheteurEmail(), e);
            throw new RuntimeException("Erreur envoi email", e);
        }
    }

    private void sendAcheteurOffreRefuseeEmail(NotifyAcheteurOffreRefusee msg) {
        try {
            logger.info("Tentative d'envoi email offre refusée à: " + msg.acheteurEmail());
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(msg.acheteurEmail());
            helper.setSubject("❌ Offre refusée");
            
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
                "<div style='padding:40px 30px;text-align:center;'>" +
                "<div style='font-size:60px;margin-bottom:20px;'>😔</div>" +
                "<h2 style='color:#333;margin:0 0 20px 0;'>Offre refusée</h2>" +
                "<p style='color:#666;font-size:16px;line-height:1.6;'>" +
                "Malheureusement, votre offre pour <strong style='color:#667eea;'>%s</strong> a été refusée." +
                "</p>" +
                "<p style='color:#999;font-size:14px;margin:25px 0;'>" +
                "Ne vous découragez pas ! Vous pouvez faire une nouvelle offre si l'annonce est toujours disponible." +
                "</p>" +
                "<div style='text-align:center;margin-top:30px;'>" +
                "<a href='http://localhost:8080/index.html' style='display:inline-block;background:linear-gradient(135deg,#667eea 0%%,#764ba2 100%%);color:white;padding:15px 40px;text-decoration:none;border-radius:8px;font-weight:bold;font-size:16px;'>" +
                "Voir les annonces" +
                "</a>" +
                "</div>" +
                "</div>" +
                "<div style='background:#f5f5f5;padding:20px;text-align:center;color:#999;font-size:12px;'>" +
                "© 2025 Magasin2Disque - Votre marketplace de vinyles" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>",
                msg.annonceTitre()
            );
            
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            logger.info("✅ Notification offre refusée envoyée avec succès à: " + msg.acheteurEmail());
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'envoi de l'email offre refusée à " + msg.acheteurEmail(), e);
            throw new RuntimeException("Erreur envoi email", e);
        }
    }
}
