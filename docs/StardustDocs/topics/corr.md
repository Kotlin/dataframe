[//]: # (title: corr)

Returns [`DataFrame`](DataFrame.md) with pairwise correlation between two sets of columns.

Computes Pearson correlation coefficient.

```kotlin
corr { columns1 }
    .with { columns2 } | .withItself()
```

To compute pairwise correlation between all columns in [`DataFrame`](DataFrame.md) use `corr` without arguments:

```kotlin
corr()
```

Available for numeric and `Boolean` columns. `Boolean` values are converted into `1` for `true` and `0` for `false`. All other columns are ignored.

If [`ColumnGroup`](DataColumn.md#columngroup) is passed as target column for correlation, it will be unpacked into suitable nested columns.

Resulting [`DataFrame`](DataFrame.md) will have `n1` rows and `n2+1` columns, where `n1` and `n2` are numbers of columns in `columns1` and `columns2` correspondingly.

First column will have the name "column" and will contain names of columns in `column1`. Other columns will have the same names as in `columns2` and will contain computed correlation coefficients.

If exactly one [`ColumnGroup`](DataColumn.md#columngroup) is passed in `columns1`, first column in output will have its name. 
