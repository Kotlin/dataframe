[//]: # (title: Slicing)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

The following operations return `DataFrame` with a subset of rows from original `DataFrame`.

<!---FUN getSeveralRows-->

```kotlin
df[0, 3, 4]
df[1..2]

df.take(5) // first 5 rows
df.takeLast(5) // last 5 rows
df.drop(5) // all rows except first 5
df.dropLast(5) // all rows except last 5
```

<!---END-->

To select several top / bottom rows see [take](take.md) / [takeLast](takeLast.md) / [drop](drop.md) / [dropLast](dropLast.md) operations

To select several rows based on [row condition](DataRow.md#row-conditions) see [filter](filter.md) / [drop](drop.md) operations
