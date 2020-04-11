package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.Settings;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MyFileManagerTest {

    MyFileManager myFileManager;

    @BeforeEach
    void init(){
        myFileManager = MyFileManager.getInstance(Settings.getInstance());
    }


    @Test
    void getItemsList() {
        var file = new File(Settings.getInstance().getValue(Settings.SettingsKeys.FOLDER_LOCATION));
        int expectedValue = Objects.requireNonNull(file.listFiles(myFileManager)).length;

        List<String> fileList = myFileManager.getItemsList();

        assertEquals(expectedValue, fileList.size());
    }
}