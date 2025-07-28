# SQLite

<web-summary>
Use Kotlin DataFrame to read data from SQLite databases with minimal setup via JDBC.
</web-summary>

<card-summary>
Query and transform SQLite data directly in Kotlin using DataFrame and JDBC.
</card-summary>

<link-summary>
Read SQLite tables into Kotlin DataFrame using the built-in JDBC integration.
</link-summary>


Kotlin DataFrame supports reading from [SQLite](https://www.sqlite.org) database using JDBC.

Requires the [`dataframe-jdbc` module](Modules.md#dataframe-jdbc),
which is included by default in the general [`dataframe` artifact](Modules.md#dataframe-general)
and in [`%use dataframe`](gettingStartedKotlinNotebook.md#integrate-kotlin-dataframe) for Kotlin Notebook.

You’ll also need [SQLite JDBC driver](https://github.com/xerial/sqlite-jdbc):

<tabs>
<tab title="Gradle project">

```kotlin
dependencies {
    implementation("org.xerial:sqlite-jdbc:$version")
}
```

</tab>
<tab title="Kotlin Notebook">


```kotlin
USE {
    dependencies("org.xerial:sqlite-jdbc:$version")
}
```

</tab>
</tabs>

The actual Maven Central driver version could be found
[here](https://mvnrepository.com/artifact/com.mysql/mysql-connector-j).

## Read

[`DataFrame`](DataFrame.md) can be loaded from an SQLite database using various methods:
[`readSqlTable`](readSqlDatabases.md), [`readSqlQuery`](readSqlDatabases.md),
[`readResultSet`](readSqlDatabases.md), and [`readAllSqlTables`](readSqlDatabases.md).
See [](readSqlDatabases.md) for more details.

```kotlin
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.api.*

val url = "jdbc:sqlite:testDatabase.db"

val dbConfig = DbConnectionConfig(url)

val tableName = "Customer"

val df = DataFrame.readSqlTable(dbConfig, tableName)
```
