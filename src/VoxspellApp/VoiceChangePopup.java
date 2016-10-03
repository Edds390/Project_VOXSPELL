package VoxspellApp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Festival;

/**
 * Created by edson on 21/09/16.
 */
public class VoiceChangePopup {
    Stage _window;
    VBox _layout;

    //Buttons
    Button _applyButton;
    Button _cancelButton;

    String _voiceOption;
    String _oldVoice;

    ComboBox<String> _voiceCombo;


    public VoiceChangePopup(){
        _window = new Stage();
        _window.initModality(Modality.APPLICATION_MODAL);//modality for suppressing main window
        _window.setTitle("Menu");
        _window.setMinWidth(125);
        _window.setResizable(false);

        _layout = new VBox(7);
        _layout.setAlignment(Pos.CENTER);
        _layout.setPadding(new Insets(10));
        _layout.setStyle("-fx-base: #262262;");

        _oldVoice = Festival._getVoice();
        _voiceCombo = new ComboBox<>();
        for(String voice: Festival.getVoiceList()){
            _voiceCombo.getItems().add(voice);
        }

        _voiceCombo.setValue(_oldVoice);
        _voiceCombo.setStyle("-fx-background-radius: 10 10 10 10");

        HBox buttonBox = new HBox(15);

        _applyButton = new Button("Apply");
        _applyButton.setDisable(true);
        _applyButton.setMinWidth(70);
        _applyButton.setStyle("-fx-background-radius: 10 10 10 10");

        _cancelButton = new Button("Cancel");
        _cancelButton.setMinWidth(70);
        _cancelButton.setStyle("-fx-background-radius: 10 10 10 10");

        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(_applyButton, _cancelButton);
        _layout.getChildren().addAll(_voiceCombo, buttonBox);

        Scene scene = new Scene(_layout);
        _window.setScene(scene);

        setupEventHandlers();

    }

    public void display(){
        _window.showAndWait();
    }

    public void setupEventHandlers(){
        _voiceCombo.setOnAction(e->{
            String option = (String)_voiceCombo.getValue();
            if (!option.equals(_oldVoice)){
                _voiceOption = option;
                _applyButton.setDisable(false);
            } else {//same voice option
                _applyButton.setDisable(true);
            }
        });
        _applyButton.setOnAction(e->{
            Festival.changeVoice(_voiceOption);
            _oldVoice = _voiceOption;
            _applyButton.setDisable(true);
        });
        _cancelButton.setOnAction(e->{
            _window.close();
        });
    }
}
