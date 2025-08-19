[//]: # (title: std)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes the [standard deviation (std, σ)](https://en.wikipedia.org/wiki/Standard_deviation) of values.

`null` values are ignored.

All primitive numeric types are supported: `Byte`, `Short`, `Int`, `Long`, `Float`, and `Double`.

`std` also supports the "mixed" `Number` type, as long as the column consists only of the aforementioned
primitive numbers.
The numbers are automatically converted to a [common type](numberUnification.md) for the operation.

The return type is always `Double`; `Double.NaN` for empty columns.

All operations on `Double`/`Float`/`Number` have the `skipNaN` option, which is
set to `false` by default. This means that if a `NaN` is present in the input, it will be propagated to the result.
When it's set to `true`, `NaN` values are ignored.

### Delta Degrees of Freedom: DDoF

All `std` operations also have the `ddof`
([Delta Degrees of Freedom](https://en.wikipedia.org/wiki/Degrees_of_freedom_%28statistics%29)) argument.

The default is set to `1`, meaning DataFrame uses
[Bessel’s correction](https://en.wikipedia.org/wiki/Bessel%27s_correction)
to calculate the "unbiased sample standard deviation" by default.
This is also the standard in languages like [R](https://www.r-project.org/).
However, it is different from the "population standard deviation" (where `ddof = 0`),
which is used in libraries like [Numpy](https://numpy.org/doc/stable/reference/generated/numpy.std.html).

<!---FUN stdModes-->

```kotlin
df.std() // std of values per every numeric column
df.std { age and weight } // std of all values in `age` and `weight`
df.stdFor(skipNaN = true) { age and weight } // std of values per `age` and `weight` separately, skips NA
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
