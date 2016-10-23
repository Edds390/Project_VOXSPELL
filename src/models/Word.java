package models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by edson on 15/09/16.
 *
 * Represents the Word data structure which stores the word string that is associated with it
 * as well as the relevant statistics for that particular word.
 * The statistics is stored as an array of int and is incremented as the user spells the word.
 */
public class Word implements Resettable, Serializable, Comparable<Word>{
    private static final long serialVersionUID = 1L;
    private String _word;
    private int _level;
    private Status _status;
    int[] _countList;


    /**
     * Takes in a word string and wraps it within the class.
     * Enum statuses to show the status the word is currently in at runtime.
     * Unseen: untested.
     * @param word word from the spelling quiz
     */
    public Word(String word){
        _word = word;
        _status = Status.Unseen;//word yet to be seen ingame
        _countList = new int[3];
    }

    /**
     * Increments the statistics based on the status of the word.
     * @param status word's status
     */
    public void countUp(Status status){
        _countList[status.getStatus()] += 1;
        _status = status;
    }

    /**
     * resets the statistics of the word when the user clears history.
     */
    public void reset(){
        _countList = new int[3];
        _status = Status.Unseen;
    }

    public int getStat(int status) {
        return _countList[status];
    }

    public Status getStatus() {
        return _status;
    }

    @Override
    /**
     * This method overrides the default equals method so that the words will be
     * compared to their strings and not their actual objects
     */
    public boolean equals(Object object) {
        if (object instanceof Word) {
            return ((Word)object)._word.equals(this._word);
        } else {
            return false;
        }
    }

    /**
     * This method returns a boolean to see if the input string and the word is the same
     * @return boolean
     */
    public boolean compareWords(String inputWord) {
        return (this._word.trim().toLowerCase().equals(inputWord.trim().toLowerCase()));
    }

    /**
     * This method returns the string representation of the word
     * @return String _word
     */
    public String getWord() {
        return this._word.trim();
    }

    //compare alphabetically
    @Override
    public int compareTo(Word o) {
        return o.getWord().toLowerCase().compareTo(_word.toLowerCase());
    }
}
