package controllers;

import JavaFXHelper.FXHelper;
import exceptions.MyFileManagerNotInitializedException;
import exceptions.StatusNotIntializedException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import main.Main;
import manager.FileManager;
import manager.MyFileManager;
import models.Status;
import utility.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SyncController implements Initializable {
    public static final String TITLE = "Sync";
    public static final String FXML = "/views/sync.fxml";

    private FileManager fileManager = MyFileManager.getInstance();

    @FXML
    private Button btnBack, btnClearAList, btnClearStatusList, btnClearBList, btnRefreshList, btnSync;
    @FXML
    private ListView<String> lvA, lvB, lvStatus;

    @FXML
    private ImageView ivSync, ivBack;

    public SyncController() throws MyFileManagerNotInitializedException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ivSync.setImage(Resources.getImage(Resources.SYNC_ICON));
        ivBack.setImage(Resources.getImage(Resources.BACK_ARROW_ICON));
    }

    @FXML
    public void btnSyncOnClick() {
    }

    @FXML
    public void btnRefreshListOnClick() { //todo put these in threads
        refreshAList();
        refreshStatusList();
    }

    private void refreshStatusList() {
        try{
            lvStatus.getItems().clear();
            lvStatus.getItems().addAll(Status.getStatusList());
        } catch (StatusNotIntializedException e) {
            e.printStackTrace();
            Main.outputError("Could not get status list", e);
        }
    }

    private void refreshAList() {
        lvA.getItems().clear();
        lvA.getItems().addAll(fileManager.getFileList());
    }

    @FXML
    public void btnClearStatusListOnClick() {
    }

    @FXML
    public void btnBackOnClick() throws IOException {
        FXHelper.sceneChanger(this, btnBack, HomeController.FXML, HomeController.TITLE);
    }

    @FXML
    public void btnClearAListOnClick() {
    }

    @FXML
    public void btnClearBListOnClick() {
    }
}
