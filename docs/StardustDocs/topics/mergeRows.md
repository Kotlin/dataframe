[//]: # (title: mergeRows)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns `DataFrame` where values in given columns are merged into lists or dataframes grouped by other columns.

This is reverse operation to [explode](explode.md)

Merged columns will change their types:
* `T` to `Many<T>`
* `DataRow` to `DataFrame`

Note that merged [`ColumnGroup`](DataColumn.md#columngroup) will convert into [`FrameColumn`](DataColumn.md#framecolumn)

<!---FUN mergeRows-->

```kotlin
df.mergeRows { name and age and weight and isHappy }
```

<!---END-->
