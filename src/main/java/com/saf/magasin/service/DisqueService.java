package com.saf.magasin.service;

import com.saf.magasin.model.Disque;
import com.saf.magasin.model.User;
import com.saf.magasin.repository.DisqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DisqueService {
    
    @Autowired
    private DisqueRepository disqueRepository;
    
    public Disque createDisque(Disque disque) {
        return disqueRepository.save(disque);
    }
    
    public List<Disque> getAllDisques() {
        return disqueRepository.findAll();
    }
    
    public List<Disque> getDisquesDisponibles() {
        return disqueRepository.findByDisponibleTrue();
    }
    
    public Optional<Disque> getDisqueById(Long id) {
        return disqueRepository.findById(id);
    }
    
    public List<Disque> getDisquesByVendeur(User vendeur) {
        return disqueRepository.findByVendeur(vendeur);
    }
    
    public List<Disque> searchByTitre(String titre) {
        return disqueRepository.findByTitreContainingIgnoreCase(titre);
    }
    
    public List<Disque> getDisquesByGenre(String genre) {
        return disqueRepository.findByGenre(genre);
    }
    
    public Disque updateDisque(Disque disque) {
        return disqueRepository.save(disque);
    }
    
    public void deleteDisque(Long id) {
        disqueRepository.deleteById(id);
    }
}
