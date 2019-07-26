package server;

import communication.DC;
import communication.DataCarrier;
import main.Main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.invoke.VarHandle;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
   private ServerSocket serverSocket = null;
    private Socket connection = null;
    private ObjectOutputStream os = null;
   private ObjectInputStream is = null;
   
    private AtomicBoolean unreadResponse = new AtomicBoolean(false);
    private DataCarrier tempResponseHolder;


    private static Server ourInstance;
    private boolean serverOff = true;

    static Server getInstance(){
       if(ourInstance == null)
           ourInstance = new Server();

       return ourInstance;
    }

    private Server() {
        setUpConnection();
    }
    
    
    boolean isServerOff(){
       return serverOff;
    }

    private void setUpConnection() {
        try{
            serverSocket = new ServerSocket(4000, 1);
            waitForRequests();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForRequests() {
        Main.outputVerbose("Waiting for requests");

        try{
            serverOff = false;
            connection = serverSocket.accept();
            Main.outputVerbose("connection received");

        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError("Error starting server", e);
        }
    }

    void restartServer(){
       endServer();
       setUpConnection();
    }


    private boolean initStreams() {
        try{
            if(connection == null)
                return false;

            os = new ObjectOutputStream(connection.getOutputStream());
            is = new ObjectInputStream(connection.getInputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError("Error initializing streams", e);
        }

        return false;
    }

    private DataCarrier sendRequest(DataCarrier request, boolean responseRequired){
        DataCarrier response = new DataCarrier(DC.SERVER_CONNECTION_ERROR, false);
        try{
            os.writeObject(request);
            Main.outputVerbose("Request: "+request.getInfo()+" sent");
            if(responseRequired) {
                response = waitForResponse();
                Main.outputVerbose("Response for "+request.getInfo()+" received");
            }
        } catch (IOException e) {
            e.printStackTrace();
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
    
    private void closeConnection(){
        try {
            os.close();
            is.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError("Error closing connections", e);
        }
    }
    
    public boolean areStreamsInitialized(){
       return connection != null && os != null && is != null;
    }

    private void endServer(){
        try {
            closeConnection();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError("Error ending server", e);
        }
        
        serverOff = true;
    }




}
