package pinoteaux.projetrioc.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import pinoteaux.projetrioc.Main;

import java.io.IOException;

public class ControllerMenu {


    @FXML
    private Button soloButton;
    @FXML
    private Button tournoisButton;
    @FXML
    private Button classementButton;
    private Main mainApp;

    public void actionSoloButton(ActionEvent e) {
        if (mainApp != null) {
            mainApp.startSimonGame((Stage) ((Node) e.getSource()).getScene().getWindow(), null, "SOLO");
        }
    }
    public void actionTournoisButton(ActionEvent e) {
        if (mainApp != null) {
            mainApp.startChoixServer((Stage) ((Node) e.getSource()).getScene().getWindow());
        }

    }
    public void actionClassementButton(ActionEvent e) {
        if (mainApp != null) {
            mainApp.startClassement((Stage) ((Node) e.getSource()).getScene().getWindow());
        }

    }
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

}