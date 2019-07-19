package utility;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Resources {

    public static final String ASSETS_FOLDER = System.getProperty("user.dir")+"\\assets\\";
    public static final String SYNC_ICON = "sync_green.png";
    public static final String OPTIONS_ICON = "settings_grey.png";




    public static Image getImage(String fileName){
        Image image = null;
        try {
            image = new Image(new FileInputStream(ASSETS_FOLDER+fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return image;
    }
}
