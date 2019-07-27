package server;

import communication.DataCarrier;
import models.FileContent;

import java.util.LinkedList;
import java.util.List;

public interface RequestSenderInterface {

    DataCarrier addItems(List<FileContent> files);

    DataCarrier removeItems(List<String> fileNames);

    DataCarrier getItemsList();
}
