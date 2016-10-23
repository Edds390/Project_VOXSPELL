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
 * The scene which deals with the file viewing and choosing.
 * This scene lets the user choose their own custom files or make their own spelling
 * lists. The scene then shows all the words (separated by categories) as listviews.
 * Hyperlinks show the words for that particular level/category.
 */
public class FileChooserScene {

    private Desktop desktop = Desktop.getDesktop();

    private WordModel _model;//data structure for data handling
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
    private ListView<String> _wordListView;

    //combobox items
    String _filePath;

    private MediaPlayer _buttonSound;


    /**
     * Sets up stylizing and declares all fields.
     * @param model the wordmodel which stores all the information for that level
     */
    public FileChooserScene(WordModel model){
        //sets up the logic for the help button
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

    /**
     * helper function for setting up the combobox
     */
    private void setComboBoxLayout(){
        _comboBoxLayout = new VBox(10);


        HBox topBox = new HBox();
        Label cFile = new Label("Current File: " + _model.getTitle());
        Label space = new Label("\t\t\t\t\t\t\t\t\t\t");
        cFile.setStyle("-fx-font: bold 24 arial; -fx-text-fill: white; -fx-underline: true");
        topBox.getChildren().addAll(cFile, space,_helpButton);

        HBox comboLayout = new HBox(10);

        //combobox. populates the combobox with all the spellling list options that have been imported.
        ObservableList<String> options = FXCollections.observableArrayList();
        _listCombo = new ComboBox<String>(options);
        Set<String> listSet = _model.getMasterModel().getMapKeyset();
        for (String list : listSet){
            _listCombo.getItems().add(list);
        }
        _listCombo.setValue(_model.getTitle());//set the current list as its value
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

    /**
     * helper function for setting up the listviews.
     */
    private void setViewListLayout(){


        _categoryLayout = new VBox(8);
        //extracts all the levels in the wordmodel and populates hyperlinks to see the words
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

    /**
     * helper function for setting up the listviews.
     * Uses the word model to extract all the relevant information to be displayed to the user.
     * @param category level for that particular chosen level
     * @return the words specific for that level
     */
    private ObservableList<String> fillList(String category){
        ObservableList<String> words = FXCollections.observableArrayList();
        Level level = _model.getLevel(category);
        List<Word> wordList = level.getWordList();
        for (Word word : wordList){
            words.add(word.getWord());
        }
        return words;
    }

    /**
     * helper function to set event handlers for the hyperlinks.
     * @param link hyperlink of a level
     */
    private void setAction(Hyperlink link){
        link.setOnAction(e -> {
            ObservableList<String> wordsList = fillList(link.getText());
            _wordListView.setItems(wordsList);
        });
    }

    /**
     * helper function to set up the event handlers
     */
    private void setEventHandlers(){
        //opens up the FileChooser to let the user select their custom files
        _newListButton.setOnAction(e -> {
            _buttonSound.stop();
            _buttonSound.play();
            Stage mainStage = (Stage)_newListButton.getScene().getWindow();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose a Spelling List");
            //filter only the .txt files
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            File selectedFile = fileChooser.showOpenDialog(mainStage);//pass reference to window
            //if the user has selected a valid file, recreate the wordmodel and set that new spelling list
            //as the current spelling list and refresh the window with the new spelling list.
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


            }

        });
        //creates handlers for the combobox.
        //when the user selects a different spelling list, then recreate the wordmodel with
        //the new spelling list. All the relevant statistics will be preserved
        _listCombo.setOnAction(e->{
            String spellingList = _listCombo.getSelectionModel().getSelectedItem();
            _model.saveData();//save the data before changing to a new list
            _model.newList(spellingList);//set the  wordmodel to a new list.
            //refresh the window with the new model
            setComboBoxLayout();
            setViewListLayout();
            _mainLayout.setTop(_comboBoxLayout);
            _mainLayout.setCenter(_listLayout);
            setEventHandlers();
        });

        //custom list creator creates a list creator scene for the user to make his custom list
        _createListButton.setOnAction(e->{
            _buttonSound.stop();
            _buttonSound.play();
            ListCreatorScene customList = new ListCreatorScene(_model.getMasterModel(), _model);
            //using the new custom list, if the new list is valid then replace the word model
            //with the new custom list and refresh the window.
            if(customList.display()){
                setComboBoxLayout();
                setViewListLayout();
                _mainLayout.setTop(_comboBoxLayout);
                _mainLayout.setCenter(_listLayout);
                setEventHandlers();
            }

        });
    }

    /**
     * helepr function for creating sound effects
     * @param address address of sound file
     * @return media player of sound file
     */
    protected MediaPlayer createSound(String address){
        final URL resource = getClass().getResource(address);
        final Media media = new Media(resource.toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        return mediaPlayer;
    }
}
