# 🎵 Magasin 2 Disque

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Description

**Magasin 2 Disque** est une plateforme marketplace moderne de vente de disques vinyles et CD. L'application suit une architecture microservices pour assurer la scalabilité, la résilience et la maintenabilité.

## 🏗️ Architecture

L'application est composée de 4 microservices principaux :

```
┌─────────────────┐
│   API Gateway   │  ← Point d'entrée unique (Port 8080)
│   + Frontend    │
└────────┬────────┘
         │
    ┌────┴─────────────────────┐
    │   Eureka Server (8761)   │  ← Service Discovery
    └──────────┬───────────────┘
               │
    ┏━━━━━━━━━━┻━━━━━━━━━━━┓
    ┃                       ┃
┌───▼────────┐    ┌────────▼──────┐
│   User     │    │  Transaction  │
│  Service   │◄───┤   Service     │
│  (8081)    │    │   (8082)      │
└────────────┘    └───────────────┘
```

### Microservices

#### 1. **Eureka Server** (Port 8761)
Service de découverte et registry pour tous les microservices.
- Auto-enregistrement des services
- Load balancing côté client
- Health checking

#### 2. **API Gateway** (Port 8080)
Point d'entrée unique de l'application.
- Routage intelligent vers les microservices
- Circuit Breaker avec Resilience4j
- Hébergement du frontend (HTML/CSS/JS)
- Monitoring avec Spring Boot Actuator

#### 3. **User Service** (Port 8081)
Gestion des utilisateurs et de l'authentification.
- Inscription et connexion
- Authentification JWT
- Gestion des profils
- Envoi d'emails (notifications)
- CRUD utilisateurs

#### 4. **Transaction Service** (Port 8082)
Gestion des transactions commerciales.
- Annonces de vente
- Gestion des offres
- Système de messagerie entre utilisateurs
- Notifications en temps réel
- Communication avec User Service via OpenFeign

## 🛠️ Technologies Utilisées

### Backend

| Technologie | Version | Description |
|-------------|---------|-------------|
| **Java** | 21 | Langage de programmation |
| **Spring Boot** | 3.2.0 | Framework principal |
| **Spring Cloud** | 2023.0.0 | Microservices stack |
| **Spring Security** | 3.2.0 | Sécurité et authentification |
| **Spring Data JPA** | 3.2.0 | Persistence et ORM |
| **Netflix Eureka** | Latest | Service Discovery |
| **Spring Cloud Gateway** | Latest | API Gateway réactif |
| **OpenFeign** | Latest | Client HTTP déclaratif |
| **Resilience4j** | Latest | Circuit Breaker & Résilience |
| **PostgreSQL** | Latest | Base de données relationnelle |
| **JWT (jjwt)** | 0.12.3 | JSON Web Tokens |
| **Spring Mail** | 3.2.0 | Envoi d'emails |
| **Actor Framework** | 1.0.0 | Framework acteur personnalisé |

### Frontend

| Technologie | Description |
|-------------|-------------|
| **HTML5** | Structure des pages |
| **CSS3** | Stylisation |
| **JavaScript** | Logique client (Vanilla JS) |
| **Fetch API** | Communication avec l'API |

### Outils de Build & Test

| Outil | Version | Description |
|-------|---------|-------------|
| **Maven** | 3.x | Build automation |
| **JUnit** | 5.x | Tests unitaires |
| **Mockito** | Latest | Mocking framework |
| **H2 Database** | Latest | Base de données en mémoire (tests) |

### DevOps & Monitoring

| Technologie | Description |
|-------------|-------------|
| **Spring Boot Actuator** | Monitoring et métriques |
| **Resilience4j** | Circuit Breaker, Retry, Rate Limiter |

## 📦 Prérequis

- **Java JDK** 21 ou supérieur
- **Maven** 3.8+ 
- **PostgreSQL** 12+ (avec 2 bases de données : `user_db` et `transaction_db`)
-

## 🚀 Installation et Lancement

### 1. Configuration de la Base de Données

Créez deux bases de données PostgreSQL :

```sql
CREATE DATABASE user_db;
CREATE DATABASE transaction_db;
```

### 2. Configuration des Services

Configurez les fichiers `application.yml` de chaque service avec vos paramètres PostgreSQL :
- `user-service/src/main/resources/application.yml`
- `transaction-service/src/main/resources/application.yml`

### 3. Lancement de l'Application



#### Option 1 : Script PowerShell (Windows uniquement)

```powershell
.\start-services.ps1
```



#### Option 2 : Lancement Manuel

```bash
# 1. Eureka Server
cd eureka-server
mvn spring-boot:run

# 2. User Service (attendre 15s)
cd user-service
mvn spring-boot:run

# 3. Transaction Service (attendre 20s)
cd transaction-service
mvn spring-boot:run

# 4. API Gateway (attendre 15s)
cd api-gateway
mvn spring-boot:run
```

### 4. Accès à l'Application

| Service | URL |
|---------|-----|
| **Application Web** | http://localhost:8080 |
| **API Gateway** | http://localhost:8080/api |
| **Eureka Dashboard** | http://localhost:8761 |
| **User Service** | http://localhost:8081 |
| **Transaction Service** | http://localhost:8082 |

## 📱 Fonctionnalités

### Pour les Utilisateurs
- ✅ Inscription et connexion sécurisées (JWT)
- ✅ Gestion du profil
- ✅ Création et gestion d'annonces de vente
- ✅ Recherche et consultation d'annonces
- ✅ Système d'offres sur les annonces
- ✅ Messagerie privée entre acheteurs et vendeurs
- ✅ Notifications en temps réel
- ✅ Historique des transactions

### Pour les Administrateurs
- ✅ Dashboard d'administration
- ✅ Gestion des utilisateurs
- ✅ Modération des annonces
- ✅ Statistiques et monitoring

## 🔒 Sécurité

- Authentification JWT avec tokens sécurisés
- Hashage des mots de passe avec BCrypt
- Spring Security pour la protection des endpoints
- Validation des entrées utilisateur
- Circuit Breaker pour la résilience

## 📊 Tests

```bash
# Lancer tous les tests
mvn test

# Tests d'un service spécifique
cd user-service
mvn test
```

## 🤝 Structure du Projet

```
magasin-2-disque/
├── eureka-server/          # Service Discovery
├── api-gateway/            # Gateway + Frontend
│   └── src/main/resources/static/  # Pages HTML
├── user-service/           # Gestion utilisateurs
├── transaction-service/    # Gestion transactions
├── lib-repo/              # Dépendances locales (Actor Framework)
├── start-services.py      # Script de lancement universel
├── start-services.sh      # Script Bash
├── start-services.ps1     # Script PowerShell
└── pom.xml               # POM parent
```

## 📝 License

Ce projet est sous licence MIT.

## 👨‍💻 Auteurs

Développé par l'équipe SAF

---

**Happy Coding! 🎵🎶**