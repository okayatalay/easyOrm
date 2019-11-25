package okay.atalay.com.easyorm.src.easyOrm.execution;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okay.atalay.com.easyorm.src.NameValuePair;
import okay.atalay.com.easyorm.src.column.query.QueryColumn;
import okay.atalay.com.easyorm.src.constant.Constants;
import okay.atalay.com.easyorm.src.dataSync.DataSyncListener;
import okay.atalay.com.easyorm.src.easyOrm.EasyExecute;
import okay.atalay.com.easyorm.src.easyOrm.EasyORM;
import okay.atalay.com.easyorm.src.easyOrm.factory.EasyOrmFactory;
import okay.atalay.com.easyorm.src.exception.ConstructorNotFoundException;
import okay.atalay.com.easyorm.src.exception.FieldNotFoundException;
import okay.atalay.com.easyorm.src.exception.QueryExecutionException;
import okay.atalay.com.easyorm.src.exception.QueryNotFoundException;
import okay.atalay.com.easyorm.src.query.QueryDelete;
import okay.atalay.com.easyorm.src.query.QueryInsert;
import okay.atalay.com.easyorm.src.query.QueryRawQuery;
import okay.atalay.com.easyorm.src.query.QuerySelect;
import okay.atalay.com.easyorm.src.query.QueryUpdate;
import okay.atalay.com.easyorm.src.query.clauses.Where;
import okay.atalay.com.easyorm.src.query.queryAbstraction.BaseQueryIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.DataSyncImpIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.Query;
import okay.atalay.com.easyorm.src.query.queryAbstraction.SqlImpIF;

public class EasyExecuteImpl implements EasyExecute {

    private List<BaseQueryIF> queries;
    private EasyORM easyORM;
    private List<QueryRawQuery> queryRawQueries;
    private final String QUERY_TAG = "EasyORM.query";
    private Object lock = new Object();

    public EasyExecuteImpl(List<BaseQueryIF> queries, List<QueryRawQuery> queryRawQueries, EasyORM easyORM) {
        super();
        this.easyORM = easyORM;
        this.queries = queries;
        this.queryRawQueries = queryRawQueries;
    }

    /**
     * @param queryName  must be selection query
     * @param parameters if selection query takes parameters, the params must be passed as array
     * @param input      to be filled class. it must have setter methods. fields are selected depend on column names
     * @param <T>
     * @return result list of executed query
     * @throws QueryNotFoundException
     * @throws QueryExecutionException
     */
    @Override
    public <T> List<T> select(String queryName, Object[] parameters, final Class<T> input) throws QueryNotFoundException, QueryExecutionException {
        Query query = (Query) findQuery(queryName);
        if (query == null) {
            throw new QueryNotFoundException(" query not found for given queryName " + queryName);
        } else if (!query.getType().equals(Constants.SELECT)) {
            throw new QueryNotFoundException(" query is found but it is not selection operation" + queryName);
        }
        final List<T> resultList = new ArrayList<>();
        QuerySelect q = (QuerySelect) query;
        int i = 0;
        String[] parms = new String[parameters.length];
        for (Object o : parameters) {
            if (o instanceof Boolean) {
                parms[i++] = o.toString().equals("true") ? "1" : "0";
            } else {
                parms[i++] = o.toString();
            }
        }
        synchronized (lock) {
            Execute.executeSelection(easyORM.getReadableDatabase(), q.getSql(), parms, new ColumnNotify() {
                @Override
                public void setColumnsAndValues(List<List<NameValuePair>> result) throws FieldNotFoundException, ConstructorNotFoundException {

                    for (int i = 0; i < result.size(); i++) {
                        try {
                            T t = input.newInstance();
                            for (NameValuePair nvp : result.get(i)) {
                                try {
                                    Field field;
                                    try {
                                        //try to get field from main class
                                        field = t.getClass().getDeclaredField(nvp.getName());
                                    } catch (NoSuchFieldException e) {
                                        //maybe field can be in superclass
                                        field = t.getClass().getSuperclass().getDeclaredField(nvp.getName());
                                    }
                                    if (field.getGenericType() == Date.class) {
                                        Method method = t.getClass().getMethod("set" + nvp.getName().substring(0, 1).toUpperCase().replace("İ", "I") + nvp.getName().substring(1), field.getType());
                                        method.invoke(t, new Date(nvp.getValue().toString()));
                                    } else if (field.getGenericType() == Boolean.class) {
                                        Method method = t.getClass().getMethod("set" + nvp.getName().substring(0, 1).toUpperCase().replace("İ", "I") + nvp.getName().substring(1), field.getType());
                                        boolean enable = nvp.getValue().equals("true") || nvp.getValue().equals(1) ? true : false;
                                        method.invoke(t, enable);
                                    } else {
                                        Method method = t.getClass().getMethod("set" + nvp.getName().substring(0, 1).toUpperCase().replace("İ", "I") + nvp.getName().substring(1), field.getType());
                                        method.invoke(t, nvp.getValue());
                                    }
                                } catch (Exception e) {
                                    if (EasyOrmFactory.exactMatch) {
                                        throw new FieldNotFoundException("are you missing to define field/setter method for " + nvp.getName());
                                    }
                                    e.printStackTrace();
                                }
                            }
                            resultList.add(t);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new ConstructorNotFoundException("are you missing to define default constructor for" + input.getSimpleName() + " ?\n" + e);
                        }
                    }

                }
            });
        }
        sendDataSync(q, resultList);
        return resultList;
    }

