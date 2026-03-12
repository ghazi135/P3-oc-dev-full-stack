# ChâTop – Base de données (documentation BDD)

Documentation de la base de données du projet ChâTop, au format demandé pour le livrable P3.

---

## Les entités métiers à manipuler

```
USERS (
  id         INTEGER PRIMARY KEY AUTO_INCREMENT,
  email      VARCHAR(255) NOT NULL UNIQUE,
  name       VARCHAR(255) NOT NULL,
  password   VARCHAR(255) NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

RENTALS (
  id          INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
  name        VARCHAR(255) NOT NULL,
  surface     INTEGER NOT NULL,
  price       INTEGER NOT NULL,
  picture     VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  owner_id    INTEGER NOT NULL,
  created_at  TIMESTAMP,
  updated_at  TIMESTAMP
);

MESSAGES (
  id         INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
  rental_id  INTEGER NOT NULL,
  user_id    INTEGER NOT NULL,
  message    TEXT NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

---

## Contraintes des clés étrangères

```
ALTER TABLE rentals
ADD CONSTRAINT fk_rentals_owner
FOREIGN KEY (owner_id) REFERENCES users(id);

ALTER TABLE messages
ADD CONSTRAINT fk_messages_rental
FOREIGN KEY (rental_id) REFERENCES rentals(id),
ADD CONSTRAINT fk_messages_user
FOREIGN KEY (user_id) REFERENCES users(id);
```

*(Dans le script complet ci‑dessous, ces contraintes sont définies directement dans les `CREATE TABLE`.)*

---

## Script de création de la base de données

Le fichier **`schema.sql`** à la racine du projet contient le script complet. Résumé de sa structure :

1. **Création de la base** : `CREATE DATABASE chatop_db` (utf8mb4), puis `USE chatop_db`.
2. **Table USERS** : `CREATE TABLE IF NOT EXISTS users (...)` avec contrainte `UNIQUE` sur `email`.
3. **Table RENTALS** : `CREATE TABLE IF NOT EXISTS rentals (...)` avec clé étrangère `owner_id` → `users(id)`.
4. **Table MESSAGES** : `CREATE TABLE IF NOT EXISTS messages (...)` avec clés étrangères `rental_id` → `rentals(id)` et `user_id` → `users(id)`.
5. **Utilisateur SQL dédié** : `CREATE USER 'chatop_user'`, `GRANT ... ON chatop_db.*`, `FLUSH PRIVILEGES`.
6. **Vérification** : `SHOW DATABASES`, `USE chatop_db`, `SHOW TABLES`, `SELECT * FROM users/rentals/messages`.

---

### Contenu du fichier `schema.sql`

```sql
-- ======================================================
-- Création de la base de données ChâTop
-- ======================================================
CREATE DATABASE IF NOT EXISTS chatop_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
USE chatop_db;

-- ======================================================
-- Table USERS
-- ======================================================
CREATE TABLE IF NOT EXISTS users (
id INT AUTO_INCREMENT PRIMARY KEY,
email VARCHAR(255) NOT NULL,
name VARCHAR(255) NOT NULL,
password VARCHAR(255) NOT NULL,
created_at TIMESTAMP NULL,
updated_at TIMESTAMP NULL,
CONSTRAINT uk_users_email UNIQUE (email)
);

-- ======================================================
-- Table RENTALS
-- ======================================================
CREATE TABLE IF NOT EXISTS rentals (
id INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(255) NOT NULL,
surface INT NOT NULL,
price INT NOT NULL,
picture VARCHAR(255) NOT NULL,
description TEXT NOT NULL,
owner_id INT NOT NULL,
created_at TIMESTAMP NULL,
updated_at TIMESTAMP NULL,
CONSTRAINT fk_rentals_owner
FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- ======================================================
-- Table MESSAGES
-- ======================================================
CREATE TABLE IF NOT EXISTS messages (
id INT AUTO_INCREMENT PRIMARY KEY,
rental_id INT NOT NULL,
user_id INT NOT NULL,
message TEXT NOT NULL,
created_at TIMESTAMP NULL,
updated_at TIMESTAMP NULL,
CONSTRAINT fk_messages_rental
FOREIGN KEY (rental_id) REFERENCES rentals(id),
CONSTRAINT fk_messages_user
FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ======================================================
-- Utilisateur SQL dédié
-- ======================================================
CREATE USER IF NOT EXISTS 'chatop_user'@'%' IDENTIFIED BY 'mot_de_passe_solide';
GRANT ALL PRIVILEGES ON chatop_db.* TO 'chatop_user'@'%';
FLUSH PRIVILEGES;

-- ======================================================
-- Vérifier les tables
-- ======================================================
SHOW DATABASES;
USE chatop_db;
SHOW TABLES;
SELECT * FROM users;
SELECT * FROM rentals;
SELECT * FROM messages;
```

Remplacer `mot_de_passe_solide` par un mot de passe réel. Côté application, utiliser `DB_USERNAME=chatop_user` et `DB_PASSWORD=<ce_mot_de_passe>` dans les variables d’environnement.

---

### Exécution du script

En ligne de commande (après création de la base si besoin) :

```bash
mysql -u root -p < schema.sql
```

Ou en se connectant d’abord à MySQL, puis en exécutant le contenu du fichier. Les commandes `SHOW` et `SELECT` à la fin permettent de vérifier que les tables sont créées et accessibles.
