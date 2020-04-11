package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.Settings;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyFileManagerTest {

    MyFileManager myFileManager;

    @BeforeEach
    void init(){
        myFileManager = MyFileManager.getInstance(Settings.getInstance());
    }


    @Test
    void getItemsList() {
        int expectedValue = 7;

        List<String> fileList = myFileManager.getItemsList();

        assertEquals(expectedValue, fileList.size());
    }
}