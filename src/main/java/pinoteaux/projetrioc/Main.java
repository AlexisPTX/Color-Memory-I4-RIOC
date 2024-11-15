package pinoteaux.projetrioc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pinoteaux.projetrioc.gamepart.Chrono;
import pinoteaux.projetrioc.gamepart.ControllerSimon;
import pinoteaux.projetrioc.gamepart.Simon;
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
            System.out.println("Error in Main loading menu.fxml : " + e.getMessage());
        }

        ControllerMenu controller = fxmlLoader.getController();
        controller.setMainApp(this);

        Scene scene = new Scene(root, 1000, 800);
        stage.setTitle("Menu");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    // Méthode pour lancer le jeu Simon
    public void startSimonGame(Stage stage,Socket socketServ, String mode) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gamepart/simon.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error in Main loading simon.fxml : " + e.getMessage());
        }

        // Obtenez le contrôleur de Simon et configurez-le
        ControllerSimon controller = fxmlLoader.getController();
        Chrono chrono = new Chrono(1,controller);
        Simon simon = new Simon(controller,null,chrono,mode);
        controller.setSimon(simon);
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
            System.out.println("Error in Main loading choixServer.fxml : " + e.getMessage());
        }
        ControllerChoixServer controller = fxmlLoader.getController();
        controller.setMainApp(this);

        Scene scene = new Scene(root, 1000, 800);
        stage.setTitle("Color Memory");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
    public void attenteDebutTournoi(Stage stage, Socket socketServ, String mode, int port) {
            BufferedReader bf = null;
            try {
                bf = new BufferedReader(new InputStreamReader(socketServ.getInputStream()));
                String line;
                while ((line = bf.readLine()) != null) {
                    if (line.equals("START")) {
                        startSimonGame(stage, socketServ, mode);
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error in Main attenteDebutTournoi : " + e.getMessage());
            } finally {
                if (bf != null) {
                    try {
                        bf.close();
                    } catch (IOException e) {
                        System.err.println("Error closing BufferedReader: " + e.getMessage());
                    }
                }
            }
    }


    public static void main(String[] args) {
        launch();
    }
}