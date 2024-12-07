package pinoteaux.projetrioc.menu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;

public class ChatHandler implements Runnable {
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

    // Injecte le contrôleur pour mettre à jour l'interface
    public void setControllerChat(ControllerChat controllerChat) {
        this.controllerChat = controllerChat;
    }

    @Override
    public void run() {
        try {
            Gson gson = new Gson();
            String message;
            while ((message = in.readLine()) != null) {
                if (!message.isBlank()) {
                    JsonObject messageJson = gson.fromJson(message, JsonObject.class);
                    if (messageJson != null) { // Vérifie que le parsing JSON a réussi
                        if (messageJson.has("type")) {
                            String type = messageJson.get("type").getAsString();
                            if (type.equals("CHAT")) {
                                if (messageJson.has("message")) {
                                    String text = messageJson.get("message").getAsString();
                                    this.controllerChat.addMessageToUI(text,"pseudo");
                                    System.out.println("Received: " + text);
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

    public void sendMessage(String message) {
        this.out.println(message);
        this.out.flush();
    }
}
