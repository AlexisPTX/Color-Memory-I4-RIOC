package pinoteaux.projetrioc.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
            mainApp.startSimonGame(0);
        }
    }
    public void actionTournoisButton(ActionEvent e) {
        if (mainApp != null) {
            mainApp.startChoixServer();
        }
    }
    public void actionClassementButton(ActionEvent e) {
        if (mainApp != null) {
            mainApp.startClassement();
        }

    }
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

}