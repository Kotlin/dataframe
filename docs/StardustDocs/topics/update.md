[//]: # (title: update)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Changes the values in some cells preserving original column types

Syntax:
```kotlin
update { columns }
   [.where { filter } | .at(rowIndices) ] 
    .with { valueExpression } | .withNull()
```

where

```kotlin
filter = DataRow.(OldValue) -> Boolean
valueExpression = DataRow.(OldValue) -> NewValue
```

Examples:
```kotlin
df.update { price }.with { it * 2 }
df.update { age }.where { name == "Alice" }.with { 20 }
df.update { column }.at(4,6,10).with { "value" } 
df.update { column }.at(5..15).withNull() 
df.update { price }.with { (it + (prev?.price ?: it) + (next?.price ?: it)) / 3 } // moving average
df.update { cases }.with { it.toDouble() / population * 100 }
```
