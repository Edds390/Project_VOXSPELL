package VoxspellApp;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Created by edson on 22/10/16.
 */
public class WarningBox {

    public void display(String title, String message) {
        final URL resource = getClass().getResource("/MediaResources/124905__greencouch__beeps-5.wav");
        final Media media = new Media(resource.toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);//modality for suppressing main window
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);
        label.setStyle("-fx-font: bold 13 ariel");

        //create buttons
        Button yesButton = new Button("OK");
        yesButton.setStyle("-fx-background-radius: 5 5 5 5");

        yesButton.setOnAction(e -> {
            window.close();
        });

        VBox layout = new VBox(5);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(label, yesButton);
        layout.setStyle("-fx-base: #262262;");

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

    }

}
