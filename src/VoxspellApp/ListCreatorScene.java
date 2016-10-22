package VoxspellApp;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Level;
import models.MasterModel;
import models.Word;
import models.WordModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by edson on 17/10/16.
 */
public class ListCreatorScene {

    private Stage window = new Stage();

    private MasterModel _master;
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
    private StringBuilder _sb = new StringBuilder();

    private String _wordToDelete=null;
    private String _categoryToDelete = null;

    //Menu buttons
    private Button _save;
    private Button _back;
    private boolean _isSaved;

    StringBuilder output;

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


        setupEventHandlers();

    }


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
        window.showAndWait();

        return _isSaved;
    }

    private void closeProgram(){
        ConfirmQuitBox confirm = new ConfirmQuitBox();
        if(confirm.display("Quit Create List", "All unsaved changes will be lost. Continue?")){
            window.close();
        }
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

        _save.setOnAction(e->{
            boolean isSuccessful = true;
            output = new StringBuilder();
            if (_categoryList.size()==0){
                WarningBox wb = new WarningBox();
                wb.display("Save Spelling List", "Not enough categories. Please add more categories.");
            } else {
                for (String category : _categoryList){
                    output.append("%"+category+"\n");
                    //if not enough words, set warning and do nothing
                    if(_levelMap.get(category).size()<10){
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

        _back.setOnAction(e->{
            if(_isSaved){
                String filePath="CustomFile.txt";
                File file = new File(filePath);
                int i = 1;

                // if file doesnt exists, then create it
                while(file.exists()){
                    filePath ="CustomFile"+i+".txt";
                    file = new File(filePath);

                    i++;
                }
                try {
                    file.createNewFile();
                    FileWriter fw = new FileWriter(file.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(output.toString().trim());
                    bw.close();
                    _model.getMasterModel().addAddress(filePath, filePath);
                    _model.newList(filePath);
                } catch (IOException e1) {
                    WarningBox wb = new WarningBox();
                    wb.display("Save Error", "Spelling list could not be created.");
                }
            }
            closeProgram();
        });

    }



}
