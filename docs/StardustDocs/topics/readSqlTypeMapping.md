[//]: # (title: SQL to DataFrame column type mapping)

<web-summary>
Overview of how SQL column types are mapped to Kotlin types when reading a DataFrame
from each supported JDBC database.
</web-summary>

<card-summary>
Overview of the SQL to Kotlin type mapping used by `dataframe-jdbc`.
</card-summary>

<link-summary>
Overview and index of the per-database SQL to Kotlin type mapping used by `dataframe-jdbc`.
</link-summary>

When reading from a JDBC database, DataFrame determines the Kotlin type of each column in
two steps using `DbType` — a class responsible for handling column metadata and creating
[`DataFrame`](DataFrame.md) columns based on it:

1. **Type resolution** — the SQL/JDBC type reported by the driver is mapped to a JDBC-side
   Kotlin type (`DbType.getExpectedJdbcType`).
2. **Value preprocessing** — a few JDBC types are converted to more idiomatic Kotlin types
   before being placed into the DataFrame (`DbType.preprocessValue`). For example,
   `java.sql.Timestamp` is turned into `kotlin.time.Instant`.

Column nullability is determined from the metadata provided by the JDBC driver. If the driver does not explicitly report a column as non-nullable, it is mapped to a nullable Kotlin type (`Int?` instead of `Int`).`

For some databases (such as [DuckDB](readSqlTypeMapping_DuckDB.md) and [SQLite](readSqlTypeMapping_SQLite.md)),
this logic is overridden with custom dialect-specific converters based on column metadata —
`JdbcToDataFrameConverter`. See [`AdvancedDbType`](readSqlFromCustomDatabase.md#advanceddbtype-new-in-1-0-0-beta5)
for more information.

## Type alias handling

Most SQL dialects define **type aliases** (e.g. `INT8` for `BIGINT` in MariaDB/MySQL/PostgreSQL,
`BOOL` for `BOOLEAN`, `INTEGER` for `INT`, ...). Every database supported by DataFrame — **except
SQLite** — canonicalizes the declared type name at `CREATE TABLE` time, so the JDBC driver reports
only the canonical form while getting a column metadata. That means DataFrame never sees
the alias, only its canonical mapping. Each per-database page lists the aliases in the same row as
the canonical type.

**SQLite is the exception** — it preserves the declared type verbatim in metadata and applies
"type affinity" instead. See the [SQLite page](readSqlTypeMapping_SQLite.md) for how this works.

## Per-database type mapping pages

Here is a list of links for each database supported in Kotlin DataFrame:

| Database                                       | How it uses `DbType`                                                                                                   |
|------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| [MariaDB](readSqlTypeMapping_MariaDB.md)       | Default `DbType` + overrides for unsigned integer types.                                                               |
| [MySQL](readSqlTypeMapping_MySQL.md)           | Default `DbType` + overrides for unsigned integer types.                                                               |
| [PostgreSQL](readSqlTypeMapping_PostgreSQL.md) | Default `DbType` + `PGobject` overrides (`box`, `point`, `money`, ...).                                                |
| [MS SQL Server](readSqlTypeMapping_MsSql.md)   | Default `DbType`, no overrides.                                                                                        |
| [H2](readSqlTypeMapping_H2.md)                 | Default `DbType` in `Regular` mode; other modes delegate to another dialect.                                           |
| [SQLite](readSqlTypeMapping_SQLite.md)         | `AdvancedDbType` with custom converters for boolean, date-time and numeric types. Allows to provide custom converters. |
| [DuckDB](readSqlTypeMapping_DuckDB.md)         | `AdvancedDbType` with its own converters.                                                                              |

## Extending the mapping

If none of the built-in mappings fit your use case, you can register a custom `DbType`;
see [Reading from a custom SQL database](readSqlFromCustomDatabase.md).
