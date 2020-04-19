package models;

import exceptions.StatusNotInitializedException;
import library.sharedpackage.manager.ItemManager;
import library.sharedpackage.models.FileContent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.FileManager;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Status implements ItemManager {

    private static final Logger logger = LogManager.getLogger();
    private static HashSet<String> status;
    private static final String FOLDER_NAME = "Status Folder";
    private static final String FILE_NAME = "status.sync";




    private static void isStatusInitialized() throws StatusNotInitializedException {
        if(status == null)
            throw new StatusNotInitializedException();
    }

    public static boolean saveStatus() throws StatusNotInitializedException {
        isStatusInitialized();

        try {
            FileManager.WriteFile(FOLDER_NAME, FILE_NAME, status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Status file not saved: " + e.getMessage(), e);
        }

        return false;
    }

    private static boolean retrieveStatusFromFile() {
        try {
            status = FileManager.ReadFile(FOLDER_NAME, FILE_NAME);
            return true;
        }  catch (Exception e) {
            e.printStackTrace();
            logger.error("Status could not be retrieved from file: "+e.getMessage(), e);
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
        try{
            isStatusInitialized();
        } catch (StatusNotInitializedException e) {
            logger.error("Status not initialized in addToStatus method: "+e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
        }

        return new LinkedList<>();
    }

    @Override
    public List<FileContent> getItems(List<String> fileNames) {
        return null;
    }
}
