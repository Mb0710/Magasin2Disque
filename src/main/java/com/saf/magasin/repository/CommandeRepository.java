package com.saf.magasin.repository;

import com.saf.magasin.model.Commande;
import com.saf.magasin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findByAcheteur(User acheteur);
    Optional<Commande> findByNumeroCommande(String numeroCommande);
    List<Commande> findByStatut(String statut);
}
