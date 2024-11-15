package pinoteaux.projetrioc.gamepart;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ControllerSimon {

    @FXML
    private SVGPath red;
    @FXML
    private SVGPath blue;
    @FXML
    private SVGPath yellow;
    @FXML
    private SVGPath green;
    @FXML
    private Text temps;
    @FXML
    private Text sequence;
    @FXML
    private Text messageSequence;
    @FXML
    private Group finGroup;
    @FXML
    private Group jeuGroup;
    private Simon simon;

    public void setSimon(Simon simon) {
        this.simon = simon;
        finGroup.setVisible(false);
    }

    public void stopSimon(){
        jeuGroup.setMouseTransparent(true);
        finGroup.setVisible(true);
    }

    public void actionRed(MouseEvent e) {
        handleAction(e, 4);
    }

    public void actionYellow(MouseEvent e) {
        handleAction(e, 3);
    }

    public void actionBlue(MouseEvent e) {
        handleAction(e, 2);
    }

    public void actionGreen(MouseEvent e) {
        handleAction(e, 1);
    }

    private void handleAction(MouseEvent e, int colorIndex) {
        disableShape();
        Timeline timeline = new Timeline();
        KeyFrame brightFrame = new KeyFrame(Duration.seconds(0.1), event -> {
            this.blinkShape(colorIndex, "BRIGHT");
        });
        KeyFrame darkFrame = new KeyFrame(Duration.seconds(0.5), event -> {
            this.blinkShape(colorIndex, "DARK");
        });
        KeyFrame delayFrame = new KeyFrame(Duration.seconds(0.3)); // Délai de 0.5 seconde

        timeline.getKeyFrames().addAll(brightFrame, darkFrame, delayFrame);
        timeline.setOnFinished(event -> {
            simon.checkAnswer(colorIndex); // Appeler checkAnswer après la fin de la timeline
            enableShape();
        });
        timeline.play();
    }

    public void blinkShape(int i, String s) {
        if(s.equals("DARK")) {
            switch (i) {
                case 1 -> green.setFill(Color.GREEN);
                case 2 -> blue.setFill(Color.MIDNIGHTBLUE);
                case 3 -> yellow.setFill(Color.DARKGOLDENROD);
                case 4 -> red.setFill(Color.DARKRED);
            }
        } else if(s.equals("BRIGHT")) {
            switch (i) {
                case 1 -> green.setFill(Color.LIME);
                case 2 -> blue.setFill(Color.BLUE);
                case 3 -> yellow.setFill(Color.YELLOW);
                case 4 -> red.setFill(Color.RED);
            }
        }
    }

    public void disableShape() {
        red.setDisable(true);
        blue.setDisable(true);
        green.setDisable(true);
        yellow.setDisable(true);
    }

    public void enableShape() {
        red.setDisable(false);
        blue.setDisable(false);
        green.setDisable(false);
        yellow.setDisable(false);
    }

    public void updateTemps(String temps) {
        this.temps.setText(temps);
    }

    public void updateSequence(int sequence) {
        this.sequence.setText(String.valueOf(sequence));
    }

    public void updateMessageSequence(String message) {
        Timeline timeline = new Timeline();
        KeyFrame writeFrame = null;
        KeyFrame emptyFrame = null;
        if (message.equals("SUIVANT")) {
            writeFrame = new KeyFrame(Duration.seconds(0), event -> {
                this.messageSequence.setFill(Color.LIME);
                this.messageSequence.setText("Bien joué ! Séquence suivante.");
            });
            emptyFrame = new KeyFrame(Duration.seconds(1), event -> {
                this.messageSequence.setText("");
            });

        } else if (message.equals("RESET")) {
            writeFrame = new KeyFrame(Duration.seconds(0), event -> {
                this.messageSequence.setFill(Color.RED);
                this.messageSequence.setText("Raté ! Recommencez");
            });
            emptyFrame = new KeyFrame(Duration.seconds(1), event -> {
                this.messageSequence.setText("");
            });
        }
        timeline.getKeyFrames().addAll(writeFrame, emptyFrame);
        timeline.play();
    }
}
