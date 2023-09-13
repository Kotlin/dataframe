[//]: # (title: Read from SQL databases)

These functions allow you to interact with an SQL database using a Kotlin DataFrame library.

There are two main blocks of available functionality:
* reading data from the database
  * reading specific tables
  * executing SQL queries
  * reading from ResultSet
  * reading entire tables (all non-system tables)
* schema retrieval
  * for specific tables
  * for result of executing SQL queries
  * for rows reading through the given ResultSet
  * for all non-system tables

 
## Reading Specific Tables

These functions read all data from a specific table in the database. 
Variants with a limit parameter restrict how many rows will be read from the table.

**readSqlTable(dbConfig: DatabaseConfiguration, tableName: String): AnyFrame**

Read all data from a specific table in the SQL database and transform it into an AnyFrame object.

The `dbConfig: DatabaseConfiguration` parameter represents the configuration for a database connection, 
created under the hood and managed by the library. Typically, it requires a URL, username and password.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DatabaseConfiguration

val dbConfig = DatabaseConfiguration("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val users = DataFrame.readSqlTable(dbConfig, "Users")
```

**readSqlTable(dbConfig: DatabaseConfiguration, tableName: String, limit: Int): AnyFrame**

A variant of the previous function,
but with an added `limit: Int` parameter that allows setting the maximum number of records to be read.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DatabaseConfiguration

val dbConfig = DatabaseConfiguration("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val users = DataFrame.readSqlTable(dbConfig, "Users", 100)
```

**readSqlTable(connection: Connection, tableName: String): AnyFrame**

Another variant, where instead of `dbConfig: DatabaseConfiguration` we use a JDBC connection: `Connection` object.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val users = DataFrame.readSqlTable(connection, "Users")

connection.close()
```

**readSqlTable(connection: Connection, tableName: String, limit: Int): AnyFrame**

A variant of the previous function,
but with an added `limit: Int` parameter that allows setting the maximum number of records to be read.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val users = DataFrame.readSqlTable(connection, "Users", 100)

connection.close()
```

## Executing SQL Queries

These functions execute an SQL query on the database and convert the result into a DataFrame. 
If a limit is provided, only that many rows will be returned from the result.

**readSqlQuery(dbConfig: DatabaseConfiguration, sqlQuery: String): AnyFrame**

Execute a specific SQL query on the SQL database and retrieve the resulting data as an AnyFrame.

The `dbConfig: DatabaseConfiguration` parameter represents the configuration for a database connection,
created under the hood and managed by the library. Typically, it requires a URL, username and password.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DatabaseConfiguration

val dbConfig = DatabaseConfiguration("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val df = DataFrame.readSqlQuery(dbConfig, "SELECT * FROM Users WHERE age > 35")
```

**readSqlQuery(dbConfig: DatabaseConfiguration, sqlQuery: String, limit: Int): AnyFrame**

A variant of the previous function,
but with an added `limit: Int` parameter that allows setting the maximum number of records to be read.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DatabaseConfiguration

val dbConfig = DatabaseConfiguration("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val df = DataFrame.readSqlQuery(dbConfig, "SELECT * FROM Users WHERE age > 35", 10)
```

**readSqlQuery(connection: Connection, sqlQuery: String): AnyFrame**

Another variant, where instead of `dbConfig: DatabaseConfiguration` we use a JDBC connection: `Connection` object.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val df = DataFrame.readSqlQuery(dbConfig, "SELECT * FROM Users WHERE age > 35")

connection.close()
```

**readSqlQuery(connection: Connection, sqlQuery: String, limit: Int): AnyFrame**

A variant of the previous function,
but with an added `limit: Int` parameter that allows setting the maximum number of records to be read.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val df = DataFrame.readSqlQuery(dbConfig, "SELECT * FROM Users WHERE age > 35", 10)

connection.close()
```

## Reading from ResultSet

These functions read data from a ResultSet object and convert it into a DataFrame. 
The versions with a limit parameter will only read up to the specified number of rows.

**readResultSet(resultSet: ResultSet, dbType: DbType): AnyFrame**

This function allows reading a ResultSet object from your SQL database 
and transforms it into an AnyFrame object. 

The `dbType: DbType` parameter specifies the type of our database (e.g., PostgreSQL, MySQL, etc), 
supported by a library. 
Currently, the following classes are available: `H2, MariaDb, MySql, PostgreSql, Sqlite`.


```kotlin
import org.jetbrains.kotlinx.dataframe.io.db.PostgreSql
import java.sql.ResultSet

val df = DataFrame.readResultSet(resultSet, PostgreSql)
```

**readResultSet(resultSet: ResultSet, dbType: DbType, limit: Int): AnyFrame**

A variant of the previous function, 
but with an added `limit: Int` parameter that allows setting the maximum number of records to be read.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.db.PostgreSql
import java.sql.ResultSet

val df = DataFrame.readResultSet(resultSet, PostgreSql, 10)
```

**readResultSet(resultSet: ResultSet, connection: Connection): AnyFrame**

Another variant, where instead of `dbType: DbType` we use a JDBC connection: `Connection` object.

```kotlin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val df = DataFrame.readResultSet(resultSet, connection)

connection.close()
```

**readResultSet(resultSet: ResultSet, connection: Connection, limit: Int): AnyFrame**

A variant of the previous function,
but with an added `limit: Int` parameter that allows setting the maximum number of records to be read.

```kotlin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val df = DataFrame.readResultSet(resultSet, connection, 10)

connection.close()
```

## Reading Entire Tables

These functions read all data from all tables in the connected database. 
Variants with a limit parameter restrict how many rows will be read from each table.

**readAllTables(connection: Connection): List<AnyFrame>**

Retrieves data from all the non-system tables in the SQL database and returns them as a list of AnyFrame objects.

The `dbConfig: DatabaseConfiguration` parameter represents the configuration for a database connection,
created under the hood and managed by the library. Typically, it requires a URL, username and password.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DatabaseConfiguration

val dbConfig = DatabaseConfiguration("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val dataframes = DataFrame.readAllTables(dbConfig)
```

**readAllTables(connection: Connection, limit: Int): List<AnyFrame>**

A variant of the previous function,
but with an added `limit: Int` parameter that allows setting the maximum number of records to be read from each table.

NOTE: the setting the different limits for different tables is not supported.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DatabaseConfiguration

val dbConfig = DatabaseConfiguration("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val dataframes = DataFrame.readAllTables(dbConfig, 100)
```

**readAllTables(connection: Connection): AnyFrame**

Another variant, where instead of `dbConfig: DatabaseConfiguration` we use a JDBC connection: `Connection` object.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val dataframes = DataFrame.readAllTables(connection)

connection.close()
```

**readAllTables(connection: Connection, limit: Int): List<AnyFrame>**

A variant of the previous function,
but with an added `limit: Int` parameter that allows setting the maximum number of records to be read from each table.

NOTE: the setting the different limits for different tables is not supported.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val dataframes = DataFrame.readAllTables(connection, 100)

connection.close()
```

## Schema retrieval for specific SQL table

The purpose of these functions is to facilitate the retrieval of table schema. 
By providing a table name and either a database configuration or connection, 
these functions return the [DataFrameSchema](schema.md) of the specified table.

**getSchemaForSqlTable(dbConfig: DatabaseConfiguration, tableName: String): DataFrameSchema**

This function captures the schema of a specific table from an SQL database.

The `dbConfig: DatabaseConfiguration` parameter represents the configuration for a database connection,
created under the hood and managed by the library. Typically, it requires a URL, username and password.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DatabaseConfiguration

val dbConfig = DatabaseConfiguration("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val schema = DataFrame.getSchemaForSqlTable(dbConfig, "Users")
```

**getSchemaForSqlTable(connection: Connection, tableName: String): DataFrameSchema**

Another variant, where instead of `dbConfig: DatabaseConfiguration` we use a JDBC connection: `Connection` object.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val schema = DataFrame.getSchemaForSqlTable(connection, "Users")

connection.close()
```

## Schema retrieval from SQL query

These functions return the schema of an SQL query result. 

Once you provide a database configuration or connection and an SQL query, 
they return the [DataFrameSchema](schema.md) of the query result.

**getSchemaForSqlQuery(dbConfig: DatabaseConfiguration, sqlQuery: String): DataFrameSchema**

This function executes an SQL query on the database and then retrieves the resulting schema.

The `dbConfig: DatabaseConfiguration` parameter represents the configuration for a database connection,
created under the hood and managed by the library. Typically, it requires a URL, username and password.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DatabaseConfiguration

val dbConfig = DatabaseConfiguration("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val schema = DataFrame.getSchemaForSqlQuery(dbConfig, "SELECT * FROM Users WHERE age > 35")
```

**getSchemaForSqlQuery(connection: Connection, sqlQuery: String): DataFrameSchema**

Another variant, where instead of `dbConfig: DatabaseConfiguration` we use a JDBC connection: `Connection` object.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val schema = DataFrame.getSchemaForSqlQuery(connection, "SELECT * FROM Users WHERE age > 35")

connection.close()
```

## Schema retrieval from ResultSet

These functions return the schema from a ResultSet provided by the user. 

This can help developers infer the structure of the result set, 
which is quite essential for data transformation and mapping purposes.

**getSchemaForResultSet(resultSet: ResultSet, dbType: DbType): DataFrameSchema**

This function reads the schema from a ResultSet object provided by the user.

The `dbType: DbType` parameter specifies the type of our database (e.g., PostgreSQL, MySQL, etc.),
supported by a library.
Currently, the following classes are available: `H2, MariaDb, MySql, PostgreSql, Sqlite`.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.db.PostgreSql
import java.sql.ResultSet

val schema = DataFrame.getSchemaForResultSet(resultSet, PostgreSql)
```

**getSchemaForSqlQuery(connection: Connection, sqlQuery: String): DataFrameSchema**

Another variant, where instead of `dbConfig: DatabaseConfiguration` we use a JDBC connection: `Connection` object.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val schema = DataFrame.getSchemaForResultSet(resultSet, connection)

connection.close()
```

## Schema retrieval for all non-system tables

These functions return a list of all [`DataFrameSchema`](schema.md) from all the non-system tables in the SQL database. 
They can be called with either a database configuration or a connection.

**getSchemaForAllTables(dbConfig: DatabaseConfiguration): List<DataFrameSchema>**

This function retrieves the schema of all tables from an SQL database 
and returns them as a list of [`DataFrameSchema`](schema.md).

The `dbConfig: DatabaseConfiguration` parameter represents the configuration for a database connection,
created under the hood and managed by the library. Typically, it requires a URL, username and password.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DatabaseConfiguration

val dbConfig = DatabaseConfiguration("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

val schemas = DataFrame.getSchemaForAllTables(dbConfig)
```

**getSchemaForAllTables(connection: Connection): List<DataFrameSchema**

This function retrieves the schema of all tables using a JDBC connection: `Connection` object 
and returns them as a list of [`DataFrameSchema`](schema.md).

```kotlin
import java.sql.Connection
import java.sql.DriverManager

val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

val schemas = DataFrame.getSchemaForAllTables(connection)

connection.close()
```
