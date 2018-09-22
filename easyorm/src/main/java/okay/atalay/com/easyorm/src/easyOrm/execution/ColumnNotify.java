package okay.atalay.com.easyorm.src.easyOrm.execution;

import java.util.List;

import okay.atalay.com.easyorm.src.NameValuePair;
import okay.atalay.com.easyorm.src.exception.ConstructorNotFoundException;
import okay.atalay.com.easyorm.src.exception.FieldNotFoundException;

/**
 * Created by 1 on 12.03.2018.
 */

interface ColumnNotify {
    void setColumnsAndValues(List<List<NameValuePair>> result) throws FieldNotFoundException, ConstructorNotFoundException;
}
