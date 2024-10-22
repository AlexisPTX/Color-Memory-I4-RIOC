package pinoteaux.projetrioc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu.fxml"));
        Parent root = fxmlLoader.load();


        Controller controller = fxmlLoader.getController();
        controller.setMainApp(this);

        Scene scene = new Scene(root, 1000, 800);
        stage.setTitle("Menu");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    // Méthode pour lancer le jeu Simon
    public void startSimonGame(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("simon.fxml"));
        Parent root = fxmlLoader.load();

        // Obtenez le contrôleur de Simon et configurez-le
        Controller controller = fxmlLoader.getController();
        Chrono chrono = new Chrono(1,controller);
        Simon simon = new Simon(controller,chrono);
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

    public static void main(String[] args) {
        launch();
    }
}
