package okay.atalay.com.easyorm.src.Table;

import java.util.HashMap;
import java.util.Map;

import okay.atalay.com.easyorm.src.column.table.TableColumn;
import okay.atalay.com.easyorm.src.constant.Constants;
import okay.atalay.com.easyorm.src.easyOrm.factory.EasyOrmFactory;
import okay.atalay.com.easyorm.src.sql.Sql;
import okay.atalay.com.easyorm.src.sqlGenerator.SQLGenerator;


public class Table implements TableIF {
	private String name;
	private Map<String, TableColumn> columns = new HashMap<>();
	private Sql sql;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String tableName) {
		this.name = tableName;
	}

	public void addColumn(TableColumn tableColumn) {
		columns.put(tableColumn.getAttribute(Constants.ATTRIBUTE_NAME).getValue(), tableColumn);
	}

	public TableColumn getColumn(String columnName) {
		return columns.get(columnName);
	}

	public String[] getColumnNames() {
		return columns.keySet().toArray(new String[columns.keySet().size()]);
	}

	public Sql getSql() {
		return sql;
	}

	public void setSql(Sql sql) {
		this.sql = sql;
	}

	public void generateSql() {
		String query = SQLGenerator.getInstance().getCreateTable(this);
		if (EasyOrmFactory.verboseQueries) {
			System.out.println(query);
		}
		sql = new Sql();
		sql.setSql(query);

	}

}
