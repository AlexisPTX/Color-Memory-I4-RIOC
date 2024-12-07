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
import pinoteaux.projetrioc.menu.ChatHandler;
import pinoteaux.projetrioc.menu.ControllerChat;
import pinoteaux.projetrioc.menu.ControllerChoixServer;
import pinoteaux.projetrioc.menu.ControllerMenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu/menu.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error in Main loading menu.fxml: " + e.getMessage());
        }

        ControllerMenu controller = fxmlLoader.getController();
        controller.setMainApp(this);

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
            simon = new Simon(controllerJeu, socketServ, chrono);
        }else{
            simon = new Simon(controllerJeu, socketServ, chrono, firstInt);
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
        Parent chatRoot = null;
        try {
            chatRoot = chatLoader.load();

            // Obtenez le contrôleur du chat
            ControllerChat controllerChat = chatLoader.getController();

            // Crée le socket et démarre le ChatHandler
            Socket socket = new Socket("localhost", 9999); // Adresse/port du serveur
            ChatHandler chatHandler = startChatHandler(socket, controllerChat);

            // Passe le ChatHandler au contrôleur pour envoyer des messages
            controllerChat.setChatHandler(chatHandler);

        } catch (IOException e) {
            System.out.println("Error loading chat.fxml: " + e.getMessage());
        }

        BorderPane layout = new BorderPane();
        layout.setLeft(chatRoot);
        layout.setCenter(mainContent);

        return layout;
    }


    private ChatHandler startChatHandler(Socket socket, ControllerChat controllerChat) {
        ChatHandler chatHandler = null;
        chatHandler = new ChatHandler(socket);

        // Injecte le contrôleur dans le ChatHandler
        chatHandler.setControllerChat(controllerChat);

        // Démarre le ChatHandler dans un thread séparé
        new Thread(chatHandler).start();
        return chatHandler;
    }




    public static void main(String[] args) {
        launch();
    }
}