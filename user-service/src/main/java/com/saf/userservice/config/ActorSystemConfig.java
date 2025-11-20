package com.saf.userservice.config;

import com.saf.core.ActorSystem;
import com.saf.core.ActorRef;
import com.saf.userservice.actor.AnnonceActor;
import com.saf.userservice.actor.EmailActor;
import com.saf.userservice.actor.UserActor;
import com.saf.userservice.repository.AnnonceRepository;
import com.saf.userservice.repository.UserRepository;
import com.saf.userservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration du système d'acteurs
 * Initialise tous les acteurs avec supervision et résilience
 */
@Configuration
public class ActorSystemConfig {

    @Value("${spring.mail.username:noreply@marketplace.com}")
    private String fromEmail;

    @Bean
    public ActorSystem actorSystem() {
        return new ActorSystem();
    }

    @Bean
    public ActorRef emailActor(ActorSystem actorSystem, JavaMailSender mailSender) {
        return actorSystem.createActor("emailActor",
                () -> new EmailActor(mailSender, fromEmail));
    }

    @Bean
    public ActorRef userActor(ActorSystem actorSystem,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            ActorRef emailActor) {
        return actorSystem.createActor("userActor",
                () -> new UserActor(userRepository, passwordEncoder, jwtUtil, emailActor));
    }

    @Bean
    public ActorRef annonceActor(ActorSystem actorSystem,
            AnnonceRepository annonceRepository,
            UserRepository userRepository) {
        return actorSystem.createActor("annonceActor",
                () -> new AnnonceActor(annonceRepository, userRepository));
    }
}
