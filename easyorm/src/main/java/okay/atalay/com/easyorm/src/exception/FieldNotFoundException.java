package okay.atalay.com.easyorm.src.exception;

/**
 * Created by 1 on 9.03.2018.
 */

public class FieldNotFoundException extends Exception {

    public FieldNotFoundException(Exception e) {
        super(e);
    }

    public FieldNotFoundException(String s) {
        super(s);
    }
}
