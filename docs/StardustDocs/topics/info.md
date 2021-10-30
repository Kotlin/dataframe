[//]: # (title: Basic info)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Basic information about `DataFrame`:
* `nrow()` - number of rows
* `ncol()` - number of columns
* `columnNames()` - list of column names
* [`schema()`](schema.md) - schema of columns
* [`describe()`](describe.md) - basic statistics for every column 

<!---FUN basicInfo-->

```kotlin
df.nrow()
df.ncol()
df.columnNames()
df.schema()
df.describe()
```

<!---END-->
