package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    Button btnSync, btnOptions;

    public static final String TITLE = "Sync Home";
    public static final String FXML = "/views/startpage.fxml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
