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

**Unlike the other databases supported in Kotlin DataFrame, SQLite does NOT canonicalize declared
types.** `sqlTypeName` in the driver's metadata is byte-for-byte what you wrote in
`CREATE TABLE`. So `INT8`, `INTEGER`, `TINYINT`, `MEDIUMINT`, `UNSIGNED BIG INT` all share
INTEGER affinity, but each keeps its literal declared name. There is no separate alias
table — every declared name is resolved directly via the affinity tables below.

Because SQLite is dynamically typed, the Xerial JDBC driver reports `getColumnClassName`
based on the **actual stored value in the current row**, not on the declared column type. The
raw values returned by `rs.getObject(int)` therefore fall into a fixed set — driver produces
exactly one of `Integer` / `Long` / `Double` / `String` / `byte[]` / `null`, chosen per row
from the storage class of that value.

DataFrame's SQLite handler resolves each column in the following order:

1. **User-supplied [custom converters](#custom-converters)** (by column name, then by declared
   type name) always win.
2. **`DATE` / `DATETIME` / `TIME` / `TIMESTAMP`** — detected by a substring match on the declared
   type name. Column type is fixed to an idiomatic Kotlin date-time type
   (`kotlinx.datetime.LocalDate` / `LocalDateTime` / `LocalTime` / `kotlin.time.Instant`); each
   value is **converted** from its storage class during preprocessing (ISO text → parsed via
   `LocalDate`/`LocalDateTime`/`Instant.parse`; Unix INTEGER → epoch seconds; Julian REAL → date
   via the Julian-day formula). This gives one stable Kotlin type per column even when values
   are stored in mixed forms.
3. **`BOOLEAN` / `BIT`** — the driver reports `Types.BOOLEAN` but `rs.getObject` returns Integer
   (`0`/`1`). A preprocessor converts every raw value (Int, Long, Double, or `"true"`/`"1"`/`"y"`
   String) to an actual Kotlin `Boolean`.
4. **`DECIMAL` / `NUMERIC`** — no canonical numeric type; DataFrame trusts the driver-reported
   class of each column (`Int` / `Long` / `Double` / `ByteArray` / `String`).
5. **Everything else** falls through to the base `DbType` end-to-end mapping. Note that
   DataFrame may expect a special type for some SQL type names (for example, `UUID`), while the Xerial driver only provides primitives.
   Consider using [custom converters](#custom-converters) to handle these cases.

Nullable columns produce nullable Kotlin types (`Int?` instead of `Int`).

## INTEGER affinity

Declared type contains `INT`.

| Declared type                        | DataFrame column type | Notes                                                         |
|--------------------------------------|-----------------------|---------------------------------------------------------------|
| `INT`                                | `Int`                 |                                                               |
| `INTEGER`                            | `Int`                 | Also used implicitly for `INTEGER PRIMARY KEY` (rowid alias). |
| `TINYINT`                            | `Int`                 |                                                               |
| `SMALLINT`, `INT2`                   | `Int`                 |                                                               |
| `MEDIUMINT`                          | `Int`                 |                                                               |
| `BIGINT`, `INT8`, `UNSIGNED BIG INT` | `Long`                | Xerial reports `Types.BIGINT`; default map returns `Long`.    |

## REAL affinity

Declared type contains `REAL`, `FLOA`, or `DOUB`.

| Declared type                | DataFrame column type | Notes                                                                                                                                                    |
|------------------------------|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `REAL`, `FLOAT`              | `Double`              | Note: not `Float`. Driver reports `java.lang.Double` for the stored value, which triggers the `Types.REAL/FLOAT if java.lang.Double -> Double` override. |
| `DOUBLE`, `DOUBLE PRECISION` | `Double`              |                                                                                                                                                          |

## TEXT affinity

Declared type contains `CHAR`, `CLOB`, or `TEXT`.

| Declared type                                                 | DataFrame column type | Notes |
|---------------------------------------------------------------|-----------------------|-------|
| `TEXT`, `VARCHAR(n)`, `NVARCHAR(n)`, `CLOB`                   | `String`              |       |
| `CHAR(n)`, `NCHAR(n)`                                         | `String`              |       |
| `VARYING CHARACTER(n)`, `NATIVE CHARACTER(n)`, `CHARACTER(n)` | `String`              |       |

## BLOB affinity

Declared type contains `BLOB` or the column has no declared type.

| Declared type | DataFrame column type | Notes                                            |
|---------------|-----------------------|--------------------------------------------------|
| `BLOB`        | `ByteArray`           |                                                  |
| *(none)*      | `ByteArray`           | Column with no declared type falls back to BLOB. |

## NUMERIC affinity (fallback for everything else)

### DATE / DATETIME / TIME / TIMESTAMP — column type is fixed, values are converted

The DataFrame column type is an idiomatic Kotlin date-time type; each row's value is
converted from its storage class during preprocessing.

| Declared type | DataFrame column type            | Storage class → conversion                                                                                                                              |
|---------------|----------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| `DATE`        | `kotlinx.datetime.LocalDate`     | TEXT (ISO `YYYY-MM-DD`) → `LocalDate.parse`; INTEGER → date at Unix-seconds UTC; REAL → date at Julian day.                                             |
| `DATETIME`    | `kotlinx.datetime.LocalDateTime` | TEXT (`YYYY-MM-DD HH:MM:SS` or `YYYY-MM-DDTHH:MM:SS`) → `LocalDateTime.parse`; INTEGER → date-time at Unix-seconds UTC; REAL → date-time at Julian day. |
| `TIME`        | `kotlinx.datetime.LocalTime`     | TEXT (`HH:MM:SS` or `HH:MM:SS.SSS`) → `LocalTime.parse`; INTEGER → seconds since midnight; REAL → seconds since midnight (fractional part ignored).     |
| `TIMESTAMP`   | `kotlin.time.Instant`            | TEXT (ISO) → `Instant.parse`; INTEGER → `Instant.fromEpochSeconds` (Unix seconds); REAL → `Instant` at Julian day.                                      |

**Detection is by declared type name, not `jdbcType`.** Xerial changes the reported `jdbcType`
based on the actual stored value — e.g. a `DATE` column with a Julian-day REAL value is
reported as `Types.FLOAT`, and a `TIMESTAMP` column with an INTEGER value is reported as
`Types.INTEGER`. DataFrame's SQLite adapter looks at `sqlTypeName` (substring match: `DATETIME`,
`TIMESTAMP`, `DATE`, `TIME`) to preserve the intended date-time semantics regardless.

