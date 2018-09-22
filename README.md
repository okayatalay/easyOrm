# EasyOrm
xml based Android ORM

This orm allows developer to create simple database using xml files.

Xml file based on 4 main tags. They are below<br>
<code>tables</code> We define all tables in this section<br>
<code>initialize</code> Same tables should be initialize when they are created. So, to make this action, developers use this section. This section only runs after tables are created.
<br><code>queries</code> Developers must define their all queries in this section. Every Query must have unique name.
<br><code>upgrades</code>Day after day, We need to upgrade the database, Main Section <code>easyORM</code> must have version attribute. When this
attribute is increased, this section operations will be performed under onUpgrade method which is provided by SqLite.
<br><strong> Let's look at each section more closer</strong><br>

# Usage

<i> following xml overviewl shows the general usage: </i> <br>

	<easyORM version="1">
		<tables>
			section1 
		</tables>
		<initialize>
			section2 
		</initialize>
		<queries>
			section3 
		</queries>
		<upgrades>
			section4 
		</upgrades>
	</easyORM>

  
# section1
<code> tables </code> tag should include the <code> table </code> tags. this section allows us to define our all tables.
<h5> usage example is </h5>
<br>

		<tables>
			<table name="informations">
				<column name="ID" type="integer"/>
				<column name="address" size="250" type="varchar"/>
				<column name="phone" nullable="false" size="15" type="varchar"/>
			</table>
			<table name="users">
				<column name="ID" autoIncrement="true" primary="true" type="integer"/>
				<column name="name" size="20" type="varchar" unique="true"/>
				<column name="lastName" size="20" type="varchar"/>
				<column name="number" type="integer"/>
				<column name="infoID" reference="informations:ID" type="integer"/>
			</table>
		</tables>
  
  Above defination creates 2 table which are informations and users.<br> <strong>information</strong> table has ID,address and phone columns
  <br><strong>users</strong> table has ID,name,lastName,number and infoID columns. ID is primary key. infoID is foreign key and it refers
  to information table' s ID'filed.
  
# section2
<code> initialize </code> section runs only when tables are created. If we initialize some fields/tables, it is good to use this part.
There is 2 type of usage. First one is <strong>rawquery</strong>. Second one is <strong> insert </strong>
<br> <code>rawQuery</code> allows us to define sql queries.
<br> <code>insert</code> allows us to define sql queries using xml schema.
 <br><h5> usage example is </h5>
<br>
	
		<initialize>
			<rawquery query="insert into informations(ID,address,phone) values(1,'First Address','00905343332211')"/>
			<insert into="users">
				<column name="name" value="lionell"/>
				<column name="lastName" value="messi"/>
				<column name="number" value="10"/>
				<column name="infoID" value="1"/>
			</insert>
		</initialize>
# section3
<code> queries </code> this section should have <strong> query </strong> tags. <code> query </code> tags allow us to define select/insert/update/delete
operation methods. <br> <code>query</code> must have type and name attribute. type attibute points the query type which can be select/insert/update/delete.
<br><h5> usage example is </h5>
<br>
	
	<query name="getInformation" type="select">
		<table name="informations"/>
		<where name="ID"/>
	</query>
	<query name="addUser" dataSync="true" type="insert">
		<table name="users"/>
		<column name="name" type="varchar" value="?"/>
		<column name="lastName" value="?"/>
		<column name="number" type="integer" value="?"/>
		<column name="infoID" type="integer" value="?"/>
	</query>
	<query name="deleteUserByID" dataSync="true" type="delete">
		<table name="users"/>
		<where name="ID" value="?"/>
	</query>
	<query name="upduteUserByID" dataSync="true" type="update">
		<table name="users"/>
		<column name="name" value="?"/>
		<column name="lastName" value="?"/>
		<column name="number" value="?"/>
		<where name="ID" value="?"/>
	</query>
	
	
	
