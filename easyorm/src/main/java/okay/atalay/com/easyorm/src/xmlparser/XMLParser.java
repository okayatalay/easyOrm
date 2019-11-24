package okay.atalay.com.easyorm.src.xmlparser;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import okay.atalay.com.easyorm.src.Table.Table;
import okay.atalay.com.easyorm.src.column.query.Having;
import okay.atalay.com.easyorm.src.column.query.QueryColumn;
import okay.atalay.com.easyorm.src.column.query.SelectColumn;
import okay.atalay.com.easyorm.src.column.table.TableColumn;
import okay.atalay.com.easyorm.src.columnAttribute.ColumnAttribute;
import okay.atalay.com.easyorm.src.constant.Constants;
import okay.atalay.com.easyorm.src.exception.ColumnNotFoundException;
import okay.atalay.com.easyorm.src.exception.EasyORMNotFoundException;
import okay.atalay.com.easyorm.src.exception.EasyORMVersionNotFoundException;
import okay.atalay.com.easyorm.src.exception.InitializeTagException;
import okay.atalay.com.easyorm.src.exception.InvalidColumnException;
import okay.atalay.com.easyorm.src.exception.SqlNotFoundException;
import okay.atalay.com.easyorm.src.exception.TableNotFoundException;
import okay.atalay.com.easyorm.src.exception.UpgradesTagException;
import okay.atalay.com.easyorm.src.initialize.Initializer;
import okay.atalay.com.easyorm.src.initialize.insertion.InitInsertion;
import okay.atalay.com.easyorm.src.initialize.rawQuery.InitRawQuery;
import okay.atalay.com.easyorm.src.query.QueryDelete;
import okay.atalay.com.easyorm.src.query.QueryInsert;
import okay.atalay.com.easyorm.src.query.QueryRawQuery;
import okay.atalay.com.easyorm.src.query.QuerySelect;
import okay.atalay.com.easyorm.src.query.QueryUpdate;
import okay.atalay.com.easyorm.src.query.clauses.GroupBy;
import okay.atalay.com.easyorm.src.query.clauses.OrderBy;
import okay.atalay.com.easyorm.src.query.clauses.TableClause;
import okay.atalay.com.easyorm.src.query.clauses.Where;
import okay.atalay.com.easyorm.src.query.queryAbstraction.BaseQueryIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.ColumnSelectIF;
import okay.atalay.com.easyorm.src.query.queryAbstraction.Query;
import okay.atalay.com.easyorm.src.query.queryAbstraction.WhereImpIF;
import okay.atalay.com.easyorm.src.query.subQuery.Select;
import okay.atalay.com.easyorm.src.upgrade.UpgradeQuery;
import okay.atalay.com.easyorm.src.upgrade.rawQuery.RawQuery;
import okay.atalay.com.easyorm.src.upgrade.table.UpgradeTable;

import static okay.atalay.com.easyorm.src.constant.Constants.QUERY;
import static okay.atalay.com.easyorm.src.constant.Constants.RAW_QUERY;


/**
 * Created by 1 on 9.03.2018.
 */
public class XMLParser {

    private List<Table> tableList;
    private List<BaseQueryIF> queries;
    private List<UpgradeQuery> upgradeList;
    private List<UpgradeTable> upgradeTableList;
    private List<QueryRawQuery> rawQueryList;

    public XMLParser(List<Table> tableList, List<BaseQueryIF> queries, List<QueryRawQuery> rawQueryList, List<UpgradeQuery> upgrades, List<UpgradeTable> upgradeTableList) {
        super();
        this.tableList = tableList;
        this.queries = queries;
        this.upgradeList = upgrades;
        this.upgradeTableList = upgradeTableList;
        this.rawQueryList = rawQueryList;
    }

