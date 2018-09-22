package okay.atalay.com.easyorm.src.query.queryAbstraction;

import java.util.List;

import okay.atalay.com.easyorm.src.column.query.QueryColumn;

/**
 * Created by 1 on 23.03.2018.
 */

public interface ColumnImpIF {
    void addColumn(QueryColumn queryColumn);

    List<QueryColumn> getColumns();
}
