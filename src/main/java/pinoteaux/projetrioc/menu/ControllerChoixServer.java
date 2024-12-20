package pinoteaux.projetrioc.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import pinoteaux.projetrioc.Constantes;
import pinoteaux.projetrioc.Main;

import java.io.IOException;
import java.net.Socket;

/**
 * La classe ControllerChoixServer gère la sélection d'un serveur parmi plusieurs options
 * disponibles dans l'interface utilisateur et établit une connexion avec le serveur choisi.
 */
public class ControllerChoixServer {

    /**
     * Référence à l'application principale permettant de naviguer entre les vues et gérer les fonctionnalités globales.
     */
    private Main mainApp;

    /**
     * Bouton pour se connecter au serveur 1.
     */
    @FXML
    private Button buttonServer1;

    /**
     * Bouton pour se connecter au serveur 2.
     */
    @FXML
    private Button buttonServer2;

    /**
     * Bouton pour se connecter au serveur 3.
     */
    @FXML
    private Button buttonServer3;

    /**
     * Bouton pour se connecter au serveur 4.
     */
    @FXML
    private Button buttonServer4;

    /**
     * Bouton pour se connecter au serveur 5.
     */
    @FXML
    private Button buttonServer5;

    /**
     * Groupe contenant les boutons de sélection de serveur.
     */
    @FXML
    private Group buttonGroup;

    /**
     * Masque le groupe de boutons de sélection de serveur.
     */
    public void hideGroup() {
        buttonGroup.setVisible(false);
    }

    /**
     * Établit une connexion avec un serveur en fonction du port spécifié
     * et masque le groupe de boutons après une connexion réussie.
     *
     * @param e    L'événement déclenché par l'utilisateur (non utilisé directement ici).
     * @param port Le port du serveur auquel se connecter.
     */
    private void connectToServer(ActionEvent e, int port) {
        Socket socket = null;
        try {
            socket = new Socket(Constantes.SERVER_ADDRESS, port);
        } catch (IOException ex) {
            System.out.println("Error in creating socket: " + ex.getMessage());
            return; // Sortie en cas d'échec de la création du socket
        }
        hideGroup();
        mainApp.attenteDebutTournoi(socket);
    }

    /**
     * Gère l'action du bouton de connexion au serveur 1.
     *
     * @param e L'événement déclenché par l'utilisateur.
     */
    public void actionButtonServer1(ActionEvent e) {
        connectToServer(e, Constantes.SERVER_1_PORT);
    }

    /**
     * Gère l'action du bouton de connexion au serveur 2.
     *
     * @param e L'événement déclenché par l'utilisateur.
     */
    public void actionButtonServer2(ActionEvent e) {
        connectToServer(e, Constantes.SERVER_2_PORT);
    }

    /**
     * Gère l'action du bouton de connexion au serveur 3.
     *
     * @param e L'événement déclenché par l'utilisateur.
     */
    public void actionButtonServer3(ActionEvent e) {
        connectToServer(e, Constantes.SERVER_3_PORT);
    }

    /**
     * Gère l'action du bouton de connexion au serveur 4.
     *
     * @param e L'événement déclenché par l'utilisateur.
     */
    public void actionButtonServer4(ActionEvent e) {
        connectToServer(e, Constantes.SERVER_4_PORT);
    }

    /**
     * Gère l'action du bouton de connexion au serveur 5.
     *
     * @param e L'événement déclenché par l'utilisateur.
     */
    public void actionButtonServer5(ActionEvent e) {
        connectToServer(e, Constantes.SERVER_5_PORT);
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