package pinoteaux.projetrioc.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pinoteaux.projetrioc.Main;

/**
 * La classe ControllerMenu gère les interactions avec le menu principal de l'application.
 * Elle permet à l'utilisateur de naviguer entre les différents modes de jeu et fonctionnalités.
 */
public class ControllerMenu {

    /**
     * Bouton pour démarrer une partie en mode solo.
     */
    @FXML
    private Button soloButton;

    /**
     * Bouton pour accéder au mode tournoi.
     */
    @FXML
    private Button tournoisButton;

    /**
     * Bouton pour afficher le classement des joueurs.
     */
    @FXML
    private Button classementButton;

    /**
     * Référence à l'application principale permettant de naviguer entre les vues et gérer les fonctionnalités globales.
     */
    private Main mainApp;

    /**
     * Gère l'action du bouton "Solo".
     * Lance une partie en mode hors ligne.
     */
    public void actionSoloButton() {
        if (mainApp != null) {
            mainApp.startColorMemoryGame(0); // Démarre une partie en mode solo
        }
    }

    /**
     * Gère l'action du bouton "Tournoi".
     * Redirige l'utilisateur vers l'écran de sélection du serveur pour le mode tournoi.
     */
    public void actionTournoisButton() {
        if (mainApp != null) {
            mainApp.startChoixServer(); // Accède à la sélection du serveur pour le tournoi
        }
    }

    /**
     * Gère l'action du bouton "Classement".
     * Redirige l'utilisateur vers l'écran affichant le classement des joueurs.
     */
    public void actionClassementButton() {
        /*
        if (mainApp != null) {
            mainApp.startClassement(); // Affiche le classement des joueurs
        }
        */
    }

    /**
     * Associe une instance de l'application principale à ce contrôleur.
     *
     * @param mainApp Instance de l'application principale.
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
}