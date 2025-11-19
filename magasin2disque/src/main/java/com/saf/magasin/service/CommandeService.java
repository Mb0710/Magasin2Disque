package com.saf.magasin.service;

import com.saf.magasin.model.Commande;
import com.saf.magasin.model.User;
import com.saf.magasin.repository.CommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommandeService {
    
    @Autowired
    private CommandeRepository commandeRepository;
    
    public Commande createCommande(Commande commande) {
        // Générer un numéro de commande unique
        commande.setNumeroCommande("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return commandeRepository.save(commande);
    }
    
    public List<Commande> getCommandesByUser(User user) {
        return commandeRepository.findByAcheteur(user);
    }
    
    public Optional<Commande> getCommandeById(Long id) {
        return commandeRepository.findById(id);
    }
    
    public Optional<Commande> getCommandeByNumero(String numeroCommande) {
        return commandeRepository.findByNumeroCommande(numeroCommande);
    }
    
    public Commande updateCommande(Commande commande) {
        return commandeRepository.save(commande);
    }
    
    public List<Commande> getAllCommandes() {
        return commandeRepository.findAll();
    }
}
