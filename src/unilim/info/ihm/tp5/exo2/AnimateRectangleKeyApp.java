package unilim.info.ihm.tp5.exo2;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class AnimateRectangleKeyApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Rectangle rectangle = new Rectangle(50, 50, 150, 100);
        rectangle.setFill(Color.BLUE);

        Group root = new Group(rectangle);
        Scene scene = new Scene(root, 600, 200);
        scene.setOnKeyPressed(new MoveRectangleController(rectangle));
        scene.setFill(Color.LIGHTGREEN);


        primaryStage.setScene(scene);
        primaryStage.setTitle("Animation d'un rectangle avec clavier");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}