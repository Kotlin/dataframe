[//]: # (title: mean)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes the mean of values.

Is available for numeric columns. Computed value has type `Double`.
Use `skipNA` flag to skip [`NA` values](nanAndNa.md#na) (`null` and `NaN`).

<!---FUN meanModes-->

```kotlin
df.mean() // mean of values per every numeric column
df.mean(skipNaN = true) { age and weight } // mean of all values in `age` and `weight`, skips NA
df.meanFor(skipNaN = true) { age and weight } // mean of values per `age` and `weight` separately, skips NA
df.meanOf { (weight ?: 0) / age } // median of expression evaluated for every row
```

<!---END-->

<!---FUN meanAggregations-->

```kotlin
df.mean()
df.age.mean()
df.groupBy { city }.mean()
df.pivot { city }.mean()
df.pivot { city }.groupBy { name.lastName }.mean()
```

<!---END-->

See [statistics](summaryStatistics.md#groupby-statistics) for details on complex data aggregations.
