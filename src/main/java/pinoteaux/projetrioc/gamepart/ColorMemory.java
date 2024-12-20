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

/**
 * La classe ColorMemory représente la logique du jeu, avec prise en charge
 * des modes hors ligne et en réseau. Elle gère les séquences, les réponses des joueurs,
 * et les interactions avec le contrôleur d'interface graphique.
 */
public class ColorMemory {

    /**
     * Référence au contrôleur pour mettre à jour l'interface utilisateur.
     */
    private final ControllerColorMemory controller;

    /**
     * Socket utilisé pour la communication réseau. Null si le mode est hors ligne.
     */
    final Socket socket;

    /**
     * Indique la séquence actuelle dans le jeu.
     */
    private int sequenceActual = 1;

    /**
     * Indice du joueur actuel vérifiant la séquence.
     */
    private int currentPlayerIndex = 0;

    /**
     * Liste des séquences aléatoires générées pour le jeu.
     */
    private List<Integer> randomIntegers = new ArrayList<>();

    /**
     * Nom d'utilisateur du joueur.
     */
    private String username;

    /**
     * Constructeur pour une partie de jeu hors ligne.
     *
     * @param controller Référence au contrôleur pour gérer l'interface utilisateur.
     * @param chrono      Référence à un chronomètre pour suivre le temps.
     * @param username    Nom d'utilisateur du joueur.
     */
    public ColorMemory(ControllerColorMemory controller, Chrono chrono, String username) {
        this.controller = controller;
        chrono.startChrono();
        initList();
        this.username = username;
        this.socket = null;
    }

    /**
     * Constructeur pour une partie de jeu en mode réseau.
     *
     * @param controller Référence au contrôleur pour gérer l'interface utilisateur.
     * @param socket     Socket pour la communication réseau.
     * @param chrono     Référence à un chronomètre pour suivre le temps.
     * @param firstInt   Premier entier de la séquence (généré par le serveur).
     * @param username   Nom d'utilisateur du joueur.
     */
    public ColorMemory(ControllerColorMemory controller, Socket socket, Chrono chrono, int firstInt, String username) {
        this.controller = controller;
        this.socket = socket;
        chrono.startChrono();
        this.randomIntegers.add(firstInt);
        this.username = username;
    }

    /**
     * Initialise la liste des séquences aléatoires avec 100 entiers
     * compris entre 1 et 4 pour une partie hors ligne.
     * (1 = vert, 2 = bleu, 3 = jaune, 4 = rouge)
     */
    private void initList() {
        for (int i = 0; i < 100; i++) {
            this.randomIntegers.add(new Random().nextInt(1, 5));
        }
    }

    /**
     * Affiche la séquence actuelle en faisant clignoter les formes dans l'interface.
     */
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

    /**
     * Vérifie si la réponse donnée par le joueur est correcte, pour une partie hors ligne.
     *
     * @param userAnswer La réponse donnée par le joueur (numéro de la forme cliquée).
     */
    public void checkAnswer(int userAnswer) {
        if (this.socket == null) {
            // Mode hors ligne
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
        } else {
            // Mode réseau
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
                    if (!responseJson.isBlank()) {
                        JsonObject response = gson.fromJson(responseJson, JsonObject.class);
                        if (response != null) {
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
                                if (!response.get("message").getAsString().equals("VALID")) {
                                    this.controller.updateMessageSequence(response.get("message").getAsString());
                                    javafx.application.Platform.runLater(this::displaySequence);
                                }
                                break;
                            }
                        } else {
                            System.err.println("[Color Memory] - Invalid JSON received: " + responseJson);
                        }
                    } else {
                        System.err.println("[Color Memory] - Blank line received from server.");
                    }
                }
            } catch (IOException e) {
                System.out.println("[Color Memory] - Failed to checkAnswer : " + e.getMessage());
            }
        }
    }

    /**
     * Démarre le jeu en réinitialisant la séquence et en affichant la première séquence.
     */
    public void startGame() {
        sequenceActual = 1;
        currentPlayerIndex = 0;
        javafx.application.Platform.runLater(this::displaySequence);
    }

    /**
     * Arrête le jeu et notifie le contrôleur.
     */
    public void stopGame() {
        this.controller.stopColorMemory();
    }
}