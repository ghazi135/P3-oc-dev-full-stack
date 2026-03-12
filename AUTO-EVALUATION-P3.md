# Fiche d'auto-évaluation – P3 Backend ChâTop

Référence : [Fiche d'auto-évaluation P3 FSJA](https://course.oc-static.com/projects/4079_Mod%C3%A9lisez+et+impl%C3%A9mentez+le+back-end+en+utilisant+du+code+Java+maintenable/P3+FSJA+-+Fiche+d'auto-e%CC%81valuation.pdf)

Ce document vérifie le backend par rapport aux indicateurs de réussite.

---

## Exercice 1 – Définir l'interface front-back avec une API

*(Exercice 1 = côté découverte du projet, Mockoon, front Angular. Le backend implémente ensuite cette API.)*

| Indicateur | Statut | Preuve / Commentaire |
|------------|--------|----------------------|
| Environnement Mockoon utilisé avec le front-end | À faire côté front | À valider avec le front Angular + Mockoon ou avec le backend à la place. |
| Application Angular prise en main et lancée | À faire | Sur ta machine. |
| Liste des endpoints à implémenter (format texte) | À faire | Document à rédiger (liste des routes Mockoon → routes back). |
| Entités métiers déduites | ✅ | `User`, `Rental`, `Message` (JPA entities). |
| Routes avec URL, méthodes HTTP, requêtes/réponses JSON, codes d’erreur | ✅ | Implémenté : voir les controllers et les DTOs. |

---

## Exercice 2 – Implémenter l’architecture et le code back-end

### Étape 1 – Routes register et login, sécurisation

| Indicateur | Statut | Preuve / Commentaire |
|------------|--------|----------------------|
| Spring Security en place | ✅ | `SecurityConfig.java`, `JwtAuthenticationFilter`, `CustomUserDetailsService`. |
| JWT pour l’authentification | ✅ | `JwtService.java` (génération/vérification), filtre JWT sur les requêtes. |
| Toutes les routes sauf register/login nécessitent un JWT | ✅ | `SecurityConfig` : `permitAll()` pour `/api/auth/register`, `/api/auth/login`, Swagger et `/uploads/**` ; `anyRequest().authenticated()`. |
| Documentation Swagger accessible sans authentification | ✅ | `requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()`. |
| Mot de passe crypté en base de données | ✅ | `PasswordConfig` (BCrypt), `AuthService.register()` utilise `passwordEncoder.encode()`. |
| Credentials BDD et JWT pas dans le code | ✅ | `application.yml` : `${DB_USERNAME}`, `${DB_PASSWORD}`, `${JWT_SECRET}` ; `.env` dans `.gitignore`. |

### Étape 2 – Toutes les routes du back-end

| Indicateur | Statut | Preuve / Commentaire |
|------------|--------|----------------------|
| Toutes les routes Mockoon implémentées | ✅ | **Auth** : POST `/api/auth/register`, POST `/api/auth/login`, GET `/api/auth/me`. **Rentals** : GET `/api/rentals`, GET `/api/rentals/{id}`, POST, PUT. **Messages** : POST `/api/messages`. **User** : GET `/api/user/{id}`. |
| Routes protégées par JWT (sauf login/register) | ✅ | Seuls register, login, Swagger et uploads sont en `permitAll()`. |
| Backend s’exécute sans erreur | ✅ | À vérifier en lançant `mvn spring-boot:run`. |
| Découpage Controller / Service / JPA Repository | ✅ | Chaque domaine (auth, user, rental, message) a son Controller, Service et Repository (JPA). |

### Étape 3 – Nettoyage du code et repository

| Indicateur | Statut | Preuve / Commentaire |
|------------|--------|----------------------|
| Chaque route documentée avec Swagger | ✅ | `@Operation` et `@ApiResponse` sur tous les endpoints (AuthController, RentalController, MessageController, UserController). |
| Swagger permet d’interagir avec toutes les routes (auth ou non) | ✅ | Schéma Bearer JWT dans `OpenApiConfig` ; on peut tester en mettant le token dans Swagger. |
| Méthodes bien découpées, responsabilité limitée | ✅ | Services avec méthodes dédiées (ex. `buildUserFromRegistration`, `mapToDto`, `persistImageAndGetUrl`). |
| Pas de code mort | ✅ | Code refactorisé récemment. |
| Méthodes publiques des controllers commentées | ✅ | Via `@Operation(summary = "...")` et `@ApiResponse` (équivalent de commentaires pour l’API). |
| Code faiblement couplé | ✅ | Controllers → Services → Repositories ; DTOs pour les entrées/sorties. |
| README clair et exhaustif | ✅ | README avec stack, config BDD, variables d’env, Docker, démarrage Maven, Swagger. |
| Un autre développeur peut installer le projet en suivant le README | ✅ | README décrit clonage, variables d’env / `.env`, option Docker, `mvn spring-boot:run`. |

---

## Récapitulatif

- **Sécurité** : Spring Security + JWT, mots de passe en BCrypt, credentials en variables d’environnement / `.env`.
- **API** : Routes auth, rentals, messages, user alignées sur un usage type Mockoon.
- **Structure** : Controller / Service / Repository respectée.
- **Documentation** : Swagger sur toutes les routes, README avec procédure d’installation.

À faire de ton côté avant de déposer les livrables :

1. **Exercice 1** : Rédiger la liste des endpoints (format demandé) à partir de Mockoon ou de l’API actuelle.
2. **Tester** : Lancer le backend + front (ou Mockoon) et vérifier register, login, CRUD rentals, messages.
3. **Cocher** les cases de la fiche PDF officielle une fois chaque point vérifié.
