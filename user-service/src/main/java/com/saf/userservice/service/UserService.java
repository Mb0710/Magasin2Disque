package com.saf.userservice.service;

import com.saf.userservice.model.User;
import com.saf.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des utilisateurs.
 * Contient la logique métier pour les opérations CRUD et authentification.
 * 
 * @author SAF - Team
 * @version 1.0
 */
@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Enregistre un nouvel utilisateur.
     * Valide que le username et email ne sont pas déjà utilisés.
     * 
     * @param user l'utilisateur à enregistrer
     * @return l'utilisateur enregistré
     * @throws IllegalArgumentException si username ou email sont déjà utilisés
     */
    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        // TODO : hasher le password avec BCrypt ou similaire avant de sauvegarder
        return userRepository.save(user);
    }
    
    /**
     * Connecte un utilisateur (simple vérification de credentials).
     * 
     * @param username le nom d'utilisateur
     * @param password le mot de passe fourni
     * @return Optional contenant l'utilisateur si les credentials sont valides
     */
    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // TODO : utiliser BCrypt.checkpw(password, user.getPassword())
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Récupère un utilisateur par son ID.
     * 
     * @param id l'identifiant de l'utilisateur
     * @return Optional contenant l'utilisateur ou vide si non trouvé
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Récupère un utilisateur par son username.
     * 
     * @param username le nom d'utilisateur
     * @return Optional contenant l'utilisateur ou vide si non trouvé
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Récupère un utilisateur par son email.
     * 
     * @param email l'email de l'utilisateur
     * @return Optional contenant l'utilisateur ou vide si non trouvé
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Liste tous les utilisateurs.
     * 
     * @return liste de tous les utilisateurs
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    /**
     * Met à jour un utilisateur existant.
     * 
     * @param id l'ID de l'utilisateur
     * @param updatedUser les données mises à jour
     * @return l'utilisateur mis à jour
     * @throws IllegalArgumentException si l'utilisateur n'existe pas
     */
    public User update(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(updatedUser.getUsername())) {
                throw new IllegalArgumentException("Username already exists: " + updatedUser.getUsername());
            }
            user.setUsername(updatedUser.getUsername());
        }
        
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + updatedUser.getEmail());
            }
            user.setEmail(updatedUser.getEmail());
        }
        
        if (updatedUser.getRole() != null) {
            user.setRole(updatedUser.getRole());
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Supprime un utilisateur.
     * 
     * @param id l'ID de l'utilisateur
     */
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
