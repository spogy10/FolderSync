package server;

import library.sharedpackage.communication.DataCarrier;
import main.Main;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private ServerSocket serverSocket = null;
    private Socket connection = null;
    private ObjectOutputStream os = null;
    private ObjectInputStream is = null;

    private static final long BYTES_IN_2_GB = 2147483648L;

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
            Thread t = new Thread(this);
            t.start();
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
                    runnable.run();
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

    public boolean sendFile(DataCarrier dc){ //todo: create these methods
        //todo https://stackoverflow.com/questions/10367698/java-multiple-file-transfer-over-socket?answertab=votes#tab-top
        boolean success = false;

        if(dc.isRequest())
            Main.outputVerbose("Request"+ dc.getInfo() +"to send file commence");
        else
            Main.outputVerbose("Response"+ dc.getInfo() +"to send file commence");

        FileInputStream fis = null;
        try{
            String absoluteFilePath = (String) dc.getData();
            File file = new File(absoluteFilePath);
            fis = new FileInputStream(file);
            if(FileUtils.sizeOf(file) < BYTES_IN_2_GB)
                IOUtils.copy(fis, os);
            else
                IOUtils.copyLarge(fis, os);
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Main.outputError("Error sending file", e);
        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError("Error sending file", e);
        } finally {
            if(fis == null){
                success = false;
            }else{
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Main.outputError("Error closing input stream of send file method", e);
                    success = false;
                }
            }
        }
        if(success){
            if(dc.isRequest())
                notifyRequestSent(dc.getInfo().toString());
            else
                notifyResponseSent(dc.getInfo().toString());
            return true;
        }else{
            if(dc.isRequest())
                Main.outputVerbose("Could not send file for request: "+ dc.getInfo());
            else
                Main.outputVerbose("Could not send file for response: "+ dc.getInfo());
        }

        return false;
    }

    public boolean receiveFile(DataCarrier dc) {//todo fix method
        boolean success = false;

        if(dc.isRequest())
            Main.outputVerbose("Request"+ dc.getInfo() +"to receive file commence");
        else
            Main.outputVerbose("Response"+ dc.getInfo() +"to receive file commence");

        FileOutputStream fos = null;
        try{
            String absoluteFilePath = (String) dc.getData();
            File file = new File(absoluteFilePath);
            fos = new FileOutputStream(file);
            fos.write(is.readNBytes(2));
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Main.outputError("Error receiving file", e);
        } catch (IOException e) {
            e.printStackTrace();
            Main.outputError("Error receiving file", e);
        } finally {
            if(fos == null){
                success = false;
            }else{
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Main.outputError("Error closing out stream of receive file method", e);
                    success = false;
                }
            }
        }
        if(success){
            if(dc.isRequest())
                notifyRequestSent(dc.getInfo().toString());
            else
                notifyResponseSent(dc.getInfo().toString());
            return true;
        }else{
            if(dc.isRequest())
                Main.outputVerbose("Could not receive file for request: "+ dc.getInfo());
            else
                Main.outputVerbose("Could not receive file for response: "+ dc.getInfo());
        }

        return false;
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


    @Override
    public void run() {
        waitForRequests();
    }
}
