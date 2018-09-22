package okay.atalay.com.easyorm.src.initialize.insertion;

import java.util.ArrayList;
import java.util.List;

import okay.atalay.com.easyorm.src.column.query.QueryColumn;
import okay.atalay.com.easyorm.src.easyOrm.factory.EasyOrmFactory;
import okay.atalay.com.easyorm.src.sql.Sql;
import okay.atalay.com.easyorm.src.sqlGenerator.SQLGenerator;

/**
 * Created by 1 on 22.09.2018.
 */

public class InitInsertion {
    private String into;
    private List<QueryColumn> columns = new ArrayList<>();
    private Sql sql;
    private Object[] parms;

    public String getInto() {
        return into;
    }

    public void setInto(String tableName) {
        this.into = tableName;
    }

    public void addColumn(QueryColumn queryColumn) {
        columns.add(queryColumn);
    }

    public List<QueryColumn> getColumns() {
        return columns;
    }

    public Sql getSql() {
        return sql;
    }

    public void setSql(Sql sql) {
        this.sql = sql;
    }

    public Object[] getParms() {
        return parms;
    }

    public void setParms(Object[] parms) {
        this.parms = parms;
    }

    public void generateSql() {
        String query = SQLGenerator.getInstance().parseInitializeInsertion(this);
        if (EasyOrmFactory.verboseQueries) {
            System.out.println(query);
        }
        sql = new Sql();
        sql.setSql(query);
    }

}
