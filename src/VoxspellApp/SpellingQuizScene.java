package VoxspellApp;

import VoxspellApp.Popups.*;
import VoxspellApp.StartScenes.InitialScene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.SpellingQuiz;
import models.Status;
import models.WordModel;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ratterz on 16/09/16.
 */
public class SpellingQuizScene {

    private SpellingQuiz _quiz = new SpellingQuiz(this);
    private WordModel _wordModel;
    private boolean _review;

    //SCENE
    private Stage _window;
    private Scene _mainScene;

    //PANES
    private VBox _mainLayout = new VBox();
    private HBox _textArea = new HBox();
    private HBox _statusArea = new HBox();
    private HBox _buttonArea = new HBox();
    private HBox _resultsArea = new HBox();
    private VBox _accuracyArea = new VBox();
    private GridPane _gameArea = new GridPane();
    private HBox _overallGameArea;

    //CONGRATS PANE
    private HBox _congratsStatusArea = new HBox();
    private VBox _congratsStatusVBox = new VBox();

    //TEXT
    private TextField _inputText = new TextField();
    private Label _levelTitle = new Label();
    private Label _congratsTitle = new Label();
    private Label _congratsTitle2 = new Label();
    private Label _modeTitle = new Label();
    private Label _accuracyLabel = new Label();
    private Label _accuracyTitle = new Label();
    private Label _accuracyHover = new Label();

    //BUTTONS
    private Button _submitButton = new Button("Submit");
    private Button _startQuizButton = new Button("Start Quiz");
    private Button _settingsButton = new Button("Settings");
    private Button _repeatButton = new Button("Repeat");
    private Button _videoButton  = new Button("Watch Video");
    private Button _stayButton = new Button("Stay");
    private Button _nextLevelButton = new Button("Next Level");
    private Button _mainMenu = new Button("Main Menu");
    private Button _helpButton = new Button("?");

    //STORAGE
    private ArrayList<Circle> _circleList = new ArrayList<Circle>();
    private int _position;
    private int _wordNum;
    private int miceNum;
    private int _miceHowMany;
    private int _numberMastered;
    private double _accuracy = 0;
    private String _savedString = "";

    private MenuPopup _menu;
    private ImageView _miceGroup;



    //IMAGE AND SOUNDS
    Image _loadingIcon = new Image("MediaResources/loaderSpinner.gif", 25, 25, false, false);
    double _submitButtonOpacity;
    private MediaPlayer _mediaPlayer;
    private MediaPlayer _rewardSound;
    private MediaPlayer _failedSound;
    private MediaPlayer _toggleSound;
    private MediaPlayer _buttonSound;

    private final Effect glow = new Glow(1.0);



    /**
     * This is the constructor for the spelling quiz scene. This will call the set up gui method
     * so the gui is set up and also it will call a method to start a new spelling quiz. The wordModel
     * will be passed onto the new spelling quiz.
     * @param wordModel
     */
    public SpellingQuizScene(WordModel wordModel, Stage window, boolean review) {
        final URL resource = getClass().getResource("/MediaResources/SoundFiles/264447__kickhat__open-button-2.wav");
        final Media media = new Media(resource.toString());
        _buttonSound = new MediaPlayer(media);

        _rewardSound = bgm("/MediaResources/SoundFiles/353543__maxmakessounds__happy-theme.wav");
        _failedSound = bgm("/MediaResources/SoundFiles/Meow.m4a");
        //set the functionality of the help button
        _helpButton.setStyle("-fx-font: bold 30 latoheavy; -fx-base: #1db361; " +
                "-fx-background-radius: 40 40 40 40; -fx-text-fill:  white; -fx-border: 20px; -fx-border-color: white; -fx-border-radius: 40");
        _helpButton.setOnAction(e->{
            HelpWindow help = new HelpWindow(2);
            help.display();
        });

        this._wordModel = wordModel;
        this._window = window;
        this._review = review;
        setUpGui();
        setUpEventHandelers();
    }

