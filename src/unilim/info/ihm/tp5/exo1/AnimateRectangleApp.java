package unilim.info.ihm.tp5.exo1;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class AnimateRectangleApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Rectangle rectangle = new Rectangle(50, 50, 150, 100);
        rectangle.setFill(Color.BLUE);

        AnimationTimer anim = animateRectangle(rectangle);
        anim.start();

        Button resetButton = new Button("Reset");
        resetButton.setOnMouseClicked(event ->{
            rectangle.xProperty().set(50);
            anim.start();
        });

        Group root = new Group(rectangle, resetButton);
        Scene scene = new Scene(root, 600, 200);
        scene.setFill(Color.LIGHTGREEN);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Animation d'un rectangle");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private AnimationTimer animateRectangle(Rectangle rect) {
        AnimationTimer animation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (rect.xProperty().get() < 400) {
                    rect.xProperty().set(rect.xProperty().get() + 1);
                } else {
                    stop();
                }
            }
        };

        return animation;
    }
}