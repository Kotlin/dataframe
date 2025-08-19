[//]: # (title: implode)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) where values in given columns are merged into lists grouped by other columns.

```text
implode(dropNA = false) [ { columns } ]
```

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

**Parameters:**
* `dropNA` — if `true`, removes `NA` values from merged lists.

**Reverse operation:** [`explode`](explode.md)

Imploded columns will change their types:
* `T` to `List<T>`
* [`DataRow`](DataRow.md) to [`DataFrame`](DataFrame.md)

Imploded [`ColumnGroup`](DataColumn.md#columngroup) will convert into [`FrameColumn`](DataColumn.md#framecolumn)

<!---FUN implode-->

```kotlin
df.implode { name and age and weight and isHappy }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.implode.html" width="100%"/>
<!---END-->
