package server;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import library.sharedpackage.communication.DataCarrier;
import library.sharedpackage.models.FileContent;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.Settings;

import javax.annotation.Nullable;
import java.io.*;
import java.net.*;
import java.util.Iterator;

public class Server implements Runnable {
    private static final Logger logger = LogManager.getLogger();

    private ServerSocket serverSocket = null;
    private Socket connection = null;
    private ObjectOutputStream os = null;
    private ObjectInputStream is = null;
    private Thread t;
    private Settings settings = Settings.getInstance();


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


    //region SERVER MANAGEMENT

    private void setUpConnection() {
        outPutIpAddress();
        try{
            int port = Integer.parseInt(settings.getValue(Settings.SettingsKeys.SERVER_PORT_NUMBER));
            int backLog = Integer.parseInt(settings.getValue(Settings.SettingsKeys.SERVER_BACKLOG));

            serverSocket = new ServerSocket(port, backLog);
            t = new Thread(this, "Server Thread");
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void outPutIpAddress(){
        try{
            Iterator<NetworkInterface> iterator = NetworkInterface.getNetworkInterfaces().asIterator();
            while(iterator.hasNext()){
                var network = iterator.next();
                String networkDisplayName = "Intel(R) Dual Band Wireless-AC 3168";

                if(network.getDisplayName().equals(networkDisplayName)){
                    Iterator<InetAddress> addressIterator =  network.getInetAddresses().asIterator();

                    InetAddress address = addressIterator.next();
                    logger.info("Address: "+address.getHostAddress());
                    logger.info("Name: "+address.getHostName());

                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
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
            logger.error("Error initializing streams: "+e.getMessage(), e);
        }

        return false;
    }

    private void closeConnection(){
        try {
            if(os != null)
                os.close();
            if(is != null)
                is.close();
            if(connection != null)
                connection.close();
            logger.info("Server connections closed");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error closing connections: "+e.getMessage(), e);
        }

        os = null;
        is = null;
        connection = null;
    }

    boolean areStreamsInitialized(){
        return connection != null && os != null && is != null;
    }

    boolean isServerOff(){
        return serverOff;
    }

    void restartServer(){
        logger.info("Restarting Server");
        endServer();
        setUpConnection();
    }

    void endServer(){
        try {
            closeConnection();
            serverSocket.close();
            logger.info("Server ended");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error ending server: "+e.getMessage(), e);
        }

        serverOff = true;
    }
    //endregion


    //region THREAD STUFF

    @Override
    public void run() {
        waitForRequests();
    }

    private void waitForRequests() {
        logger.info("Waiting for requests");

        try{
            serverOff = false;
            connection = serverSocket.accept();
            if(initStreams()){
                logger.info("connection received");
                if(runnable != null){
                    runnable.run();
                    return;
                }
                logger.warn("Could not start ServerHandler Thread");
            }

            endServer();

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error starting server: "+e.getMessage(), e);
        }
    }
    //endregion


    //region NOTIFICATION

    private void notifyRequestSent(String request) {
        logger.debug("Request: "+request+" sent");
    }

    private void notifyResponseSent(String response){
        logger.debug("Response: "+response+" sent");
    }

    private void notifyRequestReceived(String request) {
        logger.debug("Request "+request+" received");
    }

    private void notifyResponseReceived(String response){
        logger.debug("Response "+response+" received");
    }
    //endregion


    //region SEND AND RECIEVE REQUEST AND RESPONSE

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
    //endregion



    //region FILE TRANSFER

    //region SEND FILES

    boolean sendFile(DataCarrier<FileContent> dc, @Nullable DoubleProperty loadingProperty){ //https://stackoverflow.com/questions/10367698/java-multiple-file-transfer-over-socket?answertab=votes#tab-top
        boolean success = false;

        if(dc.isRequest())
            logger.debug("Request "+ dc.getInfo() +" to send file commence");
        else
            logger.debug("Response "+ dc.getInfo() +" to send file commence");

        FileInputStream fis = null;
        try{
            FileContent fileContent = dc.getData();
            String folderPath = settings.getValue(Settings.SettingsKeys.FOLDER_LOCATION);
            File file = new File(folderPath, fileContent.getFileName());
            fis = new FileInputStream(file);

            if(loadingProperty == null)
                sendFileStream(fileContent.getFileSize(), fis);
            else
                sendFileStream(fileContent.getFileSize(), fis, loadingProperty);

            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("Error sending file: "+e.getMessage(), e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error sending file: "+e.getMessage(), e);
        } finally {
            if(loadingProperty != null){
                loadingProperty.setValue(0);
            }
            if(fis == null){
                success = false;
            }else{
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("Error closing input stream of send file method: "+e.getMessage(), e);
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
                logger.warn("Could not send file for request: "+ dc.getInfo());
            else
                logger.warn("Could not send file for response: "+ dc.getInfo());
        }

        return false;
    }

    private void sendFileStream(final long totalFileSize, final FileInputStream fis) throws IOException {
        logger.debug("In sendFileStream no updates method");
        transferStreamData(fis, os, totalFileSize);
    }

    private void sendFileStream(final long totalFileSize, final FileInputStream fis, DoubleProperty loadingProperty) throws IOException {
        logger.debug("In sendFileStream updates method");
        transferStreamData(fis, os, totalFileSize, loadingProperty);
    }

    //endregion

    //region RECEIVE FILES

    boolean receiveFile(DataCarrier<FileContent> dc, @Nullable DoubleProperty loadingProperty) {
        boolean success = false;

        if(dc.isRequest())
            logger.debug("Request "+ dc.getInfo() +" to receive file commence");
        else
            logger.debug("Response "+ dc.getInfo() +" to receive file commence");

        FileOutputStream fos = null;
        try{
            FileContent fileContent = dc.getData();
            String folderPath = settings.getValue(Settings.SettingsKeys.FOLDER_LOCATION);
            File file = new File(folderPath, fileContent.getFileName());
            fos = new FileOutputStream(file);

            if(loadingProperty == null)
                receiveFileStream(fileContent.getFileSize(), fos);
            else
                receiveFileStream(fileContent.getFileSize(), fos, loadingProperty);

            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("Error receiving file: "+e.getMessage(), e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error receiving file: "+e.getMessage(), e);
        } finally {
            if(loadingProperty != null){
                loadingProperty.setValue(0);
            }
            if(fos == null){
                success = false;
            }else{
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("Error closing out stream of receive file method: "+e.getMessage(), e);
                    success = false;
                }
            }
        }
        if(success){
            if(dc.isRequest())
                notifyRequestReceived(dc.getInfo().toString());
            else
                notifyRequestReceived(dc.getInfo().toString());
            return true;
        }else{
            if(dc.isRequest())
                logger.warn("Could not receive file for request: "+ dc.getInfo());
            else
                logger.warn("Could not receive file for response: "+ dc.getInfo());
        }

        return false;
    }

    private void receiveFileStream(final long totalFileSize, final FileOutputStream fos) throws IOException {
        logger.debug("In receiveFileStream no updates method");
        transferStreamData(is, fos, totalFileSize);
    }

    private void receiveFileStream(final long totalFileSize, final FileOutputStream fos, DoubleProperty loadingProperty) throws IOException {
        logger.debug("In receiveFileStream updates method");
        transferStreamData(is, fos, totalFileSize, loadingProperty);
    }
    //endregion

    private void transferStreamData(InputStream input, OutputStream outPut, final long totalFileSize) throws IOException {
        int n;
        long fileSize = totalFileSize;
        byte[] buffer = new byte[1024 * 4];
        while ( (fileSize > 0) && (IOUtils.EOF != (n = input.read(buffer, 0, (int)Math.min(buffer.length, fileSize)))) ) { //checks if fileSize is 0 or if EOF sent
            outPut.write(buffer, 0, n);
            fileSize -= n;
        }
    }

    private void transferStreamData(InputStream input, OutputStream outPut, final long totalFileSize, DoubleProperty loadingProperty) throws IOException {
        int n;
        long fileSize = totalFileSize;
        byte[] buffer = new byte[1024 * 4];
        while ( (fileSize > 0) && (IOUtils.EOF != (n = input.read(buffer, 0, (int)Math.min(buffer.length, fileSize)))) ) { //checks if fileSize is 0 or if EOF sent
            outPut.write(buffer, 0, n);
            fileSize -= n;
            updateProgressProperty(calculateFilePercentage(totalFileSize, fileSize), loadingProperty);
        }
    }

    private double calculateFilePercentage(double totalFileSize, double currentFileSize){
        return 1 - currentFileSize/totalFileSize;
    }

    private void updateProgressProperty(double value, DoubleProperty loadingProperty){
        Platform.runLater(() -> {
            loadingProperty.setValue(value);
        });
    }
    //endregion




}
