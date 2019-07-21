package models;

import exceptions.StatusNotIntializedException;
import main.Main;
import manager.ItemManager;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Status implements ItemManager {
    private static HashSet<String> status;
    private static final String FOLDER_NAME = "Status Folder";
    private static final String FILE_NAME = "status.sync";




    private static void isStatusInitialized() throws StatusNotIntializedException {
        if(status == null)
            throw new StatusNotIntializedException();
    }

    public static boolean saveStatus() throws StatusNotIntializedException {
        isStatusInitialized();
        String errorMessage = "Status file not saved created";

        boolean success = false;
        ObjectOutputStream oos = null;
        try {
            File file = new File(FOLDER_NAME);
            if (!file.isDirectory())
                file.mkdir();
            File statusFile = new File(file, FILE_NAME);

            oos = new ObjectOutputStream(new FileOutputStream(statusFile));

            oos.writeObject(status);
            oos.flush();

            success = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Main.outputError(errorMessage, e);
        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError(errorMessage, e);
        } finally {
            if (oos == null)
                success = false;
            else {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Main.outputError(errorMessage, e);
                    success = false;
                }
            }
        }
        return success;
    }

    private static boolean retrieveStatusFromFile() {
        boolean success = false;
        ObjectInputStream ois = null;

        String errorMessage = "Status could not be retrieved from file";

        try {
            File statusFile = new File(FOLDER_NAME, FILE_NAME);
            ois = new ObjectInputStream(new FileInputStream(statusFile));

            status = (HashSet<String>) ois.readObject();

            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Main.outputError(errorMessage, e);
        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError(errorMessage, e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Main.outputError(errorMessage, e);
        } finally {
            if (ois != null)
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Main.outputError(errorMessage, e);
                    success = false;
                }

        }
        return success;
    }

    public static boolean setUpStatus(){
        File statusFile = new File(FOLDER_NAME, FILE_NAME);
        if(statusFile.exists())
            return retrieveStatusFromFile();

        initializeStatus();

        return true;
    }


    private static void initializeStatus(){
        status = new HashSet<>();
    }

    public static List<String> getStatusList() throws StatusNotIntializedException {
        isStatusInitialized();

        return new LinkedList<>(status);
    }

    public static void addToStatus(List<String> fileNames){
        String errorMessage = "Status not initialized in addToStatus method";

        try{
            isStatusInitialized();
        } catch (StatusNotIntializedException e) {
            Main.outputError(errorMessage, e);
            initializeStatus();
        }

        status.addAll(fileNames);
    }

    public static void removeFromStatus(List<String> fileNames) throws StatusNotIntializedException {
        isStatusInitialized();

        status.removeAll(fileNames);
    }

    @Override
    public boolean addItems(List<FileContent> files) {
        String errorMessage = "Status not initialized in addToStatus method";

        try{
            isStatusInitialized();
        } catch (StatusNotIntializedException e) {
            Main.outputError(errorMessage, e);
            initializeStatus();
            return false;
        }
        for(FileContent fileName : files){
            status.add(fileName.getFileName());
        }

        return true;
    }

    @Override
    public boolean removeItems(List<String> fileNames) {
        try {
            removeFromStatus(fileNames);
        } catch (StatusNotIntializedException e) {
            e.printStackTrace();
            Main.outputError("Error in removeItems for Status", e);
            return false;
        }

        return true;
    }

    @Override
    public List<String> getItemsList() {
        try{
            return getStatusList();
        } catch (StatusNotIntializedException e) {
            e.printStackTrace();
            Main.outputError("Error in getItemList for Status", e);
        }

        return new LinkedList<String>();
    }
}
