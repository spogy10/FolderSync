package services;

import exceptions.StatusNotIntializedException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import library.sharedpackage.manager.RemoteItemManager;
import library.sharedpackage.models.FileContent;
import main.Main;
import library.sharedpackage.manager.ItemManager;
import manager.MyFileManager;
import manager.MySyncManager;
import manager.SyncManager;
import models.Changes;
import models.Status;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SyncService extends Service<Void> {

    private ItemManager fileManager;
    private RemoteItemManager remoteManager;

    private void handleChanges(Map<String, Boolean> pcMap, Map<String, Boolean> mobileMap, Map<String, Boolean> statMap) throws StatusNotIntializedException { //todo: check for correct order of operations, check for empty lists
        fileManager.removeItems(filesToBeRemoved(pcMap));
        remoteManager.removeItems(filesToBeRemoved(mobileMap));
        Status.removeFromStatus(filesToBeRemoved(statMap));

        remoteManager.getItems(filesToBeAdded(pcMap));
        List<FileContent> filesToBeAddedToMobile = fileManager.getItems(filesToBeAdded(mobileMap));

        remoteManager.addItems(filesToBeAddedToMobile);

        Status.addToStatus(filesToBeAdded(statMap));

    }
    
    @Override
    protected Task<Void> createTask() {
        try{
            fileManager = MyFileManager.getInstance();
            remoteManager = Main.getRemoteItemManager();
            SyncManager syncManager = MySyncManager.getInstance();
            
            Changes changes = syncManager.sync(fileManager.getItemsList(), remoteManager.getItemsList(), Status.getStatusList());
            handleChanges(changes.getPc(), changes.getMobile(), changes.getStat());

        }catch(Exception e){
            e.printStackTrace();
            Main.outputError("Error syncing", e);
        }
        return null;
    }


    private List<String> filesToBeAdded(Map<String, Boolean> map){
        return mapFilter(map, true);
    }

    private List<String> filesToBeRemoved(Map<String, Boolean> map){
        return mapFilter(map, false);
    }

    private List<String> mapFilter(Map<String, Boolean> map, boolean filterByValue){
        LinkedList<String> files = new LinkedList<>();

        for (Map.Entry<String, Boolean> entry : map.entrySet()){
            if(entry.getValue() == filterByValue)
                files.add(entry.getKey());
        }

        return files;
    }
}
