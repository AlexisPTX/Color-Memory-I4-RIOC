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

/**
 * Classe contrôleur pour le jeu.
 * Gère l'interaction utilisateur, l'affichage des séquences, le chronomètre et les actions liées aux boutons de couleur.
 */
public class ControllerColorMemory {

    /**
     * Chemin SVG pour la section rouge.
     */
    @FXML
    private SVGPath red;

    /**
     * Chemin SVG pour la section bleue.
     */
    @FXML
    private SVGPath blue;

    /**
     * Chemin SVG pour la section jaune.
     */
    @FXML
    private SVGPath yellow;

    /**
     * Chemin SVG pour la section verte.
     */
    @FXML
    private SVGPath green;

    /**
     * Texte affichant le temps restant.
     */
    @FXML
    private Text temps;

    /**
     * Texte affichant la séquence actuelle.
     */
    @FXML
    private Text sequence;

    /**
     * Texte affichant les messages de progression ou d'erreur.
     */
    @FXML
    private Text messageSequence;

    /**
     * Groupe de fin de jeu (affiché à la fin d'une partie). Affichage de GAME OVER à l'écran.
     */
    @FXML
    private Group finGroup;

    /**
     * Groupe de jeu principal.
     */
    @FXML
    private Group jeuGroup;

    /**
     * Instance du jeu associée à ce contrôleur.
     */
    private ColorMemory colorMemory;

    /**
     * Définit l'instance du pour ce contrôleur.
     *
     * @param colorMemory Instance de {@link ColorMemory}.
     */
    public void setColorMemory(ColorMemory colorMemory) {
        this.colorMemory = colorMemory;
        this.finGroup.setVisible(false);
    }

    /**
     * Arrête la partie, rend l'interface non interactive et affiche l'écran de fin.
     */
    public void stopColorMemory() {
        this.jeuGroup.setMouseTransparent(true);
        this.finGroup.setVisible(true);
    }

    /**
     * Action déclenchée lorsque la section rouge est cliquée.
     *
     * @param e Événement de la souris.
     */
    public void actionRed(MouseEvent e) {
        handleAction(e, 4);
    }

    /**
     * Action déclenchée lorsque la section jaune est cliquée.
     *
     * @param e Événement de la souris.
     */
    public void actionYellow(MouseEvent e) {
        handleAction(e, 3);
    }

    /**
     * Action déclenchée lorsque la section bleue est cliquée.
     *
     * @param e Événement de la souris.
     */
    public void actionBlue(MouseEvent e) {
        handleAction(e, 2);
    }

    /**
     * Action déclenchée lorsque la section verte est cliquée.
     *
     * @param e Événement de la souris.
     */
    public void actionGreen(MouseEvent e) {
        handleAction(e, 1);
    }

    /**
     * Gère une action utilisateur pour une couleur spécifique.
     * Désactive les sections, fait clignoter la couleur et vérifie la réponse.
     *
     * @param e          Événement de la souris.
     * @param colorIndex Index de la couleur (1 = vert, 2 = bleu, 3 = jaune, 4 = rouge).
     */
    public void handleAction(MouseEvent e, int colorIndex) {
        disableShape();
        Timeline timeline = new Timeline();
        KeyFrame brightFrame = new KeyFrame(Duration.seconds(0.1), event -> {
            this.blinkShape(colorIndex, "BRIGHT");
        });
        KeyFrame darkFrame = new KeyFrame(Duration.seconds(0.5), event -> {
            this.blinkShape(colorIndex, "DARK");
        });
        KeyFrame delayFrame = new KeyFrame(Duration.seconds(0.3));

        timeline.getKeyFrames().addAll(brightFrame, darkFrame, delayFrame);
        timeline.setOnFinished(event -> {
            this.colorMemory.checkAnswer(colorIndex);
            enableShape();
        });
        timeline.play();
    }

    /**
     * Fait clignoter une forme SVG dans une couleur spécifique (BRIGHT ou DARK).
     *
     * @param i Index de la couleur (1 = vert, 2 = bleu, 3 = jaune, 4 = rouge).
     * @param s État de la couleur ("BRIGHT" ou "DARK").
     */
    public void blinkShape(int i, String s) {
        if (s.equals("DARK")) {
            switch (i) {
                case 1 -> this.green.setFill(Color.GREEN);
                case 2 -> this.blue.setFill(Color.MIDNIGHTBLUE);
                case 3 -> this.yellow.setFill(Color.DARKGOLDENROD);
                case 4 -> this.red.setFill(Color.DARKRED);
            }
        } else if (s.equals("BRIGHT")) {
            switch (i) {
                case 1 -> this.green.setFill(Color.LIME);
                case 2 -> this.blue.setFill(Color.BLUE);
                case 3 -> this.yellow.setFill(Color.YELLOW);
                case 4 -> this.red.setFill(Color.RED);
            }
        }
    }

    /**
     * Désactive les sections de couleur pour empêcher toute interaction.
     */
    public void disableShape() {
        this.red.setDisable(true);
        this.blue.setDisable(true);
        this.green.setDisable(true);
        this.yellow.setDisable(true);
    }

    /**
     * Réactive les sections de couleur pour permettre l'interaction.
     */
    public void enableShape() {
        this.red.setDisable(false);
        this.blue.setDisable(false);
        this.green.setDisable(false);
        this.yellow.setDisable(false);
    }

    /**
     * Met à jour l'affichage du temps restant.
     *
     * @param temps Temps restant au format texte.
     */
    public void updateTemps(String temps) {
        this.temps.setText(temps);
    }

    /**
     * Met à jour l'affichage de la séquence actuelle.
     *
     * @param sequence Séquence actuelle.
     */
    public void updateSequence(int sequence) {
        this.sequence.setText(String.valueOf(sequence));
    }

    /**
     * Met à jour le message de progression ou d'erreur.
     * Affiche un message temporaire selon le type (suivant ou réinitialisation).
     *
     * @param message Message à afficher ("SUIVANT" ou "RESET").
     */
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
