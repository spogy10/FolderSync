package utility;

import logger.LoggerInterface;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;

@Plugin(
        name = "StringBuilderAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class StringBuilderAppender extends AbstractAppender {

    private static final StringBuilder stringLogger = new StringBuilder();
    private static LoggerInterface loggerInterface;

    protected StringBuilderAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    @PluginFactory
    public static StringBuilderAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout) {
        return new StringBuilderAppender(name, filter, layout, false, new Property[]{});
    }


    @Override
    public void append(LogEvent logEvent) {
        String message = new String(this.getLayout().toByteArray(logEvent));
        stringLogger.append(message);
        if (loggerInterface != null)
            loggerInterface.updateLogger(message);
    }

    public static String getLoggerText(){
        return stringLogger.toString();
    }

    public static void setLoggerInterface(LoggerInterface loggerInterface){
        StringBuilderAppender.loggerInterface = loggerInterface;
    }
}
