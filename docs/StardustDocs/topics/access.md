[//]: # (title: Access Data)
<show-structure depth="3"/>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Get [rows](DataRow.md) or [columns](DataColumn.md):

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
* [Get single column](getColumns.md)
