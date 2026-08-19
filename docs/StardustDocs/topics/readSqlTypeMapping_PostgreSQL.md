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
and the Kotlin type produced when the column is read into a DataFrame. Aliases are
canonicalized by PostgreSQL at `CREATE TABLE` time, so DataFrame only ever sees the canonical
type; they are listed in the same row as the canonical type for reference.

Nullable columns produce nullable Kotlin types (`Int?` instead of `Int`).

## Numeric types

| Canonical           | Aliases            | DataFrame column type          | Notes                                                                                     |
|---------------------|--------------------|-------------------------|-------------------------------------------------------------------------------------------|
| `smallint`          | `int2`             | `Int`                   | 2-byte signed integer.                                                                    |
| `integer`           | `int`, `int4`      | `Int`                   | 4-byte signed integer.                                                                    |
| `bigint`            | `int8`             | `Long`                  | 8-byte signed integer.                                                                    |
| `smallserial`       | `serial2`          | `Int`                   | Auto-incrementing `smallint` backed by a sequence.                                        |
| `serial`            | `serial4`          | `Int`                   | Auto-incrementing `integer`.                                                              |
| `bigserial`         | `serial8`          | `Long`                  | Auto-incrementing `bigint`.                                                               |
| `numeric[(p,s)]`    | `decimal[(p,s)]`   | `java.math.BigDecimal`  | Arbitrary-precision decimal.                                                              |
| `real`              | `float4`           | `Float`                 | 4-byte float.                                                                             |
| `double precision`  | `float8`           | `Double`                | 8-byte float.                                                                             |
| `money`             | *none*             | `org.postgresql.util.PGmoney` | PostgreSQL override: read as `PGmoney`.                                             |

## Boolean

| Canonical | Aliases | DataFrame column type | Notes |
|-----------|---------|----------------|-------|
| `boolean` | `bool`  | `Boolean`      |       |

## Character types

| Canonical            | Aliases                   | DataFrame column type | Notes                          |
|----------------------|---------------------------|----------------|--------------------------------|
| `character(n)`       | `char(n)`                 | `String`       | Fixed-length text.             |
| `character varying(n)` | `varchar(n)`            | `String`       | Variable-length text.          |
| `text`               | *none*                    | `String`       | Unbounded text.                |

## Binary

| Canonical | Aliases | DataFrame column type | Notes                                                                       |
|-----------|---------|----------------|-----------------------------------------------------------------------------|
| `bytea`   | *none*  | `ByteArray`    | Raw binary. Driver reports it as `[B` (`byte[]`).                           |

## Date and time types

| Canonical                             | Aliases          | DataFrame column type              | Notes                                                                     |
|---------------------------------------|------------------|-----------------------------|---------------------------------------------------------------------------|
| `date`                                | *none*           | `java.util.Date`            |                                                                           |
| `time [without time zone] [(p)]`      | *none*           | `java.sql.Time`             | `p` is fractional-second precision (0–6).                                 |
| `time with time zone [(p)]`           | `timetz`         | `java.time.OffsetTime`      |                                                                           |
| `timestamp [without time zone] [(p)]` | *none*           | `kotlin.time.Instant`       | Preprocessed from `java.sql.Timestamp`.                                   |
| `timestamp with time zone [(p)]`      | `timestamptz`    | `java.time.OffsetDateTime`  |                                                                           |
| `interval [fields] [(p)]`             | *none*           | `org.postgresql.util.PGInterval` | PostgreSQL override.                                                 |

## Geometric types (PostgreSQL overrides)

Case-insensitive `sqlTypeName` lookup selects a PostgreSQL-specific PGobject wrapper.

| Canonical | Aliases | DataFrame column type                              | Notes            |
|-----------|---------|---------------------------------------------|------------------|
| `box`     | *none*  | `org.postgresql.geometric.PGbox`            | Rectangle        |
| `circle`  | *none*  | `org.postgresql.geometric.PGcircle`         | Circle           |
| `line`    | *none*  | `org.postgresql.geometric.PGline`           | Infinite line    |
| `lseg`    | *none*  | `org.postgresql.geometric.PGlseg`           | Line segment     |
| `path`    | *none*  | `org.postgresql.geometric.PGpath`           | Open or closed   |
| `point`   | *none*  | `org.postgresql.geometric.PGpoint`          | 2-D point        |
| `polygon` | *none*  | `org.postgresql.geometric.PGpolygon`        | Polygon          |

## Bit strings

| Canonical         | Aliases   | DataFrame column type | Notes                                                                    |
|-------------------|-----------|----------------|--------------------------------------------------------------------------|
| `bit(n)`          | *none*    | `String`       | Reported by the driver as `String` — `"0"` and `"1"` characters.         |
| `bit varying(n)`  | `varbit`  | `String`       |                                                                          |

## UUID, XML, JSON

