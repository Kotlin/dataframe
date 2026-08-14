[//]: # (title: valueCounts)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ValueCountsSamples-->

Return [`DataFrame`](DataFrame.md) containing counts of unique values in [`DataFrame`](DataFrame.md) 
or [`DataColumn`](DataColumn.md).

```kotlin
valueCounts(sort = true, ascending = false, dropNA = true, resultColumn = "count")
     [ { columns } ]
```

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

**Parameters:**
* `sort: Boolean = true` — whether to sort by count
* `ascending: Boolean = false` — sort direction (by default most frequent first)
* `dropNA: Boolean = true` — doesn't include counts of [`NA`](nanAndNa.md) values 
* `resultColumn: String = "count"` — name of the column with counts
* `columns = all` — columns to use when counting unique combinations

<!---FUN valueCountsDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/valueCountsDf.html" width="100%" height="500px"></inline-frame>

For a [`DataFrame`](DataFrame.md), `dropNA = true` (default) excludes the entire row if any selected column contains 
an [`NA`](nanAndNa.md) value. [`NA`](nanAndNa.md) values in columns that are not selected do not affect the result.

<!---FUN valueCounts-->

```kotlin
df.valueCounts()
```

<!---END-->
<inline-frame src="./resources/valueCounts.html" width="100%" height="500px"></inline-frame>

To include rows with [`NA`](nanAndNa.md) values, set `dropNA = false`:

<!---FUN valueCountsWithNA-->

```kotlin
df.valueCounts(dropNA = false)
```

<!---END-->
<inline-frame src="./resources/valueCountsWithNA.html" width="100%" height="500px"></inline-frame>

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
<!---END-->
<inline-frame src="./resources/valueCountsSelector_properties.html" width="100%" height="500px"></inline-frame>

This operation can also be applied to a [`DataColumn`](DataColumn.md):

<!---FUN valueCountsColumn-->

```kotlin
df.age.valueCounts()
```

<!---END-->
<inline-frame src="./resources/valueCountsColumn.html" width="100%" height="500px"></inline-frame>
