package VoxspellApp.StartScenes;


import VoxspellApp.Popups.HelpWindow;
import VoxspellApp.SpellingQuizScene;
import VoxspellApp.StatisticsScene;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Festival;
import models.WordModel;

import java.net.URL;
import java.util.Set;

/**
 * Created by edson on 15/09/16.
 *
 * Initial scene of the game. Sets all the windows the user will see during the initial phases
 * of the program, and deals with setting up all the data structures that are relvant to the program.
 * It is detached with the game itself.
 * responsible for creating and handling interaction with the initial window of the game.
 */
public class InitialScene {
    private Stage _window;
    private Scene _mainScene;//background scene of primary window
    private BorderPane _mainLayout;

    //sounds
    private MediaPlayer _mediaPlayer;
    private MediaPlayer _buttonSound;
    private MediaPlayer _toggleButtonSound;

    private boolean _review = false;


    //Buttons
    private ToggleGroup _menuGroup;
    private ToggleButton _newGameButton;
    private ToggleButton _reviewGameButton;
    private ToggleButton _statisticsButton;
    private ToggleButton _viewWordsButton;
    private ToggleButton _resetButton;
    private Button playButton;
    private Button _helpButton;

    private int _helpStatus;//status of the help control. based on the int, will show different help tutorial.
    private int _level;
    private Mode _mode = Mode.NEW;
    protected enum Mode{NEW, REVIEW};

    private WordModel _model;

    private ComboBox _voiceOptionCombo;
    private ComboBox<String> listCombo;

    /**
     * creates an initial scene based on the window and models provided.
     * Stylizes all the controls.
     * @param window window to set the initial scene
     * @param model data structure holding all the relevant information of words
     */
    public InitialScene(Stage window, WordModel model){
        _model = model;
        _window = window;

        playButton = new Button("PLAY");
        playButton.setStyle("-fx-font: bold 64 latoheavy; -fx-base: #fbb040; -fx-background-radius: 10 10 10 10; -fx-text-fill:  white;");
        playButton.setPrefSize(325, 285);

        _helpButton = new Button("?");
        _helpButton.setStyle("-fx-font: bold 30 latoheavy; -fx-base: #1db361; " +
                "-fx-background-radius: 40 40 40 40; -fx-text-fill:  white; -fx-border: 20px; -fx-border-color: white; -fx-border-radius: 40");
        _helpStatus=0;

        VBox menuSceneLayout = setMenuScene();//sets the left-hand side menu panel
        GridPane gameSceneLayout = setGameScene();//sets the right-hand side main

        //set all scenes into the main scene
        _mainLayout = new BorderPane();
        _mainLayout.setLeft(menuSceneLayout);
        _mainLayout.setCenter(gameSceneLayout);
        //mainLayout.setRight()

        //set the background image
        BackgroundImage menuBackground = new BackgroundImage(new Image("MediaResources/background.png", 1040, 640, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        _mainLayout.setBackground(new Background(menuBackground));

        _mainScene = new Scene(_mainLayout, 1040, 640);
        _mainScene.getStylesheets().add("VoxspellApp/LayoutStyles");//add the css style-sheet to the main menu scene

        //create sound effects
        _toggleButtonSound = createSound("/MediaResources/SoundFiles/264388__magedu__toilet-flushing-button.wav");
        _buttonSound = createSound("/MediaResources/SoundFiles/264447__kickhat__open-button-2.wav");




    }

    /**
     * creates the sound effects helper function
     * @param address address of the .wav file
     * @return media player wrapping the wav file
     */
    protected MediaPlayer createSound(String address){
        final URL resource = getClass().getResource(address);
        final Media media = new Media(resource.toString());
        _mediaPlayer = new MediaPlayer(media);
        return _mediaPlayer;
    }

    /**
     * creates the scene based on the information from the constructor.
     * Returns the scene to be set.
     * Also plays the relevant media.
     * @return
     */
    @SuppressWarnings("fallthrough")
    public Scene createScene(){
        //https://www.freesound.org/people/jmggs@hotmail.com/sounds/195355/
        final URL resource = getClass().getResource("/MediaResources/SoundFiles/195355__jmggs-hotmail-com__music-box.wav");
        final Media media = new Media(resource.toString());
        _mediaPlayer = new MediaPlayer(media);
        _mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                _mediaPlayer.seek(Duration.ZERO);
            }
        });
        _mediaPlayer.play();
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
        _viewWordsButton = createMenuButtons("MediaResources/newGame.png", "VIEW WORDS");


