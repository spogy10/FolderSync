package manager;

import exceptions.MyFileManagerNotInitializedException;
import main.Main;
import models.FileContent;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MyFileManager implements ItemManager, FileFilter {

    private static File folder;

    private static final String[] FILE_EXTENSIONS = new String[]{"mp4", "mkv", "flv"};

    private final List FILE_EXTENSIONS_LIST;

    private static MyFileManager instance;

    public static MyFileManager getInstance(String folderPath){
        instance = new MyFileManager(folderPath);

        return instance;
    }

    public static MyFileManager getInstance() throws MyFileManagerNotInitializedException {
        if(instance == null)
            throw new MyFileManagerNotInitializedException();

        return instance;
    }

    private MyFileManager(String folderPath){
        folder = new File(folderPath);
        FILE_EXTENSIONS_LIST = new LinkedList(Arrays.asList(FILE_EXTENSIONS));
    }



    @Override
    public boolean addItems(List<FileContent> files) {
        boolean success = true;
        for(FileContent file : files){
            if(!createFile(file))
                success = false;
        }

        return success;
    }

    @Override
    public boolean removeItems(List<String> fileNames) {
        boolean success = true;
        for(String fileName : fileNames){
            try{

                Files.deleteIfExists(new File(folder, fileName).toPath());

            } catch (IOException e) {
                e.printStackTrace();
                String message = "Error deleting file :"+fileName;
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

        return null;
    }

    @Override
    public boolean accept(File pathname) {
        boolean isFile, isRightFileType;

        String fileExtension = getFileExtension(pathname.getName());

        isFile = pathname.isFile();

        isRightFileType = FILE_EXTENSIONS_LIST.contains(fileExtension);

        return isFile && isRightFileType;
    }

    private String getFileExtension(String fileName){
        int lastIndex = fileName.lastIndexOf('.');

        return (lastIndex > 0) ? fileName.substring(++lastIndex) : "";
    }

    private boolean createFile(FileContent fileContent){
        boolean success = false;
        try {
            Files.write(new File(folder, fileContent.getFileName()).toPath(), fileContent.getData());
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError("Error unable to create file :"+fileContent.getFileName(), e);
            success = false;
        }

        return success;
    }
}
