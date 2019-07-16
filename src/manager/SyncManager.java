package manager;

import model.Changes;
import syncenum.SyncEnum;

import java.util.List;

public interface SyncManager {

    Changes sync(List<String> pcFiles, List<String> mobileFiles, List<String> statusFiles);

    SyncEnum checkFile(String fileName, boolean pc, boolean mobile, boolean stat);
}
