package VoxspellApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Level;
import models.Word;
import models.WordModel;
import sun.jvm.hotspot.opto.Block_List;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by edson on 5/10/16.
 */
public class FileChooserScene {

    private Desktop desktop = Desktop.getDesktop();

    private WordModel _model;
    private BorderPane _mainLayout;
    private VBox _comboBoxLayout;
    private HBox _listLayout;
    private ComboBox<String> _listCombo;

    //Buttons
    private Button _newListButton;
    private Button _deleteListButton;

    //ListViews
    private VBox _categoryLayout;
    ListView<String> _wordListView;

    //combobox items




    public FileChooserScene(WordModel model){
        _model = model;


        _mainLayout = new BorderPane();
        Label title = new Label("Change Spelling List");
        setComboBoxLayout();
        setViewListLayout();
        setEventHandlers();
        _mainLayout.setTop(_comboBoxLayout);
        _mainLayout.setCenter(_listLayout);
        _mainLayout.setPadding(new Insets(20));

    }

    public BorderPane getLayout(){
        return _mainLayout;
    }

    private void setComboBoxLayout(){
        _comboBoxLayout = new VBox(10);


        Label cFile = new Label("Current File: " + _model.getTitle());
        cFile.setStyle("-fx-font: bold 20 arial; -fx-text-fill: white;");


        HBox comboLayout = new HBox(10);
        //combobox
        ObservableList<String> options = FXCollections.observableArrayList();
        _listCombo = new ComboBox<String>(options);
        Set<String> listSet = _model.getMasterModel().getMapKeyset();
        for (String list : listSet){
            _listCombo.getItems().add(list);
        }
        _listCombo.setValue(_model.getTitle());
        //add and minus buttons
        HBox buttonBox = new HBox();
        _newListButton = new Button("+");
        _deleteListButton = new Button("-");
        buttonBox.getChildren().addAll(_newListButton, _deleteListButton);
        comboLayout.getChildren().addAll(_listCombo, buttonBox);
        comboLayout.setAlignment(Pos.CENTER);

        _comboBoxLayout.getChildren().addAll(cFile, comboLayout);
        _comboBoxLayout.setAlignment(Pos.CENTER);
        _comboBoxLayout.setPadding(new Insets(8));

    }

    private void setViewListLayout(){
        _listLayout = new HBox(30);

        _categoryLayout = new VBox(4);
        for (String category : _model.getCategoryList()){
            Hyperlink link = new Hyperlink(category);
            setAction(link);
            _categoryLayout.getChildren().add(link);

        }

        _wordListView = new ListView<>();
        ObservableList<String> words = fillList(_model.getCategoryList().get(0));

        _wordListView.setItems(words);
        _wordListView.setStyle("-fx-font: bold 20 arial; -fx-text-fill: white;");

        _listLayout.getChildren().addAll(_categoryLayout, _wordListView);
        _listLayout.setAlignment(Pos.CENTER);
    }

    private ObservableList<String> fillList(String category){
        ObservableList<String> words = FXCollections.observableArrayList();
        Level level = _model.getLevel(category);
        List<Word> wordList = level.getWordList();
        for (Word word : wordList){
            words.add(word.getWord());
        }
        return words;
    }

    private void setAction(Hyperlink link){
        link.setOnAction(e -> {
            ObservableList<String> wordsList = fillList(link.getText());
            _wordListView.setItems(wordsList);
        });
    }

    private void setEventHandlers(){
        _newListButton.setOnAction(e -> {
            Stage mainStage = (Stage)_newListButton.getScene().getWindow();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose a Spelling List");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            File selectedFile = fileChooser.showOpenDialog(mainStage);//pass reference to window
            if (selectedFile != null) {
                String fileName = selectedFile.getName();
                _model.newList(fileName);//update model with new spelling list
                setComboBoxLayout();
                setViewListLayout();
                _mainLayout.setTop(_comboBoxLayout);
                _mainLayout.setCenter(_listLayout);
                setEventHandlers();


            } else {
                //TODO prompt user saying file not found
            }

        });
        _deleteListButton.setOnAction(e -> {

        });
        _listCombo.setOnAction(e->{
            String spellingList = _listCombo.getSelectionModel().getSelectedItem();
            _model.newList(spellingList);
            setComboBoxLayout();
            setViewListLayout();
            _mainLayout.setTop(_comboBoxLayout);
            _mainLayout.setCenter(_listLayout);
            setEventHandlers();
        });

    }
}
