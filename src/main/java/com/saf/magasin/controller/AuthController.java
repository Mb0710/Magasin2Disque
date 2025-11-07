package com.saf.magasin.controller;

import com.saf.magasin.model.User;
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
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");
            
            User user = userService.registerUser(username, email, password);
            return ResponseEntity.ok(Map.of(
                "message", "Utilisateur créé avec succès",
                "userId", user.getId(),
                "username", user.getUsername()
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
        
        return ResponseEntity.ok(Map.of(
            "message", "Connexion réussie",
            "userId", user.getId(),
            "username", user.getUsername(),
            "role", user.getRole()
        ));
    }
}
