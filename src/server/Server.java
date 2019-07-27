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

    private Runnable runnable;


    private static Server ourInstance;
    private boolean serverOff = true;

    static Server getInstance(Runnable runnable){
       if(ourInstance != null)
           ourInstance.endServer();

       ourInstance = new Server(runnable);

       return ourInstance;
    }

    static Server getInstance(){
        if(ourInstance == null || ourInstance.runnable == null)
            return null;

        return ourInstance;
    }

    private Server(Runnable runnable) {
        this.runnable = runnable;
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
            if(initStreams()){
                Main.outputVerbose("connection received");
                if(runnable != null){
                    Thread t = new Thread(runnable);
                    t.start();
                    return;
                }
                Main.outputVerbose("Could not start ServerHandler Thread");
            }

            endServer();

        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError("Error starting server", e);
        }
    }

    void restartServer(){
        Main.outputVerbose("Restarting Server");
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

    private void notifyRequestSent(String request) {
        Main.outputVerbose("Request: "+request+" sent");
    }

    private void notifyResponseSent(String response){
        Main.outputVerbose("Response: "+response+" sent");
    }

    private void notifyRequestReceived(String request) {
        Main.outputVerbose("Request "+request+" received");
    }

    private void notifyResponseReceived(String response){
        Main.outputVerbose("Response "+response+" received");
    }

    void sendObject(DataCarrier dc) throws IOException {
        os.writeObject(dc);
        if(dc.isRequest())
            notifyRequestSent(dc.getInfo().toString());
        else
            notifyResponseSent(dc.getInfo().toString());
    }

    DataCarrier receiveObject() throws IOException, ClassNotFoundException {
        DataCarrier dc = (DataCarrier) is.readObject();
        if(dc.isRequest())
            notifyRequestReceived(dc.getInfo().toString());
        else
            notifyResponseReceived(dc.getInfo().toString());
        return dc;
    }
    
    private void closeConnection(){
        try {
            os.close();
            is.close();
            connection.close();
            Main.outputVerbose("Server connections closed");
        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError("Error closing connections", e);
        }
    }
    
    public boolean areStreamsInitialized(){
       return connection != null && os != null && is != null;
    }

    void endServer(){
        try {
            closeConnection();
            serverSocket.close();
            Main.outputVerbose("Server ended");
        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError("Error ending server", e);
        }
        
        serverOff = true;
    }




}
