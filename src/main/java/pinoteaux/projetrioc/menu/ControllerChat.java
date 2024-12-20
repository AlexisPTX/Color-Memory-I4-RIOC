package pinoteaux.projetrioc.menu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * La classe ControllerChat gère l'interface utilisateur du chat dans l'application,
 * permettant d'envoyer et d'afficher les messages échangés entre les utilisateurs.
 */
public class ControllerChat {

    /**
     * Boîte contenant tous les messages affichés dans l'interface.
     */
    @FXML
    private VBox messagesBox;

    /**
     * Zone de texte pour la saisie des messages par l'utilisateur.
     */
    @FXML
    private TextArea messageInput;

    /**
     * Bouton permettant d'envoyer un message.
     */
    @FXML
    private Button buttonSend;

    /**
     * Gestionnaire de chat permettant la communication avec le serveur.
     */
    private ChatHandler chatHandler;

    /**
     * Nom d'utilisateur associé à l'utilisateur actuel.
     */
    private String username;

    /**
     * Associe un gestionnaire de chat et un nom d'utilisateur à ce contrôleur.
     *
     * @param chatHandler Gestionnaire de chat pour gérer les messages.
     * @param username    Nom d'utilisateur de l'utilisateur actuel.
     */
    public void setChatHandler(ChatHandler chatHandler, String username) {
        this.chatHandler = chatHandler;
        this.username = username;
    }

    /**
     * Initialise les composants de l'interface utilisateur et configure l'action
     * du bouton d'envoi pour transmettre les messages au serveur.
     */
    public void initialize() {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        buttonSend.setOnAction(event -> {
            String message = messageInput.getText();
            if (!message.isEmpty() && chatHandler != null) {
                json.addProperty("type", "CHAT");
                json.addProperty("message", message);
                json.addProperty("pseudo", this.username);
                chatHandler.sendMessage(gson.toJson(json)); // Envoie le message au serveur
                addMessageToUI(message, "Moi"); // Affiche le message localement
                messageInput.clear();
            }
        });
    }

    /**
     * Ajoute un message à l'interface utilisateur.
     *
     * @param message Message à afficher.
     * @param pseudo  Pseudo de l'utilisateur ayant envoyé le message.
     */
    public void addMessageToUI(String message, String pseudo) {
        Platform.runLater(() -> {
            Text text = new Text(pseudo + " : " + message);
            messagesBox.getChildren().add(text);
        });
    }
}