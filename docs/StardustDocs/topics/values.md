[//]: # (title: values)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples-->

Return a lazy [`Sequence`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/) of values from one or several columns of [`DataFrame`](DataFrame.md).

**API reference**: [`values`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/values.html)

```
values(byRows: Boolean = false) 
    [ { columns } ]: Sequence<C>
```

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

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

In [`groupBy`](groupBy.md#aggregation) and [`pivot`](pivot.md#aggregation) aggregations, `values` collects
the values of the selected columns for every group. Each selected column is collected independently of
the others.

In a `groupBy`, the result holds the group keys and, per selected column, a `List` with all values from
the group. In a `pivot`, every cell holds the collected values: the single value if there is exactly one,
a `List` if there are several.

<!---FUN valuesInGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
// for every city, the "name" and "age" values of its people, as lists
val result = df.groupBy { city }.values { name and age }
```

</tab>
<tab title="Strings">

```kotlin
val result = df.groupBy("city").values("name", "age")
```

</tab></tabs>
<!---END-->

<!---FUN valuesInPivot-->
<tabs>
<tab title="Properties">

```kotlin
// the "age" values of each city, as a single row
val result = df.pivot { city }.values { age }
// the same, for all columns except the pivot column
val resultAll = df.pivot { city }.values()
```

</tab>
<tab title="Strings">

```kotlin
val result = df.pivot("city").values("age")
```

</tab></tabs>
<!---END-->

<!---FUN valuesInPivotGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
// for every person, the "age" values per city: a single value when there is one,
// a list when there are several, and an empty cell when there is nothing to collect
val result = df.pivot { city }.groupBy { name }.values { age }
// with dropNA, a cell whose values are all `null` becomes empty as well
val withoutNA = df.pivot { city }.groupBy { name }.values(dropNA = true) { weight }
```

</tab>
<tab title="Strings">

```kotlin
val result = df.pivot("city").groupBy("name").values("age")
```

</tab></tabs>
<!---END-->
