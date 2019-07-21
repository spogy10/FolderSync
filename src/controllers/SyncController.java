package controllers;

import JavaFXHelper.FXHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import utility.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SyncController implements Initializable {
    public static final String TITLE = "Sync";
    public static final String FXML = "/views/sync.fxml";

    @FXML
    private Button btnBack, btnClearAList, btnClearStatusList, btnClearBList, btnRefreshList, btnSync;
    @FXML
    private ListView lvA, lvB, lvStatus;

    @FXML
    private ImageView ivSync, ivBack;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ivSync.setImage(Resources.getImage(Resources.SYNC_ICON));
        ivBack.setImage(Resources.getImage(Resources.BACK_ARROW_ICON));
    }

    @FXML
    public void btnSyncOnClick() {
    }

    @FXML
    public void btnRefreshListOnClick() {
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
