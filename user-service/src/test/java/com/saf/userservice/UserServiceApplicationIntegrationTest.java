package com.saf.userservice;

import com.saf.userservice.model.User;
import com.saf.userservice.model.Annonce;
import com.saf.userservice.repository.UserRepository;
import com.saf.userservice.repository.AnnonceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceApplicationIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        annonceRepository.deleteAll();
    }

    @Test
    void testCompleteUserLifecycle() {
        // 1. Créer un utilisateur
        User user = new User("testuser", "test@example.com", passwordEncoder.encode("password123"));
        user.setEmailVerified(false);
        user = userRepository.save(user);
        
        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertFalse(user.isEmailVerified());
        assertEquals("USER", user.getRole());

        // 2. Vérifier l'email
        user.setEmailVerified(true);
        userRepository.save(user);
        
        User verifiedUser = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(verifiedUser.isEmailVerified());

        // 3. Créer une annonce pour cet utilisateur
        Annonce annonce = new Annonce();
        annonce.setTitre("Abbey Road");
        annonce.setArtiste("The Beatles");
        annonce.setGenre("Rock");
        annonce.setPrix(new BigDecimal("39.99"));
        annonce.setVendeurId(user.getId());
        annonce.setVendeurUsername(user.getUsername());
        annonce = annonceRepository.save(annonce);

        assertNotNull(annonce.getId());
        assertTrue(annonce.isDisponible());

        // 4. Vérifier que l'annonce est associée à l'utilisateur
        List<Annonce> userAnnonces = annonceRepository.findByVendeurId(user.getId());
        assertEquals(1, userAnnonces.size());
        assertEquals("Abbey Road", userAnnonces.get(0).getTitre());

        // 5. Bannir l'utilisateur
        user.setBanned(true);
        user.setBannedReason("Test ban");
        user.setEnabled(false);
        userRepository.save(user);

        User bannedUser = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(bannedUser.isBanned());
        assertFalse(bannedUser.isEnabled());

        // 6. Débannir l'utilisateur
        user.setBanned(false);
        user.setBannedReason(null);
        user.setEnabled(true);
        userRepository.save(user);

        User unbannedUser = userRepository.findById(user.getId()).orElseThrow();
        assertFalse(unbannedUser.isBanned());
        assertTrue(unbannedUser.isEnabled());
    }

    @Test
    void testMultipleUsersAndAnnonces() {
        // Créer plusieurs utilisateurs
        User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("pass1"));
        user1.setEmailVerified(true);
        user1 = userRepository.save(user1);

        User user2 = new User("user2", "user2@example.com", passwordEncoder.encode("pass2"));
        user2.setEmailVerified(true);
        user2 = userRepository.save(user2);

        // Créer plusieurs annonces
        Annonce annonce1 = new Annonce();
        annonce1.setTitre("Album 1");
        annonce1.setArtiste("Artist 1");
        annonce1.setPrix(new BigDecimal("29.99"));
        annonce1.setVendeurId(user1.getId());
        annonce1.setVendeurUsername(user1.getUsername());
        annonceRepository.save(annonce1);

        Annonce annonce2 = new Annonce();
        annonce2.setTitre("Album 2");
        annonce2.setArtiste("Artist 2");
        annonce2.setPrix(new BigDecimal("39.99"));
        annonce2.setVendeurId(user2.getId());
        annonce2.setVendeurUsername(user2.getUsername());
        annonceRepository.save(annonce2);

        Annonce annonce3 = new Annonce();
        annonce3.setTitre("Album 3");
        annonce3.setArtiste("Artist 1");
        annonce3.setPrix(new BigDecimal("19.99"));
        annonce3.setVendeurId(user1.getId());
        annonce3.setVendeurUsername(user1.getUsername());
        annonceRepository.save(annonce3);

        // Vérifications
        assertEquals(2, userRepository.count());
        assertEquals(3, annonceRepository.count());

        List<Annonce> user1Annonces = annonceRepository.findByVendeurId(user1.getId());
        assertEquals(2, user1Annonces.size());

        List<Annonce> user2Annonces = annonceRepository.findByVendeurId(user2.getId());
        assertEquals(1, user2Annonces.size());
    }

    @Test
    void testUserSearch() {
        // Créer des utilisateurs
        User user1 = new User("johnsmith", "john@example.com", "pass");
        userRepository.save(user1);

        User user2 = new User("janesmith", "jane@example.com", "pass");
        userRepository.save(user2);

        User user3 = new User("bobdoe", "bob@example.com", "pass");
        userRepository.save(user3);

        // Recherche par nom
        List<User> smithUsers = userRepository.findByUsernameContainingOrEmailContaining("smith", "smith");
        assertEquals(2, smithUsers.size());

        // Recherche par email
        List<User> johnUsers = userRepository.findByUsernameContainingOrEmailContaining("john", "john");
        assertEquals(1, johnUsers.size());
    }

    @Test
    void testAnnonceSearch() {
        User vendor = new User("vendor", "vendor@example.com", "pass");
        vendor = userRepository.save(vendor);

        Annonce annonce1 = new Annonce();
        annonce1.setTitre("Dark Side of the Moon");
        annonce1.setArtiste("Pink Floyd");
        annonce1.setGenre("Rock");
        annonce1.setPrix(new BigDecimal("45.99"));
        annonce1.setVendeurId(vendor.getId());
        annonceRepository.save(annonce1);

        Annonce annonce2 = new Annonce();
        annonce2.setTitre("The Wall");
        annonce2.setArtiste("Pink Floyd");
        annonce2.setGenre("Rock");
        annonce2.setPrix(new BigDecimal("49.99"));
        annonce2.setVendeurId(vendor.getId());
        annonceRepository.save(annonce2);

        Annonce annonce3 = new Annonce();
        annonce3.setTitre("Thriller");
        annonce3.setArtiste("Michael Jackson");
        annonce3.setGenre("Pop");
        annonce3.setPrix(new BigDecimal("35.99"));
        annonce3.setVendeurId(vendor.getId());
        annonceRepository.save(annonce3);

        // Recherche par genre
        List<Annonce> rockAlbums = annonceRepository.findByGenre("Rock");
        assertEquals(2, rockAlbums.size());

        // Toutes les annonces disponibles
        List<Annonce> availableAnnonces = annonceRepository.findByDisponibleTrue();
        assertEquals(3, availableAnnonces.size());
    }

    @Test
    void testBannedUsersCount() {
        User user1 = new User("user1", "user1@example.com", "pass");
        user1.setBanned(true);
        userRepository.save(user1);

        User user2 = new User("user2", "user2@example.com", "pass");
        userRepository.save(user2);

        User user3 = new User("user3", "user3@example.com", "pass");
        user3.setBanned(true);
        userRepository.save(user3);

        long bannedCount = userRepository.countByIsBannedTrue();
        assertEquals(2, bannedCount);

        List<User> bannedUsers = userRepository.findByIsBannedTrue();
        assertEquals(2, bannedUsers.size());
    }
}
