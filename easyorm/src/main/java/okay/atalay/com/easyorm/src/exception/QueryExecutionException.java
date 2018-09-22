package okay.atalay.com.easyorm.src.exception;

/**
 * Created by 1 on 9.03.2018.
 */

public class QueryExecutionException extends Exception {

    public QueryExecutionException(Exception e) {
        super(e);
    }

    public QueryExecutionException(String s) {
        super(s);
    }
}
