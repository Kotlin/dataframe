[//]: # (title: sum)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes the sum of values.

`null` and `NaN` values are ignored.

<!---FUN statisticModes-->

```kotlin
df.sum() // sum of values per every numeric column
df.sum { age and weight } // sum of all values in `age` and `weight`
df.sumFor { age and weight } // sum of values per `age` and `weight` separately
df.sumOf { (weight ?: 0) / age } // sum of expression evaluated for every row
```

<!---END-->

<!---FUN sumAggregations-->

```kotlin
df.age.sum()
df.groupBy { city }.sum()
df.pivot { city }.sum()
df.pivot { city }.groupBy { name.lastName }.sum()
```

<!---END-->

See [statistics](summaryStatistics.md#groupby-statistics) for details on complex data aggregations.
