package VoxspellApp;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.WordModel;

/**
 * Created by edson on 5/10/16.
 */
public class FileChooserScene {
    WordModel _model;
    VBox _mainLayout;
    HBox _currentFileLayout;
    HBox _comboBoxLayout;
    HBox _listLayout;
    ComboBox<String> _listCombo;



    public FileChooserScene(WordModel model){
        _model = model;


        _mainLayout = new VBox(8);
        Label title = new Label("Change Spelling List");
        setCurrentFileLayout();
        setComboBoxLayout();


    }

    private void setCurrentFileLayout(){
        _currentFileLayout = new HBox(10);
        Label cFile = new Label("Current File: " + _model.getTitle());
    }

    private void setComboBoxLayout(){
        _comboBoxLayout = new HBox(10);

        _listCombo = new ComboBox<String>();


    }
}
