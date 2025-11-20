package com.saf.userservice.actor.messages;

import com.saf.userservice.model.Annonce;
import java.util.List;

/**
 * Messages pour les opérations sur les annonces
 */
public class AnnonceMessages {

    // Commandes
    public record CreateAnnonce(Annonce annonce) {
    }

    public record GetAnnonce(Long annonceId) {
    }

    public record GetAllAnnoncesDisponibles() {
    }

    public record SearchAnnonces(String query) {
    }

    public record GetAnnoncesByGenre(String genre) {
    }

    public record UpdateAnnonce(Long annonceId, Annonce annonce) {
    }

    public record DeleteAnnonce(Long annonceId) {
    }

    public record MarkAnnonceAsUnavailable(Long annonceId) {
    }

    // Réponses
    public record AnnonceCreated(Long annonceId) {
    }

    public record AnnonceResult(Annonce annonce) {
    }

    public record AnnoncesList(List<Annonce> annonces) {
    }

    public record AnnonceOperationSuccess(String message) {
    }

    public record AnnonceOperationError(String error) {
    }
}
