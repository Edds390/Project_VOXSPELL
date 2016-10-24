package models;

import VoxspellApp.SpellingQuizScene;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ratterz on 16/09/16.
 * Represents the spelling quiz abstraction and handles all the logic that is
 * required for a spelling quiz. Tied with the spelling quiz scene.
 * Master: right first try
 * Faulted: right second try
 * Failed: wrong
 */
public class SpellingQuiz {

    private WordModel _wordModel;
    private boolean _setUpFlag;
    private boolean _attemptFlag;
    private int _position;
    private List<Word> _spellingList;
    private Task _festivalTask;
    private Status _status;
    private boolean _finished;
    private String _phrase;
    private SpellingQuizScene _quizScene;
    private boolean _review;
    private List<Word> _failedWordsToMove;

    public SpellingQuiz(SpellingQuizScene scene) {
        _quizScene = scene;
    }

    /**
     * Sets up the type of spelling quiz that is to be tested:
     * review: uses failed words
     * @param wordModel used to collect the relevant data
     * @param review if true then use failed words from word model
     */
    public void setUpSpellingQuiz(WordModel wordModel, boolean review) {
        this._wordModel = wordModel;
        _finished = false;//word list has been finished
        _setUpFlag = false;//initial start up logic
        _attemptFlag = false;//used for faulted logic
        _position = 0;//position in wordlist
        _review = review;//mode of the review
        _failedWordsToMove = new ArrayList<Word>();//the list of failed words by the user
        _spellingList = _wordModel.getSpellingList(_review);//list to be tested
        _quizScene.addCircles(_spellingList.size());//progress bar
        _phrase = "";//word to be said via tts
        spellingLogic("");
    }

    /**
     * Uses the input genereated from the textfield that is inputted by the user in
     * the spelling quiz scene. Using this, compares with the correct spelling and
     * handles the logic necessary.
     * @param userinput the word that the user has spelled
     */
    public void spellingLogic(String userinput) {
        if (!_setUpFlag) {
            _phrase = "Please Spell " + _spellingList.get(_position).getWord();//phrase: word that is said by the program.
            startFestivalThread(_phrase);//assigns festival to say it
            _setUpFlag = true;
            _status = Status.Unseen;
            return;
        } else if (!_attemptFlag) {
            //correct on first try
            if (_spellingList.get(_position).compareWords(userinput)) {
                _phrase = "Correct .";
                _spellingList.get(_position).countUp(Status.Mastered);
                if (_review) {
                    _failedWordsToMove.add(_spellingList.get(_position));
                }
                _position++;
                _status = Status.Mastered;
            } else {
                _phrase = "Incorrect . Please Try Again ... " + _spellingList.get(_position).getWord() + " ... " +  _spellingList.get(_position).getWord();
                startFestivalThread(_phrase);
                _attemptFlag = true;
                _status = Status.Unseen;
                return;
            }
        } else {
            //correct on second try: faulted
            if (_spellingList.get(_position).compareWords(userinput)) {
                _phrase = "Correct .";
                _spellingList.get(_position).countUp(Status.Faulted);
                _status = Status.Faulted;
            } else {
                //incorrect on both tries: failed
                _phrase = "Incorrect .";
                _spellingList.get(_position).countUp(Status.Failed);
                if (!_review) {
                    _wordModel.getLevelList().get(_wordModel.getCurrentLevel()-1).addFailedWord(_spellingList.get(_position));
                }
                _status = Status.Failed;
            }
            _position++;
            _attemptFlag = false;
        }


        //checks if the user has spelled all words given by the system. if yes, stores the faild list of words to the level
        if (_position < _spellingList.size()) {//user hasnt finished
            _phrase = _phrase + " Please Spell " + _spellingList.get(_position).getWord();
            startFestivalThread(_phrase);
        } else {//user has finished
            startFestivalThread(_phrase);
            if (_review) {
                for (Word word : _failedWordsToMove) {
                    _wordModel.getLevelList().get(_wordModel.getCurrentLevel()-1).removeFailedWord(word);
                }
            }
            _finished = true;
            return;
        }


    }


    public Status getStatus() {
        return this._status;
    }

    public boolean getFinishedStatus() {
        return this._finished;
    }

    /**
     * background thread for the festival to say the word.
     * @param phrase the phrase that is to be said by tts
     */
    private void startFestivalThread(String phrase) {
        _festivalTask = new Task() {
            @Override
            protected Object call() throws Exception {
                Festival.festivalTTS(phrase);
                return null;
            }
        };

        _festivalTask.setOnSucceeded(event -> {
            _quizScene.endThreadState();
        });

        new Thread(_festivalTask).start();
    }


    /**
     * repeats the word to the user
     */
    public void repeatWord() {
        startFestivalThread(_spellingList.get(_position).getWord());
    }

}
