[//]: # (title: SQLite type mapping)

<web-summary>
How SQLite's dynamic type system is mapped to Kotlin types when read into a Kotlin DataFrame,
including the type-affinity rules DataFrame relies on.
</web-summary>

<card-summary>
How SQLite column types are read into DataFrame, including type affinity and dynamic typing.
</card-summary>

<link-summary>
Full mapping of SQLite declared types to Kotlin types via affinity, plus DataFrame's SQLite-specific
overrides.
</link-summary>

SQLite differs meaningfully from the other JDBC databases: it uses **dynamic typing** and
has only **5 storage classes** (`NULL`, `INTEGER`, `REAL`, `TEXT`, `BLOB`). The declared
column type is used only as a hint via
[type affinity](https://www.sqlite.org/datatype3.html#type_affinity):

| Declared type contains        | Affinity   |
|-------------------------------|------------|
| `INT`                         | `INTEGER`  |
| `CHAR`, `CLOB`, `TEXT`        | `TEXT`     |
| `BLOB`, or no declared type   | `BLOB`     |
| `REAL`, `FLOA`, `DOUB`        | `REAL`     |
| anything else                 | `NUMERIC`  |

**Unlike the other databases in this documentation, SQLite does NOT canonicalize declared
types.** `sqlTypeName` in the driver's metadata is byte-for-byte what you wrote in
`CREATE TABLE`. So `INT8`, `INTEGER`, `TINYINT`, `MEDIUMINT`, `UNSIGNED BIG INT` all share
INTEGER affinity but each keeps its literal declared name. There is no separate alias
table — every declared name is resolved directly via the affinity tables below.

Because SQLite is dynamically typed, the Xerial JDBC driver reports `getColumnClassName`
based on the **actual stored value in the current row**, not on the declared column type.
DataFrame handles the two flavours of SQLite ambiguity differently:

- **`DATE` / `DATETIME` / `TIME` / `TIMESTAMP`** — column type is fixed to an idiomatic Kotlin
  date-time type (`kotlinx.datetime.LocalDate` / `kotlinx.datetime.LocalDateTime` /
  `kotlinx.datetime.LocalTime` / `kotlin.time.Instant`); each value is **converted** from
  its storage class during preprocessing (ISO text → parsed via `LocalDate`/`LocalDateTime`/
  `Instant.parse`; Unix INTEGER → epoch seconds; Julian REAL → date via the Julian-day formula).
  This gives one stable Kotlin type per column even when values are stored in mixed forms.
  `DATE` and `DATETIME` both surface as `Types.DATE` in the driver — they are distinguished by
  the declared type name.
- **`DECIMAL` / `NUMERIC`** — no canonical numeric type; DataFrame trusts the storage class of
  each column (`Double` / `Int` / `Long` / `BigDecimal`, ...).

Nullable columns produce nullable Kotlin types (`Int?` instead of `Int`).

## INTEGER affinity

Declared type contains `INT`.

| Declared type                                     | DataFrame type | Notes                                                                                     |
|---------------------------------------------------|----------------|-------------------------------------------------------------------------------------------|
| `INT`                                             | `Int`          |                                                                                           |
| `INTEGER`                                         | `Int`          | Also used implicitly for `INTEGER PRIMARY KEY` (rowid alias).                             |
| `TINYINT`                                         | `Int`          |                                                                                           |
| `SMALLINT`, `INT2`                                | `Int`          |                                                                                           |
| `MEDIUMINT`                                       | `Int`          |                                                                                           |
| `BIGINT`, `INT8`, `UNSIGNED BIG INT`              | `Long`         | Xerial reports `Types.BIGINT`; default map returns `Long`.                                |

## REAL affinity

Declared type contains `REAL`, `FLOA`, or `DOUB`.

| Declared type                       | DataFrame type | Notes                                                                                       |
|-------------------------------------|----------------|---------------------------------------------------------------------------------------------|
| `REAL`, `FLOAT`                     | `Double`       | Note: not `Float`. Driver reports `java.lang.Double` for the stored value, which triggers the `Types.REAL/FLOAT if java.lang.Double -> Double` override. |
| `DOUBLE`, `DOUBLE PRECISION`        | `Double`       |                                                                                             |

## TEXT affinity

Declared type contains `CHAR`, `CLOB`, or `TEXT`.

| Declared type                       | DataFrame type   | Notes                                                                                    |
|-------------------------------------|------------------|------------------------------------------------------------------------------------------|
| `TEXT`, `VARCHAR(n)`, `NVARCHAR(n)` | `String`         |                                                                                          |
| `CHAR(n)`, `NCHAR(n)`               | `String`         |                                                                                          |
| `VARYING CHARACTER(n)`, `NATIVE CHARACTER(n)`, `CHARACTER(n)` | `String` | All fall under TEXT affinity via the `CHAR` substring.                             |
| `CLOB`                              | `java.sql.Clob`  | Follows the default JDBC mapping; the actual stored value is text.                       |

## BLOB affinity

Declared type contains `BLOB` or the column has no declared type.

| Declared type | DataFrame type | Notes                                              |
|---------------|----------------|----------------------------------------------------|
| `BLOB`        | `ByteArray`    |                                                    |
| *(none)*      | `ByteArray`    | Column with no declared type falls back to BLOB.   |

## NUMERIC affinity (fallback for everything else)

### DATE / DATETIME / TIME / TIMESTAMP — column type is fixed, values are converted

The DataFrame column type is an idiomatic Kotlin date-time type; each row's value is
converted from its storage class during preprocessing.

| Declared type          | DataFrame type                       | Storage class → conversion                                                                                    |
|------------------------|--------------------------------------|---------------------------------------------------------------------------------------------------------------|
| `DATE`                 | `kotlinx.datetime.LocalDate`         | TEXT (ISO `YYYY-MM-DD`) → `LocalDate.parse`; INTEGER → date at Unix-seconds UTC; REAL → date at Julian day. |
| `DATETIME`             | `kotlinx.datetime.LocalDateTime`     | TEXT (`YYYY-MM-DD HH:MM:SS` or `YYYY-MM-DDTHH:MM:SS`) → `LocalDateTime.parse`; INTEGER → date-time at Unix-seconds UTC; REAL → date-time at Julian day. |
| `TIME`                 | `kotlinx.datetime.LocalTime`         | TEXT (`HH:MM:SS`) → `LocalTime.parse`; INTEGER → seconds since midnight.                                      |
| `TIMESTAMP`            | `kotlin.time.Instant`                | TEXT (ISO) → `Instant.parse`; INTEGER → `Instant.fromEpochSeconds` (Unix seconds); REAL → `Instant` at Julian day. |

**Detection is by declared type name, not `jdbcType`.** Xerial changes the reported `jdbcType`
based on the actual stored value — e.g. a `DATE` column with a Julian-day REAL value is
reported as `Types.FLOAT`, and a `TIMESTAMP` column with an INTEGER value is reported as
`Types.INTEGER`. DataFrame's SQLite adapter looks at `sqlTypeName` (substring match: `DATETIME`,
`TIMESTAMP`, `DATE`, `TIME`) to preserve the intended date-time semantics regardless.

If a value cannot be parsed (e.g. a `DATE` column contains an unexpected format), reading
throws with a clear error message referencing the column name and stored value. Opt out
of conversion by supplying a custom mapping, e.g.

```kotlin
val sqlite = Sqlite(customTypesMap = mapOf("DATETIME" to typeOf<String>()))
```

### DECIMAL / NUMERIC / BOOLEAN / unknown types — follow the storage class

| Declared type    | Storage class | DataFrame type          | Notes                                                                                     |
|------------------|---------------|-------------------------|-------------------------------------------------------------------------------------------|
| `BOOLEAN`        | INTEGER (0/1) | `Boolean`               | Stored as INTEGER but the driver still reports `Types.BOOLEAN`. `Sqlite.getValueFromResultSet` uses `rs.getBoolean` to return a real `Boolean`. |
| `NUMERIC`        | INTEGER       | `Int` / `Long`          | Follows the actual value's class.                                                         |
| `NUMERIC`        | REAL          | `Double`                |                                                                                           |
| `DECIMAL(P,S)`   | REAL          | `Double`                |                                                                                           |
| `DECIMAL(P,S)`   | INTEGER       | `Int` / `Long`          |                                                                                           |
| unrecognised type| any           | depends on stored value | E.g. a text value in a `CUSTOM_TYPE` column is reported as `Types.VARCHAR` and read as `String`. |

## STRICT tables

SQLite supports [`STRICT` tables](https://www.sqlite.org/stricttables.html) which enforce a
limited set of storage class names (`ANY`, `INT`, `INTEGER`, `REAL`, `TEXT`, `BLOB`) and
reject values of the wrong storage class. In `STRICT` tables the declared type is guaranteed
to match the storage class, so the affinity tables above still hold — just without the
ambiguity of ordinary tables. There are no dedicated `BOOLEAN` / `DATE` / `TIMESTAMP`
storage classes in `STRICT` tables either.

The `ANY` column type accepts any storage class and reports the class of the stored value in
metadata — DataFrame maps this via the same storage-class rules (`String` / `Int` / `Long` /
`Double` / `ByteArray`).

## SQLite specifics

- **No canonicalisation** — the driver preserves the declared type verbatim in
  `sqlTypeName`. Two columns declared `INTEGER` and `TINYINT` both have INTEGER affinity but
  distinct `sqlTypeName` values in metadata.
- **`BOOLEAN` requires a workaround.** SQLite has no boolean storage class — values are
  stored as INTEGER (0/1). Xerial reports the metadata as `Types.BOOLEAN`, but
  `rs.getObject` returns `Integer`. DataFrame calls `rs.getBoolean` explicitly to convert
  each value back to a real Kotlin `Boolean`.
- **`DATE` / `DATETIME` / `TIME` / `TIMESTAMP` are converted from storage class to an idiomatic
  Kotlin date-time type.** ISO strings, Unix epoch integers, and Julian days are all normalised
  to `kotlinx.datetime.LocalDate` / `LocalDateTime` / `LocalTime` / `kotlin.time.Instant` in
  preprocessing. This keeps the schema stable across rows even when values are stored in
  different formats. Unsupported inputs throw with a message pointing at the column and stored
  value.
- **`DECIMAL` and `NUMERIC` follow the actual value's type.** A DECIMAL column with a stored
  double value becomes `Double`; with a stored integer value it becomes `Int` / `Long`.
- **Custom overrides** can be supplied via `Sqlite(customTypesMap = ...)` — entries are
  matched by declared type name (`sqlTypeName`) and take precedence over the built-in mapping:

  ```kotlin
  val sqlite = Sqlite(customTypesMap = mapOf("MY_TYPE" to typeOf<String>()))
  ```

## Unsupported types

- SQLite JSON values are simply TEXT — read as `String`. Use client-side parsing.
- Dates and timestamps are read as raw storage-class values (`String` / `Int` / `Long` /
  `Double`) — DataFrame does not attempt to parse them into `kotlinx.datetime` types.
- Client-defined types via SQLite's JSON1 or R*Tree extensions are read as their storage class.
