[//]: # (title: xs)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Return cross-section from the [`DataFrame`](DataFrame.md).

**API reference**: [`xs`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/xs.html)

Filters [`DataFrame`](DataFrame.md) by matching key values with key columns and removes key columns.

```
xs(vararg keyValues: Any?)
     [ { keyColumns } ]
```

When `keyColumns` are not specified, it takes the first `n` columns in depth-first order (looking inside [`ColumnGroup`](DataColumn.md#columngroup)), where `n` is the number of given `keyValues`.

<!---FUN xs-->

```kotlin
df.xs("Charlie", "Chaplin")

df.xs("Moscow", true) { city and isHappy }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.xs.html" width="100%"/>
<!---END-->
