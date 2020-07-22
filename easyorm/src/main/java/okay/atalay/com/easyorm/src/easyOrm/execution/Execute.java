package okay.atalay.com.easyorm.src.easyOrm.execution;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import okay.atalay.com.easyorm.src.NameValuePair;
import okay.atalay.com.easyorm.src.easyOrm.factory.EasyOrmFactory;
import okay.atalay.com.easyorm.src.exception.QueryExecutionException;
import okay.atalay.com.easyorm.src.sql.Sql;

/**
 * Created by 1 on 12.03.2018.
 */

class Execute {

    protected static void executeSelection(SQLiteDatabase liteDatabase, Sql sql, String[] parms, ColumnNotify callBack) throws QueryExecutionException {
        Cursor cursor = null;
        try {
            generateLog(sql, parms);
            List<List<NameValuePair>> resultList = new ArrayList<>();
            cursor = liteDatabase.rawQuery(sql.getSql(), parms);
            String[] columnNames = cursor.getColumnNames();
            int i = 0;
            while (cursor.moveToNext()) {
                List<NameValuePair> result = new ArrayList<>();
                for (String column : columnNames) {
                    int columnIndex = cursor.getColumnIndex(column);
                    int type = cursor.getType(columnIndex);
                    switch (type) {
                        case Cursor.FIELD_TYPE_INTEGER:
                            result.add(new NameValuePair(column, cursor.getInt(columnIndex)));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            result.add(new NameValuePair(column, cursor.getFloat(columnIndex)));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            result.add(new NameValuePair(column, cursor.getString(columnIndex)));
                            break;
                        case Cursor.FIELD_TYPE_BLOB:
                            result.add(new NameValuePair(column, cursor.getBlob(columnIndex)));
                            break;
                        case Cursor.FIELD_TYPE_NULL:
                            result.add(new NameValuePair(column, cursor.getString(columnIndex)));
                            break;
                    }
                }
                resultList.add(result);
                i++;
            }
            callBack.setColumnsAndValues(resultList);
        } catch (Exception e) {
            throw new QueryExecutionException("Exception occurs while query is executing :" + e);
        } finally {
            try {
                liteDatabase.close();
            } catch (Exception e) {
            }
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
            }
        }

    }

    public static void executeInsertion(SQLiteDatabase db, Sql sql, Object[] parms) throws QueryExecutionException {
        /*SQLiteStatement stmt = db.compileStatement(sql.getSql());
        for (int i = 0; i < parms.length; i++) {
            stmt.bindString(i + 1, parms[i]);
        }
        stmt.execute();
        stmt.clearBindings();*/
        try {
            generateLog(sql, parms);
            db.execSQL(sql.getSql(), parms);
        } catch (Exception e) {
            throw new QueryExecutionException("Exception occurs while insertion query is executing :" + sql.getSql() + e);
        } finally {
            try {
                db.close();
            } catch (Exception e) {
            }
        }
    }

    public static void executeDeletion(SQLiteDatabase db, Sql sql, Object[] parms) throws QueryExecutionException {
        try {
            generateLog(sql, parms);
            if (parms != null) {
                db.execSQL(sql.getSql(), parms);
            } else {
                db.execSQL(sql.getSql());
            }
        } catch (Exception e) {
            throw new QueryExecutionException("Exception occurs while deletion query is executing :" + sql.getSql() + e);
        } finally {
            try {
                db.close();
            } catch (Exception e) {
            }
        }
    }

    public static void executeUpdate(SQLiteDatabase db, Sql sql, Object[] parms) throws QueryExecutionException {
        try {
            generateLog(sql, parms);
            if (parms != null) {
                db.execSQL(sql.getSql(), parms);
            } else {
                db.execSQL(sql.getSql());
            }
        } catch (Exception e) {
            throw new QueryExecutionException("Exception occurs while update query is executing :" + sql.getSql() + e);
        } finally {
            try {
                db.close();
            } catch (Exception e) {
            }
        }
    }

    public static void executeQueryRawQuery(SQLiteDatabase db, String sql, Object[] parms) throws QueryExecutionException {
        try {
            generateLog(new Sql(sql), parms);
            if (parms != null) {
                db.execSQL(sql, parms);
            } else {
                db.execSQL(sql);
            }
        } catch (Exception e) {
            throw new QueryExecutionException("Exception occurs while update query is executing :" + sql + e);
        } finally {
            try {
                db.close();
            } catch (Exception e) {
            }
        }
    }

    private static void generateLog(Sql sql, Object[] parms) {
        if (EasyOrmFactory.isVerboseQueries()) {
            StringBuilder params = new StringBuilder(250);
            if (parms != null) {
                for (int i = 0; i < parms.length; i++) {
                    if (i != 0) {
                        params.append(",");
                    }
                    params.append(parms[i]);
                }
            }
            Log.d("EasyORM.Exec Queries", sql.getSql() + " parms:" + params.toString());
        }
    }
}
