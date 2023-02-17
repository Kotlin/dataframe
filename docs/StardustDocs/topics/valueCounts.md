[//]: # (title: valueCounts)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Return [`DataFrame`](DataFrame.md) containing counts of unique values in [`DataFrame`](DataFrame.md) or `DataColumn`.

```kotlin
valueCounts(sort = true, ascending = false, dropNA = false)
     [ { columns } ]
```

**Parameters:**
* `sort: Boolean = true` — sort by count
* `ascending: Boolean = false` — sort in ascending order
* `dropNA: Boolean = true` — don't include counts of `NA` value
* `columns = all` — columns to use when counting unique combinations 

<!---FUN valueCounts-->

```kotlin
df.city.valueCounts()

df.valueCounts { name and city }
```

<!---END-->
