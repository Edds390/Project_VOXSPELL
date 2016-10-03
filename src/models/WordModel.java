package models;

import VoxspellApp.ConfirmQuitBox;
import VoxspellApp.Voxspell;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edson on 15/09/16.
 */
public class WordModel implements Resettable, Serializable {
    private static final long serialVersionUID = 1L;
    List<Level> _levelList;//arraylist of Level objects
    private int _totalLevels;
    private  int _currentLevel;
    //private int _accessLevel = 1;//int of user's highest accessible level
    private boolean[] _accessStats;
    private List<int[]> _accuracyList;//list of int arrays showing statistic for each level
    private int[] _overallStatstic;//int array of overall frequency of each mastered(2),faulted(1),failed(0)

    private String _spellingListPath;
    private File _file = new File(".voxspellData.ser");

    public WordModel(String spellingListPath) throws IOException{
        _spellingListPath = spellingListPath;
        //serializble already exists; not new game
        if (_file.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(_file));
                try {
                    WordModel wordModel = (WordModel)ois.readObject();
                    _levelList = wordModel.getLevelList();
                    _totalLevels = wordModel.getTotalLevels();
                    _currentLevel = wordModel.getCurrentLevel();
                    //_accessLevel = wordModel.getAccessLevel();
                    _accessStats = wordModel.getAccessStats();
                    _accuracyList = wordModel.getAccuracyList();
                    _overallStatstic = wordModel.getOverall();
                    ois.close();
                } catch (ClassNotFoundException e) {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//new game
            makeNewModel();
        }
    }

    private void makeNewModel() throws IOException{
        //initialise fields
        _accuracyList = new ArrayList<int[]>();
        _overallStatstic = new int[3];

        int currentLevelValue = 1;//integer used to construct level class
        Level currentLevel;
        String currentLine;
        //begin reading spelling list
        FileReader fr = new FileReader(_spellingListPath);
        BufferedReader br = new BufferedReader(fr);
        currentLine = br.readLine();
        if (!currentLine.substring(0,1).equals("%")){//check if word not level
            throw new IOException();//TODO create exception class for invalid IO of text input
        } else {
            currentLevel = new Level(currentLevelValue);
            _levelList = new ArrayList<>();
            _levelList.add(currentLevel);
            currentLevelValue+=1;
        }
        while((currentLine = br.readLine())!=null){
            if (!currentLine.substring(0,1).equals("%")){//check if word not level
                currentLevel.addWord(currentLine);//add to level object
            } else {
                currentLevel = new Level(currentLevelValue);
                _levelList.add(currentLevel);
                currentLevelValue+=1;
            }

        }

        _totalLevels = _levelList.size();//set the number of levels
        //_accessLevel = 1;//reset highest accessible level to 1

        //create boolean array showing which levels are accessible in statistics
        _accessStats = new boolean[_totalLevels];
        for(int i = 0; i < _accessStats.length; i++){
            _accuracyList.add(new int[3]);//initialise accuracy stats for each level
            _accessStats[i] = false;//set accessible to all level stats to false
        }
    }

    //reset signal propagate to contained object
    //TODO should consider reset to make a new WordModel object
    public void reset(){
        for (Level level : _levelList){
            level.reset();
        }
        _overallStatstic=new int[3];
    }

    /**
     * updates model; called whenever user wishes to see the statistics.
     */
    public void updateStatistics(){
        //reinitialise overall statistics
        _overallStatstic = new int[3];
        for (int i = 0; i < _totalLevels; i++){
            Level currentLevel = _levelList.get(i);
            currentLevel.countStats();
            int[] statusFrequency = _accuracyList.get(i);//get the stats
            statusFrequency[0] = currentLevel.getFailedFrequency();
            _overallStatstic[0] += currentLevel.getFailedFrequency();//add to overall accuracy integer array
            statusFrequency[1] = currentLevel.getFaultedFrequency();//create int array representing accuracy for each level
            _overallStatstic[1] += currentLevel.getFaultedFrequency();
            statusFrequency[2] = currentLevel.getMasterFrequency();
            _overallStatstic[2] += currentLevel.getMasterFrequency();
        }
    }

    public int[] findAccuracy(int level){
        return _accuracyList.get(level);
    }

    public void updateLevel(int level) {
        this._currentLevel = level;
    }

    /*
    public void levelUp(){
        if (_accessLevel != Voxspell.COUNT && _currentLevel == _accessLevel){
            _accessLevel++;
        }
    }
    */

    public int[] getOverall(){
        return _overallStatstic;
    }

    public List<Word> getSpellingList(boolean review) {
        Level level = _levelList.get(_currentLevel-1);
        return level.getWords(review);
    }
    /*
    public int getAccessLevel(){
        return _accessLevel;
    }
    */

    public Level getLevel(int level){
        return _levelList.get(level);
    }

    public int getCurrentLevel() {
        return this._currentLevel;
    }

    public int getTotalLevels(){ return this._totalLevels; }

    public int getNumberOfLevels() {
        return this._levelList.size();
    }

    public void StatsAccessibleOn(){
        this._accessStats[_currentLevel-1] = true;//toggle on; -1 because currentlevel starts at 1
    }

    public boolean isStatsAccessible(int position){
        return this._accessStats[position];
    }

    public boolean[] getAccessStats() {
        return this._accessStats;
    }

    public List<int[]> getAccuracyList() {
        return this._accuracyList;
    }

    public List<Level> getLevelList() {
        return this._levelList;
    }

    public void saveData() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(_file));
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {

        }
    }

    public void recreate(){
        try{
            Files.delete(Paths.get(".voxspellData.ser"));//delete the ser file
        } catch (NoSuchFileException x){
            //do nothing; if user presses repeat reset this will be caught
        } catch (IOException x){
            //possibly file permissions error
            ConfirmQuitBox quitBox = new ConfirmQuitBox();
            quitBox.display("Corrupted History", "The history is corrupted. Quit the program?");
        } finally {
            try {
                makeNewModel();//make new model
            } catch (IOException x){
                ConfirmQuitBox quitBox = new ConfirmQuitBox();
                quitBox.display("Corrupted Spelling List", "Spelling list is corrupted. Quit the program?");

            }
        }

    }
}
