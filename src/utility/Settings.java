package utility;

import main.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SuppressWarnings("Duplicates")
public class Settings implements SettingInterface {
    public static Settings instance = null;

    private static final String SETTING_FILE_NAME = "Settings.txt";

    private static Map<String, String> settingsMap = null;

    private static final String escapeCharacter = "\\";
    private static final String characterToEscape = "\"";

    private static final String DEFAULT_FOLDER_LOCATION = "E:\\poliv\\Videos\\TEW BII WATCHED";

    public static Settings getInstance(){
        if(instance == null)
            instance = new Settings();

        return instance;
    }

    private Settings(){
        if(!loadSettings()){
            resetSettings();
        }

    }

    public String getValue(String key){
        return settingsMap.get(key);
    }

    public String getValue(SettingsKeys key){
        return getValue(String.valueOf(key));
    }

    private String mapToString(){
        StringBuilder s = new StringBuilder();

        for(String setting : settingsMap.keySet()){
            String value = escapeString(settingsMap.get(setting));
            setting = escapeString(setting);
            String temp = "\""+setting+"\" : \""+value+"\"\n";
            s.append(temp);
        }

        return s.toString();
    }

    private Map<String, String> stringToMap(String string){

        return inputToMap(new Scanner(string));
    }

    private Map<String, String> inputToMap(Scanner slevel0){
        HashMap<String, String> map = new HashMap<>();

        slevel0.useDelimiter("\n");
        while(slevel0.hasNext()){
            Scanner sLevel1 = new Scanner(slevel0.next());
            sLevel1.useDelimiter("\" : \"");
            String key = getKeyFromLevel1Parser(sLevel1.next());
            String value = getValueFromLevel1Parser(sLevel1.next());
            map.put(key, value);

        }
        return map;
    }

    private Map<String, String> fileToMap(File file) throws FileNotFoundException {
        return inputToMap(new Scanner(file));
    }

    private String getKeyFromLevel1Parser(String s){
        s = s.substring(1);

        return unEscapeString(s);
    }

    private String getValueFromLevel1Parser(String s){
        s = s.substring(0, s.length() - 1);

        return unEscapeString(s);
    }

    @Override
    public boolean saveSettings() {
        boolean success = false;
        String save = mapToString();

        System.out.println(save);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(SETTING_FILE_NAME));

            fileWriter.write(save);
            fileWriter.close();
            success = true;
        } catch (IOException e) {
            Main.outputError("Settings not saved", e);
            e.printStackTrace();
            success = false;
        }finally {
            if(fileWriter != null){
                try{
                    fileWriter.close();
                } catch (IOException e) {
                    Main.outputError("Settings not saved", e);
                    e.printStackTrace();
                    success = false;
                }
            }
        }

        return success;
    }

    @Override
    public boolean loadSettings() {
        boolean success = false;
        try{
            settingsMap = fileToMap(new File(SETTING_FILE_NAME));
            success = settingsMap.size() > 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Main.outputError("Could not load settings", e);
            success = false;
        }
        return success;
    }

    @Override
    public void resetSettings() {
        settingsMap = new HashMap<>();
        settingsMap.put(String.valueOf(SettingsKeys.FOLDER_LOCATION), DEFAULT_FOLDER_LOCATION);
    }

    public static String escapeString(String string){
        return generalEscapeString(escapeCharacter, characterToEscape, string);
    }

    public static String unEscapeString(String string){
        return generalUnEscapeString(escapeCharacter, characterToEscape, string);
    }

    private static String generalEscapeString(String escapeCharacter, String characterToEscape, String string){

        string = string.replace(escapeCharacter, escapeCharacter+escapeCharacter);
        return string.replace(characterToEscape, characterToEscape+escapeCharacter);
    }


    private static String generalUnEscapeString(String escapeCharacter, String characterToEscape, String string){

        string = string.replace(characterToEscape+escapeCharacter, characterToEscape);
        return string.replace(escapeCharacter+escapeCharacter, escapeCharacter);
    }


    public enum SettingsKeys{
        FOLDER_LOCATION
    }
}
