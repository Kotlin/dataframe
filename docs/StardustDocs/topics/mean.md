[//]: # (title: mean)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes the [mean (average)](https://en.wikipedia.org/wiki/Arithmetic_mean) of values.

`null` values are ignored.

All primitive numeric types are supported: `Byte`, `Short`, `Int`, `Long`, `Float`, and `Double`.

`mean` also supports the "mixed" `Number` type, as long as the column consists only of the aforementioned
primitive numbers.
The numbers are automatically converted to a [common type](numberUnification.md) for the operation.

The return type is always `Double`; `Double.NaN` for empty columns.

All operations on `Double`/`Float`/`Number` have the `skipNaN` option, which is
set to `false` by default. This means that if a `NaN` is present in the input, it will be propagated to the result.
When it's set to `true`, `NaN` values are ignored.

<!---FUN meanModes-->

```kotlin
df.mean() // mean of values per every numeric column
df.mean { age and weight } // mean of all values in `age` and `weight`
df.meanFor(skipNaN = true) { age and weight } // mean of values per `age` and `weight` separately, skips NaN
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

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

### Type Conversion

The following automatic type conversions are performed for the `mean` operation:

| Conversion                                                                 | Result for Empty Input |
|----------------------------------------------------------------------------|------------------------|
| Int -> Double                                                              | Double.NaN             |
| Byte -> Double                                                             | Double.NaN             |
| Short -> Double                                                            | Double.NaN             |
| Long -> Double                                                             | Double.NaN             |
| Double -> Double                                                           | Double.NaN             |
| Float -> Double                                                            | Double.NaN             |
| Number -> Conversion([Common number type](numberUnification.md)) -> Double | Double.NaN             |
| Nothing -> Double                                                          | Double.NaN             |
