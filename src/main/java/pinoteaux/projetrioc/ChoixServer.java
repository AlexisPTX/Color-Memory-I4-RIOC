package pinoteaux.projetrioc;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChoixServer {
    public static void main(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChoixServer.class.getResource("choixServer.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("Choix Serveur");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
