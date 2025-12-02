# Tests du Projet Magasin2Disque

Ce document décrit les tests unitaires et d'intégration mis en place pour le projet.

## Structure des Tests

### User Service

#### Tests Unitaires
- **AdminServiceTest** : Tests du service d'administration
  - Statistiques globales
  - Bannissement/débannissement d'utilisateurs
  - Suppression d'annonces
  - Gestion des actions administratives
  - Recherche d'utilisateurs

- **MessageServiceTest** : Tests du service de messagerie
  - Envoi de messages
  - Gestion des conversations
  - Messages non lus
  - Messages avec pièces jointes
  - Suppression de messages

- **UserTest** : Tests du modèle User
  - Création d'utilisateur
  - Valeurs par défaut
  - Bannissement
  - Vérification d'email

- **AnnonceTest** : Tests du modèle Annonce
  - Création d'annonce
  - États (NEUF, OCCASION, COLLECTOR)
  - Disponibilité

#### Tests d'Intégration
- **AdminControllerIntegrationTest** : Tests du contrôleur admin
  - Endpoints de statistiques
  - Gestion des utilisateurs
  - Actions administratives
  - Contrôle d'accès (rôle ADMIN requis)

- **AuthControllerIntegrationTest** : Tests d'authentification
  - Inscription
  - Connexion
  - Vérification d'email
  - Gestion des erreurs (doublons, credentials invalides)

- **UserServiceApplicationIntegrationTest** : Tests de bout en bout
  - Cycle de vie complet d'un utilisateur
  - Création et gestion d'annonces
  - Recherche d'utilisateurs et d'annonces
  - Gestion des utilisateurs bannis

### Transaction Service

#### Tests Unitaires
- **TransactionTest** : Tests du modèle Transaction
  - Création de transaction
  - États (PENDING, COMPLETED, CANCELLED)
  - Types d'achat (DIRECT, OFFRE_ACCEPTEE)

- **OffreTest** : Tests du modèle Offre
  - Création d'offre
  - États (PENDING, ACCEPTED, REFUSED)
  - Prix proposé vs prix initial

#### Tests d'Intégration
- **TransactionRepositoryIntegrationTest** : Tests du repository Transaction
  - Recherche par acheteur
  - Recherche par vendeur
  - Recherche par annonce

- **OffreRepositoryIntegrationTest** : Tests du repository Offre
  - Recherche par vendeur et statut
  - Recherche par acheteur
  - Recherche par annonce

- **TransactionControllerIntegrationTest** : Tests du contrôleur Transaction
  - Achat direct
  - Récupération de transactions
  - Filtrage par utilisateur
  - Comptage de transactions

- **OffreControllerIntegrationTest** : Tests du contrôleur Offre
  - Faire une offre
  - Accepter une offre
  - Refuser une offre
  - Récupération des offres

- **TransactionServiceApplicationIntegrationTest** : Tests de bout en bout
  - Flux complet de transaction
  - Flux complet d'offre (création → acceptation → transaction)
  - Gestion de multiples transactions
  - Gestion de multiples offres par annonce

## Configuration des Tests

### Dépendances
Les tests utilisent :
- **JUnit 5** : Framework de tests
- **Mockito** : Mock objects pour tests unitaires
- **Spring Boot Test** : Support de tests Spring Boot
- **H2 Database** : Base de données en mémoire pour les tests
- **Spring Security Test** : Tests avec authentification

### Configuration
Les fichiers `application-test.yml` configurent :
- Base de données H2 en mémoire
- Désactivation d'Eureka
- Configuration mail pour tests
- Propriétés JWT pour tests

## Exécution des Tests

### Tous les tests
```bash
mvn test
```

### Tests d'un service spécifique
```bash
# User Service
cd user-service
mvn test

# Transaction Service
cd transaction-service
mvn test
```

### Tests d'une classe spécifique
```bash
mvn test -Dtest=AdminServiceTest
```

### Avec rapport de couverture
```bash
mvn test jacoco:report
```

## Couverture des Tests

### User Service
- **Services** : AdminService, MessageService
- **Controllers** : AdminController, AuthController
- **Models** : User, Annonce
- **Intégration** : Cycle de vie complet des utilisateurs et annonces

### Transaction Service
- **Models** : Transaction, Offre
- **Repositories** : TransactionRepository, OffreRepository
- **Controllers** : TransactionController, OffreController
- **Intégration** : Flux complets de transactions et d'offres

## Scénarios de Test Principaux

### 1. Authentification Utilisateur
- Inscription → Vérification email → Connexion
- Gestion des erreurs (utilisateur existant, email non vérifié)

### 2. Administration
- Statistiques du système
- Bannissement/débannissement
- Suppression de contenu
- Traçabilité des actions

### 3. Transactions
- Achat direct d'une annonce
- Proposition d'offre → Acceptation → Création de transaction
- Refus d'offre

### 4. Messagerie
- Création de conversation
- Envoi de messages
- Gestion des messages non lus

## Bonnes Pratiques

1. **Isolation** : Chaque test est indépendant grâce à `@Transactional`
2. **Données de test** : Utilisation de `@BeforeEach` pour initialiser les données
3. **Nettoyage** : Les transactions sont rollback automatiquement
4. **Assertions claires** : Utilisation de messages explicites
5. **Coverage** : Vise une couverture > 80%

## Améliorations Futures

- [ ] Tests de performance
- [ ] Tests de sécurité (injections, XSS)
- [ ] Tests de charge
- [ ] Tests E2E avec Selenium
- [ ] Mutation testing
- [ ] Tests de l'API Actor Framework
