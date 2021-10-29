[//]: # (title: convert)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Changes data and type of the columns.

```kotlin
convert { columnsSelector }
    .with { rowExpression } | .withConst(value) | .withRowCol { rowColExpression } | to<Type>() | to { colExpression }
```

```
rowExpression = DataRow.(OldValue) -> NewValue
rowColExpression = DataRow.(DataColumn) -> NewValue
colExpression = DataFrame.(DataColumn) -> NewValue
```

See [column selectors](ColumnSelectors.md) and [row expressions](DataRow.md#row-expressions)

<!---FUN convert-->

```kotlin
df.convert { age }.with { it.toDouble() }
df.convert { dfsOf<String>() }.with { it.toCharArray().toList() }
```

<!---END-->

Supports automatic type conversions between the following types:
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

<!---FUN convertTo-->

```kotlin
df.convert { age }.to<Double>()
df.convert { numberCols() }.to<String>()
df.convert { name.firstName and name.lastName }.to { it.length() }
// with helper
df.convert { weight }.toFloat()
```

<!---END-->
