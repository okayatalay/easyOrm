package okay.atalay.com.easyorm.src.upgrade.rawQuery;

import okay.atalay.com.easyorm.src.upgrade.UpgradeQuery;

/**
 * Created by 1 on 16.09.2018.
 */

public class RawQuery implements UpgradeQuery {
    private String rawQuery;
    private int version;

    public String getRawQuery() {
        return rawQuery;
    }

    public void setRawQuery(String rawQuery) {
        this.rawQuery = rawQuery;
    }

    @Override
    public String getQuery() {
        return getRawQuery();
    }

    @Override
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