| Canonical | Aliases | DataFrame column type | Notes                                                                             |
|-----------|---------|----------------|-----------------------------------------------------------------------------------|
| `uuid`    | *none*  | `String`       | Read as text by default. Use a client-side parser to get `kotlin.uuid.Uuid`.      |
| `xml`     | *none*  | `String`       |                                                                                   |
| `json`    | *none*  | `String`       | Raw JSON text.                                                                    |
| `jsonb`   | *none*  | `String`       | Binary-stored JSON, read as text.                                                 |

## Network address types

| Canonical    | Aliases | DataFrame column type | Notes                                        |
|--------------|---------|----------------|----------------------------------------------|
| `inet`       | *none*  | `String`       | IPv4 or IPv6 host / network.                 |
| `cidr`       | *none*  | `String`       | IPv4 or IPv6 network.                        |
| `macaddr`    | *none*  | `String`       | MAC address (6 bytes).                       |
| `macaddr8`   | *none*  | `String`       | MAC address (8 bytes / EUI-64).              |

## Range types

Range types are read as `String` (their canonical `[lo,hi)` text form). Parse client-side.

| Canonical      | Aliases | DataFrame column type | Notes                                                             |
|----------------|---------|----------------|-------------------------------------------------------------------|
| `int4range`    | *none*  | `String`       |                                                                   |
| `int8range`    | *none*  | `String`       |                                                                   |
| `numrange`     | *none*  | `String`       |                                                                   |
| `tsrange`      | *none*  | `String`       |                                                                   |
| `tstzrange`    | *none*  | `String`       |                                                                   |
| `daterange`    | *none*  | `String`       |                                                                   |
| `int4multirange`, `int8multirange`, `nummultirange`, `tsmultirange`, `tstzmultirange`, `datemultirange` | *none* | `String` | Multi-ranges (PG 14+). Read as text. |

## Full-text search

| Canonical  | Aliases | DataFrame column type | Notes                          |
|------------|---------|----------------|--------------------------------|
| `tsvector` | *none*  | `String`       | Text search document.          |
| `tsquery`  | *none*  | `String`       | Text search query.             |

## Object identifiers

| Canonical          | Aliases | DataFrame column type | Notes                                              |
|--------------------|---------|----------------|----------------------------------------------------|
| `oid`              | *none*  | `Long`         | Underlying storage is 32-bit unsigned integer.     |
| `regclass`         | *none*  | `String`       | Reported as text alias.                            |
| `regconfig`, `regdictionary`, `regoper`, `regoperator`, `regproc`, `regprocedure`, `regrole`, `regnamespace`, `regtype`, `regcollation`, `regnamespace`, `regcollation` | *none* | `String` | Various OID aliases. |

## Other

| Canonical            | Aliases   | DataFrame column type          | Notes                                                                                                               |
|----------------------|-----------|-------------------------|---------------------------------------------------------------------------------------------------------------------|
| `pg_lsn`             | *none*    | `String`                | Write-Ahead Log sequence number.                                                                                    |
| `pg_snapshot`, `txid_snapshot` | *none* | `String`             | Snapshot info.                                                                                                      |
| user-defined `ENUM`  | *none*    | `String`                | User-declared enum types are read as their string label.                                                            |
| user-defined `DOMAIN`| *none*    | as the underlying type  | Domains are transparent; the underlying type's mapping applies.                                                     |

## PostgreSQL specifics

- Type name lookup for PGobject types (`box`, `point`, `money`, ...) is case-insensitive.
- User-defined [`DOMAIN`](https://www.postgresql.org/docs/current/domains.html) types are
  transparent at the JDBC layer — the underlying primitive's mapping applies.
- Auto-incrementing [`SERIAL` variants](https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-SERIAL)
  are `INTEGER` / `BIGINT` in the metadata — the sequence is a server-side default, not a
  separate JDBC type.
- Composite / anonymous [`ROW(...)`](https://www.postgresql.org/docs/current/rowtypes.html)
  values come through the driver as a `PGobject` with `Types.OTHER` and fall through to the
  default handler → column type is `Any`. Cast to text in your query
  (e.g. `SELECT (row_col)::text`) or supply a [custom converter](readSqlFromCustomDatabase.md).

## Unsupported types

The following types are not currently mapped to a dedicated Kotlin type; they are read as
`Any` / `String` or as the driver's raw form. Explicit support may be added later; for now,
cast in your query (e.g. `SELECT ST_AsText(geom)` / `field::text`) or supply a custom
converter via [a custom DbType](readSqlFromCustomDatabase.md).

- Composite (`ROW(...)`) and user-defined composite types — driver returns `PGobject`,
  DataFrame column type is `Any`.
- Array types (`type[]`) — read as SQL `ARRAY` and post-processed to `Array<*>`; element
  types beyond the primitives listed here are not resolved.
- [`hstore`](https://www.postgresql.org/docs/current/hstore.html) extension — driver returns
  a `Map<String, String>` wrapped in `PGobject`; column type is `Any`.
- [PostGIS](https://postgis.net/documentation/) types (`geometry`, `geography`, ...) —
  driver returns `PGobject`; column type is `Any`.
- Range / multi-range internal representation — read as text.
- `bit(n)` / `bit varying(n)` — driver returns the string form `"0"`/`"1"`.
