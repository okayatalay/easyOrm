package okay.atalay.com.easyorm.src.query.subQuery;

import java.util.ArrayList;
import java.util.List;

import okay.atalay.com.easyorm.src.column.query.SelectColumn;
import okay.atalay.com.easyorm.src.query.clauses.Where;
import okay.atalay.com.easyorm.src.query.queryAbstraction.ColumnSelectIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.Query;
import okay.atalay.com.easyorm.src.query.queryAbstraction.SelectImpIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.WhereImpIF;

public class Select extends Query implements ColumnSelectIF, WhereImpIF, SelectImpIF {

    private List<Where> whereClauses = new ArrayList<>();
    private List<SelectColumn> columns = new ArrayList<>();
    private boolean star = false;

    public Select() {
        setType("select");
    }

    @Override
    public List<SelectColumn> getColumns() {
        return columns;
    }

    @Override
    public void addColumn(SelectColumn column) {
        if (column.getName().equals("") || column.getName().contains("*")) {
            star = true;
        }
        this.columns.add(column);
    }


    @Override
    public void addWhere(Where where) {
        whereClauses.add(where);
    }

    @Override
    public List<Where> getWhereClauses() {
        return whereClauses;
    }


    @Override
    public boolean isStar() {
        return star;
    }

    @Override
    public void parseSql() {
        
    }
}
