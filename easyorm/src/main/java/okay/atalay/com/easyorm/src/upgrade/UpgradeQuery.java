package okay.atalay.com.easyorm.src.upgrade;

/**
 * Created by 1 on 16.09.2018.
 */

public interface UpgradeQuery {

    String getQuery();

    int getVersion();
}
