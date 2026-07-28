[//]: # (title: DuckDB type mapping)

<web-summary>
Reference table of how each DuckDB column type is mapped to a Kotlin type when read into
a Kotlin DataFrame.
</web-summary>

<card-summary>
How DuckDB column types are read into DataFrame, including nested types.
</card-summary>

<link-summary>
Full mapping of DuckDB SQL types to Kotlin types, including aliases and nested-type support.
</link-summary>

DuckDB uses a dedicated converter that bypasses `DbType.getExpectedJdbcType` and matches
directly on DuckDB's type names (see `DuckDb.parseDuckDbType`).
The tables below list every DuckDB column type
([DuckDB Data Types](https://duckdb.org/docs/sql/data_types/overview)) and the resulting
Kotlin type. Aliases are canonicalised by DuckDB at `CREATE TABLE` time, so DataFrame only
ever sees the canonical type; they are listed in the same row as the canonical type for
reference.

Nullable columns produce nullable Kotlin types (`Int?` instead of `Int`).

## Boolean

| Canonical | Aliases            | DataFrame type | Notes |
|-----------|--------------------|----------------|-------|
| `BOOLEAN` | `BOOL`, `LOGICAL`  | `Boolean`      |       |

## Signed integer types

| Canonical    | Aliases          | DataFrame type          | Notes                                  |
|--------------|------------------|-------------------------|----------------------------------------|
| `TINYINT`    | `INT1`           | `Byte`                  | 1-byte signed.                         |
| `SMALLINT`   | `INT2`, `SHORT`  | `Short`                 | 2-byte signed.                         |
| `INTEGER`    | `INT4`, `INT`, `SIGNED` | `Int`            | 4-byte signed.                         |
| `BIGINT`     | `INT8`, `LONG`   | `Long`                  | 8-byte signed.                         |
| `HUGEINT`    | *none*           | `java.math.BigInteger`  | 16-byte signed (`-2^127 .. 2^127 - 1`).|

## Unsigned integer types

| Canonical    | Aliases | DataFrame type          | Notes                                          |
|--------------|---------|-------------------------|------------------------------------------------|
| `UTINYINT`   | *none*  | `Short`                 | 1-byte unsigned; widened to `Short`.           |
| `USMALLINT`  | *none*  | `Int`                   | 2-byte unsigned; widened to `Int`.             |
| `UINTEGER`   | *none*  | `Long`                  | 4-byte unsigned; widened to `Long`.            |
| `UBIGINT`    | *none*  | `java.math.BigInteger`  | 8-byte unsigned; exceeds `Long`.               |
| `UHUGEINT`   | *none*  | `java.math.BigInteger`  | 16-byte unsigned.                              |

## Floating-point and fixed-point

| Canonical     | Aliases                             | DataFrame type          | Notes                                          |
|---------------|-------------------------------------|-------------------------|------------------------------------------------|
| `FLOAT`       | `FLOAT4`, `REAL`                    | `Float`                 | 4-byte float.                                  |
| `DOUBLE`      | `FLOAT8`, `DOUBLE PRECISION`        | `Double`                | 8-byte float.                                  |
| `DECIMAL(p,s)`| `NUMERIC(p,s)`                      | `java.math.BigDecimal`  | Fixed-point; `NUMERIC` without precision defaults to `DECIMAL(18,3)`. |

## String and binary

| Canonical   | Aliases                                         | DataFrame type   | Notes                                                                    |
|-------------|-------------------------------------------------|------------------|--------------------------------------------------------------------------|
| `VARCHAR`   | `CHAR`, `BPCHAR`, `TEXT`, `STRING`              | `String`         | Variable-length text.                                                    |
| `BLOB`      | `BYTEA`, `BINARY`, `VARBINARY`                  | `java.sql.Blob`  | Binary large object.                                                     |
| `BIT`       | *none*                                          | `String`         | Fixed-length bit string; read as text.                                   |

## Date and time

| Canonical                    | Aliases                            | DataFrame type              | Notes                                                        |
|------------------------------|------------------------------------|-----------------------------|--------------------------------------------------------------|
| `DATE`                       | *none*                             | `kotlinx.datetime.LocalDate`| Preprocessed from `java.time.LocalDate`.                     |
| `TIME`                       | *none*                             | `kotlinx.datetime.LocalTime`| Preprocessed from `java.time.LocalTime`.                     |
| `TIME_NS`                    | *none*                             | `kotlinx.datetime.LocalTime`| Nanosecond-precision time.                                   |
| `TIME WITH TIME ZONE`        | `TIMETZ`                           | `java.time.OffsetTime`      |                                                              |
| `TIMESTAMP`                  | `DATETIME`                         | `kotlin.time.Instant`       | Preprocessed from `java.sql.Timestamp`.                      |
| `TIMESTAMP_MS`               | *none*                             | `kotlin.time.Instant`       | Millisecond-precision timestamp.                             |
| `TIMESTAMP_NS`               | *none*                             | `kotlin.time.Instant`       | Nanosecond-precision timestamp.                              |
| `TIMESTAMP_S`                | *none*                             | `kotlin.time.Instant`       | Second-precision timestamp.                                  |
| `TIMESTAMP WITH TIME ZONE`   | `TIMESTAMPTZ`                      | `java.time.OffsetDateTime`  | Timestamp with time zone.                                    |
| `INTERVAL`                   | *none*                             | `String`                    | Read as text.                                                |

## Complex / nested types

DuckDB supports nested / composite types, which the DataFrame converter resolves recursively.

| Canonical                    | Aliases | DataFrame type                     | Notes                                                                                     |
|------------------------------|---------|------------------------------------|-------------------------------------------------------------------------------------------|
| `LIST` (`T[]`)               | *none*  | `List<T>`                          | Element type is resolved recursively.                                                     |
| `ARRAY` (`T[N]`)             | *none*  | `List<T>`                          | Same as `LIST`; the element type is resolved recursively.                                 |
| `MAP(K, V)`                  | *none*  | `Map<K, V>`                        | Key and value types are resolved recursively.                                             |
| `STRUCT(...)`                | *none*  | column group (`DataRow`)           | Fields resolved recursively; array-of-struct becomes a frame column.                      |
| `UNION`                      | *none*  | `Any`                              | Not currently unpacked in Kotlin.                                                         |
| `VARIANT`                    | *none*  | `Any`                              | Not currently unpacked in Kotlin.                                                         |

## Other

| Canonical    | Aliases | DataFrame type       | Notes                                                                                    |
|--------------|---------|----------------------|------------------------------------------------------------------------------------------|
| `UUID`       | *none*  | `kotlin.uuid.Uuid`   | Preprocessed from `java.util.UUID`.                                                      |
| `JSON`       | *none*  | `String` (parsed)    | Read as JSON text and passed through `tryParse` + type inference.                        |
| `ENUM`       | *none*  | `String`             | Read as the enum label.                                                                  |
| `GEOMETRY`   | *none*  | `java.sql.Blob`      | Binary WKB read as `Blob`.                                                               |
| `UNKNOWN`    | *none*  | `String`             | Fallback for anything not recognised by the converter.                                   |

## DuckDB specifics

- DuckDB bypasses `DbType.getExpectedJdbcType` — its type resolution goes through
  `DuckDb.parseDuckDbType` and matches directly on the DuckDB type name.
- Nested types (`LIST`, `ARRAY`, `MAP`, `STRUCT`) are resolved recursively: `INTEGER[]`
  becomes `List<Int>`, `MAP(VARCHAR, INTEGER)` becomes `Map<String, Int>`, and so on.
- `STRUCT(...)` becomes a **column group** (`DataRow`); arrays of `STRUCT` become **frame
  columns** (`DataFrame`).
- Reading a DuckDB `JSON` column parses the text and re-infers the resulting DataFrame's
  type — the column ends up as whatever the parser detected.

## Unsupported types

- `UNION` and `VARIANT` values are read as raw `Any` — the payload's actual type is not
  unpacked in Kotlin.
- `INTERVAL` values are read as their text form; no `Duration` conversion is applied.
- Extension types (spatial via `duckdb_spatial`, full-text search, etc.) are read as the
  extension's underlying storage type (usually `Blob` or `String`).
