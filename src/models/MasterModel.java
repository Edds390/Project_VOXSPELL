package models;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by edson on 9/10/16.
 */
public class MasterModel implements Serializable{
    Map<String, WordModel> _masterMap;
    private File _file = new File(".MasterModel.ser");


    public MasterModel(){
        if (_file.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(_file));
                try {
                    _masterMap = (HashMap<String, WordModel>) ois.readObject();
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
        }
    }


    public void addToMaster(WordModel wordModel){
        _masterMap.put(wordModel.getTitle(), wordModel);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(".MasterModel.ser"));
            oos.writeObject(_masterMap);
            oos.close();
        } catch (IOException e) {

        }

    }

    private Map getMasterMap(){
        return _masterMap;
    }

    public Set<String> getMapKeyset(){
        return _masterMap.keySet();
    }
}
