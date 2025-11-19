package com.saf.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saf.userservice.model.User;

/**
 * Repository pour gérer l'accès aux données des utilisateurs en base de données.
 * Fournit des méthodes prédéfinies et personnalisées pour les opérations CRUD
 * sur les entités User.
 * 
 * @author SAF - Team
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Récupère un utilisateur par son nom d'utilisateur.
     * 
     * @param username le nom d'utilisateur
     * @return Optional contenant l'utilisateur ou vide si non trouvé
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Récupère un utilisateur par son email.
     * 
     * @param email l'email de l'utilisateur
     * @return Optional contenant l'utilisateur ou vide si non trouvé
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Vérifie si un utilisateur existe par son nom d'utilisateur.
     * 
     * @param username le nom d'utilisateur
     * @return true si l'utilisateur existe, false sinon
     */
    boolean existsByUsername(String username);
    
    /**
     * Vérifie si un utilisateur existe par son email.
     * 
     * @param email l'email de l'utilisateur
     * @return true si l'utilisateur existe, false sinon
     */
    boolean existsByEmail(String email);
}
