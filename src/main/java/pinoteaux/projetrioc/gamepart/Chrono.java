package pinoteaux.projetrioc.gamepart;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe Chrono gérant un chronomètre en secondes pour un jeu Simon.
 * Cette classe utilise une instance de {@link Timeline} pour déclencher des actions à intervalles réguliers
 * et met à jour l'affichage du temps restant via un contrôleur.
 */
public class Chrono {

    /**
     * Temps restant en secondes.
     */
    private int timeInSeconds;

    /**
     * Formatteur pour afficher le temps au format "mm:ss".
     */
    private final DateTimeFormatter timeFormatter;

    /**
     * Timeline permettant de gérer le décompte du temps.
     */
    private Timeline timeline;

    /**
     * Contrôleur permettant de mettre à jour l'affichage du temps.
     */
    private final ControllerSimon controller;

    /**
     * Instance du jeu Simon associée au chronomètre.
     */
    private Simon simon;

    /**
     * Constructeur de la classe Chrono.
     *
     * @param minutes   Nombre de minutes pour initialiser le chronomètre (converti en secondes).
     * @param controller Instance de {@link ControllerSimon} pour mettre à jour l'affichage du temps.
     */
    public Chrono(int minutes, ControllerSimon controller) {
        this.controller = controller;
        this.timeInSeconds = minutes * 30; // Nombre de secondes initiales (erreur potentielle ici, vérifier si *30 est intentionnel)
        this.timeFormatter = DateTimeFormatter.ofPattern("mm:ss");
        this.controller.updateTemps(formatTime(timeInSeconds));
    }

    /**
     * Définit l'instance du jeu Simon associée au chronomètre.
     *
     * @param simon Instance de {@link Simon}.
     */
    public void setSimon(Simon simon) {
        this.simon = simon;
    }

    /**
     * Démarre le chronomètre.
     * Si le chronomètre est déjà en cours d'exécution, l'appel est ignoré.
     * Lorsque le temps arrive à zéro, le jeu est arrêté.
     */
    public void startChrono() {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
            return;
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeInSeconds--;
            this.controller.updateTemps(formatTime(timeInSeconds));

            if (timeInSeconds <= 0) {
                timeline.stop();
                if (simon != null) {
                    simon.stopGame();
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Formatte le temps restant en une chaîne de caractères au format "mm:ss".
     *
     * @param seconds Nombre de secondes à formater.
     * @return Temps formaté en chaîne de caractères.
     */
    private String formatTime(int seconds) {
        LocalTime time = LocalTime.ofSecondOfDay(seconds);
        return timeFormatter.format(time);
    }

    /**
     * Arrête le chronomètre.
     * Si le chronomètre est déjà arrêté, l'appel est sans effet.
     */
    public void stopChrono() {
        if (timeline != null) {
            timeline.stop();
            System.out.println("Chrono manually stopped");
        }
    }
}
