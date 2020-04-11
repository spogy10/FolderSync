package utility;

import main.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

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
}
