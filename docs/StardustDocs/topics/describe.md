[//]: # (title: describe)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Returns [`DataFrame`](DataFrame.md) with general statistics for all [`ValueColumns`](DataColumn.md#valuecolumn).

```kotlin
describe [ columns ]
```

[`ColumnGroup`](DataColumn.md#columngroup) and [`FrameColumns`](DataColumn.md#framecolumn) are traversed recursively down to `ValueColumns`.

### Summary Metrics:

- **`name`** — The name of the column.
- **`path`** — path to the column (for hierarchical `DataFrame`)
- **`type`** — The data type of the column (e.g., Int, String, Boolean).
- **`count`** — The total number of non-null values in the column.
- **`unique`** — The number of unique values in the column.
- **`nulls`** — The count of null (missing) values in the column.
- **`top`** — The most frequently occurring value in the column.
- **`freq`** — The frequency of the most common value.
- **`mean`** — The arithmetic mean (only for numeric columns).
- **`std`** — The standard deviation (only for numeric columns).
- **`min`** — The minimum value in the column.
- **`p25`** — The 25th percentile value (first quartile).
- **`median`** — The median value (50th percentile / second quartile).
- **`p75`** — The 75th percentile value (third quartile).
- **`max`** — The maximum value in the column.

For non-numeric columns, statistical metrics
such as `mean` and `std` will return `null`. If column values are incomparable,
percentile values (`min`, `p25`, `median`, `p75`, `max`) will also return `null`.
<!---FUN describe-->

```kotlin
df.describe()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.describe.html" width="100%"/>
<!---END-->

To describe only specific columns, pass them as an argument:

<!---FUN describeColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.describe { age and name.allCols() }
```

</tab>
<tab title="Strings">

```kotlin
df.describe { "age" and "name".allCols() }
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.describeColumns.html" width="100%"/>
<!---END-->
