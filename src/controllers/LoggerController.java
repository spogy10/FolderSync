package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import logger.LoggerInterface;
import main.Main;
import utility.StringBuilderAppender;

import java.net.URL;
import java.util.ResourceBundle;

public class LoggerController implements Initializable, LoggerInterface {
    public static final String TITLE = "Logger";
    public static final String FXML = "/views/logger.fxml";

    @javafx.fxml.FXML
    private TextArea taDisplay;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        taDisplay.setText(StringBuilderAppender.getLoggerText());
    }

    public void updateLogger(String message){
        taDisplay.appendText(message);
    }

    @Override
    public void closeLoggerDisplay() {
        Stage stage = (Stage) taDisplay.getScene().getWindow();
        stage.close();
    }
}
