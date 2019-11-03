package main;

import JavaFXHelper.FXHelper;
import controllers.HomeController;
import controllers.SyncControllerInterface;
import exceptions.SaveStatusException;
import exceptions.SetupStatusException;
import exceptions.StatusNotIntializedException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import library.sharedpackage.manager.RemoteItemManager;
import library.sharedpackage.models.FileContent;
import manager.MyFileManager;
import manager.MyRemoteItemManager;
import manager.UpdatableRemoteItemManager;
import models.Status;
import services.SyncService;
import utility.Settings;

import java.io.IOException;

public class Main extends Application {

    private static UpdatableRemoteItemManager remoteItemManager;
    private static SyncControllerInterface syncControllerInterface;

    //todo set up folder selection

    //todo: start over server once connection reset

    private static void onStartUp(){
        Settings settings = Settings.getInstance();
        MyFileManager.getInstance(settings.getValue(Settings.SettingsKeys.FOLDER_LOCATION));
        getRemoteItemManager();
        try {
            if(!Status.setUpStatus())
                throw new SetupStatusException();
        } catch (SetupStatusException e) {
            e.printStackTrace();
            outputError("Could not setup status", e);
            System.exit(1);
        }
    }

    private static void onApplicationClose(){
        String saveStatusErrorMessage = "Could not save status on Application close";
        try {
            if(!Status.saveStatus())
                throw new SaveStatusException();
        } catch (StatusNotIntializedException e) {
            e.printStackTrace();
            outputError(saveStatusErrorMessage, e);
        } catch (SaveStatusException e) {
            e.printStackTrace();
            outputError(saveStatusErrorMessage, e);
        }

        boolean settingsSaved = Settings.getInstance().saveSettings();

        outputVerbose("Settings Saved? -- "+settingsSaved);

        System.exit(0);
    }

    public static void main(String[] args) {
        onStartUp();

        launch(args);

        onApplicationClose();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(HomeController.FXML));

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);

        primaryStage.setTitle(HomeController.TITLE);

        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                try {
                    if(FXHelper.closeProgram(this, primaryStage))
                        primaryStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void sync(){
        SyncService syncService = new SyncService();
        syncService.restart();
        refreshList();
    }

    private static void refreshList(){
        if(syncControllerInterface != null){
            syncControllerInterface.refreshLists();
            Main.outputVerbose("refresh list");
        }else{
            Main.outputVerbose("attempted to refresh list but controller instance was null");
        }
    }

    public static void setBItemManager(UpdatableRemoteItemManager manager){
        remoteItemManager = manager;
        outputVerbose("bItemManager Set");
    }

    public static UpdatableRemoteItemManager getRemoteItemManager(){
        outputVerbose("Get bItemManager");
        if(remoteItemManager == null){
            remoteItemManager = new MyRemoteItemManager();
            outputVerbose("bItemManager set to remote manager");
        }


        return remoteItemManager;
    }

    public static void setSyncControllerInterface(SyncControllerInterface syncControllerInterface){
        Main.syncControllerInterface = syncControllerInterface;
        if(syncControllerInterface == null)
            outputVerbose("SyncControllerInterface Set to null");
        else
            outputVerbose("SyncControllerInterface Set");
    }

    public static SyncControllerInterface getSyncControllerInterface(){
        outputVerbose("Get SyncControllerInterface");
        return syncControllerInterface;
    }


    public static void outputError(Exception e){
        outputError("", e);
    }

    public static void outputError(String message, Exception e){
        System.out.println(message);
    }

    public static void outputVerbose(String message){
        System.out.println(message);
    }


}