<strong>inner query defination is usable. </strong> <br> <code>where</code> tag can have <strong>select</strong> tag. <br>For examle :<br>
	
	
	<query name="deleteUserByPhone" type="delete">
		<table name="users"/>
		<where name="infoID" value="?">
			<select columns="ID">
				<table name="informations"/>
				<where name="phone" value="?"/>
			</select>
		</where>
	</query>
	<query name="upduteUserByPhone" type="update">
		<table name="users"/>
		<column name="name" value="?"/>
		<column name="lastName" value="?"/>
		<where name="infoID" value="?">
			<select columns="ID">
				<table name="informations"/>
				<where name="phone" value="?"/>
			</select>
		</where>
	</query>
	
# section 4
<code> upgrades</code> section can have <code>upgrade</code> tags. all <strong>upgrade</strong> tags must have version attribute. Because, 
the upgrade tags only run its version is between db oldVersion and dbNewVersion.<br><code> Upgrade</code> tag can have <strong>table</strong> creation or 
<strong> rawQuery </strong><br> let's look at the usage: <br>

	<upgrade version="2">
		<table name="locations">
			<column name="ID" type="integer"/>
			<column name="latitude" size="250" type="float"/>
			<column name="longitude" size="250" type="float"/>
			<column name="userID" reference="users:ID" type="integer"/>
		</table>
		<rawquery query="insert into locations(ID,longitude,userID) values(1,55.55,66.66,123456)"/>
	</upgrade>

# Table Tag Attributes more Detail

We use the tag to create new table. <code>table</table tag must have a unique <strong> name </strong>.
<code>table</code> tag should have <code>column</code> defination and <strong>column</strong> name points the table filed name. So it must be unique.
<br> <h3> Column Tag Attributes</h3>
	<li><strong>name</strong> must be unique</li>
	<li><strong>type</strong> can be varchar, integer, date, boolean, float. Default values is varchar</li>
	<li><strong>primary</strong> can be true/false. It is used to define <strong>Primary Key</strong>. Default values is false</li>
	<li><strong>autoIncrement</strong> can be true/false. It is used to define <strong>Auto incr</strong> Field. Default values is false</li>
	<li><strong>unique</strong> can be true/false. It is used to define <strong>Unique</strong> Field. Default values is false</li>
	<li><strong>size</strong> should be integer value. It is used to assign size to field. Default values is 50</li>
	<li><strong>nullable</strong> can be true/false.. It is used to accept null value for the column(Field). Default values is false</li>
	<li><strong>reference</strong> can be true/false.. It is used to Define <strong>Foreign Key</strong>.Usage is <code>tableName:itsField</code>. Default values is false</li>
	
# Query Tag Attributes more Detail

A <code>query</code> must have a unique<strong>name</strong> attribute and <code>type</code> attribute. 
<br><code>type</code> should be select/insert/delete/update. <br><code>distinct</code> is optional default is false. usage is<strong> distinct="true"</strong>
<br>Also it should have <code>columns</code> attribute. This attribute is used for select queries. if it is left empty, means that <strong>*</strong>
if we select some fileds, <code>columns</code> should be used like <strong> select="name,lastname,number" </strong><br>
<br>
<code>query</code> tag can have <strong> table,column,where,orderBy,groupBys,havings</strong> tags.
<br><code>query</code> tags can have dataSync attribute. it it is set to true,a data sync event is distributed to all listeners after this query is executed
<h3> Table Tag Attributes</h3>
	<li><strong>name</strong> it is mandatory attribute. it must point the valid table name. More than 1 <code>table</code> tags can be used.All tables will be join </li>
<h3> Columns Tag Attributes</h3>
	<li><strong>name</strong> it is mandatory attribute. it must point the table field</li>
	<li><strong>sum</strong> it can be true/false. Aim is to calculate sum of filed's values. Default value is false. Valid for Select Queries</li>
	<li><strong>avg</strong> it can be true/false. Aim is to calculate avg of filed's values. Default value is false. Valid for Select Queries</li>
	<li><strong>count</strong> it can be true/false. Aim is to calculate total count of filed's values. Default value is false. Valid for Select Queries</li>
	<li><strong>alias</strong> it can be string. Aim is to assign a allias for the field. Valid for Select Queries</li>
	<li><strong>UUID</strong> it can be true/false. Aim is to generate unique number. Valid for Insert Queries</li>
	
