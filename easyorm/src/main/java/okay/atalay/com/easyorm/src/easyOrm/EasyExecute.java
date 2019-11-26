package okay.atalay.com.easyorm.src.easyOrm;

import android.database.Cursor;

import java.util.List;

import okay.atalay.com.easyorm.src.exception.FieldNotFoundException;
import okay.atalay.com.easyorm.src.exception.QueryExecutionException;
import okay.atalay.com.easyorm.src.exception.QueryNotFoundException;

/**
 * Created by 1 on 24.03.2018.
 */

public interface EasyExecute {
    void delete(String queryName, Object[] params) throws QueryNotFoundException, QueryExecutionException;

    void deleteObject(String queryName, Object object) throws QueryNotFoundException, QueryExecutionException, FieldNotFoundException;

    boolean insert(String queryName, Object u) throws QueryNotFoundException, FieldNotFoundException, QueryExecutionException;

    <T> List<T> select(String queryName, final Class<T> input) throws QueryNotFoundException, QueryExecutionException;

    <T> List<T> select(String queryName, Object[] parameters, final Class<T> input) throws QueryNotFoundException, QueryExecutionException;

    void delete(String queryName) throws QueryNotFoundException, QueryExecutionException;

    void update(String queryName, Object[] params) throws QueryNotFoundException, QueryExecutionException;

    void updateObject(String queryName, Object object) throws QueryNotFoundException, QueryExecutionException, FieldNotFoundException;

    void executeRawQuery(String queryName, Object[] params) throws QueryNotFoundException, QueryExecutionException;
}
