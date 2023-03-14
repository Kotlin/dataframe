[//]: # (title: xs)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Return cross-section from the [`DataFrame`](DataFrame.md).

Filters [`DataFrame`](DataFrame.md) by matching key values with key columns and removes key columns.

```
xs(vararg keyValues: Any?)
     [ { keyColumns } ]
```

When `keyColumns` are not specified, it takes first `n` columns in dfs order (looking inside [`ColumnGroup`](DataColumn.md#columngroup)), where `n` is a number of given `keyValues`.

<!---FUN xs-->

```kotlin
df.xs("Charlie", "Chaplin")

df.xs("Moscow", true) { city and isHappy }
```

<!---END-->
