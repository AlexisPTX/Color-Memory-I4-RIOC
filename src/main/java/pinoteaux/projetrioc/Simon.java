package pinoteaux.projetrioc;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simon {
    private final Controller controller;
    private Chrono chrono;

    private int sequenceActual = 1; // Pas besoin d'être statique
    private int currentPlayerIndex = 0; // L'index actuel dans la séquence du joueur
    private final List<Integer> randomIntegers = new ArrayList<>();

    public Simon(Controller controller, Chrono chrono) {
        this.controller = controller;
        this.chrono = chrono;
        this.chrono.startChrono();
        initList(); // Initialiser la liste lors de la création de Simon
        startGame(); // Lancer le jeu immédiatement après
    }

    // Méthode pour initialiser la liste de séquences aléatoires
    private void initList() {
        for (int i = 0; i < 100; i++) {
            randomIntegers.add(new Random().nextInt(1, 5));
        }
        // Debug : afficher la séquence générée
        /*for (Integer number : randomIntegers) {
            System.out.println(number);
        }*/
    }

    // Méthode pour afficher la séquence actuelle
    private void displaySequence() {
        this.controller.updateSequence(sequenceActual);
        this.controller.disableShape();
        Timeline timeline = new Timeline();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));

        for (int i = 0; i < sequenceActual; i++) {
            int index = i;
            if (index == 0) {
                pause.play();
            }

            // Le temps d'attente entre chaque clignotement

            // Clignotement de la couleur
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


    // Méthode appelée quand le joueur clique sur une couleur
    public void checkAnswer(int userAnswer) {
        if (randomIntegers.get(currentPlayerIndex) == userAnswer) {
            currentPlayerIndex++; // Si la réponse est correcte, on passe à l'index suivant

            if (currentPlayerIndex == sequenceActual) {
                // Si la séquence est terminée, on passe à la séquence suivante
                sequenceActual++;
                currentPlayerIndex = 0;
                this.controller.updateMessageSequence("SUIVANT");
                displaySequence(); // Afficher la nouvelle séquence
            }

        } else {
            // Si la réponse est incorrecte, on réinitialise le jeu
            this.controller.updateMessageSequence("RESET");
            currentPlayerIndex = 0; // Appeler une méthode pour réinitialiser le jeu
            displaySequence();
        }
    }

    // Méthode pour réinitialiser le jeu
    private void resetGame() {
        sequenceActual = 1;
        currentPlayerIndex = 0;
        displaySequence(); // Réafficher la première séquence
    }

    // Méthode pour démarrer le jeu
    public void startGame() {
        resetGame(); // Lancer le jeu en réinitialisant
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
