[//]: # (title: implode)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) where values in given columns are merged into lists grouped by other columns.

**API reference**: [`implode`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/implode.html)

```text
implode(dropNA = false) [ { columns } ]
```

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

**Parameters:**
* `dropNA` — if `true`, `NA` values are removed from the merged lists of value columns.
  Has no effect on imploded column groups and frame columns.

**Reverse operation:** [`explode`](explode.md)

Imploded columns will change their types:
* `T` to `List<T>`
* [`DataRow`](DataRow.md) to [`DataFrame`](DataFrame.md)

Imploded [`ColumnGroup`s](DataColumn.md#columngroup) will be converted into [`FrameColumn`s](DataColumn.md#framecolumn)

<!---FUN implode-->

```kotlin
df.implode { name and age and weight and isHappy }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.implode.html" width="100%"/>
<!---END-->
