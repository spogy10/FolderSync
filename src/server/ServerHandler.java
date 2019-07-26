package server;

import main.Main;
import manager.ItemManager;

public class ServerHandler implements Runnable {
    private Server server;

    private static ServerHandler ourInstance = null;


    public static ServerHandler getInstance(){
        if(ourInstance == null)
            ourInstance = new ServerHandler();

        return ourInstance;
    }

    private ServerHandler(){
        Thread t = new Thread(this);
        t.start();
    }


    @Override
    public void run() {
        Main.outputVerbose("ServerHandler thread started");
        server = Server.getInstance();
        if(server.isServerOff())
            server.restartServer();
    }
}
