# DuckDB

<web-summary>
Work with DuckDB databases in Kotlin — read tables and queries into DataFrames using JDBC.
</web-summary>

<card-summary>
Use Kotlin DataFrame to query and transform DuckDB data directly via JDBC.
</card-summary>

<link-summary>
Read DuckDB data into Kotlin DataFrame with JDBC support.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.io.DuckDb-->

Kotlin DataFrame supports reading from [DuckDB](https://duckdb.org/) databases using JDBC.

This requires the [`dataframe-jdbc` module](Modules.md#dataframe-jdbc),
which is included by default in the general [`dataframe` artifact](Modules.md#dataframe-general)
and in [`%use dataframe`](SetupKotlinNotebook.md#integrate-kotlin-dataframe) for Kotlin Notebook.

You’ll also need [the official DuckDB JDBC driver](https://duckdb.org/docs/stable/clients/java):

<tabs>
<tab title="Gradle project">

```kotlin
dependencies {
    implementation("org.duckdb:duckdb_jdbc:$version")
}
```

</tab>
<tab title="Kotlin Notebook">

```kotlin
USE {
    dependencies("org.duckdb:duckdb_jdbc:$version")
}
```

</tab>
</tabs>

The actual Maven Central driver version can be found
[here](https://mvnrepository.com/artifact/org.duckdb/duckdb_jdbc).

## Read

A [`DataFrame`](DataFrame.md) instance can be loaded from a database in several ways:  
a user can read data from a SQL table by a given name ([`readSqlTable`](readSqlDatabases.md)),  
as the result of a user-defined SQL query ([`readSqlQuery`](readSqlDatabases.md)),  
or from a given `ResultSet` ([`readResultSet`](readSqlDatabases.md)).  
It is also possible to load all data from non-system tables, each into a separate `DataFrame` ([
`readAllSqlTables`](readSqlDatabases.md)).

See [](readSqlDatabases.md) for more details.

<!---FUN readSqlTable-->

```kotlin
val url = "jdbc:duckdb:/testDatabase"
val username = "duckdb"
val password = "password"

val dbConfig = DbConnectionConfig(url, username, password)

val tableName = "Customer"

val df = DataFrame.readSqlTable(dbConfig, tableName)
```

<!---END-->

### Extensions

DuckDB has a special trick up its sleeve: it has support
for [extensions](https://duckdb.org/docs/stable/extensions/overview).
These can be installed, loaded, and used to connect to a different database via DuckDB.
See [Core Extensions](https://duckdb.org/docs/stable/core_extensions/overview) for a list of available extensions.

For example, let's load a dataframe
from [Apache Iceberg via DuckDB](https://duckdb.org/docs/stable/core_extensions/iceberg/overview.html),
as Iceberg is an unsupported data source in DataFrame at the moment:

<!---FUN readIcebergExtension-->

```kotlin
// Creating an in-memory DuckDB database
val connection = DriverManager.getConnection("jdbc:duckdb:")
val df = connection.use { connection ->
    // install and load Iceberg
    connection.createStatement().execute("INSTALL iceberg; LOAD iceberg;")

    // query a table from Iceberg using a specific SQL query
    DataFrame.readSqlQuery(
        connection = connection,
        sqlQuery = "SELECT * FROM iceberg_scan('data/iceberg/lineitem_iceberg', allow_moved_paths = true);",
    )
}
```

<!---END-->

As you can see, the process is very similar to reading from any other JDBC database,
just without needing explicit DataFrame support.
