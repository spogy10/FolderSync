package manager;

import models.FileContent;

import java.util.LinkedList;
import java.util.List;

public class RemoteItemManager implements ItemManager {

    @Override
    public boolean addItems(List<FileContent> files) {
        return false;
    }

    @Override
    public boolean removeItems(List<String> fileNames) {
        return false;
    }

    @Override
    public List<String> getItemsList() {
        return new LinkedList<>();
    }
}
