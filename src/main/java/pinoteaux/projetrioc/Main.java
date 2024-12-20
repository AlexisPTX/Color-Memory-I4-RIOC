package pinoteaux.projetrioc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pinoteaux.projetrioc.gamepart.Chrono;
import pinoteaux.projetrioc.gamepart.ControllerColorMemory;
import pinoteaux.projetrioc.gamepart.ColorMemory;
import pinoteaux.projetrioc.menu.*;

import java.io.IOException;
import java.net.Socket;

import static javafx.application.Platform.exit;

/**
 * La classe Main est l'application principale qui gère l'interface graphique et les interactions avec les utilisateurs.
 * Elle est responsable du démarrage des différents écrans de l'application, tels que la connexion, le menu principal,
 * le jeu, et le chat.
 */
public class Main extends Application {

    private String username;
    private Parent chatRoot = null;
    private ChatHandler chatHandler;
    private Stage actualStage = null;
    private Socket socketServ;

    /**
     * Méthode principale qui démarre l'application JavaFX.
     * Elle initialise la scène de connexion et la montre à l'utilisateur.
     *
     * @param stage Le stage principal de l'application.
     */
    @Override
    public void start(Stage stage) {
        actualStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu/connexion.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error in Main loading connexion.fxml: " + e.getMessage());
        }

        ControllerConnexion controller = fxmlLoader.getController();
        controller.setMainApp(this);

        Scene scene = new Scene(root, 1400, 800);
        actualStage.setTitle("Choix pseudo");
        actualStage.setResizable(false);
        actualStage.setScene(scene);

        actualStage.setOnCloseRequest(event -> {
            if (this.chatHandler != null) {
                this.chatHandler.stop();
            }
            exit();
        });

