# Parking Management System

## Présentation du projet
Ce projet est une application de gestion de parking développée dans le cadre d'un délai très court (4 sprints d'une demi-journée). 
Afin d'optimiser le temps de développement et de permettre un travail en parallèle, l'architecture a été strictement découpée entre le frontend et le backend.

## Architecture Technique (ADR)
Les décisions architecturales sont documentées via des **ADR (Architecture Decision Records)** disponibles dans le dossier `docs/adrs/`. Il est **fortement recommandé** de les lire pour comprendre les choix techniques du projet.

Voici les points clés de l'architecture retenue :
* **Découplage Front/Back (ADR 15)** : Séparation stricte pour faciliter le développement en parallèle.
* **Backend : Monolithe Modulaire (ADR 03 & 15)** : 
  * API REST développée en **Java avec Spring Boot**.
  * Le code est découpé par **domaines métiers** de manière étanche (Architecture Hexagonale). Les packages cibles sont : `users`, `reservations`, `parking-lots`, et `notifications`.
* **Frontend (ADR 16)** : 
  * Application Single Page (SPA) développée en **Angular**.
  * Accessible sur mobile pour faciliter le scan de QR codes.
* **Base de données** : PostgreSQL.

## Comment lancer le projet

Le projet est entièrement conteneurisé. Vous n'avez besoin que de **Docker** et **Docker Compose** d'installés sur votre machine (ainsi que `bash` pour lancer les scripts).

### Démarrage avec les scripts fournis

Dans le dossier `scripts/`, vous trouverez plusieurs utilitaires :

1. **Lancer le projet** (construit et démarre les conteneurs) :
   ```bash
   ./scripts/run.sh
   # ou manuellement : docker compose up --build
   ```

2. **Arrêter le projet** :
   ```bash
   ./scripts/stop.sh
   # ou manuellement : docker compose down
   ```

3. **Reconstruire les images** :
   ```bash
   ./scripts/build.sh
   ```

4. **Redémarrer le projet** :
   ```bash
   ./scripts/restart.sh
   ```

### Services exposés en local

Une fois le projet lancé avec Docker Compose, les services sont accessibles sur les ports suivants :

* **Frontend Angular** : [http://localhost:4200](http://localhost:4200)
* **Backend API (Spring Boot)** : [http://localhost:8080](http://localhost:8080)
* **Base de données PostgreSQL** : `localhost:5433` (credentials dans le `docker-compose.yml` : DB=`parking_management`, User=`parking_user`, Password=`parking_pass`)

N'hésitez pas à consulter les ADR dans `docs/adrs` pour plus de consignes structurantes !
