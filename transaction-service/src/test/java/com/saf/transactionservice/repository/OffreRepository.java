package com.saf.transactionservice.repository;

import com.saf.transactionservice.model.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {
    List<Offre> findByVendeurIdAndStatut(Long vendeurId, String statut);
    List<Offre> findByAcheteurId(Long acheteurId);
    List<Offre> findByAnnonceId(Long annonceId);
}
