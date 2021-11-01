[//]: # (title: convert)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Changes values and types of the columns.

#### Syntax

```kotlin
convert { columnsSelector }
    .with { rowExpression } | .withConst(value) | .withRowCol { rowColExpression } | to<Type>() | to { colExpression }

rowExpression = DataRow.(OldValue) -> NewValue
rowColExpression = DataRow.(DataColumn) -> NewValue
colExpression = DataFrame.(DataColumn) -> NewValue
```

See [column selectors](ColumnSelectors.md) and [row expressions](DataRow.md#row-expressions)

#### Examples

<!---FUN convert-->

```kotlin
df.convert { age }.with { it.toDouble() }
df.convert { dfsOf<String>() }.with { it.toCharArray().toList() }
```

<!---END-->

### Type conversions

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

<!---FUN convertTo-->

```kotlin
df.convert { age }.to<Double>()
df.convert { numberCols() }.to<String>()
df.convert { name.firstName and name.lastName }.to { it.length() }
df.convert { weight }.toFloat()
```

<!---END-->
