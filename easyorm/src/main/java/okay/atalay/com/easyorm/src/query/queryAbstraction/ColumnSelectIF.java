package okay.atalay.com.easyorm.src.query.queryAbstraction;

import java.util.List;

import okay.atalay.com.easyorm.src.column.query.SelectColumn;

/**
 * Created by 1 on 23.03.2018.
 */

public interface ColumnSelectIF {
    List<SelectColumn> getColumns();
    void addColumn(SelectColumn queryColumn);
}
