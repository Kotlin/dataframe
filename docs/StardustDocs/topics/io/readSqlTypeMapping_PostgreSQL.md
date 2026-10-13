[//]: # (title: PostgreSQL type mapping)

<web-summary>
Reference table of how each PostgreSQL column type is mapped to a Kotlin type when read
into a Kotlin DataFrame.
</web-summary>

<card-summary>
How PostgreSQL column types are read into DataFrame.
</card-summary>

<link-summary>
Full mapping of PostgreSQL SQL types to Kotlin types, including aliases, PGobject overrides,
and driver caveats.
</link-summary>

The tables below list every PostgreSQL column type ([PostgreSQL Data Types](https://www.postgresql.org/docs/current/datatype.html))
and the Kotlin type produced when the column is read into a DataFrame. PostgreSQL 
canonicalizes aliases at `CREATE TABLE` time, so DataFrame only ever sees the canonical
type; they are listed in the same row as the canonical type for reference.

Column nullability is determined from the metadata provided by the JDBC driver. If the driver does not explicitly report a column as non-nullable, it is mapped to a nullable Kotlin type (`Int?` instead of `Int`).

## Numeric types

| Canonical          | Aliases          | DataFrame column type         | Notes                                              |
|--------------------|------------------|-------------------------------|----------------------------------------------------|
| `smallint`         | `int2`           | `Int`                         | 2-byte signed integer.                             |
| `integer`          | `int`, `int4`    | `Int`                         | 4-byte signed integer.                             |
| `bigint`           | `int8`           | `Long`                        | 8-byte signed integer.                             |
| `smallserial`      | `serial2`        | `Int`                         | Auto-incrementing `smallint` backed by a sequence. |
| `serial`           | `serial4`        | `Int`                         | Auto-incrementing `integer`.                       |
| `bigserial`        | `serial8`        | `Long`                        | Auto-incrementing `bigint`.                        |
| `numeric[(p,s)]`   | `decimal[(p,s)]` | `java.math.BigDecimal`        | Arbitrary-precision decimal.                       |
| `real`             | `float4`         | `Float`                       | 4-byte float.                                      |
| `double precision` | `float8`         | `Double`                      | 8-byte float.                                      |
| `money`            | *none*           | `org.postgresql.util.PGmoney` | PostgreSQL override: read as `PGmoney`.            |

## Boolean

| Canonical | Aliases | DataFrame column type | Notes |
|-----------|---------|-----------------------|-------|
| `boolean` | `bool`  | `Boolean`             |       |

## Character types

| Canonical              | Aliases      | DataFrame column type | Notes                 |
|------------------------|--------------|-----------------------|-----------------------|
| `character(n)`         | `char(n)`    | `String`              | Fixed-length text.    |
| `character varying(n)` | `varchar(n)` | `String`              | Variable-length text. |
| `text`                 | *none*       | `String`              | Unbounded text.       |

## Binary

| Canonical | Aliases | DataFrame column type | Notes                                             |
|-----------|---------|-----------------------|---------------------------------------------------|
| `bytea`   | *none*  | `ByteArray`           | Raw binary. Driver reports it as `[B` (`byte[]`). |

## Date and time types

| Canonical                             | Aliases       | DataFrame column type            | Notes                                     |
|---------------------------------------|---------------|----------------------------------|-------------------------------------------|
| `date`                                | *none*        | `java.util.Date`                 |                                           |
| `time [without time zone] [(p)]`      | *none*        | `java.sql.Time`                  | `p` is fractional-second precision (0–6). |
| `time with time zone [(p)]`           | `timetz`      | `java.sql.Time`                  | The offset is **not** kept — the driver reports the column as plain `TIME`. |
| `timestamp [without time zone] [(p)]` | *none*        | `kotlin.time.Instant`            | Preprocessed from `java.sql.Timestamp`.   |
| `timestamp with time zone [(p)]`      | `timestamptz` | `kotlin.time.Instant`            | Also preprocessed from `java.sql.Timestamp`; an `Instant` is a point in time, so no offset is carried. |
| `interval [fields] [(p)]`             | *none*        | `org.postgresql.util.PGInterval` | PostgreSQL override.                      |

Neither of the two time-zone-aware types keeps the UTC offset that was stored. A `timetz` value
loses it outright, because the driver reports the column as a plain `time`; a `timestamptz` value
keeps the instant it denotes but not the offset it was written with, because a `kotlin.time.Instant`
is a point in time. Read the offset as a separate column if it matters — for example
`SELECT timestamptzCol, to_char(timestamptzCol, 'OF') AS offset`.
{style="warning"}

## Geometric types (PostgreSQL overrides)

Case-insensitive `sqlTypeName` lookup selects a PostgreSQL-specific PGobject wrapper.

| Canonical | Aliases | DataFrame column type                | Notes          |
|-----------|---------|--------------------------------------|----------------|
| `box`     | *none*  | `org.postgresql.geometric.PGbox`     | Rectangle      |
| `circle`  | *none*  | `org.postgresql.geometric.PGcircle`  | Circle         |
| `line`    | *none*  | `org.postgresql.geometric.PGline`    | Infinite line  |
| `lseg`    | *none*  | `org.postgresql.geometric.PGlseg`    | Line segment   |
| `path`    | *none*  | `org.postgresql.geometric.PGpath`    | Open or closed |
| `point`   | *none*  | `org.postgresql.geometric.PGpoint`   | 2-D point      |
| `polygon` | *none*  | `org.postgresql.geometric.PGpolygon` | Polygon        |

## Bit strings

| Canonical        | Aliases  | DataFrame column type | Notes                                                            |
|------------------|----------|-----------------------|------------------------------------------------------------------|
| `bit(1)`         | *none*   | `Boolean`             | A single-bit column is the one case the driver really returns a `Boolean` for.       |
| `bit(n)`, `n > 1`| *none*   | `String`              | The bit string, as `"0"` and `"1"` characters, e.g. `"101"`.                         |
| `bit varying(n)` | `varbit` | `String`              | The bit string, as `"0"` and `"1"` characters, e.g. `"101"`.                         |

## UUID, XML, JSON

| Canonical | Aliases | DataFrame column type | Notes                                                                                   |
|-----------|---------|-----------------------|-----------------------------------------------------------------------------------------|
| `uuid`    | *none*  | `kotlin.uuid.Uuid`    | Preprocessed from the driver's `java.util.UUID`, as in [H2](readSqlTypeMapping_H2.md) and [DuckDB](readSqlTypeMapping_DuckDB.md). |
| `xml`     | *none*  | `java.sql.SQLXML`     | The value is the driver's `org.postgresql.jdbc.PgSQLXML`.                               |
| `json`    | *none*  | `String`              | The JSON text as it was inserted: `{"k":1}` is read as `{"k":1}`.                       |
| `jsonb`   | *none*  | `String`              | The JSON text as PostgreSQL stores it, normalized: `{"k":1}` is read as `{"k": 1}`.     |

The JSON text is not parsed: the column holds strings, not a
[frame column](DataColumn.md#framecolumn) or a [column group](DataColumn.md#columngroup). This differs
from [DuckDB](readSqlTypeMapping_DuckDB.md), which parses its `JSON` columns. To get the same result
here, call [`parse`](parse.md) on the dataframe after reading it: with the `dataframe-json` dependency
on the classpath, a column of JSON objects becomes a column group, and a column of JSON arrays becomes
a frame column.

## Network address types

All four are read as `String`, holding the address as PostgreSQL prints it.

| Canonical  | Aliases | DataFrame column type | Notes                                                       |
|------------|---------|-----------------------|-------------------------------------------------------------|
| `inet`     | *none*  | `String`              | IPv4 or IPv6 host / network, e.g. `10.0.0.1`.               |
| `cidr`     | *none*  | `String`              | IPv4 or IPv6 network, e.g. `10.0.0.0/8`.                    |
| `macaddr`  | *none*  | `String`              | MAC address (6 bytes), e.g. `08:00:2b:01:02:03`.            |
| `macaddr8` | *none*  | `String`              | MAC address (8 bytes / EUI-64), e.g. `08:00:2b:01:02:03:04:05`. |

## Range types

Range and multirange types are read as `String`, holding the range as PostgreSQL prints it. That text
can differ from the inserted one: a range of integers or dates is printed in its canonical form,
with an inclusive lower and an exclusive upper bound, so the `int4range` `(1,3]` is read as `[2,4)`.
The bounds are not parsed.

| Canonical                                                                                               | Aliases | DataFrame column type | Notes                                                                                   |
|---------------------------------------------------------------------------------------------------------|---------|-----------------------|-----------------------------------------------------------------------------------------|
| `int4range`                                                                                             | *none*  | `String`              | e.g. `[2,4)` for the inserted `(1,3]`.                                                  |
| `int8range`                                                                                             | *none*  | `String`              | e.g. `[1,2)`.                                                                           |
| `numrange`                                                                                              | *none*  | `String`              | e.g. `[1.0,2.0)`.                                                                       |
| `tsrange`                                                                                               | *none*  | `String`              | e.g. `["2024-01-01 00:00:00","2024-01-02 00:00:00")`.                                   |
| `tstzrange`                                                                                             | *none*  | `String`              | Printed in the session time zone, e.g. `["2024-01-01 00:00:00+00","2024-01-02 00:00:00+00")` in UTC. |
| `daterange`                                                                                             | *none*  | `String`              | e.g. `[2024-01-01,2024-01-03)` for the inserted `[2024-01-01,2024-01-02]`.              |
| `int4multirange`, `int8multirange`, `nummultirange`, `tsmultirange`, `tstzmultirange`, `datemultirange` | *none*  | `String`              | Multi-ranges (PG 14+), e.g. `{[1,2)}`.                                                  |

## Full-text search

Both types are read as `String`, holding the value as PostgreSQL prints it.

| Canonical  | Aliases | DataFrame column type | Notes                                                                   |
|------------|---------|-----------------------|-------------------------------------------------------------------------|
| `tsvector` | *none*  | `String`              | Text search document; the inserted `fat cat` is read as `'cat' 'fat'`.  |
| `tsquery`  | *none*  | `String`              | Text search query; the inserted `fat & cat` is read as `'fat' & 'cat'`. |

## Object identifiers

| Canonical                                                                                                                                                               | Aliases | DataFrame column type | Notes                                          |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|-----------------------|------------------------------------------------|
| `oid`                                                                                                                                                                   | *none*  | `Long`                | Underlying storage is 32-bit unsigned integer. |
| `regclass`                                                                                                                                                              | *none*  | `String`              | Reported as text alias.                        |
| `regconfig`, `regdictionary`, `regoper`, `regoperator`, `regproc`, `regprocedure`, `regrole`, `regnamespace`, `regtype`, `regcollation`                               | *none*  | `String`              | Various OID aliases.                           |

## Other

| Canonical                      | Aliases | DataFrame column type  | Notes                                                           |
|--------------------------------|---------|------------------------|-----------------------------------------------------------------|
| `pg_lsn`                       | *none*  | `String`               | Write-Ahead Log sequence number.                                |
| `pg_snapshot`, `txid_snapshot` | *none*  | `String`               | Snapshot info.                                                  |
| user-defined `ENUM`            | *none*  | `String`               | User-declared enum types are read as their string label.        |
| user-defined `DOMAIN`          | *none*  | as the underlying type | Domains are transparent; the underlying type's mapping applies. |

## PostgreSQL specifics

- Type name lookup for PGobject types (`box`, `point`, `money`, ...) is case-insensitive.
- User-defined [`DOMAIN`](https://www.postgresql.org/docs/current/domains.html) types are
  transparent at the JDBC layer — the underlying primitive's mapping applies.
- Auto-incrementing [`SERIAL` variants](https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-SERIAL)
  are `INTEGER` / `BIGINT` in the metadata — the sequence is a server-side default, not a
  separate JDBC type.
- Composite / anonymous [`ROW(...)`](https://www.postgresql.org/docs/current/rowtypes.html)
  values come through the driver as a `PGobject`, and the column type is `Any`.
- H2 in PostgreSQL mode uses the PostgreSQL type mapping described here, but reads its own `JSON` column as a
  `ByteArray`, as described on the [H2 page](readSqlTypeMapping_H2.md): the H2 driver returns the JSON
  text as raw bytes.

## Unsupported types

The following types are not currently mapped to a dedicated Kotlin type; they are read as
`Any` / `String` or as the driver's raw form. Explicit support may be added later.

- Composite (`ROW(...)`) and user-defined composite types — a driver returns `PGobject`,
  DataFrame column type is `Any`.
- Array types (`type[]`) — read as SQL `ARRAY` and post-processed to `Array<*>`; element
  types beyond the primitives listed here are not resolved.
- [`hstore`](https://www.postgresql.org/docs/current/hstore.html) extension — driver returns
  a `Map<String, String>` wrapped in `PGobject`; a column type is `Any`.
- [PostGIS](https://postgis.net/documentation/) types (`geometry`, `geography`, ...) —
  a driver returns `PGobject`; a column type is `Any`.
