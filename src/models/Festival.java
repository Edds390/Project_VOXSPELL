package models;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by ratterz on 18/09/16.
 */
public class Festival {
    private static String _currentVoice = "kal_diphone";
    private static ArrayList<String> _voiceList;

    /**
     * This method takes in a sentence and relays it to festival
     * @param phrase
     */
    public static void festivalTTS(String phrase) {
        try {
            createSCM(phrase);
            String command = "festival -b .voxspellVoices.scm";
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            Process p = pb.start();
            p.waitFor();
        } catch (Exception e) {

        }
    }

    private static void createSCM(String phrase) {
        String changeVoice = "(voice_" + _currentVoice + ")";
        String sayText = "(SayText \"" + phrase + "\")";
        BufferedWriter bw = null;
        File file = new File(".voxspellVoices.scm");

        checkFile(file);

        try {
            bw = new BufferedWriter(new FileWriter(".voxspellVoices.scm", true));
            bw.write(changeVoice);
            bw.newLine();
            bw.write("(Parameter.set 'Duration_Stretch 1.2)");
            bw.newLine();
            bw.write(sayText);
            bw.flush();
        } catch (IOException e) {

        } finally {
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) {

            }
        }
    }

    private static void checkFile(File file) {
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {

        }
    }

    public static void changeVoice(String voice) {
        _currentVoice = voice;
    }

    public static void findVoiceList() {
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", "ls /usr/share/festival/voices/english > .voxspellVoices.scm");
            Process p = pb.start();
            p.waitFor();
            storeVoiceList();
        } catch (Exception e) {

        }
    }

    private static void storeVoiceList() {
        _voiceList = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(".voxspellVoices.scm"));
            String line;
            while ((line = br.readLine()) != null) {
                _voiceList.add(line);
            }
            br.close();
        } catch (Exception e) {

        }
    }

    public static ArrayList<String> getVoiceList() {
        return _voiceList;
    }

    public static String _getVoice(){
        return _currentVoice;
    }
}
