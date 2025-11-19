package com.saf.magasin.repository;

import com.saf.magasin.model.Disque;
import com.saf.magasin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisqueRepository extends JpaRepository<Disque, Long> {
    List<Disque> findByVendeur(User vendeur);
    List<Disque> findByGenre(String genre);
    List<Disque> findByArtiste(String artiste);
    List<Disque> findByDisponibleTrue();
    List<Disque> findByTitreContainingIgnoreCase(String titre);
}
