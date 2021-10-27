[//]: # (title: union)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

[Unions](multipleDataFrames.md#union) all groups of [`GroupedDataFrame`](groupBy.md) into single `DataFrame`.

<!---FUN groupByUnion-->

```kotlin
df.groupBy { city }.union()
```

<!---END-->

Returns original ungrouped `DataFrame` with reordered rows according to grouping keys.
