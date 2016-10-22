package models;

import VoxspellApp.SpellingQuizScene;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ratterz on 16/09/16.
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

    public void setUpSpellingQuiz(WordModel wordModel, boolean review) {
        this._wordModel = wordModel;
        _finished = false;
        _setUpFlag = false;
        _attemptFlag = false;
        _position = 0;
        _review = review;
        _failedWordsToMove = new ArrayList<Word>();
        _spellingList = _wordModel.getSpellingList(_review);
        _quizScene.addCircles(_spellingList.size());
        _phrase = "";
        spellingLogic("");
    }

    public void spellingLogic(String userinput) {
        if (!_setUpFlag) {
            _phrase = "Please Spell " + _spellingList.get(_position).getWord();
            System.out.println(_phrase);
            startFestivalThread(_phrase);
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
                _phrase = "Incorrect . Please Try Again . " + _spellingList.get(_position).getWord() + " . " +  _spellingList.get(_position).getWord();
                startFestivalThread(_phrase);
                System.out.println(_phrase);
                _attemptFlag = true;
                _status = Status.Unseen;
                return;
            }
        } else {
            //correct on second try
            if (_spellingList.get(_position).compareWords(userinput)) {
                _phrase = "Correct .";
                _spellingList.get(_position).countUp(Status.Faulted);
                _status = Status.Faulted;
            } else {
                //incorrect on both tries
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



        if (_position < _spellingList.size()) {
            _phrase = _phrase + " Please Spell " + _spellingList.get(_position).getWord();
            startFestivalThread(_phrase);
            System.out.println(_phrase);
        } else {
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

        _quizScene.startThreadState();
        new Thread(_festivalTask).start();
    }


    public void repeatWord() {
        startFestivalThread(_spellingList.get(_position).getWord());
    }

}
