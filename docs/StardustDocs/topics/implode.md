[//]: # (title: implode)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns `DataFrame` where values in given columns are merged into lists grouped by other columns.

This is reverse operation to [`explode`](explode.md)

Imploded columns will change their types:
* `T` to `Many<T>`
* `DataRow` to `DataFrame`

Note that imploded [`ColumnGroup`](DataColumn.md#columngroup) will convert into [`FrameColumn`](DataColumn.md#framecolumn)

<!---FUN implode-->

```kotlin
df.implode { name and age and weight and isHappy }
```

<!---END-->

Set `dropNulls` flag to filter
