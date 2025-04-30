[//]: # (title: percentile)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes the specified percentile of values.

Is available for `Comparable` columns. [`NA` values](nanAndNa.md#na) (`null` and `NaN`) are ignored.

<!---FUN percentileModes-->

```kotlin
df.percentile(25.0) // percentile of values per every comparable column
df.percentile(25.0) { age and weight } // percentile of all values in `age` and `weight`
df.percentileFor(25.0, skipNaN = true) { age and weight } // percentile of values per `age` and `weight` separately
df.percentileOf(25.0) { (weight ?: 0) / age } // percentile of expression evaluated for every row
```

<!---END-->

<!---FUN percentileAggregations-->

```kotlin
df.percentile(25.0)
df.age.percentile(25.0)
df.groupBy { city }.percentile(25.0)
df.pivot { city }.percentile(25.0)
df.pivot { city }.groupBy { name.lastName }.percentile(25.0)
```

<!---END-->

See [statistics](summaryStatistics.md#groupby-statistics) for details on complex data aggregations.
