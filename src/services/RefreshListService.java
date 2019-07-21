package services;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import manager.ItemManager;

import java.util.LinkedList;
import java.util.List;

public class RefreshListService extends Service<List<String>> {

    private ItemManager itemManager;

    public RefreshListService(ItemManager itemManager, ObservableList<String> observableList) {
        this.itemManager = itemManager;

        setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                observableList.addAll((List<String>) event.getSource().getValue());
            }
        });

        setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                observableList.addAll(new LinkedList<>());
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
