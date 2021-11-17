[//]: # (title: Access Data)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Get [rows](DataRow.md) or [columns](DataColumn.md):

<!---FUN getRowsColumns-->

```kotlin
df.rows() // Iterable<DataRow>
df.columns() // List<DataColumn>
```

<!---END-->

**How to:**
* [Access data by index](indexing.md)
* [Slice portion of data](slicing.md)
* [Iterate over data](iterate.md)
* [Get single row](getRow.md)
* [Get single column](getColumn.md)
