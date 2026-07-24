[//]: # (title: MariaDB type mapping)

<web-summary>
Reference table of how each MariaDB column type is mapped to a Kotlin type when read into
a Kotlin DataFrame.
</web-summary>

<card-summary>
How MariaDB column types are read into DataFrame.
</card-summary>

<link-summary>
Full mapping of MariaDB SQL types to Kotlin types, including aliases and driver caveats.
</link-summary>

The tables below list every MariaDB column type ([MariaDB Data Types](https://mariadb.com/kb/en/data-types/))
and the Kotlin type produced when the column is read into a DataFrame. Aliases are
canonicalised by MariaDB at `CREATE TABLE` time, so DataFrame only ever sees the canonical
type; they are listed in the same row as the canonical type for reference.

Nullable columns produce nullable Kotlin types (`Int?` instead of `Int`).

## Numeric types

| Canonical              | Aliases                | DataFrame type          | Notes                                                          |
|------------------------|------------------------|-------------------------|----------------------------------------------------------------|
| `TINYINT[(M)]`         | `INT1`                 | `Int`                   | 1-byte signed integer (`-128 .. 127`).                         |
| `TINYINT UNSIGNED`     | `INT1 UNSIGNED`        | `Int`                   | 1-byte unsigned integer (`0 .. 255`).                          |
| `BOOL`, `BOOLEAN`      | *(alias only)*         | `Boolean`               | Stored as `TINYINT(1)`; values are `0`/`1`.                    |
| `SMALLINT[(M)]`        | `INT2`                 | `Int`                   | 2-byte signed integer. Becomes `Short` if the driver reports the column class as `java.lang.Short`. |
| `SMALLINT UNSIGNED`    | `INT2 UNSIGNED`        | `Int`                   | 2-byte unsigned integer.                                       |
| `MEDIUMINT[(M)]`       | `INT3`, `MIDDLEINT`    | `Int`                   | 3-byte signed integer.                                         |
| `MEDIUMINT UNSIGNED`   | `INT3 UNSIGNED`, `MIDDLEINT UNSIGNED` | `Int`    | 3-byte unsigned integer.                                       |
| `INT[(M)]`             | `INTEGER`, `INT4`      | `Int`                   | 4-byte signed integer.                                         |
| `INT UNSIGNED`         | `INTEGER UNSIGNED`, `INT4 UNSIGNED` | `Long`     | MariaDB override: fits in `Long`, not `Int`.                   |
| `BIGINT[(M)]`          | `INT8`                 | `Long`                  | 8-byte signed integer.                                         |
| `BIGINT UNSIGNED`      | `INT8 UNSIGNED`        | `java.math.BigInteger`  | MariaDB override: exceeds `Long`, needs `BigInteger`.          |
| `DECIMAL(M,D)`         | `DEC`, `NUMERIC`, `FIXED` | `java.math.BigDecimal` | Fixed-point.                                                   |
| `FLOAT[(P)]`           | `FLOAT4`               | `Float`                 | 4-byte float. Becomes `Double` if the driver reports the column class as `java.lang.Double`. |
| `DOUBLE`               | `FLOAT8`, `DOUBLE PRECISION`, `REAL` | `Double`  | 8-byte float.                                                  |
| `BIT(M)`               | *none*                 | `ByteArray`             | Reported as `VARBINARY` for `M > 1`; `BIT(1)` may map to `Boolean`. |

## Date and time types

| Canonical           | Aliases        | DataFrame type              | Notes                                                                      |
|---------------------|----------------|-----------------------------|----------------------------------------------------------------------------|
| `DATE`              | *none*         | `java.util.Date`            |                                                                            |
| `TIME[(fsp)]`       | *none*         | `java.sql.Time`             | `fsp` is fractional seconds precision (0–6).                               |
| `DATETIME[(fsp)]`   | *none*         | `kotlin.time.Instant`       | Preprocessed from `java.sql.Timestamp`.                                    |
| `TIMESTAMP[(fsp)]`  | *none*         | `kotlin.time.Instant`       | Preprocessed from `java.sql.Timestamp`.                                    |
| `YEAR[(4)]`         | *none*         | `java.util.Date`            | 1-byte year (`1901..2155`); driver reports it as `Types.DATE`.             |

## String types

| Canonical                | Aliases                              | DataFrame type       | Notes                                                                    |
|--------------------------|--------------------------------------|----------------------|--------------------------------------------------------------------------|
| `CHAR(M)`                | `CHARACTER(M)`, `NATIONAL CHAR(M)`, `NCHAR(M)` | `String`   | Fixed-length text. Max `M = 255`.                                        |
| `VARCHAR(M)`             | `CHARACTER VARYING(M)`, `NATIONAL VARCHAR(M)`, `NVARCHAR(M)` | `String` | Variable-length text.                                            |
| `BINARY(M)`              | *none*                               | `ByteArray`          | Fixed-length binary.                                                     |
| `VARBINARY(M)`           | `BINARY VARYING(M)`                  | `ByteArray`          | Variable-length binary.                                                  |
| `TINYBLOB`               | *none*                               | `ByteArray`          | Up to 255 bytes.                                                         |
| `TINYTEXT`               | *none*                               | `String`             | Up to 255 bytes.                                                         |
| `BLOB[(M)]`              | *none*                               | `ByteArray`          | Up to 64 KiB.                                                            |
| `TEXT[(M)]`              | *none*                               | `String`             | Up to 64 KiB.                                                            |
| `MEDIUMBLOB`             | `LONG VARBINARY`                     | `ByteArray`          | Up to 16 MiB.                                                            |
| `MEDIUMTEXT`             | `LONG VARCHAR`, `LONG`               | `String`             | Up to 16 MiB.                                                            |
| `LONGBLOB`               | *none*                               | `ByteArray`          | Up to 4 GiB.                                                             |
| `LONGTEXT`               | *none*                               | `String`             | Up to 4 GiB.                                                             |
| `ENUM(...)`              | *none*                               | `String`             | Enumeration; values read as their string form.                           |
| `SET(...)`               | *none*                               | `String`             | Comma-separated set of values, as a single string.                       |

## JSON and UUID

| Canonical  | Aliases | DataFrame type          | Notes                                                                                |
|------------|---------|-------------------------|--------------------------------------------------------------------------------------|
| `JSON`     | *none*  | `String`                | Physically stored as `LONGTEXT` in MariaDB; JDBC reports `LONGVARCHAR` → `String`.   |
| `UUID`     | *none*  | `String`                | Available in MariaDB 10.7+. Read as text.                                            |
| `INET4`    | *none*  | `String`                | Available in MariaDB 10.10+.                                                         |
| `INET6`    | *none*  | `String`                | Available in MariaDB 10.10+.                                                         |
| `ROW(...)` | *none*  | *unsupported*           | Anonymous row types are not currently mapped; see [Unsupported types](#unsupported-types). |

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

## MariaDB specifics

- `INT UNSIGNED` / `INTEGER UNSIGNED` and `BIGINT UNSIGNED` are the only types that need
  MariaDB-specific handling. `INT UNSIGNED` is widened to `Long` and `BIGINT UNSIGNED`
  becomes `BigInteger` because its range exceeds `Long`.
- `SMALLINT` may become `Short` (rather than `Int`) when the JDBC driver reports the column
  class as `java.lang.Short`.
- `BIT(1)` and `BOOLEAN` are stored the same way (as `TINYINT(1)`) in older MariaDB versions;
  which Kotlin type surfaces depends on driver behavior.
- Zero-fill and display-width modifiers (e.g. `INT(11)`, `TINYINT ZEROFILL`) do not affect the
  DataFrame type.

## Unsupported types

The following types are not mapped and read into DataFrame as best effort — either as
`String` (via the driver's `getString` fallback) or as `ByteArray`. Explicit support may be
added in future releases; for now, prefer explicit `CAST(... AS TEXT)` in the query or
supply a custom converter via [a custom DbType](readSqlFromCustomDatabase.md).

- Anonymous `ROW(...)` types.
- `VECTOR` (MariaDB 11.7+).
- Native representations of spatial types (they read as raw WKB `ByteArray`).
