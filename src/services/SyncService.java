package services;

import exceptions.StatusNotIntializedException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import library.sharedpackage.manager.RemoteItemManager;
import library.sharedpackage.models.FileContent;
import main.Main;
import library.sharedpackage.manager.ItemManager;
import manager.MyFileManager;
import manager.MyRemoteItemManager;
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

        update("Removing files from primary folder");
        removeItems(filesToBeRemoved(pcMap), fileManager);

        update("Removing files from remote folder");
        removeItems(filesToBeRemoved(mobileMap), remoteManager);

        update("Removing files from Status keeper");
        removeItems(filesToBeRemoved(statMap), new Status());


        update("Adding files to primary folder");
        addItems(filesToBeAdded(pcMap), fileManager);

        update("Adding files to remote folder");
        addItems(filesToBeAdded(mobileMap), remoteManager);

        update("Adding files from Status keeper");
        addItems(filesToBeAdded(statMap), new Status());
    }
    
    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                try{
                    fileManager = MyFileManager.getInstance();
                    remoteManager = Main.getRemoteItemManager();
                    SyncManager syncManager = MySyncManager.getInstance();

                    if(!remoteManager.isRequestSenderSetup()){
                        update("Connection not setup, cannot sync files");
                        return null;
                    }

                    update("Syncing files");
                    Changes changes = syncManager.sync(fileManager.getItemsList(), remoteManager.getItemsList(), Status.getStatusList());

                    handleChanges(changes.getPc(), changes.getMobile(), changes.getStat());

                    update("Sync Complete");
                }catch(Exception e){
                    e.printStackTrace();
                    Main.outputError("Error syncing", e);
                    update("An error occurred during sync operation");
                }
                return null;
            }
        };
    }

    private void update(String updateMessage){ //todo: turn into ui
        Main.outputVerbose(updateMessage);
    }


    private void removeItems(List<String> fileList, ItemManager itemManager) throws StatusNotIntializedException {
        if(!fileList.isEmpty()){

            if(itemManager instanceof Status)
                Status.removeFromStatus(fileList);
            else
                itemManager.removeItems(fileList);
        }

    }

    private void addItems(List<String> fileList, ItemManager itemManager) {
        if(!fileList.isEmpty()){

            if(itemManager instanceof Status)
                Status.addToStatus(fileList);
            else if(itemManager instanceof MyFileManager){
                remoteManager.getItems(fileList);
            } else if(itemManager instanceof MyRemoteItemManager){
                List<FileContent> filesToBeAddedToMobile = fileManager.getItems(fileList);
                remoteManager.addItems(filesToBeAddedToMobile);
            }

        }

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
