package manager;

import model.FileContent;

import java.util.List;

public interface FileManager {

    boolean addFiles(List<FileContent> files);

    boolean removeFiles(List<String> fileNames);

    List<String> getFileList();
}
