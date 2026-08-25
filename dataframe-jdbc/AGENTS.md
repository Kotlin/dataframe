# AGENTS.md — dataframe-jdbc

Guidance for this module. See the root `AGENTS.md` for repo-wide build/style/KoDEx rules; only module-specific
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

## The `DbType` abstraction — the core of this module

`io/db/DbType.kt` is an `abstract class` keyed by `dbTypeInJdbcUrl`. Each supported database is a subclass:
`H2`, `MariaDb`, `MySql`, `Sqlite`, `PostgreSql`, `MsSql`, `DuckDb` (all in `io/db/`). `extractDBTypeFromUrl(url)`
in `io/db/util.kt` matches the JDBC URL substring to pick the instance (H2 additionally resolves its `MODE` —
MySQL/PostgreSQL/MSSQLServer/MariaDB/Regular — from the URL or a live connection).

The value-reading pipeline is a sequence of overridable `open` functions on `DbType`, applied per column. Understand
this chain before changing type mapping — the type variables `J → D → P` flow through it:

1. `getTableColumnsMetadata(resultSet)` → `TableColumnMetadata` list (with defensive fallbacks for drivers like Hive
   that throw `SQLFeatureNotSupportedException`).
2. Type **J** (what the driver returns): `getExpectedJdbcType(...)` gives `typeOf<J>()` — the type
   `ResultSet.getObject` actually returns — and `getValueFromResultSet(...)` extracts the actual value of type J
   (which must match that type exactly).
3. Type **D**: `getPreprocessedValueType(...)` / `preprocessValue(...)` — Java→Kotlin conversions
   (e.g. `Timestamp`→`kotlin.time.Instant`, `java.util.UUID`→`kotlin.uuid.Uuid`).
4. Type **P**: `getTargetColumnSchema(...)` → `ColumnSchema` and `buildDataColumn(...)` → the final `DataColumn<P>`
   (post-processes `java.sql.Array`→Kotlin arrays).

Other overridable behavior: `quoteIdentifier` (per-DB identifier quoting), `buildSqlQueryWithLimit`/
`buildSelectTableQueryWithLimit`, `configureReadStatement` (fetch size/direction, query timeout), `createConnection`
(SQLite needs read-only set at connect time), `isSystemTable`, `buildTableMetadata`, `tableTypes`.

**Adding a new database or fixing type handling:** subclass `DbType`, override only the functions that deviate from
the defaults, register it in `extractDBTypeFromUrl`, and copy the KDoc structure from a sibling like `PostgreSql.kt`.
For databases with structured/composite types, subclass `AdvancedDbType` instead (see next section).

### `AdvancedDbType` — for structured types / heavy per-type mapping (experimental)

`io/db/AdvancedDbType.kt` is an alternative base class (`abstract class AdvancedDbType : DbType`, **experimental,
API subject to change**). Instead of overriding the individual `open` pipeline functions, it `final`-overrides the
**whole `J → D → P` chain** and routes every column through a single per-column `JdbcToDataFrameConverter`
(`io/db/JdbcToDataFrameConverter.kt`). A subclass implements just one method —
`protected abstract fun generateConverter(tableColumnMetadata): AnyJdbcToDataFrameConverter` — and the returned
converter supplies the expected JDBC type, preprocessed type, target schema, value extraction, and column building.
Converters are cached per `CacheKey(sqlTypeName, jdbcType, javaClassName, isNullable)`.

**When to use which:** extend plain `DbType` for ordinary relational databases where you only tweak a few functions
(type mapping, quoting, limits, system-table filtering). Extend `AdvancedDbType` when a database has
structured/composite types (arrays, `STRUCT`/`MAP`/`LIST` nesting) or needs a lot of per-JDBC-type mapping and a
single caching converter per column type is cleaner than many overrides. The only built-in subclass today is
**`DuckDb`** (`io/db/DuckDb.kt`) — use it as the reference implementation. (This is the piece agents have missed in
the past, e.g. while extending SQLite handling — check whether `AdvancedDbType` fits before overriding `DbType`.)

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

The module uses KoDEx + BuildConfig generation (`processKDocsMain` depends on `generateBuildConfigClasses`);
generated sources are build output — see the root `AGENTS.md` for the generated-sources and KDoc rules.
