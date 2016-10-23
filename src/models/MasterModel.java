package models;

import VoxspellApp.Popups.ConfirmQuitBox;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by edson on 9/10/16.
 * This class is the master model that stores all WordModel objects that are specific to a
 * particular word list, using a hashmap. Thus, it retains all statistics information across
 * different sessions and different list selections.
 * It also stores the address of the spelling list and is identifiable using the word list's
 * simple name.
 */
public class MasterModel implements Serializable{
    private static final long serialVersionUID = 123L;
    private Map<String, WordModel> _masterMap;//map of list to wordmodel
    private Map<String, String> _titleDictionary;//map of list name to list address
    private File _file = new File(".ser/.MasterModel.ser");


    public MasterModel(){
        //if ser file exists, loads the old history, else make a new one if fresh start.
        //error statements will send the user a prompt saying the proper directory structure
        //doesn't exist.
        if (_file.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(_file));
                try {
                    MasterModel oldModel = (MasterModel) ois.readObject();
                    _masterMap = oldModel.getMasterMap();
                    _titleDictionary = oldModel.getTitleMap();

                    ois.close();
                } catch (ClassNotFoundException e) {
                    ConfirmQuitBox quit = new ConfirmQuitBox();
                    quit.display("Corrupt Program", "Please ensure \".ser\" folder exists in your current directory.");
                }

            } catch (FileNotFoundException e) {
                ConfirmQuitBox quit = new ConfirmQuitBox();
                quit.display("Corrupt Program", "Please ensure \".ser\" folder exists in your current directory.");
            } catch (IOException e) {
                ConfirmQuitBox quit = new ConfirmQuitBox();
                quit.display("Corrupt Program", "Please ensure \".ser\" folder exists in your current directory.");
            }


        } else {
            _masterMap = new HashMap<String, WordModel>();
            _titleDictionary = new HashMap<String, String>();
        }
    }


    /**
     * Adds a word model to the word model dictionary, keyed by the list's simple name.
     * If error, end program.
     * @param wordModel the word model to be added
     */
    public void addToMaster(WordModel wordModel){
        _masterMap.put(wordModel.getTitle(), wordModel);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(".ser/.MasterModel.ser"));
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            ConfirmQuitBox quit = new ConfirmQuitBox();
            quit.display("Corrupt Program", "Please ensure \".ser\" folder exists in your current directory.");
        }
    }

    /**
     * associates a spelling list's simple name with its address
     * @param title list simple name
     * @param address list address
     */
    public void addAddress(String title, String address){
        _titleDictionary.put(title, address);

    }

    public String getAddress(String title){
        return _titleDictionary.get(title);
    }

    private Map getMasterMap(){
        return _masterMap;
    }

    public Map getTitleMap(){
        return _titleDictionary;
    }

    public Set<String> getMapKeyset(){
        return _masterMap.keySet();
    }

    public Set<String> getDictionaryKeyset(){
        return _titleDictionary.keySet();
    }

}
