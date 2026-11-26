[//]: # (title: convert)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ConvertSamples-->

<web-summary>
Discover the `convert` operation for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover the `convert` operation for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover the `convert` operation for Kotlin DataFrame.
</link-summary>

Returns a [`DataFrame`](DataFrame.md) with changed values in some columns. Allows changing column types.

**API reference**: [`convert`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/convert.html)

```text
convert { columnsSelector }
    .cast<Type>() // optional: adjusts the static type before the terminal step
    .with { rowConverter } | .notNull { rowExpression } | .perRowCol { rowColExpression }
    | .asFrame { frameExpression } | .asColumn { colExpression }
    | .to<Type>() | .toDataFrames()

rowConverter = AddDataRow.(OldValue) -> NewValue
rowExpression = DataRow.(OldValue) -> NewValue
rowColExpression = (DataRow, DataColumn) -> NewValue
colExpression = DataFrame.(DataColumn) -> DataColumn
frameExpression: DataFrame.(DataFrame) -> DataFrame
```

Shortcuts such as `toLocalDate`, `toStdlibInstant`, `toDuration`, or `toImg` pick one target type each;
see [Specialized date/time conversions](#specialized-date-time-conversions) and
[toImg and toIFrame](#toimg-and-toiframe). `toDataFrames` changes the shape of a column instead;
see [toDataFrames](#todataframes).

**Related operations**: [](updateConvert.md), [](convertTo.md), [](cast.md)

> This page is for the `DataFrame<*>.convert {}` operation and
> the `DataColumn<*>.convertTo<ValueType>()` family of functions.
>
> If you were looking for [`DataFrame<*>.convertTo<Schema>()`](convertTo.md), see [](convertTo.md).

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation and
[row expressions](DataRow.md#row-expressions) for how to provide new values.

The basic examples on this page use the same [`DataFrame`](DataFrame.md);
the specialized sections use their own data, shown where each section starts:

<!---FUN convertDf-->

```kotlin
df
```

<inline-frame src="./resources/convertDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

## with

Computes a new value for every cell of the selected columns with an expression that receives the current row as
`this` and the original value of the current cell as `it`.

The expression receiver is an
[`AddDataRow`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/-add-data-row/index.html),
which extends [`DataRow`](DataRow.md). Use `prev()?.newValue()` to access a value already converted in the preceding
row. This can be used to calculate running totals and other recurrence relations.

<!---FUN convert-->
<tabs>
<tab title="Properties">

```kotlin
df.convert { age }.with { it.toDouble() }
```

</tab>
<tab title="Strings">

```kotlin
df.convert { "age"<Int>() }.with { it.toDouble() }
```

</tab></tabs>
<inline-frame src="./resources/convert.html" width="100%" height="500px"></inline-frame>

<!---END-->

Calculate a running total of the `age` column and rename it to `runningAge` in place:

<!---FUN convertRunningTotal-->

```kotlin
df.convert { age into "runningAge" }.with {
    (prev()?.newValue<Int>() ?: 0) + it
}
```

<inline-frame src="./resources/convertRunningTotal.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertChars-->
<tabs>
<tab title="Properties">

```kotlin
df.convert { colsAtAnyDepth().colsOf<String>() }.with { it.toCharArray().toList() }
```

</tab>
<tab title="Strings">

```kotlin
df.convert { colsAtAnyDepth().colsOf<String>() }.with { it.toCharArray().toList() }
```

</tab></tabs>
<inline-frame src="./resources/convertChars.html" width="100%" height="500px"></inline-frame>

<!---END-->

## notNull

Like [with](#with), but the expression is applied only to non-null values; nulls stay nulls.
Use it to convert nullable columns without handling `null` in the expression.

<!---FUN convertNotNull-->

```kotlin
// `weight` contains nulls; `notNull` applies the expression only to non-null values
df.convert { weight }.notNull { it / 10 }
```

<inline-frame src="./resources/convertNotNull.html" width="100%" height="500px"></inline-frame>

<!---END-->

## perRowCol

Computes a new value for every selected cell given both its row and its column.
Use it when the conversion needs to know the column it is applied to.

<!---FUN convertPerRowCol-->

```kotlin
// appends the column name to every value of the selected columns
df.convert { age and weight }.perRowCol { row, col ->
    col[row.index()].toString() + col.name()
}
```

<inline-frame src="./resources/convertPerRowCol.html" width="100%" height="500px"></inline-frame>

<!---END-->

## asFrame

A [`ColumnGroup`](DataColumn.md#columngroup) is itself a [`DataFrame`](DataFrame.md),
so it can be converted with the DataFrame API:

<!---FUN convertAsFrame-->
<tabs>
<tab title="Properties">

```kotlin
df.convert { name }.asFrame { it.asDataFrame().add("fullName") { "$firstName $lastName" } }
```

</tab>
<tab title="Strings">

```kotlin
df.convert { colGroup("name") }.asFrame {
    it.asDataFrame().add("fullName") {
        getValue<String>("firstName") + " " + getValue<String>("lastName")
    }
}
```

</tab></tabs>
<inline-frame src="./resources/convertAsFrame.html" width="100%" height="500px"></inline-frame>

<!---END-->

## asColumn

Converts the selected columns with a column expression that sees the whole column.
Similar to [replace with](replace.md), this form is compiler-plugin-friendly
whenever you need to perform an operation on the entire column without changing its name.
For example, parallel processing:

<!---FUN convertAsColumn-->

```kotlin
df.convert { name }.asColumn { col ->
    col.toList()
        .parallelStream().map { it.toString() }.collect(Collectors.toList())
        .toColumn()
}
```

<inline-frame src="./resources/convertAsColumn.html" width="100%" height="500px"></inline-frame>

<!---END-->

## cast

Casts the static type of the selected columns to `R` without touching the values,
so that type-safe conversions compile when the compiler doesn't know the column type —
for example, on a [`DataFrame<*>`](DataFrame.md) where no schema is known.
(For the dataframe-level `cast<Schema>()`, see [](cast.md).)

<!---FUN convertCast-->

```kotlin
// `numbers` is an `AnyFrame`: the compiler doesn't know the column type,
// so `cast` adjusts the static type before the conversion
val numbers: AnyFrame = dataFrameOf("value")(1, 2, 3)
numbers.convert("value").cast<Int>().with { it * 2 }
```

<inline-frame src="./resources/convertCast.html" width="100%" height="500px"></inline-frame>

<!---END-->

## to

Converts the selected columns to `Type` automatically:

<!---FUN convertTo-->

```kotlin
df.convert { age }.to<Double>()
```

<inline-frame src="./resources/convertTo.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertColsToString-->

```kotlin
df.convert { colsOf<Number>() }.to<String>()
```

<inline-frame src="./resources/convertColsToString.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertToFloat-->

```kotlin
df.convert { weight }.toFloat()
```

<inline-frame src="./resources/convertToFloat.html" width="100%" height="500px"></inline-frame>

<!---END-->

Notice the `toFloat()` call in the example above.
This is a shortcut for `to<Float>()`. DataFrame has a handful of shortcuts like these.

### Supported target types

`df.convert {}.to<>()` and `col.convertTo<>()` support automatic type conversions between the following types:
* `String`, `Char` (uses [`parse`](parse.md) to convert from `String` to other types)
* `Boolean`
* `Byte`
* `Short`
* `Int` (and `Char`)
* `Long`
* `Float`
* `Double` (See [parsing doubles](parse.md#parsing-doubles) for `String` to `Double` conversion)
* `BigDecimal` (java.math)
* `BigInteger` (java.math)
* `LocalDateTime` (kotlinx.datetime and java.time)
* `LocalDate` (kotlinx.datetime and java.time)
* `LocalTime` (kotlinx.datetime and java.time)
* `Instant` (kotlinx.datetime, kotlin.time, and java.time)
* `Duration` (kotlin.time, and java.time, to/from `Long`, `Int`, `String`, and each other)
* `DateTimeComponents` (kotlinx.datetime, to any other kotlinx-datetime/java.time type, `Instant`, or `Long`)
* `URL` (java.net, to [`IMG`](toHTML.md#display-images) or [`IFRAME`](toHTML.md#embed-pages))
* `enum` classes (by name)

> Note that converting between `Char` and `Int` is done by UTF-16 character code.
> This means the `Char` `'1'` becomes the `Int` `49`.
> To convert `Char -> Int` the way it is written, use [parse()](parse.md) instead, or,
> in either case, use `String` as intermediary type.
> {style="warning"}

If you miss a type, or conversion, please let us know by creating a
[GitHub issue](https://github.com/Kotlin/dataframe/issues/new).

### Single columns

A single [`DataColumn`](DataColumn.md) is converted with `convertTo<ValueType>()`,
which supports the same automatic conversions as `convert { }.to<Type>()`:

<!---FUN convertColumnTo-->

```kotlin
df.weight.convertTo<Float?>()
```

<inline-frame src="./resources/convertColumnTo.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertColumnToDouble-->

```kotlin
df.age.convertToDouble()
```

<inline-frame src="./resources/convertColumnToDouble.html" width="100%" height="500px"></inline-frame>

<!---END-->

### ParserOptions

It is allowed to pass [`ParserOptions`](parse.md#parser-options) to conversion functions.
This will only be used for `String`/`Char` conversions. See [](parse.md) for more details.

For example, with this `String` dataframe:

<!---FUN stringDfInput-->

```kotlin
stringDf
```

<inline-frame src="./resources/stringDfInput.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertStringToLocalDate-->

```kotlin
// String -> LocalDate conversion
stringDf.convert { date }.to<LocalDate>(
    parserOptions = ParserOptions(
        dateTime = DateTimeParserOptions.Kotlin.withFormat(LocalDate.Formats.ISO),
    ),
)
```

<inline-frame src="./resources/convertStringToLocalDate.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertStringToLocalDateShortcut-->

```kotlin
// shortcut for String -> LocalDate conversion
stringDf.convert { date }.toLocalDate(LocalDate.Formats.ISO)
```

<inline-frame src="./resources/convertStringToLocalDateShortcut.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertStringTo-->

```kotlin
// String -> Double? conversion
stringDf.convert { value }.to<Double?>(
    parserOptions = ParserOptions(locale = Locale.GERMAN, nullStrings = setOf("-")),
)
```

<inline-frame src="./resources/convertStringTo.html" width="100%" height="500px"></inline-frame>

<!---END-->

### Enums and value classes

Automatic conversion from `String` to [enum classes](https://kotlinlang.org/docs/enum-classes.html#enum-classes)
is also supported. Given this enum:

```text
enum class Direction { NORTH, SOUTH, WEST, EAST }
```

<!---FUN convertToEnum-->

```kotlin
dataFrameOf("direction")("NORTH", "WEST")
    .convert("direction").to<Direction>()
```

<inline-frame src="./resources/convertToEnum.html" width="100%" height="500px"></inline-frame>

<!---END-->

[Value classes](https://kotlinlang.org/docs/inline-classes.html) can be used with `convert` too,
both as conversion source and target:

```text
@JvmInline
value class IntClass(val value: Int)
```

<!---FUN convertToValueClass-->

```kotlin
dataFrameOf("value")("1", "2") // note that values are strings; conversion is done automatically
    .convert("value").to<IntClass>()
```

<inline-frame src="./resources/convertToValueClass.html" width="100%" height="500px"></inline-frame>

<!---END-->

## Specialized date/time conversions {id="specialized-date-time-conversions"}

`to<Type>()` converts dates and times with the default parser.
The specialized functions give you control over the
[pattern and the format](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime.format/)
and the time zone.
`Long`, `Int` and `Instant` timestamps are interpreted in the given `zone`,
or in the system current time zone when no `zone` is passed.
The following examples use this [`DataFrame`](DataFrame.md):

<!---FUN datesDfInput-->

```kotlin
datesDf
```

<inline-frame src="./resources/datesDfInput.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertToLocalDateMillis-->

```kotlin
// Long epoch milliseconds -> kotlinx.datetime.LocalDate in the given time zone
datesDf.convert { millis }.toLocalDate(TimeZone.UTC)
```

<inline-frame src="./resources/convertToLocalDateMillis.html" width="100%" height="500px"></inline-frame>

<!---END-->

A `kotlin.time.Instant` column converts the same way. These examples use this [`DataFrame`](DataFrame.md):

<!---FUN instantsDfInput-->

```kotlin
instantsDf
```

<inline-frame src="./resources/instantsDfInput.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertInstantToLocalDate-->
<tabs>
<tab title="Properties">

```kotlin
// kotlin.time.Instant -> kotlinx.datetime.LocalDate in the given time zone
instantsDf.convert { instant }.toLocalDate(TimeZone.of("Europe/Berlin"))
```

</tab>
<tab title="Strings">

```kotlin
// a column selected by name converts in the given time zone too
instantsDf.convert("instant").toLocalDate(TimeZone.of("Europe/Berlin"))
```

</tab></tabs>
<inline-frame src="./resources/convertInstantToLocalDate.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertToLocalDate-->

```kotlin
// String -> kotlinx.datetime.LocalDate with a pattern
// the pattern-string overload requires an explicit opt-in
@OptIn(FormatStringsInDatetimeFormats::class)
datesDf.convert { date }.toLocalDate("dd.MM.yyyy")
```

<inline-frame src="./resources/convertToLocalDate.html" width="100%" height="500px"></inline-frame>

<!---END-->

A [`DateTimeFormat`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime.format/-date-time-format/)
is used the same way for date-times:

<!---FUN convertToLocalDateTime-->

```kotlin
// String -> kotlinx.datetime.LocalDateTime with a DateTimeFormat
val format = LocalDateTime.Format {
    year()
    chars("-")
    monthNumber()
    chars("-")
    day()
    chars(" ")
    hour()
    chars(":")
    minute()
}
datesDf.convert { dateTime }.toLocalDateTime(format)
```

<inline-frame src="./resources/convertToLocalDateTime.html" width="100%" height="500px"></inline-frame>

<!---END-->

`java.time` targets have their own shortcuts that take a pattern with an optional locale,
or a `DateTimeFormatter`:

<!---FUN convertToJavaLocalDateFormatter-->

```kotlin
// or with a DateTimeFormatter
datesDf.convert { date }.toJavaLocalDate(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
```

<inline-frame src="./resources/convertToJavaLocalDateFormatter.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertToJavaLocalDate-->

```kotlin
// String -> java.time.LocalDate with a pattern and a locale
datesDf.convert { date }.toJavaLocalDate("dd.MM.yyyy", Locale.GERMANY)
```

<inline-frame src="./resources/convertToJavaLocalDate.html" width="100%" height="500px"></inline-frame>

<!---END-->

### Instant

`toInstant()` is temporarily unavailable while the `Instant` type moves from `kotlinx.datetime`
to `kotlin.time`. Until version 1.1:

* `toStdlibInstant()` converts to `kotlin.time.Instant` — the current and future type
* `toDeprecatedInstant()` converts to `kotlinx.datetime.Instant` — the old type, kept for migration
  and deprecated with a warning

In 1.1, `toStdlibInstant()` will be renamed back to `toInstant()`.

The `Instant` examples use this dataframe:

<!---FUN timestampsDfInput-->

```kotlin
timestampsDf
```

<inline-frame src="./resources/timestampsDfInput.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertToStdlibInstant-->

```kotlin
// String -> kotlin.time.Instant
timestampsDf.convert { timestamp }.toStdlibInstant()
```

<inline-frame src="./resources/convertToStdlibInstant.html" width="100%" height="500px"></inline-frame>

<!---END-->

### DateTimeComponents, UtcOffset, YearMonth

[`DateTimeComponents`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime.format/-date-time-components/)
keeps the parsed date and time fields without committing to a concrete type,
so a string can be parsed once and converted onwards:

<!---FUN convertToDateTimeComponents-->

```kotlin
// String -> DateTimeComponents; date and time fields stay accessible for later conversions
timestampsDf.convert { timestamp }.toDateTimeComponents()
```

<!---END-->

From `DateTimeComponents` you can then convert to types that need only some of the fields
(`componentsDf` holds `DateTimeComponents` parsed from ISO strings with UTC offsets):

<!---FUN convertFromDateTimeComponents-->

```kotlin
// DateTimeComponents -> UtcOffset
componentsDf.convert { components }.toUtcOffset()
```

<inline-frame src="./resources/convertFromDateTimeComponents.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertToYearMonth-->

```kotlin
// DateTimeComponents -> YearMonth
componentsDf.convert { components }.toYearMonth()
```

<inline-frame src="./resources/convertToYearMonth.html" width="100%" height="500px"></inline-frame>

<!---END-->

### Duration

`toDuration()` converts to `kotlin.time.Duration`, `toJavaDuration()` to `java.time.Duration`.
Supported sources: `String` (parsed as ISO-8601), `Long` and `Int` (interpreted as milliseconds),
and `java.time.Duration`/`kotlin.time.Duration` into each other.
The examples use this dataframe:

<!---FUN durationsDfInput-->

```kotlin
durationsDf
```

<inline-frame src="./resources/durationsDfInput.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertToDuration-->

```kotlin
// String -> kotlin.time.Duration
durationsDf.convert { duration }.toDuration()
```

<inline-frame src="./resources/convertToDuration.html" width="100%" height="500px"></inline-frame>

<!---END-->

`toJavaDuration()` converts to `java.time.Duration`:

<!---FUN convertToJavaDuration-->

```kotlin
// String -> java.time.Duration
durationsDf.convert { duration }.toJavaDuration()
```

<inline-frame src="./resources/convertToJavaDuration.html" width="100%" height="500px"></inline-frame>

<!---END-->

## toDataFrames

Converts a column of nested lists into a column of nested [`DataFrame`](DataFrame.md) values.
By default the nested lists are row-oriented: the first inner list is the header,
the remaining lists are rows. With `containsColumns = true` they are column-oriented:
each inner list is one column, and its first element is the column name.

<!---FUN convertToDataFrames-->

```kotlin
// row-oriented: the first inner list is the header, the remaining lists are rows
val rowLists = dataFrameOf("userData")(
    listOf(listOf("name", "age"), listOf("Alice", 15), listOf("Bob", 45)),
).cast<WithUserData>()

rowLists.convert { userData }.toDataFrames()
```

<inline-frame src="./resources/convertToDataFrames.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertToDataFramesContainsColumns-->

```kotlin
// column-oriented: each inner list is one column, its first element is the column name
val columnLists = dataFrameOf("userData")(
    listOf(listOf("name", "Alice", "Bob"), listOf("age", 15, 45)),
).cast<WithUserData>()

columnLists.convert { userData }.toDataFrames(containsColumns = true)
```

<inline-frame src="./resources/convertToDataFramesContainsColumns.html" width="100%" height="500px"></inline-frame>

<!---END-->

## toImg and toIFrame

For rendering, `URL` columns can be converted to [`IMG`](toHTML.md#display-images)
or [`IFRAME`](toHTML.md#embed-pages) values, which [toHTML](toHTML.md) renders
as `<img>` and `<iframe>` tags. `toImg` takes an optional `width` and `height`;
`toIFrame` also takes a `border`.
The examples use this dataframe:

<!---FUN mediaDfInput-->

```kotlin
mediaDf
```

<inline-frame src="./resources/mediaDfInput.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertToImg-->

```kotlin
mediaDf.convert { logo }.toImg(width = 120)
```

<inline-frame src="./resources/convertToImg.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN convertToIFrame-->

```kotlin
mediaDf.convert { page }.toIFrame(border = true, width = 640, height = 200)
```

<inline-frame src="./resources/convertToIFrame.html" width="100%" height="500px"></inline-frame>

<!---END-->
