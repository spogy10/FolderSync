package utility;

import main.Main;

import java.io.*;
import java.util.HashSet;

public class FileManager {

    public static boolean WriteFile(String directory, String fileName, String content) throws IOException{
        return WriteFile(directory+File.separator+fileName, content);
    }

    public static boolean WriteFile(String fileName, String content) throws IOException {
        boolean success = false;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(fileName));
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
            success = true;
        } catch (IOException e) {
            Main.outputError("Error writing to file "+fileName, e);
            e.printStackTrace();
            throw e;
        }finally {
            if(fileWriter != null){
                try{
                    fileWriter.close();
                } catch (IOException e) {
                    Main.outputError("Error closing file writer for "+fileName, e);
                    e.printStackTrace();
                }
            }
        }

        return success;
    }

    public static <T> T ReadFile(String directory, String fileName) throws IOException, ClassNotFoundException {
        return ReadFile(directory+File.separator+fileName);
    }

    public static <T> T ReadFile(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = null;
        String errorMessage = "FileManager.ReadFile: Error reading from file: "+fileName;
        try {
            File file = new File(fileName);
            ois = new ObjectInputStream(new FileInputStream(file));

            return (T) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Main.outputError(errorMessage, e);
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError(errorMessage, e);
            throw e;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Main.outputError(errorMessage, e);
            throw e;
        } finally {
            if (ois != null)
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Main.outputError(errorMessage, e);
                }
        }
    }
}