    /**
     * @param queryName must be selection query
     * @param input     to be filled class. it must have setter methods. fields are selected depend on column names
     * @param <T>
     * @return result list of executed query
     * @throws QueryNotFoundException
     * @throws QueryExecutionException
     */
    @Override
    public <T> List<T> select(String queryName, final Class<T> input) throws QueryNotFoundException, QueryExecutionException {
        Query query = (Query) findQuery(queryName);
        if (query == null) {
            throw new QueryNotFoundException(" query not found for given queryName " + queryName);
        } else if (!query.getType().equals(Constants.SELECT)) {
            throw new QueryNotFoundException(" query is found but it is not selection operation " + queryName);
        }
        QuerySelect q = (QuerySelect) query;
        final List<T> resultList = new ArrayList<>();
        synchronized (lock) {
            Execute.executeSelection(easyORM.getReadableDatabase(), q.getSql(), null, new ColumnNotify() {
                @Override
                public void setColumnsAndValues(List<List<NameValuePair>> result) throws FieldNotFoundException, ConstructorNotFoundException {
                    for (int i = 0; i < result.size(); i++) {
                        try {
                            T t = input.newInstance();
                            for (NameValuePair nvp : result.get(i)) {
                                try {
                                    Field field;
                                    try {
                                        //try to get field from main class
                                        field = t.getClass().getDeclaredField(nvp.getName());
                                    } catch (NoSuchFieldException e) {
                                        //maybe field can be in superclass
                                        field = t.getClass().getSuperclass().getDeclaredField(nvp.getName());
                                    }
                                    if (field.getGenericType() == Date.class) {
                                        Method method = t.getClass().getMethod("set" + nvp.getName().substring(0, 1).toUpperCase().replace("İ", "I") + nvp.getName().substring(1), field.getType());
                                        method.invoke(t, new Date(nvp.getValue().toString()));
                                    } else if (field.getGenericType() == Boolean.class) {
                                        Method method = t.getClass().getMethod("set" + nvp.getName().substring(0, 1).toUpperCase().replace("İ", "I") + nvp.getName().substring(1), field.getType());
                                        boolean enable = nvp.getValue().equals("true") || nvp.getValue().equals(1) ? true : false;
                                        method.invoke(t, enable);
                                    } else {
                                        Method method = t.getClass().getMethod("set" + nvp.getName().substring(0, 1).toUpperCase().replace("İ", "I") + nvp.getName().substring(1), field.getType());
                                        method.invoke(t, nvp.getValue());
                                    }
                                } catch (Exception e) {
                                    if (EasyOrmFactory.exactMatch) {
                                        throw new FieldNotFoundException("are you missing to define field/setter method for " + nvp.getName());
                                    }
                                    e.printStackTrace();
                                }
                            }
                            resultList.add(t);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new ConstructorNotFoundException("are you missing to define default constructor for" + input.getSimpleName() + " ?\n" + e);
                        }
                    }
                }
            });
        }
        sendDataSync(q, resultList);
        return resultList;
    }

