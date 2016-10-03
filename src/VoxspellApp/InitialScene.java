package VoxspellApp;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Festival;
import models.WordModel;

/**
 * Created by edson on 15/09/16.
 *
 * responsible for creating and handling interaction with the initial window of the game.
 */
public class InitialScene {
    private Stage _window;
    private Scene _mainScene;//background scene of primary window
    private BorderPane _mainLayout;
    private boolean _review = false;

    //Buttons
    private ToggleGroup _menuGroup;
    private ToggleButton _newGameButton;
    private ToggleButton _reviewGameButton;
    private ToggleButton _statisticsButton;
    private ToggleButton _resetButton;
    private Button playButton;

    private Label tipLabel1;
    private Label tipLabel2;
    private Label tipLabel3;
    private Label tipLabel4;
    private VBox _newTip = new VBox();

    private int _level;
    private Mode _mode = Mode.NEW;
    protected enum Mode{NEW, REVIEW};

    private WordModel _model;

    private ComboBox _voiceOptionCombo;

    public InitialScene(Stage window, WordModel model){
        _model = model;
        _window = window;

        playButton = new Button("PLAY");
        playButton.setStyle("-fx-font: bold 64 latoheavy; -fx-base: #fbb040; -fx-background-radius: 10 10 10 10; -fx-text-fill:  white;");
        playButton.setPrefSize(325, 285);

        VBox menuSceneLayout = setMenuScene();//sets the left-hand side menu panel
        GridPane gameSceneLayout = setGameScene();//sets the right-hand side main

        //set all scenes into the main scene
        _mainLayout = new BorderPane();
        _mainLayout.setLeft(menuSceneLayout);
        _mainLayout.setCenter(gameSceneLayout);
        //mainLayout.setRight()

        BackgroundImage menuBackground = new BackgroundImage(new Image("MediaResources/background.png", 1040, 640, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        _mainLayout.setBackground(new Background(menuBackground));

        _mainScene = new Scene(_mainLayout, 1040, 640);
        _mainScene.getStylesheets().add("VoxspellApp/LayoutStyles");//add the css style-sheet to the main menu scene



        setupEventHandlers();

    }

    protected Scene createScene(){
        return _mainScene;
    }
    /**
     * Logic for setting up the left-side menu scene (specific for main entry menu only).
     * @return menu vbox layout
     */
    private VBox setMenuScene(){
        VBox menuSceneLayout = new VBox();
        menuSceneLayout.setPrefWidth(200);//set width of menu buttons
        _menuGroup = new ToggleGroup();
        //http://docs.oracle.com/javafx/2/ui_controls/button.htm
        _newGameButton = createMenuButtons("MediaResources/newGame.png", "NEW");
        _reviewGameButton = createMenuButtons("MediaResources/newGame.png", "REVIEW");
        _statisticsButton = createMenuButtons("MediaResources/newGame.png", "STATISTICS");
        _resetButton = createMenuButtons("MediaResources/newGame.png", "RESET");

        menuSceneLayout.setPadding(new Insets(10));//insets: top right bottom left
        menuSceneLayout.getChildren().addAll(_newGameButton, _reviewGameButton, _statisticsButton, _resetButton);
        menuSceneLayout.getStyleClass().add("vbox");//add the custom vbox layout style


        return menuSceneLayout;
    }



    /**
     * Create menu buttons for the menu scene
     * @param imageName image filepath
     * @param caption button caption
     * @return button node
     */
    private ToggleButton createMenuButtons(String imageName, String caption){
        //Image newGameIcon = new Image(imageName, 120, 100, false, false);//size of image
        //ToggleButton newButton = new ToggleButton(caption, new ImageView(newGameIcon));
        ToggleButton newButton = new ToggleButton(caption);
        newButton.setPrefWidth(180);
        newButton.setPrefHeight(140);
        newButton.setStyle("-fx-font: bold 20 arial; -fx-base: #1db361; -fx-background-radius: 10 10 10 10; -fx-text-fill: white");
        newButton.setContentDisplay(ContentDisplay.TOP);
        newButton.setToggleGroup(_menuGroup);
        if (caption.equals("NEW")){
            newButton.setSelected(true);
        }
        return newButton;
    }



    /**
     * Logic for the main game scene of the main entry window
     * thinking of reusing for settings popup window
     * @return main game scene as a gridPane
     */
    //we may want to reuse this for settings page
    private GridPane setGameScene(){
        GridPane gameGrid = new GridPane();
        gameGrid.setPadding(new Insets(30));
        gameGrid.setVgap(20);
        gameGrid.setHgap(5);

        Label levelLabel = new Label("Level");
        levelLabel.setStyle("-fx-font: bold 25 laotheavy; -fx-text-fill:  white;");
        levelLabel.setAlignment(Pos.CENTER);
        GridPane.setConstraints(levelLabel, 0, 0);

        ToggleGroup levelToggles = setLevelButtons(_model.getTotalLevels(), gameGrid);

        Label voiceLabel = new Label("Voice");
        voiceLabel.setStyle("-fx-font: bold 25 latoheavy; -fx-text-fill:  white;");
        voiceLabel.setAlignment(Pos.CENTER);
        GridPane.setConstraints(voiceLabel, 0, 3);

        _newTip.setStyle("-fx-font: 16 arial");

        if (_review) {
            setUpReviewToolTip();
        } else {
            setUpNewToolTip();
        }

        tipLabel4.setStyle("-fx-text-fill: white");
        tipLabel2.setStyle("-fx-text-fill: white");
        tipLabel3.setStyle("-fx-text-fill: white");
        tipLabel1.setStyle("-fx-text-fill: white");

        GridPane.setConstraints(_newTip, 0, 5);

        //set up combo box for choosing levels
        _voiceOptionCombo = new ComboBox<String>();
        _voiceOptionCombo.getItems().addAll(
                Festival.getVoiceList()
        );
        _voiceOptionCombo.setStyle("-fx-font: 20 latoheavy; -fx-background-radius: 20 20 20 20");
        _voiceOptionCombo.setPrefWidth(275);
        _voiceOptionCombo.setValue(Festival._getVoice());
        _voiceOptionCombo.setOnAction(event -> {
            String option = (String)_voiceOptionCombo.getValue();
            Festival.changeVoice(option);
        });
        GridPane.setConstraints(_voiceOptionCombo, 0, 4);

        gameGrid.getChildren().addAll(levelLabel, voiceLabel, _voiceOptionCombo, _newTip);

        GridPane.setConstraints(playButton, 0, 10);
        gameGrid.getChildren().add(playButton);
        return gameGrid;

    }

    private void setUpReviewToolTip() {
        _newTip.getChildren().removeAll(tipLabel1,tipLabel2,tipLabel3,tipLabel4);
        tipLabel1 = new Label("Tool Tip:");
        tipLabel2 = new Label("This is the Review Spelling Quiz. Here we test you on any words you have failed in each level");
        tipLabel3 = new Label("Please pick a level and then press PLAY to start that level. If all levels are locked, then");
        tipLabel4 = new Label("you need to attempt a spelling quiz before coming back here");
        _newTip.getChildren().addAll(tipLabel1,tipLabel2,tipLabel3,tipLabel4);

    }

    private void setUpNewToolTip() {
        _newTip.getChildren().removeAll(tipLabel1,tipLabel2,tipLabel3,tipLabel4);
        tipLabel1 = new Label("Tool Tip:");
        tipLabel2 = new Label("This is the New Spelling Quiz. Here we test you on 10 words from each corresponding level");
        tipLabel3 = new Label("Please pick a level and then press PLAY to start that level. If you wish to change the voice,");
        tipLabel4 = new Label("click the drop down menu next to voice and then pick your voice");
        _newTip.getChildren().addAll(tipLabel1,tipLabel2,tipLabel3,tipLabel4);
    }

    public Button getPlayButton(){
        return playButton;
    }

    private ToggleGroup setLevelButtons(int maxLevel, GridPane gameGrid){
        HBox levelHBox = new HBox();
        levelHBox.setSpacing(5);
        ToggleGroup levelGroup = new ToggleGroup();
        boolean reviewExists = false;
        for (int i = 1; i <maxLevel+1 ; i++){
            ToggleButton levelButton = new ToggleButton();
            levelButton.setPrefHeight(50);
            levelButton.setPrefWidth(50);
            levelButton.setText("" + i);
            levelButton.setUserData(i);
            levelButton.setStyle("-fx-font: 18 arial;-fx-background-radius: 25 25 25 25");
            //upon button click, update model's level
            levelButton.setOnAction(e->{
                _model.updateLevel(Integer.parseInt(levelButton.getText()));
            });

            if (i ==1){
                levelButton.setSelected(true);
                _model.updateLevel(1);
            }
            //disable if user has no access to level
            //if (i > _model.getAccessLevel()){
            //    levelButton.setDisable(true);
            //}

            if (_review) {
                if (_model.getLevel(i-1).getFailedList().size() == 0) {
                    levelButton.setDisable(true);
                } else if (!reviewExists) {
                    reviewExists = true;
                    levelButton.setSelected(true);
                    _model.updateLevel(i);
                }
            }

            levelButton.setToggleGroup(levelGroup);
            levelHBox.getChildren().add(levelButton);

        }

        if (!reviewExists && _review) {
            playButton.setDisable(true);
        } else {
            playButton.setDisable(false);
        }

        levelGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue==null){
                    _level=1;//default level
                } else {
                    _level = (int) levelGroup.getSelectedToggle().getUserData();//set level via button select
                }
            }
        });
        GridPane.setConstraints(levelHBox, 0, 1);
        gameGrid.getChildren().add(levelHBox);
        return levelGroup;
    }

    private void setupEventHandlers(){
        _newGameButton.setOnAction(event -> {
            _review = false;
            _mainLayout.setCenter(setGameScene());
            _mode=Mode.NEW;
        });
        _reviewGameButton.setOnAction(event -> {
            _review = true;
            _mainLayout.setCenter(setGameScene());
            _mode=Mode.REVIEW;
        });
        _statisticsButton.setOnAction(event -> {
            StatisticsScene graphScene = new StatisticsScene(_model);
            _mainLayout.setCenter(graphScene.createScene());//set center pane to the StatisticsScene's layout node
        });
        _resetButton.setOnAction(event -> {
            final VBox resetVbox = new VBox(20);
            resetVbox.setPadding(new Insets(40,50,40,40));
            resetVbox.setAlignment(Pos.TOP_CENTER);
            Label title = new Label("Clear History");
            title.setStyle("-fx-font: bold 30 arial; -fx-text-fill: white;");
            Image resetImage = new Image("MediaResources/clearhistory.png", 330, 250, false, true);
            ImageView rsImageContainer = new ImageView(resetImage);
            Label caption1 = new Label("Clearing the history will remove all history statistics.");
            caption1.setStyle("-fx-font: 15 arial; -fx-text-fill: white");
            Label caption2 = new Label("The accuracy rates for all the words you attempted to spell will be lost.");
            caption2.setStyle("-fx-font: 15 arial; -fx-text-fill: white");
            Label caption3 = new Label("Are you sure you want to clear the history?");
            caption3.setStyle("-fx-font: 15 arial; -fx-text-fill: white");
            final Label caption4 = new Label("History Successfully Cleared.");
            caption4.setStyle("-fx-font: bold 15 arial;-fx-text-fill: white");
            caption4.setVisible(false);
            Button confirmButton = new Button("Clear History");
            confirmButton.setStyle("-fx-font: bold 15 arial; -fx-background-radius: 10 10 10 10");

            confirmButton.setOnAction(e->{
                _model.recreate();
                caption4.setVisible(true);
            });
            resetVbox.getChildren().addAll(title,  rsImageContainer,caption1,caption2,caption3, confirmButton, caption4);
            _mainLayout.setCenter(resetVbox);
        });
        playButton.setOnAction(event ->{
            SpellingQuizScene newGameSceneCreator = new SpellingQuizScene(_model, _window, _review);
            Scene newGameScene = newGameSceneCreator.createScene();
            _window.setScene(newGameScene);
        });
    }




}
