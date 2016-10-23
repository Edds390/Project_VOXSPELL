package VoxspellApp;

import VoxspellApp.Popups.ConfirmQuitBox;
import VoxspellApp.StartScenes.InitialScene;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import models.Festival;
import models.MasterModel;
import models.WordModel;

import java.io.IOException;

/**
 * Main program for the Voxspell Spelling aid game. An instance of the Voxspell
 * sets up the gui comprising of a menu scene set on the left hand side and the
 * main scene on the right hand side. This main scene changes depending on thee
 * menu option that they choose.
 */

/*
References:
Toggle Button
https://www.freesound.org/people/magedu/sounds/264388/
Normal Button
https://www.freesound.org/people/kickhat/sounds/264447/

 */

public class Voxspell extends Application {


    //GUI COMPONENTS
    private Stage _mainWindow;
    private InitialScene _initialScene;


    //Models
    private int level = 1;//default level 1
    private WordModel _model;
    private MasterModel _masterModel;

    Button playButton;

    //GLOBAL VARIABLES
    public static final int COUNT = 10;

    @Override
    public void start(Stage primaryStage) throws Exception{

        _masterModel = new MasterModel();

        //main window of program
        _mainWindow = primaryStage;
        _mainWindow.setTitle("VOXSPELL");
        _mainWindow.setResizable(false);
        _mainWindow.setOnCloseRequest(e -> {
            e.consume();//suppress user request
            closeProgram("Are you sure you want to quit Voxspell?");//replace with our own close implementation
        });

        try{
            _model = new WordModel("custom_texts/NZCER-spelling-lists.txt", _masterModel);
        } catch (IOException e){
            closeProgram("Spelling list was not found. Continuing may corrupt the program. Quit?");
        }

        Festival.findVoiceList(); // This finds the voice list in the directory and then saves it

        _initialScene = new InitialScene(_mainWindow, _model);


        _mainWindow.setScene(_initialScene.createScene());
        _mainWindow.show();
    }

    /**
     * Logic for closing program. Used to save history before qutting game.
     */
    private void closeProgram(String message){
        ConfirmQuitBox quitBox = new ConfirmQuitBox();
        Boolean answer = quitBox.display("Quit VOXSPELL", message);
        if (answer){
            _model.saveData();
            _mainWindow.close();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    public Stage getStageRef(){
        return _mainWindow;
    }

}
