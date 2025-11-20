package com.saf.transactionservice.config;

import com.saf.core.ActorSystem;
import com.saf.core.ActorRef;
import com.saf.transactionservice.actor.NotificationActor;
import com.saf.transactionservice.actor.OffreActor;
import com.saf.transactionservice.actor.TransactionActor;
import com.saf.transactionservice.client.UserServiceClient;
import com.saf.transactionservice.repository.OffreRepository;
import com.saf.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Configuration du système d'acteurs pour transaction-service
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
    public ActorRef notificationActor(ActorSystem actorSystem, JavaMailSender mailSender) {
        return actorSystem.createActor("notificationActor",
                () -> new NotificationActor(mailSender, fromEmail));
    }

    @Bean
    public ActorRef transactionActor(ActorSystem actorSystem,
            TransactionRepository transactionRepository,
            UserServiceClient userServiceClient,
            ActorRef notificationActor) {
        return actorSystem.createActor("transactionActor",
                () -> new TransactionActor(transactionRepository, userServiceClient, notificationActor));
    }

    @Bean
    public ActorRef offreActor(ActorSystem actorSystem,
            OffreRepository offreRepository,
            TransactionRepository transactionRepository,
            UserServiceClient userServiceClient,
            ActorRef notificationActor) {
        return actorSystem.createActor("offreActor",
                () -> new OffreActor(offreRepository, transactionRepository, userServiceClient, notificationActor));
    }
}
