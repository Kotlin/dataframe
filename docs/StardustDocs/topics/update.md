[//]: # (title: update)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns `DataFrame` with changed values in some cells. Column types can not be changed.

```kotlin
update { columns }
    [.where { rowCondition } ]
    [.at(rowIndices) ] 
     .with { rowExpression } | .perCol { colExpression } | .perRowCol { rowColExpression } | .withValue(value) | .withNull() | .withZero() 

rowCondition: DataRow.(OldValue) -> Boolean
rowExpression: DataRow.(OldValue) -> NewValue
colExpression: DataColumn.(DataColumn) -> NewValue
rowColExpression: DataRow.(DataColumn) -> NewValue
```

See [column selectors](ColumnSelectors.md) and [row expressions](DataRow.md#row-expressions)

<!---FUN update-->

```kotlin
df.update { age }.with { it * 2 }
df.update { dfsOf<String>() }.with { it.uppercase() }
df.update { weight }.at(1..4).notNull { it / 2 }
df.update { name.lastName and age }.at(1, 3, 4).withNull()
```

<!---END-->

**Update with constant value:**

<!---FUN updateWithConst-->

```kotlin
df.update { city }.where { name.firstName == "Alice" }.withValue("Paris")
```

<!---END-->

**Update with value depending on row:**

<!---FUN updateWith-->

```kotlin
df.update { city }.with { name.firstName + " from " + it }
```

<!---END-->

**Update with value depending on column:**

<!---FUN updatePerColumn-->

```kotlin
df.update { numberCols() }.perCol { mean(skipNA = true) }
```

<!---END-->

**Update with value depending on row and column:**

<!---FUN updatePerRowCol-->

```kotlin
df.update { stringCols() }.perRowCol { row, col -> col.name() + ": " + row.index() }
```

<!---END-->
