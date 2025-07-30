# MariaDB

<web-summary>
Access MariaDB databases using Kotlin DataFrame and JDBC — fetch data from tables or custom SQL queries with ease.
</web-summary>

<card-summary>
Seamlessly integrate MariaDB with Kotlin DataFrame — load data using JDBC and analyze it in Kotlin.
</card-summary>

<link-summary>
Read data from MariaDB into Kotlin DataFrame using standard JDBC configurations.
</link-summary>


Kotlin DataFrame supports reading from [MariaDB](https://mariadb.org) database using JDBC.

Requires the [`dataframe-jdbc` module](Modules.md#dataframe-jdbc),
which is included by default in the general [`dataframe` artifact](Modules.md#dataframe-general)
and in [`%use dataframe`](SetupKotlinNotebook.md#integrate-kotlin-dataframe) for Kotlin Notebook.

You’ll also need [the official MariaDB JDBC driver](https://mariadb.com/docs/connectors/mariadb-connector-j):

<tabs>
<tab title="Gradle project">

```kotlin
dependencies {
    implementation("org.mariadb.jdbc:mariadb-java-client:$version")
}
```

</tab>
<tab title="Kotlin Notebook">


```kotlin
USE {
    dependencies("org.mariadb.jdbc:mariadb-java-client:$version")
}
```

</tab>
</tabs>

The actual Maven Central driver version could be found
[here](https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client).

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

val url = "jdbc:mariadb://localhost:3306/testDatabase"
val username = "root"
val password = "password"

val dbConfig = DbConnectionConfig(url, username, password)

val tableName = "Customer"

val df = DataFrame.readSqlTable(dbConfig, tableName)
```
