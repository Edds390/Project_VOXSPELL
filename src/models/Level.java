package models;


import VoxspellApp.Voxspell;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Created by edson on 15/09/16.
 */
public class Level implements Resettable, Iterable<Word>, Serializable {
    private static final long serialVersionUID = 1L;

    private int _level;
    private int[] _accuracyStats;
    private List<Word> _wordList;
    private List<Word> _failedList;

    public Level(int level){
        _wordList = new ArrayList<Word>();
        _failedList = new ArrayList<Word>();
        _level = level;
        _accuracyStats = new int[3];
    }

    protected void addWord(String word){
        _wordList.add(new Word(word));
    }

    protected void addFailedWord(Word word) {
        if (!_failedList.contains(word)) {
            _failedList.add(word);
        }
    }

    protected void removeFailedWord(Word word) {
        _failedList.remove(word);
    }

    public void reset(){
        for (Word word: _wordList){
            word.reset();
        }
    }

    /**
     * This method shuffles the word list and returns a selected list of words. If there are
     * less than 10 words in the list, the list is returned and a spelling quiz can be done
     * with less than 10 words.
     * @return List<Word>
     */
   public List<Word> getWords(boolean review) {
       if (review) {
           Collections.shuffle(_failedList);
           return selectWords(_failedList);
       } else {
           Collections.shuffle(_wordList);
           return selectWords(_wordList);
       }
   }

   private List<Word> selectWords(List<Word> wordList) {
       if (wordList.size() < Voxspell.COUNT) {
           return wordList;
       } else {
           List<Word> selectedWords = new ArrayList<Word>(Voxspell.COUNT);
           for (int i = 0; i < Voxspell.COUNT; i++) {
               selectedWords.add(wordList.get(i));
           }
           return selectedWords;
       }
   }

    /**
     * updates the statistics of Mastered, Faulted, Failed
     */
   public void countStats(){
       //reinitialise accuracy stats
       _accuracyStats = new int[3];
       for (Word word : _wordList){//go through wordlist and sum up statistics
           if (word.getStatus() != Status.Unseen){//check only words that have been tested
               for (int i = 0; i < 3; i++){//0 failed 1 faulted 2 mastered
                   _accuracyStats[i] += word.getStat(i);//get the statistic based on int array position
               }
           }
       }
   }

   public void sort(){
       Collections.sort(_wordList);
   }

   public int getMasterFrequency(){
       return _accuracyStats[2];
   }


    public int getFaultedFrequency(){
        return _accuracyStats[1];
    }


    public int getFailedFrequency(){
        return _accuracyStats[0];
    }

    public Iterator<Word> iterator(){
        Iterator<Word> wordIterator = _wordList.iterator();
        return wordIterator;
    }

    public List<Word> getFailedList() {
        return this._failedList;
    }
}
