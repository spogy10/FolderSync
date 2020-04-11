package manager;

import exceptions.MyFileManagerNotInitializedException;
import main.Main;
import library.sharedpackage.manager.ItemManager;
import library.sharedpackage.models.FileContent;
import org.apache.commons.io.FileUtils;
import utility.Settings;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MyFileManager implements ItemManager, FileFilter {

    private static File folder;

    private final List<String> FILE_EXTENSIONS_LIST;

    private static MyFileManager instance;

    public static MyFileManager getInstance(Settings settings){
        instance = new MyFileManager(settings);

        return instance;
    }

    public static MyFileManager getInstance() throws MyFileManagerNotInitializedException {
        if(instance == null)
            throw new MyFileManagerNotInitializedException();

        return instance;
    }

    private MyFileManager(Settings settings){
        folder = new File(settings.getValue(Settings.SettingsKeys.FOLDER_LOCATION));
        FILE_EXTENSIONS_LIST = getFileExtensionList(settings.getValue(Settings.SettingsKeys.FILE_EXTENSIONS));
    }

    @Override
    public boolean removeItems(List<String> fileNames) {
        boolean success = true;
        String informationMessageHeader = "MyFileManager.removeItems";
        for(String fileName : fileNames){
            try{

                boolean result = Files.deleteIfExists(new File(folder, fileName).toPath());
                if(result)
                    Main.outputInformation(informationMessageHeader+" "+fileName+" deleted.");
                else
                    Main.outputInformation(informationMessageHeader+" "+fileName+" does not exist.");
            } catch (IOException e) {
                e.printStackTrace();
                String message = informationMessageHeader + " Error deleting file :"+fileName;
                Main.outputError(message, e);
                success = false;
            }
        }

        return success;
    }

    @Override
    public List<String> getItemsList() {
        LinkedList<String> list = new LinkedList<>();

        for(File file : folder.listFiles(this)){
            list.add(file.getName());
        }

        return list;
    }

    @Override
    public List<FileContent> getItems(List<String> fileNames) {
        LinkedList<FileContent> list = new LinkedList<>();

        for(String fileName : fileNames){
            list.add(retrieveFile(fileName));
        }

        Main.outputVerbose("created list of A files");
        return list;
    }

    @Override
    public boolean accept(File pathname) {
        boolean isFile, isRightFileType;

        String fileExtension = getFileExtension(pathname.getName());

        isFile = pathname.isFile();

        isRightFileType = FILE_EXTENSIONS_LIST.contains(fileExtension);

        return isFile && isRightFileType;
    }

    private LinkedList<String> getFileExtensionList(String fileExtensionList){
        String[] array = fileExtensionList.split(",");
        return new LinkedList<>(Arrays.asList(array));
    }

    private String getFileExtension(String fileName){
        int lastIndex = fileName.lastIndexOf('.');

        return (lastIndex > 0) ? fileName.substring(++lastIndex) : "";
    }

    private FileContent retrieveFile(String fileName){
        FileContent fileContent = null;

        File file = new File(folder, fileName);
        if(file.isFile()){
            fileContent = new FileContent(fileName, FileUtils.sizeOf(file));
        }else{
            Main.outputVerbose("error retrieving file:" + fileName +  "does not exist");
        }


        return fileContent;
    }
}
