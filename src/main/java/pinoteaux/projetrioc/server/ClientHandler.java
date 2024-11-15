package pinoteaux.projetrioc.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final int serverNumber;
    private final List<Integer> randomIntegers;
    private int sequenceActual = 1;
    private int currentPlayerIndex = 0;

    public ClientHandler(Socket socket, int serverNumber, List<Integer> randomIntegers) {
        this.socket = socket;
        this.serverNumber = serverNumber;
        this.randomIntegers = randomIntegers;
    }

    @Override
    public void run() {
        Gson gson = new Gson();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            String jsonString;
            while((jsonString = bf.readLine()) == null);
            JsonObject json = gson.fromJson(jsonString, JsonObject.class);

            // Récupération des valeurs JSON
            int userAnswer = json.get("userAnswer").getAsInt();
            int sequenceActual = json.get("sequenceActual").getAsInt();
            int currentPlayerIndex = json.get("currentPlayerIndex").getAsInt();

            // Vérification de la réponse de l'utilisateur
            if (randomIntegers.get(currentPlayerIndex) == userAnswer) {
                currentPlayerIndex++;
                if (currentPlayerIndex == sequenceActual) {
                    sequenceActual++;
                    currentPlayerIndex = 0;
                }

                // Construction de la réponse JSON pour "SUIVANT"
                JsonObject response = new JsonObject();
                response.addProperty("sequenceActual", sequenceActual);
                response.addProperty("currentPlayerIndex", currentPlayerIndex);
                response.addProperty("message", "SUIVANT");
                pw.println(gson.toJson(response));

            } else {
                // Construction de la réponse JSON pour "RESET"
                JsonObject response = new JsonObject();
                response.addProperty("sequenceActual", sequenceActual);
                response.addProperty("currentPlayerIndex", currentPlayerIndex);

                JsonArray jsonArray = new JsonArray();
                randomIntegers.subList(0, sequenceActual).forEach(jsonArray::add);
                response.add("randomIntegers", jsonArray);
                response.addProperty("message", "RESET");
                pw.println(gson.toJson(response));
            }

        } catch (IOException e) {
            System.out.println("Client disconnected from server " + serverNumber);
        }
    }

}