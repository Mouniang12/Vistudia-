# Vistudia - Guide d'Installation et de Démarrage

Ce projet est composé d'un backend en Node.js/Express et d'une application mobile frontend sous Android (Kotlin).

## Prérequis

* **Node.js** (et npm) pour exécuter le serveur backend.
* **MongoDB** pour la base de données.
* **Android Studio** pour compiler et exécuter l'application mobile.

---

## 1. Démarrer le Backend (Node.js)

Le backend utilise Express et Mongoose pour interagir avec une base de données MongoDB.

1.  **Ouvrir le terminal** et naviguer vers le dossier `backend` :
    ```bash
    cd Vistudia-/backend
    ```

2.  **Installer les dépendances** :
    ```bash
    npm install
    ```

3.  **Configurer les variables d'environnement** :
    Créez un fichier `.env` à la racine du dossier `backend` et ajoutez-y les variables requises :
    ```env
    PORT=3000
    DB_CONNECTION_ID=votre_chaine_de_connexion_mongodb
    JWT_SECRET=super_cle_secrete_vistudia_2026_tr3s_s3curis3e
    ```
    *(Remplacez `votre_chaine_de_connexion_mongodb` par l'URL de votre base de données MongoDB locale ou cloud).*

4.  **Lancer le serveur** :
    Comme il n'y a pas de script de démarrage explicite dans le `package.json`, utilisez directement Node pour lancer le fichier principal :
    ```bash
    node src/server.js
    ```
    Le serveur devrait maintenant écouter sur le port spécifié (ex: `http://localhost:3000/`).

---

## 2. Démarrer le Frontend (Application Android)

Le frontend est une application Android native en Kotlin utilisant Retrofit pour les appels API.

1.  **Ouvrir le projet** :
    Lancez **Android Studio** et ouvrez le dossier `Vistudia-/frontend`.

2.  **Configurer l'URL de l'API (`BASE_URL`)** :
    L'application configure son client Retrofit en utilisant la variable `BuildConfig.BASE_URL`.
    * Assurez-vous que cette variable est correctement définie dans vos fichiers Gradle  dans `app/build.gradle.kts`.
    * **Attention** : Si vous testez l'application sur l'émulateur Android et que votre backend tourne en local sur le port 3000, le `BASE_URL` doit être configuré sur `http://10.0.2.2:3000/` (l'émulateur utilise `10.0.2.2` pour accéder au `localhost` de votre machine).
    * Par défaut, BASE_URL = `10.0.2.2`.

3.  **Synchroniser et Exécuter** :
    * Laissez Android Studio synchroniser les fichiers Gradle.
    * Sélectionnez un émulateur ou un appareil physique connecté.
    * Cliquez sur le bouton **Run** (le triangle vert) pour compiler et lancer l'application.

## Information complémentaire

Pour la validation de l'email, la console du serveur va logger dans la console le lien de validation du compte. Il faut cliquer sur ce lien pour pouvoir ensuite se connecter à l'application.