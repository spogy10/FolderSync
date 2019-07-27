package server;

import communication.DC;
import communication.DataCarrier;
import main.Main;
import manager.ItemManager;
import models.FileContent;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerHandler implements Runnable, RequestSenderInterface {
    private static final String CONNECTION_RESET_EXCEPTION_STRING = "java.net.SocketException: Connection reset";
    private static final String END_OF_FILE_EXCEPTION_STRING = "java.io.EOFException";

    private Server server;
    private ItemManager remoteManager;

    private AtomicBoolean unreadResponse = new AtomicBoolean(false);
    private DataCarrier tempResponseHolder;

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
        server = Server.getInstance(this);
    }


    @Override
    public void run() {
        Main.outputVerbose("ServerHandler thread started");

        DC action = DC.NO_INFO;
        try{
            while (!action.equals(DC.DISCONNECT)){
                DataCarrier carrier = server.receiveObject();
                if(carrier.isRequest()){
                    action = carrier.getInfo();
                    //response = new DataCarrier(DC.NO_ERROR,false);
                    caseStatements(carrier);
                }else {//it is a response
                    tempResponseHolder = carrier;
                    unreadResponse.compareAndSet(false, true);

                }
            }
            Main.outputVerbose("client disconnected from server normally");
        } catch (IOException e) {
            String message = "Error occurred in ServerHandler run method";
            if(e.toString().equals(CONNECTION_RESET_EXCEPTION_STRING) || e.toString().equals(END_OF_FILE_EXCEPTION_STRING))
                message = "client disconnected from server";
            else
                e.printStackTrace();
            Main.outputError(message, e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Main.outputError("Error occurred in ServerHandler run method", e);
        } finally {
            server.endServer();
        }
    }

    private void caseStatements(DataCarrier carrier) {
        switch (carrier.getInfo()){
            case SYNC_LISTS:
                Main.sync();
                break;

            default:
        }
    }

    private DataCarrier sendRequest(DataCarrier request, boolean responseRequired){
        DataCarrier response = new DataCarrier(DC.SERVER_CONNECTION_ERROR, false);
        try{
            server.sendObject(request);

            if(responseRequired) {
                response = waitForResponse();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError("Error sending request", e);
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


    @Override
    public DataCarrier getItemsList(){
        DataCarrier request = new DataCarrier(DC.GET_ITEM_LIST, true);

        return sendRequest(request, true);
    }

    @Override
    public DataCarrier addItems(List<FileContent> files) {
        DataCarrier request = new DataCarrier(DC.ADD_ITEMS, true);

        return sendRequest(request, true);
    }

    @Override
    public DataCarrier removeItems(List<String> fileNames) {
        DataCarrier request = new DataCarrier(DC.REMOVE_ITEMS, true);

        return sendRequest(request, true);
    }
}
