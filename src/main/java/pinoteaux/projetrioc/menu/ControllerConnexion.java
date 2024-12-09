package pinoteaux.projetrioc.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pinoteaux.projetrioc.Main;

public class ControllerConnexion {

    private Main mainApp;
    @FXML
    private TextField usernameField;
    @FXML
    private Button usernameButton;
    @FXML
    private Text message;

    public void actionPseudoButton(ActionEvent e) {
        if (mainApp != null) {
            String user = usernameField.getText();
            if(!user.isEmpty()) {
                mainApp.startMain((Stage) ((Node) e.getSource()).getScene().getWindow(), user);
            }else{
                message.setVisible(true);
            }
        }
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        this.message.setVisible(false);
    }
}
