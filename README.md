# 🧳 Vistudia

**Vistudia** est une application mobile qui accompagne les étudiants internationaux tout au long de leurs démarches d'immigration : de la préparation du dossier jusqu'à l'installation dans le pays d'accueil.

L'idée est simple : partir étudier à l'étranger demande de gérer beaucoup de papiers, de délais et d'étapes administratives différentes selon le pays. Vistudia centralise tout ça dans une seule application, avec des guides clairs, une liste de tâches personnalisée et un espace pour échanger avec d'autres étudiants qui vivent la même expérience.

> 💡 **Vous n'êtes pas développeur ?** Pas de souci : la première partie de ce document explique le projet en langage simple. La partie technique (installation, code, etc.) arrive plus loin, pour celles et ceux qui veulent faire tourner l'application sur leur ordinateur.

---

## 📱 À quoi sert Vistudia ?

Imaginez un étudiant qui vit au Sénégal et qui souhaite venir étudier au Canada. Il doit savoir :
- Quels documents préparer (passeport, lettre d'admission, preuve de fonds, etc.)
- Dans quel ordre faire ses démarches
- Combien de temps chaque étape prend en général
- Quand ses documents vont expirer
- Où poser ses questions à d'autres personnes dans la même situation

Vistudia répond à tous ces besoins à travers plusieurs fonctionnalités :

### 🗺️ Guides d'immigration
Des guides détaillés selon le pays d'origine et le pays de destination de l'utilisateur. Chaque guide présente les grandes étapes du parcours (durée estimée, coût approximatif, documents nécessaires) avec des explications claires, étape par étape.

### ✅ Checklist personnalisée
Une liste de démarches à cocher au fur et à mesure qu'elles sont complétées, propre à la destination choisie par l'utilisateur. C'est un peu comme une liste de choses à faire, mais spécialement conçue pour les démarches d'immigration. Certaines démarches ont aussi une date d'expiration, pour ne rien oublier de renouveler.

### 🔗 Partage de checklist
La checklist peut être partagée avec une autre personne (par exemple un parent, un conjoint ou un conseiller) via un lien. Selon les droits accordés, cette personne peut simplement consulter la liste, ou aussi cocher les démarches effectuées.

### 📄 Gestion des documents
Un espace pour enregistrer ses documents importants (passeport, visa, certificat, etc.) avec leur date d'expiration. L'application peut ainsi prévenir l'utilisateur avant qu'un document n'expire.

### 💬 Forum de discussion
Des salons de discussion thématiques où les utilisateurs peuvent échanger des messages, poser des questions et partager leurs expériences avec d'autres étudiants en démarche d'immigration.

### 👤 Profil utilisateur
Chaque utilisateur peut créer un compte, se connecter, gérer ses informations personnelles (nom, nationalité, pays d'origine, pays de destination, etc.), changer son mot de passe et réinitialiser celui-ci en cas d'oubli.

### ✉️ Vérification par courriel
Lors de l'inscription, un courriel de confirmation est envoyé pour valider le compte, un peu comme sur la plupart des applications actuelles.

---

## 🧩 Comment l'application est construite (vue d'ensemble)

Vistudia est composée de **deux grandes parties** qui fonctionnent ensemble :

| Partie | Rôle | Où c'est situé |
|---|---|---|
| 📲 **Application mobile (frontend)** | Ce que l'utilisateur voit et utilise sur son téléphone Android | dossier `frontend/` |
| 🖥️ **Serveur (backend)** | Le "cerveau" caché qui gère les comptes, les données, les guides, la checklist, le forum, etc. | dossier `backend/` |

L'application mobile envoie des demandes au serveur (par exemple : « connecte-moi », « donne-moi ma checklist », « envoie mon message dans le forum »), et le serveur va chercher ou enregistre les informations dans une base de données, puis répond à l'application.

C'est le même principe que la plupart des applications que vous utilisez au quotidien (une appli sur votre téléphone qui communique avec un serveur pour fonctionner).

---

## 🛠️ Technologies utilisées

Pour les curieux ou les personnes techniques, voici les outils utilisés pour construire Vistudia :

**Backend (serveur)**
- [Node.js](https://nodejs.org/) — environnement d'exécution JavaScript côté serveur
- [Express](https://expressjs.com/) — framework pour créer l'API (le service qui répond aux demandes de l'application)
- [MongoDB](https://www.mongodb.com/) + [Mongoose](https://mongoosejs.com/) — base de données qui stocke les utilisateurs, checklists, documents, messages, etc.
- [JWT (JSON Web Token)](https://jwt.io/) — pour sécuriser la connexion des utilisateurs
- Un service d'envoi de courriels — pour la vérification de compte et la réinitialisation de mot de passe

**Frontend (application mobile)**
- [Kotlin](https://kotlinlang.org/) — langage de programmation natif Android
- [Jetpack Compose](https://developer.android.com/jetpack/compose) — pour construire l'interface visuelle
- [Retrofit](https://square.github.io/retrofit/) — pour que l'application communique avec le serveur (l'API)

---

## 🚀 Installation et démarrage (partie technique)

Cette section s'adresse aux personnes qui souhaitent faire fonctionner le projet sur leur propre ordinateur (par exemple pour contribuer au code ou faire une démonstration).

### Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- **[Node.js](https://nodejs.org/)** (avec npm) pour exécuter le serveur
- **[MongoDB](https://www.mongodb.com/try/download/community)** (en local ou via un service cloud comme [MongoDB Atlas](https://www.mongodb.com/atlas))
- **[Android Studio](https://developer.android.com/studio)** pour compiler et lancer l'application mobile

---

### 1️⃣ Démarrer le serveur (backend)

1. Ouvrez un terminal et déplacez-vous dans le dossier `backend` :
   ```bash
   cd backend
   ```

2. Installez les dépendances du projet :
   ```bash
   npm install
   ```

3. Créez un fichier `.env` à la racine du dossier `backend`, contenant les variables suivantes :
   ```env
   PORT=3000
   DB_CONNECTION_ID=votre_chaine_de_connexion_mongodb
   JWT_SECRET=une_cle_secrete_de_votre_choix
   ```
   Remplacez `votre_chaine_de_connexion_mongodb` par l'adresse de votre base de données MongoDB (locale ou en ligne).

4. Lancez le serveur :
   ```bash
   node src/server.js
   ```

   Le serveur démarre alors sur `http://localhost:3000/` (ou sur le port indiqué dans votre `.env`).

---

### 2️⃣ Démarrer l'application mobile (frontend)

1. Ouvrez **Android Studio**, puis ouvrez le dossier `frontend`.

2. Vérifiez que l'adresse du serveur (`BASE_URL`) est bien configurée dans le fichier `app/build.gradle.kts`.
   - Si vous testez sur un **émulateur Android** avec le serveur backend lancé en local sur le port 3000, utilisez :
     ```
     http://10.0.2.2:3000/
     ```
     (`10.0.2.2` est l'adresse spéciale que l'émulateur utilise pour désigner l'ordinateur qui l'héberge — l'équivalent de "localhost" côté émulateur.)

3. Laissez Android Studio synchroniser les fichiers du projet (cela peut prendre quelques minutes la première fois).

4. Sélectionnez un émulateur ou branchez un téléphone Android, puis cliquez sur le bouton **▶️ Run** pour lancer l'application.

---

### ℹ️ Information utile

Lors de la création d'un compte, aucun courriel réel n'est nécessairement envoyé en environnement de développement : le **lien de vérification s'affiche directement dans la console du serveur**. Il suffit de copier ce lien et de l'ouvrir dans un navigateur pour valider le compte avant de pouvoir se connecter.

---

## 📂 Structure du projet

```
Vistudia/
├── backend/                 # Serveur (API, base de données, logique métier)
│   └── src/
│       ├── Controllers/     # Logique de chaque fonctionnalité (utilisateurs, checklist, forum, etc.)
│       ├── Models/          # Structure des données stockées (utilisateur, document, checklist, etc.)
│       ├── Routers/         # Les "chemins" (URLs) que l'application peut appeler
│       ├── Services/        # Services annexes (ex : envoi de courriels)
│       └── views/           # Pages web utilisées pour le partage de checklist et la réinitialisation de mot de passe
│
└── frontend/                # Application mobile Android (Kotlin)
    └── app/.../ui/screens/  # Les différents écrans de l'application (connexion, accueil, checklist, forum, profil, etc.)
```

---

## 🎓 Contexte du projet

Vistudia est un projet développé dans un cadre universitaire (UQAC), visant à répondre à un besoin concret vécu par de nombreux étudiants internationaux : simplifier et centraliser les démarches d'immigration liées aux études à l'étranger.

---

## 📄 Licence

Aucune licence n'a été spécifiée pour ce projet à ce jour.
