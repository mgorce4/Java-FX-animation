package unilim.info.ihm.tp5.exo3;

import java.util.Objects;

import javafx.scene.image.Image;

public class ImageLoader {
    public static Image loadMarioImage(String fileName, double width, double height) {
        return new Image(
            Objects.requireNonNull(ImageLoader.class.getResource(fileName)).toExternalForm(),
            width,
            height,
            true,
            false,
            false
        );
    }
}