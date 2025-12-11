[//]: # (title: percentile)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes the specified [percentile](https://en.wikipedia.org/wiki/Percentile) of values.

This is also called the "centile" or the 100-[quantile](https://en.wikipedia.org/wiki/Quantile).

The 25th percentile is also known as the first [quartile](https://en.wikipedia.org/wiki/Quartile) (Q1),
the 50th percentile as the [median](median.md) or second [quartile](https://en.wikipedia.org/wiki/Quartile) (Q2),
and the 75th percentile as the third [quartile](https://en.wikipedia.org/wiki/Quartile) (Q3).

`null` values in the input are ignored.
The operations either throw an exception when the input is empty (after filtering `null` or `NaN` values),
or they return `null` when using the `-orNull` overloads.

All primitive numeric types are supported: `Byte`, `Short`, `Int`, `Long`, `Float`, and `Double`,
but no mix of different number types.
In these cases, the return type is always `Double?`.
The results of the operation on these types are interpolated using
[Quantile Estimation Method](#quantile-estimation-methods) R8.

The operation is also available for self-comparable columns
(so columns of type `T : Comparable<T>`, whose values are mutually comparable, like `DateTime`, `String`, etc.)
In this case, the return type remains `T?`.
The index of the result of the operation on these types is rounded using
[Quantile Estimation Method](#quantile-estimation-methods) R3.
NOTE: This logic also applies to other self-comparable `Number` types, like `BigDecimal`.
They will not be interpolated.

All operations on `Double`/`Float` have the `skipNaN` option, which is
set to `false` by default. This means that if a `NaN` is present in the input, it will be propagated to the result.
When it's set to `true`, `NaN` values are ignored.

### Quantile Estimation Methods

DataFrame
follows [Hyndman, Rob & Fan, Yanan. (1996). Sample Quantiles in Statistical Packages. The American Statistician. 50. 361-365. 10.1080/00031305.1996.10473566.](https://www.researchgate.net/publication/222105754_Sample_Quantiles_in_Statistical_Packages)
and [Apache Commons Statistics](https://svn.apache.org/repos/infra/websites/production/commons/content/proper/commons-statistics/commons-statistics-docs/apidocs/org/apache/commons/statistics/descriptive/Quantile.EstimationMethod.html)
for
the [9 commonly used quantile estimation methods](https://en.wikipedia.org/wiki/Quantile#Estimating_quantiles_from_a_sample).

For the `percentile` operation, DataFrame uses estimation method R3 when the given percentile needs
to be selected from the values (like for self-comparable columns), and R8 when the given percentile can be
interpolated from the values (of a numeric column).
R8 was the recommended method by Hyndman and Fan,
though other libraries, like [Numpy](https://numpy.org/doc/2.1/reference/generated/numpy.quantile.html)
default to R7, so slightly different results are to be expected.

In the future we might add an option to change the quantile estimation method.

<!---FUN percentileModes-->

```kotlin
df.percentile(25.0) // 25th percentile of values for every column with mutually comparable values
df.percentile(75.0) { age and weight } // 75th percentile of all values in `age` and `weight`
df.percentileFor(50.0, skipNaN = true) { age and name.firstName } // 50th percentile of values per `age` and `firstName` separately
df.percentileOf(75.0) { (weight ?: 0) / age } // 75th percentile of expression evaluated for every row
df.percentileBy(25.0) { age } // DataRow where the 25th percentile of `age` lies (index rounded using R3)
```

<!---END-->

<!---FUN percentileAggregations-->

```kotlin
df.percentile(25.0)
df.age.percentile(75.0)
df.groupBy { city }.percentile(50.0)
df.pivot { city }.percentile(75.0)
df.pivot { city }.groupBy { name.lastName }.percentile(25.0)
```

<!---END-->

See [statistics](summaryStatistics.md#groupby-statistics) for details on complex data aggregations.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

### Type Conversion

The following automatic type conversions are performed for the `percentile` operation.
(Note that `null` only appears in the return type when using `-orNull` overloads).

| Conversion                       | Result for Empty Input |
|----------------------------------|------------------------|
| T -> T where T : Comparable\<T\> | null                   |
| Int -> Double                    | null                   |
| Byte -> Double                   | null                   |
| Short -> Double                  | null                   |
| Long -> Double                   | null                   |
| Double -> Double                 | null                   |
| Float -> Double                  | null                   |
| Nothing -> Nothing               | null                   |
