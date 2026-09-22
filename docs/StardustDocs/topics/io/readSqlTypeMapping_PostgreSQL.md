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
| `bit varying(n)` | `varbit` | `Any`                 | Read as the driver's `org.postgresql.util.PGobject`; call `toString()` for the bits. |

## UUID, XML, JSON

| Canonical | Aliases | DataFrame column type | Notes                                                                                 |
|-----------|---------|-----------------------|---------------------------------------------------------------------------------------|
| `uuid`    | *none*  | `Any`                 | The value is a `java.util.UUID`.                                                      |
| `xml`     | *none*  | `java.sql.SQLXML`     | The value is the driver's `org.postgresql.jdbc.PgSQLXML`.                             |
| `json`    | *none*  | `Any`                 | The value is an `org.postgresql.util.PGobject`; `toString()` gives the JSON text.     |
| `jsonb`   | *none*  | `Any`                 | The value is an `org.postgresql.util.PGobject`; `toString()` gives the JSON text.     |

## Network address types

All four are read as `Any`, holding an `org.postgresql.util.PGobject`; call `toString()` for the text form.

| Canonical  | Aliases | DataFrame column type | Notes                           |
|------------|---------|-----------------------|---------------------------------|
| `inet`     | *none*  | `Any`                 | IPv4 or IPv6 host / network.    |
| `cidr`     | *none*  | `Any`                 | IPv4 or IPv6 network.           |
| `macaddr`  | *none*  | `Any`                 | MAC address (6 bytes).          |
| `macaddr8` | *none*  | `Any`                 | MAC address (8 bytes / EUI-64). |

## Range types

Range types are read as `Any`, holding an `org.postgresql.util.PGobject`; `toString()` gives the
canonical `[lo,hi)` text form.

| Canonical                                                                                               | Aliases | DataFrame column type | Notes                                |
|---------------------------------------------------------------------------------------------------------|---------|-----------------------|--------------------------------------|
| `int4range`                                                                                             | *none*  | `Any`                 |                                      |
| `int8range`                                                                                             | *none*  | `Any`                 |                                      |
| `numrange`                                                                                              | *none*  | `Any`                 |                                      |
| `tsrange`                                                                                               | *none*  | `Any`                 |                                      |
| `tstzrange`                                                                                             | *none*  | `Any`                 |                                      |
| `daterange`                                                                                             | *none*  | `Any`                 |                                      |
| `int4multirange`, `int8multirange`, `nummultirange`, `tsmultirange`, `tstzmultirange`, `datemultirange` | *none*  | `Any`                 | Multi-ranges (PG 14+).               |

## Full-text search

| Canonical  | Aliases | DataFrame column type | Notes                 |
|------------|---------|-----------------------|-----------------------|
| `tsvector` | *none*  | `Any`                 | Text search document. |
| `tsquery`  | *none*  | `Any`                 | Text search query.    |

## Object identifiers

| Canonical                                                                                                                                                               | Aliases | DataFrame column type | Notes                                          |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|-----------------------|------------------------------------------------|
| `oid`                                                                                                                                                                   | *none*  | `Long`                | Underlying storage is 32-bit unsigned integer. |
| `regclass`                                                                                                                                                              | *none*  | `String`              | Reported as text alias.                        |
| `regconfig`, `regdictionary`, `regoper`, `regoperator`, `regproc`, `regprocedure`, `regrole`, `regnamespace`, `regtype`, `regcollation`, `regnamespace`, `regcollation` | *none*  | `String`              | Various OID aliases.                           |

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
  values come through the driver as a `PGobject` with `Types.OTHER` and fall through to the
  default handler → column type is `Any`.

## Unsupported types

The following types are not currently mapped to a dedicated Kotlin type; they are read as
`Any` / `String` or as the driver's raw form. Explicit support may be added later;

- Composite (`ROW(...)`) and user-defined composite types — a driver returns `PGobject`,
  DataFrame column type is `Any`.
- Array types (`type[]`) — read as SQL `ARRAY` and post-processed to `Array<*>`; element
  types beyond the primitives listed here are not resolved.
- [`hstore`](https://www.postgresql.org/docs/current/hstore.html) extension — driver returns
  a `Map<String, String>` wrapped in `PGobject`; a column type is `Any`.
- [PostGIS](https://postgis.net/documentation/) types (`geometry`, `geography`, ...) —
  a driver returns `PGobject`; a column type is `Any`.
- Range / multi-range internal representation — read as text.
- `bit(n)` / `bit varying(n)` — driver returns the string form `"0"`/`"1"`.
