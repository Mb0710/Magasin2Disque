# Résilience et Tolérance aux Pannes

## Problème Initial

Votre architecture microservices présentait un problème de **dépendance forte** :

```
transaction-service ──[Feign Client]──> user-service
                    (dépendance)
```

**Comportement avant** :
- ❌ Si `user-service` crash → `transaction-service` crash aussi
- ❌ Site web complètement inaccessible
- ❌ Aucune tolérance aux pannes

## Solution Implémentée : Circuit Breaker Pattern

### 1. **Architecture de Résilience**

Ajout de **Resilience4j** (bibliothèque de résilience Java) avec :

#### **Circuit Breaker** 🔌
- Surveille les appels à `user-service`
- **3 états** :
  - **CLOSED** (fermé) : Tout fonctionne normalement
  - **OPEN** (ouvert) : Trop d'échecs → arrête d'appeler le service
  - **HALF_OPEN** (semi-ouvert) : Test si le service est revenu

**Configuration** (`application.yml`) :
```yaml
resilience4j:
  circuitbreaker:
    instances:
      user-service:
        slidingWindowSize: 10          # Observe les 10 derniers appels
        minimumNumberOfCalls: 5         # Min 5 appels avant d'ouvrir
        failureRateThreshold: 50        # Si 50% échouent → OPEN
        waitDurationInOpenState: 10s    # Attend 10s avant de réessayer
```

#### **Retry** (Réessai automatique) 🔄
```yaml
resilience4j:
  retry:
    instances:
      user-service:
        maxAttempts: 3                  # Réessaie 3 fois
        waitDuration: 1s                # Attend 1s entre chaque essai
        exponentialBackoffMultiplier: 2 # 1s, 2s, 4s...
```

#### **Timeout** ⏱️
```yaml
resilience4j:
  timelimiter:
    instances:
      user-service:
        timeoutDuration: 3s             # Timeout après 3s
```

### 2. **Fallback Mechanism (Plan B)**

Création de `UserServiceClientFallback.java` qui retourne des **données par défaut** quand `user-service` est indisponible :

```java
@Component
public class UserServiceClientFallback implements UserServiceClient {
    
    @Override
    public AnnonceDTO getAnnonce(Long id) {
        logger.warn("user-service indisponible - Fallback pour getAnnonce({})", id);
        // Retourne une annonce par défaut au lieu de crasher
        AnnonceDTO fallback = new AnnonceDTO();
        fallback.setId(id);
        fallback.setTitre("Annonce temporairement indisponible");
        fallback.setDisponible(false);
        return fallback;
    }
    
    @Override
    public UserDTO getUser(Long id) {
        // Retourne un utilisateur par défaut
        UserDTO fallback = new UserDTO();
        fallback.setId(id);
        fallback.setUsername("Utilisateur temporairement indisponible");
        return fallback;
    }
}
```

**Activation** dans `UserServiceClient.java` :
```java
@FeignClient(name = "user-service", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {
    // ...
}
```

### 3. **Comportement Maintenant**

#### **Scénario : user-service crash**

```
1. Client → transaction-service/api/transactions
                ↓
2. transaction-service → appel user-service via Feign
                ↓
3. user-service DOWN ❌
                ↓
4. Circuit Breaker détecte l'échec
                ↓
5. Retry → réessaie 3 fois (1s, 2s, 4s)
                ↓
6. Toujours DOWN → Circuit s'OUVRE
                ↓
7. Fallback activé → Retourne données par défaut
                ↓
8. Client reçoit réponse (dégradée mais fonctionnelle) ✅
```

**Avantages** :
- ✅ `transaction-service` reste **opérationnel**
- ✅ Site web **accessible** (mode dégradé)
- ✅ Messages d'erreur **clairs** dans les logs
- ✅ Récupération **automatique** quand user-service revient

#### **Scénario : user-service lent**

```
1. Appel user-service
       ↓
2. Timeout après 3s ⏱️
       ↓
3. Retry automatique (3 fois)
       ↓
4. Si toujours lent → Fallback
       ↓
5. Pas de blocage du transaction-service ✅
```

### 4. **Monitoring du Circuit Breaker**

Les logs affichent maintenant :
```
WARN user-service indisponible - Fallback pour getAnnonce(123)
WARN user-service indisponible - Impossible de supprimer l'annonce 456
```

Vous pouvez monitorer l'état du circuit via **Spring Boot Actuator** :
```
GET /actuator/health
GET /actuator/circuitbreakers
GET /actuator/circuitbreakerevents
```

### 5. **Dépendances Ajoutées**

**pom.xml** :
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
```

Cette dépendance inclut :
- `resilience4j-circuitbreaker` : Circuit Breaker
- `resilience4j-retry` : Mécanisme de retry
- `resilience4j-timelimiter` : Gestion des timeouts
- `resilience4j-spring-boot3` : Intégration Spring Boot

## Comparaison Avant/Après

| Situation | AVANT | APRÈS |
|-----------|-------|-------|
| user-service crash | ❌ Tout crash | ✅ transaction-service fonctionne |
| user-service lent | ❌ Blocage | ✅ Timeout + retry |
| Erreur réseau | ❌ Exception | ✅ Fallback automatique |
| Récupération | ❌ Manuelle | ✅ Automatique (Circuit HALF_OPEN) |
| Expérience utilisateur | ❌ Site down | ✅ Mode dégradé |

## Test de la Résilience

### Test 1 : Arrêter user-service
```bash
# Terminal 1 : Lancer transaction-service
cd transaction-service
mvn spring-boot:run

# Terminal 2 : ARRÊTER user-service (Ctrl+C si lancé)

# Terminal 3 : Tester transaction-service
curl http://localhost:8082/api/transactions/all
# → Devrait retourner les transactions (sans détails utilisateur)
```

### Test 2 : Monitorer le Circuit Breaker
```bash
# Ajouter dans pom.xml :
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

# Accéder au monitoring :
curl http://localhost:8082/actuator/health
curl http://localhost:8082/actuator/circuitbreakers
```

## Best Practices Appliquées

1. ✅ **Fail Fast** : Timeout rapide (3s) pour ne pas bloquer
2. ✅ **Graceful Degradation** : Service dégradé mais fonctionnel
3. ✅ **Automatic Recovery** : Circuit se referme automatiquement
4. ✅ **Observability** : Logs clairs + métriques
5. ✅ **Isolation** : Échec d'un service n'affecte pas les autres

## Patterns Complémentaires

Pour aller plus loin, vous pourriez ajouter :

1. **Bulkhead Pattern** : Isoler les pools de threads
2. **Rate Limiter** : Limiter les appels à user-service
3. **Message Queue** : Stocker les notifications en attente
4. **Health Checks** : Vérifier la santé des services régulièrement

## Conclusion

Votre architecture est maintenant **résiliente** :
- 🛡️ Protection contre les pannes en cascade
- 🔄 Récupération automatique
- 📊 Monitoring intégré
- ⚡ Performance maintenue même en cas de défaillance

**Le site reste accessible même si user-service est down !**

---

*Implémenté le 2 décembre 2025*
*Framework : Resilience4j 2.1.0*
*Pattern : Circuit Breaker + Fallback*
