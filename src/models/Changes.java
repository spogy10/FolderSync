package models;

import java.util.LinkedHashMap;
import java.util.Map;

public class Changes { //false delete, true add
    private Map<String, Boolean> pc = new LinkedHashMap<>();
    private Map<String, Boolean> mobile = new LinkedHashMap<>();
    private Map<String, Boolean> stat = new LinkedHashMap<>();


    public Map<String, Boolean> getPc() {
        return pc;
    }

    public Map<String, Boolean> getMobile() {
        return mobile;
    }

    public Map<String, Boolean> getStat() {
        return stat;
    }

    public void addToStatus(String fileName){
        stat.put(fileName, true);
    }

    public void removeFromStatus(String fileName){
        stat.put(fileName, false);
    }

    private void addToA(String fileName){
        pc.put(fileName, true);
    }

    private void removeFromA(String fileName){
        pc.put(fileName, false);
    }

    private void addToB(String fileName){
        mobile.put(fileName, true);
    }

    private void removeFromB(String fileName){
        mobile.put(fileName, false);
    }

    public void addToBAndStatus(String fileName){
        addToB(fileName);
        addToStatus(fileName);
    }

    public void addToAAndStatus(String fileName){
        addToA(fileName);
        addToStatus(fileName);
    }

    public void removeFromAAndStatus(String fileName){
        removeFromA(fileName);
        removeFromStatus(fileName);
    }

    public void removeFromBAndStatus(String fileName){
        removeFromB(fileName);
        removeFromStatus(fileName);
    }

}
