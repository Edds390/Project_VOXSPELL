package VoxspellApp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Level;
import models.Word;
import models.WordModel;

import java.util.*;

/**
 * Created by edson on 17/10/16.
 */
public class ListCreatorScene {
    private WordModel _model;
    private Map<String, ObservableList<String>> _levelMap;
    private ObservableList<String> _categoryList;
    private ObservableList<String> _wordList;

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
    StringBuilder _sb = new StringBuilder();

    String _wordToDelete=null;
    String _categoryToDelete = null;

    //Menu buttons
    Button _save;
    Button _back;

    public ListCreatorScene(){


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
        _mainLayout.setTop(titleBox);
        _mainLayout.setCenter(_listBox);


        setupEventHandlers();

    }

    public BorderPane getLayout(){
        return _mainLayout;
    }

    private void createListViews(){
        _listBox = new HBox();
        VBox categoryBox = new VBox();
        VBox wordBox = new VBox();

        _categoryListView = new ListView<String>();
        _wordListView = new ListView<String>();

        HBox categorySubmitBox = new HBox();
        HBox wordSubmitBox = new HBox();
        _wordInput = new TextField();
        _categoryInput = new TextField();
        _submitCategory = new Button("+");
        _deleteCategory = new Button("-");
        _submitWord = new Button("+");
        _deleteWord = new Button("-");
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

    private VBox createButtonLayout(){
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(5));
        vbox.setAlignment(Pos.CENTER);
        _save = createButtons("SAVE");
        _back = createButtons("BACK");
        vbox.getChildren().addAll(_save, _back);
        return vbox;
    }

    private Button createButtons(String text){
        Button newButton = new Button(text);
        newButton.setPrefWidth(180);
        newButton.setPrefHeight(140);
        newButton.setStyle("-fx-font: bold 20 arial; -fx-base: #fbb040; -fx-background-radius: 10 10 10 10; -fx-text-fill: white");
        return newButton;
    }

    private void setupEventHandlers(){
        _submitCategory.setOnAction(e -> {
            String input = _categoryInput.getText();
            if (input.length() > 15) {
                WarningBox wb = new WarningBox();
                wb.display("New Category", "Category name is too long. Please shorten the name.");
            } else if (input.length() <= 0) {
                WarningBox wb = new WarningBox();
                wb.display("New Category", "Empty category name. Please add a word as a category.");

            } else if (_levelMap.get(input)!=null){
                _categoryListView.getSelectionModel().select(input);
                WarningBox wb = new WarningBox();
                wb.display("New Category", "Category already exists.");
                _categoryInput.clear();
            }else {
                _categoryList.add(input);
                ObservableList<String> wordList = FXCollections.observableArrayList();
                _levelMap.put(input, wordList);
                _categoryListView.setItems(_categoryList);
                _categoryListView.getSelectionModel().select(input);
                _categoryInput.clear();
            }

        });

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

        _submitWord.setOnAction(e->{
            String input = _wordInput.getText();
            if (!input.matches("[a-zA-Z]+")) {
                WarningBox wb = new WarningBox();
                wb.display("New Word", "Please enter only letters.");
            } else if (_wordList.contains(input)){
                WarningBox wb = new WarningBox();
                wb.display("New Word", "Word already exists!");
            } else {
                _wordList.add(input);
                _wordListView.setItems(_wordList);
            }
            _wordInput.clear();
        });

        _wordListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                _deleteWord.setDisable(false);
                _wordToDelete=newValue;
            }
        });

        _deleteWord.setOnAction(e->{
            _wordList.remove(_wordToDelete);
            _wordListView.setItems(_wordList);
            if (_wordList.size()==0){
                _deleteWord.setDisable(true);
            }
        });

        _deleteCategory.setOnAction(e->{
            _levelMap.remove(_categoryToDelete);
            _categoryList.remove(_categoryToDelete);
            _categoryListView.setItems(_categoryList);
            if(_categoryList.size()==0){
                _deleteCategory.setDisable(true);
            }
        });

    }



}
