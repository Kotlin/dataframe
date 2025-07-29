# PostgreSQL

<web-summary>
Work with PostgreSQL databases in Kotlin — read tables and queries into DataFrames using JDBC.
</web-summary>

<card-summary>
Use Kotlin DataFrame to query and transform PostgreSQL data directly via JDBC.
</card-summary>

<link-summary>
Read PostgreSQL data into Kotlin DataFrame with JDBC support.
</link-summary>


Kotlin DataFrame supports reading from [PostgreSQL](https://www.postgresql.org) database using JDBC.

Requires the [`dataframe-jdbc` module](Modules.md#dataframe-jdbc),
which is included by default in the general [`dataframe` artifact](Modules.md#dataframe-general)
and in [`%use dataframe`](gettingStartedKotlinNotebook.md#integrate-kotlin-dataframe) for Kotlin Notebook.

You’ll also need [the official PostgreSQL JDBC driver](https://jdbc.postgresql.org):

<tabs>
<tab title="Gradle project">

```kotlin
dependencies {
    implementation("org.postgresql:postgresql:$version")
}
```

</tab>
<tab title="Kotlin Notebook">


```kotlin
USE {
    dependencies("org.postgresql:postgresql:$version")
}
```

</tab>
</tabs>

The actual Maven Central driver version could be found 
[here](https://mvnrepository.com/artifact/org.postgresql/postgresql).

## Read

[`DataFrame`](DataFrame.md) can be loaded from a database in several ways:  
a user can read data from a SQL table by given name ([`readSqlTable`](readSqlDatabases.md)),  
as a result of a user-defined SQL query ([`readSqlQuery`](readSqlDatabases.md)),  
or from a given `ResultSet` ([`readResultSet`](readSqlDatabases.md)).  
It is also possible to load all data from non-system tables, each into a separate `DataFrame` ([`readAllSqlTables`](readSqlDatabases.md)).

See [](readSqlDatabases.md) for more details.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig

val url = "jdbc:postgresql://localhost:5432/testDatabase"
val username = "postgres"
val password = "password"

val dbConfig = DbConnectionConfig(url, username, password)

val tableName = "Customer"

val df = DataFrame.readSqlTable(dbConfig, tableName)
```
