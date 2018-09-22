package okay.atalay.com.easyorm.src.upgrade.table;

import okay.atalay.com.easyorm.src.Table.Table;

/**
 * Created by 1 on 16.09.2018.
 */

public class UpgradeTable extends Table {
    private int version;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
