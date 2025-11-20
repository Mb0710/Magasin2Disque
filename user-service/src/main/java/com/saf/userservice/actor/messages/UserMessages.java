package com.saf.userservice.actor.messages;

/**
 * Messages pour les opérations sur les utilisateurs
 */
public class UserMessages {

    // Inscription
    public record RegisterUser(String username, String email, String password) {
    }

    public record UserRegistered(Long userId, String username, boolean emailSent) {
    }

    public record UserOperationError(String error) {
    }

    // Connexion
    public record Login(String username, String password) {
    }

    public record LoginSuccess(String token, Long userId, String username, String role) {
    }

    // Vérification email
    public record VerifyEmail(String token) {
    }

    public record EmailVerified(boolean verified) {
    }

    // Resend verification
    public record ResendVerification(String email) {
    }

    // Récupération utilisateur
    public record GetUserById(Long userId) {
    }

    public record GetUserByIdResponse(com.saf.userservice.model.User user) {
    }

    // Email (messages internes)
    public record SendVerificationEmail(String to, String verificationLink) {
    }

    public record SendNotificationEmail(String to, String subject, String body) {
    }
}
