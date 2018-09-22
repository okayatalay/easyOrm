package okay.atalay.com.easyorm.src.easyOrm.factory;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okay.atalay.com.easyorm.src.dataSync.DataSyncListener;
import okay.atalay.com.easyorm.src.easyOrm.EasyORM;


public class EasyOrmFactory {
    private static Map<String, EasyORM> map = new HashMap<>();
    private static List<DataSyncListener> listenerList = new ArrayList<>();
    public static boolean verboseQueries = false;
    public static boolean exactMatch = false;
    public static boolean enableDataSync = true;


    /**
     * @param dbName to create new database given name
     * @return given dbname is exist, the db is returned. otherwise, a new database is created and the new one is returned
     */
    public static EasyORM getOrCreateDataBase(Context context, String dbName) {
        EasyORM easyORM;
        synchronized (map) {
            easyORM = map.get(dbName);
            if (easyORM == null) {
                easyORM = new EasyORM(context, dbName);
                map.put(dbName, easyORM);
            }
        }
        return easyORM;
    }

    public static boolean isEnableDataSync() {
        return enableDataSync;
    }

    /**
     * to disable all dataSync set it as false. if it is set true,  dataSync is triggered only  dataSync property is true
     * default value is true
     *
     * @param enableDataSync
     */
    public static void setEnableDataSync(boolean enableDataSync) {
        EasyOrmFactory.enableDataSync = enableDataSync;
    }

    /**
     * @param b this must be set true to enable all created queries
     */
    public static void verboseAllQueries(boolean b) {
        verboseQueries = b;
    }

    public static boolean isVerboseQueries() {
        return verboseQueries;
    }

    public static boolean isExactMatch() {
        return exactMatch;
    }

    /**
     * if you enable this param, you must define all columns which are selected. Otherwise, only match fields are filled.
     *
     * @param exactMatch
     */
    public static void setExactMatch(boolean exactMatch) {
        EasyOrmFactory.exactMatch = exactMatch;
    }

    /**
     * when a query runs and its dataSync property is true then to get dataSync, register your class
     *
     * @param listener
     */
    public static void registerDataSync(DataSyncListener listener) {
        listenerList.add(listener);
    }

    /**
     * please do not forget to use on your class going to die. if you forget, gc will not clean your unused objet
     *
     * @param listener
     */
    public static void unRegisterDataSync(DataSyncListener listener) {
        listenerList.remove(listener);
    }

    public static List<DataSyncListener> getListenerList() {
        return listenerList;
    }
}
