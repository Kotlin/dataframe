[//]: # (title: Statistical estimation methods)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.EstimationMethodSamples-->

Use the `method` argument on [percentile](percentile.md) and [median](median.md) to choose how a statistic is
estimated between observations. Percentiles still use the `[0, 100]` scale; the median is the 50th percentile.

The methods come from `org.jetbrains.kotlinx.dataframe.math.QuantileEstimationMethod`.

| Method | Rank in a sorted sample of size `n`, with probability `p` | Result |
|--------|----------------------------------------------------------|--------|
| R1 | Ceiling of `n * p` | An original observation |
| R3 | Nearest integer to `n * p`, ties rounded to the even rank | An original observation |
| R7 | `1 + (n - 1) * p` | Interpolated primitive numbers, returned as `Double` |
| R8 | `1/3 + (n + 1/3) * p` | Interpolated primitive numbers, returned as `Double` |

Ranks are one-based and clipped to the sample bounds. Other Hyndman–Fan methods remain outside this change.

Calls without a method retain the existing defaults: R8 for primitive numeric values, R3 for other
self-comparable values, and R3 for `-By` operations.

### Selecting and interpolating

Selecting methods preserve the observation type, including primitive numbers and precise `Long` values.
Interpolating methods return `Double`, including for a singleton sample. They support `Byte`, `Short`, `Int`,
`Long`, `Float`, and `Double`, and can lose precision when converting large `Long` values.

<!---FUN estimationMethodColumn-->
<!---END-->

Self-comparable values such as strings and dates support selecting methods. Interpolation of arbitrary-precision
numbers or mixed `Number` columns is unsupported.

<!---FUN estimationMethodMedian-->
<!---END-->

### Frames, groups, and pivots

The explicit-method overloads cover DataColumn, DataRow, DataFrame, Grouped/GroupBy, Pivot, and PivotGroupBy,
including `-Of`, `-For`, and `-By` modes and their existing nullable variants.
Use column names or selector lambdas; deprecated property/reference overloads keep their existing signatures.
Compiler-plugin generation of result accessors for explicit-method aggregations is not yet supported.
Access the result by column name or with explicitly typed selectors.

When several columns are aggregated, the method applies to every selected column. Select only primitive numeric
columns when choosing an interpolating method; an incompatible column causes an exception.

<!---FUN estimationMethodDataFrame-->
<!---END-->

<!---FUN estimationMethodGrouped-->
<!---END-->

### Null, NaN, and empty input

Null comparison values are ignored. By default NaN propagates; `skipNaN = true` excludes it before ranking.
Empty scalar input returns null for `-OrNull` and throws `NoSuchElementException` otherwise.
Empty aggregate cells are null. Invalid explicit percentiles are rejected even for empty input.

`-By` accepts only selecting methods because it returns an existing element or row. It selects the first NaN
when `skipNaN = false`; with `skipNaN = true`, it ignores NaN while retaining original row positions.

<!---FUN estimationMethodBy-->
<!---END-->

Interpolation returns an exact observation directly at integral positions and preserves identical infinities
and exact signed zeros. Other interpolation arithmetic can overflow or produce NaN, including opposite infinities.
