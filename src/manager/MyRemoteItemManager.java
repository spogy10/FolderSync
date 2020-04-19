package manager;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import library.sharedpackage.communication.DataCarrier;
import library.sharedpackage.models.FileContent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.RequestSenderInterface;
import server.ServerHandler;

import java.util.LinkedList;
import java.util.List;

public class MyRemoteItemManager implements UpdatableRemoteItemManager {

    private static final Logger logger = LogManager.getLogger();

    private RequestSenderInterface requestSenderInterface;


    public void setRequestSenderInterface(RequestSenderInterface requestSenderInterface) {
        this.requestSenderInterface = requestSenderInterface;
    }

    public MyRemoteItemManager(){
        requestSenderInterface = ServerHandler.getInstance(this);
    }

    public MyRemoteItemManager(RequestSenderInterface requestSenderInterface){
        this.requestSenderInterface = requestSenderInterface;
    }

    public static boolean responseCheck(DataCarrier response) {
        if(!response.isRequest()) {
            if (!response.getInfo().IsErrorCode) {
                logger.debug("No Error Code");
                return true;
            }

            logger.debug(response.getInfo().toReadableString());
        }else{
            logger.debug("Response set as request");
        }

        return false;
    }

    @Override
    public boolean addItems(List<FileContent> files) {
        DataCarrier carrier = requestSenderInterface.addItems((LinkedList<FileContent>) files);
        if(responseCheck(carrier) && (boolean) carrier.getData()){
            logger.info("Remote add items succeeded");
            return true;
        }
        logger.warn("Remote add items failed");
        return false;
    }

    @Override
    public boolean isRequestSenderSetup() {
        if(requestSenderInterface == null)
            return false;

        DataCarrier carrier = requestSenderInterface.testConnection();
        if(!responseCheck(carrier)){
            logger.warn("Connection not setup");
            return false;
        }

        if(!( (boolean) carrier.getData() )){
            logger.warn("Connection Setup result returned false");
            return false;
        }

        logger.debug("Connection setup");

        return true;
    }

    @Override
    public StringProperty getUpdateProperty() {
        return requestSenderInterface != null? requestSenderInterface.getUpdateProperty() : null;
    }

    @Override
    public DoubleProperty getProgressProperty() {
        return requestSenderInterface != null? requestSenderInterface.getProgressProperty() : null;
    }

    @Override
    public boolean removeItems(List<String> fileNames) {
        DataCarrier carrier = requestSenderInterface.removeItems((LinkedList<String>) fileNames);
        if(responseCheck(carrier) && (boolean) carrier.getData()){
            logger.info("Remote remove items succeeded");
            return true;
        }
        logger.warn("Remote remove items failed");
        return false;
    }

    @Override
    public List<String> getItemsList() {
        DataCarrier carrier = requestSenderInterface.getItemsList();
        if(responseCheck(carrier)){
            logger.info("Remote get items list succeeded");
            return (LinkedList<String>) carrier.getData();
        }
        logger.warn("Remote get items list failed");
        return new LinkedList<>();
    }

    @Override
    public List<FileContent> getItems(List<String> fileNames) {
        DataCarrier carrier = requestSenderInterface.getItems((LinkedList<String>) fileNames);
        if(responseCheck(carrier)){
            logger.info("Remote get items succeeded");
            return (List<FileContent>) carrier.getData();
        }
        logger.warn("Remote get items failed");
        return new LinkedList<>();
    }


}
