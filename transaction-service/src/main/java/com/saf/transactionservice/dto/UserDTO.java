package com.saf.transactionservice.dto;

/**
 * DTO représentant les informations d'un utilisateur depuis user-service
 */
public class UserDTO {
    private Long id;
    private String username;
    private String email;

    // Constructeurs
    public UserDTO() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