    /**
     * @param queryName must be insertion query
     * @param input,    to be used field must have getter methods. fields are selected depend on column names.
     *                  input can be array, so in this case, given query will be executed in a loop
     * @return returns true if insertion operation is success, otherwise returns false
     * @throws QueryNotFoundException
     */
    @Override
    public boolean insert(String queryName, Object input) throws QueryNotFoundException, FieldNotFoundException, QueryExecutionException {
        Query query = (Query) findQuery(queryName);
        if (query == null) {
            throw new QueryNotFoundException(" query not found for given queryName " + queryName);
        } else if (!query.getType().equals(Constants.INSERT)) {
            throw new QueryNotFoundException(" query is found but it is not insertion operation" + queryName);
        }
        QueryInsert q = (QueryInsert) query;
        List<String> columns = q.getParmColumns();
        if (input instanceof Object[]) {
            SQLiteDatabase writableDatabase = null;
            try {
                writableDatabase = easyORM.getWritableDatabase();
                writableDatabase.beginTransaction();
                Object[] array = (Object[]) input;
                for (Object o : array) {
                    insert(queryName, o);
                }
                writableDatabase.setTransactionSuccessful();
            } finally {
                if (writableDatabase != null)
                    writableDatabase.endTransaction();
            }
        }
        if (input instanceof List) {
            SQLiteDatabase writableDatabase = null;
            try {
                writableDatabase = easyORM.getWritableDatabase();
                writableDatabase.beginTransaction();
                List list = (List) input;
                for (Object o : list) {
                    insert(queryName, o);
                }
                writableDatabase.setTransactionSuccessful();
            } finally {
                if (writableDatabase != null)
                    writableDatabase.endTransaction();
            }
        } else {
            Object[] parms = new Object[columns.size()];
            int i = 0;
            for (String column : columns) {
                try {
                    Method method = input.getClass().getMethod("get" + column.substring(0, 1).toUpperCase().replace("İ", "I") + column.substring(1), null);
                    Object parm = method.invoke(input, null);
                    parms[i++] = parm;
                } catch (Exception e) {
                    throw new FieldNotFoundException("are you missing to define field or getter method ?" + column + "\n" + e);
                }
            }
            synchronized (lock) {
                Execute.executeInsertion(easyORM.getWritableDatabase(), q.getSql(), parms);
            }
            sendDataSync(q, true);
        }
        return true;
    }

    /**
     * This method allows users to perform any deleting operations.
     * Fitst parms points the queryName which is defined in xml file
     * second parms provides to pass parameters to query to use in where clauses
     *
     * @param queryName
     * @param params
     */

    @Override
    public void delete(String queryName, Object[] params) throws QueryNotFoundException, QueryExecutionException {
        Query query = (Query) findQuery(queryName);
        if (query == null) {
            throw new QueryNotFoundException(" query not found for given queryName " + queryName);
        } else if (!query.getType().equals(Constants.DELETE)) {
            throw new QueryNotFoundException(" query is found but it is not deletion operation" + queryName);
        }
        QueryDelete q = (QueryDelete) query;
        synchronized (lock) {
            Execute.executeDeletion(easyORM.getWritableDatabase(), q.getSql(), params);
        }
        sendDataSync(q, true);
    }

    /**
     * This method allows users to perform any deleting operations.
     * Fitst parms points the queryName which is defined in xml file
     * second parms provides to pass parameters to query to use in where clauses
     *
     * @param queryName
     * @param object
     */

    @Override
    public void deleteObject(String queryName, Object object) throws QueryNotFoundException, QueryExecutionException, FieldNotFoundException {
        Query query = (Query) findQuery(queryName);
        if (query == null) {
            throw new QueryNotFoundException(" query not found for given queryName " + queryName);
        } else if (!query.getType().equals(Constants.DELETE)) {
            throw new QueryNotFoundException(" query is found but it is not deletion operation" + queryName);
        }
        QueryDelete q = (QueryDelete) query;
        List<Where> whereClauses = q.getWhereClauses();
        Object[] parms = new Object[whereClauses.size()];
        int i = 0;
        for (Where where : whereClauses) {
            String name = where.getName();
            try {
                Method method = object.getClass().getMethod("get" + name.substring(0, 1).toUpperCase().replace("İ", "I") + name.substring(1), null);
                Object parm = method.invoke(object, null);
                parms[i++] = parm;
            } catch (Exception e) {
                if (EasyOrmFactory.exactMatch) {
                    throw new FieldNotFoundException("are you missing to define field or getter method ?\n" + name + " -> " + e);
                }
                e.printStackTrace();
            }
        }
        synchronized (lock) {
            Execute.executeDeletion(easyORM.getWritableDatabase(), q.getSql(), parms);
        }
        sendDataSync(q, true);
    }

    /**
     * it deletes all rows in given table
     *
     * @param queryName
     * @throws QueryNotFoundException
     * @throws QueryExecutionException
     */

    @Override
    public void delete(String queryName) throws QueryNotFoundException, QueryExecutionException {
        QueryDelete q = (QueryDelete) findQuery(queryName);
        if (q == null) {
            throw new QueryNotFoundException(" query not found for given queryName " + queryName);
        } else if (!q.getType().equals(Constants.DELETE)) {
            throw new QueryNotFoundException(" query is found but it is not deletion operation" + queryName);
        }
        synchronized (lock) {
            Execute.executeDeletion(easyORM.getWritableDatabase(), q.getSql(), null);
        }
        sendDataSync(q, true);
    }

