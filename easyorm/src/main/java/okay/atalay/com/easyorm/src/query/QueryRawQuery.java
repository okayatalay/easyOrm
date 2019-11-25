package okay.atalay.com.easyorm.src.query;

/**
 * Created by 1 on 24.11.2019.
 */

public class QueryRawQuery {
    private String name;
    private String sql;


    public QueryRawQuery(String name, String sql) {
        this.name = name;
        this.sql = sql;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
