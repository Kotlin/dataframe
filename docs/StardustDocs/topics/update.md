[//]: # (title: update)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Changes the values in some cells preserving original column types

```kotlin
update { columnSelector }
    [.where { rowCondition } ]
    [.at(rowIndices) ] 
     .with { rowExpression } | .withNull() | .withConst(value) | withRowCol { rowColExpression }
```

```
rowCondition = DataRow.(OldValue) -> Boolean
rowExpression = DataRow.(OldValue) -> NewValue
rowColExpression = DataRow.(DataColumn) -> NewValue
```

See [column selectors](ColumnSelectors.md) and [row expressions](DataRow.md#row-expressions)

<!---FUN update-->

```kotlin
df.update { age }.with { it * 2 }
df.update { dfsOf<String>() }.with { it.uppercase() }
df.update { city }.where { name.firstName == "Alice" }.withValue("Paris")
df.update { weight }.at(1..4).notNull { it / 2 }
df.update { name.lastName and age }.at(1, 3, 4).withNull()
df.update { age }.with { movingAverage(2) { age }.toInt() }
```

<!---END-->