    /**
     * This method allows users to perform any update operations.
     * First parms points the queryName which is defined in xml file
     * second parms provides to pass parameters to query to use in set clauses
     *
     * @param queryName
     * @param params
     */

    @Override
    public void update(String queryName, Object[] params) throws QueryNotFoundException, QueryExecutionException {
        Query query = (Query) findQuery(queryName);
        if (query == null) {
            throw new QueryNotFoundException(" query not found for given queryName " + queryName);
        } else if (!query.getType().equals(Constants.UPDATE)) {
            throw new QueryNotFoundException(" query is found but it is not update operation" + queryName);
        }
        QueryUpdate q = (QueryUpdate) query;
        synchronized (lock) {
            Execute.executeUpdate(easyORM.getWritableDatabase(), q.getSql(), params);
        }
        sendDataSync(q, true);
    }


    /**
     * This method allows users to perform any update operations.
     * First parms points the queryName which is defined in xml file
     * second parms provides to pass parameters to query to use in set clauses
     *
     * @param queryName
     * @param object
     */

    @Override
    public void updateObject(String queryName, Object object) throws QueryNotFoundException, QueryExecutionException, FieldNotFoundException {
        Query query = (Query) findQuery(queryName);
        if (query == null) {
            throw new QueryNotFoundException(" query not found for given queryName " + queryName);
        } else if (!query.getType().equals(Constants.UPDATE)) {
            throw new QueryNotFoundException(" query is found but it is not update operation" + queryName);
        }
        QueryUpdate q = (QueryUpdate) query;
        List<QueryColumn> columns = q.getColumns();
        List<Object> parms = new ArrayList<>();
        for (QueryColumn column : columns) {
            if (column.getAttribute(Constants.ATTRIBUTE_VALUE).getValue().equals("?")) {
                String columnName = column.getAttribute(Constants.ATTRIBUTE_NAME).getValue();
                try {
                    Method method = object.getClass().getMethod("get" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1), null);
                    Object parm = method.invoke(object, null);
                    parms.add(parm);
                } catch (Exception e) {
                    if (EasyOrmFactory.exactMatch) {
                        throw new FieldNotFoundException("are you missing to define field or getter method for " + columnName + " ?\n" + e);
                    }
                    e.printStackTrace();
                }
            }
        }
        List<Where> whereClauses = q.getWhereClauses();
        for (Where where : whereClauses) {
            String name = where.getName();
            try {
                Method method = object.getClass().getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1), null);
                Object parm = method.invoke(object, null);
                parms.add(parm);
            } catch (Exception e) {
                if (EasyOrmFactory.exactMatch) {
                    throw new FieldNotFoundException("are you missing to define field or getter method ?\n" + name + " -> " + e);
                }
                e.printStackTrace();
            }
        }
        synchronized (lock) {
            Execute.executeUpdate(easyORM.getWritableDatabase(), q.getSql(), parms.toArray());
        }
        sendDataSync(q, true);
    }

    @Override
    public void executeRawQuery(String queryName, Object[] params) throws QueryNotFoundException, QueryExecutionException {
        QueryRawQuery query = findQueryRawQuery(queryName);
        if (query == null) {
            throw new QueryNotFoundException(" query not found for given queryName " + queryName);
        }
        synchronized (lock) {
            Execute.executeQueryRawQuery(easyORM.getWritableDatabase(), query.getSql(), params);
        }
    }

    private SqlImpIF findQuery(String queryName) {
        for (BaseQueryIF q : queries) {
            SqlImpIF s = (SqlImpIF) q;
            if (s.getName().equals(queryName)) {
                if (EasyOrmFactory.verboseQueries) {
                    Log.d(QUERY_TAG, s.getSql().getSql());
                }
                return s;
            }
        }
        return null;
    }

    private QueryRawQuery findQueryRawQuery(String queryName) {
        for (QueryRawQuery q : queryRawQueries) {
            if (q.getName().equals(queryName)) {
                if (EasyOrmFactory.verboseQueries) {
                    Log.d(QUERY_TAG, q.getSql());
                }
                return q;
            }
        }
        return null;
    }

    private void sendDataSync(DataSyncImpIF q, Object result) {
        if (q.isDataSync()) {
            Query query = (Query) q;
            for (DataSyncListener listener : EasyOrmFactory.getListenerList()) {
                listener.receiveDataSync(query.getName(), result);
            }
        }
    }


}
