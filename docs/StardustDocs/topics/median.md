[//]: # (title: median)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes the [median](https://en.wikipedia.org/wiki/Median) of values.

This is also called the "middle" of a sorted list, the "50th [percentile](percentile.md)", or
the 2-[quantile](https://en.wikipedia.org/wiki/Quantile).

`null` values in the input are ignored.
The operations either throw an exception when the input is empty (after filtering `null` or `NaN` values),
or they return `null` when using the `-orNull` overloads.

All primitive numeric types are supported: `Byte`, `Short`, `Int`, `Long`, `Float`, and `Double`,
but no mix of different number types.
In these cases, the return type is always `Double?`.
When the number of values is even, the median is the average of the two middle values.

The operation is also available for self-comparable columns
(so columns of type `T : Comparable<T>`, like `DateTime`, `String`, etc.)
In this case, the return type remains `T?`.
When the number of values is even, the median is the low of the two middle values.

All operations on `Double`/`Float` have the `skipNaN` option, which is
set to `false` by default. This means that if a `NaN` is present in the input, it will be propagated to the result.
When it's set to `true`, `NaN` values are ignored.

<!---FUN medianModes-->

```kotlin
df.median() // median of values per every comparable column
df.median { age and weight } // median of all values in `age` and `weight`
df.medianFor(skipNaN = true) { age and weight } // median of values per `age` and `weight` separately
df.medianOf { (weight ?: 0) / age } // median of expression evaluated for every row
df.medianBy { age } // DataRow where the median age lies (lower-median for an even number of values)
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

### Type Conversion

The following automatic type conversions are performed for the `median` operation:

| Conversion                               | skipNaN option |
|------------------------------------------|----------------|
| T -> T? where T : Comparable<T>          |                |
| Int -> Double?                           |                |
| Byte -> Double?                          |                |
| Short -> Double?                         |                |
| Long -> Double?                          |                |
| Double -> Double?                        | yes            |
| Float -> Double?                         | yes            |
| Nothing / no values -> Nothing? (`null`) |                |
