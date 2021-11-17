[//]: # (title: explode)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Splits list-like values in one or several columns and spreads them vertically. Values in other columns are duplicated.  

This is reverse operation to [`implode`](implode.md)

Exploded columns will change their types:
* `List<T>` to `T`
* `DataFrame` to `DataRow`

Note that exploded [`FrameColumn`](DataColumn.md#framecolumn) will convert into [`ColumnGroup`](DataColumn.md#columngroup)

Rows with empty lists will be skipped. If you want to keep such rows with `null` value in exploded columns, set `dropEmpty` flag to `false`.

<!---FUN explode-->

```kotlin
val df2 = df.convert { age }.with { (1..it step 4).toList() }

df2.explode { age }
```

<!---END-->

When several columns are exploded in one operation, lists in different columns will be aligned.
