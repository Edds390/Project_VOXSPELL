package VoxspellApp.StartScenes;

import VoxspellApp.Popups.ConfirmQuitBox;
import VoxspellApp.Popups.WarningBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.MasterModel;
import models.WordModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by edson on 17/10/16.
 * Represents the List Creator window, where the user can make their own custom
 * spelling list using this window. Using the new custom list, this class will
 * prompt the main window to switch to the new spelling list.
 * If the custom spelling list is invalid, then the list creator will not prompt
 * for a spelling list change.
 */
public class ListCreatorScene {

    private Stage window = new Stage();

    private MasterModel _master;
    private WordModel _model;//stores data needed by this class
    private Map<String, ObservableList<String>> _levelMap;//maps the category name to the levels
    private ObservableList<String> _categoryList;//stores all the categories
    private ObservableList<String> _wordList;//stores all the words of the current category

    private BorderPane _mainLayout;
    private HBox _listBox;

    //ListViews
    private ListView<String> _wordListView;
    private ListView<String> _categoryListView;

    //textFields
    private TextField _categoryInput;
    private TextField _wordInput;

    //submit buttons
    private Button _submitWord;
    private Button _submitCategory;
    private Button _deleteCategory;
    private Button _deleteWord;
    //counters;
    private int _categoryCount = 0;
    private int _wordCount = 0;

    //output
    private StringBuilder _sb = new StringBuilder();

    private String _wordToDelete=null;
    private String _categoryToDelete = null;

    //Menu buttons
    private Button _save;
    private Button _back;
    private boolean _isSaved;

    StringBuilder output;
    private MediaPlayer _sound;

    /**
     * constructor which stores the data it needs and sets up the layouts that is to be set, as
     * well as the stylizing of those layouts.
     * @param master the master model
     * @param model the word model that is to be updated.
     */
    public ListCreatorScene(MasterModel master, WordModel model){
        _master = master;
        _model = model;

        Label title = new Label("Make your own spelling quiz!");
        title.setStyle("-fx-font: bold 24 arial; -fx-text-fill: white; -fx-underline: true");
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);

        createListViews();
        _categoryList = FXCollections.observableArrayList();
        _wordList = FXCollections.observableArrayList();
        _levelMap= new HashMap<String, ObservableList<String>>();



        _mainLayout = new BorderPane();
        _mainLayout.setStyle("-fx-base: #1d194b;");
        _mainLayout.setTop(titleBox);
        _mainLayout.setCenter(_listBox);

        //sfx
        final URL resource = getClass().getResource("/MediaResources/SoundFiles/264447__kickhat__open-button-2.wav");
        final Media media = new Media(resource.toString());
        _sound = new MediaPlayer(media);


