package com.saf.userservice.repository;

import com.saf.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    long countByEnabledTrue();

    long countByIsBannedTrue();

    java.util.List<User> findByIsBannedTrue();

    java.util.List<User> findByUsernameContainingOrEmailContaining(String username, String email);
}
