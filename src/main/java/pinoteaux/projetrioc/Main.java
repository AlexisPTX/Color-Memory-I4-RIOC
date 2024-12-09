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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static javafx.application.Platform.exit;

public class Main extends Application {

    private String username;
    private Parent chatRoot = null;
    private ChatHandler chatHandler;

    @Override
    public void start(Stage stage) {
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
        stage.setTitle("Choix pseudo");
        stage.setResizable(false);
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            if (this.chatHandler != null) {
                this.chatHandler.stop();
            }
            exit();
        });

        stage.show();
    }


    public void startMain(Stage stage, String username) {
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
        Parent chatLayout = createChatLayout(root);

        Scene scene = new Scene(chatLayout, 1400, 800);
        stage.setTitle("Menu");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }


    // Méthode pour lancer le jeu Simon
    public void startSimonGame(Stage stage,Socket socketServ, int firstInt) {
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
        stage.setTitle("Color Memory");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        // Démarrez le jeu Simon
        simon.startGame();
    }
    public void startClassement(Stage stage) {
    }
    public void startChoixServer(Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu/choixServer.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error in Main loading choixServer.fxml: " + e.getMessage());
        }

        ControllerChoixServer controllerServer = fxmlLoader.getController();
        controllerServer.setMainApp(this);

        Parent chatLayout = createChatLayout(root);

        Scene scene = new Scene(chatLayout, 1400, 800);
        stage.setTitle("Choix Server");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void attenteDebutTournoi(Stage stage, Socket socketServ) {
            BufferedReader bf = null;
            try {
                bf = new BufferedReader(new InputStreamReader(socketServ.getInputStream()));
                String line;
                while ((line = bf.readLine()) != null) {
                    if (line.equals("START")) {
                        if ((line = bf.readLine()) != null) {
                            startSimonGame(stage, socketServ, Integer.parseInt(line));
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error in Main attenteDebutTournoi : " + e.getMessage());
            }
    }

    private Parent createChatLayout(Parent mainContent) {
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("menu/chat.fxml"));
        if(this.chatRoot == null) {
            try {
                this.chatRoot = chatLoader.load();

                // Obtenez le contrôleur du chat
                ControllerChat controllerChat = chatLoader.getController();

                // Crée le socket et démarre le ChatHandler
                Socket socket = new Socket("localhost", 9999); // Adresse/port du serveur
                startChatHandler(socket, controllerChat);

                // Passe le ChatHandler au contrôleur pour envoyer des messages
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




    public static void main(String[] args) {
        launch();
    }
}