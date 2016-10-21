package VoxspellApp;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by edson on 22/10/16.
 */
public class WarningBox {

    public void display(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);//modality for suppressing main window
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);
        label.setStyle("-fx-font: bold 13 ariel");

        //create buttons
        Button yesButton = new Button("Yes");
        yesButton.setStyle("-fx-background-radius: 5 5 5 5");

        yesButton.setOnAction(e -> {
            window.close();
        });

        VBox layout = new VBox(5);
        layout.getChildren().addAll(label, yesButton);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

    }

}
