package com.saf.transactionservice.repository;

import com.saf.transactionservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAcheteurId(Long acheteurId);

    List<Transaction> findByVendeurId(Long vendeurId);

    List<Transaction> findByAnnonceId(Long annonceId);
}
