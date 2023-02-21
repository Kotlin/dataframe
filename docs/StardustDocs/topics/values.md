[//]: # (title: values)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Return `Sequence` of values from one or several columns of [`DataFrame`](DataFrame.md).

```
values(byRows: Boolean = false) 
    [ columns ]: Sequence<C>
```

**Parameters:**
* `columns` (optional) — subset of columns for values extraction
* `byRows: Boolean = false` — if `true`, data is traversed by rows, not by columns

<!---FUN values-->

```kotlin
df.values()
df.values(byRows = true)
df.values { age and weight }
```

<!---END-->

## values in aggregation

In [`groupBy`](groupBy.md#aggregation) and [`pivot`](pivot.md#aggregation) aggregations `values` function yields list of column values for every aggregated data group. 

```kotlin
df.groupBy { A }.values { B }

df.pivot { A }.values { B }

df.pivot { A }.groupBy { B }.values { C and D }
```
