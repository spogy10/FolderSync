package utility;

import main.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Settings implements SettingInterface {
    private static Settings instance = null;

    private static final String SETTING_FILE_NAME = "Settings.txt";

    private static Map<String, String> settingsMap = null;

    private static final String escapeCharacter = "\\";
    private static final String characterToEscape = "\"";

    private static final String DEFAULT_FOLDER_LOCATION = "E:\\poliv\\Videos\\TEW BII WATCHED";
    private static final int DEFAULT_SERVER_PORT_NUMBER = 4000;
    private static final int DEFAULT_SERVER_BACKLOG = 1;
    private static final String DEFAULT_FILE_EXTENSIONS = "mp4,mkv,flv";

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

    private String getValue(String key){
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
        String save = mapToString();
        Main.outputVerbose("Settings: "+save);

        try {
            return FileManager.WriteFile(SETTING_FILE_NAME, save);
        } catch (IOException e) {
            Main.outputError("Settings not saved", e);
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean loadSettings() {
        try{
            settingsMap = fileToMap(new File(SETTING_FILE_NAME));
            return settingsMap.size() > 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Main.outputError("Could not load settings", e);
        }
        return false;
    }

    @Override
    public void resetSettings() {
        settingsMap = new HashMap<>();
        settingsMap.put(String.valueOf(SettingsKeys.FOLDER_LOCATION), DEFAULT_FOLDER_LOCATION);
        settingsMap.put(String.valueOf(SettingsKeys.SERVER_PORT_NUMBER), String.valueOf(DEFAULT_SERVER_PORT_NUMBER));
        settingsMap.put(String.valueOf(SettingsKeys.SERVER_BACKLOG), String.valueOf(DEFAULT_SERVER_BACKLOG));
        settingsMap.put(String.valueOf(SettingsKeys.FILE_EXTENSIONS), DEFAULT_FILE_EXTENSIONS);
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
        FOLDER_LOCATION,
        SERVER_PORT_NUMBER,
        SERVER_BACKLOG,
        FILE_EXTENSIONS
    }
}
