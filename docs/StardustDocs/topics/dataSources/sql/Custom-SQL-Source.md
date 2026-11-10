# Custom SQL Source

<web-summary>
Connect Kotlin DataFrame to any JDBC-compatible database using a custom SQL source configuration.
</web-summary>

<card-summary>
Easily integrate unsupported SQL databases in Kotlin DataFrame using a flexible custom source setup.
</card-summary>

<link-summary>
Define a custom SQL source in Kotlin DataFrame to work with any JDBC-based database.
</link-summary>


If your SQL database is not officially supported, you can either
[create an issue](https://github.com/Kotlin/dataframe/issues)
or define a simple, configurable custom SQL source.

**API reference**: [`DbType`](https://kotlin.github.io/dataframe/api-docs/dataframe-jdbc/org.jetbrains.kotlinx.dataframe.io.db/-db-type/index.html),
[`AdvancedDbType`](https://kotlin.github.io/dataframe/api-docs/dataframe-jdbc/org.jetbrains.kotlinx.dataframe.io.db/-advanced-db-type/index.html)

See the [How to Extend DataFrame Library for Custom SQL Database Support guide](readSqlFromCustomDatabase.md)
for detailed instructions and an example with HSQLDB.

