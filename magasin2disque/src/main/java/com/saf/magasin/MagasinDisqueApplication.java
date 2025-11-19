package com.saf.magasin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Classe principale de l'application Magasin2Disque.
 * 
 * Architecture :
 * - RestTemplate : communication HTTP avec user-service (8081) et transaction-service (8082)
 * - Contrôleurs : délègguent les opérations aux microservices
 * 
 * @author SAF - Team
 * @version 2.0
 */
@SpringBootApplication
public class MagasinDisqueApplication {

    public static void main(String[] args) {
        SpringApplication.run(MagasinDisqueApplication.class, args);
    }
    
    /**
     * Configure un bean RestTemplate pour les appels HTTP inter-services.
     * Utilisé par les contrôleurs pour communiquer avec user-service et transaction-service.
     * 
     * @return RestTemplate configuré
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
