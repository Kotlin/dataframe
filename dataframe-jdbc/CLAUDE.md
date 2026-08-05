# CLAUDE.md — dataframe-jdbc

Guidance for this module. See the root `CLAUDE.md` for repo-wide build/style/KoDEx rules; only module-specific
details are here.

## What this module is

`dataframe-jdbc` (artifact `dataframe-jdbc`, "JDBC support for Kotlin DataFrame"). Reads SQL tables, queries,
`ResultSet`s, `DataSource`s, and whole schemas from any JDBC database into a `DataFrame`, mapping SQL types to
Kotlin types. Depends only on `core`.

**No JDBC driver is shipped for any database** — users always bring their own driver on the runtime classpath
(this applies equally to H2, MySQL, MariaDB, MSSQL, PostgreSQL, SQLite, DuckDB). DuckDB, SQLite, and PostgreSQL are
declared `compileOnly` for a *different* reason: the module's own code compiles against those drivers' specific API
classes (`DuckDb.kt` → `org.duckdb.DuckDBColumnType.*`; `PostgreSql.kt` → `org.postgresql.geometric.PG*`, `PGobject`;
`Sqlite.kt` → `org.sqlite.SQLiteConfig`), so their classes must be on the *compile* classpath but are deliberately
kept off the *runtime* classpath. The other `DbType`s reference their driver only by class-name string
(e.g. `"org.h2.Driver"`, `"com.mysql.jdbc.Driver"`), so they need no compile-time dependency at all.

## Public API surface (`src/main/kotlin/.../io/`)

- `readJdbc.kt` — the entry points, all as `DataFrame.Companion` / receiver extensions:
  `readSqlTable`, `readSqlQuery`, `readResultSet`, `readAllSqlTables`, and `readDataFrame` on
  `DbConnectionConfig` / `Connection` / `DataSource` / `ResultSet`. Each has overloads taking a
  `DbConnectionConfig`, an existing `Connection`, or a `DataSource`.
- `DbConnectionConfig.kt` — URL/user/password/readOnly config for library-managed connections.
- `jdbcSchema.kt`, `readDataFrameSchema.kt` — schema inference (for codegen / `@DataSchema`).

Binary-compatibility validation is enabled here (`binary-compatibility-validator` plugin). Public API changes must
be reflected in the `.api` dump — run the `apiDump`/`apiCheck` Gradle tasks when you change public signatures.

## The `DbType` abstraction — the core of this module

`io/db/DbType.kt` is an `abstract class` keyed by `dbTypeInJdbcUrl`. Each supported database is a subclass:
`H2`, `MariaDb`, `MySql`, `Sqlite`, `PostgreSql`, `MsSql`, `DuckDb` (all in `io/db/`). `extractDBTypeFromUrl(url)`
in `io/db/util.kt` matches the JDBC URL substring to pick the instance (H2 additionally resolves its `MODE` —
MySQL/PostgreSQL/MSSQLServer/MariaDB/Regular — from the URL or a live connection).

The value-reading pipeline is a sequence of overridable `open` functions on `DbType`, applied per column. Understand
this chain before changing type mapping — the four type variables `J → D → P` flow through it:

1. `getTableColumnsMetadata(resultSet)` → `TableColumnMetadata` list (with defensive fallbacks for drivers like Hive
   that throw `SQLFeatureNotSupportedException`).
2. `getExpectedJdbcType(...)` → `KType` **J**: the type `ResultSet.getObject` actually returns.
3. `getValueFromResultSet(...)` → raw value of type J.
4. `getPreprocessedValueType(...)` / `preprocessValue(...)` → type **D**: Java→Kotlin conversions
   (e.g. `Timestamp`→`kotlin.time.Instant`, `java.util.UUID`→`kotlin.uuid.Uuid`).
5. `getTargetColumnSchema(...)` → `ColumnSchema` and `buildDataColumn(...)` → the final `DataColumn<P>`
   (post-processes `java.sql.Array`→Kotlin arrays).

Other overridable behavior: `quoteIdentifier` (per-DB identifier quoting), `buildSqlQueryWithLimit`/
`buildSelectTableQueryWithLimit`, `configureReadStatement` (fetch size/direction, query timeout), `createConnection`
(SQLite needs read-only set at connect time), `isSystemTable`, `buildTableMetadata`, `tableTypes`.

**Adding a new database or fixing type handling:** subclass `DbType` (see `AdvancedDbType.kt` for extended cases),
override only the functions that deviate from the defaults, register it in `extractDBTypeFromUrl`, and copy the
KDoc structure from a sibling like `PostgreSql.kt`.

## Custom `DbType` (extensibility for users)

Every `read*` function accepts an optional `DbType` argument. When it's omitted, the library resolves the built-in
type from the JDBC URL via `extractDBTypeFromUrl`; when it's supplied, that instance is used directly and URL
detection is skipped. A user can therefore pass their own `DbType` subclass **without touching this module or
registering anything** — it lives entirely in the user's own code.

This is the intended escape hatch for cases the built-ins don't cover, for example:

- a database the library doesn't ship a `DbType` for (Oracle, DB2, Snowflake, ClickHouse, Hive, …);
- a driver whose SQL→Kotlin type mapping or `ResultSet` behavior deviates and needs `getExpectedJdbcType` /
  `preprocessValue` / `getValueFromResultSet` overridden;
- per-DB tuning: custom `quoteIdentifier`, `defaultFetchSize`/`defaultQueryTimeout`, `tableTypes`, or
  `isSystemTable` filtering.

Because the user's subclass overrides the same `open` pipeline described above, custom types get the full
metadata → JDBC type → preprocess → column-build flow for free and only override what differs.

## Tests

Test layout under `src/test/kotlin/.../io/` splits by how the DB is provided:

- `io/h2/` — run entirely on **embedded H2** in the various compatibility modes (`h2Test`, `mysqlH2Test`,
  `postgresH2Test`, `mssqlH2Test`, `mariadbH2Test`). These need no external DB and run in normal `test`.
- `io/local/` — integration tests against a **real DBMS** (`postgresTest`, `mysqlTest`, `mariadbTest`,
  `mssqlTest`, `duckDbTest`, `imdbTest`). They connect to either a Dockerized or a locally installed server;
  don't point them at a production database.
- `io/db/jdbcTypesTest.kt` — SQL-type-to-KType mapping.
- SQLite tests at the top level use bundled `.sqlite` files in `src/test/resources/`.
- `commonTestScenarios.kt` holds the shared assertions reused across databases.

Test-only driver dependencies (MariaDB, MySQL, MSSQL, H2, SQLite, PostgreSQL, DuckDB, plus HikariCP and
kotest-assertions) are declared `testImplementation` in `build.gradle.kts`. Run just this module with
`./gradlew dataframe-jdbc:test` (add `-Pkotlin.dataframe.debug=true` to match CI checks).

## Note

The module uses KoDEx (`processKDocsMain` depends on `generateBuildConfigClasses`) and BuildConfig generation, so
generated sources here are produced by the build — don't hand-edit them.
