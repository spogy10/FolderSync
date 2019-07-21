package main;

import JavaFXHelper.FXHelper;
import controllers.HomeController;
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
import manager.MyFileManager;
import models.Status;
import utility.Settings;

import java.io.IOException;

public class Main extends Application {

    //todo create user interface

    //todo set up serv socket

    //todo set up folder selection

    //todo set up methods to get and pass file data

    private static void onStartUp(){
        Settings settings = Settings.getInstance();
        MyFileManager.getInstance(settings.getValue(Settings.SettingsKeys.FOLDER_LOCATION));

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
                        System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void outputError(Exception e){
        outputError("", e);
    }

    public static void outputError(String message, Exception e){
        System.out.println(message);
    }

}
