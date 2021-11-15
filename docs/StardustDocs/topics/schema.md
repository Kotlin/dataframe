[//]: # (title: schema)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Returns `DataFrameSchema` object with `DataFrame` schema description. It can be printed to see column structure.

[`ColumnGroups`](DataColumn.md#columngroup) are marked by indentation:

<!---FUN schema-->

```kotlin
df.schema().print()
```

<!---END-->

Output:

```
name:
    firstName: String
    lastName: String
age: Int
city: String?
weight: Int?
isHappy: Boolean
```

[`FrameColumns`](DataColumn.md#framecolumn) are marked with `*`:

<!---FUN schemaGroupBy-->

```kotlin
df.groupBy { city }.schema().print()
```

<!---END-->

Output:

```
city: String?
group: *
    name:
        firstName: String
        lastName: String
    age: Int
    city: String?
    weight: Int?
    isHappy: Boolean
```
