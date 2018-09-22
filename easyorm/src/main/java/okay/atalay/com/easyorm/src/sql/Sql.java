package okay.atalay.com.easyorm.src.sql;

import java.util.UUID;

public class Sql {
    private String sql;
    private boolean uuid = false;

    public String getSql() {
        if (!uuid) return sql;
        return sql.replace("#uuid", "'" + UUID.randomUUID().toString() + "'");
    }

    public void setSql(String sql) {
        if (sql.contains("#uuid")) uuid = true;
        this.sql = sql;
    }
}
