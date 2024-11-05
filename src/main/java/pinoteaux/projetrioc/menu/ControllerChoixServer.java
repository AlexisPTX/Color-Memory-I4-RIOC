package pinoteaux.projetrioc.menu;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.Socket;

public class ControllerChoixServer {

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

    public void actionButtonServer1(ActionEvent e) throws IOException {
        Socket socket = new Socket("localhost", 1111);
    }
    public void actionButtonServer2(ActionEvent e) throws IOException {
        Socket socket = new Socket("localhost", 2222);
    }
    public void actionButtonServer3(ActionEvent e) throws IOException {
        Socket socket = new Socket("localhost", 3333);
    }
    public void actionButtonServer4(ActionEvent e) throws IOException {
        Socket socket = new Socket("localhost", 4444);
    }
    public void actionButtonServer5(ActionEvent e) throws IOException {
        Socket socket = new Socket("localhost", 5555);
    }

}