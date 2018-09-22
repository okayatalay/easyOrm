package okay.atalay.com.easyorm.src.query.clauses;

import java.util.ArrayList;
import java.util.List;

import okay.atalay.com.easyorm.src.column.query.SelectColumn;

/**
 * Created by 1 on 22.03.2018.
 */

public class GroupBy {
    private List<SelectColumn> columns = new ArrayList<>();

    public void addColumn(SelectColumn column) {
        columns.add(column);
    }

    public List<SelectColumn> getColumns() {
        return columns;
    }
}
