package pinoteaux.projetrioc.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import pinoteaux.projetrioc.Main;

/**
 * La classe ControllerConnexion gère l'interface utilisateur pour la saisie du pseudonyme
 * et la transition vers l'écran principal de l'application après validation.
 */
public class ControllerConnexion {

    /**
     * Référence à l'application principale permettant de naviguer entre les vues et gérer les fonctionnalités globales.
     */
    private Main mainApp;

    /**
     * Champ de texte pour la saisie du pseudonyme de l'utilisateur.
     */
    @FXML
    private TextField usernameField;

    /**
     * Bouton pour valider le pseudonyme saisi.
     */
    @FXML
    private Button usernameButton;

    /**
     * Message d'erreur affiché si aucun pseudonyme n'est saisi.
     */
    @FXML
    private Text message;

    /**
     * Gère l'action du bouton de validation du pseudonyme.
     * Vérifie que le champ de saisie n'est pas vide et transmet le pseudonyme à l'application principale.
     * Si le champ est vide, affiche un message d'erreur.
     *
     * @param e L'événement déclenché par l'utilisateur.
     */
    public void actionPseudoButton(ActionEvent e) {
        if (mainApp != null) {
            String user = usernameField.getText();
            if (!user.isEmpty()) {
                mainApp.startMain(user); // Démarre l'écran principal avec le pseudonyme saisi
            } else {
                message.setVisible(true); // Affiche un message d'erreur si le champ est vide
            }
        }
    }

    /**
     * Associe une instance de l'application principale à ce contrôleur
     * et masque le message d'erreur au démarrage.
     *
     * @param mainApp Instance de l'application principale.
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        this.message.setVisible(false);
    }
}