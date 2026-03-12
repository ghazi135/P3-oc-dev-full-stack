# ChâTop – API REST (Spring Boot)

Service backend du site de locations saisonnières **ChâTop**, exposé en API REST.  
Ce document décrit les endpoints, les modèles de données et la procédure de mise en route.

---

## Spécification des endpoints

| Endpoint | Verbe | Rôle | Paramètres | Corps (JSON) | Codes HTTP | Réponse type |
|----------|--------|------|------------|---------------|------------|--------------|
| `/api/auth/register` | POST | Inscription d’un nouvel utilisateur | - | `{ "name": "string", "email": "string", "password": "string" }` | 201, 400, 409, 500 | `{ "token": "jwt", "message": "User registered" }` |
| `/api/auth/login` | POST | Connexion et obtention d’un JWT | - | `{ "email": "string", "password": "string" }` | 200, 400, 401, 500 | `{ "token": "jwt", "message": "Login successful" }` |
| `/api/auth/me` | GET | Profil du compte connecté (via JWT) | - | - | 200, 401, 500 | `{ "id": 1, "name": "...", "email": "...", "created_at": "...", "updated_at": "..." }` |
| `/api/messages` | POST | Envoi d’un message au propriétaire d’une annonce | - | `{ "rental_id": number, "user_id": number, "message": "string" }` | 201, 400, 401, 404, 500 | `{ "message": "Message sent with success" }` |
| `/api/rentals` | GET | Liste de toutes les annonces | - | - | 200, 401, 500 | `{ "rentals": [ { "id", "name", "surface", "price", "picture", "description", "owner_id", "created_at", "updated_at" }, ... ] }` |
| `/api/rentals/:id` | GET | Détail d’une annonce par identifiant | path : `id` | - | 200, 401, 404, 500 | Objet location (même structure qu’un élément de la liste) |
| `/api/rentals` | POST | Ajout d’une annonce | - | **multipart/form-data** : `name`, `surface`, `price`, `description`, `picture` (fichier) | 201, 400, 401, 500 | `{ "message": "Rental created !" }` |
| `/api/rentals/:id` | PUT | Modification d’une annonce | path : `id` | **multipart/form-data** : `name`, `surface`, `price`, `description`, `picture` (optionnel) | 200, 400, 401, 403, 404, 500 | `{ "message": "Rental updated !" }` |
| `/api/user/:id` | GET | Affichage du profil d’un utilisateur (ex. propriétaire) | path : `id` | - | 200, 401, 404, 500 | `{ "id", "name", "email", "created_at", "updated_at" }` |

**Sécurité :** hormis `register` et `login`, chaque requête doit inclure l’en-tête  
`Authorization: Bearer <votre_token>`.

**Upload d’images :** pour la création et la mise à jour des annonces, la photo est envoyée en fichier (formulaire multipart), et non en URL. Le serveur stocke le fichier et renvoie l’URL dans le champ `picture` des réponses.

---

## Rôle des principales fonctionnalités

- **Compte utilisateur** : inscription (register), connexion (login) avec JWT ; la déconnexion consiste à supprimer le token côté client.
- **Accueil** : affichage de la liste des annonces (`GET /api/rentals`) avec accès à la création, à l’édition et au détail de chaque offre.
- **Fiche annonce** : détail d’une location (`GET /api/rentals/:id`) et formulaire de contact (`POST /api/messages`) ; le serveur répond par *"Message sent with success"*.
- **Modification d’annonce** : `PUT /api/rentals/:id` ; réponse *"Rental updated !"*.
- **Nouvelle annonce** : `POST /api/rentals` ; réponse *"Rental created !"*.
- **Profil** : utilisateur courant (`GET /api/auth/me`) ou profil public (`GET /api/user/:id`).

---

## Modèles de requête et de réponse (DTO)

- **RegisterRequestDto** — `POST /api/auth/register`  
  `{ "name": string, "email": string, "password": string }`

- **LoginRequestDto** — `POST /api/auth/login`  
  `{ "email": string, "password": string }`

- **AuthResponseDto** — réponses register / login  
  `{ "token": string, "message": string }`

