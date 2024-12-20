package pinoteaux.projetrioc.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import pinoteaux.projetrioc.Constantes;
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
            socket = new Socket(Constantes.SERVER_ADDRESS, port);
        } catch(IOException ex){
            System.out.println("Error in creating socket: " + ex.getMessage());
            return; // Exit if socket creation failed
        }
        hideGroup();
        mainApp.attenteDebutTournoi(socket);
    }

    public void actionButtonServer1(ActionEvent e) {
        connectToServer(e, Constantes.SERVER_1_PORT);
    }

    public void actionButtonServer2(ActionEvent e) {
        connectToServer(e, Constantes.SERVER_2_PORT);
    }

    public void actionButtonServer3(ActionEvent e) {
        connectToServer(e, Constantes.SERVER_3_PORT);
    }

    public void actionButtonServer4(ActionEvent e) {
        connectToServer(e, Constantes.SERVER_4_PORT);
    }

    public void actionButtonServer5(ActionEvent e) {
        connectToServer(e, Constantes.SERVER_5_PORT);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
}
