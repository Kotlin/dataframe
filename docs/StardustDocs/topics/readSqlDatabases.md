[//]: # (title: Read from SQL databases)

<web-summary>
Read data and infer schemas from SQL databases directly into Kotlin DataFrame, 
with support for PostgreSQL, MySQL, SQLite, and more.
</web-summary>

<card-summary>
Set up SQL database access and read query results efficiently into DataFrame.
</card-summary>

<link-summary>
Learn how to query, read, and inspect SQL database tables using Kotlin DataFrame
with full schema inference and flexible JDBC setup.
</link-summary>

These functions allow you to interact with an SQL database using a Kotlin DataFrame library.

There are two main blocks of available functionality:
* Methods for reading data from a database
  *  ```readSqlTable``` reads specific database table
  *  ```readSqlQuery``` executes SQL query
  *  ```readResultSet``` reads from created earlier ResultSet
  *  ```readAllSqlTables``` reads all tables (all non-system tables)
* Methods for reading table schemas
  * ```getSchemaForSqlTable``` for specific tables
  * ```getSchemaForSqlQuery``` for result of executing SQL queries
  * ```getSchemaForResultSet``` for created earlier `ResultSet`
  * ```getSchemaForAllSqlTables``` for all non-system tables

All methods above can be accessed like `DataFrame.getSchemaFor...()` via a companion for `DataFrame`.

Also, there are a few **extension functions** available on `Connection`,
`ResultSet`, and `DbConnectionConfig` objects.

* Methods for reading data from a database
    *  ```readDataFrame``` on `Connection` or `DbConnectionConfig` 
  converts the result of an SQL query or SQL table to a `DataFrame` object.
    *  ```readDataFrame``` on `ResultSet` reads from created earlier `ResultSet`
* Methods for reading table schemas from a database
    * ```getDataFrameSchema``` on `Connection` or `DbConnectionConfig`
  for an SQL query result or the SQL table
    * ```getDataFrameSchema``` on `ResultSet` for created earlier `ResultSet`


**NOTE:** This is an experimental module, and for now, 
we only support these databases: MS SQL, MariaDB, MySQL, PostgreSQL, SQLite, and DuckDB. 

Moreover, since release 0.15 we support the possibility to register custom SQL database, read more in our [guide](readSqlFromCustomDatabase.md).

Additionally, support for JSON and date-time types is limited. 
Please take this into consideration when using these functions.

## Getting started with reading from SQL database in a Gradle Project

First, you need to add a dependency

```kotlin
implementation("org.jetbrains.kotlinx:dataframe-jdbc:$dataframe_version")
```

after that, you need to add the dependency for the database's JDBC driver, for example

For **MariaDB**:

```kotlin
implementation("org.mariadb.jdbc:mariadb-java-client:$version")
```

The Maven Central version can be found [here](https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client).

For **PostgreSQL**:

```kotlin
implementation("org.postgresql:postgresql:$version")
```

The Maven Central version can be found [here](https://mvnrepository.com/artifact/org.postgresql/postgresql).

For **MySQL**:

```kotlin
implementation("com.mysql:mysql-connector-j:$version")
```

The Maven Central version can be found [here](https://mvnrepository.com/artifact/com.mysql/mysql-connector-j).

For **SQLite**:

```kotlin
implementation("org.xerial:sqlite-jdbc:$version")
```

The Maven Central version can be found [here](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc).

For **MS SQL**:

```kotlin
implementation("com.microsoft.sqlserver:mssql-jdbc:$version")
```

The Maven Central version can be found [here](https://mvnrepository.com/artifact/com.microsoft.sqlserver/mssql-jdbc).

For **DuckDB**:

```kotlin
implementation("org.duckdb:duckdb_jdbc:$version")
```

The Maven Central version can be found [here](https://mvnrepository.com/artifact/org.duckdb/duckdb_jdbc).

Next, be sure that you can establish a connection to the database.

For this, usually, you need to have three things: a URL to the database, a username, and a password.

Call one of the following functions to collect data from the database and transform it to a dataframe.

For example, if you have a local PostgreSQL database named `testDatabase` with a table `Customer`,
you can read the first 100 rows and print the data by just copying the code below:

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.api.print

val url = "jdbc:postgresql://localhost:5432/testDatabase"
val username = "postgres"
val password = "password"

val dbConfig = DbConnectionConfig(url, username, password)

val tableName = "Customer"

val df = DataFrame.readSqlTable(dbConfig, tableName, 100)

df.print()
```

You can find a full example project [here](https://github.com/zaleslaw/KotlinDataFrame-SQL-Examples/).

## Getting Started with Notebooks

To use the latest version of the Kotlin DataFrame library 
and a specific version of the JDBC driver for your database (MariaDB is used as an example below) in your Notebook,
run the following two cells.

First, specify the version of the JDBC driver

```
USE {
    dependencies("org.mariadb.jdbc:mariadb-java-client:$version")
}
```
Next, import `Kotlin DataFrame` library in the cell below.

```
%use dataframe
```

**NOTE:** The order of cell execution is important, 
the dataframe library is waiting for a JDBC driver to force classloading.

Find a full example Notebook [here](https://github.com/zaleslaw/KotlinDataFrame-SQL-Examples/blob/master/notebooks/imdb.ipynb).

## Nullability Inference

Each method has an important parameter called `inferNullability`. 

By default, this parameter is set to `true`, 
indicating that the method should inherit the `NOT NULL` constraints 
from the SQL table definition. 

However, if you prefer to ignore the SQL constraints 
and determine nullability solely based on the presence of null values in the data, 
set this parameter to `false`. 

In this case, the column will be considered nullable if there is at least one null value in the data; 
otherwise, it will be considered non-nullable for the newly created `DataFrame` object.

## Reading Specific Tables

These functions read all data from a specific table in the database. 
Variants with a limit parameter restrict how many rows will be read from the table.

**readSqlTable(dbConfig: DbConnectionConfig, tableName: String, limit: Int, inferNullability: Boolean, dbType: DbType?): AnyFrame**

Read all data from a specific table in the SQL database and transform it into an `AnyFrame` object.

The `dbConfig: DbConnectionConfig` parameter represents the configuration for a database connection, 
created under the hood and managed by the library.
Typically, it requires a URL, username, and password.

The `dbType` parameter is the type of database, could be a custom object, provided by user, optional, default is `null`,
to know more, read the [guide](readSqlFromCustomDatabase.md).

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig

val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val users = DataFrame.readSqlTable(dbConfig, "Users")
```

The `limit: Int` parameter allows setting the maximum number of records to be read.

```kotlin
val users = DataFrame.readSqlTable(dbConfig, "Users", limit = 100)
```

**readSqlTable(connection: Connection, tableName: String, limit: Int, inferNullability: Boolean, dbType: DbType?): AnyFrame**

Another variant, where instead of `dbConfig: DbConnectionConfig` we use a JDBC connection: `Connection` object.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val users = DataFrame.readSqlTable(connection, "Users")

connection.close()
```

### Extension functions for reading SQL table

The same example, rewritten with the extension function:

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val users = connection.readDataFrame("Users", 100)

connection.close()
```

**Connection.readDataFrame(sqlQueryOrTableName: String, limit: Int, inferNullability: Boolean, dbType: DbType?): AnyFrame**

Read all data from a specific table in the SQL database and transform it into an `AnyFrame` object.

`sqlQueryOrTableName:String` is the SQL query to execute or name of the SQL table. 

NOTE: It should be a name of one of the existing SQL tables, 
or the SQL query should start from SELECT and contain one query for reading data without any manipulation.
It should not contain `;` symbol.

All other parameters are described above.

**DbConnectionConfig.readDataFrame(sqlQueryOrTableName: String, limit: Int, inferNullability: Boolean, dbType: DbType?): AnyFrame**

If you do not have a connection object or need to run a quick, 
isolated experiment reading data from an SQL database,
you can delegate the creation of the connection to `DbConnectionConfig`.

## Executing SQL Queries

These functions execute an SQL query on the database and convert the result into a `DataFrame` object. 
If a limit is provided, only that many rows will be returned from the result.

**readSqlQuery(dbConfig: DbConnectionConfig, sqlQuery: String, limit: Int, inferNullability: Boolean, dbType: DbType?): AnyFrame**

Execute a specific SQL query on the SQL database and retrieve the resulting data as an AnyFrame.

The `dbConfig: DbConnectionConfig` parameter represents the configuration for a database connection,
created under the hood and managed by the library.
Typically, it requires a URL, username, and password.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig

val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val df = DataFrame.readSqlQuery(dbConfig, "SELECT * FROM Users WHERE age > 35")
```

**readSqlQuery(connection: Connection, sqlQuery: String, limit: Int, inferNullability: Boolean, dbType: DbType?): AnyFrame**

Another variant, where instead of `dbConfig: DbConnectionConfig` we use a JDBC connection: `Connection` object.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val df = DataFrame.readSqlQuery(connection, "SELECT * FROM Users WHERE age > 35")

connection.close()
```

### Extension functions for reading a result of an SQL query

The same example, rewritten with the extension function:

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val df = connection.readDataFrame(dbConfig, "SELECT * FROM Users WHERE age > 35", 10)

connection.close()
```

## Reading from ResultSet

These functions read data from a `ResultSet` object and convert it into a `DataFrame`. 
The versions with a limit parameter will only read up to the specified number of rows.

**readResultSet(resultSet: ResultSet, dbType: DbType, limit: Int, inferNullability: Boolean): AnyFrame**

This function allows reading a `ResultSet` object from your SQL database 
and transforms it into an `AnyFrame` object. 

A ResultSet object maintains a cursor pointing to its current row of data. 
By default, a `ResultSet` object is not updatable and has a cursor that moves forward only. 
Therefore, you can iterate it only once and only from the first row to the last row. 

More details about `ResultSet` can be found in the [official Java documentation](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).

Note that reading from the `ResultSet` could potentially change its state.

The `dbType: DbType` parameter specifies the type of our database (e.g., PostgreSQL, MySQL, etc.), 
supported by a library. 
Currently, the following classes are available: `H2, MsSql, MariaDb, MySql, PostgreSql, Sqlite, DuckDb`.

Also, users have an ability to pass objects, describing their custom databases, more information in [guide](readSqlFromCustomDatabase.md).

```kotlin
import org.jetbrains.kotlinx.dataframe.io.db.PostgreSql
import java.sql.ResultSet

val df = DataFrame.readResultSet(resultSet, PostgreSql)
```

**readResultSet(resultSet: ResultSet, connection: Connection, limit: Int, inferNullability: Boolean, dbType: DbType?): AnyFrame**

Another variant, we use a JDBC connection: `Connection` object.

```kotlin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val df = DataFrame.readResultSet(resultSet, connection)

connection.close()
```

### Extension functions for reading a result of the SQL query

The same example, rewritten with the extension function:

```kotlin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val df = rs.readDataFrame(connection, 10)

connection.close()
```

**ResultSet.readDataFrame(connection: Connection, limit: Int, inferNullability: Boolean, dbType: DbType?): AnyFrame**

Reads the data from a `ResultSet` and converts it into a `DataFrame`.

`connection` is the connection to the database (it's required to extract the database type) 
that the `ResultSet` belongs to.

## Reading Entire Tables

These functions read all data from all tables in the connected database. 
Variants with a limit parameter restrict how many rows will be read from each table.

**readAllSqlTables(dbConfig: DbConnectionConfig, limit: Int, inferNullability: Boolean, dbType: DbType?): Map\<String, AnyFrame>**

Retrieves data from all the non-system tables in the SQL database and returns them as a map of table names to `AnyFrame` objects.

The `dbConfig: DbConnectionConfig` parameter represents the configuration for a database connection,
created under the hood and managed by the library.
Typically, it requires a URL, username, and password.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig

val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val dataframes = DataFrame.readAllSqlTables(dbConfig)
```

**readAllSqlTables(connection: Connection, limit: Int, inferNullability: Boolean, dbType: DbType?): Map\<String, AnyFrame>**

Another variant, where instead of `dbConfig: DbConnectionConfig` we use a JDBC connection: `Connection` object.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val dataframes = DataFrame.readAllSqlTables(connection)

connection.close()
```

## Schema reading for a specific SQL table

The purpose of these functions is to facilitate the retrieval of table schema. 
By providing a table name and either a database configuration or connection, 
these functions return the [DataFrameSchema](schema.md) of the specified table.

**getSchemaForSqlTable(dbConfig: DbConnectionConfig, tableName: String, dbType: DbType?): DataFrameSchema**

This function captures the schema of a specific table from an SQL database.

The `dbConfig: DbConnectionConfig` parameter represents the configuration for a database connection,
created under the hood and managed by the library.
Typically, it requires a URL, username, and password.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig

val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val schema = DataFrame.getSchemaForSqlTable(dbConfig, "Users")
```

**getSchemaForSqlTable(connection: Connection, tableName: String, dbType: DbType?): DataFrameSchema**

Another variant, where instead of `dbConfig: DbConnectionConfig` we use a JDBC connection: `Connection` object.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val schema = DataFrame.getSchemaForSqlTable(connection, "Users")

connection.close()
```

## Schema reading from an SQL query

These functions return the schema of an SQL query result. 

Once you provide a database configuration or connection and an SQL query, 
they return the [DataFrameSchema](schema.md) of the query result.

**getSchemaForSqlQuery(dbConfig: DbConnectionConfig, sqlQuery: String, dbType: DbType?): DataFrameSchema**

This function executes an SQL query on the database and then retrieves the resulting schema.

The `dbConfig: DbConnectionConfig` parameter represents the configuration for a database connection,
created under the hood and managed by the library.
Typically, it requires a URL, username, and password.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig

val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val schema = DataFrame.getSchemaForSqlQuery(dbConfig, "SELECT * FROM Users WHERE age > 35")
```

**getSchemaForSqlQuery(connection: Connection, sqlQuery: String, dbType: DbType?): DataFrameSchema**

Another variant, where instead of `dbConfig: DbConnectionConfig` we use a JDBC connection: `Connection` object.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val schema = DataFrame.getSchemaForSqlQuery(connection, "SELECT * FROM Users WHERE age > 35")

connection.close()
```

### Extension functions for schema reading from an SQL query or an SQL table

The same example, rewritten with the extension function:

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val schema = connection.getDataFrameSchema("SELECT * FROM Users WHERE age > 35")

connection.close()
```
**Connection.getDataFrameSchema(sqlQueryOrTableName: String, dbType: DbType?): DataFrameSchema**

Retrieves the schema of an SQL query result or an SQL table using the provided database configuration.

**DbConnectionConfig.getDataFrameSchema(sqlQueryOrTableName: String, dbType: DbType?): DataFrameSchema**

Retrieves the schema of an SQL query result or an SQL table using the provided database configuration.

The `dbConfig: DbConnectionConfig` represents the configuration for a database connection,
created under the hood and managed by the library.
Typically, it requires a URL, username, and password.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig

val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val schema = dbConfig.getDataFrameSchema("SELECT * FROM Users WHERE age > 35")
```

## Schema reading from ResultSet

These functions return the schema from a `ResultSet` provided by the user. 

This can help developers infer the structure of the result set, 
which is quite essential for data transformation and mapping purposes.

**getSchemaForResultSet(resultSet: ResultSet, dbType: DbType): DataFrameSchema**

This function reads the schema from a `ResultSet` object provided by the user.

The `dbType: DbType` parameter specifies the type of our database (e.g., PostgreSQL, MySQL, etc.),
supported by a library.
Currently, the following classes are available: `H2, MsSql, MariaDb, MySql, PostgreSql, Sqlite, DuckDB`.

Also, users have an ability to pass objects, describing their custom databases, more information in [guide](readSqlFromCustomDatabase.md).

```kotlin
import org.jetbrains.kotlinx.dataframe.io.db.PostgreSql
import java.sql.ResultSet

val schema = DataFrame.getSchemaForResultSet(resultSet, PostgreSql)
```

### Extension functions for schema reading from the ResultSet

The same example, rewritten with the extension function:

```kotlin
import org.jetbrains.kotlinx.dataframe.io.db.PostgreSql
import java.sql.ResultSet

val schema = resultSet.getDataFrameSchema(PostgreSql)
```

based on

**ResultSet.getDataFrameSchema(dbType: DbType): DataFrameSchema**

## Schema reading for all non-system tables

These functions return a list of all [`DataFrameSchema`](schema.md) from all the non-system tables in the SQL database. 
They can be called with either a database configuration or a connection.

**getSchemaForAllSqlTables(dbConfig: DbConnectionConfig, dbType: DbType?): Map\<String, DataFrameSchema>**

This function retrieves the schema of all tables from an SQL database 
and returns them as a map of table names to [`DataFrameSchema`](schema.md) objects.

The `dbConfig: DbConnectionConfig` parameter represents the configuration for a database connection,
created under the hood and managed by the library.
Typically, it requires a URL, username, and password.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig

val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val schemas = DataFrame.getSchemaForAllSqlTables(dbConfig)
```

**getSchemaForAllSqlTables(connection: Connection, dbType: DbType?): Map\<String, DataFrameSchema>**

This function retrieves the schema of all tables using a JDBC connection: `Connection` object 
and returns them as a list of [`DataFrameSchema`](schema.md).

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val schemas = DataFrame.getSchemaForAllSqlTables(connection)

connection.close()
```
