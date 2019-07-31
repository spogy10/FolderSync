package manager;

import library.sharedpackage.communication.DC;
import library.sharedpackage.communication.DataCarrier;
import library.sharedpackage.manager.RemoteItemManager;
import main.Main;
import library.sharedpackage.models.FileContent;
import server.RequestSenderInterface;
import server.ServerHandler;

import java.util.LinkedList;
import java.util.List;

public class MyRemoteItemManager implements RemoteItemManager {

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
            if (response.getInfo().equals(DC.NO_ERROR)) {
                Main.outputVerbose("NO ERROR");
                return true;
            }

            if (response.getInfo().equals(DC.SERVER_CONNECTION_ERROR)) {
                Main.outputVerbose("SERVER CONNECTION ERROR");
                return false;
            }

            if (response.getInfo().equals(DC.REMOTE_SERVER_ERROR)) {
                Main.outputVerbose("REMOTE SERVER ERROR");
                return false;
            }

            if (response.getInfo().equals(DC.GENERAL_ERROR)) {
                Main.outputVerbose("GENERAL ERROR");
                return false;
            }
            return true;
        }

        Main.outputVerbose("Response set as request");

        return false;

    }

    @Override
    public boolean addItems(List<FileContent> files) {
        DataCarrier carrier = requestSenderInterface.addItems((LinkedList<FileContent>) files);
        if(responseCheck(carrier)){
            Main.outputVerbose("Remote add items succeeded");
            return (boolean) carrier.getData();
        }
        Main.outputVerbose("Remote add items failed");
        return false;
    }

    @Override
    public boolean removeItems(List<String> fileNames) {
        DataCarrier carrier = requestSenderInterface.removeItems((LinkedList<String>) fileNames);
        if(responseCheck(carrier)){
            Main.outputVerbose("Remote remove items succeeded");
            return (boolean) carrier.getData();
        }
        Main.outputVerbose("Remote remove items failed");
        return false;
    }

    @Override
    public List<String> getItemsList() {
        DataCarrier carrier = requestSenderInterface.getItemsList();
        if(responseCheck(carrier)){
            Main.outputVerbose("Remote get items list succeeded");
            return (LinkedList<String>) carrier.getData();
        }
        Main.outputVerbose("Remote get items list failed");
        return new LinkedList<>();
    }

    @Override
    public List<FileContent> getItems(List<String> fileNames) {
        DataCarrier carrier = requestSenderInterface.getItems((LinkedList<String>) fileNames);
        if(responseCheck(carrier)){
            Main.outputVerbose("Remote get items succeeded");
            return (LinkedList<FileContent>) carrier.getData();
        }
        Main.outputVerbose("Remote get items failed");
        return new LinkedList<>();
    }


}
