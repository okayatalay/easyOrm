package okay.atalay.com.easyorm.src.sqlGenerator;

import java.util.List;

import okay.atalay.com.easyorm.src.Table.Table;
import okay.atalay.com.easyorm.src.column.query.Having;
import okay.atalay.com.easyorm.src.column.query.QueryColumn;
import okay.atalay.com.easyorm.src.column.query.SelectColumn;
import okay.atalay.com.easyorm.src.column.table.TableColumn;
import okay.atalay.com.easyorm.src.columnAttribute.ColumnAttribute;
import okay.atalay.com.easyorm.src.constant.Constants;
import okay.atalay.com.easyorm.src.initialize.insertion.InitInsertion;
import okay.atalay.com.easyorm.src.query.QueryDelete;
import okay.atalay.com.easyorm.src.query.QueryInsert;
import okay.atalay.com.easyorm.src.query.QuerySelect;
import okay.atalay.com.easyorm.src.query.QueryUpdate;
import okay.atalay.com.easyorm.src.query.clauses.GroupBy;
import okay.atalay.com.easyorm.src.query.clauses.OrderBy;
import okay.atalay.com.easyorm.src.query.clauses.TableClause;
import okay.atalay.com.easyorm.src.query.clauses.Where;
import okay.atalay.com.easyorm.src.query.queryAbstraction.Query;
import okay.atalay.com.easyorm.src.query.subQuery.Select;


public class SQLGenerator {
    private static SQLGenerator generator = new SQLGenerator();

    public static SQLGenerator getInstance() {
        return generator;
    }

    private SQLGenerator() {
    }

