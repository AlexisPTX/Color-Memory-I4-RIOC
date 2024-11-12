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

    public void actionButtonServer1(ActionEvent e) throws IOException {
        Socket socket = new Socket("localhost", 1111);
        //hideGroup();
        //mainApp.attenteDebutTournoi((Stage) ((Node) e.getSource()).getScene().getWindow(), socket);
    }
    public void actionButtonServer2(ActionEvent e) throws IOException {
        Socket socket = new Socket("localhost", 2222);
        hideGroup();
        mainApp.attenteDebutTournoi((Stage) ((Node) e.getSource()).getScene().getWindow(), socket);
    }
    public void actionButtonServer3(ActionEvent e) throws IOException {
        Socket socket = new Socket("localhost", 3333);
        hideGroup();
        mainApp.attenteDebutTournoi((Stage) ((Node) e.getSource()).getScene().getWindow(), socket);
    }
    public void actionButtonServer4(ActionEvent e) throws IOException {
        Socket socket = new Socket("localhost", 4444);
        hideGroup();
        mainApp.attenteDebutTournoi((Stage) ((Node) e.getSource()).getScene().getWindow(), socket);
    }
    public void actionButtonServer5(ActionEvent e) throws IOException {
        Socket socket = new Socket("localhost", 5555);
        hideGroup();
        mainApp.attenteDebutTournoi((Stage) ((Node) e.getSource()).getScene().getWindow(), socket);
    }

}