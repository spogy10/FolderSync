package manager;

import models.FileContent;

import java.util.List;

public interface ItemManager {

    boolean addItems(List<FileContent> files);

    boolean removeItems(List<String> fileNames);

    List<String> getItemsList();
}
