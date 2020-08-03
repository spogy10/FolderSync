package manager;

import models.Changes;
import syncenum.SyncEnum;

import java.util.List;

public interface SyncManager {

    Changes sync(List<String> pcFiles, List<String> mobileFiles, List<String> statusFiles);
}