- **UserResponseDto** — `GET /api/auth/me` et `GET /api/user/:id`  
  `{ "id": number, "name": string, "email": string, "created_at": string, "updated_at": string }` (dates au format ISO-8601)

- **MessageRequestDto** — `POST /api/messages`  
  `{ "rental_id": number, "user_id": number, "message": string }`  
  L’expéditeur réel est celui identifié par le JWT ; le champ `user_id` est accepté pour respect du contrat d’API.

- **MessageResponseDto** — réponse après envoi d’un message  
  `{ "message": "Message sent with success" }`

- **RentalRequestDto** — création / mise à jour d’annonce en **multipart/form-data**  
  Champs : `name`, `surface`, `price`, `description`, `picture` (fichier). En PUT, `picture` est facultatif.

- **RentalResponseDto** — une annonce (détail ou élément de liste)  
  `id`, `name`, `surface`, `price`, `picture`, `description`, `owner_id`, `created_at`, `updated_at`

- **RentalListResponseDto** — réponse de `GET /api/rentals`  
  `{ "rentals": [ ... ] }`

---

## Bibliothèques utilisées (Maven)

- **spring-boot-starter-web** — Contrôleurs REST, routage HTTP et sérialisation JSON.

- **spring-boot-starter-data-jpa** — Accès aux données (JPA/Hibernate, repositories) et persistance en MySQL.

- **spring-boot-starter-validation** — Contraintes sur les DTO (`@NotBlank`, `@Email`, `@Size`, etc.).

- **spring-boot-starter-security** — Politique d’accès, filtres et support pour le filtre JWT.

- **jjwt** — Émission et contrôle des jetons JWT.

- **mysql-connector-j** — Pilote JDBC MySQL.

- **lombok** — Annotations pour getters/setters et réduction du code répétitif (`@Data`, etc.).

- **springdoc-openapi-starter-webmvc-ui** — Documentation OpenAPI et interface Swagger.

---

## Mise en route

### Ce qu’il faut avoir

- **Java 17**, **Maven**, et une instance **MySQL** (ou utilisation de Docker pour MySQL).

### Configuration sensible

Ne pas mettre les identifiants ni le secret JWT dans le code. Utiliser des **variables d’environnement** ou un fichier **`.env`** (à ne pas committer ; déjà ignoré dans `.gitignore`) :

- `DB_URL` — (optionnel) URL JDBC ; valeur par défaut fournie dans `application.yml`.
- `DB_USERNAME` — utilisateur MySQL (ex. `chatop`).
- `DB_PASSWORD` — mot de passe MySQL.
- `JWT_SECRET` — clé utilisée pour signer les JWT.
- `JWT_EXPIRATION` — (optionnel) durée de vie du token en millisecondes.

En production, éviter le compte root ; créer un utilisateur MySQL dédié avec des droits restreints.

### Option : MySQL avec Docker

À la racine du projet :

```bash
docker compose up -d
```

La base `chatop_db` et l’utilisateur sont créés selon `docker-compose.yml`. Ajuster `DB_USERNAME` et `DB_PASSWORD` en fonction.

### Démarrer l’application

1. **Récupérer le code**
   ```bash
   git clone https://github.com/ghazi135/P3-oc-dev-full-stack.git
   cd P3-oc-dev-full-stack
   ```

2. **Compiler**
   ```bash
   ./mvnw clean install
   ```
   (sous Windows : `mvnw.cmd`)

3. **Lancer le serveur**
   ```bash
   ./mvnw spring-boot:run
   ```
   Ou exécuter la classe `ChatopApplication` depuis l’IDE.

L’API écoute par défaut sur **http://localhost:8080** (ou un autre port si configuré, ex. 3001).

### Accès à la doc Swagger

- **Interface** : http://localhost:8080/swagger-ui.html  
- **Spécification JSON** : http://localhost:8080/v3/api-docs  

L’accès à Swagger ne demande pas de token. Pour appeler les endpoints protégés depuis l’UI, utiliser *Authorize* et coller le JWT reçu après un login.

---

Avec ces indications, on peut reproduire l’environnement (BDD + backend) et utiliser l’API telle que décrite dans le tableau des endpoints.
