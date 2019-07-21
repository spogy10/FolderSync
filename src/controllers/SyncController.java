package controllers;

import JavaFXHelper.FXHelper;
import exceptions.MyFileManagerNotInitializedException;
import exceptions.StatusNotIntializedException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import main.Main;
import manager.ItemManager;
import manager.MyFileManager;
import manager.RemoteItemManager;
import models.Status;
import services.RefreshListService;
import utility.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SyncController implements Initializable {
    public static final String TITLE = "Sync";
    public static final String FXML = "/views/sync.fxml";

    private ItemManager fileManager = MyFileManager.getInstance();
    private ItemManager statusManager = new Status();
    private ItemManager remoteManager = new RemoteItemManager();

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
    public void btnRefreshListOnClick() { //todo show loading circles
        refreshAList();
        refreshStatusList();
        refreshBList();
    }

    private void refreshStatusList() {
        lvStatus.getItems().clear();
        RefreshListService rlService = new RefreshListService(statusManager, lvStatus.getItems());
        rlService.restart();
    }

    private void refreshAList() {
        lvA.getItems().clear();
        RefreshListService rlService = new RefreshListService(fileManager, lvA.getItems());
        rlService.restart();
    }

    private void refreshBList(){
        lvB.getItems().clear();
        RefreshListService rlService = new RefreshListService(remoteManager, lvB.getItems());
        rlService.restart();
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
