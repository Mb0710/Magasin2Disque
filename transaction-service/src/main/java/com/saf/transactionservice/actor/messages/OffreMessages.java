package com.saf.transactionservice.actor.messages;

import com.saf.transactionservice.model.Offre;
import java.math.BigDecimal;
import java.util.List;

/**
 * Messages pour les opérations d'offres
 */
public class OffreMessages {

    // Commandes
    public record FaireOffre(Long annonceId, Long acheteurId, BigDecimal prixPropose, String message) {
    }

    public record AccepterOffre(Long offreId) {
    }

    public record RefuserOffre(Long offreId) {
    }

    public record GetOffresPendingForVendeur(Long vendeurId) {
    }

    public record GetOffresForAcheteur(Long acheteurId) {
    }

    // Réponses
    public record OffreCreated(Offre offre) {
    }

    public record OffreAccepted(Offre offre) {
    }

    public record OffreRefused(Offre offre) {
    }

    public record OffresList(List<Offre> offres) {
    }

    public record OffreOperationError(String error) {
    }
}
