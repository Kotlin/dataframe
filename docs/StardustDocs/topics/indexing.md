[//]: # (title: Indexing)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

<!---FUN getCell-->
<tabs>
<tab title="Properties">

```kotlin
df.age[1]
df[1].age
```

</tab>
<tab title="Strings">

```kotlin
df["age"][1]
df[1]["age"]
```

</tab></tabs>
<!---END-->

## Row indices

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.IndicesSamples-->

`indices()` returns the indices of all rows of the [`DataFrame`](DataFrame.md) as an `IntRange` —
from `0` to the number of rows minus one.
The examples below use the same ten-row dataframe as [`count`](count.md).

<!---FUN indices-->

```kotlin
df.indices() // 0..9
```

<!---END-->

Pass a [row condition](DataRow.md#row-conditions) to get only the indices of the rows that satisfy it,
as a `List<Int>` in row order:

<!---FUN indicesCondition-->
<tabs>
<tab title="Properties">

```kotlin
df.indices { city == "Moscow" } // [2, 6]
```

</tab>
<tab title="Strings">

```kotlin
df.indices { "city"<String?>() == "Moscow" } // [2, 6]
```

</tab></tabs>
<!---END-->

Use the result to [slice those rows](sliceRows.md) out of the dataframe.

See also [`rowsCount`](rowsCount.md) and [`filter`](filter.md),
which returns the matching rows instead of their indices.
