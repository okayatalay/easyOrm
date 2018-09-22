package atalay.okay.com.easyormex;

import android.content.Context;

import okay.atalay.com.easyorm.src.easyOrm.EasyExecute;
import okay.atalay.com.easyorm.src.easyOrm.EasyORM;
import okay.atalay.com.easyorm.src.easyOrm.factory.EasyOrmFactory;

/**
 * Created by 1 on 22.09.2018.
 */

public class DatabaseManager {
    public EasyORM easyORM;
    public static EasyExecute easyExecute;

    public DatabaseManager(Context context) {
        easyORM = EasyOrmFactory.getOrCreateDataBase(context, Constant.DB_NAME);
    }

    public synchronized void initDatabase() {
        // to open debug level, set verbose to true, default is false
        EasyOrmFactory.verboseAllQueries(true);
        //if this is set to true ,query and given object match the each otherwise, matching does not be cared
        EasyOrmFactory.setExactMatch(false);
        try {
            easyORM.registerXMLSchema("database.xml");
            easyExecute = easyORM.getEasyExecute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
