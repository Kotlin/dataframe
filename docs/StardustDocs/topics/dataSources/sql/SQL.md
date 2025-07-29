# SQL

<web-summary>
Work with SQL databases in Kotlin using DataFrame and JDBC — read tables and queries with ease.
</web-summary>

<card-summary>
Connect to PostgreSQL, MySQL, SQLite, and other SQL databases using Kotlin DataFrame's JDBC support.
</card-summary>

<link-summary>
Load data from SQL databases into Kotlin DataFrame using JDBC and built-in reading functions.
</link-summary>

Kotlin DataFrame supports reading from SQL databases using JDBC.

Requires the [`dataframe-jdbc` module](Modules.md#dataframe-jdbc),
which is included by default in the general [`dataframe` artifact](Modules.md#dataframe-general)
and in [`%use dataframe`](gettingStartedKotlinNotebook.md#integrate-kotlin-dataframe) for Kotlin Notebook.
You’ll also need a JDBC driver for the specific database.

## Supported databases

Kotlin DataFrame provides out-of-the-box support for the most common SQL databases:

- [PostgreSQL](PostgreSQL.md)
- [MySQL](MySQL.md)
- [Microsoft SQL Server](Microsoft-SQL-Server.md)
- [SQLite](SQLite.md)
- [H2](H2.md)
- [MariaDB](MariaDB.md)

You can also define a [Custom SQL Source](Custom-SQL-Source.md)
to work with any other JDBC-compatible database.

## Read

[`DataFrame`](DataFrame.md) can be loaded from a database in several ways: 
a user can read data from a SQL table by given name ([`readSqlTable`](readSqlDatabases.md)), 
as a result of a user-defined SQL query ([`readSqlQuery`](readSqlDatabases.md)), 
or from a given `ResultSet` ([`readResultSet`](readSqlDatabases.md)). 
It is also possible to load all data from non-system tables, each into a separate `DataFrame`
([`readAllSqlTables`](readSqlDatabases.md)).

See [](readSqlDatabases.md) for more details.
