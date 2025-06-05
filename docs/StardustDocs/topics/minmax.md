[//]: # (title: min / max)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes the [minimum / maximum](https://en.wikipedia.org/wiki/Maximum_and_minimum) of values.

`null` values in the input are ignored.
The operations either throw an exception when the input is empty (after filtering `null` or `NaN` values),
or they return `null` when using the `-orNull` overloads.

They are available for self-comparable columns
(so columns of type `T : Comparable<T>`, like `DateTime`, `String`, `Int`, etc.)
which includes all primitive number columns, but no mix of different number types.

All operations on `Double`/`Float` have the `skipNaN` option, which is
set to `false` by default. This means that if a `NaN` is present in the input, it will be propagated to the result.
When it's set to `true`, `NaN` values are ignored.

<!---FUN minmaxModes-->

```kotlin
df.min() // min of values per every comparable column
df.min { age and weight } // min of all values in `age` and `weight`
df.minFor(skipNaN = true) { age and weight } // min of values per `age` and `weight` separately
df.minOf { (weight ?: 0) / age } // min of expression evaluated for every row
df.minBy { age } // DataRow with minimal `age`
```

<!---END-->

<!---FUN minmaxAggregations-->

```kotlin
df.min()
df.age.min()
df.groupBy { city }.min()
df.pivot { city }.min()
df.pivot { city }.groupBy { name.lastName }.min()
```

<!---END-->

See [statistics](summaryStatistics.md#groupby-statistics) for details on complex data aggregations.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

### Type Conversion

The following automatic type conversions are performed for the `min` and `max` operations.
(Note that `null` only appears in the return type when using `-orNull` overloads).

| Conversion                       | Result for Empty Input |
|----------------------------------|------------------------|
| T -> T where T : Comparable\<T\> | null                   |
| Int -> Int                       | null                   |
| Byte -> Byte                     | null                   |
| Short -> Short                   | null                   |
| Long -> Long                     | null                   |
| Double -> Double                 | null                   |
| Float -> Float                   | null                   |
| Nothing -> Nothing               | null                   |
