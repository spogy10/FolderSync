package controllers;

import JavaFXHelper.FXHelper;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import utility.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    public static final String TITLE = "Sync Home";
    public static final String FXML = "/views/startpage.fxml";

    @FXML
    private Button btnSync, btnOptions;

    @FXML
    private ImageView ivSync, ivOptions;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ivSync.setImage(Resources.getImage(Resources.SYNC_ICON));
        ivOptions.setImage(Resources.getImage(Resources.OPTIONS_ICON));
    }

    @FXML
    public void btnSyncOnClick() throws IOException {
        FXHelper.sceneChanger(this, btnSync, SyncController.FXML, SyncController.TITLE);
    }

    @FXML
    public void btnOptionsOnClick() {
        System.out.println("options button clicked");
    }
}
