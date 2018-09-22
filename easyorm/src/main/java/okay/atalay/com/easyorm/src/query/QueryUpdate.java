package okay.atalay.com.easyorm.src.query;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import okay.atalay.com.easyorm.src.column.query.QueryColumn;
import okay.atalay.com.easyorm.src.easyOrm.factory.EasyOrmFactory;
import okay.atalay.com.easyorm.src.query.clauses.TableClause;
import okay.atalay.com.easyorm.src.query.clauses.Where;
import okay.atalay.com.easyorm.src.query.queryAbstraction.ColumnImpIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.DataSyncImpIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.Query;
import okay.atalay.com.easyorm.src.query.queryAbstraction.SqlImpIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.WhereImpIF;
import okay.atalay.com.easyorm.src.sql.Sql;
import okay.atalay.com.easyorm.src.sqlGenerator.SQLGenerator;


public class QueryUpdate extends Query implements SqlImpIF, DataSyncImpIF, WhereImpIF, ColumnImpIF {

    private List<QueryColumn> columnList = new ArrayList<>();
    private List<Where> whereClauses = new ArrayList<>();
    private List<TableClause> tableClauses = new ArrayList<>();
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
        return columnList;
    }

    public void addColumn(QueryColumn tableColumn) {
        columnList.add(tableColumn);
    }

    @Override
    public List<Where> getWhereClauses() {
        return whereClauses;
    }

    @Override
    public List<TableClause> getTableClauses() {
        return tableClauses;
    }


    public Sql getSql() {
        return sql;
    }

    @Override
    public void setSql(Sql sql) {
        this.sql = sql;
    }

    @Override
    public void addWhere(Where where) {
        whereClauses.add(where);
    }

    @Override
    public void addTable(TableClause tableClause) {
        tableClauses.add(tableClause);
    }

    public void parseSql() {
        String query = SQLGenerator.getInstance().getQuery(this);
        if (EasyOrmFactory.verboseQueries) {
            Log.d("queryUpdate:", getName() + ":" + query);
        }
        sql = new Sql();
        sql.setSql(query);
    }

}
