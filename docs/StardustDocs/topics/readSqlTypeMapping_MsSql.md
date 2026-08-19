[//]: # (title: MS SQL Server type mapping)

<web-summary>
Reference table of how each MS SQL Server column type is mapped to a Kotlin type when read
into a Kotlin DataFrame.
</web-summary>

<card-summary>
How MS SQL Server column types are read into DataFrame.
</card-summary>

<link-summary>
Full mapping of MS SQL Server SQL types to Kotlin types, including aliases and driver caveats.
</link-summary>

The tables below list every MS SQL Server column type
([Transact-SQL Data types](https://learn.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql))
and the Kotlin type produced when the column is read into a DataFrame. Aliases are
canonicalised by SQL Server at `CREATE TABLE` time, so DataFrame only ever sees the canonical
type; they are listed in the same row as the canonical type for reference.

Nullable columns produce nullable Kotlin types (`Int?` instead of `Int`).

## Exact numeric types

| Canonical           | Aliases                | DataFrame column type          | Notes                                                                                     |
|---------------------|------------------------|-------------------------|-------------------------------------------------------------------------------------------|
| `bit`               | *none*                 | `Boolean`               | Single-bit integer (`0`/`1`/`NULL`).                                                      |
| `tinyint`           | *none*                 | `Int`                   | 1-byte **unsigned** integer (`0..255`) — unusual: SQL Server's `tinyint` is unsigned.     |
| `smallint`          | *none*                 | `Int`                   | 2-byte signed integer.                                                                    |
| `int`               | `integer`              | `Int`                   | 4-byte signed integer.                                                                    |
| `bigint`            | *none*                 | `Long`                  | 8-byte signed integer.                                                                    |
| `decimal(p,s)`      | `dec(p,s)`, `numeric(p,s)` | `java.math.BigDecimal` | Fixed-point. `NUMERIC` and `DECIMAL` are stored identically.                          |
| `smallmoney`        | *none*                 | `java.math.BigDecimal`  | 4-byte fixed-point money (`-214,748.3648 .. 214,748.3647`).                               |
| `money`             | *none*                 | `java.math.BigDecimal`  | 8-byte fixed-point money.                                                                 |

## Approximate numeric types

| Canonical             | Aliases                            | DataFrame column type | Notes                                                    |
|-----------------------|------------------------------------|----------------|----------------------------------------------------------|
| `float(n)`            | `double precision` (n = 53)        | `Double`       | Default `n = 53`. `float(1..24)` maps to `Float`.        |
| `real`                | *(alias)* `float(24)`              | `Float`        | Explicit 4-byte float; equivalent to `float(24)`.        |

## Date and time types

| Canonical               | Aliases | DataFrame column type              | Notes                                                                     |
|-------------------------|---------|-----------------------------|---------------------------------------------------------------------------|
| `date`                  | *none*  | `java.util.Date`            | Date only (`0001-01-01 .. 9999-12-31`).                                   |
| `time(n)`               | *none*  | `java.sql.Time`             | Time only with fractional-second precision (0–7).                         |
| `smalldatetime`         | *none*  | `kotlin.time.Instant`       | Date + minute-precision time.                                             |
| `datetime`              | *none*  | `kotlin.time.Instant`       | Legacy date + time; ~3.33 ms precision.                                   |
| `datetime2(n)`          | *none*  | `kotlin.time.Instant`       | Modern date + time with fractional-second precision (0–7).                |
| `datetimeoffset(n)`     | *none*  | `java.time.OffsetDateTime`  | Date + time + explicit UTC offset.                                        |

## Character strings

| Canonical             | Aliases                                   | DataFrame column type | Notes                                                              |
|-----------------------|-------------------------------------------|----------------|--------------------------------------------------------------------|
| `char(n)`             | `character(n)`                            | `String`       | Fixed-length non-Unicode text (max `n = 8000`).                    |
| `varchar(n | max)`    | `character varying(n)`                    | `String`       | Variable-length non-Unicode text.                                  |
| `text`                | *none*                                    | `String`       | **Deprecated** — use `varchar(max)`.                               |
| `nchar(n)`            | `national character(n)`, `national char(n)` | `String`     | Fixed-length UTF-16 text (max `n = 4000`).                         |
| `nvarchar(n | max)`   | `national character varying(n)`, `national char varying(n)` | `String` | Variable-length UTF-16 text.                                 |
| `ntext`               | `national text`                           | `String`       | **Deprecated** — use `nvarchar(max)`.                              |

## Binary strings

| Canonical             | Aliases                          | DataFrame column type | Notes                                              |
|-----------------------|----------------------------------|----------------|----------------------------------------------------|
| `binary(n)`           | *none*                           | `ByteArray`    | Fixed-length binary (max `n = 8000`).              |
| `varbinary(n | max)`  | `binary varying(n)`              | `ByteArray`    | Variable-length binary.                            |
| `image`               | *none*                           | `ByteArray`    | **Deprecated** — use `varbinary(max)`.             |

## Row-version and identity

| Canonical         | Aliases       | DataFrame column type | Notes                                                                                                     |
|-------------------|---------------|----------------|-----------------------------------------------------------------------------------------------------------|
| `rowversion`      | `timestamp`   | `ByteArray`    | 8-byte row-version. **Note:** SQL Server's `timestamp` is *not* a date/time type. Use `datetime2` instead. |
| `uniqueidentifier`| *none*        | `String`       | 16-byte GUID, read as its 36-character string form.                                                       |

## Other types

| Canonical     | Aliases | DataFrame column type | Notes                                                                      |
|---------------|---------|-----------------------|----------------------------------------------------------------------------|
| `xml`         | *none*  | `String`              | XML document text.                                                         |
| `sql_variant` | *none*  | `Any`                 | Column holds a value of any base type; driver returns as generic `Object`. |
| `hierarchyid` | *none*  | `ByteArray`           | Hierarchical position (raw binary).                                        |
| `geometry`    | *none*  | `ByteArray`           | Planar spatial (WKB / EWKB).                                               |
| `geography`   | *none*  | `ByteArray`           | Geodetic spatial (WKB / EWKB).                                             |

## MS SQL Server specifics

- Unlike most databases, SQL Server's [`tinyint`](https://learn.microsoft.com/en-us/sql/t-sql/data-types/int-bigint-smallint-and-tinyint-transact-sql)
  is **unsigned** (`0..255`), not signed.
- The keyword `TIMESTAMP` in T-SQL is a synonym for
  [`rowversion`](https://learn.microsoft.com/en-us/sql/t-sql/data-types/rowversion-transact-sql)
  (a row-version stamp), **not** a date/time type. Use
  [`datetime2`](https://learn.microsoft.com/en-us/sql/t-sql/data-types/datetime2-transact-sql)
  for wall-clock timestamps.
- [`text`, `ntext`, and `image`](https://learn.microsoft.com/en-us/sql/t-sql/data-types/ntext-text-and-image-transact-sql)
  are deprecated; SQL Server documentation recommends `varchar(max)` / `nvarchar(max)` /
  `varbinary(max)` respectively.
- [`float(n)`](https://learn.microsoft.com/en-us/sql/t-sql/data-types/float-and-real-transact-sql)
  with `n <= 24` returns `Float`; `n >= 25` returns `Double`. `float` alone means `float(53)`
  and returns `Double`. `real` is `float(24)` and returns `Float`.

## Unsupported types

- [`geometry`, `geography`](https://learn.microsoft.com/en-us/sql/t-sql/spatial-geometry/spatial-types-geometry-transact-sql);
  read as raw WKB `ByteArray`.
- [`hierarchyid`](https://learn.microsoft.com/en-us/sql/t-sql/data-types/hierarchyid-data-type-method-reference);
  read as raw `ByteArray`.
- [`sql_variant`](https://learn.microsoft.com/en-us/sql/t-sql/data-types/sql-variant-transact-sql);
  read as generic `Any`.
- [`cursor`](https://learn.microsoft.com/en-us/sql/t-sql/data-types/cursor-transact-sql) and
  [`table`](https://learn.microsoft.com/en-us/sql/t-sql/data-types/table-transact-sql) types
  cannot be columns; they only occur as procedure parameters.
