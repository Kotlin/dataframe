[//]: # (title: convert)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) with changed values in some columns. Allows changing column types.

```text
convert { columnsSelector }
    .with { rowExpression } | .asFrame { frameExpression } | .perRowCol { rowColExpression } | to<Type>() | to { colExpression }

rowExpression = DataRow.(OldValue) -> NewValue
rowColExpression = (DataRow, DataColumn) -> NewValue
colExpression = DataFrame.(DataColumn) -> DataColumn
frameExpression: DataFrame.(DataFrame) -> DataFrame
```

**Related operations**: [](updateConvert.md), [](convertTo.md)

> This page is for the `DataFrame<*>.convert {}` operation and
> the `DataColumn<*>.convertTo<ValueType>()` family of functions.
>
> If you were looking for [`DataFrame<*>.convertTo<Schema>()`](convertTo.md), see [](convertTo.md).

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation and
[row expressions](DataRow.md#row-expressions) for how to provide new values.

<!---FUN convert-->

```kotlin
df.convert { age }.with { it.toDouble() }
df.convert { colsAtAnyDepth().colsOf<String>() }.with { it.toCharArray().toList() }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.convert.html" width="100%"/>
<!---END-->

<!---FUN convertColumnTo-->

```kotlin
df.weight.convertTo<Float?>()
df.age.convertToDouble()
```

<!---END-->

ColumnGroup can be converted using DataFrame API, for example:

<!---FUN convertAsFrame-->

```kotlin
df.convert { name }.asFrame { it.add("fullName") { "$firstName $lastName" } }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.convertAsFrame.html" width="100%"/>
<!---END-->

Similar to `replace with` operation, 
columns can be converted in a compiler-plugin-friendly fashion
whenever you need to perform an operation on the entire column without changing its name.
For example, parallel reading.

<!---FUN convertAsColumn-->

```kotlin
df.convert { name }.asColumn { col ->
    col.toList().parallelStream().map { it.toString() }.collect(Collectors.toList()).toColumn()
}
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.convertAsColumn.html" width="100%"/>
<!---END-->


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

<!---FUN convertTo-->

```kotlin
df.convert { age }.to<Double>()
df.convert { colsOf<Number>() }.to<String>()
df.convert { name.firstName and name.lastName }.asColumn { col -> col.map { it.length } }
df.convert { weight }.toFloat()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.convertTo.html" width="100%"/>
<!---END-->

Notice the `toFloat()` call in the example above.
This is a shortcut for `to<Float>()`. DataFrame has a handful of shortcuts like these

**ParserOptions:**

It is allowed to pass [`ParserOptions`](parse.md#parser-options) to conversion functions.
This will only be used for `String`/`Char` conversions. See [](parse.md) for more details.

For example:

<!---FUN convertStringTo-->

```kotlin
// String -> Double? conversion
stringDf.convert { value }.to<Double?>(
    parserOptions = ParserOptions(locale = Locale.GERMAN, nullStrings = setOf("-")),
)

// String -> LocalDate conversion
stringDf.convert { date }.to<LocalDate>(
    parserOptions = ParserOptions(
        dateTime = DateTimeParserOptions.Kotlin.withFormat(LocalDate.Formats.ISO),
    ),
)
// shortcut for String -> LocalDate conversion
stringDf.convert { date }.toLocalDate(LocalDate.Formats.ISO)
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.convertStringTo.html" width="100%"/>
<!---END-->

Automatic conversion from `String` to [enum classes](https://kotlinlang.org/docs/enum-classes.html#enum-classes.md)
is also supported:

```kotlin
enum class Direction { NORTH, SOUTH, WEST, EAST }
```

<!---FUN convertToEnum-->

```kotlin
dataFrameOf("direction")("NORTH", "WEST")
    .convert("direction").to<Direction>()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.convertToEnum.html" width="100%"/>
<!---END-->

And finally, [Value classes](https://kotlinlang.org/docs/inline-classes.html) can be used with `convert` too.
Both as conversion source and target:

```kotlin
@JvmInline 
value class IntClass(val value: Int)
```

<!---FUN convertToValueClass-->

```kotlin
dataFrameOf("value")("1", "2") // note that values are strings; conversion is done automatically
    .convert("value").to<IntClass>()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.convertToValueClass.html" width="100%"/>
<!---END-->
