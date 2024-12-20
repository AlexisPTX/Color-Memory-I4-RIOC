package pinoteaux.projetrioc.menu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import pinoteaux.projetrioc.Main;

import java.io.*;
import java.net.Socket;

/**
 * La classe ChatHandler gère les communications entre un client et un serveur via des sockets,
 * en écoutant les messages entrants et en permettant l'envoi de messages sortants.
 * Elle est utilisée principalement pour la gestion du chat et des commandes serveur dans l'application.
 */
public class ChatHandler implements Runnable {

    /**
     * Indique si le thread de gestion du chat est en cours d'exécution.
     */
    private volatile boolean running = true;

    /**
     * Référence à l'application principale, permettant d'interagir avec d'autres parties du programme.
     */
    private Main mainApp;

    /**
     * Socket pour la communication avec le serveur.
     */
    private Socket socket;

    /**
     * Flux de lecture des messages entrants depuis le serveur.
     */
    private BufferedReader in;

    /**
     * Flux d'écriture pour envoyer des messages au serveur.
     */
    private PrintWriter out;

    /**
     * Référence au contrôleur de l'interface du chat pour mettre à jour les messages affichés.
     */
    private ControllerChat controllerChat;

    /**
     * Constructeur du gestionnaire de chat.
     *
     * @param socket Socket de connexion au serveur.
     */
    public ChatHandler(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException ignored) {
        }
    }

    /**
     * Définit un nouveau socket pour le gestionnaire.
     *
     * @param socket Nouveau socket à utiliser.
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Arrête le gestionnaire en fermant le socket et en arrêtant le thread.
     */
    public void stop() {
        this.running = false; // Signal pour arrêter le thread
        try {
            this.socket.close(); // Ferme le socket pour libérer les ressources
        } catch (IOException e) {
            System.out.println("Error closing socket: " + e.getMessage());
        }
    }

    /**
     * Définit le contrôleur de chat, permettant de mettre à jour l'interface utilisateur.
     *
     * @param controllerChat Contrôleur de chat à associer.
     */
    public void setControllerChat(ControllerChat controllerChat) {
        this.controllerChat = controllerChat;
    }

    /**
     * Corps du thread exécuté pour écouter les messages entrants depuis le serveur.
     */
    @Override
    public void run() {
        if (this.running) {
            try {
                Gson gson = new Gson();
                String message;
                while ((message = this.in.readLine()) != null) {
                    if (!message.isBlank()) {
                        JsonObject messageJson = gson.fromJson(message, JsonObject.class);
                        if (messageJson != null) { // Vérifie que le parsing JSON a réussi
                            if (messageJson.has("type")) {
                                String type = messageJson.get("type").getAsString();
                                if (type.equals("CHAT")) {
                                    // Gère les messages de chat
                                    if (messageJson.has("message") && messageJson.has("pseudo")) {
                                        String text = messageJson.get("message").getAsString();
                                        String pseudo = messageJson.get("pseudo").getAsString();
                                        this.controllerChat.addMessageToUI(text, pseudo);
                                    }
                                } else if (type.equals("SERVER")) {
                                    // Gère les commandes serveur
                                    if (messageJson.has("message") && messageJson.has("intDebut")) {
                                        int intDebut = messageJson.get("intDebut").getAsInt();
                                        String messageServ = messageJson.get("message").getAsString();
                                        if (messageServ.equals("START")) {
                                            Platform.runLater(() -> {
                                                mainApp.startSimonGame(intDebut);
                                            });
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Envoie un message au serveur.
     *
     * @param message Message à envoyer.
     */
    public void sendMessage(String message) {
        this.out.println(message);
        this.out.flush();
    }

    /**
     * Associe l'application principale au gestionnaire.
     *
     * @param mainApp Référence à l'application principale.
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
}