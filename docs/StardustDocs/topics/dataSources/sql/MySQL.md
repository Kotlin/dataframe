# MySQL

<web-summary>
Connect to MySQL databases and load data into Kotlin DataFrame using JDBC — query, analyze, and transform SQL data in Kotlin.
</web-summary>

<card-summary>
Use Kotlin DataFrame with MySQL — easily read tables and queries over JDBC into powerful data structures.
</card-summary>

<link-summary>
Read data from MySQL into Kotlin DataFrame using JDBC configuration.
</link-summary>


Kotlin DataFrame supports reading from [MySQL](https://www.mysql.com) database using JDBC.

Requires the [`dataframe-jdbc` module](Modules.md#dataframe-jdbc),
which is included by default in the general [`dataframe` artifact](Modules.md#dataframe-general)
and in [`%use dataframe`](SetupKotlinNotebook.md#integrate-kotlin-dataframe) for Kotlin Notebook.

You’ll also need [the official MySQL JDBC driver](https://dev.mysql.com/downloads/connector/j/):

<tabs>
<tab title="Gradle project">

```kotlin
dependencies {
    implementation("com.mysql:mysql-connector-j:$version")
}
```

</tab>
<tab title="Kotlin Notebook">


```kotlin
USE {
    dependencies("com.mysql:mysql-connector-j:$version")
}
```

</tab>
</tabs>

The actual Maven Central driver version could be found
[here](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc).

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

val url = "jdbc:mysql://localhost:3306/testDatabase"
val username = "root"
val password = "password"

val dbConfig = DbConnectionConfig(url, username, password)

val tableName = "Customer"

val df = DataFrame.readSqlTable(dbConfig, tableName)
```
