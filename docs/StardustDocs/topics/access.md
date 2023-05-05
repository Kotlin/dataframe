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
* [Access data by index](indexing.md)
* [Iterate over data](iterate.md)
* [Get single row](getRow.md)
* [Get single column](getColumns.md)
