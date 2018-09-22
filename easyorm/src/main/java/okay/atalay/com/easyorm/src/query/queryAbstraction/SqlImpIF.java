package okay.atalay.com.easyorm.src.query.queryAbstraction;

import okay.atalay.com.easyorm.src.sql.Sql;

/**
 * Created by 1 on 23.03.2018.
 */

public interface SqlImpIF extends  BaseQueryIF{
    Sql getSql();

    void setSql(Sql sql);

}