If a value cannot be parsed automatically (e.g., a `DATE` column contains an unexpected format), reading
throws with a clear error message referencing the column name and stored value. Opt out
of conversion by supplying a [custom converter](#custom-converters), e.g.

```kotlin
val sqlite = Sqlite.withCustomConverters {
    // Provide custom conversion for DATETIME columns.
    forType("DATETIME") { raw: String -> Instant.parse(raw, customFormat) }
}
```

### BOOLEAN — INTEGER 0/1 converted to Boolean

| Declared type    | Storage class | DataFrame column type | Notes                                                                                                                          |
|------------------|---------------|-----------------------|--------------------------------------------------------------------------------------------------------------------------------|
| `BOOLEAN`, `BIT` | INTEGER (0/1) | `Boolean`             | The preprocessor treats non-zero as `true`, zero as `false`.                                                                   |
| `BOOLEAN`, `BIT` | REAL          | `Boolean`             | Same convention (non-zero → `true`).                                                                                           |
| `BOOLEAN`, `BIT` | TEXT          | `Boolean`             | Case-insensitively accepts `true`/`1`/`yes`/`y`/`t` → `true` and `false`/`0`/`no`/`n`/`f` → `false`. Unrecognised text throws. |

`BOOL`/`BIT` substring in the declared name is also caught (e.g. columns declared `IS_ACTIVE_BOOL`).

### DECIMAL / NUMERIC — follow the storage class

`DECIMAL` and `NUMERIC` columns have no canonical numeric type; DataFrame reads the raw stored
value as-is (`Int`, `Long`, or `Double` depending on how each row was inserted).

| Declared type     | Storage class | DataFrame column type   | Notes                                                                                                                                                                 |
|-------------------|---------------|-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `NUMERIC`         | INTEGER       | `Int` / `Long`          | Follows the actual value's class. May fail for columns mixed `Int` /  `Long` values. Consider using [custom converter](#custom-converters)                            |
| `NUMERIC`         | REAL          | `Double`                |                                                                                                                                                                       |
| `DECIMAL(P,S)`    | REAL          | `Double`                |                                                                                                                                                                       |
| `DECIMAL(P,S)`    | INTEGER       | `Int` / `Long`          |                                                                                                                                                                       |
| unrecognised type | any           | depends on stored value | E.g. a text value in a `CUSTOM_TYPE` column is reported as `Types.VARCHAR` and read as `String`. Use a [custom converter](#custom-converters) to pin a specific type. |

Sometimes, a driver may return mixed `Int` and `Long` values for the same column,
which can break column type detection.
Consider using a [custom converter](#custom-converters) to specify the expected column type
and, optionally, provide a converter.

Assume we have a nullable `"mixed_values"` column for which the Xerial driver returns both
`Int` and `Long` values.

You can specify the expected column type (`Number`) explicitly:

```kotlin
val sqlite = Sqlite.withCustomConverters {
    forColumn<Number?>("mixed_values")
}
```

Or you can provide a converter to convert the values to the desired type:

```kotlin
val sqlite = Sqlite.withCustomConverters {
    forColumn("mixed_values") { raw: Number? -> raw?.toLong() }
}
```

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

## Custom converters

Use `Sqlite.withCustomConverters { ... }` to register per-column or per-declared-type overrides.
Column-name overrides win over type-name overrides.

Two overloads are available for each side:

- **Converter form** — `forType<T, R>(name) { raw -> ... }` / `forColumn<T, R>(name) { raw -> ... }`.
  A lambda transforms each raw stored value; the DataFrame column's Kotlin type is derived from
  the reified `R` via `typeOf<R>()`.
- **Identity form** — `forType<T>(name)` / `forColumn<T>(name)`. No transformation — values pass
  through as `T`. Handy when SQLite's type affinity misclassifies your column and the built-in
  mapping picks the wrong Kotlin type. Note: **nullability is part of `T`** — declare it
  explicitly (`forType<Long?>("BIGINT")`) if you want a nullable column type.

```kotlin
val format = LocalDateTime.Format {
    year(); char('-'); monthNumber(); char('-'); day()
    char(' ')
    hour(); char(':'); minute(); char(':'); second()
    chars(" UTC")
}

val sqlite = Sqlite.withCustomConverters {
    // Parse a proprietary text format into Instant.
    forType("MY_DATETIME") { raw: String? ->
        raw?.let { LocalDateTime.parse(it, format).toInstant(TimeZone.UTC) }
    }
    // Pin LONGVARCHAR to String? even though SQLite affinity says NUMERIC.
    forType<String?>("LONGVARCHAR")
    // Override a specific column by name — this wins over any type-name override.
    forColumn("ratio") { raw: String -> raw.toDouble() }
}

val df = DataFrame.readSqlTable(connection, "events", dbType = sqlite)
```

## SQLite specifics

- **No canonicalization** — the [Xerial JDBC driver](https://github.com/xerial/sqlite-jdbc)
  preserves the declared type verbatim in `sqlTypeName`. Two columns declared `INT` and
  `TINYINT` both have INTEGER affinity but distinct `sqlTypeName` values in metadata.
- **`rs.getObject(int)` returns exactly one of `Integer` / `Long` / `Double` / `String` /
  `byte[]` / `null`.** No `Timestamp` / `LocalDate` / `Boolean` / `Blob` ever reaches
  DataFrame from a SQLite driver — hence the SQLite adapter needs to translate.
- **`DATE` / `DATETIME` / `TIME` / `TIMESTAMP` are converted from storage class to an idiomatic
  Kotlin date-time type.** ISO strings, Unix epoch integers, and Julian days are all normalized
  to `kotlinx.datetime.LocalDate` / `LocalDateTime` / `LocalTime` / `kotlin.time.Instant` in
  preprocessing. This keeps the schema stable across rows even when values are stored in
  different formats. Unsupported inputs throw with a message pointing at the column and stored
  value and suggesting `Sqlite.withCustomConverters { }` as the escape hatch.
- **`BOOLEAN` and `BIT` are converted to Boolean.** SQLite has no boolean storage class —
  values are stored as INTEGER (0/1). The preprocessor converts every raw value (Int, Long,
  Double, or textual `true`/`false`/`yes`/`no`) back to a real Kotlin `Boolean`.
- **`DECIMAL` and `NUMERIC` follow the actual value's type.** A DECIMAL column with a stored
  double value becomes `Double`; with a stored integer value it becomes `Int` / `Long`.
- **Custom overrides** are registered via `Sqlite.withCustomConverters { ... }`. See the
  [Custom converters](#custom-converters) section above.
