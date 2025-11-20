package com.saf.magasin.service;

import com.saf.magasin.model.User;
import com.saf.magasin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public User registerUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username déjà utilisé");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email déjà utilisé");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setEmailVerified(false);
        
        // Générer token de vérification
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenExpiryDate(LocalDateTime.now().plusHours(24));
        
        User savedUser = userRepository.save(user);
        
        // Envoyer email de confirmation
        try {
            emailService.sendVerificationEmail(email, username, token);
        } catch (Exception e) {
            System.err.println("Erreur envoi email : " + e.getMessage());
            // On continue même si l'email échoue
        }
        
        return savedUser;
    }
    
    public boolean verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);
        
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // Vérifier si le token n'est pas expiré
        if (user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        // Activer le compte
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setTokenExpiryDate(null);
        
        userRepository.save(user);
        return true;
    }
    
    public void resendVerificationEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur introuvable");
        }
        
        User user = userOpt.get();
        
        if (user.isEmailVerified()) {
            throw new RuntimeException("Email déjà vérifié");
        }
        
        // Générer nouveau token
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenExpiryDate(LocalDateTime.now().plusHours(24));
        
        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), token);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
