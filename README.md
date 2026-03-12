# ChâTop – Backend Spring Boot

Backend Java / Spring Boot pour le portail de location saisonnière **ChâTop**.  
Ce document décrit l’API REST et l’installation du projet, dans le même format que la documentation d’API demandée (exercice 1 du P3).

---

## Définition d’API

| URL | Méthode | Description | Params (path/query) | Body (JSON) | Status | Exemple de réponse |
|-----|---------|-------------|----------------------|-------------|--------|---------------------|
| `/api/auth/register` | POST | Créer un compte utilisateur | - | `{ "name": "string", "email": "string", "password": "string" }` | 201, 400, 409, 500 | `{ "token": "jwt", "message": "User registered" }` |
| `/api/auth/login` | POST | Authentifier un utilisateur | - | `{ "email": "string", "password": "string" }` | 200, 400, 401, 500 | `{ "token": "jwt", "message": "Login successful" }` |
| `/api/auth/me` | GET | Récupérer les infos de l’utilisateur actuellement authentifié (JWT) | - | - | 200, 401, 500 | `{ "id": 1, "name": "Test TEST", "email": "test@test.com", "created_at": "2024-01-15T10:00:00Z", "updated_at": "2024-01-15T10:00:00Z" }` |
| `/api/messages` | POST | Envoyer un message au propriétaire d’une location | - | `{ "rental_id": number, "user_id": number, "message": "string" }` | 201, 400, 401, 404, 500 | `{ "message": "Message sent with success" }` |
| `/api/rentals` | GET | Récupérer la liste de toutes les locations | - | - | 200, 401, 500 | `{ "rentals": [ { "id": 1, "name": "test house 1", "surface": 24, "price": 300, "picture": "http://.../uploads/xxx.jpg", "description": "...", "owner_id": 1, "created_at": "...", "updated_at": "..." }, ... ] }` |
| `/api/rentals/:id` | GET | Récupérer le détail d’une location par son id | path: `id` (location) | - | 200, 401, 404, 500 | `{ "id": 1, "name": "dream house", "surface": 24, "price": 30, "picture": "http://...", "description": "...", "owner_id": 1, "created_at": "...", "updated_at": "..." }` |
| `/api/rentals` | POST | Créer une nouvelle location | - | **multipart/form-data** : `name`, `surface`, `price`, `description`, `picture` (fichier image) | 201, 400, 401, 500 | `{ "message": "Rental created !" }` |
| `/api/rentals/:id` | PUT | Mettre à jour une location existante | path: `id` (location) | **multipart/form-data** : `name`, `surface`, `price`, `description`, `picture` (fichier, optionnel) | 200, 400, 401, 403, 404, 500 | `{ "message": "Rental updated !" }` |
| `/api/user/:id` | GET | Récupérer le profil public d’un utilisateur (ex. propriétaire) | path: `id` (utilisateur) | - | 200, 401, 404, 500 | `{ "id": 2, "name": "Owner Name", "email": "test@test.com", "created_at": "...", "updated_at": "..." }` |

**Authentification :** les routes autres que `/api/auth/register` et `/api/auth/login` nécessitent un JWT dans le header :  
`Authorization: Bearer <token>`.

**Note :** pour POST et PUT `/api/rentals`, l’image est envoyée en fichier (`multipart/form-data`), pas en URL dans un JSON. Le backend enregistre le fichier et renvoie l’URL dans le champ `picture` des réponses GET.

---

## Fonctionnalités majeures

- **Inscription, connexion, déconnexion** : register, login, token JWT ; déconnexion gérée côté front (suppression du token).
- **Page d’accueil** : liste des locations (`GET /api/rentals`), avec boutons création, édition, détail.
- **Détail d’une offre** : affichage d’une location (`GET /api/rentals/:id`), formulaire pour envoyer un message au propriétaire (`POST /api/messages`). Réponse : *"Message sent with success"*.
- **Édition d’une offre** : mise à jour d’une location (`PUT /api/rentals/:id`). Réponse : *"Rental updated !"*.
- **Création d’une offre** : création d’une location (`POST /api/rentals`). Réponse : *"Rental created !"*.
- **Profil utilisateur** : infos de l’utilisateur connecté (`GET /api/auth/me`) ou profil public (`GET /api/user/:id`).

---

## Les DTO côté back

- **RegisterRequestDto** (pour `POST /api/auth/register`)  
  `{ "name": string, "email": string, "password": string }`

- **LoginRequestDto** (pour `POST /api/auth/login`)  
  `{ "email": string, "password": string }`

