package controllers;

import JavaFXHelper.FXHelper;
import exceptions.MyFileManagerNotInitializedException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import library.sharedpackage.models.FileContent;
import main.Main;
import library.sharedpackage.manager.ItemManager;
import manager.MyFileManager;
import models.Status;
import services.RefreshListService;
import utility.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class SyncController implements Initializable, SyncControllerInterface {
    public static final String TITLE = "Sync";
    public static final String FXML = "/views/sync.fxml";

    private ItemManager fileManager = MyFileManager.getInstance();
    private ItemManager statusManager = new Status();
    private ItemManager remoteManager = Main.getRemoteItemManager();

    @FXML
    private Button btnBack, btnClearStatusList, btnRefreshList, btnSync;
    @FXML
    private ListView<String> lvA, lvB, lvStatus;

    @FXML
    private ImageView ivSync, ivBack;

    @FXML
    private ProgressIndicator piA, piB;

    public SyncController() throws MyFileManagerNotInitializedException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ivSync.setImage(Resources.getImage(Resources.SYNC_ICON));
        ivBack.setImage(Resources.getImage(Resources.BACK_ARROW_ICON));
        Main.setSyncControllerInterface(this);
    }

    @FXML
    public void btnSyncOnClick() {
        try {
            test();
        } catch (Exception e) {
            e.printStackTrace();
            Main.outputError("Error with test", e);
        }
    }

    private static void test() throws MyFileManagerNotInitializedException { //todo: remove
        //todo: current problem, remote does not immediately end send file connection
        List<String> fileNames = new LinkedList<>(Arrays.asList(new String[]{"testFile.mp4"}));
        List<FileContent> files = MyFileManager.getInstance().getItems(fileNames);

        for(FileContent file : files){
            Main.outputVerbose(file.toString());
        }

        Thread t = new Thread(() -> {
            Main.getRemoteItemManager().addItems(files);
        });
        t.start();
    }

    @FXML
    public void btnRefreshListOnClick() { //todo show loading circles
        refreshLists();
    }

    public void refreshLists(){
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
        RefreshListService rlService = new RefreshListService(fileManager, lvA.getItems(), piA);
        piA.setOpacity(1);
        rlService.restart();
    }

    private void refreshBList(){
        lvB.getItems().clear();
        RefreshListService rlService = new RefreshListService(remoteManager, lvB.getItems(), piB);
        piB.setOpacity(1);
        rlService.restart();
    }

    @FXML
    public void btnClearStatusListOnClick() {
    }

    @FXML
    public void btnBackOnClick() throws IOException {
        Main.setSyncControllerInterface(null);
        FXHelper.sceneChanger(this, btnBack, HomeController.FXML, HomeController.TITLE);
    }
}
