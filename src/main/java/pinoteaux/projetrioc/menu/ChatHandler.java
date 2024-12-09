package pinoteaux.projetrioc.menu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import pinoteaux.projetrioc.Main;

import java.io.*;
import java.net.Socket;

public class ChatHandler implements Runnable {
    private volatile boolean running = true;
    private Main mainApp;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ControllerChat controllerChat; // Référence au contrôleur

    public ChatHandler(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("[ChatHandler] - Could not open");
        }
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void stop() {
        this.running = false; // Signal to stop the thread
        try {
            this.socket.close(); // Close the socket to release resources
        } catch (IOException e) {
            System.out.println("Error closing socket: " + e.getMessage());
        }
    }

    // Injecte le contrôleur pour mettre à jour l'interface
    public void setControllerChat(ControllerChat controllerChat) {
        this.controllerChat = controllerChat;
    }

    @Override
    public void run() {
        if(this.running) {
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
                                    if (messageJson.has("message") && messageJson.has("pseudo")) {
                                        String text = messageJson.get("message").getAsString();
                                        String pseudo = messageJson.get("pseudo").getAsString();
                                        this.controllerChat.addMessageToUI(text, pseudo);
                                    }
                                }else if(type.equals("SERVER")){
                                    if(messageJson.has("message") && messageJson.has("intDebut")){
                                        int intDebut = messageJson.get("intDebut").getAsInt();
                                        String messageServ = messageJson.get("message").getAsString();
                                        if(messageServ.equals("START")){
                                            Platform.runLater(() -> {
                                                mainApp.startSimonGame(intDebut);
                                            });

                                            System.out.println("[ChatHandler] - START game started");
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error in ChatHandler: " + e.getMessage());
            }
        }
    }

    public void sendMessage(String message) {
        this.out.println(message);
        this.out.flush();
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
}
