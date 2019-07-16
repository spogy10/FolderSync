package manager;

import model.Changes;
import syncenum.SyncEnum;

import java.util.HashSet;
import java.util.List;

import static syncenum.SyncEnum.*;

public class MySyncManager implements SyncManager {
    private static MySyncManager ourInstance = new MySyncManager();

    public static MySyncManager getInstance() {
        return ourInstance;
    }

    private MySyncManager() {
    }

    @Override
    public Changes sync(List<String> pcFiles, List<String> mobileFiles, List<String> statusFiles) {

        HashSet<String> combination = new HashSet<>(pcFiles);
        combination.addAll(mobileFiles);
        combination.addAll(statusFiles);

        Changes changes = new Changes();

        for(String fileName : combination){
            boolean pc = pcFiles.contains(fileName);
            boolean mobile = mobileFiles.contains(fileName);
            boolean stat = statusFiles.contains(fileName);

            switch(checkFile(fileName, pc, mobile, stat)){
                case ALL: //do nothing
                    break;
                case A_ONLY: //add to b and status
                    changes.addToBAndStatus(fileName);
                    break;
                case B_ONLY: // add to a and status
                    changes.addToAAndStatus(fileName);
                    break;
                case A_AND_B: // add to status
                    changes.addToStatus(fileName);
                    break;
                case A_AND_STATUS: // delete from a and status
                    changes.removeFromAAndStatus(fileName);
                    break;
                case B_AND_STATUS: // delete from b and status
                    changes.removeFromBAndStatus(fileName);
                    break;
                case STATUS_ONLY: // delete from status
                    changes.removeFromStatus(fileName);
                    break;
                default:

            }
        }
        return changes;
    }

    @Override
    public SyncEnum checkFile(String fileName, boolean pc, boolean mobile, boolean stat) {


        if(pc && mobile && stat)
            return ALL;

        if(pc && !mobile && !stat)
            return A_ONLY;

        if(!pc && mobile && !stat)
            return B_ONLY;

        if(pc && !mobile && stat)
            return A_AND_STATUS;

        if(!pc && mobile && stat)
            return B_AND_STATUS;

        if(pc && mobile && !stat)
            return A_AND_B;

        if(!pc && !mobile && stat)
            return STATUS_ONLY;



        return INVALID;
    }
}
