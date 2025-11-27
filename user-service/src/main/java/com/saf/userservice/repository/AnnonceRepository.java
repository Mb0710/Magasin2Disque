package com.saf.userservice.repository;

import com.saf.userservice.model.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long> {
    List<Annonce> findByDisponibleTrue();

    List<Annonce> findByVendeurId(Long vendeurId);

    List<Annonce> findByTitreContainingIgnoreCase(String titre);

    List<Annonce> findByGenre(String genre);

    long countByDisponibleTrue();

    int countByVendeurId(Long vendeurId);
}