- **AuthResponseDto** (réponse register / login)  
  `{ "token": string, "message": string }`

- **UserResponseDto** (pour `GET /api/auth/me` et `GET /api/user/:id`)  
  `{ "id": number, "name": string, "email": string, "created_at": string (ISO-8601), "updated_at": string }`

- **MessageRequestDto** (pour `POST /api/messages`)  
  `{ "rental_id": number, "user_id": number, "message": string }`  
  *(Le back utilise toujours l’utilisateur authentifié via JWT ; `user_id` peut être présent pour compatibilité contrat.)*

- **MessageResponseDto** (réponse POST messages)  
  `{ "message": "Message sent with success" }`

- **RentalRequestDto** (POST/PUT rentals, en **multipart/form-data**)  
  Champs : `name`, `surface`, `price`, `description`, `picture` (fichier). En PUT, `picture` est optionnel.

- **RentalResponseDto** (détail d’une location, élément de la liste)  
  `id`, `name`, `surface`, `price`, `picture`, `description`, `owner_id`, `created_at`, `updated_at`

- **RentalListResponseDto** (réponse `GET /api/rentals`)  
  `{ "rentals": [ RentalResponseDto, ... ] }`

---

## Dépendances Spring Boot typiques pour ce projet

- **spring-boot-starter-web**  
  Exposer les controllers REST, gérer les routes HTTP et la sérialisation JSON pour l’API.

- **spring-boot-starter-data-jpa**  
  Couche d’accès aux données avec JPA/Hibernate et les Repository, mapping des entités vers MySQL.

- **spring-boot-starter-validation**  
  Validation des DTO : emails, champs obligatoires, tailles, etc. (ex. `@NotBlank`, `@Email`, `@Size`).

- **spring-boot-starter-security**  
  Sécurité : filtres, configuration des routes protégées, contexte d’authentification ; base pour le filtre JWT.

- **jjwt** (lib JWT)  
  Création et vérification des tokens JWT (login / authentification).

- **mysql-connector-j**  
  Driver JDBC pour communiquer avec la base MySQL.

- **lombok**  
  Réduction du code boilerplate : `@Getter`, `@Setter`, `@Data`, etc.

- **springdoc-openapi-starter-webmvc-ui**  
  Génération de la doc Swagger / OpenAPI et exposition via une UI web.

---

## Installation et démarrage

### Prérequis

- Java 17
- Maven
- MySQL (en cours d’exécution) ou Docker pour lancer MySQL

### Variables d’environnement

Les identifiants de la BDD et le secret JWT ne doivent **pas** être dans le code. Les configurer via des variables d’environnement ou un fichier **`.env`** (non versionné, présent dans `.gitignore`) :

- `DB_URL` (optionnel) : par défaut `jdbc:mysql://localhost:3306/chatop_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- `DB_USERNAME` : utilisateur MySQL (ex. `chatop`)
- `DB_PASSWORD` : mot de passe MySQL
- `JWT_SECRET` : secret pour signer les JWT
- `JWT_EXPIRATION` (optionnel) : durée de validité du token en ms (défaut : 86400000)

En production, ne pas utiliser le compte root MySQL ; utiliser un utilisateur avec droits limités.

### Base de données avec Docker (optionnel)

Pour lancer MySQL en local avec Docker :

```bash
docker compose up -d
```

Cela crée la base `chatop_db` et un utilisateur (voir `docker-compose.yml`). Adapter `DB_USERNAME` et `DB_PASSWORD` en conséquence.

### Lancer le backend

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/ghazi135/P3-oc-dev-full-stack.git
   cd P3-oc-dev-full-stack
   ```

2. Build Maven :
   ```bash
   ./mvnw clean install
   ```
   (sous Windows : `mvnw.cmd`)

3. Démarrer l’application :
   ```bash
   ./mvnw spring-boot:run
   ```
   Ou depuis ton IDE : lancer la classe `ChatopApplication`.

L’API est disponible sur **http://localhost:8080** (ou le port configuré, ex. 3001 si passé en argument).

### Documentation Swagger

Une fois l’application démarrée :

- **Swagger UI :** http://localhost:8080/swagger-ui.html  
- **OpenAPI (JSON) :** http://localhost:8080/v3/api-docs  

La doc Swagger est accessible sans authentification ; pour tester les routes protégées, utiliser le bouton *Authorize* et renseigner le JWT obtenu après login.

---

En suivant ces étapes, un autre développeur peut installer le projet et la BDD, puis faire tourner le backend et utiliser l’API comme décrit dans le tableau ci-dessus.
