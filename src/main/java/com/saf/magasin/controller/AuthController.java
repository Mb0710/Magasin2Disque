package com.saf.magasin.controller;

import com.saf.magasin.model.User;
import com.saf.magasin.security.JwtUtil;
import com.saf.magasin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");
            
            User user = userService.registerUser(username, email, password);
            return ResponseEntity.ok(Map.of(
                "message", "Compte créé ! Veuillez vérifier votre email pour activer votre compte.",
                "userId", user.getId(),
                "username", user.getUsername(),
                "emailSent", true
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            boolean verified = userService.verifyEmail(token);
            
            if (verified) {
                return ResponseEntity.ok(Map.of(
                    "message", "Email vérifié avec succès ! Vous pouvez maintenant vous connecter.",
                    "verified", true
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Token invalide ou expiré",
                    "verified", false
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur lors de la vérification"));
        }
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            userService.resendVerificationEmail(email);
            
            return ResponseEntity.ok(Map.of(
                "message", "Email de vérification renvoyé avec succès"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        Optional<User> userOpt = userService.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Utilisateur introuvable"));
        }
        
        User user = userOpt.get();
        
        if (!userService.checkPassword(user, password)) {
            return ResponseEntity.status(401).body(Map.of("error", "Mot de passe incorrect"));
        }
        
        // Vérifier si l'email est vérifié
        if (!user.isEmailVerified()) {
            return ResponseEntity.status(403).body(Map.of(
                "error", "Veuillez vérifier votre email avant de vous connecter",
                "emailVerified", false
            ));
        }
        
        // Générer le token JWT
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
        
        return ResponseEntity.ok(Map.of(
            "message", "Connexion réussie",
            "token", token,
            "userId", user.getId(),
            "username", user.getUsername(),
            "role", user.getRole(),
            "emailVerified", true
        ));
    }
}