    private void setUpGui() {
        setUpStatusArea();
        setUpTextArea();
        setUpButtonArea();
        setUpResultsArea();
        setUpGameArea();
        _menu = new MenuPopup();
        _mainLayout.setPadding(new Insets(10,20,20,20));
        _mainLayout.getChildren().addAll(_statusArea,_resultsArea,_overallGameArea,_buttonArea,_textArea);
        BackgroundImage menuBackground = new BackgroundImage(new Image("MediaResources/newgamebackground.png", 1040, 640, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        _mainLayout.setBackground(new Background(menuBackground));

        _mainScene = new Scene(_mainLayout, 1040, 640);
        //_mainScene.getStylesheets().add("VoxspellApp/LayoutStyles");
        final URL resource = getClass().getResource("/MediaResources/SoundFiles/bgm.wav");
        final Media media = new Media(resource.toString());
        _mediaPlayer = new MediaPlayer(media);
        _mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                _mediaPlayer.seek(Duration.ZERO);
            }
        });
        _mediaPlayer.play();

    }

    private void setUpGameArea(){
        _overallGameArea = new HBox();

        _gameArea = new GridPane();
        _gameArea.setPadding(new Insets(60,0,0,0));
        miceNum = _wordModel.getSpellingList(_review).size();
        _wordNum = miceNum-1;
        _miceHowMany = miceNum;
        miceNum--;

        final ImageView catimv = new ImageView();
        final Image catImage = new Image("MediaResources/catplaysflute.png", 250, 250, false, true);
        catimv.setImage(catImage);

        _miceGroup = new ImageView();
        final Image miceImage = new Image("MediaResources/"+_miceHowMany+"mice.png", 150, 150, false, true);
        _miceGroup.setImage(miceImage);
        GridPane.setConstraints(_miceGroup,miceNum,0);

        //set equidistance mice passage based on how many words there are
        //http://stackoverflow.com/questions/22298336/javafx-gridpane-resizing-of-pane-children
        for (int j = 0; j < miceNum; j++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPrefWidth(65);
            _gameArea.getColumnConstraints().add(cc);
        }

        _gameArea.getChildren().addAll(_miceGroup);

        _overallGameArea.getChildren().addAll(catimv,_gameArea);

    }

    public void updateMice(){
        _gameArea.getChildren().remove(_miceGroup);

        final ImageView catimv = new ImageView();
        final Image catImage = new Image("MediaResources/catplaysflute.png", 250, 250, false, true);
        catimv.setImage(catImage);
        GridPane.setConstraints(catimv, 0, 0);

        _miceGroup = new ImageView();
        final Image miceImage = new Image("MediaResources/"+_miceHowMany+"mice.png", 150, 150, false, true);
        _miceGroup.setImage(miceImage);
        GridPane.setConstraints(_miceGroup,miceNum,0);

        _gameArea.getChildren().addAll(_miceGroup);
        _overallGameArea.getChildren().clear();
        _overallGameArea.getChildren().addAll(catimv, _gameArea);

        _mainLayout.getChildren().clear();
        _mainLayout.getChildren().addAll(_statusArea,_resultsArea,_overallGameArea,_buttonArea,_textArea);
    }

    private void setUpTextArea() {
        _textArea.setSpacing(20);
        _textArea.setPadding(new Insets(5));
        _textArea.setAlignment(Pos.CENTER);

        _inputText.setMinWidth(700);
        _inputText.setMinHeight(50);
        _inputText.setText("Press Start Quiz To Start Your Quiz!!");
        _inputText.setStyle("-fx-font: 20 arial; -fx-background-radius: 10 10 10 10;");
        _inputText.setDisable(true);

        _submitButton.setMinWidth(100);
        _submitButton.setMinHeight(50);
        _submitButton.setStyle("-fx-font: bold 18 arial; -fx-base: #fbb040; -fx-background-radius: 10 10 10 10; -fx-text-fill: white");
        _submitButtonOpacity = _submitButton.getOpacity();
        _submitButton.setDisable(true);

        _textArea.getChildren().addAll(_inputText,_submitButton);
    }

    private void setUpStatusArea() {
        _statusArea.setSpacing(10);
        _statusArea.setPadding(new Insets(5));
        _statusArea.setAlignment(Pos.CENTER);

        _levelTitle.setText("Level " + _wordModel.getCurrentLevel());
        _levelTitle.setStyle("-fx-font: bold 40 arial; -fx-base: #fbb040; -fx-text-fill: white");

        Label space2 = new Label("\t\t");
        space2.setStyle("-fx-font: bold 40 arial; -fx-base: #fbb040; -fx-text-fill: white");

        Label space1 = new Label("\t\t\t   ");
        space1.setStyle("-fx-font: bold 40 arial; -fx-base: #fbb040; -fx-text-fill: white");


        if (_review) {
            _modeTitle.setText("Review Quiz");
        } else {
            _modeTitle.setText("New Quiz");
        }
        _modeTitle.setStyle("-fx-font: bold 40 arial; -fx-base: #fbb040; -fx-text-fill: white");

        _statusArea.getChildren().addAll(space1,_levelTitle,_modeTitle, space2, _helpButton);
    }

    private void setUpButtonArea() {
        _buttonArea.setSpacing(50);
        _buttonArea.setPadding(new Insets(5));
        _buttonArea.setPrefHeight(200);
        _buttonArea.setAlignment(Pos.CENTER);

        _settingsButton.setMinWidth(150);
        _settingsButton.setMinHeight(150);
        _settingsButton.setStyle("-fx-font: bold 20 arial; -fx-base: #fbb040; -fx-background-radius: 75 75 75 75; -fx-text-fill: white");

        _accuracyArea.setMinWidth(200);
        _accuracyArea.setMinHeight(150);
        setUpAccuracyTitles();
        //_accuracyArea.setStyle("-fx-font: bold 20 arial; -fx-base: #fbb040; -fx-background-radius: 75 75 75 75; -fx-text-fill: white");

        _repeatButton.setMinWidth(150);
        _repeatButton.setMinHeight(150);
        _repeatButton.setStyle("-fx-font: bold 20 arial; -fx-base: #fbb040; -fx-background-radius: 75 75 75 75; -fx-text-fill: white");
        _repeatButton.setDisable(true);


        _buttonArea.getChildren().addAll(_repeatButton,_accuracyArea,_settingsButton);
    }

    private void setUpAccuracyTitles() {
        _accuracyArea.setPadding(new Insets(5,0,0,0));
        _accuracyArea.setAlignment(Pos.CENTER);

        _accuracyTitle.setText("Accuracy");
        _accuracyTitle.setStyle("-fx-font: bold 20 arial;-fx-text-fill: white");

        _accuracyLabel.setText("---.--%");
        _accuracyLabel.setStyle("-fx-font: bold 40 arial;-fx-text-fill: #fbb040");

        _accuracyArea.getChildren().setAll(_accuracyTitle,_accuracyLabel);
    }

    public void addCircles(int number) {
        createCircles(number);
        _resultsArea.getChildren().removeAll(_startQuizButton);
        _resultsArea.getChildren().addAll(_circleList);
    }

    private void setUpResultsArea() {
        _resultsArea.setSpacing(15);
        _resultsArea.setPadding(new Insets(5));
        _resultsArea.setPrefHeight(150);
        _resultsArea.setAlignment(Pos.CENTER);

        _startQuizButton.setMinWidth(180);
        _startQuizButton.setMinHeight(50);
        _startQuizButton.setStyle("-fx-font: bold 18 arial; -fx-base: #fbb040; -fx-background-radius: 30 30 30 30; -fx-text-fill: white");
        _resultsArea.getChildren().addAll(_startQuizButton);
    }

    private void createCircles(int number) {
        _circleList.clear();
        for (int i = 0; i < number; i++) {
            Circle circle = new Circle(20);
            circle.setStyle("-fx-fill: #c2c2c2;");
            _circleList.add(circle);
        }
    }

    private void updateMice(Status status){

        if (status.equals(Status.Mastered)){
            soundEffect("/MediaResources/SoundFiles/goodmeow.wav");
            miceNum--;
            updateMice();
            //mice number does not decrement as none gets left behind if correct

        } else if (status.equals(Status.Faulted)){
            soundEffect("/MediaResources/SoundFiles/333916__thearxx08__cat-meowing.mp3");
            miceNum--;
            _miceHowMany--;
            updateMice();
            final ImageView miceimv = new ImageView();
            final Image miceImage = new Image("MediaResources/mouse1sharp.png", 80, 105, false, true);
            miceimv.setImage(miceImage);
            GridPane.setConstraints(miceimv,miceNum+1,0);
            _gameArea.getChildren().addAll(miceimv);
        } else if (status.equals(Status.Failed)){
            soundEffect("/MediaResources/SoundFiles/badmeow.wav");
            miceNum--;
            _miceHowMany--;
            updateMice();
            final ImageView miceimv = new ImageView();
            final Image miceImage = new Image("MediaResources/mouse3flat.png", 80, 105, false, true);
            miceimv.setImage(miceImage);
            GridPane.setConstraints(miceimv,miceNum+1,0);
            _gameArea.getChildren().addAll(miceimv);
        }
    }

    private void updateCircle(Status status) {
        DropShadow glow = new DropShadow();
        glow.setOffsetX(0f);
        glow.setOffsetY(0f);
        glow.setRadius(50);
        if (status.equals(Status.Mastered)) {
            glow.setColor(Color.GREEN);
            _circleList.get(_position).setStyle("-fx-fill: rgb(90,175,90);");
            _circleList.get(_position).setEffect(glow);
            _numberMastered++;
            _accuracy++;
            _position--;
            colorAccuracy(_accuracy/(_wordNum-_position) * 100);
        } else if (status.equals(Status.Faulted)) {
            glow.setColor(Color.ORANGE);
            _circleList.get(_position).setStyle("-fx-fill: rgb(230,160,40);");
            _circleList.get(_position).setEffect(glow);
            _position--;
            colorAccuracy(_accuracy/(_wordNum-_position) * 100);
        } else if (status.equals(Status.Failed)) {
            glow.setColor(Color.RED);
            _circleList.get(_position).setStyle("-fx-fill: rgb(225,100,50);");
            _circleList.get(_position).setEffect(glow);
            _position--;
            colorAccuracy(_accuracy/(_wordNum-_position) * 100);
        }
    }

    private void colorAccuracy(double percentage) {
        _accuracyLabel.setText(String.format("%.2f", percentage)+"%");
        _accuracyHover.setText(String.format("Accuracy: %.2f", percentage)+"%");
        if (percentage >= 90) {
            _accuracyLabel.setStyle("-fx-font: bold 40 arial;-fx-text-fill: #5aaf5a");
            _accuracyHover.setStyle("-fx-font: bold 40 arial;-fx-text-fill: #5aaf5a");
        } else if (percentage >= 50) {
            _accuracyLabel.setStyle("-fx-font: bold 40 arial;-fx-text-fill: #e6a028");
            _accuracyHover.setStyle("-fx-font: bold 40 arial;-fx-text-fill: #e6a028");
        } else {
            _accuracyLabel.setStyle("-fx-font: bold 40 arial;-fx-text-fill: #ff6432");
            _accuracyHover.setStyle("-fx-font: bold 40 arial;-fx-text-fill: #ff6432");
        }
    }

    private void reset() {
        _mediaPlayer.play();
        resetAccuracyHandlers();
        _position = _wordModel.getSpellingList(_review).size();
        _accuracy = 0;
        setUpGameArea();
        _accuracyLabel.setText("---.--%");
        _accuracyLabel.setStyle("-fx-font: bold 40 arial;-fx-text-fill: #fbb040");
        _startQuizButton.setDisable(false);
        //_definitionButton.setText("");
        _repeatButton.setDisable(true);
        _inputText.setDisable(true);
        _inputText.setText("Press Start Quiz To Start Your Quiz!!");
        _submitButton.setDisable(true);
        _mainLayout.setSpacing(0);
        _mainLayout.setAlignment(Pos.TOP_CENTER);
        _levelTitle.setText("Level " + _wordModel.getCurrentLevel());
        _resultsArea.getChildren().removeAll(_circleList);
        _resultsArea.getChildren().addAll(_startQuizButton);

    }

    private void setUpAccuracyHandlers() {
        _resultsArea.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                _resultsArea.getChildren().removeAll(_circleList);
                _resultsArea.getChildren().addAll(_accuracyHover);
            }
        });

        _resultsArea.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                _resultsArea.getChildren().removeAll(_accuracyHover);
                _resultsArea.getChildren().addAll(_circleList);
            }
        });
    }

    private void resetAccuracyHandlers() {
        _resultsArea.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            }
        });

        _resultsArea.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            }
        });
    }

    private void setUpRewardGui() {
        //_wordModel.levelUp();
        _mediaPlayer.stop();
        _rewardSound.play();

        _mainLayout.getChildren().removeAll(_statusArea,_resultsArea,_buttonArea,_textArea, _overallGameArea);
        _mainLayout.setAlignment(Pos.CENTER);
        _mainLayout.setSpacing(13);

        _congratsStatusArea.setSpacing(50);
        _congratsStatusArea.setPadding(new Insets(20));
        _congratsStatusArea.setAlignment(Pos.CENTER);

        _congratsTitle.setText("You Passed Level " + _wordModel.getCurrentLevel() + "!!");
        _congratsTitle.setStyle("-fx-font: bold italic 35 arial; -fx-base: #fbb040; -fx-text-fill: white");

        _congratsStatusArea.getChildren().removeAll(_congratsTitle,_congratsStatusVBox,_congratsTitle2);
        _congratsStatusArea.getChildren().addAll(_congratsTitle);

        _videoButton.setMinWidth(200);
        _videoButton.setMinHeight(200);
        _videoButton.setStyle("-fx-font: bold italic 25 arial; -fx-base: #fbb040;-fx-background-radius: 100 100 100 100; -fx-text-fill: white");

        _nextLevelButton.setMinWidth(250);
        _nextLevelButton.setMinHeight(25);
        _nextLevelButton.setStyle("-fx-font: bold 18 arial; -fx-base: #fbb040; -fx-background-radius: 10 10 10 10; -fx-text-fill: white");
        if (_wordModel.getCurrentLevel() >= _wordModel.getNumberOfLevels()) {
            _nextLevelButton.setDisable(true);
        }

        _stayButton.setMinWidth(250);
        _stayButton.setMinHeight(25);
        _stayButton.setStyle("-fx-font: bold 18 arial; -fx-base: #fbb040; -fx-background-radius: 10 10 10 10; -fx-text-fill: white");

        _mainMenu.setMinWidth(250);
        _mainMenu.setMinHeight(25);
        _mainMenu.setStyle("-fx-font: bold 18 arial; -fx-base: #fbb040; -fx-background-radius: 10 10 10 10; -fx-text-fill: white");

        _mainLayout.getChildren().addAll(_congratsStatusArea,_resultsArea,_videoButton,_nextLevelButton,_stayButton,_mainMenu);
    }

    private void setUpFailedGui() {
        _mediaPlayer.stop();
        _failedSound.play();
        _mainLayout.getChildren().removeAll(_statusArea,_resultsArea,_buttonArea,_textArea, _overallGameArea);
        _mainLayout.setAlignment(Pos.CENTER);
        _mainLayout.setSpacing(13);

        _congratsStatusArea.setSpacing(50);
        _congratsStatusArea.setPadding(new Insets(20));
        _congratsStatusArea.setAlignment(Pos.CENTER);
        _congratsStatusVBox.setAlignment(Pos.CENTER);

        _congratsTitle.setText("Please Try Again!!");
        _congratsTitle2.setText("You Didn't Pass Level " + _wordModel.getCurrentLevel());
        _congratsTitle.setStyle("-fx-font: bold italic 35 arial; -fx-base: #fbb040; -fx-text-fill: white");
        _congratsTitle2.setStyle("-fx-font: bold italic 35 arial; -fx-base: #fbb040; -fx-text-fill: white");

        _congratsStatusVBox.getChildren().removeAll(_congratsTitle,_congratsTitle2);
        _congratsStatusVBox.getChildren().addAll(_congratsTitle,_congratsTitle2);

        _congratsStatusArea.getChildren().removeAll(_congratsStatusVBox,_congratsTitle2,_congratsTitle);
        _congratsStatusArea.getChildren().addAll(_congratsStatusVBox);

        _stayButton.setMinWidth(250);
        _stayButton.setMinHeight(25);
        _stayButton.setStyle("-fx-font: bold 18 arial; -fx-base: #fbb040; -fx-background-radius: 10 10 10 10; -fx-text-fill: white");

        _mainMenu.setMinWidth(250);
        _mainMenu.setMinHeight(25);
        _mainMenu.setStyle("-fx-font: bold 18 arial; -fx-base: #fbb040; -fx-background-radius: 10 10 10 10; -fx-text-fill: white");

        _mainLayout.getChildren().addAll(_congratsStatusArea,_resultsArea,_stayButton,_mainMenu);
    }

    private void setUpReviewGui() {
        _mainLayout.getChildren().removeAll(_statusArea,_resultsArea,_buttonArea,_textArea, _overallGameArea);
        _mainLayout.setAlignment(Pos.CENTER);
        _mainLayout.setSpacing(13);

        _congratsStatusArea.setSpacing(50);
        _congratsStatusArea.setPadding(new Insets(20));
        _congratsStatusArea.setAlignment(Pos.CENTER);

        _congratsTitle.setText("Thanks for reviewing Level " + _wordModel.getCurrentLevel());
        _congratsTitle.setStyle("-fx-font: bold italic 35 arial; -fx-base: #fbb040; -fx-text-fill: white");

        _congratsStatusArea.getChildren().removeAll(_congratsTitle);
        _congratsStatusArea.getChildren().addAll(_congratsTitle);

        _mainMenu.setMinWidth(250);
        _mainMenu.setMinHeight(25);
        _mainMenu.setStyle("-fx-font: bold 18 arial; -fx-base: #fbb040; -fx-background-radius: 10 10 10 10; -fx-text-fill: white");

        _mainLayout.getChildren().addAll(_congratsStatusArea,_resultsArea,_mainMenu);
    }

    private void submitHandler() {
        String text = _inputText.getText();
        _inputText.clear();
        _quiz.spellingLogic(text);
        Status stat = _quiz.getStatus();
        updateCircle(stat);
        if(!(miceNum<=0)) {
            updateMice(stat);
        }

        _wordModel.StatsAccessibleOn();//turn on access to statistics for this level
        isFinished();
    }

    private void isFinished() {
        if (_quiz.getFinishedStatus()) {
            _repeatButton.setDisable(true);
            //_definitionButton.setDisable(true);
            _submitButton.setDisable(true);
            _inputText.setDisable(true);

            if (_review) {
                setUpReviewGui();
            } else if ((double)_numberMastered/Voxspell.COUNT >= 0.9) {
                setUpRewardGui();
            } else {
                setUpFailedGui();
            }
            setUpAccuracyHandlers();
        }
    }

    private void setUpEventHandelers() {
        _inputText.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().toString().equals("ENTER")) {
                    //Festival.stopFestivalTTS();
                    submitHandler();
                }
            }
        });

        _submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _buttonSound.stop();
                _buttonSound.play();
                //Festival.stopFestivalTTS();
                submitHandler();
            }
        });

        _startQuizButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _buttonSound.stop();
                _buttonSound.play();
                _position = _wordModel.getSpellingList(_review).size()-1;
                _numberMastered = 0;
                _startQuizButton.setDisable(true);
                _inputText.setDisable(false);
                _repeatButton.setDisable(false);
                //_definitionButton.setDisable(false);
                _inputText.clear();
                _submitButton.setDisable(false);
                _quiz.setUpSpellingQuiz(_wordModel,_review);
            }
        });

        _repeatButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _buttonSound.stop();
                _buttonSound.play();
                _quiz.repeatWord();
            }
        });

        _stayButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _buttonSound.stop();
                _buttonSound.play();
                _failedSound.stop();
                _rewardSound.stop();
                _mainLayout.getChildren().removeAll(_congratsStatusArea,_resultsArea,_videoButton,_nextLevelButton,_stayButton,_mainMenu);
                reset();
                _mainLayout.getChildren().addAll(_statusArea,_resultsArea,_overallGameArea,_buttonArea,_textArea);
            }
        });

        _nextLevelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _buttonSound.stop();
                _buttonSound.play();
                _failedSound.stop();
                _rewardSound.stop();
                _mainLayout.getChildren().removeAll(_congratsStatusArea,_resultsArea,_videoButton,_nextLevelButton,_stayButton,_mainMenu);
                _wordModel.updateLevel(_wordModel.getCurrentLevel()+1);
                reset();
                _mainLayout.getChildren().addAll(_statusArea,_resultsArea,_overallGameArea,_buttonArea,_textArea);
            }
        });

        _mainMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _buttonSound.stop();
                _buttonSound.play();
                _mediaPlayer.stop();
                _failedSound.stop();
                _rewardSound.stop();
                //Switch To Main Menu Scene
                InitialScene mainMenu = new InitialScene(_window, _wordModel);
                _window.setScene(mainMenu.createScene());
            }
        });


        _videoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _buttonSound.stop();
                _buttonSound.play();
                _rewardSound.stop();
                VideoPlayer video = new VideoPlayer();
                video.display();
            }
        });

        _settingsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _buttonSound.stop();
                _buttonSound.play();
                _menu = new MenuPopup();
                MenuStatus option = _menu.display();
                if (option == MenuStatus.VOICE){
                    VoiceChangePopup voiceOptionMenu = new VoiceChangePopup();
                    voiceOptionMenu.display();
                } else if (option == MenuStatus.STATS){
                    Stage statsPopup = new Stage();
                    statsPopup.initModality(Modality.APPLICATION_MODAL);
                    statsPopup.setTitle("Statistics");

                    StatisticsScene statsCreator = new StatisticsScene(_wordModel);
                    VBox vbox = new VBox();
                    vbox.getChildren().addAll(statsCreator.createScene());
                    BackgroundImage statsBg = new BackgroundImage(new Image("MediaResources/background.png", 1040, 640, false, true),
                            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
                    vbox.setBackground(new Background(statsBg));
                    Scene scene = new Scene(vbox, 800, 480);
                    statsPopup.setScene(scene);
                    statsPopup.showAndWait();

                } else if (option == MenuStatus.MAIN){
                    Stage stage = Stage.class.cast(_mainScene.getWindow());
                    InitialScene initialScene = new InitialScene(stage, _wordModel);
                    stage.setScene(initialScene.createScene());
                    _mediaPlayer.stop();

                } else if (option == MenuStatus.EXIT){
                    Stage stage = Stage.class.cast(_mainScene.getWindow());
                    _wordModel.saveData();
                    stage.close();
                    _mediaPlayer.stop();

                }

            }
        });
    }

    public void startThreadState() {
        _inputText.setDisable(true);
        _inputText.setText("PLEASE WAIT...");
        _submitButton.setDisable(true);
        _submitButton.setText("");
        _submitButton.setGraphic(new ImageView(_loadingIcon));
        _submitButton.setOpacity(100);
        _submitButton.setAlignment(Pos.CENTER);
        _repeatButton.setDisable(true);
    }

    public void endThreadState() {
        _inputText.setDisable(false);
        _inputText.setText("");
        _inputText.requestFocus();
        _submitButton.setDisable(false);
        _submitButton.setText("Submit");
        _submitButton.setGraphic(null);
        _submitButton.setOpacity(_submitButtonOpacity);
        _repeatButton.setDisable(false);
    }

    /**
     * make a sound effect for a button
     * @param fileName: address of sound effect
     */
    private void soundEffect(String fileName){
        final URL resource = getClass().getResource(fileName);
        final Media media = new Media(resource.toString());
        MediaPlayer sfx = new MediaPlayer(media);
        sfx.play();
    }

    private MediaPlayer bgm(String fileName){
        final URL resource = getClass().getResource(fileName);
        final Media media = new Media(resource.toString());
        MediaPlayer sfx = new MediaPlayer(media);
        sfx.setOnEndOfMedia(new Runnable() {
            public void run() {
                sfx.seek(Duration.ZERO);
            }
        });
        return sfx;
    }




    public Scene createScene() {
        return this._mainScene;
    }

}
