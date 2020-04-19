package utility;

import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Resources {
    private static final Logger logger = LogManager.getLogger();

    public static final String ASSETS_FOLDER = System.getProperty("user.dir")+"\\assets\\";
    public static final String SYNC_ICON = "sync_green.png";
    public static final String OPTIONS_ICON = "settings_grey.png";
    public static final String BACK_ARROW_ICON = "arrow_back_grey.png";


    public static Image getImage(String fileName){
        Image image = null;
        try {
            image = new Image(new FileInputStream(ASSETS_FOLDER+fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        return image;
    }
}
