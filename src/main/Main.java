package main;

import JavaFXHelper.FXHelper;
import controllers.HomeController;
import controllers.LoggerController;
import controllers.SyncControllerInterface;
import exceptions.SaveStatusException;
import exceptions.SetupStatusException;
import exceptions.StatusNotInitializedException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logger.LoggerInterface;
import manager.MyFileManager;
import manager.MyRemoteItemManager;
import manager.UpdatableRemoteItemManager;
import models.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.SyncService;
import utility.LoggerUtility;
import utility.Settings;
import utility.StringBuilderAppender;

import java.io.IOException;

public class Main extends Application {

    private static final Logger logger = LogManager.getLogger();

    private static UpdatableRemoteItemManager remoteItemManager;
    private static SyncControllerInterface syncControllerInterface;
    private static LoggerInterface loggerInterface;

    //todo: different file for log level error and up

    //todo: set up folder selection

    //todo: start over server once connection reset

    //todo: logger display blury

    //todo: logger display hides behind main window

    //todo: add finish button on loading controller; maybe also cancel button

    //todo: change service worker implementation to use multiple Tasks and use updateProgress method instead of Platform.runLater

    //todo: look over Platform.runLater calls for frequently updated items


    public static void main(String[] args) {
        try{
            onStartUp();

            launch(args);

            onApplicationClose();
        }catch(Exception e){
            logger.fatal(e.getMessage(), e);
        }
    }

    //region Application Events

    private static void onStartUp(){
        LoggerUtility.configureLogger();
        startLoggerDisplay();
        Settings settings = Settings.getInstance();
        MyFileManager.getInstance(settings);
        getRemoteItemManager();
        try {
            if(!Status.setUpStatus())
                throw new SetupStatusException();
        } catch (SetupStatusException e) {
            e.printStackTrace();
            logger.error("Could not setup status: "+e.getMessage(), e);
            System.exit(1);
        }
    }

    private static void startLoggerDisplay(){

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Stage stage = new Stage();

                    stage.setTitle(LoggerController.TITLE);

                    FXMLLoader loader = new FXMLLoader(Main.class.getResource(LoggerController.FXML));
                    Parent root = (Parent)loader.load();

                    loggerInterface = loader.getController();
                    StringBuilderAppender.setLoggerInterface(loggerInterface);

                    Scene scene = new Scene(root);

                    stage.setScene(scene);

                    stage.show();



                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage(), e);
                }
            }
        });
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
                    if(FXHelper.closeProgram(this, primaryStage)){
                        primaryStage.close();
                        if(loggerInterface != null)
                            loggerInterface.closeLoggerDisplay();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private static void onApplicationClose(){
        String saveStatusErrorMessage = "Could not save status on Application close: ";
        try {
            if(!Status.saveStatus())
                throw new SaveStatusException();
        } catch (StatusNotInitializedException | SaveStatusException e) {
            e.printStackTrace();
            logger.error(saveStatusErrorMessage+e.getMessage(), e);
        }

        boolean settingsSaved = Settings.getInstance().saveSettings();

        logger.debug("Settings Saved? -- "+settingsSaved);

        System.exit(0);
    }

    //endregion


    //region GlobalMethods

    public static void sync(){
        SyncService syncService = new SyncService();
        syncService.restart();
    }

    public static void refreshList(){
        if(syncControllerInterface != null){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    syncControllerInterface.refreshLists();
                    logger.debug("refresh list");
                }
            });
        }else{
            logger.warn("attempted to refresh list but controller instance was null");
        }
    }

    public static void setBItemManager(UpdatableRemoteItemManager manager){
        remoteItemManager = manager;
        logger.debug("bItemManager Set");
    }

    public static UpdatableRemoteItemManager getRemoteItemManager(){
        logger.debug("Get bItemManager");
        if(remoteItemManager == null){
            remoteItemManager = new MyRemoteItemManager();
            logger.debug("bItemManager set to remote manager");
        }
        return remoteItemManager;
    }

    public static void setSyncControllerInterface(SyncControllerInterface syncControllerInterface){
        Main.syncControllerInterface = syncControllerInterface;
        if(syncControllerInterface == null)
            logger.warn("SyncControllerInterface Set to null");
        else
            logger.debug("SyncControllerInterface Set");
    }

    public static SyncControllerInterface getSyncControllerInterface(){
        logger.debug("Get SyncControllerInterface");
        return syncControllerInterface;
    }

    //endregion
}