        actualStage.show();
    }

    /**
     * Démarre l'écran principal de l'application après qu'un nom d'utilisateur a été saisi.
     * Affiche le menu principal avec le chat.
     *
     * @param username Le nom d'utilisateur choisi par l'utilisateur.
     */
    public void startMain(String username) {
        this.username = username;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu/menu.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error in Main loading menu.fxml: " + e.getMessage());
        }

        ControllerMenu controllerMenu = fxmlLoader.getController();
        controllerMenu.setMainApp(this);

        // Créez le layout avec le chat
        Parent chatLayout = createChatLayout(root, "GLOBAL");

        Scene scene = new Scene(chatLayout, 1400, 800);
        actualStage.setTitle("Menu");
        actualStage.setResizable(false);
        actualStage.setScene(scene);
        actualStage.show();
    }

    /**
     * Lance le jeu en initialisant les composants nécessaires et en démarrant le jeu.
     *
     * @param firstInt Si ce paramètre est 0, une partie hors ligne commence ; sinon, il représente
     *                 la première couleur pour un jeu en réseau.
     */
    public void startColorMemoryGame(int firstInt) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gamepart/colorMemory.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error in Main loading colorMemory.fxml : " + e.getMessage());
        }

        // Obtenez le contrôleur de Color Memory et configurez-le
        ControllerColorMemory controllerJeu = fxmlLoader.getController();
        Chrono chrono = new Chrono(1,controllerJeu);
        ColorMemory colorMemory;
        if(firstInt == 0) {
            colorMemory = new ColorMemory(controllerJeu, chrono, this.username);
        }else{
            colorMemory = new ColorMemory(controllerJeu, socketServ, chrono, firstInt, this.username);
        }
        controllerJeu.setColorMemory(colorMemory);
        chrono.setColorMemory(colorMemory);

        // Créez la scène pour le jeu
        Scene scene = new Scene(root, 800, 600);
        actualStage.setTitle("Color Memory");
        actualStage.setResizable(false);
        actualStage.setScene(scene);
        actualStage.show();

        // Démarrez le jeu
        colorMemory.startGame();
    }

    /**
     * Lance l'écran de sélection du serveur.
     */
    public void startChoixServer() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu/choixServer.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error in Main loading choixServer.fxml: " + e.getMessage());
        }

        ControllerChoixServer controllerServer = fxmlLoader.getController();
        controllerServer.setMainApp(this);

        Parent chatLayout = createChatLayout(root,"");

        Scene scene = new Scene(chatLayout, 1400, 800);
        actualStage.setTitle("Choix Server");
        actualStage.setResizable(false);
        actualStage.setScene(scene);
        actualStage.show();
    }

    /**
     * Gère la réception d'un nouveau socket pour un serveur, et met à jour l'interface dans l'attente du début du tournoi.
     *
     * @param socketServ Le socket du serveur auquel se connecter.
     */
    public void attenteDebutTournoi(Socket socketServ) {
        try {
            this.socketServ.close();
        } catch (IOException e) {
            System.out.println("[Main] - Failed to close socket");
        }
        this.socketServ = socketServ;
        this.chatHandler.setSocket(socketServ);

        // Charge le nouveau chat layout
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("menu/chat.fxml"));
        Parent newChatRoot = null;
        try {
            newChatRoot = chatLoader.load();
            ControllerChat controllerChat = chatLoader.getController();
            startChatHandler(this.socketServ, controllerChat);
            controllerChat.setChatHandler(this.chatHandler, this.username);
            this.chatHandler.setMainApp(this);
        } catch (IOException e) {
            return;
        }

        // Récupère le layout principal actuel
        Parent root = actualStage.getScene().getRoot();
        if (root instanceof BorderPane layout) {
            layout.setLeft(newChatRoot); // Remplace l'ancien chat par le nouveau
        } else {
            System.out.println("Root is not a BorderPane. Cannot update layout.");
        }

        // Ajuste la scène si nécessaire
        actualStage.setTitle("Attente Début Tournoi");
        actualStage.setResizable(false);
        actualStage.show();
    }

    /**
     * Crée un layout avec un chat intégré à un contenu principal donné.
     *
     * @param mainContent Le contenu principal de la scène (par exemple, le menu ou le jeu).
     * @param affichage   Si "GLOBAL", initialiser le chat avec un socket de serveur global.
     *                    Si "SERVER", utiliser un socket serveur déjà établi.
     * @return Un `Parent` contenant le chat et le contenu principal.
     */
    private Parent createChatLayout(Parent mainContent, String affichage) {
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("menu/chat.fxml"));
        if(affichage.equals("GLOBAL")) {
            try {
                this.chatRoot = chatLoader.load();

                // Obtenez le contrôleur du chat
                ControllerChat controllerChat = chatLoader.getController();

                // Crée le socket et démarre le ChatHandler
                this.socketServ = new Socket(Constantes.SERVER_ADDRESS, Constantes.SERVER_GLOBAL_PORT); // Adresse/port du serveur
                startChatHandler(this.socketServ, controllerChat);

                // Passe le ChatHandler au contrôleur pour envoyer des messages
                controllerChat.setChatHandler(this.chatHandler, this.username);

            } catch (IOException ignored) {
            }
        }else if(affichage.equals("SERVER")){
            try {
                this.chatRoot = chatLoader.load();
                ControllerChat controllerChat = chatLoader.getController();

                chatHandler.setSocket(this.socketServ);
                controllerChat.setChatHandler(this.chatHandler, this.username);

            } catch (IOException ignored) {
            }
        }

        BorderPane layout = new BorderPane();
        layout.setLeft(this.chatRoot);
        layout.setCenter(mainContent);

        return layout;
    }

    /**
     * Initialise et démarre un ChatHandler dans un thread séparé.
     *
     * @param socket        Le socket pour la connexion de chat.
     * @param controllerChat Le contrôleur de chat pour gérer les interactions avec l'utilisateur.
     */
    private void startChatHandler(Socket socket, ControllerChat controllerChat) {
        this.chatHandler = new ChatHandler(socket);
        this.chatHandler.setControllerChat(controllerChat);

        // Démarre le ChatHandler dans un thread séparé
        new Thread(this.chatHandler).start();
    }

    /**
     * Méthode principale pour démarrer l'application JavaFX.
     *
     * @param args Les arguments de ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        launch();
    }
}
