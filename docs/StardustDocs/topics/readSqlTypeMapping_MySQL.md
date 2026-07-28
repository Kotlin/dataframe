[//]: # (title: MySQL type mapping)

<web-summary>
Reference table of how each MySQL column type is mapped to a Kotlin type when read into
a Kotlin DataFrame.
</web-summary>

<card-summary>
How MySQL column types are read into DataFrame.
</card-summary>

<link-summary>
Full mapping of MySQL SQL types to Kotlin types, including aliases and driver caveats.
</link-summary>

The tables below list every MySQL column type ([MySQL 8.0 Data Types](https://dev.mysql.com/doc/refman/8.0/en/data-types.html))
and the Kotlin type produced when the column is read into a DataFrame. Aliases are
canonicalised by MySQL at `CREATE TABLE` time, so DataFrame only ever sees the canonical
type; they are listed in the same row as the canonical type for reference.

Nullable columns produce nullable Kotlin types (`Int?` instead of `Int`).

## Numeric types

| Canonical              | Aliases                | DataFrame type          | Notes                                                                                     |
|------------------------|------------------------|-------------------------|-------------------------------------------------------------------------------------------|
| `TINYINT[(M)]`         | `INT1`                 | `Int`                   | 1-byte signed integer (`-128 .. 127`).                                                    |
| `TINYINT UNSIGNED`     | `INT1 UNSIGNED`        | `Int`                   | 1-byte unsigned integer (`0 .. 255`).                                                     |
| `BOOL`, `BOOLEAN`      | *(alias only)*         | `Boolean`               | Stored as `TINYINT(1)`; values are `0`/`1`.                                               |
| `SMALLINT[(M)]`        | `INT2`                 | `Int`                   | 2-byte signed integer.                                                                    |
| `SMALLINT UNSIGNED`    | `INT2 UNSIGNED`        | `Int`                   | 2-byte unsigned integer.                                                                  |
| `MEDIUMINT[(M)]`       | `INT3`, `MIDDLEINT`    | `Int`                   | 3-byte signed integer.                                                                    |
| `MEDIUMINT UNSIGNED`   | `INT3 UNSIGNED`, `MIDDLEINT UNSIGNED` | `Int`    | 3-byte unsigned integer.                                                                  |
| `INT[(M)]`             | `INTEGER`, `INT4`      | `Int`                   | 4-byte signed integer.                                                                    |
| `INT UNSIGNED`         | `INTEGER UNSIGNED`, `INT4 UNSIGNED` | `Long`     | MySQL override: fits in `Long`, not `Int`.                                                |
| `BIGINT[(M)]`          | `INT8`                 | `Long`                  | 8-byte signed integer.                                                                    |
| `BIGINT UNSIGNED`      | `INT8 UNSIGNED`        | `java.math.BigInteger`  | MySQL override: exceeds `Long`, needs `BigInteger`.                                       |
| `DECIMAL(M,D)`         | `DEC`, `NUMERIC`, `FIXED` | `java.math.BigDecimal` | Fixed-point.                                                                              |
| `FLOAT[(P)]`           | `FLOAT4`               | `Float`                 | 4-byte float. Becomes `Double` if the driver reports the column class as `java.lang.Double`. |
| `DOUBLE`               | `FLOAT8`, `DOUBLE PRECISION` | `Double`         | 8-byte float.                                                                             |
| `REAL`                 | *(alias only)*         | `Double`                | By default `REAL` is `DOUBLE`. Becomes `FLOAT` under the `REAL_AS_FLOAT` SQL mode.         |
| `BIT(M)`               | *none*                 | `ByteArray`             | Reported as `BIT`/`VARBINARY`; `BIT(1)` may map to `Boolean`.                              |

## Date and time types

| Canonical           | Aliases  | DataFrame type          | Notes                                                                     |
|---------------------|----------|-------------------------|---------------------------------------------------------------------------|
| `DATE`              | *none*   | `java.util.Date`        |                                                                           |
| `TIME[(fsp)]`       | *none*   | `java.sql.Time`         | `fsp` is fractional seconds precision (0–6).                              |
| `DATETIME[(fsp)]`   | *none*   | `kotlin.time.Instant`   | Preprocessed from `java.sql.Timestamp`.                                   |
| `TIMESTAMP[(fsp)]`  | *none*   | `kotlin.time.Instant`   | Preprocessed from `java.sql.Timestamp`.                                   |
| `YEAR[(4)]`         | *none*   | `java.util.Date`        | 1-byte year (`1901..2155`); driver reports it as `Types.DATE`. Two-digit form `YEAR(2)` was removed in 5.7.5. |

## String types

| Canonical             | Aliases                                                       | DataFrame type       | Notes                                                                    |
|-----------------------|---------------------------------------------------------------|----------------------|--------------------------------------------------------------------------|
| `CHAR(M)`             | `CHARACTER(M)`, `NATIONAL CHAR(M)`, `NCHAR(M)`                | `String`             | Fixed-length text. Max `M = 255`.                                        |
| `VARCHAR(M)`          | `CHARACTER VARYING(M)`, `NATIONAL VARCHAR(M)`, `NVARCHAR(M)`  | `String`             | Variable-length text.                                                    |
| `BINARY(M)`           | *none*                                                        | `ByteArray`          | Fixed-length binary.                                                     |
| `VARBINARY(M)`        | `BINARY VARYING(M)`                                           | `ByteArray`          | Variable-length binary.                                                  |
| `TINYBLOB`            | *none*                                                        | `ByteArray`          | Up to 255 bytes.                                                         |
| `TINYTEXT`            | *none*                                                        | `String`             | Up to 255 bytes.                                                         |
| `BLOB[(M)]`           | *none*                                                        | `ByteArray`          | Up to 64 KiB.                                                            |
| `TEXT[(M)]`           | *none*                                                        | `String`             | Up to 64 KiB.                                                            |
| `MEDIUMBLOB`          | `LONG VARBINARY`                                              | `ByteArray`          | Up to 16 MiB.                                                            |
| `MEDIUMTEXT`          | `LONG VARCHAR`, `LONG`                                        | `String`             | Up to 16 MiB.                                                            |
| `LONGBLOB`            | *none*                                                        | `ByteArray`          | Up to 4 GiB.                                                             |
| `LONGTEXT`            | *none*                                                        | `String`             | Up to 4 GiB.                                                             |
| `ENUM(...)`           | *none*                                                        | `String`             | Enumeration; values read as their string form.                           |
| `SET(...)`            | *none*                                                        | `String`             | Comma-separated set of values, as a single string.                       |

## JSON

| Canonical | Aliases | DataFrame type | Notes                                                                                              |
|-----------|---------|----------------|----------------------------------------------------------------------------------------------------|
| `JSON`    | *none*  | `String`       | Validated JSON text. Physically distinct from `LONGTEXT` in MySQL; JDBC still returns it as text. |

## Spatial types

All spatial types are read as `ByteArray` (WKB / EWKB binary as reported by the driver).
Parse them with a client-side geometry library.

| Canonical            | Aliases | DataFrame type | Notes |
|----------------------|---------|----------------|-------|
| `GEOMETRY`           | *none*  | `ByteArray`    |       |
| `POINT`              | *none*  | `ByteArray`    |       |
| `LINESTRING`         | *none*  | `ByteArray`    |       |
| `POLYGON`            | *none*  | `ByteArray`    |       |
| `MULTIPOINT`         | *none*  | `ByteArray`    |       |
| `MULTILINESTRING`    | *none*  | `ByteArray`    |       |
| `MULTIPOLYGON`       | *none*  | `ByteArray`    |       |
| `GEOMETRYCOLLECTION` | *none*  | `ByteArray`    |       |

## MySQL specifics

- `INT UNSIGNED` and `BIGINT UNSIGNED` are the only types that need MySQL-specific handling.
  `INT UNSIGNED` is widened to `Long`; `BIGINT UNSIGNED` becomes `BigInteger` because its range
  exceeds `Long`.
- The value of `REAL` depends on the [`REAL_AS_FLOAT`](https://dev.mysql.com/doc/refman/8.0/en/sql-mode.html)
  SQL mode: default `REAL` is `DOUBLE`; with `REAL_AS_FLOAT` set, it's `FLOAT`.
- Zero-fill and display-width modifiers (e.g. `INT(11)`, `TINYINT ZEROFILL`) do not affect the
  DataFrame type; MySQL 8.0.17+ ignores them for most numeric types.
- Unlike MariaDB, MySQL 8.0 does **not** have native `UUID` / `INET4` / `INET6` types; use
  `CHAR(36)` or `BINARY(16)` and convert client-side.

## Unsupported types

- Native representations of spatial types (they read as raw WKB `ByteArray`).
- `VECTOR` (MySQL 9.0+); currently read as `ByteArray`.
- User-defined `TYPE` (rarely used in MySQL).

For explicit support of these, either cast in your query (e.g. `SELECT ST_AsText(geom)`)
or supply a custom converter via [a custom DbType](readSqlFromCustomDatabase.md).
