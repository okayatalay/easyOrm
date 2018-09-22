package okay.atalay.com.easyorm.src.query.queryAbstraction;

import java.util.ArrayList;
import java.util.List;

import okay.atalay.com.easyorm.src.query.clauses.TableClause;

public abstract class Query implements BaseQueryIF{

    private List<TableClause> tableClauseList = new ArrayList<>();
    private String name;
    private String type;


    public void addTable(TableClause tableClause) {
        tableClauseList.add(tableClause);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TableClause> getTableClauses() {
        return tableClauseList;
    }


    public String getType() {
        return type;
    }


    public void setType(String queryType) {
        this.type = queryType;
    }

    public void parseSql() {

    }

}
