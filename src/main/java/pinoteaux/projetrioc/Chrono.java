package pinoteaux.projetrioc;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Chrono{

    private int timeInSeconds;
    private final DateTimeFormatter timeFormatter;
    private Timeline timeline;
    private final Controller controller;
    private Simon simon;

    public Chrono(int minutes, Controller controller) {
        this.controller = controller;
        this.timeInSeconds = minutes * 5; // Convertir les minutes en secondes
        this.timeFormatter = DateTimeFormatter.ofPattern("mm:ss");
        this.controller.updateTemps(formatTime(timeInSeconds));
    }
    public void setSimon(Simon simon) {
        this.simon = simon;
    }

    public void startChrono() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeInSeconds--;
            this.controller.updateTemps(formatTime(timeInSeconds));

            // Arrêter le chronomètre à 0 secondes
            if (timeInSeconds <= 0) {
                timeline.stop();
                simon.stopGame();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private String formatTime(int seconds) {
        LocalTime time = LocalTime.ofSecondOfDay(seconds);
        return timeFormatter.format(time);
    }

    public void stopChrono() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}
