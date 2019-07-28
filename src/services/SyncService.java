package services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.Main;
import sharedpackage.manager.ItemManager;
import manager.MyFileManager;
import manager.MySyncManager;
import manager.SyncManager;
import models.Changes;
import models.Status;

public class SyncService extends Service<Void> {

    private ItemManager fileManager;
    private ItemManager remoteManager;
    private SyncManager syncManager;
    
    private void handleChanges(Changes changes){

    }
    
    @Override
    protected Task<Void> createTask() {
        try{
            fileManager = MyFileManager.getInstance();
            remoteManager = Main.getBItemManager();
            SyncManager syncManager = MySyncManager.getInstance();
            
            Changes changes = syncManager.sync(fileManager.getItemsList(), remoteManager.getItemsList(), Status.getStatusList());
            handleChanges(changes);

        }catch(Exception e){
            e.printStackTrace();
            Main.outputError("Error syncing", e);
        }
        return null;
    }
}
