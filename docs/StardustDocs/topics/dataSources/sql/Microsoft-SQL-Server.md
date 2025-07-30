# Microsoft SQL Server (MS SQL)

<web-summary>
Connect to Microsoft SQL Server using Kotlin DataFrame and JDBC — load structured data directly into your Kotlin workflow.
</web-summary>

<card-summary>
Use Kotlin DataFrame to read from Microsoft SQL Server — run queries or load entire tables via JDBC.
</card-summary>

<link-summary>
Fetch data from Microsoft SQL Server into Kotlin DataFrame using JDBC configuration.
</link-summary>


Kotlin DataFrame supports reading from [Microsoft SQL Server (MS SQL)](https://www.microsoft.com/en-us/sql-server) 
database using JDBC.

Requires the [`dataframe-jdbc` module](Modules.md#dataframe-jdbc),
which is included by default in the general [`dataframe` artifact](Modules.md#dataframe-general)
and in [`%use dataframe`](gettingStartedKotlinNotebook.md#integrate-kotlin-dataframe) for Kotlin Notebook.

You’ll also need 
[the official MS SQL JDBC driver](https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server?view=sql-server-ver17):

<tabs>
<tab title="Gradle project">

```kotlin
dependencies {
    implementation("com.microsoft.sqlserver:mssql-jdbc:$version")
}
```

</tab>
<tab title="Kotlin Notebook">


```kotlin
USE {
    dependencies("com.microsoft.sqlserver:mssql-jdbc:$version")
}
```

</tab>
</tabs>

The actual Maven Central driver version could be found
[here](https://mvnrepository.com/artifact/com.microsoft.sqlserver/mssql-jdbc).

## Read

[`DataFrame`](DataFrame.md) can be loaded from a database in several ways:  
a user can read data from a SQL table by given name ([`readSqlTable`](readSqlDatabases.md)),  
as a result of a user-defined SQL query ([`readSqlQuery`](readSqlDatabases.md)),  
or from a given `ResultSet` ([`readResultSet`](readSqlDatabases.md)).  
It is also possible to load all data from non-system tables, each into a separate `DataFrame` ([`readAllSqlTables`](readSqlDatabases.md)).

See [](readSqlDatabases.md) for more details.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.api.*

val url = "jdbc:sqlserver://localhost:1433;databaseName=testDatabase"
val username = "sa"
val password = "password"

val dbConfig = DbConnectionConfig(url, username, password)

val tableName = "Customer"

val df = DataFrame.readSqlTable(dbConfig, tableName)
```
