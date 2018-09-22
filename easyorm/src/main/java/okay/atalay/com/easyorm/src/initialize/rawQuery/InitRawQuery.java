package okay.atalay.com.easyorm.src.initialize.rawQuery;

/**
 * Created by 1 on 22.09.2018.
 */

public class InitRawQuery {
    private String rawQuery;

    public String getRawQuery() {
        return rawQuery;
    }

    public void setRawQuery(String rawQuery) {
        this.rawQuery = rawQuery;
    }

    public String getQuery() {
        return getRawQuery();
    }
}
