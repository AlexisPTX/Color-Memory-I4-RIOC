package pinoteaux.projetrioc.gamepart;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simon {
    private final ControllerSimon controller;
    final Socket socket;

    private int sequenceActual = 1;
    private int currentPlayerIndex = 0;
    private List<Integer> randomIntegers = new ArrayList<>();
    private String username;

    public Simon(ControllerSimon controller, Chrono chrono, String username) {
        this.controller = controller;
        chrono.startChrono();
        initList();
        this.username = username;
        this.socket = null;
    }
    public Simon(ControllerSimon controller, Socket socket, Chrono chrono, int firstInt, String username) {
        this.controller = controller;
        this.socket = socket;
        chrono.startChrono();
        this.randomIntegers.add(firstInt);
        this.username = username;
    }

    private void initList() {
        for (int i = 0; i < 100; i++) {
            this.randomIntegers.add(new Random().nextInt(1, 5));
        }
    }


    // Méthode pour afficher la séquence actuelle
    public void displaySequence() {
        this.controller.updateSequence(this.sequenceActual);
        this.controller.disableShape();

        SequentialTransition sequence = new SequentialTransition();
        for (int i = 0; i < this.sequenceActual; i++) {
            int index = i;

            PauseTransition bright = new PauseTransition(Duration.seconds(0.5));
            bright.setOnFinished(event -> this.controller.blinkShape(this.randomIntegers.get(index), "BRIGHT"));

            PauseTransition dark = new PauseTransition(Duration.seconds(0.5));
            dark.setOnFinished(event -> this.controller.blinkShape(this.randomIntegers.get(index), "DARK"));

            sequence.getChildren().addAll(bright, dark);
        }

        sequence.setOnFinished(event -> this.controller.enableShape());
        sequence.play();
    }


    public void checkAnswer(int userAnswer) {
        if(this.socket == null) {
            if (this.randomIntegers.get(this.currentPlayerIndex) == userAnswer) {
                this.currentPlayerIndex++;

                if (this.currentPlayerIndex == this.sequenceActual) {
                    this.sequenceActual++;
                    this.currentPlayerIndex = 0;
                    this.controller.updateMessageSequence("SUIVANT");
                    javafx.application.Platform.runLater(this::displaySequence);
                }

            } else {
                this.controller.updateMessageSequence("RESET");
                this.currentPlayerIndex = 0;
                javafx.application.Platform.runLater(this::displaySequence);
            }
        }else{
            try {
                BufferedReader bf = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                PrintWriter pw = new PrintWriter(this.socket.getOutputStream(), true);

                Gson gson = new Gson();
                JsonObject json = new JsonObject();
                json.addProperty("type", "GAME");
                json.addProperty("userAnswer", userAnswer);
                json.addProperty("sequenceActual", this.sequenceActual);
                json.addProperty("currentPlayerIndex", this.currentPlayerIndex);

                pw.println(gson.toJson(json));
                pw.flush();

                String responseJson;
                while ((responseJson = bf.readLine()) != null) {
                    if (!responseJson.isBlank()) { // Vérifie que la ligne n'est pas vide
                        JsonObject response = gson.fromJson(responseJson, JsonObject.class);
                        if (response != null) { // Vérifie que le parsing JSON a réussi
                            if (response.has("currentPlayerIndex")) {
                                this.currentPlayerIndex = response.get("currentPlayerIndex").getAsInt();
                            }
                            if (response.has("sequenceActual")) {
                                this.sequenceActual = response.get("sequenceActual").getAsInt();
                            }
                            if (response.has("randomIntegers")) {
                                JsonArray jsonArray = response.getAsJsonArray("randomIntegers");
                                this.randomIntegers = new ArrayList<>();
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    this.randomIntegers.add(jsonArray.get(i).getAsInt());
                                }
                            }
                            if (response.has("message")) {
                                if(!response.get("message").getAsString().equals("VALID")) {
                                    this.controller.updateMessageSequence(response.get("message").getAsString());
                                    javafx.application.Platform.runLater(this::displaySequence);
                                }
                                break;
                            }
                        } else {
                            System.err.println("[Simon] - Invalid JSON received: " + responseJson);
                        }
                    } else {
                        System.err.println("[Simon] - Blank line received from server.");
                    }
                }
            } catch (IOException e) {
                System.out.println("[Simon] - Failed to checkAnswer : " + e.getMessage());
            }
        }
    }

    // Méthode pour démarrer le jeu
    public void startGame() {
        sequenceActual = 1;
        currentPlayerIndex = 0;
        javafx.application.Platform.runLater(this::displaySequence);
    }

    public void stopGame() {
        this.controller.stopSimon();
        //sendResult(this.username);
    }
}