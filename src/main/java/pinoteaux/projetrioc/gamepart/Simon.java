package pinoteaux.projetrioc.gamepart;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
    private Socket socket;
    private Chrono chrono;

    private int sequenceActual = 1;
    private int currentPlayerIndex = 0;
    private final List<Integer> randomIntegers = new ArrayList<>();

    public Simon(ControllerSimon controller, Socket socket, Chrono chrono, String mode) {
        this.controller = controller;
        this.socket = socket;
        this.chrono = chrono;
        this.chrono.startChrono();
        initList("SOLO");
        startGame();
    }

    private void initList(String mode) {
        if(mode.equals("SOLO")) {
            for (int i = 0; i < 100; i++) {
                randomIntegers.add(new Random().nextInt(1, 5));
            }
        }else if(mode.equals("MULTI"))  {
            try{
                BufferedReader bf = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                randomIntegers.set(0, Integer.parseInt(bf.readLine()));
            }
            catch(IOException e){e.printStackTrace();}
        }
    }

    // Méthode pour afficher la séquence actuelle
    public void displaySequence() {
        this.controller.updateSequence(sequenceActual);
        this.controller.disableShape();
        Timeline timeline = new Timeline();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));

        for (int i = 0; i < sequenceActual; i++) {
            int index = i;
            if (index == 0) {
                pause.play();
            }

            KeyFrame brightFrame = new KeyFrame(Duration.seconds(index), event -> {
                this.controller.blinkShape(randomIntegers.get(index), "BRIGHT");
            });
            KeyFrame darkFrame = new KeyFrame(Duration.seconds(index + 0.5), event -> {
                this.controller.blinkShape(randomIntegers.get(index), "DARK");
            });

            timeline.getKeyFrames().addAll(brightFrame, darkFrame);
        }

        pause.setOnFinished(eventP -> {
            timeline.setOnFinished(eventF -> {
                this.controller.enableShape();
            });
            timeline.play();
        });
    }

    public void checkAnswer(int userAnswer) {
        if(this.socket == null) {
            if (randomIntegers.get(currentPlayerIndex) == userAnswer) {
                currentPlayerIndex++;

                if (currentPlayerIndex == sequenceActual) {
                    sequenceActual++;
                    currentPlayerIndex = 0;
                    this.controller.updateMessageSequence("SUIVANT");
                    javafx.application.Platform.runLater(this::displaySequence);
                }

            } else {
                this.controller.updateMessageSequence("RESET");
                currentPlayerIndex = 0;
                javafx.application.Platform.runLater(this::displaySequence);
            }
        }else{
            try {
                BufferedReader bf = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                PrintWriter pw = new PrintWriter(this.socket.getOutputStream(), true);

                Gson gson = new Gson();
                JsonObject json = new JsonObject();
                json.addProperty("userAnswer", userAnswer);
                json.addProperty("sequenceActual", sequenceActual);
                json.addProperty("currentPlayerIndex", currentPlayerIndex);

                pw.println(gson.toJson(json));

                String responseJson;
                while((responseJson = bf.readLine()) != null){
                    JsonObject response = gson.fromJson(responseJson, JsonObject.class);
                    currentPlayerIndex = response.get("currentPlayerIndex").getAsInt();
                    sequenceActual = response.get("sequenceActual").getAsInt();
                    javafx.application.Platform.runLater(this::displaySequence);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Méthode pour démarrer le jeu
    public void startGame() {
        sequenceActual = 1;
        currentPlayerIndex = 0;
        javafx.application.Platform.runLater(this::displaySequence);
    }

    // Méthode main pour démarrer l'application
    public static void main(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Simon.class.getResource("simon.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("Color Memory");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void stopGame() {
        this.controller.stopSimon();
    }
}