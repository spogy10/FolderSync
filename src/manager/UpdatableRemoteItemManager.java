package manager;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import library.sharedpackage.manager.RemoteItemManager;

public interface UpdatableRemoteItemManager extends RemoteItemManager {
    StringProperty getUpdateProperty();

    DoubleProperty getProgressProperty();
}