    private final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    public void parseInitialize(InputStream path, Initializer initializer) throws ParserConfigurationException, IOException, SAXException, InitializeTagException, TableNotFoundException, InvalidColumnException {
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(path);
        doc.getDocumentElement().normalize();
        NodeList initialize = doc.getElementsByTagName(Constants.INITIALIZE);
        if (initialize != null && initialize.getLength() != 1) {
            throwException(new InitializeTagException("cannot be more than 1 initialize tag"));
        }
        if (initialize == null) {
            return;
        }
        NodeList rawQueries = ((Element) initialize.item(0)).getElementsByTagName(RAW_QUERY);
        if (rawQueries != null) {
            for (int i = 0; i < rawQueries.getLength(); i++) {
                Node node = rawQueries.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element ee = (Element) node;
                    String rawQuery = ee.getAttribute(Constants.ATTRIBUTE_QUERY);
                    InitRawQuery rq = new InitRawQuery();
                    rq.setRawQuery(rawQuery);
                    initializer.getInitRawQueryList().add(rq);
                }
            }
        }
        NodeList insertQueries = ((Element) initialize.item(0)).getElementsByTagName(Constants.INSERT);
        if (insertQueries != null) {
            for (int i = 0; i < insertQueries.getLength(); i++) {
                Node node = insertQueries.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element ee = (Element) node;
                    String into = ee.getAttribute(Constants.ATTRIBUTE_INTO);
                    String insertionName = ee.getAttribute(Constants.ATTRIBUTE_NAME);
                    NodeList elementsColumns = ee.getElementsByTagName(Constants.COLUMN);
                    if (elementsColumns == null || elementsColumns.getLength() == 0) {
                        throwException(new InitializeTagException("Column size which is in insert, must be greater than 0 (" + insertionName + ")"));
                    }
                    if (tableIsExist(into) == null) {
                        throwException(new TableNotFoundException("Table is not found to execute this insertion query.Please fill in to into tag using valid tableName. given into is " + into));
                    }
                    InitInsertion initInsertion = new InitInsertion();
                    for (int k = 0; k < elementsColumns.getLength(); k++) {
                        Node nodeIns = elementsColumns.item(k);
                        if (nodeIns.getNodeType() == Node.ELEMENT_NODE) {
                            Element instE = (Element) nodeIns;
                            String name = instE.getAttribute(Constants.ATTRIBUTE_NAME);
                            String value = instE.getAttribute(Constants.ATTRIBUTE_VALUE);
                            if ("".equals(name) || "".equals(value)) {
                                throwException(new InvalidColumnException("Invalid column name or value " + name + "," + value));
                            }
                            ColumnAttribute columnAttributeName = new ColumnAttribute(Constants.ATTRIBUTE_NAME, name);
                            ColumnAttribute columnAttributeValue = new ColumnAttribute(Constants.ATTRIBUTE_VALUE, value);
                            QueryColumn queryColumn = new QueryColumn();
                            queryColumn.addAttribute(columnAttributeName);
                            queryColumn.addAttribute(columnAttributeValue);
                            initInsertion.addColumn(queryColumn);
                        }
                    }
                    initInsertion.setInto(into);
                    initInsertion.generateSql();
                    initializer.getInitInsertionList().add(initInsertion);
                }
            }
        }
    }

    public void parseUpgrade(InputStream path) throws ParserConfigurationException, IOException, SAXException, UpgradesTagException, EasyORMVersionNotFoundException, ColumnNotFoundException, TableNotFoundException, InvalidColumnException, InstantiationException, IllegalAccessException {
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(path);
        doc.getDocumentElement().normalize();
        NodeList upgrades = doc.getElementsByTagName(Constants.UPGRADES);
        if (upgrades != null && upgrades.getLength() != 1) {
            throwException(new UpgradesTagException("cannot be more than 1 upgrades tag"));
        }
        if (upgrades == null) {
            return;
        }
        NodeList nodes = ((Element) upgrades.item(0)).getElementsByTagName(Constants.UPGRADE);
        for (int temp = 0; temp < nodes.getLength(); temp++) {
            Node nNode = nodes.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String version = eElement.getAttribute(Constants.ATTRIBUTE_VERSION);
                NodeList upgradeNodes = eElement.getElementsByTagName(RAW_QUERY);
                if (upgradeNodes != null) {
                    for (int i = 0; i < upgradeNodes.getLength(); i++) {
                        Node node = upgradeNodes.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element ee = (Element) node;
                            String rawQuery = ee.getAttribute(Constants.ATTRIBUTE_QUERY);
                            RawQuery rq = new RawQuery();
                            try {
                                rq.setVersion(Integer.valueOf(version));
                            } catch (Exception e) {
                                throwException(new EasyORMVersionNotFoundException("cannot be empty easyorm version tag for " + rawQuery));
                            }
                            rq.setRawQuery(rawQuery);
                            upgradeList.add(rq);
                        }
                    }
                }
                int ver = 0;
                try {
                    ver = Integer.valueOf(version);
                } catch (Exception e) {
                    throwException(new EasyORMVersionNotFoundException("cannot be empty upgrade version tag "));
                }
                NodeList tableNodes = eElement.getElementsByTagName(Constants.TABLE);
                List<Table> tables = findTables(tableNodes, UpgradeTable.class);
                for (Table table : tables) {
                    ((UpgradeTable) table).setVersion(ver);
                    upgradeTableList.add((UpgradeTable) table);
                }
            }
        }
    }

    public int parse(InputStream path) throws ParserConfigurationException, IOException, SAXException,
            TableNotFoundException, ColumnNotFoundException, InvalidColumnException, EasyORMNotFoundException, EasyORMVersionNotFoundException, InstantiationException, IllegalAccessException {
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(path);
        doc.getDocumentElement().normalize();
        NodeList easyOrmList = doc.getElementsByTagName(Constants.EASYORM);
        if (easyOrmList == null || easyOrmList.getLength() != 1) {
            throwException(new EasyORMNotFoundException("cannot be more than 1 easyOrm tag"));
        }
        Element easyOrmElement = (Element) easyOrmList.item(0);
        int version = 1;
        try {
            version = Integer.valueOf(easyOrmElement.getAttribute(Constants.ATTRIBUTE_VERSION));
        } catch (Exception e) {
            throwException(new EasyORMVersionNotFoundException("cannot be empty easyorm version tag"));
        }
        NodeList tablesNodes = doc.getElementsByTagName(Constants.TABLES);
        if (tablesNodes == null || tablesNodes.getLength() != 1) {
            throwException(new TableNotFoundException("cannot be more than 1 tableList tag"));
        }
        NodeList nodes = ((Element) tablesNodes.item(0)).getElementsByTagName(Constants.TABLE);
        findTables(nodes);
        return version;
    }

    private void findTables(NodeList nodes) throws InvalidColumnException, ColumnNotFoundException, TableNotFoundException, IllegalAccessException, InstantiationException {
        if (nodes == null) {
            throwException(new TableNotFoundException("there is no table in the xml file"));
        }
        for (int temp = 0; temp < nodes.getLength(); temp++) {
            Node nNode = nodes.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                String tableName = eElement.getAttribute(Constants.TABLE_NAME).trim();
                if (tableName.equals("")) {
                    throwException(new TableNotFoundException("Table name must not be empty"));
                }
                NodeList columnNodeList = eElement.getElementsByTagName(Constants.COLUMN);
                if (columnNodeList == null || columnNodeList.getLength() == 0) {
                    throwException(new ColumnNotFoundException("TableColumn Not Found for table " + tableName));
                }
                Table table = new Table();
                table.setName(tableName);
                for (int j = 0; j < columnNodeList.getLength(); j++) {
                    Node columNode = columnNodeList.item(j);
                    if (columNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element cElement = (Element) columNode;
                        ColumnAttribute columnName = new ColumnAttribute(Constants.ATTRIBUTE_NAME,
                                cElement.getAttribute(Constants.ATTRIBUTE_NAME).trim());
                        ColumnAttribute columnType = new ColumnAttribute(Constants.ATTRIBUTE_TYPE,
                                cElement.getAttribute(Constants.ATTRIBUTE_TYPE).trim());
                        ColumnAttribute columnAutoIncr = new ColumnAttribute(Constants.ATTRIBUTE_AUTOINCR,
                                cElement.getAttribute(Constants.ATTRIBUTE_AUTOINCR).trim());
                        ColumnAttribute columnPrimary = new ColumnAttribute(Constants.ATTRIBUTE_PRIMARY,
                                cElement.getAttribute(Constants.ATTRIBUTE_PRIMARY).trim());
                        ColumnAttribute columnSize = new ColumnAttribute(Constants.ATTRIBUTE_SIZE,
                                cElement.getAttribute(Constants.ATTRIBUTE_SIZE).trim());
                        ColumnAttribute columnReference = new ColumnAttribute(Constants.ATTRIBUTE_REFERENCE,
                                cElement.getAttribute(Constants.ATTRIBUTE_REFERENCE).trim());
                        ColumnAttribute columnNullable = new ColumnAttribute(Constants.ATTRIBUTE_NULLABLE,
                                cElement.getAttribute(Constants.ATTRIBUTE_NULLABLE).trim());
                        ColumnAttribute columnUnique = new ColumnAttribute(Constants.ATTRIBUTE_UNIQUE,
                                cElement.getAttribute(Constants.ATTRIBUTE_UNIQUE).trim());

                        checkColumns(table, columnName, columnType, columnAutoIncr, columnPrimary, columnSize,
                                columnReference, columnNullable, columnUnique);

                    } else {
                        throwException(new ColumnNotFoundException("TableColumn not found for the table " + tableName));
                    }
                }
                table.generateSql();
                tableList.add(table);
            }
        }
    }

    private List<Table> findTables(NodeList nodes, Class<? extends UpgradeTable> tableClass) throws InvalidColumnException, ColumnNotFoundException, TableNotFoundException, IllegalAccessException, InstantiationException {
        List<Table> tables = new ArrayList<>();
        if (nodes == null) {
            throwException(new TableNotFoundException("there is no table in the xml file"));
        }
        for (int temp = 0; temp < nodes.getLength(); temp++) {
            Node nNode = nodes.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                String tableName = eElement.getAttribute(Constants.TABLE_NAME).trim();
                if (tableName.equals("")) {
                    throwException(new TableNotFoundException("Table name must not be empty"));
                }
                NodeList columnNodeList = eElement.getElementsByTagName(Constants.COLUMN);
                if (columnNodeList == null || columnNodeList.getLength() == 0) {
                    throwException(new ColumnNotFoundException("TableColumn Not Found for table " + tableName));
                }
                Table table = tableClass.newInstance();
                table.setName(tableName);
                for (int j = 0; j < columnNodeList.getLength(); j++) {
                    Node columNode = columnNodeList.item(j);
                    if (columNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element cElement = (Element) columNode;
                        ColumnAttribute columnName = new ColumnAttribute(Constants.ATTRIBUTE_NAME,
                                cElement.getAttribute(Constants.ATTRIBUTE_NAME).trim());
                        ColumnAttribute columnType = new ColumnAttribute(Constants.ATTRIBUTE_TYPE,
                                cElement.getAttribute(Constants.ATTRIBUTE_TYPE).trim());
                        ColumnAttribute columnAutoIncr = new ColumnAttribute(Constants.ATTRIBUTE_AUTOINCR,
                                cElement.getAttribute(Constants.ATTRIBUTE_AUTOINCR).trim());
                        ColumnAttribute columnPrimary = new ColumnAttribute(Constants.ATTRIBUTE_PRIMARY,
                                cElement.getAttribute(Constants.ATTRIBUTE_PRIMARY).trim());
                        ColumnAttribute columnSize = new ColumnAttribute(Constants.ATTRIBUTE_SIZE,
                                cElement.getAttribute(Constants.ATTRIBUTE_SIZE).trim());
                        ColumnAttribute columnReference = new ColumnAttribute(Constants.ATTRIBUTE_REFERENCE,
                                cElement.getAttribute(Constants.ATTRIBUTE_REFERENCE).trim());
                        ColumnAttribute columnNullable = new ColumnAttribute(Constants.ATTRIBUTE_NULLABLE,
                                cElement.getAttribute(Constants.ATTRIBUTE_NULLABLE).trim());
                        ColumnAttribute columnUnique = new ColumnAttribute(Constants.ATTRIBUTE_UNIQUE,
                                cElement.getAttribute(Constants.ATTRIBUTE_UNIQUE).trim());

                        checkColumns(tables, table, columnName, columnType, columnAutoIncr, columnPrimary, columnSize,
                                columnReference, columnNullable, columnUnique);

                    } else {
                        throwException(new ColumnNotFoundException("TableColumn not found for the table " + tableName));
                    }
                }
                table.generateSql();
                tables.add(table);
            }
        }
        return tables;
    }

    private void checkColumns(List<Table> tables, Table table, ColumnAttribute columnName, ColumnAttribute columnType, ColumnAttribute columnAutoIncr, ColumnAttribute columnPrimary, ColumnAttribute columnSize, ColumnAttribute columnReference, ColumnAttribute columnNullable, ColumnAttribute columnUnique) throws InvalidColumnException {
        if (columnName.getValue().equals(""))
            throwException(new InvalidColumnException("invalid TableColumn Name for table:" + table));
        if (columnSize.getValue().equals("")) {
            columnSize.setValue("50");
        }
        if (Integer.valueOf(columnSize.getValue()) <= 0) {
            throwException(new InvalidColumnException(
                    "invalid TableColumn size for table:" + table + " tableColumn:" + columnName.getValue()));
        }
        if (columnType.getValue().equals("")) {
            throwException(new InvalidColumnException(
                    "tableColumn type must be entered tableColumn:" + columnName.getValue() + " Table:" + table.getName()));
        }
        if (columnType.getValue().toLowerCase().equals("varchar") && columnSize.getValue().equals("")) {
            throwException(new InvalidColumnException(
                    "tableColumn size must be entered tableColumn:" + columnName.getValue() + " Table:" + table.getName()));
        }
        if (!columnReference.getValue().equals("")) {
            String[] ref = columnReference.getValue().split(":");
            if (ref.length != 2) {
                throwException(new InvalidColumnException("TableColumn reference must be defined as tableName:columnName. " + columnReference.getValue()));
            }
            Table t = tableIsExist(ref[0]);
            if (t == null && tables != null) {
                t = tableIsExist(tables, ref[0]);
            }
            if (t == null) {
                throwException(new InvalidColumnException("TableColumn reference table is invalid." + ref[0]));
            }
            if (!columnIsExist(t, ref[1])) {
                throwException(new InvalidColumnException("TableColumn reference table's tableColumn is invalid." + ref[1]));
            }
        }
        TableColumn tableColumn = new TableColumn();
        tableColumn.addAttribute(Constants.ATTRIBUTE_NAME, columnName);
        tableColumn.addAttribute(Constants.ATTRIBUTE_AUTOINCR, columnAutoIncr);
        tableColumn.addAttribute(Constants.ATTRIBUTE_PRIMARY, columnPrimary);
        tableColumn.addAttribute(Constants.ATTRIBUTE_REFERENCE, columnReference);
        tableColumn.addAttribute(Constants.ATTRIBUTE_SIZE, columnSize);
        tableColumn.addAttribute(Constants.ATTRIBUTE_TYPE, columnType);
        tableColumn.addAttribute(Constants.ATTRIBUTE_NULLABLE, columnNullable);
        tableColumn.addAttribute(Constants.ATTRIBUTE_UNIQUE, columnUnique);
        table.addColumn(tableColumn);
    }

    private Table tableIsExist(List<Table> tables, String s) {
        for (Table table : tables) {
            if (table.getName().equals(s)) return table;
        }
        return null;
    }


    private <T extends Exception> void throwException(T e) throws T {
        tableList.clear();
        queries.clear();
        throw (T) e;
    }

    private Table tableIsExist(String tableName) {
        for (Table table : tableList) {
            if (table.getName().equals(tableName)) return table;
        }
        return null;
    }

    private boolean columnIsExist(Table table, String column) {
        for (String colm : table.getColumnNames()) {
            if (colm.equals(column)) return true;
        }
        return false;
    }

    private void checkColumns(Table table, ColumnAttribute columnName, ColumnAttribute columnType,
                              ColumnAttribute columnAutoIncr, ColumnAttribute columnPrimary, ColumnAttribute columnSize,
                              ColumnAttribute columnReference, ColumnAttribute columnNullable, ColumnAttribute columnUnique)
            throws InvalidColumnException {
        checkColumns(null, table, columnName, columnType, columnAutoIncr, columnPrimary, columnSize,
                columnReference, columnNullable, columnUnique);

    }

    public void parseSql(InputStream path) throws ParserConfigurationException, IOException, SAXException, SqlNotFoundException {
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(path);
        doc.getDocumentElement().normalize();
        NodeList tablesNodes = doc.getElementsByTagName(Constants.QUERIES);
        if (tablesNodes == null || tablesNodes.getLength() != 1) {
            return;
        }
        NodeList nodes = ((Element) tablesNodes.item(0)).getElementsByTagName(QUERY);
        if (nodes == null || nodes.getLength() == 0) {
            return;
        }
        parseQuery(nodes);
        nodes = ((Element) tablesNodes.item(0)).getElementsByTagName(RAW_QUERY);
        if (nodes == null || nodes.getLength() == 0) {
            return;
        }
        parseQueryRawQuery(nodes);
    }

    private void parseQueryRawQuery(NodeList nodes) throws SqlNotFoundException {
        for (int temp = 0; temp < nodes.getLength(); temp++) {
            Node nNode = nodes.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String queryName = eElement.getAttribute(Constants.SQL_NAME).trim();
                if (queryName.equals("")) {
                    throwException(new SqlNotFoundException("RawQuery name must not be empty for " + queryName));
                }
                String query = eElement.getAttribute(Constants.ATTRIBUTE_QUERY).trim();
                if (query.equals("")) {
                    throwException(new SqlNotFoundException("RawQuery query must not be empty for " + queryName));
                }
                rawQueryList.add(new QueryRawQuery(queryName, query));
            } else {
                throwException(new SqlNotFoundException("invalid sql query in the xml file"));
            }
        }
    }

    private void parseQuery(NodeList nodes) throws SqlNotFoundException {
        for (int temp = 0; temp < nodes.getLength(); temp++) {
            Node nNode = nodes.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                String sqlColumns = eElement.getAttribute(Constants.SQL_COLUMNS).trim();

                String queryName = eElement.getAttribute(Constants.SQL_NAME).trim();
                String dataSync = eElement.getAttribute(Constants.DATA_SYNC).trim();
                if (queryName.equals("")) {
                    throwException(new SqlNotFoundException("Query name must not be empty for " + queryName));
                }
                String queryType = eElement.getAttribute(Constants.SQL_TYPE).trim();
                if (queryType.equals("")) {
                    throwException(new SqlNotFoundException("Query type must not be empty for " + queryName));
                }
                NodeList tableList = eElement.getElementsByTagName(Constants.SQL_TABLE);
                if (tableList.getLength() == 0) {
                    throwException(new SqlNotFoundException("there is no given table to proceed " + queryName));
                }
                if (getQuery(queryName)) {
                    throwException(new SqlNotFoundException("duplice query name " + queryName));
                }
                Query query = null;
                int tableTagCountInSelect = getTableCountInSelect(eElement);
                if (tableList.getLength() - tableTagCountInSelect <= 0) {
                    throwException(new SqlNotFoundException("there is no given table to proceed"));
                }
                if (queryType.equals(Constants.SELECT)) {
                    QuerySelect querySelect = new QuerySelect();
                    querySelect.setName(queryName);
                    String distinct = eElement.getAttribute(Constants.ATTRIBUTE_DISTINCT);
                    querySelect.setDistinct(distinct);
                    addColumnsFromString(querySelect, sqlColumns);
                    NodeList columnNode = eElement.getElementsByTagName(Constants.COLUMN);
                    List<SelectColumn> selectColumns = parseSelectColumn(columnNode);
                    if (selectColumns.size() > 0) {
                        for (SelectColumn ss : selectColumns) {
                            querySelect.addColumn(ss);
                        }
                    }
                    for (int j = 0; j < tableList.getLength() - tableTagCountInSelect; j++) {
                        Node tableNode = tableList.item(j);
                        if (tableNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element cElement = (Element) tableNode;
                            String name = cElement.getAttribute(Constants.ATTRIBUTE_NAME).trim();
                            if (tableIsExist(name) == null) {
                                throwException(new SqlNotFoundException("invalid Table name for the following query " + queryName));
                            }
                            TableClause tableClause = new TableClause(name);
                            querySelect.addTable(tableClause);
                        } else {
                            throwException(new SqlNotFoundException("invalid table query in the xml file for " + queryName));
                        }
                    }
                    NodeList groupByNode = eElement.getElementsByTagName(Constants.GROUPBYS);
                    NodeList orderByNode = eElement.getElementsByTagName(Constants.SQL_ORDERBY);
                    NodeList whereList = eElement.getElementsByTagName(Constants.SQL_WHERE);
                    NodeList havings = eElement.getElementsByTagName(Constants.HAVINGS);
                    parseHavingCondition(querySelect, havings);
                    parseOrderBy(querySelect, orderByNode);
                    parseGroupBy(querySelect, groupByNode);
                    if (querySelect.getGroupBy() == null && querySelect.getHavings().size() > 0) {
                        throwException(new SqlNotFoundException("Having must be peresent with groupby for query: " + queryName));
                    }
                    int whereCountInSelect = getWhereCountInSelect(eElement);
                    parseWhere(whereList, whereCountInSelect, querySelect);
                    querySelect.setDataSync(dataSync.equals("true"));
                    query = querySelect;
                } else if (queryType.equals(Constants.INSERT)) {
                    QueryInsert queryInsert = new QueryInsert();
                    Node tableNode = tableList.item(0);
                    if (tableNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element cElement = (Element) tableNode;
                        String name = cElement.getAttribute(Constants.ATTRIBUTE_NAME).trim();
                        if (tableIsExist(name) == null) {
                            throwException(new SqlNotFoundException("invalid Table name for the following query " + queryName));
                        }
                        TableClause tableClause = new TableClause(name);
                        queryInsert.addTable(tableClause);
                    }
                    NodeList columnList = eElement.getElementsByTagName(Constants.COLUMN);
                    parseInsert(queryInsert, columnList);
                    queryInsert.setDataSync(dataSync.equals("true"));
                    query = queryInsert;
                } else if (queryType.equals(Constants.UPDATE)) {
                    QueryUpdate queryUpdate = new QueryUpdate();
                    Node tableNode = tableList.item(0);
                    if (tableNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element cElement = (Element) tableNode;
                        String name = cElement.getAttribute(Constants.ATTRIBUTE_NAME).trim();
                        if (tableIsExist(name) == null) {
                            throwException(new SqlNotFoundException("invalid Table name for the following query " + queryName));
                        }
                        TableClause tableClause = new TableClause(name);
                        queryUpdate.addTable(tableClause);
                    }
                    NodeList columnList = eElement.getElementsByTagName(Constants.COLUMN);
                    parseUpdate(queryUpdate, columnList);
                    NodeList whereList = eElement.getElementsByTagName(Constants.SQL_WHERE);
                    int whereCountInSelect = getWhereCountInSelect(eElement);
                    parseWhere(whereList, whereCountInSelect, queryUpdate);
                    queryUpdate.setDataSync(dataSync.equals("true"));
                    query = queryUpdate;
                } else if (queryType.equals(Constants.DELETE)) {
                    QueryDelete queryDelete = new QueryDelete();
                    Node tableNode = tableList.item(0);
                    if (tableNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element cElement = (Element) tableNode;
                        String name = cElement.getAttribute(Constants.ATTRIBUTE_NAME).trim();
                        if (tableIsExist(name) == null) {
                            throwException(new SqlNotFoundException("invalid Table name for the following query " + queryName));
                        }
                        TableClause tableClause = new TableClause(name);
                        queryDelete.addTable(tableClause);
                    }
                    NodeList whereList = eElement.getElementsByTagName(Constants.SQL_WHERE);
                    int whereCountInSelect = getWhereCountInSelect(eElement);
                    parseWhere(whereList, whereCountInSelect, queryDelete);
                    queryDelete.setDataSync(dataSync.equals("true"));
                    query = queryDelete;
                } else {
                    throwException(new SqlNotFoundException("invalid query type for " + queryName));
                }
                query.setName(queryName);
                query.setType(queryType);
                query.parseSql();
                queries.add(query);
            } else {
                throwException(new SqlNotFoundException("invalid sql query in the xml file"));
            }
        }
    }

    private List<SelectColumn> parseSelectColumn(NodeList columnNodes) {
        List<SelectColumn> columnList = new ArrayList<>();
        for (int j = 0; j < columnNodes.getLength(); j++) {
            Node columnNode = columnNodes.item(j);
            if (columnNode.getNodeType() == Node.ELEMENT_NODE) {
                Element cElement = (Element) columnNode;
                String name = cElement.getAttribute(Constants.ATTRIBUTE_NAME).trim();
                String alias = cElement.getAttribute(Constants.ATTRIBUTE_ALIAS).trim();
                String sum = cElement.getAttribute(Constants.ATTRIBUTE_SUM).trim();
                String avg = cElement.getAttribute(Constants.ATTRIBUTE_AVG).trim();
                String count = cElement.getAttribute(Constants.ATTRIBUTE_COUNT).trim();
                SelectColumn column = new SelectColumn(name);
                column.setAlias(alias.equals("") ? null : alias);
                column.setAvg(avg.equals("true"));
                column.setSum(sum.equals("true"));
                column.setCount(count.equals("true"));
                columnList.add(column);
            }
        }
        return columnList;
    }

    private void parseHavingCondition(QuerySelect querySelect, NodeList havingsNodeList) throws SqlNotFoundException {
        if (havingsNodeList == null || havingsNodeList.getLength() == 0) return;
        if (havingsNodeList.getLength() > 1) {
            throwException(new SqlNotFoundException("invalid havings node. it must not be greater than 1 node" + querySelect.getName()));
        }
        Node nodeHavings = havingsNodeList.item(0);
        if (nodeHavings.getNodeType() == Node.ELEMENT_NODE) {
            Element gElement = (Element) nodeHavings;
            NodeList nodeListHaving = gElement.getElementsByTagName(Constants.SQL_HAVING);
            for (int j = 0; j < nodeListHaving.getLength(); j++) {
                Node columnNode = nodeListHaving.item(j);
                if (columnNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element cElement = (Element) columnNode;
                    String name = cElement.getAttribute(Constants.ATTRIBUTE_NAME).trim();
                    String value = cElement.getAttribute(Constants.ATTRIBUTE_VALUE).trim();
                    String less = cElement.getAttribute(Constants.ATTRIBUTE_LESS).trim();
                    String equals = cElement.getAttribute(Constants.ATTRIBUTE_EQUALS).trim();
                    String great = cElement.getAttribute(Constants.ATTRIBUTE_GREAT).trim();
                    String sum = cElement.getAttribute(Constants.ATTRIBUTE_SUM).trim();
                    String avg = cElement.getAttribute(Constants.ATTRIBUTE_AVG).trim();
                    String count = cElement.getAttribute(Constants.ATTRIBUTE_COUNT).trim();
                    String process = cElement.getAttribute(Constants.ATTRIBUTE_PROCESS.trim());
                    String type = cElement.getAttribute(Constants.ATTRIBUTE_TYPE.trim());
                    Having having = new Having(name);
                    having.setType(type);
                    having.setValue(value);
                    having.setAvg(avg.equals("true"));
                    having.setSum(sum.equals("true"));
                    having.setCount(count.equals("true"));
                    having.setLess(less.equals("true"));
                    having.setEquals(!equals.equals("no"));
                    having.setGreater(great.equals("true"));
                    having.setProcess(process);
                    querySelect.addHaving(having);
                }
            }
        }
    }

    private void addColumnsFromString(ColumnSelectIF query, String columns) {
        String[] split = columns.split(",");
        for (String s : split) {
            SelectColumn colum = new SelectColumn(s);
            query.addColumn(colum);
        }
    }

    public void parseOrderBy(QuerySelect query, NodeList orderByNodes) throws SqlNotFoundException {
        for (int j = 0; j < orderByNodes.getLength(); j++) {
            Node orderByNode = orderByNodes.item(j);
            if (orderByNode.getNodeType() == Node.ELEMENT_NODE) {
                Element cElement = (Element) orderByNode;
                String name = cElement.getAttribute(Constants.ATTRIBUTE_NAME).trim();
                String describe = cElement.getAttribute(Constants.ATTRIBUTE_DESCRIBE).trim();
                if ("".equals(name)) {
                    throwException(new SqlNotFoundException("invalid orderby name in the xml file for " + query.getName()));
                }
                OrderBy orderBy = new OrderBy(name, describe);
                query.addOrderBy(orderBy);
            } else {
                throwException(new SqlNotFoundException("invalid table query in the xml file for " + query.getName()));
            }
        }
    }

    public void parseGroupBy(QuerySelect query, NodeList groupByNodes) throws SqlNotFoundException {
        if (groupByNodes == null || groupByNodes.getLength() == 0) return;
        if (groupByNodes.getLength() > 1) {
            throwException(new SqlNotFoundException("invalid groupby node. it must not be greater than 1 node" + query.getName()));
        }
        Node groupBy = groupByNodes.item(0);
        if (groupBy.getNodeType() == Node.ELEMENT_NODE) {
            Element gElement = (Element) groupBy;
            NodeList columnNode = gElement.getElementsByTagName(Constants.SQL_GROUPBY);
            List<SelectColumn> selectColumns = parseSelectColumn(columnNode);
            if (selectColumns.size() > 0) {
                GroupBy gb = new GroupBy();
                query.setGroupBy(gb);
                for (SelectColumn ss : selectColumns) {
                    gb.addColumn(ss);
                }
            }
        }
    }

    private boolean getQuery(String queryName) {
        for (BaseQueryIF q : queries) {
            if (q.getName().equals(queryName)) {
                return true;
            }
        }
        return false;
    }

    private void parseUpdate(QueryUpdate query, NodeList columnList) throws SqlNotFoundException {
        for (int temp = 0; temp < columnList.getLength(); temp++) {
            Node nNode = columnList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String name = eElement.getAttribute(Constants.ATTRIBUTE_NAME).trim();
                String value = eElement.getAttribute(Constants.ATTRIBUTE_VALUE);
                if (name.equals("") || value.equals("")) {
                    throwException(new SqlNotFoundException("invalid tableColumn name for query " + query.getName()));
                }
                String type = eElement.getAttribute(Constants.ATTRIBUTE_TYPE);
                String uuid = eElement.getAttribute(Constants.ATTRIBUTE_UUID);
                ColumnAttribute attributeName = new ColumnAttribute(Constants.ATTRIBUTE_NAME, name);
                ColumnAttribute attributeValue = new ColumnAttribute(Constants.ATTRIBUTE_VALUE, value);
                ColumnAttribute attributeType = new ColumnAttribute(Constants.ATTRIBUTE_TYPE, type);
                ColumnAttribute attributeUUID = new ColumnAttribute(Constants.ATTRIBUTE_UUID, uuid);
                QueryColumn queryColumn = new QueryColumn();
                queryColumn.addAttribute(attributeName);
                queryColumn.addAttribute(attributeValue);
                queryColumn.addAttribute(attributeType);
                queryColumn.addAttribute(attributeUUID);
                query.addColumn(queryColumn);

            }
        }
    }

    private void parseInsert(QueryInsert query, NodeList columnList) throws SqlNotFoundException {
        for (int temp = 0; temp < columnList.getLength(); temp++) {
            Node nNode = columnList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String name = eElement.getAttribute(Constants.ATTRIBUTE_NAME).trim();
                String value = eElement.getAttribute(Constants.ATTRIBUTE_VALUE);
                if (name.equals("") || value.equals("")) {
                    throwException(new SqlNotFoundException("invalid tableColumn name for query " + query.getName()));
                }
                String type = eElement.getAttribute(Constants.ATTRIBUTE_TYPE);
                String uuid = eElement.getAttribute(Constants.ATTRIBUTE_UUID);
                ColumnAttribute attributeName = new ColumnAttribute(Constants.ATTRIBUTE_NAME, name);
                ColumnAttribute attributeValue = new ColumnAttribute(Constants.ATTRIBUTE_VALUE, value);
                ColumnAttribute attributeType = new ColumnAttribute(Constants.ATTRIBUTE_TYPE, type);
                ColumnAttribute attributeUUID = new ColumnAttribute(Constants.ATTRIBUTE_UUID, uuid);
                QueryColumn queryColumn = new QueryColumn();
                queryColumn.addAttribute(attributeName);
                queryColumn.addAttribute(attributeValue);
                queryColumn.addAttribute(attributeType);
                queryColumn.addAttribute(attributeUUID);
                query.addColumn(queryColumn);
                NodeList selectNodeList = eElement.getElementsByTagName(Constants.ATTRIBUTE_SELECT);
                Select select = parseSubQuerySelect(selectNodeList);
                queryColumn.setSelect(select);
            } else {

            }
        }
    }

    private int getTableCountInSelect(Element eElement) {
        NodeList selectNodes = eElement.getElementsByTagName(Constants.ATTRIBUTE_SELECT);
        if (selectNodes == null || selectNodes.getLength() == 0) {
            return 0;
        }
        NodeList elementsByTagName = ((Element) selectNodes.item(0)).getElementsByTagName(Constants.TABLE);
        return elementsByTagName.getLength();
    }

    private int getWhereCountInSelect(Element eElement) {
        NodeList selectNodes = eElement.getElementsByTagName(Constants.ATTRIBUTE_SELECT);
        if (selectNodes == null || selectNodes.getLength() == 0) {
            return 0;
        }
        NodeList elementsByTagName = ((Element) selectNodes.item(0)).getElementsByTagName(Constants.SQL_WHERE);
        return elementsByTagName.getLength();
    }

    private void parseWhere(NodeList whereList, int whereCountInSelect, WhereImpIF query) throws SqlNotFoundException {
        for (int j = 0; j < whereList.getLength() - whereCountInSelect; j++) {
            Node whereNode = whereList.item(j);
            if (whereNode.getNodeType() == Node.ELEMENT_NODE) {
                Element cElement = (Element) whereNode;
                String name = cElement.getAttribute(Constants.ATTRIBUTE_NAME).trim();
                String value = cElement.getAttribute(Constants.ATTRIBUTE_VALUE).trim();
                String process = cElement.getAttribute(Constants.ATTRIBUTE_PROCESS).trim();
                String like = cElement.getAttribute(Constants.ATTRIBUTE_LIKE).trim();
                String _static = cElement.getAttribute(Constants.ATTRIBUTE_STATIC).trim();
                String type = cElement.getAttribute(Constants.ATTRIBUTE_TYPE).trim();
                String equals = cElement.getAttribute(Constants.ATTRIBUTE_EQUALS).trim();
                String less = cElement.getAttribute(Constants.ATTRIBUTE_LESS).trim();
                String great = cElement.getAttribute(Constants.ATTRIBUTE_GREAT).trim();
                Where where = new Where(name, value, process, _static, type, like, equals);
                where.setLess(less.equals("true"));
                where.setGreater(great.equals("true"));
                query.addWhere(where);
                NodeList selectNodeList = cElement.getElementsByTagName(Constants.ATTRIBUTE_SELECT);
                if (selectNodeList != null && selectNodeList.getLength() >= 1) {
                    where.setSelect(parseSubQuerySelect(selectNodeList));
                }
            }
        }
    }

    private Select parseSubQuerySelect(NodeList nodes) throws SqlNotFoundException {
        Node nNode = nodes.item(0);
        if (nNode == null) return null;
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Select select = new Select();
            Element eElement = (Element) nNode;
            String selectName = eElement.getAttribute(Constants.ATTRIBUTE_NAME);
            String sqlColumns = eElement.getAttribute(Constants.SQL_COLUMNS).trim();
            select.setName(selectName);
            addColumnsFromString(select, sqlColumns);
            NodeList tableList = eElement.getElementsByTagName(Constants.SQL_TABLE);
            int tableCountInSubSelect = getTableCountInSelect(eElement);
            for (int j = 0; j < tableList.getLength() - tableCountInSubSelect; j++) {
                Node tableNode = tableList.item(j);
                if (tableNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element cElement = (Element) tableNode;
                    String name = cElement.getAttribute(Constants.ATTRIBUTE_NAME).trim();
                    TableClause tableClause = new TableClause(name);
                    select.addTable(tableClause);
                } else {
                    throwException(
                            new SqlNotFoundException("invalid select query in the xml file for " + select.getName()));
                }
            }
            NodeList whereList = eElement.getElementsByTagName(Constants.SQL_WHERE);
            int whereCountInSelect = getWhereCountInSelect(eElement);
            parseWhere(whereList, whereCountInSelect, select);
            return select;
        } else {
            throwException(new SqlNotFoundException("invalid sql query in the xml file"));
        }
        return null;
    }


}
