[//]: # (title: valueCounts)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ValueCountsSamples-->

Returns a [`DataFrame`](DataFrame.md) containing the counts of the unique values in a [`DataFrame`](DataFrame.md)
or [`DataColumn`](DataColumn.md).

```kotlin
valueCounts(sort = true, ascending = false, dropNA = true, resultColumn = "count")
     [ { columns } ]
```

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

**Parameters:**
* `sort: Boolean = true` — whether to sort by count
* `ascending: Boolean = false` — sort direction (by default most frequent first)
* `dropNA: Boolean = true` — whether to exclude [`NA`](nanAndNa.md) values from counting (excluded by default)
* `resultColumn: String = "count"` — name of the column with counts
* `columns = all` — columns to use when counting unique combinations

The following dataframe will be used in the examples below:

<!---FUN valueCountsDf-->

```kotlin
df
```

<inline-frame src="./resources/valueCountsDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

For a [`DataFrame`](DataFrame.md), `dropNA = true` (default) excludes the entire row if any selected column contains
an [`NA`](nanAndNa.md) value. [`NA`](nanAndNa.md) values in columns that are not selected do not affect the result.

<!---FUN valueCounts-->

```kotlin
df.valueCounts()
```

<inline-frame src="./resources/valueCounts.html" width="100%" height="500px"></inline-frame>

<!---END-->

To include rows with [`NA`](nanAndNa.md) values, set `dropNA = false`:

<!---FUN valueCountsWithNA-->

```kotlin
df.valueCounts(dropNA = false)
```

<inline-frame src="./resources/valueCountsWithNA.html" width="100%" height="500px"></inline-frame>

<!---END-->

You can specify columns that will be used to determine uniqueness:

<!---FUN valueCountsSelector-->
<tabs>
<tab title="Properties">

```kotlin
df.valueCounts(dropNA = false) { name }
```

</tab>
<tab title="Strings">

```kotlin
df.valueCounts("name", dropNA = false)
```

</tab></tabs>
<inline-frame src="./resources/valueCountsSelector.html" width="100%" height="500px"></inline-frame>

<!---END-->

This operation can also be applied to a [`DataColumn`](DataColumn.md):

<!---FUN valueCountsColumn-->

```kotlin
df.age.valueCounts()
```

<inline-frame src="./resources/valueCountsColumn.html" width="100%" height="500px"></inline-frame>

<!---END-->
