package pinoteaux.projetrioc.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import pinoteaux.projetrioc.Main;

import java.io.IOException;
import java.net.Socket;

public class ControllerChoixServer {
    private Main mainApp;

    @FXML
    private Button buttonServer1;
    @FXML
    private Button buttonServer2;
    @FXML
    private Button buttonServer3;
    @FXML
    private Button buttonServer4;
    @FXML
    private Button buttonServer5;
    @FXML
    private Group buttonGroup;

    public void hideGroup() {
        buttonGroup.setVisible(false);
    }

    private void connectToServer(ActionEvent e, int port) {
        Socket socket = null;
        try {
            System.out.println("Attempting to connect to server on port: " + port);
            socket = new Socket("localhost", port);
            System.out.println("Connected to server on port: " + port);
        } catch(IOException ex){
            System.out.println("Error in creating socket: " + ex.getMessage());
            return; // Exit if socket creation failed
        }
        hideGroup();
        mainApp.attenteDebutTournoi((Stage) ((Node) e.getSource()).getScene().getWindow(), socket);
    }

    public void actionButtonServer1(ActionEvent e) {
        connectToServer(e, 1111);
    }

    public void actionButtonServer2(ActionEvent e) {
        connectToServer(e, 2222);
    }

    public void actionButtonServer3(ActionEvent e) {
        connectToServer(e, 3333);
    }

    public void actionButtonServer4(ActionEvent e) {
        connectToServer(e, 4444);
    }

    public void actionButtonServer5(ActionEvent e) {
        connectToServer(e, 5555);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
}
