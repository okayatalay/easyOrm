package okay.atalay.com.easyorm.src.column.query;

import java.util.HashMap;
import java.util.Map;

import okay.atalay.com.easyorm.src.columnAttribute.ColumnAttribute;
import okay.atalay.com.easyorm.src.query.subQuery.Select;

/**
 * Created by 1 on 23.03.2018.
 */

public class QueryColumn {
    private Map<String, ColumnAttribute> attributeMap = new HashMap<>();
    private Select select;

    public ColumnAttribute getAttribute(String attributeName) {
        return attributeMap.get(attributeName);
    }

    public void addAttribute(ColumnAttribute attribute) {
        if (attribute.getValue().trim().equals("")) {
            return;
        }
        attributeMap.put(attribute.getName(), attribute);
    }

    public Select getSelect() {
        return select;
    }

    public void setSelect(Select select) {
        this.select = select;
    }
}
