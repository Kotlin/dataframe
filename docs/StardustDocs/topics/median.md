[//]: # (title: median)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes the median of values.

Is available for `Comparable` columns. `null` and `NaN` values are ignored.

<!---FUN medianModes-->

```kotlin
df.median() // median of values per every comparable column
df.median { age and weight } // median of all values in `age` and `weight`
df.medianFor { age and weight } // median of values per `age` and `weight` separately
df.medianOf { (weight ?: 0) / age } // median of expression evaluated for every row
```

<!---END-->

<!---FUN medianAggregations-->

```kotlin
df.median()
df.age.median()
df.groupBy { city }.median()
df.pivot { city }.median()
df.pivot { city }.groupBy { name.lastName }.median()
```

<!---END-->

See [statistics](summaryStatistics.md#groupby-statistics) for details on complex data aggregations.
