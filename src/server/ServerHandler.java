package server;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import library.sharedpackage.communication.DC;
import library.sharedpackage.communication.DataCarrier;
import library.sharedpackage.manager.ItemManager;
import library.sharedpackage.models.FileContent;
import main.Main;
import manager.MyRemoteItemManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerHandler implements Runnable, RequestSenderInterface {
    private static final String CONNECTION_RESET_EXCEPTION_STRING = "java.net.SocketException: Connection reset";
    private static final String END_OF_FILE_EXCEPTION_STRING = "java.io.EOFException";
    private static final Logger logger = LogManager.getLogger();

    private final Server server;
    private final ItemManager remoteManager;

    private final AtomicBoolean unreadResponse = new AtomicBoolean(false);
    private DataCarrier tempResponseHolder;
    private final AtomicBoolean pauseThread = new AtomicBoolean(false);

    private final StringProperty updateProperty;
    private final DoubleProperty progressProperty;

    private static ServerHandler ourInstance = null;


    public static ServerHandler getInstance(ItemManager remoteManager){
        ourInstance = new ServerHandler(remoteManager);

        return ourInstance;
    }

    public static ServerHandler getInstance(){
        if(ourInstance == null || ourInstance.remoteManager == null)
            return null;

        return ourInstance;
    }

    private ServerHandler(ItemManager remoteManager){
        this.remoteManager = remoteManager;
        updateProperty = new SimpleStringProperty("");
        progressProperty = new SimpleDoubleProperty(0);
        server = Server.getInstance(this);
    }


    @Override
    public void run() {
        logger.debug("ServerHandler thread started");

        DC action = DC.NO_INFO;
        try{
            while (!action.equals(DC.DISCONNECT)){
                DataCarrier carrier = server.receiveObject();
                if(carrier.isRequest()){
                    action = carrier.getInfo();
                    caseStatements(carrier);
                }else {//it is a response
                    tempResponseHolder = carrier;
                    unreadResponse.compareAndSet(false, true);

                    if(carrier.getInfo() == DC.OK_TO_SEND_FILES || carrier.getInfo() == DC.GET_ITEMS){
                        pauseThread.compareAndSet(false, true);
                        while (pauseThread.get()){
                            Thread.onSpinWait();
                        }
                    }
                }
            }
            DataCarrier dc = new DataCarrier(true, DC.DISCONNECT);
            sendRequest(dc, false);
            logger.debug("Client disconnected from server normally");
        } catch (IOException e) {
            if(e.toString().equals(CONNECTION_RESET_EXCEPTION_STRING) || e.toString().equals(END_OF_FILE_EXCEPTION_STRING))
                logger.warn("Client disconnected from server: "+e.getMessage(), e);
            else
                logger.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        } catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        } finally {
            server.endServer();
            server.restartServer();
        }
    }



    //region REQUEST RESPONSE UTILITY METHODS

    private void caseStatements(DataCarrier carrier) {
        switch (carrier.getInfo()){
            case SYNC_LISTS:
                Main.sync();
                break;

            case FINISHED_SENDING_FILES:
                break;

            case CONNECTION_SETUP:
                connectionSetup(carrier);

            default:
        }
    }

    private DataCarrier sendRequest(DataCarrier request, boolean responseRequired){
        if(server.isServerOff() || !server.areStreamsInitialized()){
            String header = request.isRequest()? "Request:" : "Response:";
            logger.warn(header + " " + request.getInfo() + " failed to send because connection not setup");
            return new DataCarrier(false, DC.CONNECTION_NOT_SETUP);
        }

        DataCarrier response = new DataCarrier(false, DC.SERVER_CONNECTION_ERROR);
        try{
            server.sendObject(request);

            if(responseRequired) {
                response = waitForResponse();
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error sending request: "+e.getMessage(), e);
        }

        return response;
    }

    private DataCarrier waitForResponse() {
        while(!unreadResponse.get()){
            /*wait until response comes in*/
            Thread.onSpinWait();
        }

        unreadResponse.compareAndSet(true, false);
        return tempResponseHolder;
    }
    //endregion



    //region FILE TRANSFER

    private DataCarrier sendFiles(DataCarrier<LinkedList<FileContent>> request) {
        boolean success = true;
        DataCarrier cancelRequest = new DataCarrier(true, DC.CANCEL_OPERATION);
        DataCarrier<Boolean> finalResponse = new DataCarrier<>(false, DC.GENERAL_ERROR, false);
        DataCarrier initialResponse = sendRequest(request, true);

        if(!MyRemoteItemManager.responseCheck(initialResponse)){
            pauseThread.compareAndSet(true, false);
            logger.warn("Error in Server Handler sendFiles, response check returned false");
            sendRequest(cancelRequest, false);
            finalResponse.setInfo(DC.REMOTE_SERVER_ERROR);
            return finalResponse;
        }

        if(!initialResponse.getInfo().equals(DC.OK_TO_SEND_FILES)){
            pauseThread.compareAndSet(true, false);
            logger.warn("Error in Server Handler sendFiles, remote server not ready to handle files");
            return finalResponse;
        }

        logger.debug("Remote server ready to handle files");

        List<FileContent> files = request.getData();
        int count = 1;
        int total = files.size();
        for(FileContent fileContent : files){
            logger.info("Attempting to send "+fileContent.getFileName());
            DataCarrier<FileContent> sendFile = new DataCarrier<>(true, DC.ADD_ITEMS, fileContent);
            String message = String.format("Sending: %s (%d/%d)", fileContent.getFileName(), count, total);
            updateProperty(message);
            boolean currentSuccess = server.sendFile(sendFile, progressProperty);
            updateProperty("");
            logger.info("Sending "+fileContent.getFileName()+": "+currentSuccess);

            success = currentSuccess && success;

            count++;
        }

        logger.info("Finished sending files: "+success);

        finalResponse.setInfo(DC.NO_ERROR);
        finalResponse.setData(success);

        pauseThread.compareAndSet(true, false);
        DataCarrier finishedResponse = new DataCarrier(true, DC.FINISHED_SENDING_FILES);
        sendRequest(finishedResponse, false);

        return finalResponse;
    }

    private DataCarrier receiveFiles(DataCarrier<LinkedList<String>> request){
        boolean success = true;
        DataCarrier cancelRequest = new DataCarrier(true, DC.CANCEL_OPERATION);
        DataCarrier<LinkedList<FileContent>> finalResponse = new DataCarrier<>(false, DC.GENERAL_ERROR, null);
        DataCarrier initialResponse = sendRequest(request, true);

        if(!MyRemoteItemManager.responseCheck(initialResponse)){
            pauseThread.compareAndSet(true, false);
            logger.warn("Error in Server Handler receiveFiles, response check returned false");
            sendRequest(cancelRequest, false);
            finalResponse.setInfo(DC.REMOTE_SERVER_ERROR);
            return finalResponse;
        }

        if(!initialResponse.getInfo().equals(DC.GET_ITEMS)){
            pauseThread.compareAndSet(true, false);
            logger.warn("Error in Server Handler receiveFiles, remote server did not send correct response");
            return finalResponse;
        }else if ( !( (initialResponse.getData() instanceof LinkedList) && ((LinkedList) initialResponse.getData()).get(0) instanceof FileContent ) ){
            pauseThread.compareAndSet(true, false);
            logger.warn("Error in Server Handler receiveFiles, remote server did not send correct data");
            return finalResponse;
        }

        logger.debug("Remote server ready to send files");


        LinkedList<FileContent> files = (LinkedList<FileContent>) initialResponse.getData();
        int count = 1;
        int total = files.size();
        for(FileContent fileContent : files){
            logger.info("Attempting to receive "+fileContent.getFileName());
            DataCarrier<FileContent> receiveFile = new DataCarrier<>(false, DC.GET_ITEMS, fileContent);
            String message = String.format("Receiving: %s (%d/%d)", fileContent.getFileName(), count, total);
            updateProperty(message);
            boolean currentSuccess = server.receiveFile(receiveFile, progressProperty);
            updateProperty("");
            logger.info("Receiving "+fileContent.getFileName()+": "+currentSuccess);

            success = currentSuccess && success;

            count++;
        }

        logger.info("Finished receiving files: "+success);

        finalResponse.setInfo(success? DC.NO_ERROR : DC.GENERAL_ERROR);
        finalResponse.setData(files);

        pauseThread.compareAndSet(true, false);
        return finalResponse;
    }
    //endregion



    //region NOTIFICATION SECTION

    private void updateProperty(String updateMessage){
        logger.debug(updateMessage);
        Platform.runLater(() -> {
            updateProperty.setValue(updateMessage);
        });
    }

    @Override
    public StringProperty getUpdateProperty() {
        return updateProperty;
    }

    @Override
    public DoubleProperty getProgressProperty() {
        return progressProperty;
    }
    //endregion


    //region REQUESTS

    @Override
    public DataCarrier getItemsList(){
        DataCarrier<LinkedList> request = new DataCarrier<>(true, DC.GET_ITEM_LIST);

        return sendRequest(request, true);
    }

    @Override
    public DataCarrier getItems(LinkedList<String> fileNames) {
        DataCarrier<LinkedList<String>> request = new DataCarrier<>(true, DC.GET_ITEMS, fileNames);

        return receiveFiles(request);
    }

    @Override
    public DataCarrier addItems(LinkedList<FileContent> files) {
        DataCarrier<LinkedList<FileContent>> request = new DataCarrier<>(true, DC.ADD_ITEMS, files);

        return sendFiles(request);
    }

    @Override
    public DataCarrier removeItems(LinkedList<String> fileNames) {
        DataCarrier<LinkedList> request = new DataCarrier<>(true, DC.REMOVE_ITEMS, fileNames);

        return sendRequest(request, true);
    }

    private void connectionSetup(DataCarrier carrier) {
        DataCarrier<Boolean> response = new DataCarrier<>(false, DC.CONNECTION_SETUP, true);

        sendRequest(response, false);
    }

    @Override
    public DataCarrier testConnection() {
        DataCarrier request = new DataCarrier(true, DC.CONNECTION_SETUP);

        return sendRequest(request, true);
    }
    //endregion

}
