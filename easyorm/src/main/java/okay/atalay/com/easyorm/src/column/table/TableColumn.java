package okay.atalay.com.easyorm.src.column.table;

import java.util.HashMap;
import java.util.Map;

import okay.atalay.com.easyorm.src.columnAttribute.ColumnAttribute;


public class TableColumn  {

    private Map<String, ColumnAttribute> columnAttributes = new HashMap<>();

    public ColumnAttribute getAttribute(String attribute) {
        return columnAttributes.get(attribute);
    }

    public void addAttribute(String attribute, ColumnAttribute value) {
        if (value.getValue().trim().equals(""))
            return;
        columnAttributes.put(attribute, value);
    }

    public void addAttribute(ColumnAttribute value) {
        if (value.getValue().trim().equals(""))
            return;
        columnAttributes.put(value.getName(), value);
    }

}
