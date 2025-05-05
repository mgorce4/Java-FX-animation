package unilim.info.ihm.tp5.exo3;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.WritableValue;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class MoveMarioController implements EventHandler<KeyEvent> {
    private final ImageView mario;
    private final int GROUND_Y = 537;
    private final int SCREEN_MAX_X = 1180;
    private String direction = "droite";
    private boolean transitionDone = true;

    public MoveMarioController(ImageView mario) {
        this.mario = mario;
    }


    @Override
    public void handle(KeyEvent event) {
        // On ne peut pas déplacer Mario si une transition est en cours.
        if (!transitionDone) return;
        Timeline timeline = null;

        if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.Q) {
            direction = "gauche";
            double newValue = mario.xProperty().getValue() - 20;

            if (newValue < 0) {
                newValue = 0;
            }

            timeline = moveMario(mario.xProperty(), newValue);
        }
        else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D) {
            direction = "droite";
            double newValue = mario.xProperty().getValue() + 20;

            if (newValue > SCREEN_MAX_X) {
                newValue = SCREEN_MAX_X;
            }

            timeline = moveMario(mario.xProperty(), newValue);
        }
        else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.Z) {
            direction = "haut";
            timeline = moveMario(mario.yProperty(), mario.yProperty().getValue() - 20);
        }
        else if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.S) {
            direction = "bas";
            double newValue = mario.yProperty().getValue() + 20;

            if (newValue > GROUND_Y) {
                newValue = GROUND_Y;
            }

            timeline = moveMario(mario.yProperty(), newValue);
        }
        else if (event.getCode() == KeyCode.SPACE) {
            sautTransition();
        }

        if (timeline != null) {
            // On dit qu'une transition est en cours.
            transitionDone = false;

            timeline.play();
            timeline.setOnFinished(e -> {
                transitionDone = true;
                checkFallZone(); // Vérifie la chute seulement après un déplacement horizontal ou vertical
            });
        }

        // Load the image according to the direction.
        Image image = ImageLoader.loadMarioImage(direction + ".png", 20, 30);
        mario.setImage(image);
    }


    /**
     * Saut qui va simplement de haut en bas.
     */
    private void sautTransitionStatique () {
        TranslateTransition goToTop = new TranslateTransition(Duration.millis(500), mario);
        goToTop.setByY(-50);

        TranslateTransition goToBottom = new TranslateTransition(Duration.millis(500), mario);
        goToBottom.setByY(50);

        SequentialTransition sequences = new SequentialTransition(goToTop, goToBottom);
        sequences.play();
    }

    /**
     * Saut en fonction de la direction en utilisant des arcs de cercles.
     */
    private void sautTransition() {
        double x = mario.xProperty().get();
        double y = mario.yProperty().get();

        double sautDistance = 80; // Was 50 → now longer jump
        double targetXTemp;

        if ("droite".equals(direction)) {
            targetXTemp = Math.min(x + sautDistance, SCREEN_MAX_X);
        } else if ("gauche".equals(direction)) {
            targetXTemp = Math.max(x - sautDistance, 0);
        } else {
            return;
        }

        final double targetX = targetXTemp;

        // Saut impossible (déjà au bord)
        if (targetX == x) {
            TranslateTransition up = new TranslateTransition(Duration.millis(200), mario);
            up.setByY(-40);
            TranslateTransition down = new TranslateTransition(Duration.millis(200), mario);
            double drop = GROUND_Y - y;
            down.setToY(drop);

            SequentialTransition rebondMur = new SequentialTransition(up, down);
            transitionDone = false;
            rebondMur.setOnFinished(e -> {
                transitionDone = true;
                mario.translateYProperty().set(0);
                mario.yProperty().set(GROUND_Y);
                checkFallZone();
            });
            rebondMur.play();
            return;
        }

        // Long arc jump
        Path chemin = new Path();
        MoveTo elem1 = new MoveTo(x, y);
        ArcTo elem2 = new ArcTo(90, 90, 0, targetX, y, false, "droite".equals(direction));
        chemin.getElements().addAll(elem1, elem2);

        PathTransition pathT = new PathTransition(Duration.millis(600), chemin, mario); // Was 300 → now 600ms

        TranslateTransition descente = new TranslateTransition(Duration.millis(300), mario); // Smoother fall
        double dropToGround = GROUND_Y - y;
        descente.setToY(dropToGround);

        SequentialTransition seq = new SequentialTransition(pathT, descente);
        transitionDone = false;
        seq.setOnFinished(e -> {
            transitionDone = true;
            mario.translateYProperty().set(0);
            mario.translateXProperty().set(0);
            mario.xProperty().set(targetX);
            mario.yProperty().set(GROUND_Y);
            checkFallZone();
        });
        seq.play();
    }



    private Timeline moveMario (WritableValue<Number> prop, Number target) {
        KeyValue kv = new KeyValue(prop, target, Interpolator.LINEAR);
        KeyFrame kf = new KeyFrame(Duration.millis(250), kv);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(kf);

        return timeline;
    }
    
    private void checkFallZone() {
        double x = mario.xProperty().get();
        double y = mario.yProperty().get();

        if (y == GROUND_Y && x >= 85 && x <= 140 ) {
            transitionDone = false;

            // Animation de chute
            TranslateTransition chute = new TranslateTransition(Duration.millis(600), mario);
            chute.setByY(300); // Tomber vers le bas

            chute.setOnFinished(e -> {
                // Réinitialiser Mario après la chute
                mario.translateYProperty().set(0);
                mario.translateXProperty().set(0);
                mario.xProperty().set(14);
                mario.yProperty().set(GROUND_Y);
                transitionDone = true;
            });

            chute.play();
        }
    }

}