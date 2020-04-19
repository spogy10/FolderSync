package controllers;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadingController implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    public static final String TITLE = "Loading";
    public static final String FXML = "/views/loading.fxml";

    private static AtomicBoolean loadingOn = new AtomicBoolean(false);
    private static Stage loadingStage;

    private static StringProperty mainStringProperty, subStringProperty;
    private static DoubleProperty mainDoubleProperty, subDoubleProperty;

    @FXML
    ProgressBar pbMain, pbSub;

    @FXML
    Label lbMain, lbSub;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadingOn.compareAndSet(false, true);
        bindProperties();

    }

    private void bindProperties(){
        lbMain.textProperty().bind(mainStringProperty);
        lbSub.textProperty().bind(subStringProperty);

        pbMain.progressProperty().bind(mainDoubleProperty);
        pbSub.progressProperty().bind(subDoubleProperty);
    }






    private static boolean isLoadingViewShowing(){
        return loadingOn.get();
    }

    private static void showLoadingView(Object obj) throws IOException {
        if(isLoadingViewShowing()){
            logger.info("Loading view already showing");
            return;
        }
        logger.info("Loading view not already showing");
        startLoadingView(obj);
    }

    public static void close(){
        logger.info("close loading view");
        closeLoadingView();
    }

    private static void closeLoadingView(){
        loadingOn.compareAndSet(true, false);
        Platform.runLater(() -> {
            if(loadingStage != null){
                loadingStage.close();
            }

            loadingStage = null;
            resetProperties();
        });
    }

    private static void initializeProperties(){
        mainStringProperty = new SimpleStringProperty("");
        subStringProperty = new SimpleStringProperty("");

        mainDoubleProperty = new SimpleDoubleProperty(0.0);
        subDoubleProperty = new SimpleDoubleProperty(0.0);
    }

    private static void resetProperties(){
        if(mainStringProperty != null){
            mainStringProperty.unbind();
            mainStringProperty.setValue("");
        }
        if(subStringProperty != null){
            subStringProperty.unbind();
            subStringProperty.setValue("");
        }
        if(mainDoubleProperty != null){
            mainDoubleProperty.unbind();
            mainDoubleProperty.setValue(0.0);
        }
        if(subDoubleProperty != null){
            subDoubleProperty.unbind();
            subDoubleProperty.setValue(0.0);
        }
    }

    private static void startLoadingView(Object obj) throws IOException {
        logger.info("Starting Loading View");
        loadingStage = new Stage();
        initializeProperties();

        //loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setTitle(TITLE);

        loadingStage.setOnCloseRequest(e -> {
            e.consume();
            closeLoadingView();
        });


        FXMLLoader loader = new FXMLLoader(obj.getClass().getResource(FXML));

        Parent root = loader.load();

        Scene scene = new Scene(root);
        loadingStage.setScene(scene);
        loadingStage.show();
    }

    public static void showMainMessage(Object obj, String message) throws IOException {
        showLoadingView(obj);
        mainStringProperty.setValue(message);
    }

    public static void showSubMessage(Object obj, String message) throws IOException {
        showLoadingView(obj);
        subStringProperty.setValue(message);
    }

    public static void setMainProgress(Object obj, Double progress) throws IOException {
        showLoadingView(obj);
        mainDoubleProperty.setValue(progress);
    }

    public static void setSubProgress(Object obj, Double progress) throws IOException {
        showLoadingView(obj);
        subDoubleProperty.setValue(progress);
    }

    public static void bindMainStringProperty(Object obj, ObservableValue<String> observableValue) throws IOException {
        showLoadingView(obj);
        mainStringProperty.bind(observableValue);
    }

    public static void bindSubStringProperty(Object obj, ObservableValue<String> observableValue) throws IOException {
        showLoadingView(obj);
        subStringProperty.bind(observableValue);
    }

    public static void bindMainDoubleProperty(Object obj, ObservableValue<Number> observableValue) throws IOException {
        showLoadingView(obj);
        mainDoubleProperty.bind(observableValue);
    }

    public static void bindSubDoubleProperty(Object obj, ObservableValue<Number> observableValue) throws IOException {
        showLoadingView(obj);
        subDoubleProperty.bind(observableValue);
    }



}