        menuSceneLayout.setPadding(new Insets(10));//insets: top right bottom left
        menuSceneLayout.getChildren().addAll(_newGameButton, _reviewGameButton, _statisticsButton, _viewWordsButton, _resetButton);
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
        //glow effects
        newButton.setOnMouseEntered(e->{
            DropShadow glow = new DropShadow();
            glow.setRadius(30);
            glow.setColor(Color.GREEN);
            newButton.setEffect(glow);
        });
        newButton.setOnMouseExited(e->{
            newButton.setEffect(null);
        });
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

        //set up the level buttons
        for(int i = 1; i<12; i++){
            Label space = new Label("  ");
            GridPane.setConstraints(space, i, 0);
            gameGrid.getChildren().add(space);
        }

        GridPane.setConstraints(_helpButton, 12, 0);

        ToggleGroup levelToggles = setLevelButtons(_model.getTotalLevels(), gameGrid);

        Label voiceLabel = new Label("Voice");
        voiceLabel.setStyle("-fx-font: bold 25 latoheavy; -fx-text-fill:  white;");
        voiceLabel.setAlignment(Pos.CENTER);
        GridPane.setConstraints(voiceLabel, 0, 3);

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
            startFestivalThread("Meow");
        });
        GridPane.setConstraints(_voiceOptionCombo, 0, 4);

        Label listLabel = new Label("Spelling List");
        listLabel.setStyle("-fx-font: bold 25 latoheavy; -fx-text-fill:  white;");
        listLabel.setAlignment(Pos.CENTER);
        GridPane.setConstraints(listLabel, 0, 5);

        //make the combobox for spelling list
        ObservableList<String> options = FXCollections.observableArrayList();
        listCombo = new ComboBox<String>(options);
        Set<String> listSet = _model.getMasterModel().getDictionaryKeyset();
        for (String list : listSet){
            listCombo.getItems().add(list);
        }
        listCombo.setValue(_model.getTitle());
        listCombo.setStyle("-fx-font: 20 latoheavy; -fx-background-radius: 20 20 20 20");
        GridPane.setConstraints(listCombo, 0, 6);

        gameGrid.getChildren().addAll(levelLabel, voiceLabel, _voiceOptionCombo, listLabel, listCombo, _helpButton);//removed newtip

        GridPane.setConstraints(playButton, 0, 10);
        gameGrid.getChildren().add(playButton);
        setupEventHandlers();
        return gameGrid;

    }


    /**
     * helper function to create the level buttons
     * @param maxLevel max level available in the spelling list
     * @param gameGrid the grid pane that stores the levels
     * @return togglegroup containing all the levels
     */
    private ToggleGroup setLevelButtons(int maxLevel, GridPane gameGrid){
        HBox levelHBox = new HBox();
        levelHBox.setSpacing(5);
        ToggleGroup levelGroup = new ToggleGroup();
        boolean reviewExists = false;
        //loop through all the levels and create buttons
        for (int i = 1; i <maxLevel+1 ; i++){
            ToggleButton levelButton = new ToggleButton();
            levelButton.setPrefHeight(50);
            levelButton.setPrefWidth(50);
            levelButton.setText("" + i);
            levelButton.setUserData(i);
            levelButton.setStyle("-fx-font: 18 arial;-fx-background-radius: 25 25 25 25;");
            //upon button click, update model's level
            levelButton.setOnAction(e->{
                _toggleButtonSound.stop();
                _toggleButtonSound.play();
                _model.updateLevel(Integer.parseInt(levelButton.getText()));
            });
            //glow effects stylzing
            levelButton.setOnMouseEntered(e->{
                DropShadow glow = new DropShadow();
                glow.setRadius(18);
                glow.setColor(Color.GREEN);
                levelButton.setEffect(glow);
            });
            levelButton.setOnMouseExited(e->{
                DropShadow glow = new DropShadow();
                levelButton.setEffect(null);
            });

            if (i ==1){
                levelButton.setSelected(true);
                _model.updateLevel(1);
            }
            //disable if user has no access to level
            //if (i > _model.getAccessLevel()){
            //    levelButton.setDisable(true);
            //}

            //if review function then disable the button if there are no failed list
            if (_review) {
                if (_model.getLevel(i-1).getFailedList().size() == 0) {
                    levelButton.setDisable(true);
                } else if (!reviewExists) {//otherwise enable the level button
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

        //set the default level to 1 otherwise choose based on user selection
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

    /**
     * helper function to set up all the event handlers.
     */
    private void setupEventHandlers(){
        //new game scene sets the game functioanltiy to normal game
        _newGameButton.setOnAction(event -> {
            _helpStatus=0;
            _toggleButtonSound.stop();
            _toggleButtonSound.play();
            _review = false;
            _mainLayout.setCenter(setGameScene());
            _mode=Mode.NEW;
        });
        //review game scene sets game functionality to review game
        _reviewGameButton.setOnAction(event -> {
            _helpStatus=1;
            _toggleButtonSound.stop();
            _toggleButtonSound.play();
            _review = true;
            _mainLayout.setCenter(setGameScene());
            _mode=Mode.REVIEW;
        });
        //switch the statistics to statistics scene by creating a StatisticsScene
        //and replacing the centre scene with stats
        _statisticsButton.setOnAction(event -> {
            _helpStatus=2;
            _toggleButtonSound.stop();
            _toggleButtonSound.play();
            StatisticsScene graphScene = new StatisticsScene(_model);
            _mainLayout.setCenter(graphScene.createScene());//set center pane to the StatisticsScene's layout node
        });
        //switch the centre scene to view words scene where user can change the file or
        //make their own one.
        _viewWordsButton.setOnAction(event -> {
            _helpStatus=3;
            _toggleButtonSound.stop();
            _toggleButtonSound.play();
            FileChooserScene chooserScene = new FileChooserScene(_model);
            _mainLayout.setCenter(chooserScene.getLayout());
        });

        //switch the centre scene to a clear history functionality.
        //here, the user can clear the history for that particular spelling list.
        //this means all the relevant statistics are cleared and the user is essentially
        //playing a new game for that spelling list.
        _resetButton.setOnAction(event -> {
            _helpStatus=4;
            //sfx
            _toggleButtonSound.stop();
            _toggleButtonSound.play();
            //set stylizing and layouts.
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

            //recreate the word model by resetting all its statistics data for that spelling list
            confirmButton.setOnAction(e->{
                _buttonSound.stop();
                _buttonSound.play();
                _model.recreate();
                caption4.setVisible(true);
            });
            resetVbox.getChildren().addAll(title,  rsImageContainer,caption1,caption2,caption3, confirmButton, caption4);
            _mainLayout.setCenter(resetVbox);
        });
        //glow effects
        playButton.setOnMouseEntered(e->{
            DropShadow glow = new DropShadow();
            glow.setRadius(40);
            glow.setColor(Color.ORANGE);
            playButton.setEffect(glow);
        });
        playButton.setOnMouseExited(e->{
            playButton.setEffect(null);
        });
        //switches the whole initial scene to the spelling quiz scene where the actual game is played.
        //uses all the relevant data that is set prior.
        playButton.setOnAction(event ->{
            _mediaPlayer.stop();
            _buttonSound.stop();
            _buttonSound.play();
            SpellingQuizScene newGameSceneCreator = new SpellingQuizScene(_model, _window, _review);
            Scene newGameScene = newGameSceneCreator.createScene();
            _window.setScene(newGameScene);
        });
        listCombo.setOnAction(event -> {
            String spellingList = listCombo.getSelectionModel().getSelectedItem();
            _model.saveData();
            _model.newList(spellingList);
            //refresh page
            GridPane gameSceneLayout = setGameScene();
            _mainLayout.setCenter(gameSceneLayout);
            setupEventHandlers();

        });
        //help button displays the help window using the status of the help, represented
        //by ints.
        _helpButton.setOnAction(e->{
            HelpWindow help = new HelpWindow(_helpStatus);
            help.display();
        });
    }

    /**
     * background thread for saying a phrase via tts
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
