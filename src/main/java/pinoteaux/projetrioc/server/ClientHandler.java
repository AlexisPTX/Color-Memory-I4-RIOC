package pinoteaux.projetrioc.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * La classe ClientHandler gère les interactions entre un client et le serveur.
 * Elle gère la communication via des messages JSON pour la gestion des chats et des réponses dans une partie de jeu.
 */
public class ClientHandler implements Runnable {

    /**
     * La socket du client.
     */
    private final Socket socket;

    /**
     * Le numéro du serveur auquel le client est connecté.
     */
    private final int serverNumber;

    /**
     * La liste des entiers aléatoires représentant la séquence de couleurs.
     */
    private final List<Integer> randomIntegers;

    /**
     * L'indice de la séquence actuelle.
     */
    private int sequenceActual = 1;

    /**
     * L'indice du joueur actuel dans la séquence.
     */
    private int currentPlayerIndex = 0;

    /**
     * Le BufferedReader pour lire les messages du client.
     */
    private BufferedReader bf;

    /**
     * Le PrintWriter pour envoyer des messages au client.
     */
    private PrintWriter pw;

    /**
     * La liste des sockets des clients connectés au serveur.
     * Utilisation d'une CopyOnWriteArrayList pour gérer les concurrents.
     */
    private final CopyOnWriteArrayList<Socket> listClients;

    /**
     * Constructeur de la classe ClientHandler avec une séquence de couleurs fournie.
     *
     * @param socket          La socket du client.
     * @param serverNumber    Le numéro du serveur auquel le client est connecté.
     * @param randomIntegers  La liste des entiers aléatoires représentant la séquence des couleurs.
     * @param connectedClients La liste des clients connectés au serveur.
     */
    public ClientHandler(Socket socket, int serverNumber, List<Integer> randomIntegers, CopyOnWriteArrayList<Socket> connectedClients) {
        this.socket = socket;
        this.serverNumber = serverNumber;
        this.randomIntegers = randomIntegers;
        try {
            this.bf = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.pw = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("[ClientHandler] - Error reading from socket" + e.getMessage());
        }
        this.listClients = connectedClients;
    }

    /**
     * Constructeur de la classe ClientHandler sans séquence de couleurs fournie.
     *
     * @param socket          La socket du client.
     * @param serverNumber    Le numéro du serveur auquel le client est connecté.
     * @param connectedClients La liste des clients connectés au serveur.
     */
    public ClientHandler(Socket socket, int serverNumber, CopyOnWriteArrayList<Socket> connectedClients) {
        this.socket = socket;
        this.serverNumber = serverNumber;
        this.randomIntegers = null;
        try {
            this.bf = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.pw = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("[ClientHandler] - Error reading from socket" + e.getMessage());
        }
        this.listClients = connectedClients;
    }

    /**
     * La méthode run() permet de lire les messages envoyés par le client et de les traiter en fonction de leur type.
     * Elle exécute le traitement des messages jusqu'à ce que le client se déconnecte.
     */
    @Override
    public void run() {
        Gson gson = new Gson();
        try {
            String jsonString;
            while ((jsonString = this.bf.readLine()) != null) {
                try {
                    JsonObject json = gson.fromJson(jsonString, JsonObject.class);

                    if (json.has("type")) {
                        String type = json.get("type").getAsString();
                        if (type.equals("CHAT")) {
                            handleChatMessage(json, gson);
                        } else if (type.equals("GAME")) {
                            if (json.has("userAnswer")) {
                                int userAnswer = json.get("userAnswer").getAsInt();
                                handleUserAnswer(userAnswer);
                            } else {
                                System.err.println("[ClientHandler] - Invalid message format received.");
                            }
                        }
                    }
                } catch (JsonSyntaxException e) {
                    System.err.println("[ClientHandler] - Error parsing JSON: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("[ClientHandler] - Client disconnected from server " + serverNumber);
        }
    }

    /**
     * Gère les messages de type "CHAT" envoyés par le client.
     *
     * @param json Le message JSON reçu du client.
     * @param gson L'instance Gson pour sérialiser et désérialiser les objets JSON.
     */
    private void handleChatMessage(JsonObject json, Gson gson) {
        if (json.has("message") && json.has("pseudo")) {
            String message = json.get("message").getAsString();
            String pseudo = json.get("pseudo").getAsString();
            json = new JsonObject();
            json.addProperty("type", "CHAT");
            json.addProperty("message", message);
            json.addProperty("pseudo", pseudo);

            for (Socket otherSocket : listClients) {
                if (!otherSocket.equals(this.socket)) {
                    PrintWriter pwOthers = null;
                    try {
                        pwOthers = new PrintWriter(otherSocket.getOutputStream());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    pwOthers.println(gson.toJson(json));
                    pwOthers.flush();
                }
            }
        }
    }

    /**
     * Gère la couleur cliquée par le joueur.
     * Cette méthode vérifie la réponse et met à jour l'état du jeu en fonction de la réponse.
     *
     * @param userAnswer La réponse de l'utilisateur, la couleur qu'il a cliqué.
     */
    private void handleUserAnswer(int userAnswer) {
        Gson gson = new Gson();
        JsonObject responseJson;

        // Vérification si la réponse est correcte
        if (this.randomIntegers.get(this.currentPlayerIndex) == userAnswer) {
            this.currentPlayerIndex++;
            responseJson = createResponse("VALID");
            if (this.currentPlayerIndex == this.sequenceActual) {
                this.sequenceActual++;
                this.currentPlayerIndex = 0;
                responseJson = createResponse("SUIVANT");
            }
        } else {
            this.currentPlayerIndex = 0;
            responseJson = createResponse("RESET");
        }

        // Envoi de la réponse au client
        this.pw.println(gson.toJson(responseJson));
        this.pw.flush();
    }

    /**
     * Crée une réponse au client sous forme d'un objet JSON.
     * Cette réponse peut contenir des informations comme la séquence actuelle et les indices de la séquence du joueur.
     *
     * @param message Le message à envoyer au client (par exemple, "VALID", "RESET", "SUIVANT").
     * @return L'objet JSON contenant la réponse à envoyer.
     */
    private JsonObject createResponse(String message) {
        JsonObject response = new JsonObject();

        if (!message.equals("VALID")) {
            response.addProperty("sequenceActual", this.sequenceActual);
            response.addProperty("currentPlayerIndex", this.currentPlayerIndex);

            JsonArray randomIntegersJson = new JsonArray();
            for (int i : this.randomIntegers.subList(0, this.sequenceActual)) {
                randomIntegersJson.add(i);
            }
            response.add("randomIntegers", randomIntegersJson);
        }
        response.addProperty("message", message);
        return response;
    }
}
