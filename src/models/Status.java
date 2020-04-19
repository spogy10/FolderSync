package models;

import exceptions.StatusNotInitializedException;
import main.Main;
import library.sharedpackage.manager.ItemManager;
import library.sharedpackage.models.FileContent;
import utility.FileManager;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Status implements ItemManager {
    private static HashSet<String> status;
    private static final String FOLDER_NAME = "Status Folder";
    private static final String FILE_NAME = "status.sync";




    private static void isStatusInitialized() throws StatusNotInitializedException {
        if(status == null)
            throw new StatusNotInitializedException();
    }

    public static boolean saveStatus() throws StatusNotInitializedException {
        isStatusInitialized();
        String errorMessage = "Status file not saved";

        try {
            FileManager.WriteFile(FOLDER_NAME, FILE_NAME, status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Main.outputError(errorMessage, e);
        }

        return false;
    }

    private static boolean retrieveStatusFromFile() {
        String errorMessage = "Status could not be retrieved from file";

        try {
            status = FileManager.ReadFile(FOLDER_NAME, FILE_NAME);
            return true;
        }  catch (Exception e) {
            e.printStackTrace();
            Main.outputError(errorMessage, e);
        }
        return false;
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

    public static List<String> getStatusList() throws StatusNotInitializedException {
        isStatusInitialized();

        return new LinkedList<>(status);
    }

    public static void addToStatus(List<String> fileNames){
        String errorMessage = "Status not initialized in addToStatus method";

        try{
            isStatusInitialized();
        } catch (StatusNotInitializedException e) {
            Main.outputError(errorMessage, e);
            initializeStatus();
        }

        status.addAll(fileNames);
    }

    public static void removeFromStatus(List<String> fileNames) throws StatusNotInitializedException {
        isStatusInitialized();

        status.removeAll(fileNames);
    }

    public static void clearStatusList() throws StatusNotInitializedException {
        isStatusInitialized();

        status.clear();
    }

    @Override
    public boolean removeItems(List<String> fileNames) {
        try {
            removeFromStatus(fileNames);
        } catch (StatusNotInitializedException e) {
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
        } catch (StatusNotInitializedException e) {
            e.printStackTrace();
            Main.outputError("Error in getItemList for Status", e);
        }

        return new LinkedList<>();
    }

    @Override
    public List<FileContent> getItems(List<String> fileNames) {
        return null;
    }
}
