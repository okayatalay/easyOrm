package okay.atalay.com.easyorm.src.exception;

/**
 * Created by 1 on 9.03.2018.
 */

public class ConstructorNotFoundException extends Exception {

    public ConstructorNotFoundException(Exception e) {
        super(e);
    }

    public ConstructorNotFoundException(String s) {
        super(s);
    }
}
