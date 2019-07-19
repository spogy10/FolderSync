package utility;

public class Settings implements SettingInterface {
    public static Settings instance = null;

    public static String FOLDER_LOCATION = "E:\\poliv\\Videos\\TEW BII WATCHED";

    public static Settings getInstance(){
        if(instance == null)
            instance = new Settings();

        return instance;
    };

    private Settings(){
        loadSettings();
    }

    @Override
    public boolean saveSettings() {
        return false;
    }

    @Override
    public boolean loadSettings() {
        return false;
    }

    @Override
    public boolean resetSettings() {
        return false;
    }
}
