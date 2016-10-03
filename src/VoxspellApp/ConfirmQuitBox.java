package VoxspellApp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmQuitBox {

    //Create variable
    boolean answer;//the answer yes or no

    public boolean display(String title, String message) {
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
        Button noButton = new Button("No");
        noButton.setStyle("-fx-background-radius: 5 5 5 5");

        yesButton.setOnAction(e -> {
            answer = true;
            window.close();
        });
        noButton.setOnAction(e -> {
            answer = false;
            window.close();
        });


        TilePane optionLayout = new TilePane();
        optionLayout.setAlignment(Pos.CENTER);
        optionLayout.getChildren().addAll(yesButton, noButton);
        optionLayout.setPadding(new Insets(15, 0, 15, 0));
        optionLayout.setHgap(30);
        optionLayout.setVgap(4);
        optionLayout.setStyle("-fx-base: #262262;");

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, optionLayout);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-base: #262262;");
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        //Make sure to return answer
        //THE ORIGINAL WINDOW CALLS DISPLAY() METHOD OF THIS CLASS. TO CREATE INTERACTION BETWEEN WINDOWS, THIS
        //DISPLAY METHOD REUTNRS A BOOLEAN VALUE WHICH CAN BE USED BY THE ORIGINAL WINDOW.
        return answer;
    }

}