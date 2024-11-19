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

`convert` supports automatic type conversions between the following types:
* `String` (uses [`parse`](parse.md) to convert from `String` to other types)
* `Boolean`
* `Byte`
* `Short`
* `Int` (and `Char`)
* `Long`
* `Float`
* `Double`
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

Automatic conversion from `String` to enum classes is also supported:

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
