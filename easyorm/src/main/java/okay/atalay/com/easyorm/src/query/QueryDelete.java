package okay.atalay.com.easyorm.src.query;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import okay.atalay.com.easyorm.src.easyOrm.factory.EasyOrmFactory;
import okay.atalay.com.easyorm.src.query.clauses.Where;
import okay.atalay.com.easyorm.src.query.queryAbstraction.DataSyncImpIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.Query;
import okay.atalay.com.easyorm.src.query.queryAbstraction.SqlImpIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.WhereImpIF;
import okay.atalay.com.easyorm.src.sql.Sql;
import okay.atalay.com.easyorm.src.sqlGenerator.SQLGenerator;


public class QueryDelete extends Query implements SqlImpIF, DataSyncImpIF, WhereImpIF {

    private List<Where> whereClauses = new ArrayList<>();
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

    @Override
    public void parseSql() {
        String query = SQLGenerator.getInstance().getQuery(this);
        if (EasyOrmFactory.verboseQueries) {
            Log.d("queryDelete:", getName() + ":" + query);
        }
        sql = new Sql();
        sql.setSql(query);
    }


}
