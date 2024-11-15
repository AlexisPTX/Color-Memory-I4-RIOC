package pinoteaux.projetrioc.gamepart;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Chrono {
    private int timeInSeconds;
    private final DateTimeFormatter timeFormatter;
    private Timeline timeline;
    private final ControllerSimon controller;
    private Simon simon;

    public Chrono(int minutes, ControllerSimon controller) {
        this.controller = controller;
        this.timeInSeconds = minutes * 30;
        this.timeFormatter = DateTimeFormatter.ofPattern("mm:ss");
        this.controller.updateTemps(formatTime(timeInSeconds));
    }

    public void setSimon(Simon simon) {
        this.simon = simon;
    }

    public void startChrono() {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
            return;
        }

        System.out.println("Starting chrono");
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

    private String formatTime(int seconds) {
        LocalTime time = LocalTime.ofSecondOfDay(seconds);
        return timeFormatter.format(time);
    }

    public void stopChrono() {
        if (timeline != null) {
            timeline.stop();
            System.out.println("Chrono manually stopped");
        }
    }
}