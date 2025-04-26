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

See [column selectors](ColumnSelectors.md) and [row expressions](DataRow.md#row-expressions)

<!---FUN convert-->

```kotlin
df.convert { age }.with { it.toDouble() }
df.convert { colsAtAnyDepth().colsOf<String>() }.with { it.toCharArray().toList() }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.convert.html"/>
<!---END-->

ColumnGroup can be converted using DataFrame API, for example:

<!---FUN convertAsFrame-->

```kotlin
df.convert { name }.asFrame { it.add("fullName") { "$firstName $lastName" } }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.convertAsFrame.html"/>
<!---END-->

Similar to `replace with` operation, 
columns can be converted in a compiler plugin-friendly fashion
whenever you need to perform an operation on the entire column without changing its name.
For example, parallel reading.

<!---FUN convertAsColumn-->

```kotlin
df.convert { name }.asColumn { col ->
    col.toList().parallelStream().map { it.toString() }.collect(Collectors.toList()).toColumn()
}
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.convertAsColumn.html"/>
<!---END-->


`convert` supports automatic type conversions between the following types:
* `String` (uses [`parse`](parse.md) to convert from `String` to other types)
* `Boolean`
* `Byte`
* `Short`
* `Int` (and `Char`)
* `Long`
* `Float`
* `Double` (See [parsing doubles](parse.md#parsing-doubles) for `String` to `Double` conversion)
* `BigDecimal`
* `BigInteger`
* `LocalDateTime` (kotlinx.datetime and java.time)
* `LocalDate` (kotlinx.datetime and java.time)
* `LocalTime` (kotlinx.datetime and java.time)
* `Instant` (kotlinx.datetime and java.time)

<!---FUN convertTo-->

```kotlin
df.convert { age }.to<Double>()
df.convert { colsOf<Number>() }.to<String>()
df.convert { name.firstName and name.lastName }.to { it.length() }
df.convert { weight }.toFloat()
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.convertTo.html"/>
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

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.convertToEnum.html"/>
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

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.convertToValueClass.html"/>
<!---END-->
