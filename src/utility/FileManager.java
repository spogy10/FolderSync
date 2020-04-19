package utility;

import exceptions.DirectoryCreationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class FileManager {

    private static final Logger logger = LogManager.getLogger();

    private static void createDirectoryIfNotExists(String directory) throws DirectoryCreationException {
        File file = new File(directory);
        if (file.isDirectory())
            return;

        if(!file.mkdir())
            throw new DirectoryCreationException("Unable to create directory: "+directory);
    }

    public static void WriteStringToFile(String directory, String fileName, String content) throws IOException{
        WriteStringToFile(directory+File.separator+fileName, content);
    }

    public static void WriteStringToFile(String fileName, String content) throws IOException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(fileName));
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            logger.error("Error writing to file "+fileName+". "+e.getMessage(), e);
            e.printStackTrace();
            throw e;
        }finally {
            if(fileWriter != null){
                try{
                    fileWriter.close();
                } catch (IOException e) {
                    logger.error("Error closing file writer for "+fileName+". "+e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        }
    }

    public static <T> void WriteFile(String directory, String fileName, T content) throws IOException, DirectoryCreationException {
        createDirectoryIfNotExists(directory);

        WriteFile(directory+File.separator+fileName, content);
    }

    public static <T> void WriteFile(String fileName, T content) throws IOException {
        ObjectOutputStream oos = null;
        String errorMessage = "Unable to write object to file: "+fileName+".";

        try {
            File file = new File(fileName);

            oos = new ObjectOutputStream(new FileOutputStream(file));

            oos.writeObject(content);
            oos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error(errorMessage+" "+e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(errorMessage+" "+e.getMessage(), e);
            throw e;
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("Error closing ObjectOutputStream for: "+fileName+". "+e.getMessage(), e);
                }
            }
        }
    }

    public static <T> T ReadFile(String directory, String fileName) throws IOException, ClassNotFoundException {
        return ReadFile(directory+File.separator+fileName);
    }

    public static <T> T ReadFile(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = null;
        String errorMessage = "Error reading from file: "+fileName+".";
        try {
            File file = new File(fileName);
            ois = new ObjectInputStream(new FileInputStream(file));

            return (T) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error(errorMessage+" "+e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(errorMessage+" "+e.getMessage(), e);
            throw e;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.error(errorMessage+" "+e.getMessage(), e);
            throw e;
        } finally {
            if (ois != null)
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("Error closing ObjectInputStream for: "+fileName+". "+e.getMessage(), e);
                }
        }
    }
}
