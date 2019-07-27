package server;

import communication.DataCarrier;
import models.FileContent;

import java.util.LinkedList;
import java.util.List;

public interface RequestSenderInterface {

    DataCarrier addItems(LinkedList<FileContent> files);

    DataCarrier removeItems(LinkedList<String> fileNames);

    DataCarrier getItemsList();

    DataCarrier getItems(LinkedList<String> fileNames);
}
