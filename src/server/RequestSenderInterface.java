package server;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import library.sharedpackage.communication.DataCarrier;
import library.sharedpackage.models.FileContent;

import java.util.LinkedList;

public interface RequestSenderInterface {

    DataCarrier addItems(LinkedList<FileContent> files);

    DataCarrier removeItems(LinkedList<String> fileNames);

    DataCarrier getItemsList();

    DataCarrier getItems(LinkedList<String> fileNames);

    DataCarrier testConnection();

    StringProperty getUpdateProperty();

    DoubleProperty getProgressProperty();
}
