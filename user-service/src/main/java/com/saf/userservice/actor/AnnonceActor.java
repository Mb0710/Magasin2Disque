package com.saf.userservice.actor;

import com.saf.core.Actor;
import com.saf.core.ActorContext;
import com.saf.core.Message;
import com.saf.userservice.actor.messages.AnnonceMessages.*;
import com.saf.userservice.model.Annonce;
import com.saf.userservice.model.User;
import com.saf.userservice.repository.AnnonceRepository;
import com.saf.userservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Actor gérant les opérations sur les annonces avec résilience
 */
public class AnnonceActor implements Actor {

    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;

    public AnnonceActor(AnnonceRepository annonceRepository, UserRepository userRepository) {
        this.annonceRepository = annonceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void onReceive(Message message, ActorContext context) {
        Object payload = message.getPayload();

        try {
            if (payload instanceof CreateAnnonce msg) {
                handleCreateAnnonce(msg, message);
            } else if (payload instanceof GetAnnonce msg) {
                handleGetAnnonce(msg, message);
            } else if (payload instanceof GetAllAnnoncesDisponibles msg) {
                handleGetAllAnnoncesDisponibles(msg, message);
            } else if (payload instanceof SearchAnnonces msg) {
                handleSearchAnnonces(msg, message);
            } else if (payload instanceof GetAnnoncesByGenre msg) {
                handleGetAnnoncesByGenre(msg, message);
            } else if (payload instanceof UpdateAnnonce msg) {
                handleUpdateAnnonce(msg, message);
            } else if (payload instanceof DeleteAnnonce msg) {
                handleDeleteAnnonce(msg, message);
            } else if (payload instanceof MarkAnnonceAsUnavailable msg) {
                handleMarkAsUnavailable(msg, message);
            }
        } catch (Exception e) {
            message.reply(new AnnonceOperationError("Erreur: " + e.getMessage()));
        }
    }

    private void handleCreateAnnonce(CreateAnnonce msg, Message originalMessage) {
        Annonce annonce = msg.annonce();

        if (annonce.getVendeurId() == null) {
            originalMessage.reply(new AnnonceOperationError("VendeurId requis"));
            return;
        }

        Optional<User> vendeurOpt = userRepository.findById(annonce.getVendeurId());
        if (vendeurOpt.isEmpty()) {
            originalMessage.reply(new AnnonceOperationError("Vendeur introuvable"));
            return;
        }

        User vendeur = vendeurOpt.get();
        annonce.setVendeurUsername(vendeur.getUsername());

        Annonce saved = annonceRepository.save(annonce);
        originalMessage.reply(new AnnonceCreated(saved.getId()));
    }

    private void handleGetAnnonce(GetAnnonce msg, Message originalMessage) {
        Optional<Annonce> annonceOpt = annonceRepository.findById(msg.annonceId());

        if (annonceOpt.isEmpty()) {
            originalMessage.reply(new AnnonceOperationError("Annonce introuvable"));
            return;
        }

        originalMessage.reply(new AnnonceResult(annonceOpt.get()));
    }

    private void handleGetAllAnnoncesDisponibles(GetAllAnnoncesDisponibles msg, Message originalMessage) {
        List<Annonce> annonces = annonceRepository.findByDisponibleTrue();
        originalMessage.reply(new AnnoncesList(annonces));
    }

    private void handleSearchAnnonces(SearchAnnonces msg, Message originalMessage) {
        List<Annonce> annonces = annonceRepository.findByTitreContainingIgnoreCase(msg.query());
        originalMessage.reply(new AnnoncesList(annonces));
    }

    private void handleGetAnnoncesByGenre(GetAnnoncesByGenre msg, Message originalMessage) {
        List<Annonce> annonces = annonceRepository.findByGenre(msg.genre());
        originalMessage.reply(new AnnoncesList(annonces));
    }

    private void handleUpdateAnnonce(UpdateAnnonce msg, Message originalMessage) {
        Optional<Annonce> existingOpt = annonceRepository.findById(msg.annonceId());

        if (existingOpt.isEmpty()) {
            originalMessage.reply(new AnnonceOperationError("Annonce introuvable"));
            return;
        }

        Annonce existing = existingOpt.get();
        Annonce update = msg.annonce();

        if (update.getTitre() != null)
            existing.setTitre(update.getTitre());
        if (update.getArtiste() != null)
            existing.setArtiste(update.getArtiste());
        if (update.getGenre() != null)
            existing.setGenre(update.getGenre());
        if (update.getAnneeSortie() != null)
            existing.setAnneeSortie(update.getAnneeSortie());
        if (update.getPrix() != null)
            existing.setPrix(update.getPrix());
        if (update.getDescription() != null)
            existing.setDescription(update.getDescription());
        if (update.getImageUrl() != null)
            existing.setImageUrl(update.getImageUrl());
        if (update.getEtat() != null)
            existing.setEtat(update.getEtat());
        existing.setDisponible(update.isDisponible());

        annonceRepository.save(existing);
        originalMessage.reply(new AnnonceOperationSuccess("Annonce mise à jour"));
    }

    private void handleDeleteAnnonce(DeleteAnnonce msg, Message originalMessage) {
        annonceRepository.deleteById(msg.annonceId());
        originalMessage.reply(new AnnonceOperationSuccess("Annonce supprimée"));
    }

    private void handleMarkAsUnavailable(MarkAnnonceAsUnavailable msg, Message originalMessage) {
        Optional<Annonce> annonceOpt = annonceRepository.findById(msg.annonceId());

        if (annonceOpt.isEmpty()) {
            originalMessage.reply(new AnnonceOperationError("Annonce introuvable"));
            return;
        }

        Annonce annonce = annonceOpt.get();
        annonce.setDisponible(false);
        annonceRepository.save(annonce);

        originalMessage.reply(new AnnonceOperationSuccess("Annonce marquée comme indisponible"));
    }
}