    public String getCreateTable(Table table) {
        StringBuilder builderConstraint = new StringBuilder(100);
        StringBuilder builder = new StringBuilder(500);
        builder.append("CREATE TABLE ");
        builder.append(table.getName());
        builder.append("(");

        String[] columnNames = table.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            TableColumn tableColumn = table.getColumn(columnNames[i]);
            ColumnAttribute attributeName = tableColumn.getAttribute(Constants.ATTRIBUTE_NAME);
            builder.append(" '");
            builder.append(attributeName.getValue());
            builder.append("' ");
            ColumnAttribute attributeType = tableColumn.getAttribute(Constants.ATTRIBUTE_TYPE);
            builder.append(attributeType.getValue());
            ColumnAttribute attributeSize = tableColumn.getAttribute(Constants.ATTRIBUTE_SIZE);
            if (attributeSize != null && !Constants.NUMERIC.contains(attributeType.getValue())) {
                builder.append("(");
                builder.append(attributeSize.getValue());
                builder.append(")");
            }
            builder.append(" ");
            ColumnAttribute attributePrimary = tableColumn.getAttribute(Constants.ATTRIBUTE_PRIMARY);
            if (attributePrimary != null && attributePrimary.getValue().equals("true")) {
                builder.append(Constants.COLUMN_ATTRIBUTE_PRIMARY_VALUE);
                builder.append(" ");
            }

            ColumnAttribute attributeAuto = tableColumn.getAttribute(Constants.ATTRIBUTE_AUTOINCR);
            if (attributeAuto != null && attributeAuto.getValue().equals("true")) {
                builder.append(Constants.ATTRIBUTE_AUTOINCR);
                builder.append(" ");
            }

            ColumnAttribute attributeNull = tableColumn.getAttribute(Constants.ATTRIBUTE_NULLABLE);
            if (attributeNull != null && attributeNull.getValue().equals("false")) {
                builder.append(Constants.NOT_NULL);
                builder.append(" ");
            }

            ColumnAttribute attributeUnique = tableColumn.getAttribute(Constants.ATTRIBUTE_UNIQUE);
            if (attributeUnique != null && attributeUnique.getValue().equals("true")) {
                builder.append(Constants.ATTRIBUTE_UNIQUE);
                builder.append(" ");
            }

            if (i != columnNames.length - 1) {
                builder.append(", ");
            }

            ColumnAttribute attributeReference = tableColumn.getAttribute(Constants.ATTRIBUTE_REFERENCE);
            if (attributeReference != null && !attributeReference.getValue().equals("")) {
                builderConstraint.append(", ");
                String[] ref = attributeReference.getValue().split(":");
                builderConstraint.append(Constants.CONSTRAINT);
                builderConstraint.append(" FK_");
                builderConstraint.append(table.getName());
                builderConstraint.append("_");
                builderConstraint.append(attributeName.getValue());
                builderConstraint.append(" ");
                builderConstraint.append(Constants.FOREIGN_KEY);
                builderConstraint.append(" ");
                builderConstraint.append("(");
                builderConstraint.append(attributeName.getValue());
                builderConstraint.append(") REFERENCES ");
                builderConstraint.append(ref[0]);
                builderConstraint.append("(");
                builderConstraint.append(ref[1]);
                builderConstraint.append(")\n");
            }
        }
        builder.append(builderConstraint.toString());
        builder.append(")");
        return builder.toString();
    }

    public String parseInitializeInsertion(InitInsertion insertion) {
        StringBuilder builder = new StringBuilder(500);
        builder.append("insert into ");
        builder.append(insertion.getInto());
        builder.append("(");
        List<QueryColumn> queryColumns = insertion.getColumns();
        Object[] parms = new Object[queryColumns.size()];
        for (int i = 0; i < queryColumns.size(); i++) {
            if (i != 0) {
                builder.append(",");
            }
            QueryColumn queryColumn = queryColumns.get(i);
            String name = queryColumn.getAttribute(Constants.ATTRIBUTE_NAME).getValue();
            builder.append(name);
        }
        builder.append(") values(");
        for (int i = 0; i < queryColumns.size(); i++) {
            if (i != 0) {
                builder.append(",");
            }
            builder.append("?");
            QueryColumn queryColumn = queryColumns.get(i);
            String value = queryColumn.getAttribute(Constants.ATTRIBUTE_VALUE).getValue();
            parms[i] = value;
        }

        builder.append(")");
        insertion.setParms(parms);
        return builder.toString();
    }

    public String getQuery(Query query) {
        StringBuilder builder = new StringBuilder(500);
        if (query.getType().equals(Constants.SELECT)) {
            parseSelect((QuerySelect) query, builder);
        } else if (query.getType().equals(Constants.INSERT)) {
            parseInsert((QueryInsert) query, builder);
        } else if (query.getType().equals(Constants.DELETE)) {
            parseDelete((QueryDelete) query, builder);
        } else if (query.getType().equals(Constants.UPDATE)) {
            parseUpdate((QueryUpdate) query, builder);
        }
        return builder.toString();
    }

    private void parseDelete(QueryDelete query, StringBuilder builder) {
        builder.append(query.getType());
        builder.append(" from ");
        List<TableClause> tableClauses = query.getTableClauses();
        builder.append(tableClauses.get(0).getTable());
        builder.append(" ");
        List<Where> whereClauses = query.getWhereClauses();
        if (whereClauses.size() > 0) {
            parseWhere(whereClauses, builder);
        }
    }

    private void parseInsert(QueryInsert query, StringBuilder builder) {
        builder.append("insert into ");
        builder.append(query.getTableClauses().get(0).getTable());
        builder.append("(");
        List<QueryColumn> queryColumns = query.getColumns();
        for (int i = 0; i < queryColumns.size(); i++) {
            if (i != 0) {
                builder.append(",");
            }
            QueryColumn queryColumn = queryColumns.get(i);
            String name = queryColumn.getAttribute(Constants.ATTRIBUTE_NAME).getValue();
            builder.append(name);
        }
        builder.append(") values(");
        for (int i = 0; i < queryColumns.size(); i++) {
            if (i != 0) {
                builder.append(",");
            }
            QueryColumn queryColumn = queryColumns.get(i);
            Select select = queryColumn.getSelect();
            if (select == null) {
                String value = queryColumn.getAttribute(Constants.ATTRIBUTE_VALUE).getValue();
                ColumnAttribute type = queryColumn.getAttribute(Constants.ATTRIBUTE_TYPE);

                ColumnAttribute attributeUUID = queryColumn.getAttribute(Constants.ATTRIBUTE_UUID);
                if (attributeUUID != null && attributeUUID.getValue().equals("true")) {
                    builder.append("#uuid");
                    builder.append(" ");
                } else if (value.equals("?") || type == null) {
                    builder.append(value);
                } else {
                    String typ = type.getValue();
                    if (Constants.NUMERIC.contains(typ)) {
                        builder.append(value);
                    } else {
                        builder.append("'");
                        builder.append(value);
                        builder.append("'");
                    }
                }
            } else {
                builder.append(" (");
                parseSubSelect(select, builder);
                builder.append(") ");
            }
        }

        builder.append(")");
    }

    private void parseUpdate(QueryUpdate query, StringBuilder builder) {
        builder.append("update ");
        builder.append(query.getTableClauses().get(0).getTable());
        builder.append(" set ");
        List<QueryColumn> queryColumns = query.getColumns();
        for (int i = 0; i < queryColumns.size(); i++) {
            if (i != 0) {
                builder.append(",");
            }
            QueryColumn queryColumn = queryColumns.get(i);
            String name = queryColumn.getAttribute(Constants.ATTRIBUTE_NAME).getValue();
            String value = queryColumn.getAttribute(Constants.ATTRIBUTE_VALUE).getValue();
            builder.append(name);
            builder.append("=");
            if (value.equals("?")) {
                builder.append(value);
            } else {
                String type = queryColumn.getAttribute(Constants.ATTRIBUTE_TYPE).getValue();
                if (Constants.NUMERIC.contains(type)) {
                    builder.append(value);
                } else {
                    builder.append("'");
                    builder.append(value);
                    builder.append("'");
                }
            }

        }
        builder.append(" ");
        List<Where> whereClauses = query.getWhereClauses();
        parseWhere(whereClauses, builder);
    }

    private void parseWhere(List<Where> whereClauses, StringBuilder builder) {
        builder.append(" Where ");
        for (int i = 0; i < whereClauses.size(); i++) {
            Where where = whereClauses.get(i);
            String name = where.getName();
            builder.append(name);

            String value = where.getValue();
            Select select = where.getSelect();
            boolean equals = where.isEquals();
            boolean less = where.isLess();
            boolean greater = where.isGreater();
            String operand = " = ";
            if (equals) {
                if (less) {
                    operand = (" <= ");
                } else if (greater) {
                    operand = (" >= ");
                } else {
                    operand = (" = ");
                }
            } else {
                if (less) {
                    operand = (" < ");
                } else if (greater) {
                    operand = (" > ");
                } else {
                    operand = (" != ");
                }
            }
            if (select == null) {
                if (where.isLike()) {
                    if (equals) {
                        builder.append(" like ");
                    } else {
                        builder.append(" not like ");
                    }
                    builder.append(" '");
                    builder.append(value);
                    builder.append("' ");
                } else {
                    builder.append(operand);
                    if (value.equals("?")) {
                        builder.append(value);
                    } else if (where.is_static()) {
                        if (Constants.NUMERIC.contains(where.getType())) {
                            builder.append(value);
                        } else {
                            builder.append("'");
                            builder.append(value);
                            builder.append("'");
                        }
                    } else {
                        builder.append(value);
                    }
                }
                builder.append(" ");
            } else {
                builder.append(operand);
                builder.append(" ( ");
                parseSubSelect(select, builder);
                builder.append(" ) ");
            }
            String process = where.getProcess();
            if (whereClauses.size() > 1 && i != whereClauses.size() - 1) {
                builder.append(process);
            }
            builder.append(" ");
        }
    }

    private void parseSubSelect(Select query, StringBuilder builder) {
        builder.append(query.getType());
        builder.append(" ");
        if (query.isStar()) {
            builder.append(" * ");
        } else {
            List<SelectColumn> columns = query.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                if (i != 0) {
                    builder.append(",");
                } else {
                    builder.append(" ");
                }
                String s = columns.get(i).getName();
                builder.append(s);
            }
        }
        builder.append(" from ");
        List<TableClause> tableClauses = query.getTableClauses();
        for (int i = 0; i < tableClauses.size(); i++) {
            TableClause tableClause = tableClauses.get(i);
            builder.append(tableClause.getTable());
            if (i != tableClauses.size() - 1 && tableClauses.size() > 1) {
                builder.append(" , ");
            } else {
                builder.append(" ");
            }
        }
        List<Where> whereClauses = query.getWhereClauses();
        if (whereClauses.size() > 0) {
            parseWhere(whereClauses, builder);
        }
    }

    private void parseSelect(QuerySelect query, StringBuilder builder) {
        builder.append(query.getType());
        builder.append(" ");
        boolean distinct = query.isDistinct();
        if (distinct) {
            builder.append(" DISTINCT ");
        }
        if (query.isStar()) {
            builder.append(" * ");
        } else {
            List<SelectColumn> columns = query.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                if (i != 0) {
                    builder.append(",");
                } else {
                    builder.append(" ");
                }
                SelectColumn column = columns.get(i);
                String s = column.getName();
                String alias = column.getAlias();
                boolean avg = column.isAvg();
                boolean count = column.isCount();
                boolean sum = column.isSum();

                if (avg) {
                    builder.append(" AVG(");
                    builder.append(s);
                    builder.append(") ");
                } else if (count) {
                    builder.append(" COUNT(");
                    builder.append(s);
                    builder.append(") ");
                } else if (sum) {
                    builder.append(" SUM(");
                    builder.append(s);
                    builder.append(") ");
                } else {
                    builder.append(s);
                }
                if (alias != null) {
                    builder.append(" as ");
                    builder.append(alias);
                }

            }
        }
        builder.append(" from ");
        List<TableClause> tableClauses = query.getTableClauses();
        for (int i = 0; i < tableClauses.size(); i++) {
            TableClause tableClause = tableClauses.get(i);
            builder.append(tableClause.getTable());
            if (i != tableClauses.size() - 1 && tableClauses.size() > 1) {
                builder.append(" , ");
            } else {
                builder.append(" ");
            }
        }
        List<Where> whereClauses = query.getWhereClauses();
        if (whereClauses.size() > 0) {
            parseWhere(whereClauses, builder);
        }
        parseOrderBy(query, builder);
        parseGroupBy(query, builder);
        parseHaving(query, builder);

    }

    private void parseHaving(QuerySelect query, StringBuilder builder) {
        List<Having> havings = query.getHavings();
        if (havings.size() > 0) {
            builder.append(" HAVING ");
            for (int i = 0; i < havings.size(); i++) {
                if (i != 0) {
                    builder.append(" ");
                    builder.append(havings.get(i - 1).getProcess());
                    builder.append(" ");
                }
                Having having = havings.get(i);

                String name = having.getName();
                String value = having.getValue();
                boolean avg = having.isAvg();
                boolean count = having.isCount();
                boolean sum = having.isSum();
                boolean less = having.isLess();
                boolean equals = having.isEquals();
                boolean greater = having.isGreater();

                if (avg) {
                    builder.append(" AVG(");
                    builder.append(name);
                    builder.append(") ");
                } else if (count) {
                    builder.append(" COUNT(");
                    builder.append(name);
                    builder.append(") ");
                } else if (sum) {
                    builder.append(" SUM(");
                    builder.append(name);
                    builder.append(") ");
                } else {
                    builder.append(name);
                }
                if (equals) {
                    if (less) {
                        builder.append(" <= ");
                    } else if (greater) {
                        builder.append(" >= ");
                    } else {
                        builder.append(" = ");
                    }
                } else {
                    if (less) {
                        builder.append(" < ");
                    } else if (greater) {
                        builder.append(" > ");
                    } else {
                        builder.append(" != ");
                    }
                }
                builder.append(value);
                builder.append(" ");
            }
        }
    }

    private void parseOrderBy(QuerySelect query, StringBuilder builder) {
        List<OrderBy> orderByList = query.getOrderByList();
        if (orderByList != null && orderByList.size() > 0) {
            builder.append(" ORDER BY ");
            for (int i = 0; i < orderByList.size(); i++) {
                if (i != 0) {
                    builder.append(" , ");
                } else {
                    builder.append(" ");
                }
                OrderBy orderBy = orderByList.get(i);
                String name = orderBy.getName();
                String describe = orderBy.getDescribe();
                builder.append(name);
                builder.append(" ");
                builder.append(describe);
            }
        }
    }

    private void parseGroupBy(QuerySelect query, StringBuilder builder) {
        GroupBy groupBy = query.getGroupBy();
        // only select query can have groupby and orderby list, otherwise they came null
        if (groupBy != null) {
            builder.append(" GROUP BY ");
            for (int i = 0; i < groupBy.getColumns().size(); i++) {
                if (i != 0) {
                    builder.append(" , ");
                } else {
                    builder.append(" ");
                }
                SelectColumn column = groupBy.getColumns().get(i);
                String s = column.getName();
                boolean avg = column.isAvg();
                boolean count = column.isCount();
                boolean sum = column.isSum();

                if (avg) {
                    builder.append(" AVG(");
                    builder.append(s);
                    builder.append(") ");
                } else if (count) {
                    builder.append(" COUNT(");
                    builder.append(s);
                    builder.append(") ");
                } else if (sum) {
                    builder.append(" SUM(");
                    builder.append(s);
                    builder.append(") ");
                } else {
                    builder.append(s);
                }

            }
        }
    }
}
