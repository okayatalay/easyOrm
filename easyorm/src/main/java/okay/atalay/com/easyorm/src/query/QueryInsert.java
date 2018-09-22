package okay.atalay.com.easyorm.src.query;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import okay.atalay.com.easyorm.src.column.query.QueryColumn;
import okay.atalay.com.easyorm.src.constant.Constants;
import okay.atalay.com.easyorm.src.easyOrm.factory.EasyOrmFactory;
import okay.atalay.com.easyorm.src.query.queryAbstraction.ColumnImpIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.DataSyncImpIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.Query;
import okay.atalay.com.easyorm.src.query.queryAbstraction.SqlImpIF;
import okay.atalay.com.easyorm.src.sql.Sql;
import okay.atalay.com.easyorm.src.sqlGenerator.SQLGenerator;


public class QueryInsert extends Query implements SqlImpIF, DataSyncImpIF, ColumnImpIF {

    private List<QueryColumn> queryColumns = new ArrayList<>();
    private List<String> queryParmColumns = new ArrayList<>();
    private Sql sql;
    private boolean dataSync = false;

    @Override
    public boolean isDataSync() {
        return dataSync;
    }

    @Override
    public void setDataSync(boolean dataSync) {
        this.dataSync = dataSync;
    }


    @Override
    public List<QueryColumn> getColumns() {
        return queryColumns;
    }

    public List<String> getParmColumns() {
        return queryParmColumns;
    }

    public void addColumn(QueryColumn column) {
        if (column.getAttribute(Constants.ATTRIBUTE_VALUE).getValue().equals("?") ||
                column.getAttribute(Constants.ATTRIBUTE_UUID) == null ||
                !column.getAttribute(Constants.ATTRIBUTE_UUID).getValue().equals("true")) {
            queryParmColumns.add(column.getAttribute(Constants.ATTRIBUTE_NAME).getValue());
        }
        queryColumns.add(column);
    }

    public Sql getSql() {
        return sql;
    }

    @Override
    public void setSql(Sql sql) {
        this.sql = sql;
    }

    public void parseSql() {
        String query = SQLGenerator.getInstance().getQuery(this);
        if (EasyOrmFactory.verboseQueries) {
            Log.d("queryInsert:", getName() + ":" + query);
        }
        sql = new Sql();
        sql.setSql(query);
    }

}
