package VoxspellApp;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Festival;
import models.SpellingQuiz;
import models.Status;
import models.WordModel;

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

    //STORAGE
    private ArrayList<Circle> _circleList = new ArrayList<Circle>();
    private int _position;
    private int _numberMastered;
    private double _accuracy = 0;
    private String _savedString = "";

    private MenuPopup _menu;


    //IMAGE
    Image _loadingIcon = new Image("MediaResources/loaderSpinner.gif", 25, 25, false, false);
    double _submitButtonOpacity;



    /**
     * This is the constructor for the spelling quiz scene. This will call the set up gui method
     * so the gui is set up and also it will call a method to start a new spelling quiz. The wordModel
     * will be passed onto the new spelling quiz.
     * @param wordModel
     */
    public SpellingQuizScene(WordModel wordModel, Stage window, boolean review) {

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
        _menu = new MenuPopup();
        _mainLayout.setPadding(new Insets(20,20,20,20));
        _mainLayout.getChildren().addAll(_statusArea,_resultsArea,_buttonArea,_textArea);
        BackgroundImage menuBackground = new BackgroundImage(new Image("MediaResources/background.png", 1040, 640, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        _mainLayout.setBackground(new Background(menuBackground));

        _mainScene = new Scene(_mainLayout, 1040, 640);
        //_mainScene.getStylesheets().add("VoxspellApp/LayoutStyles");
    }

    private void setUpTextArea() {
        _textArea.setSpacing(20);
        _textArea.setPadding(new Insets(20));
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
        _statusArea.setSpacing(50);
        _statusArea.setPadding(new Insets(20));
        _statusArea.setAlignment(Pos.CENTER);

        _levelTitle.setText("Level " + _wordModel.getCurrentLevel());
        _levelTitle.setStyle("-fx-font: bold 40 arial; -fx-base: #fbb040; -fx-text-fill: white");

        if (_review) {
            _modeTitle.setText("Review Quiz");
        } else {
            _modeTitle.setText("New Quiz");
        }
        _modeTitle.setStyle("-fx-font: bold 40 arial; -fx-base: #fbb040; -fx-text-fill: white");

        _statusArea.getChildren().addAll(_levelTitle,_modeTitle);
    }

    private void setUpButtonArea() {
        _buttonArea.setSpacing(50);
        _buttonArea.setPadding(new Insets(50));
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
        _accuracyArea.setPadding(new Insets(20,0,0,0));
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
        _resultsArea.setPadding(new Insets(50));
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

    private void updateCircle(Status status) {
        if (status.equals(Status.Mastered)) {
            _circleList.get(_position).setStyle("-fx-fill: rgb(90,175,90);");
            _numberMastered++;
            _accuracy++;
            _position++;
            colorAccuracy(_accuracy/_position * 100);
        } else if (status.equals(Status.Faulted)) {
            _circleList.get(_position).setStyle("-fx-fill: rgb(230,160,40);");
            _position++;
            colorAccuracy(_accuracy/_position * 100);
        } else if (status.equals(Status.Failed)) {
            _circleList.get(_position).setStyle("-fx-fill: rgb(225,100,50);");
            _position++;
            colorAccuracy(_accuracy/_position * 100);
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
        resetAccuracyHandlers();
        _position = 0;
        _accuracy = 0;
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

        _mainLayout.getChildren().removeAll(_statusArea,_resultsArea,_buttonArea,_textArea);
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
        _mainLayout.getChildren().removeAll(_statusArea,_resultsArea,_buttonArea,_textArea);
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
        _mainLayout.getChildren().removeAll(_statusArea,_resultsArea,_buttonArea,_textArea);
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
        updateCircle(_quiz.getStatus());
        _wordModel.StatsAccessibleOn();//turn on access to statistics for this level
        isFinished();
    }

    private void isFinished() {
        if (_quiz.getFinishedStatus()) {
            _repeatButton.setDisable(true);
            //_definitionButton.setDisable(true);
            _submitButton.setDisable(true);
            _inputText.setDisable(true);
            //TODO delete _wordModel.StatsAccessibleOn();//turn on access to statistics for this level
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
                //Festival.stopFestivalTTS();
                submitHandler();
            }
        });

        _startQuizButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _position = 0;
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
                _quiz.repeatWord();
            }
        });

        _stayButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _mainLayout.getChildren().removeAll(_congratsStatusArea,_resultsArea,_videoButton,_nextLevelButton,_stayButton,_mainMenu);
                reset();
                _mainLayout.getChildren().addAll(_statusArea,_resultsArea,_buttonArea,_textArea);
            }
        });

        _nextLevelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _mainLayout.getChildren().removeAll(_congratsStatusArea,_resultsArea,_videoButton,_nextLevelButton,_stayButton,_mainMenu);
                _wordModel.updateLevel(_wordModel.getCurrentLevel()+1);
                reset();
                _mainLayout.getChildren().addAll(_statusArea,_resultsArea,_buttonArea,_textArea);
            }
        });

        _mainMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Switch To Main Menu Scene
                InitialScene mainMenu = new InitialScene(_window, _wordModel);
                _window.setScene(mainMenu.createScene());
            }
        });

        //TODO worker thread here
        _videoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                VideoPlayer video = new VideoPlayer();
                video.display();
            }
        });

        _settingsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
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

                } else if (option == MenuStatus.EXIT){
                    Stage stage = Stage.class.cast(_mainScene.getWindow());
                    _wordModel.saveData();
                    stage.close();

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




    public Scene createScene() {
        return this._mainScene;
    }

}
