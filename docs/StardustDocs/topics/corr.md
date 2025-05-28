[//]: # (title: corr)

Returns [`DataFrame`](DataFrame.md) with the pairwise correlation between two sets of columns.

It computes the [Pearson correlation coefficient](https://en.wikipedia.org/wiki/Pearson_correlation_coefficient).

```kotlin
corr { columns1 }
    .with { columns2 } | .withItself()
```

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

To compute pairwise correlation between all columns in the [`DataFrame`](DataFrame.md) use `corr` without arguments:

```kotlin
corr()
```

The function is available for numeric- and `Boolean` columns.
`Boolean` values are converted into `1` for `true` and `0` for `false`.
All other columns are ignored.

If a [`ColumnGroup`](DataColumn.md#columngroup) instance is passed as the target column for correlation,
it will be unpacked into suitable nested columns.

The resulting [`DataFrame`](DataFrame.md) will have `n1` rows and `n2+1` columns,
where `n1` and `n2` are the number of columns in `columns1` and `columns2` correspondingly.

The first column will have the name "column" and will contain names of columns in `column1`.
Other columns will have the same names as in `columns2` and will contain the computed correlation coefficients.

If exactly one [`ColumnGroup`](DataColumn.md#columngroup) is passed in `columns1`,
the first column in the output will have its name. 
