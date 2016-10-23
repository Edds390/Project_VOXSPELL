package VoxspellApp.Popups;

import javafx.concurrent.Task;
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
 * popupwindow prompted by the menu's voice change option.
 * Sets the voice if the user changes it. It is set by pressing the accept button.
 * Festival says "meow" so that user hears what the new voice sounds like before actually
 * accepting the change.
 */
public class VoiceChangePopup {
    Stage _window;
    VBox _layout;

    //Buttons
    Button _applyButton;
    Button _cancelButton;

    String _voiceOption;
    String _oldVoice;//old voice

    ComboBox<String> _voiceCombo;//voice options


    /**
     * initializes all stylizing and layouts.
     */
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
        //gets the relevant voice options available from festival
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

        _cancelButton = new Button("Back");
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

    /**
     * helper function setting up all the event handlers
     */
    private void setupEventHandlers(){
        //combobox of voice options. lsitens to changes in the new voice options.
        //if old voice, disable the accept button otherwise enable it.
        //Festival says something for user to sample the new voice.
        _voiceCombo.setOnAction(e->{
            String option = (String)_voiceCombo.getValue();
            if (!option.equals(_oldVoice)){
                _voiceOption = option;
                Festival.changeVoice(option);
                startFestivalThread("Meow!");
                Festival.changeVoice(_oldVoice);
                _applyButton.setDisable(false);
            } else {//same voice option
                _applyButton.setDisable(true);
            }
        });
        //apply the new voice on the Festival static class.
        _applyButton.setOnAction(e->{
            Festival.changeVoice(_voiceOption);
            _oldVoice = _voiceOption;
            _applyButton.setDisable(true);
        });
        //close the window.
        _cancelButton.setOnAction(e->{
            _window.close();
        });
    }

    /**
     * background thread to say the word
     * @param phrase word to be said
     */
    private void startFestivalThread(String phrase) {
        Task festivalTask = new Task() {
            @Override
            protected Object call() throws Exception {
                Festival.festivalTTS(phrase);
                return null;
            }
        };

        new Thread(festivalTask).start();
    }
}
