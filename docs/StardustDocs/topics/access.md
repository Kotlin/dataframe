[//]: # (title: Access Data)
<show-structure depth="3"/>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Get [rows](DataRow.md) or [columns](DataColumn.md):

**API reference**: [`api` package](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/index.html)

<!---FUN getRowsColumns-->

```kotlin
df.columns() // List<DataColumn>
df.rows() // Iterable<DataRow>
df.values() // Sequence<Any?>
```

<!---END-->

**Learn how to:**
* [Access a cell by index](indexing.md#access-a-cell-by-index)
* [Get row indices](indexing.md#row-indices)
* [Iterate over data](iterate.md)
* [Get a single row](getRow.md)
* [Get a single column](getColumns.md)
