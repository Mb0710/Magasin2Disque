package com.saf.userservice.controller;

import com.saf.core.ActorRef;
import com.saf.userservice.actor.messages.UserMessages.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private ActorRef userActor;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");

            Object response = userActor.ask(
                    new RegisterUser(username, email, password),
                    Duration.ofSeconds(5)).get(5, TimeUnit.SECONDS);

            if (response instanceof UserRegistered result) {
                return ResponseEntity.ok(Map.of(
                        "message", "Compte créé ! Veuillez vérifier votre email.",
                        "userId", result.userId(),
                        "username", result.username(),
                        "emailSent", result.emailSent()));
            } else if (response instanceof UserOperationError error) {
                return ResponseEntity.badRequest().body(Map.of("error", error.error()));
            }

            return ResponseEntity.status(500).body(Map.of("error", "Erreur inconnue"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            Object response = userActor.ask(new VerifyEmail(token), Duration.ofSeconds(5))
                    .get(5, TimeUnit.SECONDS);

            if (response instanceof EmailVerified result) {
                if (result.verified()) {
                    return ResponseEntity.ok(Map.of(
                            "message", "Email vérifié ! Vous pouvez vous connecter.",
                            "verified", true));
                } else {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Token invalide ou expiré",
                            "verified", false));
                }
            }

            return ResponseEntity.status(500).body(Map.of("error", "Erreur inconnue"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");

            Object response = userActor.ask(new ResendVerification(email), Duration.ofSeconds(5))
                    .get(5, TimeUnit.SECONDS);

            if (response instanceof UserOperationError error) {
                if (error.error().equals("Email renvoyé")) {
                    return ResponseEntity.ok(Map.of("message", "Email de vérification renvoyé"));
                }
                return ResponseEntity.badRequest().body(Map.of("error", error.error()));
            }

            return ResponseEntity.ok(Map.of("message", "Email renvoyé"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            Object response = userActor.ask(new Login(username, password), Duration.ofSeconds(5))
                    .get(5, TimeUnit.SECONDS);

            if (response instanceof LoginSuccess result) {
                return ResponseEntity.ok(Map.of(
                        "message", "Connexion réussie",
                        "token", result.token(),
                        "userId", result.userId(),
                        "username", result.username(),
                        "role", result.role(),
                        "emailVerified", true));
            } else if (response instanceof UserOperationError error) {
                int status = error.error().equals("Veuillez vérifier votre email") ? 403 : 401;
                return ResponseEntity.status(status).body(Map.of(
                        "error", error.error(),
                        "emailVerified", false));
            }

            return ResponseEntity.status(500).body(Map.of("error", "Erreur inconnue"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur"));
        }
    }
}
