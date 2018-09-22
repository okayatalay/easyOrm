package okay.atalay.com.easyorm.src.dataSync;

/**
 * Created by 1 on 13.03.2018.
 */

public interface DataSyncListener {
    void receiveDataSync(String queryName, Object result);
}
