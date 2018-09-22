package okay.atalay.com.easyorm.src.query;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import okay.atalay.com.easyorm.src.column.query.Having;
import okay.atalay.com.easyorm.src.column.query.SelectColumn;
import okay.atalay.com.easyorm.src.easyOrm.factory.EasyOrmFactory;
import okay.atalay.com.easyorm.src.query.clauses.GroupBy;
import okay.atalay.com.easyorm.src.query.clauses.OrderBy;
import okay.atalay.com.easyorm.src.query.clauses.Where;
import okay.atalay.com.easyorm.src.query.queryAbstraction.ColumnSelectIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.DataSyncImpIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.Query;
import okay.atalay.com.easyorm.src.query.queryAbstraction.SelectImpIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.SqlImpIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.WhereImpIF;
import okay.atalay.com.easyorm.src.sql.Sql;
import okay.atalay.com.easyorm.src.sqlGenerator.SQLGenerator;


public class QuerySelect extends Query implements SqlImpIF, DataSyncImpIF, WhereImpIF, ColumnSelectIF, SelectImpIF {

    private List<Where> whereClauses = new ArrayList<>();
    private Sql sql;
    private List<SelectColumn> columns = new ArrayList<>();
    private List<Having> havings = new ArrayList<>();
    private boolean dataSync = false;
    private List<OrderBy> orderByList = new ArrayList<>();
    private GroupBy groupBy;
    private boolean distinct = false;
    private boolean star = false;

    @Override
    public boolean isDataSync() {
        return dataSync;
    }

    @Override
    public void setDataSync(boolean dataSync) {
        this.dataSync = dataSync;
    }

    public List<SelectColumn> getColumns() {
        return columns;
    }

    @Override
    public void addColumn(SelectColumn column) {
        if (column.getName().equals("") || column.getName().contains("*")) {
            star = true;
        }
        this.columns.add(column);
    }

    @Override
    public List<Where> getWhereClauses() {
        return whereClauses;
    }


    @Override
    public Sql getSql() {
        return sql;
    }

    public void setSql(Sql sql) {
        this.sql = sql;
    }

    @Override
    public void addWhere(Where where) {
        whereClauses.add(where);
    }


    public void parseSql() {
        String query = SQLGenerator.getInstance().getQuery(this);
        if (EasyOrmFactory.verboseQueries) {
            Log.d("querySelect:", getName() + ":" + query);
        }
        sql = new Sql();
        sql.setSql(query);
    }


    public List<OrderBy> getOrderByList() {
        return orderByList;
    }

    public void addOrderBy(OrderBy orderBy) {
        this.orderByList.add(orderBy);
    }


    public GroupBy getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(GroupBy groupBy) {
        this.groupBy = groupBy;
    }

    public boolean isDistinct() {
        return distinct;
    }


    public void setDistinct(String distinct) {
        if ("true".equals(distinct)) {
            this.distinct = true;
        }
    }


    @Override
    public boolean isStar() {
        return star;
    }

    public void addHaving(Having having) {
        havings.add(having);
    }

    public List<Having> getHavings() {
        return havings;
    }
}
