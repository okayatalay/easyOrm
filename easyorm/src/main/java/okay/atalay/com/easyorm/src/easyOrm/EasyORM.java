package okay.atalay.com.easyorm.src.easyOrm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import okay.atalay.com.easyorm.src.CreateTableListener;
import okay.atalay.com.easyorm.src.Table.Table;
import okay.atalay.com.easyorm.src.easyOrm.execution.EasyExecuteImpl;
import okay.atalay.com.easyorm.src.easyOrm.factory.EasyOrmFactory;
import okay.atalay.com.easyorm.src.exception.ColumnNotFoundException;
import okay.atalay.com.easyorm.src.exception.EasyORMNotFoundException;
import okay.atalay.com.easyorm.src.exception.EasyORMVersionNotFoundException;
import okay.atalay.com.easyorm.src.exception.InitializeTagException;
import okay.atalay.com.easyorm.src.exception.InvalidColumnException;
import okay.atalay.com.easyorm.src.exception.SqlNotFoundException;
import okay.atalay.com.easyorm.src.exception.TableNotFoundException;
import okay.atalay.com.easyorm.src.exception.UpgradesTagException;
import okay.atalay.com.easyorm.src.initialize.Initializer;
import okay.atalay.com.easyorm.src.initialize.insertion.InitInsertion;
import okay.atalay.com.easyorm.src.initialize.rawQuery.InitRawQuery;
import okay.atalay.com.easyorm.src.query.queryAbstraction.BaseQueryIF;
import okay.atalay.com.easyorm.src.upgrade.UpgradeQuery;
import okay.atalay.com.easyorm.src.upgrade.UpgradeState;
import okay.atalay.com.easyorm.src.upgrade.table.UpgradeTable;
import okay.atalay.com.easyorm.src.xmlparser.XMLParser;

/**
 * Created by 1 on 13.03.2018.
 */

public class EasyORM {
    private XMLParser parser;
    private List<Table> tables = new ArrayList<>();
    private List<UpgradeTable> upgradeTableArrayList = new ArrayList<>();
    private List<UpgradeQuery> upgrades = new ArrayList<>();
    private List<BaseQueryIF> QueryList = new ArrayList<>();
    private Initializer initializer = new Initializer();
    private String databaseName;
    private EasyExecute easyExecute;
    private Context context;
    private final String QUERY_TAG = "EasyORM.createTable";
    private final String INITIALIZER_TAG = "EasyORM.initializer";
    private List<CreateTableListener> createTableListeners = new ArrayList<>();
    private int version = 1;
    private DataBase dataBase;
    private UpgradeState upgradeState = UpgradeState.NOT_STARTED;

    public EasyORM(Context context, String databaseName) {
        this.databaseName = databaseName;
        this.context = context;
    }

    public SQLiteDatabase getReadableDatabase() {
        return dataBase.getReadableDatabase();
    }

    public SQLiteDatabase getWritableDatabase() {
        return dataBase.getWritableDatabase();
    }

    private class DataBase extends SQLiteOpenHelper {

        public DataBase() {
            super(context, databaseName, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for (CreateTableListener listener : createTableListeners) {
                listener.beforeCreateTable();
            }
            createTables(db);
            initializeTable(db);
            for (CreateTableListener listener : createTableListeners) {
                listener.afterCreateTable();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            boolean b = upgradeDataBase(sqLiteDatabase, i, i1);
            if (!b) {
                upgradeState = UpgradeState.FAILED;
                Log.d(QUERY_TAG, "upgrade is failed");
            } else {
                Log.d(QUERY_TAG, "upgrade is success");
                upgradeState = UpgradeState.SUCCESS;
            }
        }
    }


    public EasyExecute getEasyExecute() {
        if (easyExecute == null) {
            easyExecute = new EasyExecuteImpl(QueryList, this);
        }
        return easyExecute;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public boolean registerXMLSchema(String xmlPath) throws Exception {
        long t1 = System.currentTimeMillis();
        parser = new XMLParser(tables, QueryList, upgrades, upgradeTableArrayList);
        parseTablesAndQueries(xmlPath);
        long t2 = System.currentTimeMillis();
        if (EasyOrmFactory.isVerboseQueries()) {
            Log.d("EasyORM.Parsing", "Elepsed time is :" + (t2 - t1) + " MS");
        }
        return true;
    }

    private void parseTablesAndQueries(String xmlPath) throws IOException, SAXException, ParserConfigurationException,
            SqlNotFoundException, UpgradesTagException, TableNotFoundException, ColumnNotFoundException,
            InvalidColumnException, EasyORMNotFoundException, EasyORMVersionNotFoundException, IllegalAccessException, InstantiationException, InitializeTagException {
        version = parser.parse(context.getAssets().open(xmlPath));
        parser.parseSql(context.getAssets().open(xmlPath));
        parser.parseUpgrade(context.getAssets().open(xmlPath));
        parser.parseInitialize(context.getAssets().open(xmlPath), initializer);
        dataBase = new DataBase();
    }

    /**
     * this method tries to create tables. if tables are already created before,
     * will do nothing and return true;
     *
     * @return
     */
    private boolean createTables(SQLiteDatabase db) {
        for (Table table : tables) {
            try {
                String sql = table.getSql().getSql();
                if (EasyOrmFactory.isVerboseQueries()) {
                    Log.d(QUERY_TAG, sql);
                }
                db.execSQL(sql);
            } catch (Exception e) {
                throw e;
            }
        }
        return true;
    }

    /**
     * this method tries to create tables. if tables are already created before,
     * will do nothing and return true;
     *
     * @return
     */
    private boolean initializeTable(SQLiteDatabase db) {
        for (InitRawQuery rawQuery : initializer.getInitRawQueryList()) {
            try {
                String sql = rawQuery.getRawQuery();
                if (EasyOrmFactory.isVerboseQueries()) {
                    Log.d(INITIALIZER_TAG, sql);
                }
                db.execSQL(sql);
            } catch (Exception e) {
                throw e;
            }
        }
        for (InitInsertion initInsertion : initializer.getInitInsertionList()) {
            try {
                String sql = initInsertion.getSql().getSql();
                if (EasyOrmFactory.isVerboseQueries()) {
                    Log.d(INITIALIZER_TAG, sql);
                }
                db.execSQL(sql, initInsertion.getParms());
            } catch (Exception e) {
                throw e;
            }
        }
        return true;
    }

    /**
     * When the version is changed by developer, this method will  be triggered automatically
     *
     * @param db
     * @param old
     * @param _new
     * @return true if all queries are success, oherwise, false
     */
    private boolean upgradeDataBase(SQLiteDatabase db, int old, int _new) {
        try {
            for (UpgradeTable upgradeTable : upgradeTableArrayList) {
                int version = upgradeTable.getVersion();
                if (version <= old || version > _new) {
                    continue;
                }
                String sql = upgradeTable.getSql().getSql();
                db.execSQL(sql);
            }
            for (UpgradeQuery query : upgrades) {
                int version = query.getVersion();
                if (version <= old || version > _new) {
                    continue;
                }
                db.execSQL(query.getQuery());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void registerCreateTableListener(CreateTableListener listener) {
        createTableListeners.add(listener);
    }

    public void unRegisterCreateTableListener(CreateTableListener listener) {
        createTableListeners.remove(listener);
    }

    public UpgradeState getUpgradeState() {
        return upgradeState;
    }
}