        setupEventHandlers();

    }


    /**
     * displays the window and waits for the window to close.
     * Returns a prompt to the main window to whether or not to change the current word model and
     * replace with the user's custom list.
     * @return if true, update the main window with the new custom list. else, do nothing.
     */
    public boolean display(){
        _isSaved=false;

        window.initModality(Modality.APPLICATION_MODAL);//modality for suppressing main window
        window.setTitle("New Spelling List");
        window.setMinWidth(500);
        window.setResizable(false);

        window.setOnCloseRequest(e -> {
            e.consume();//suppress user request
            closeProgram();//replace with our own close implementation
        });

        Scene scene = new Scene(_mainLayout);
        window.setScene(scene);
        window.showAndWait();//wait for user input

        return _isSaved;//user has successfully saved the custom list and followed all specifications
    }

    /**
     * closes program and warns beforehand. If user decides to go back to creating more words, has that
     * option too.
     */
    private void closeProgram(){
        ConfirmQuitBox confirm = new ConfirmQuitBox();
        if(confirm.display("Quit Create List", "All unsaved changes will be lost. Continue?")){
            window.close();
        }
    }

    /**
     * creates the list views that show all the words and categories that the user has inputted.
     * It's a helper function.
     */
    private void createListViews(){
        _listBox = new HBox();
        VBox categoryBox = new VBox();
        VBox wordBox = new VBox();

        _categoryListView = new ListView<String>();
        _wordListView = new ListView<String>();

        //stylizing and layouts.
        HBox categorySubmitBox = new HBox();
        HBox wordSubmitBox = new HBox();
        _wordInput = new TextField();
        _categoryInput = new TextField();
        _submitCategory = new Button("+");
        _submitCategory.setStyle("-fx-base: #fbb040;");
        _deleteCategory = new Button("-");
        _deleteCategory.setStyle("-fx-base: #fbb040;");
        _submitWord = new Button("+");
        _submitWord.setStyle("-fx-base: #fbb040;");
        _deleteWord = new Button("-");
        _deleteWord.setStyle("-fx-base: #fbb040;");
        _deleteCategory.setDisable(true);
        _deleteWord.setDisable(true);
        _submitWord.setDisable(true);
        categorySubmitBox.getChildren().addAll(_categoryInput, _submitCategory, _deleteCategory);
        wordSubmitBox.getChildren().addAll(_wordInput,_submitWord, _deleteWord);

        wordBox.getChildren().addAll(_wordListView, wordSubmitBox);
        categoryBox.getChildren().addAll(_categoryListView, categorySubmitBox);

        //create menu buttons
        VBox menuButtons = createButtonLayout();


        _listBox.getChildren().addAll(categoryBox,wordBox, menuButtons);
        _listBox.setAlignment(Pos.CENTER);
        _listBox.setPadding(new Insets(30));


    }

    /**
     * helper function to create the button layout of save and back
     * @return vbox storing the buttons
     */
    private VBox createButtonLayout(){
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(5));
        vbox.setAlignment(Pos.CENTER);
        _save = createButtons("SAVE");
        _back = createButtons("BACK");
        vbox.getChildren().addAll(_save, _back);
        return vbox;
    }

    /**
     * helper function to create and stylize the buttons for Save and Back.
     * @param text caption of the button
     * @return button itself stylized.
     */
    private Button createButtons(String text){
        Button newButton = new Button(text);
        newButton.setPrefWidth(180);
        newButton.setPrefHeight(140);
        newButton.setStyle("-fx-font: bold 20 arial; -fx-base: #fbb040; -fx-background-radius: 10 10 10 10; -fx-text-fill: white");
        return newButton;
    }

    /**
     * helper function to set up the event handlers for this window.
     */
    private void setupEventHandlers(){
        //Checks if the user's new custom list is valid for our game.
        //if the category name is too long, or no categories are inputted, or a category with
        //the same name already exists, then a warning box is shown telling the user to change his
        //custom list.
        _submitCategory.setOnAction(e -> {
            _sound.stop();
            _sound.play();
            String input = _categoryInput.getText();
            if (input.length() > 15) {//category name is too long
                WarningBox wb = new WarningBox();
                wb.display("New Category", "Category name is too long. Please shorten the name.");
            } else if (input.length() <= 0) {//no categories are inputted
                WarningBox wb = new WarningBox();
                wb.display("New Category", "Empty category name. Please add a word as a category.");

            } else if (_levelMap.get(input)!=null){//category already exists
                _categoryListView.getSelectionModel().select(input);
                WarningBox wb = new WarningBox();
                wb.display("New Category", "Category already exists.");
                _categoryInput.clear();
            }else {//meets specifications so add the category to te list view
                _categoryList.add(input);
                ObservableList<String> wordList = FXCollections.observableArrayList();
                _levelMap.put(input, wordList);
                _categoryListView.setItems(_categoryList);
                _categoryListView.getSelectionModel().select(input);
                _categoryInput.clear();
            }

        });

        //enables category to be selected so the user can delete that category.
        _categoryListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                _wordListView.setItems(_levelMap.get(newValue));
                _wordList = _levelMap.get(newValue);
                _deleteCategory.setDisable(false);
                _submitWord.setDisable(false);
                _categoryToDelete = newValue;
            }
        });

        //lets the user submit a word for a particular category.
        //checks if the user has inputted the right/correct format of the word.
        _submitWord.setOnAction(e->{
            _sound.stop();
            _sound.play();
            String input = _wordInput.getText();
            //only letters are allowed. no numbers or spaces etc
            if (!input.matches("[a-zA-Z]+")) {
                WarningBox wb = new WarningBox();
                wb.display("New Word", "Please enter only letters.");
            } else if (_wordList.contains(input)){//word already exists for that particular level
                WarningBox wb = new WarningBox();
                wb.display("New Word", "Word already exists!");
            } else {//word is valid so lets user input it in.
                _wordList.add(input);
                _wordListView.setItems(_wordList);
            }
            _wordInput.clear();
        });

        //allows the user to select a word so that he can delete the word if wanting to.
        _wordListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                _deleteWord.setDisable(false);
                _wordToDelete=newValue;
            }
        });

        //deletes the word from the word list of that particulcar category
        _deleteWord.setOnAction(e->{
            _sound.stop();
            _sound.play();
            _wordList.remove(_wordToDelete);
            _wordListView.setItems(_wordList);
            if (_wordList.size()==0){
                _deleteWord.setDisable(true);
            }
        });

        //deletes the category from the lsit of categories if the user wants to delete it.
        _deleteCategory.setOnAction(e->{
            _sound.stop();
            _sound.play();
            _levelMap.remove(_categoryToDelete);
            _categoryList.remove(_categoryToDelete);
            _categoryListView.setItems(_categoryList);
            if(_categoryList.size()==0){
                _deleteCategory.setDisable(true);
            }
        });

        //saves the custom list but first checks if the custom list is valid.
        //if it is valid, then it prompts the main window to change its word model with the
        //new custom list.
        _save.setOnAction(e->{
            _sound.stop();
            _sound.play();
            boolean isSuccessful = true;
            output = new StringBuilder();
            if (_categoryList.size()==0){//needs at least one category to be valid
                WarningBox wb = new WarningBox();
                wb.display("Save Spelling List", "Not enough categories. Please add more categories.");
            } else {
                for (String category : _categoryList){
                    output.append("%"+category+"\n");
                    //if not enough words, set warning and do nothing
                    if(_levelMap.get(category).size()<10){//each category must have at least 10 words.
                        WarningBox wb = new WarningBox();
                        wb.display("Save Spelling List", "Not enough words. Category "+category+" must have at least 10 words.");
                        isSuccessful = false;
                        break;
                    } else {
                        for (String word : _levelMap.get(category)){
                            output.append(word+"\n");
                        }
                    }
                }
                if (isSuccessful){
                    _isSaved=true;


                }

            }

        });

        //checks whether a valid save has been made. based on that, it will prompt the main program to change the
        //word model with the word model of the new custom list. saves the custom list to a text file.
        //if not valid save has been made, then jsut closes the window back to the main menu.
        _back.setOnAction(e->{
            _sound.stop();
            _sound.play();
            if(_isSaved){
                String filePath="custom_texts/CustomFile1.txt";
                File file = new File(filePath);
                int i = 1;

                // if file doesnt exists, then create it
                while(file.exists()){
                    filePath ="custom_texts/CustomFile"+i+".txt";
                    file = new File(filePath);

                    i++;
                }
                try {//write to a text file
                    file.createNewFile();
                    FileWriter fw = new FileWriter(file.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(output.toString().trim());
                    bw.close();
                    //change the model with the new custom list.
                    _model.getMasterModel().addAddress("CustomFile"+i, filePath);
                    _model.newList("CustomFile"+i);
                } catch (IOException e1) {
                    WarningBox wb = new WarningBox();
                    wb.display("Save Error", "Spelling list could not be created.");
                }
            }
            closeProgram();
        });

    }



}
