package services;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import manager.ItemManager;

import java.util.LinkedList;
import java.util.List;

public class RefreshListService extends Service<List<String>> {

    private ItemManager itemManager;

    public RefreshListService(ItemManager itemManager, ObservableList<String> observableList){
        this(itemManager, observableList, new ProgressIndicator());
    }

    public RefreshListService(ItemManager itemManager, ObservableList<String> observableList, ProgressIndicator progressIndicator) {
        this.itemManager = itemManager;

        setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                observableList.addAll((List<String>) event.getSource().getValue());
                progressIndicator.setOpacity(0);
            }
        });

        setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                observableList.addAll(new LinkedList<>());
                progressIndicator.setOpacity(0);
            }
        });
    }


    @Override
    protected Task<List<String>> createTask() {
        return new Task<List<String>>() {
            @Override
            protected List<String> call() {
                return itemManager.getItemsList();
            }
        };
    }
}
