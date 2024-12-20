# Documentation du Projet Color Memory

## Introduction
Ce programme est une application JavaFX qui permet de jouer au jeu de mémoire de couleur tout en offrant une fonctionnalité de chat entre joueurs. Le projet est organisé en plusieurs modules : connexion, menu principal, jeu et gestion du chat. Il inclut une logique de communication entre clients via un serveur socket.

---

## Fonctionnalités principales

### 1. Connexion
L'utilisateur démarre par un écran de connexion où il choisit son pseudonyme.

- **FXML utilisé** : `connexion.fxml`
- **Contrôleur** : `ControllerConnexion`
- **Action principale** : Enregistrer le pseudonyme et lancer le menu principal.

### 2. Menu principal
Une fois connecté, l'utilisateur accède à un menu principal qui propose plusieurs options :
- Jouer au jeu (en mode solo ou en mode tournois).
- Voir le classement (fonctionnalité à implémenter).
- Choisir un serveur pour participer à un tournoi.

- **FXML utilisé** : `menu.fxml`
- **Contrôleur** : `ControllerMenu`

### 3. Jeu Color Memory
Le jeu teste la mémoire de l'utilisateur en lui demandant de répéter une séquence de couleurs dans le bon ordre. Le mode de jeu peut varier selon que l'utilisateur joue en solo ou en tournoi.

- **FXML utilisé** : `colorMemory.fxml`
- **Contrôleur** : `ControllerSimon`
- **Classes principales** :
    - `Simon` : Contient la logique du jeu.
    - `Chrono` : Gestion du chronomètre.

#### Fonctionnalités du jeu
- Mode solo : La séquence de couleurs est générée localement.
- Mode tournoi : La séquence est synchronisée avec les autres joueurs via le serveur.

### 4. Chat
Le programme inclut un système de chat qui permet aux joueurs de communiquer entre eux.

- **FXML utilisé** : `chat.fxml`
- **Contrôleur** : `ControllerChat`
- **Classe associée** : `ChatHandler`
- **Sockets** : Les communications passent par un serveur socket.

---

## Organisation du projet

### Structure des packages
- **`pinoteaux.projetrioc`** : Point d’entrée du programme.
- **`pinoteaux.projetrioc.menu`** : Gère la connexion, le menu principal et la sélection du serveur.
- **`pinoteaux.projetrioc.gamepart`** : Contient la logique du jeu Simon et ses contrôleurs associés.

### Ressources FXML
- `connexion.fxml`
- `menu.fxml`
- `choixServer.fxml`
- `colorMemory.fxml`
- `chat.fxml`

### Fichiers Java
- **Main.java** : Point d’entrée principal.
- **Controller classes** : Contrôleurs pour chaque vue FXML.
- **ChatHandler.java** : Gestionnaire pour la logique de communication du chat.
- **Simon.java** et **Chrono.java** : Logique du jeu Simon et gestion du chronomètre.

---

## Ressources nécessaires

### Environnement de développement
- **Java JDK 17 ou version ultérieure**
- **JavaFX SDK** (configuré dans le chemin de compilation)
- **Un serveur socket fonctionnel** (adresse et ports configurables dans `Constantes.java`)
- **IDE** comme IntelliJ IDEA ou Eclipse pour une meilleure gestion du projet.

### Bibliothèques externes
- Aucune bibliothèque externe n’est nécessaire.

---

## Instructions pour l’exécution

1. **Configurer le serveur socket** : Assurez-vous que le serveur défini par `Constantes.SERVER_ADDRESS` et `Constantes.SERVER_GLOBAL_PORT` est actif.
2. **Compiler et exécuter** :
    - Via un IDE : Importez le projet et exécutez la classe `Main` pour démarrer un client et la classe `Server` pour le serveur.
    - Via la ligne de commande : Utilisez le Makefile ci-dessous.

---

## Makefile

Voici un Makefile pour automatiser la compilation et l’exécution :

```makefile
# Variables
SRC_DIR = src
BIN_DIR = bin
MAIN_CLASS = pinoteaux.projetrioc.Main
SERVER_CLASS = pinoteaux.projetrioc.server.Server
JAVAC = javac
JAVA = java
FLAGS = -d $(BIN_DIR) -classpath $(BIN_DIR):$(SRC_DIR)

# JavaFX Configuration
# Remplacez le chemin ci-dessous par celui de votre SDK JavaFX
JAVAFX_PATH = /path/to/javafx-sdk-XX/lib
JAVAFX_FLAGS = --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml

# Compilation
all: compile

compile:
	@echo "Compilation des fichiers Java avec JavaFX..."
	mkdir -p $(BIN_DIR)
	$(JAVAC) $(FLAGS) --module-path $(JAVAFX_PATH) --add-modules javafx.controls,javafx.fxml $(SRC_DIR)/pinoteaux/projetrioc/**/*.java
	@echo "Compilation terminée."

# Lancement du serveur
run-server:
	@echo "Lancement du serveur..."
	$(JAVA) -classpath $(BIN_DIR) $(SERVER_CLASS)

# Lancement d'un client
run-client:
	@echo "Lancement d'un client avec JavaFX..."
	$(JAVA) -classpath $(BIN_DIR) $(JAVAFX_FLAGS) $(MAIN_CLASS)

# Lancement de plusieurs clients
run-multi-client:
	@echo "Lancement de plusieurs clients..."
	for i in 1 2 3; do \
		$(JAVA) -classpath $(BIN_DIR) $(JAVAFX_FLAGS) $(MAIN_CLASS) & \
	done

# Nettoyage
clean:
	@echo "Nettoyage des fichiers compilés..."
	rm -rf $(BIN_DIR)/*
	@echo "Nettoyage terminé."

# Aide
help:
	@echo "Commandes disponibles :"
	@echo "  make all           : Compile le projet avec JavaFX."
	@echo "  make run-server    : Lance le serveur."
	@echo "  make run-client    : Lance un client avec JavaFX."
	@echo "  make run-multi-client : Lance plusieurs clients avec JavaFX (par défaut 3)."
	@echo "  make clean         : Supprime les fichiers compilés."
```

---

## Notes complémentaires
- Les fichiers FXML doivent être placés dans le répertoire `resources` pour que `FXMLLoader` puisse les charger correctement.
- Si des erreurs surviennent, vérifiez que le chemin du module JavaFX est correctement configuré dans le Makefile ou l'IDE.

---

## Fonctionnalités à implémenter
- Ajout de la fonctionnalité "Classement" dans le menu principal.
- Gestion améliorée des erreurs côté serveur.
- Tests unitaires pour les classes principales.