[//]: # (title: schema)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Returns `DataFrameSchema` object with `DataFrame` schema description. It can be printed to see column structure.

[`ColumnGroups`](DataColumn.md#columngroup) are marked by indentation:

<!---FUN schema-->

```kotlin
df.schema()
```

<!---END-->

Output:

```text
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
df.groupBy { city }.schema()
```

<!---END-->

Output:

```text
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
