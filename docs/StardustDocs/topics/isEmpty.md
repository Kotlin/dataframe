[//]: # (title: isEmpty)

Returns `true` if [`DataFrame`](DataFrame.md) has no rows or no columns.

```kotlin
isEmpty()
```

Removing all columns with [`remove`](remove.md) keeps the number of rows,
so a [`DataFrame`](DataFrame.md) can have rows and no columns.
Such a dataframe holds no values, so it counts as empty as well as one without rows.

A [`ColumnGroup`](DataColumn.md#columngroup) is a [`DataFrame`](DataFrame.md) too, and both cases apply to it:
a column group is empty when it has no nested columns, or when the dataframe that holds it has no rows.

Nested dataframes of a [`FrameColumn`](DataColumn.md#framecolumn) are not taken into account: a dataframe
whose frame column holds only empty dataframes — after [`groupBy`](groupBy.md), for example — is not empty
itself.

The examples below use this [`DataFrame`](DataFrame.md):

| name    | age |
|---------|-----|
| Alice   | 15  |
| Charlie | 40  |

```kotlin
df.isEmpty() // false
df.filter { age > 100 }.isEmpty() // true: no row is left
df.remove { all() }.isEmpty() // true: no column is left, while both rows are still there
```

## isNotEmpty

Returns `true` if [`DataFrame`](DataFrame.md) has at least one row and at least one column,
that is, if it is not empty.

```kotlin
isNotEmpty()
```

With the same two-row `df` as above:

```kotlin
df.isNotEmpty() // true
df.filter { age > 100 }.isNotEmpty() // false: no row is left
```

See also [`rowsCount()`](rowsCount.md) and [`columnsCount()`](columnsCount.md) — the two numbers behind these
checks, and [`emptyDataFrame()`](createDataFrame.md#emptydataframe) for creating an empty dataframe.
