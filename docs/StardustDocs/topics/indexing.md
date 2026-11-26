[//]: # (title: Indexing)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.IndexingSamples-->

**API reference**: [`DataFrame`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe/-data-frame/index.html), [`DataRow`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe/-data-row/index.html)

The examples on this page use the following [`DataFrame`](DataFrame.md):

<!---FUN indexingDf-->

```kotlin
df
```

<inline-frame src="./resources/indexingDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

## Access a cell by index

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

`indices()` returns the indices of all rows of the [`DataFrame`](DataFrame.md) as an `IntRange` —
from `0` to the number of rows minus one:

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
val moscow = df.indices { city == "Moscow" } // [2, 6]
```

</tab>
<tab title="Strings">

```kotlin
val moscow = df.indices { "city"<String?>() == "Moscow" } // [2, 6]
```

</tab></tabs>
<!---END-->

Use the result to [slice those rows](sliceRows.md) out of the dataframe:

<!---FUN indicesGetRows-->

```kotlin
df.getRows(df.indices { city == "Moscow" })
```

<inline-frame src="./resources/indicesGetRows.html" width="100%" height="500px"></inline-frame>

<!---END-->

See also [`rowsCount`](rowsCount.md), which returns how many rows there are rather than their indices,
and [`filter`](filter.md), which returns the matching rows themselves.
