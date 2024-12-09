package pinoteaux.projetrioc.menu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ControllerChat {
    @FXML
    private VBox messagesBox;
    @FXML
    private TextArea messageInput;
    @FXML
    private Button buttonSend;

    private ChatHandler chatHandler;
    private String username;

    public void setChatHandler(ChatHandler chatHandler, String username) {
        this.chatHandler = chatHandler;
        this.username = username;
    }

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

    public void addMessageToUI(String message, String pseudo) {
        Platform.runLater(() -> {
            Text text = new Text(pseudo + " : " + message);
            messagesBox.getChildren().add(text);
        });
    }
}
