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

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final int serverNumber;
    private List<Integer> randomIntegers;
    private int sequenceActual = 1;
    private int currentPlayerIndex = 0;
    private BufferedReader bf;
    private PrintWriter pw;
    private CopyOnWriteArrayList<Socket> listClients;

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
    @Override
    public void run() {
        Gson gson = new Gson();
        try {
            String jsonString;
            while ((jsonString = this.bf.readLine()) != null) {
                try {
                    JsonObject json = gson.fromJson(jsonString, JsonObject.class);

                    if(json.has("type")){
                        String type = json.get("type").getAsString();
                        if(type.equals("CHAT")){
                            if (json.has("message") && json.has("pseudo")) {
                                String message = json.get("message").getAsString();
                                String pseudo = json.get("pseudo").getAsString();
                                json = new JsonObject();
                                json.addProperty("type", "CHAT");
                                json.addProperty("message", message);
                                json.addProperty("pseudo", pseudo);
                                for(Socket otherSocket : listClients){
                                    if (!otherSocket.equals(this.socket)) {
                                        PrintWriter pw = new PrintWriter(otherSocket.getOutputStream());
                                        pw.println(gson.toJson(json));
                                        pw.flush();
                                    }
                                }
                            }
                        } else if(type.equals("GAME")) {
                            if (json.has("userAnswer")) {
                                int userAnswer = json.get("userAnswer").getAsInt();
                                handleUserAnswer(userAnswer);
                            }
                             else {
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

    private void handleUserAnswer(int userAnswer) {
        Gson gson = new Gson();
        JsonObject responseJson;
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
        this.pw.println(gson.toJson(responseJson));
        this.pw.flush();
    }

    private JsonObject createResponse(String message) {
        JsonObject response = new JsonObject();
        if(!message.equals("VALID")){
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