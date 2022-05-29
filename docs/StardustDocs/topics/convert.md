[//]: # (title: convert)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns `DataFrame` with changed values in some columns. Allows to change column types.

```text
convert { columnsSelector }
    .with { rowExpression } | .perRowCol { rowColExpression } | .withValue(value)  | to<Type>() | to { colExpression }

rowExpression = DataRow.(OldValue) -> NewValue
rowColExpression = DataRow.(DataColumn) -> NewValue
colExpression = DataFrame.(DataColumn) -> DataColumn
```

See [column selectors](ColumnSelectors.md) and [row expressions](DataRow.md#row-expressions)

<!---FUN convert-->

```kotlin
df.convert { age }.with { it.toDouble() }
df.convert { dfsOf<String>() }.with { it.toCharArray().toList() }
```

<!---END-->

`convert` supports automatic type conversions between the following types:
* `Int`
* `String`
* `Double`
* `Long`
* `Short`
* `Float`
* `BigDecimal`
* `LocalDateTime`
* `LocalDate`
* `LocalTime`
* `Duration`

<!---FUN convertTo-->

```kotlin
df.convert { age }.to<Double>()
df.convert { colsOf<Number>() }.to<String>()
df.convert { name.firstName and name.lastName }.to { it.length() }
df.convert { weight }.toFloat()
```

<!---END-->

Automatic conversion from `String` into enum class is also supported:

```kotlin
enum class Direction { NORTH, SOUTH, WEST, EAST }
```

<!---FUN convertToEnum-->

```kotlin
dataFrameOf("direction")("NORTH", "WEST")
   .convert("direction").to<Direction>()
```

<!---END-->
