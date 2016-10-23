package models;

import VoxspellApp.Popups.ConfirmQuitBox;
import VoxspellApp.Popups.WarningBox;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by edson on 15/09/16.
 * Represents the abstraction of the overall Wordmodel.
 * A wordmodel is specific to a particular spelling list.
 * The wordmodel stores all the levels, whivh in turn stores all the words.
 * It stores all the statistics that is specific to that particular spelling list.
 * It is the primary interface between the views and the data models.
 */
public class WordModel implements Resettable, Serializable {
    private static final long serialVersionUID = 1L;

    private MasterModel _masterModel;//model that stores wordModel
    List<String> _categoryList;//stores the names of the level's name
    List<Level> _levelList;//arraylist of Level objects
    private int _totalLevels;//number of levels
    private  int _currentLevel;//current level the user is in
    //private int _accessLevel = 1;//int of user's highest accessible level

    private boolean[] _accessStats;//accessibility of that particular level
    private List<int[]> _accuracyList;//list of int arrays showing statistic for each level
    private int[] _overallStatstic;//int array of overall frequency of each mastered(2),faulted(1),failed(0)
    private Map<String, Integer> _categoryDictionary;//links the level name to the level integer

    private String _spellingListPath;//path of the spelling list
    private File _file;//file of spelling list

    private String _title;//simple name of spelling list

    public WordModel(String spellingListPath, MasterModel masterModel) throws IOException{
        _title = "NZ Spelling List";
        checkSerExists(spellingListPath);//check if it exists otherwise makes a new one
        _masterModel = masterModel;
        _masterModel.addToMaster(this);//stores the wordmodel to the master model
        _masterModel.addAddress("NZ Spelling List", spellingListPath);

    }

    /**
     * Creates a wordmodel based on a new spelling list.
     * If it already exists, then uses the serializable history to import
     * its statistics.
     * @param fileName simple name of the spelling list
     */
    public void newList(String fileName){
        _title = fileName;

        try {
            checkSerExists(_masterModel.getAddress(fileName));
        } catch (IOException x) {
            if(x.getMessage().equals("1")) {
                WarningBox corruptList = new WarningBox();
                corruptList.display("Wrong List", "This is not a spelling list!");//wrong format of spelling list
            } else if (x.getMessage().equals("12")){
                WarningBox tooManyLevels = new WarningBox();
                tooManyLevels.display("Wrong List", "Too many categories! 11 is the maximum number.");//too many levels in list
            }
        }
    }

    /**
     * Checks whether the serializable file exists for that particular spelling list.
     * If it does exist, then imports that wordmodel and uses it.
     * Otherwise, makes a new wordmodel.
     * @param spellingListPath the new spelling list
     * @throws IOException wrong formatting/specifications of spelling list.
     */
    private void checkSerExists(String spellingListPath) throws IOException{
        _file = new File(".ser/."+_title+".ser");
        _spellingListPath = spellingListPath;
        //serializble already exists; not new game
        if (_file.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(_file));
                try {//imports all the relevant data
                    WordModel wordModel = (WordModel)ois.readObject();
                    _levelList = wordModel.getLevelList();
                    _totalLevels = wordModel.getTotalLevels();
                    _currentLevel = wordModel.getCurrentLevel();
                    //_accessLevel = wordModel.getAccessLevel();
                    _accessStats = wordModel.getAccessStats();
                    _accuracyList = wordModel.getAccuracyList();
                    _overallStatstic = wordModel.getOverall();
                    _categoryList = wordModel.getCategoryList();
                    _categoryDictionary = wordModel.getCategoryMap();
                    ois.close();
                } catch (ClassNotFoundException e) {
                    ConfirmQuitBox quit = new ConfirmQuitBox();
                    quit.display("Corrupt Program", "Please ensure \".ser\" folder exists in your current directory.");

                }
            } catch (IOException e) {
                //method signature; can never be reached
            }
        } else {//new game so make a new wordmodel
            makeNewModel();
        }

    }

    /**
     * Makes a new word model by settign all the relevant fields
     * @throws IOException
     */
    private void makeNewModel() throws IOException{
        //initialise fields
        _categoryDictionary = new HashMap<String, Integer>();
        _categoryList = new ArrayList<String>();
        _accuracyList = new ArrayList<int[]>();
        _overallStatstic = new int[3];

        int currentLevelValue = 1;//integer used to construct level class
        Level currentLevel;
        String currentLine;

        //begin reading spelling list and store in the relevant data structures.
        FileReader fr = new FileReader(_spellingListPath);
        BufferedReader br = new BufferedReader(fr);
        currentLine = br.readLine();
        if (!currentLine.substring(0,1).equals("%")){//check if word not level
            throw new IOException("1");
        } else {
            currentLevel = new Level(currentLevelValue);
            _levelList = new ArrayList<>();
            _levelList.add(currentLevel);
            _categoryList.add(currentLine.substring(1));
            _categoryDictionary.put(currentLine.substring(1), currentLevelValue);
            currentLevelValue+=1;
        }
        while((currentLine = br.readLine())!=null){
            if (!currentLine.substring(0,1).equals("%")){//check if word not level
                currentLevel.addWord(currentLine);//add to level object
            } else {
                if(currentLevelValue>=12){
                    throw new IOException("12");
                }
                currentLevel = new Level(currentLevelValue);
                _levelList.add(currentLevel);
                _categoryList.add(currentLine.substring(1));
                _categoryDictionary.put(currentLine.substring(1), currentLevelValue);
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

    /**
     * gets the words needed for a spelling game.
     * @param review if true, then select only failed words
     * @return words that are to be tested
     */
    public List<Word> getSpellingList(boolean review) {
        Level level = _levelList.get(_currentLevel-1);
        return level.getWords(review);
    }
    /*
    public int getAccessLevel(){
        return _accessLevel;
    }
    */


    public Level getLevel(String category){
        return _levelList.get(_categoryDictionary.get(category)-1);
    }

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

    public List<String> getCategoryList() { return this._categoryList; }

    public Map<String, Integer> getCategoryMap() { return this._categoryDictionary; }

    public MasterModel getMasterModel() { return this._masterModel; }

    public String getFilePath(){ return this._spellingListPath; }


    public String getTitle(){ return _title; }

    /**
     * saves the data of the wordModel as a ser file. Called whenever the user closes the program.
     */
    public void saveData() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(_file));
            oos.writeObject(this);
            oos.close();
            _masterModel.addToMaster(this);

        } catch (IOException e) {
            WarningBox warn = new WarningBox();
            warn.display("Corrupt Program", "Please choose another file.");
        }
    }

    /**
     * Used to clear the history and make a new word model for the Clear History control.
     */
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
                if(x.getMessage().equals("1")) {
                    WarningBox corruptList = new WarningBox();
                    corruptList.display("Wrong List", "This is not a spelling list!");
                } else if (x.getMessage().equals("11")){
                    WarningBox tooManyLevels = new WarningBox();
                    tooManyLevels.display("Wrong List", "Too many categories! 11 is the maximum number.");
                }

            }
        }

    }
}
