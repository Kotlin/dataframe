[//]: # (title: H2 type mapping)

<web-summary>
Reference table of how each H2 column type is mapped to a Kotlin type when read into
a Kotlin DataFrame.
</web-summary>

<card-summary>
How H2 column types are read into DataFrame.
</card-summary>

<link-summary>
Full mapping of H2 SQL types to Kotlin types, including aliases and mode-specific behavior.
</link-summary>

The tables below list every H2 column type ([H2 Data Types](https://www.h2database.com/html/datatypes.html))
and the Kotlin type produced when the column is read into a DataFrame in `H2.Mode.Regular`.
Aliases are canonicalised by H2 at `CREATE TABLE` time, so DataFrame only ever sees the
canonical type; they are listed in the same row as the canonical type for reference.

For non-`Regular` modes, H2 delegates to the emulated dialect — see the [Mode section](#h2-modes) below.

Nullable columns produce nullable Kotlin types (`Int?` instead of `Int`).

## Character types

| Canonical               | Aliases                                                                | DataFrame type | Notes                          |
|-------------------------|------------------------------------------------------------------------|----------------|--------------------------------|
| `CHAR[(n)]`             | `CHARACTER`, `NCHAR`                                                   | `String`       | Fixed-length text.             |
| `VARCHAR[(n)]`          | `CHARACTER VARYING`, `LONGVARCHAR`, `VARCHAR2`, `NVARCHAR`, `NVARCHAR2`, `VARCHAR_CASESENSITIVE` | `String` | Variable-length text.  |
| `VARCHAR_IGNORECASE[(n)]` | *none*                                                               | `String`       | Case-insensitive comparison; stored as text.        |
| `CLOB`                  | `CHARACTER LARGE OBJECT`, `TINYTEXT`, `TEXT`, `MEDIUMTEXT`, `LONGTEXT`, `NTEXT`, `NCLOB` | `java.sql.Clob` | Large text object. |

## Binary types

| Canonical      | Aliases                                                                | DataFrame type | Notes                          |
|----------------|------------------------------------------------------------------------|----------------|--------------------------------|
| `BINARY[(n)]`  | `RAW`                                                                  | `ByteArray`    | Fixed-length binary.           |
| `VARBINARY[(n)]` | `LONGVARBINARY`, `BINARY VARYING`                                    | `ByteArray`    | Variable-length binary.        |
| `BLOB`         | `BINARY LARGE OBJECT`, `TINYBLOB`, `MEDIUMBLOB`, `LONGBLOB`, `IMAGE`   | `java.sql.Blob` | Large binary object.          |

## Boolean

| Canonical | Aliases                | DataFrame type | Notes                                    |
|-----------|------------------------|----------------|------------------------------------------|
| `BOOLEAN` | `BOOL`, `BIT`, `LOGICAL` | `Boolean`    | Single-bit boolean.                      |

## Numeric types

| Canonical                        | Aliases                                        | DataFrame type          | Notes                                          |
|----------------------------------|------------------------------------------------|-------------------------|------------------------------------------------|
| `TINYINT`                        | *none*                                         | `Int`                   | 1-byte signed integer.                         |
| `SMALLINT`                       | `INT2`, `YEAR`                                 | `Int`                   | 2-byte signed integer.                         |
| `INTEGER`                        | `INT`, `INT4`, `MEDIUMINT`, `SIGNED`           | `Int`                   | 4-byte signed integer.                         |
| `BIGINT`                         | `INT8`                                         | `Long`                  | 8-byte signed integer.                         |
| `NUMERIC[(p[,s])]`               | `DECIMAL[(p[,s])]`, `DEC[(p[,s])]`, `NUMBER[(p[,s])]` | `java.math.BigDecimal` | Fixed-point.                               |
| `REAL`                           | `FLOAT4`                                       | `Float`                 | 4-byte float.                                  |
| `DOUBLE PRECISION`               | `DOUBLE`, `FLOAT`, `FLOAT8`                    | `Double`                | 8-byte float.                                  |
| `DECFLOAT[(p)]`                  | *none*                                         | `java.math.BigDecimal`  | Decimal floating-point (H2 2.0+).              |

## Date and time types

| Canonical                                  | Aliases       | DataFrame type              | Notes                                                        |
|--------------------------------------------|---------------|-----------------------------|--------------------------------------------------------------|
| `DATE`                                     | *none*        | `java.util.Date`            |                                                              |
| `TIME[(p)] [WITHOUT TIME ZONE]`            | *none*        | `java.sql.Time`             |                                                              |
| `TIME[(p)] WITH TIME ZONE`                 | *none*        | `java.time.OffsetTime`      |                                                              |
| `TIMESTAMP[(p)] [WITHOUT TIME ZONE]`       | `DATETIME`, `SMALLDATETIME` | `kotlin.time.Instant`     | Preprocessed from `java.sql.Timestamp`.                      |
| `TIMESTAMP[(p)] WITH TIME ZONE`            | `DATETIMEOFFSET`, `TIMESTAMPTZ` | `java.time.OffsetDateTime` |                                                    |
| `INTERVAL YEAR[(p)]`, `INTERVAL MONTH[(p)]`, ... | *none*  | `String`                    | Any of the 13 interval subtypes; read as text.               |

## Other types

| Canonical            | Aliases        | DataFrame type      | Notes                                                                                    |
|----------------------|----------------|---------------------|------------------------------------------------------------------------------------------|
| `UUID`               | *none*         | `kotlin.uuid.Uuid`  | Preprocessed from `java.util.UUID`. Driver reports `Types.BINARY` + `java.util.UUID` class. |
| `JAVA_OBJECT`        | `OTHER`        | `Any`               | Serialised Java object; read as opaque `Any`.                                            |
| `ENUM`               | *none*         | `String`            | User-declared enum labels.                                                               |
| `GEOMETRY`           | *none*         | `String`            | WKT text as reported by the driver.                                                      |
| `JSON`               | *none*         | `String`            | JSON text.                                                                               |
| `ARRAY`              | *none*         | `Array<*>`          | Element type inferred; falls back to `Any` for heterogeneous arrays.                     |
| `ROW(...)`           | *none*         | *unsupported*       | See [Unsupported types](#unsupported-types).                                             |

## H2 modes

`H2(mode = ...)` selects the dialect H2 emulates and delegates its type mapping to that
dialect. The default mode is `Regular`.

| Mode           | Delegates to                                                    |
|----------------|-----------------------------------------------------------------|
| `Regular`      | Default H2 mapping (this page)                                  |
| `MySql`        | [MySQL](readSqlTypeMapping_MySQL.md)                            |
| `PostgreSql`   | [PostgreSQL](readSqlTypeMapping_PostgreSQL.md)                  |
| `MsSqlServer`  | [MS SQL](readSqlTypeMapping_MsSql.md)                           |
| `MariaDb`      | [MariaDB](readSqlTypeMapping_MariaDB.md)                        |

## H2 specifics

- H2 supports **compatibility modes** that emulate other dialects — set them via the JDBC URL
  (`;MODE=MySQL`) and construct the corresponding `H2.Mode`. In compatibility modes, H2's
  reserved-word list, function set, and type mapping change to match the emulated dialect.
- `TIMESTAMP` and `DATETIME` are canonicalised to the same `TIMESTAMP` in metadata.
- H2's `UUID` type is a first-class type (unlike PostgreSQL's, which is read as text) — the
  driver reports it as a `BINARY` JDBC type with `java.util.UUID` class, so the mapping
  produces `kotlin.uuid.Uuid` after preprocessing.
- `NUMERIC` and `DECIMAL` are identical in H2 — both map to `BigDecimal`.

## Unsupported types

- `ROW(...)` composite / structured types.
- `INTERVAL` subtypes (all 13 read as their string form).
- Domain types (H2 supports `CREATE DOMAIN`), read as the underlying primitive.
- `GEOMETRY` in binary/WKB form (WKT string is what the driver returns by default).
