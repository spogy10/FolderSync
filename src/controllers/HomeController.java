package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import utility.Resources;

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
    public void btnSyncOnClick() {
        System.out.println("sync button clicked");
    }

    @FXML
    public void btnOptionsOnClick() {
        System.out.println("options button clicked");
    }
}
