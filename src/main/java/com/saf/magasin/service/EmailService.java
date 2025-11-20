package com.saf.magasin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    public void sendVerificationEmail(String toEmail, String username, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Confirmation de votre compte - Magasin2Disque");
            
            String verificationUrl = baseUrl + "/api/auth/verify?token=" + token;
            
            String emailBody = String.format(
                "Bonjour %s,\n\n" +
                "Merci de vous être inscrit sur Magasin2Disque !\n\n" +
                "Pour activer votre compte, veuillez cliquer sur le lien suivant :\n" +
                "%s\n\n" +
                "Ce lien est valable pendant 24 heures.\n\n" +
                "Si vous n'êtes pas à l'origine de cette inscription, ignorez cet email.\n\n" +
                "Cordialement,\n" +
                "L'équipe Magasin2Disque",
                username, verificationUrl
            );
            
            message.setText(emailBody);
            
            mailSender.send(message);
            System.out.println("Email de vérification envoyé à : " + toEmail);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email de confirmation");
        }
    }
    
    public void sendPasswordResetEmail(String toEmail, String username, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Réinitialisation de votre mot de passe - Magasin2Disque");
            
            String resetUrl = baseUrl + "/api/auth/reset-password?token=" + token;
            
            String emailBody = String.format(
                "Bonjour %s,\n\n" +
                "Vous avez demandé la réinitialisation de votre mot de passe.\n\n" +
                "Pour réinitialiser votre mot de passe, cliquez sur le lien suivant :\n" +
                "%s\n\n" +
                "Ce lien est valable pendant 1 heure.\n\n" +
                "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.\n\n" +
                "Cordialement,\n" +
                "L'équipe Magasin2Disque",
                username, resetUrl
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email de reset : " + e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email de réinitialisation");
        }
    }
}
