package models;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by edson on 9/10/16.
 */
public class MasterModel implements Serializable{
    private static final long serialVersionUID = 123L;
    private Map<String, WordModel> _masterMap;
    private Map<String, String> _titleDictionary;
    private File _file = new File(".ser/.MasterModel.ser");


    public MasterModel(){
        if (_file.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(_file));
                try {
                    MasterModel oldModel = (MasterModel) ois.readObject();
                    _masterMap = oldModel.getMasterMap();
                    _titleDictionary = oldModel.getTitleMap();

                    ois.close();
                } catch (ClassNotFoundException e) {

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            _masterMap = new HashMap<String, WordModel>();
            _titleDictionary = new HashMap<String, String>();
        }
    }


    public void addToMaster(WordModel wordModel){
        _masterMap.put(wordModel.getTitle(), wordModel);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(".ser/.MasterModel.ser"));
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            //TODO prompt io exception
        }
    }

    public void addAddress(String title, String address){
        _titleDictionary.put(title, address);

    }

    public String getAddress(String title){
        if (_titleDictionary.get(title) == null){
            //TODO prompt io exception
            System.out.println("error state");
        }
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
