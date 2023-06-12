[//]: # (title: std)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes the standard deviation of values.

Is available for numeric columns. Computed value has type `Double`.

<!---FUN stdModes-->

```kotlin
df.std() // std of values per every numeric column
df.std { age and weight } // std of all values in `age` and `weight`
df.stdFor { age and weight } // std of values per `age` and `weight` separately, skips NA
df.stdOf { (weight ?: 0) / age } // std of expression evaluated for every row
```

<!---END-->

<!---FUN stdAggregations-->

```kotlin
df.std()
df.age.std()
df.groupBy { city }.std()
df.pivot { city }.std()
df.pivot { city }.groupBy { name.lastName }.std()
```

<!---END-->

See [statistics](summaryStatistics.md#groupby-statistics) for details on complex data aggregations.
