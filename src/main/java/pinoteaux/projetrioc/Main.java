package pinoteaux.projetrioc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pinoteaux.projetrioc.gamepart.Chrono;
import pinoteaux.projetrioc.gamepart.ControllerSimon;
import pinoteaux.projetrioc.gamepart.Simon;
import pinoteaux.projetrioc.menu.*;

import java.io.IOException;
import java.net.Socket;

import static javafx.application.Platform.exit;

public class Main extends Application {

    private String username;
    private Parent chatRoot = null;
    private ChatHandler chatHandler;
    private Stage actualStage = null;
    private Socket socketServ;

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


    // Méthode pour lancer le jeu Simon
    public void startSimonGame(int firstInt) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gamepart/simon.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error in Main loading simon.fxml : " + e.getMessage());
        }

        // Obtenez le contrôleur de Simon et configurez-le
        ControllerSimon controllerJeu = fxmlLoader.getController();
        Chrono chrono = new Chrono(1,controllerJeu);
        Simon simon;
        if(firstInt == 0) {
            simon = new Simon(controllerJeu, socketServ, chrono, this.username);
        }else{
            simon = new Simon(controllerJeu, socketServ, chrono, firstInt, this.username);
        }
        controllerJeu.setSimon(simon);
        chrono.setSimon(simon);

        // Créez la scène pour Simon
        Scene scene = new Scene(root, 800, 600);
        actualStage.setTitle("Color Memory");
        actualStage.setResizable(false);
        actualStage.setScene(scene);
        actualStage.show();

        // Démarrez le jeu Simon
        simon.startGame();
    }
    public void startClassement() {
    }
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
            System.out.println("Error loading chat.fxml: " + e.getMessage());
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


    private Parent createChatLayout(Parent mainContent, String affichage) {
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("menu/chat.fxml"));
        if(affichage.equals("GLOBAL")) {
            try {
                this.chatRoot = chatLoader.load();

                // Obtenez le contrôleur du chat
                ControllerChat controllerChat = chatLoader.getController();

                // Crée le socket et démarre le ChatHandler
                this.socketServ = new Socket("localhost", 9999); // Adresse/port du serveur
                startChatHandler(this.socketServ, controllerChat);

                // Passe le ChatHandler au contrôleur pour envoyer des messages
                controllerChat.setChatHandler(this.chatHandler, this.username);

            } catch (IOException e) {
                System.out.println("Error loading chat.fxml: " + e.getMessage());
            }
        }else if(affichage.equals("SERVER")){
            try {
                this.chatRoot = chatLoader.load();
                ControllerChat controllerChat = chatLoader.getController();

                chatHandler.setSocket(this.socketServ);
                controllerChat.setChatHandler(this.chatHandler, this.username);

            } catch (IOException e) {
                System.out.println("Error loading chat.fxml: " + e.getMessage());
            }
        }

        BorderPane layout = new BorderPane();
        layout.setLeft(this.chatRoot);
        layout.setCenter(mainContent);

        return layout;
    }


    private void startChatHandler(Socket socket, ControllerChat controllerChat) {
        this.chatHandler = new ChatHandler(socket);
        this.chatHandler.setControllerChat(controllerChat);

        // Démarre le ChatHandler dans un thread séparé
        new Thread(this.chatHandler).start();
    }

    public Stage getStage(){
        return this.actualStage;
    }




    public static void main(String[] args) {
        launch();
    }
}