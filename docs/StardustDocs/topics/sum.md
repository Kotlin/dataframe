[//]: # (title: sum)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes the [sum](https://en.wikipedia.org/wiki/Summation) of values.

`null` values are ignored.

All primitive numeric types are supported: `Byte`, `Short`, `Int`, `Long`, `Float`, and `Double`.

`sum` also supports the "mixed" `Number` type, as long as the column consists only of the aforementioned
primitive numbers.
The numbers are automatically converted to a [common type](numberUnification.md) for the operation.

All operations on `Double`/`Float`/`Number` have the `skipNaN` option, which is
set to `false` by default. This means that if a `NaN` is present in the input, it will be propagated to the result.
When it's set to `true`, `NaN` values are ignored.

```kotlin

<!---FUN statisticModes -->

```kotlin
df.sum() // sum of values per every numeric column
df.sum { age and weight } // sum of all values in `age` and `weight`
df.sumFor(skipNaN = true) { age and weight } // sum of values per `age` and `weight` separately
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

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

### Type Conversion

The following automatic type conversions are performed for the `sum` operation:

| Conversion                                                                 | Result for Empty Input |
|----------------------------------------------------------------------------|------------------------|
| Int -> Int                                                                 | 0                      |
| Byte -> Int                                                                | 0                      |
| Short -> Int                                                               | 0                      |
| Long -> Long                                                               | 0L                     |
| Double -> Double                                                           | 0.0                    |
| Float -> Float                                                             | 0.0f                   |
| Number -> Conversion([Common number type](numberUnification.md)) -> Number | 0.0                    |
| Nothing -> Double                                                          | 0.0                    |
