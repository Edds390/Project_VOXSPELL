package VoxspellApp.StartScenes;

import VoxspellApp.Popups.HelpWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Level;
import models.Word;
import models.WordModel;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Set;

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
    private Button _createListButton;
    private Button _helpButton;

    //ListViews
    private VBox _categoryLayout;
    ListView<String> _wordListView;

    //combobox items
    String _filePath;

    MediaPlayer _buttonSound;



    public FileChooserScene(WordModel model){
        _helpButton = new Button("?");
        _helpButton.setStyle("-fx-font: bold 30 latoheavy; -fx-base: #1db361; " +
                "-fx-background-radius: 40 40 40 40; -fx-text-fill:  white; -fx-border: 20px; -fx-border-color: white; -fx-border-radius: 40");
        _helpButton.setOnAction(e->{
            HelpWindow help = new HelpWindow(3);
            help.display();
        });

        _model = model;
        _filePath = model.getFilePath();
        _buttonSound = createSound("/MediaResources/SoundFiles/264447__kickhat__open-button-2.wav");

        _mainLayout = new BorderPane();
        Label title = new Label("Change Spelling List");
        setComboBoxLayout();
        setViewListLayout();
        setEventHandlers();
        _mainLayout.setTop(_comboBoxLayout);
        _mainLayout.setCenter(_listLayout);
        _mainLayout.setPadding(new Insets(20,20,40,20));


    }

    public BorderPane getLayout(){
        return _mainLayout;
    }

    private void setComboBoxLayout(){
        _comboBoxLayout = new VBox(10);


        HBox topBox = new HBox();
        Label cFile = new Label("Current File: " + _model.getTitle());
        Label space = new Label("\t\t\t\t\t\t\t\t\t\t");
        cFile.setStyle("-fx-font: bold 24 arial; -fx-text-fill: white; -fx-underline: true");
        topBox.getChildren().addAll(cFile, space,_helpButton);

        HBox comboLayout = new HBox(10);
        //combobox
        ObservableList<String> options = FXCollections.observableArrayList();
        _listCombo = new ComboBox<String>(options);
        Set<String> listSet = _model.getMasterModel().getMapKeyset();
        for (String list : listSet){
            _listCombo.getItems().add(list);
        }
        _listCombo.setValue(_model.getTitle());
        _listCombo.setStyle("-fx-font: 20 latoheavy; -fx-background-radius: 20 20 20 20");
        //add and minus buttons
        HBox buttonBox = new HBox();
        _newListButton = new Button("+");
        _newListButton.setStyle("-fx-font: 20 latoheavy; -fx-background-radius: 20 20 20 20");
        buttonBox.getChildren().addAll(_newListButton);
        comboLayout.getChildren().addAll(_listCombo, buttonBox);
        comboLayout.setAlignment(Pos.CENTER);

        _comboBoxLayout.getChildren().addAll(topBox, comboLayout);
        _comboBoxLayout.setAlignment(Pos.CENTER);
        _comboBoxLayout.setPadding(new Insets(8,8,35,8));

    }

    private void setViewListLayout(){


        _categoryLayout = new VBox(8);
        for (String category : _model.getCategoryList()){
            Hyperlink link = new Hyperlink(category);
            setAction(link);
            _categoryLayout.getChildren().add(link);

        }

        _wordListView = new ListView<>();
        ObservableList<String> words = fillList(_model.getCategoryList().get(0));

        _wordListView.setItems(words);
        _wordListView.setStyle("-fx-font: bold 20 arial; -fx-text-fill: white;");

        _createListButton = new Button("NEW LIST");
        _createListButton.setPrefWidth(180);
        _createListButton.setPrefHeight(140);
        _createListButton.setStyle("-fx-font: bold 20 arial; -fx-base: #fbb040; -fx-background-radius: 10 10 10 10; -fx-text-fill: white");

        _listLayout = new HBox(30);
        _listLayout.getChildren().addAll(_categoryLayout, _wordListView, _createListButton);
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
            _buttonSound.stop();
            _buttonSound.play();
            Stage mainStage = (Stage)_newListButton.getScene().getWindow();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose a Spelling List");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            File selectedFile = fileChooser.showOpenDialog(mainStage);//pass reference to window
            if (selectedFile != null) {
                String fileName = selectedFile.getName();
                String filePath = selectedFile.getPath();
                _model.getMasterModel().addAddress(fileName, filePath);
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
        _listCombo.setOnAction(e->{
            String spellingList = _listCombo.getSelectionModel().getSelectedItem();
            _model.saveData();
            _model.newList(spellingList);
            setComboBoxLayout();
            setViewListLayout();
            _mainLayout.setTop(_comboBoxLayout);
            _mainLayout.setCenter(_listLayout);
            setEventHandlers();
        });

        _createListButton.setOnAction(e->{
            _buttonSound.stop();
            _buttonSound.play();
            ListCreatorScene customList = new ListCreatorScene(_model.getMasterModel(), _model);
            if(customList.display()){
                setComboBoxLayout();
                setViewListLayout();
                _mainLayout.setTop(_comboBoxLayout);
                _mainLayout.setCenter(_listLayout);
                setEventHandlers();
            }

        });
    }

    protected MediaPlayer createSound(String address){
        final URL resource = getClass().getResource(address);
        final Media media = new Media(resource.toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        return mediaPlayer;
    }
}
