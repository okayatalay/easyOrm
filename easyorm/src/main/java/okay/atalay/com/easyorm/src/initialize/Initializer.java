package okay.atalay.com.easyorm.src.initialize;

import java.util.ArrayList;
import java.util.List;

import okay.atalay.com.easyorm.src.initialize.insertion.InitInsertion;
import okay.atalay.com.easyorm.src.initialize.rawQuery.InitRawQuery;

/**
 * Created by 1 on 22.09.2018.
 */

public class Initializer {
    private List<InitRawQuery> initRawQueryList = new ArrayList<>();
    private List<InitInsertion> initInsertionList = new ArrayList<>();

    public List<InitRawQuery> getInitRawQueryList() {
        return initRawQueryList;
    }

    public List<InitInsertion> getInitInsertionList() {
        return initInsertionList;
    }
}