To be continued...
<br>Mixed Example and it's sql view<br>
	
	
	<query name="getUser" columns="name,surname" type="select">
		<column name="ID" alias="SYUm" sum="true"/>
		<column name="ID" alias="total" avg="true"/>
		<table name="users"/>
		<where name="ID" less="true" value="?"/>
		<orderby name="name" describe="desc"/>
		<orderby name="surname"/>
		<groupbys>
			<groupby name="name"/>
			<groupby name="surname"/>
			<groupby name="ID" sum="true"/>
			<groupby name="ID" avg="true"/>
		</groupbys>
		<havings>
			<having name="ID" avg="true" equals="true" less="true" process="or" value="?"/>
			<having name="ID" equals="true" greater="true" process="or" sum="true" value="?"/>
		</havings>
	</query>
	
Above example' s sql code is : <br>

	getUser
		select  name,surname, SUM(ID)  as SYUm, AVG(ID)  as total 
		from users  
		Where ID <= ?   
		ORDER BY  name asc , surname  
		GROUP BY  name , surname ,  SUM(ID)  ,  AVG(ID) 
		HAVING  AVG(ID)  <= ?  or  SUM(ID)  >= ?
	
	
# Code Snippets

We need to create a simple java class to map defined table. Class' field name must be the same with defined table column names.<br>
All field must have getter/setter method. The class must have default constructor.
<br> Java class should be below for above table users


	public class User {
		private Integer ID;
		private String name, lastName;
		private Integer number, infoID;

		public Integer getID() {
			return ID;
		}

		public void setID(Integer ID) {
			this.ID = ID;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public Integer getNumber() {
			return number;
		}

		public void setNumber(Integer number) {
			this.number = number;
		}

		public Integer getInfoID() {
			return infoID;
		}

		public void setInfoID(Integer infoID) {
			this.infoID = infoID;
		}
	}

To create DataBase:


	EasyORM easyORM = EasyOrmFactory.getOrCreateDataBase(this, "dbName");
	try {
		EasyOrmFactory.verboseAllQueries(true);
		EasyOrmFactory.setExactMatch(false);
		easyORM.registerXMLSchema("db.xml");
	} catch (Exception e) {
		e.printStackTrace();
	}

To register DataSyncListener<br>
<code>
	EasyOrmFactory.registerDataSync(this);
</code>
<br>To register CreateTable and Upgrade table events<br>
<code>
	easyORM.registerCreateTableListener(this);
</code>

<br>To use insert, update , select, delete queries:

	easyORM.getEasyExecute().insert("queryName", dbObject);

Execute performer interface methods are<br>


	void delete(String queryName, Object[] params) throws QueryNotFoundException, QueryExecutionException;

	void deleteObject(String queryName, Object object) throws QueryNotFoundException, QueryExecutionException, FieldNotFoundException;

	boolean insert(String queryName, Object u) throws QueryNotFoundException, FieldNotFoundException, QueryExecutionException;

	<T> List<T> select(String queryName, final Class<T> input) throws QueryNotFoundException, QueryExecutionException;

	<T> List<T> select(String queryName, Object[] parameters, final Class<T> input) throws QueryNotFoundException, QueryExecutionException;

	void delete(String queryName) throws QueryNotFoundException, QueryExecutionException;

	void update(String queryName, Object[] params) throws QueryNotFoundException, QueryExecutionException;

	void updateObject(String queryName, Object object) throws QueryNotFoundException, QueryExecutionException, FieldNotFoundException;
	
	
# Installation
Download last version(.aar file) on release tab and copy the file to your libs folder then insert below line into gradle Module file
	
	dependencies {
		:
		:
		compile files('libs/easyorm.aar')
	}

# Contact
<h3>Feel Free to get in touch with me</h3>
okay.atalay38@hotmail.com