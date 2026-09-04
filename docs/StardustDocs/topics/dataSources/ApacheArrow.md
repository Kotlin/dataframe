# Apache Arrow

<web-summary>
Read and write Apache Arrow files in Kotlin — efficient binary format support with Kotlin DataFrame.
</web-summary>

<card-summary>
Work with Arrow files in Kotlin for fast I/O — supports both streaming and random access formats.
</card-summary>

<link-summary>
Kotlin DataFrame provides full support for reading and writing Apache Arrow files in high-performance workflows.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.io.ApacheArrow-->

Kotlin DataFrame supports reading from and writing to Apache Arrow files.

Requires the [`dataframe-arrow` module](Modules.md#dataframe-arrow), which is included by 
default in the general [`dataframe`](Modules.md#dataframe-general) artifact 
and in [`%use dataframe`](SetupKotlinNotebook.md#integrate-kotlin-dataframe) for Kotlin Notebook.

> Make sure to follow the 
> [Apache Arrow Java compatibility guide](https://arrow.apache.org/docs/java/install.html#java-compatibility) 
> when using Java 9+.
> {style="warning"}

> Nested Arrow `Struct` columns are read as a [`ColumnGroup`](DataColumn.md#columngroup). An **optional
> (nullable)** struct is read as a `ColumnGroup` whose child columns become nullable, holding `null` in the rows
> where the struct is absent. A `ColumnGroup` is never `null` per row, so an absent struct and a present struct
> with all-`null` children are represented the same way. See [Parquet](Parquet.md) for details.
> {style="note"}

## Read

[`DataFrame`](DataFrame.md) supports both the 
[Arrow interprocess streaming format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-streaming-format) 
and the [Arrow random access format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-random-access-files).

You can read a `DataFrame` from Apache Arrow data sources 
(via a file path, URL, or stream) using the [`readArrowFeather()`](read.md#read-apache-arrow-formats) method:

<!---FUN readArrowFeather-->

```kotlin
val df = DataFrame.readArrowFeather("example.feather")
```

<!---END-->

<!---FUN readArrowFeatherViaUrl-->

```kotlin
val df = DataFrame.readArrowFeather("https://kotlin.github.io/dataframe/resources/example.feather")
```

<!---END-->

## Write

A [`DataFrame`](DataFrame.md) can be written to Arrow format using the interprocess streaming or random access format. 
Output targets include `WritableByteChannel`, `OutputStream`, `File`, or `ByteArray`.

See [](write.md#writing-to-apache-arrow-formats) for more details.

## Type mapping

### Reading

| Arrow type | Kotlin type |
|------------|-------------|
| `Null` | `Nothing?` |
| `Bool` | `Boolean` |
| `Int(8, signed)` / `Int(16, signed)` / `Int(32, signed)` / `Int(64, signed)` | `Byte` / `Short` / `Int` / `Long` |
| `Int(8, unsigned)` / `Int(16, unsigned)` / `Int(32, unsigned)` / `Int(64, unsigned)` | `Short` / `Int` / `Long` / `BigInteger` |
| `FloatingPoint(SINGLE)` / `FloatingPoint(DOUBLE)` | `Float` / `Double` |
| `Decimal` (128- and 256-bit) | `BigDecimal` |
| `Utf8`, `LargeUtf8`, `Utf8View` | `String` |
| `Binary`, `LargeBinary`, `BinaryView` | `ByteArray` |
| `Date(DAY)` | [`kotlinx.datetime.LocalDate`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-local-date/) |
| `Date(MILLISECOND)` | [`kotlinx.datetime.LocalDateTime`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-local-date-time/) |
| `Time(SECOND / MILLISECOND / MICROSECOND / NANOSECOND)` | [`kotlinx.datetime.LocalTime`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-local-time/) |
| `Timestamp(unit, null)` — no time zone | [`kotlinx.datetime.LocalDateTime`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-local-date-time/) |
| `Timestamp(unit, tz)` — with a time zone | [`kotlin.time.Instant`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/-instant/) |
| `Duration` | `kotlin.time.Duration` |
| `Struct` | [`ColumnGroup`](DataColumn.md#columngroup) |
| `List`, `LargeList` | `List<T>`, or a [`FrameColumn`](DataColumn.md#framecolumn) for a list of structs |

Anything else raises `NotImplementedError`. Column nullability comes from the `nullability` argument
(`NullabilityOptions.Infer` by default, which marks a column nullable only if it actually contains nulls).

A timestamp **with** a time zone is an offset from `1970-01-01T00:00:00Z` and so identifies a single point on the
time-line, which is why it becomes an `Instant`; a timestamp **without** one is a bare calendar-and-clock reading
that identifies no such point, and stays a `LocalDateTime`. This is also how Parquet's `isAdjustedToUTC` flag is
mapped — see [](Parquet.md#timestamps-and-time-zones).

> The zone in `Timestamp(unit, tz)` is display metadata: the stored values are already normalized to UTC, so two
> columns describing the same instants read back equal no matter which zone names they carry. Use
> `convert { … }.with { it.toLocalDateTime(zone) }` to get wall-clock values in a zone you pick.
> {style="note"}

### Writing

| Kotlin type | Arrow type |
|-------------|------------|
| `Nothing?` | `Null` |
| `String` | `Utf8` |
| `Boolean` | `Bool` |
| `Byte` / `Short` / `Int` / `Long` | `Int(8 / 16 / 32 / 64, signed)` |
| `Float` / `Double` | `FloatingPoint(SINGLE)` / `FloatingPoint(DOUBLE)` |
| `LocalDate` (`kotlinx.datetime` or `java.time`) | `Date(DAY)` |
| `LocalDateTime` (`kotlinx.datetime` or `java.time`) | `Date(MILLISECOND)` |
| `LocalTime` (`kotlinx.datetime` or `java.time`) | `Time(NANOSECOND)` |
| `Instant` (`kotlin.time` or `java.time`) | `Timestamp(MICROSECOND, "UTC")` |
| [`ColumnGroup`](DataColumn.md#columngroup) | `Struct` |
| [`FrameColumn`](DataColumn.md#framecolumn) | `List` of `Struct` |

Any other type is written as `Utf8` (its `toString()`), reported through the `ConvertingMismatch` subscriber.
When you supply an explicit target `Schema`, `Timestamp` fields are also accepted in every unit, with or without
a time zone, and the column is converted accordingly.

> An `Instant` is written at **microsecond** precision, so an instant carrying nanoseconds loses its last three
> digits — reported as `ConvertingMismatch.PrecisionReduced`. The unit is microseconds rather than nanoseconds for
> range: an Arrow nanosecond timestamp is an `int64` count of nanoseconds since the epoch and therefore only spans
> 1677–2262. Pass a target `Schema` with `Timestamp(NANOSECOND, "UTC")` when you need the full precision and your
> data stays inside that window.
> {style="note"}

Conversions between local date-times and instants are always resolved against **UTC**, never against the JVM's
default time zone, so the same [`DataFrame`](DataFrame.md) always writes the same bytes.
