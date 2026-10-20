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

If no observations are counted, the result has zero rows and retains the observed-value columns and count column.
This includes empty input and rows excluded by `dropNA`.

This operation can also be applied to a [`DataColumn`](DataColumn.md):

<!---FUN valueCountsColumn-->

```kotlin
df.age.valueCounts()
```

<inline-frame src="./resources/valueCountsColumn.html" width="100%" height="500px"></inline-frame>

<!---END-->

## Normalized counts

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.NormalizedValueCountsSamples-->

Pass `normalize = true` to return relative frequencies: each count is divided by the sum of the counts that remain
after applying `dropNA`. The generated count column has type `Double` and keeps the default name `count`.
You can change its name with `resultColumn`; existing name-collision suffixes still apply.

For the observations `a, a, null, b`, the default excludes `null`, so the proportions are `2/3` and `1/3`.

<!---FUN valueCountsProportions-->
<!---END-->

With `dropNA = false`, missing observations contribute to the denominator. The same input has proportions
`0.5`, `0.25`, and `0.25`, including a row for `null`:

<!---FUN valueCountsProportionsIncludingNA-->
<!---END-->

For a dataframe, only the selected columns determine which rows are missing. Missing cells in unselected columns
do not affect normalization. Empty input, or input with no counted observations, returns no rows and retains a
non-nullable `Double` count column.

To normalize independently within each group, apply the operation inside `updateGroups`:

<!---FUN valueCountsProportionsPerGroup-->
<!---END-->

The `normalize` overloads return `DataFrame<*>`, since the output count type depends on the flag. Use name-based
access such as `proportions["count"]`; generated compiler-plugin accessors are not available for these overloads.
Passing `normalize = false` keeps absolute `Int` counts. Calls that omit `normalize` keep their existing typed API.
