package utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;

public class LoggerUtility {
    private static final Logger logger = LogManager.getLogger();

    public static void configureLogger(){
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File("config/log4j2.json");

        context.setConfigLocation(file.toURI());
        logger.info("Logger json configuration loaded");
    }
}