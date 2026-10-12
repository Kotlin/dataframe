# quantile

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.QuantileSamples-->

Computes a quantile on a [`DataColumn`](DataColumn.md) using an explicit estimator.
The probability `p` is in `[0, 1]`. In contrast, [`percentile`](percentile.md) uses `[0, 100]`.
This first API exposes the four estimators already implemented in the shared engine.
Existing percentile signatures and estimator defaults remain unchanged.

| Method | Rule | Supported input | Return type |
|---|---|---|---|
| `QuantileMethod.R1` | Observation at ceiling of `n × p`, clipped to the sample bounds | Self-comparables | Input value type |
| `QuantileMethod.R3` | Observation nearest `n × p`, with ties to an even one-based position | Self-comparables | Input value type |
| `QuantileMethod.R7` | Interpolate at one-based position `1 + (n − 1) × p` | Primitive numbers | `Double` |
| `QuantileMethod.R8` | Interpolate at one-based position `1/3 + (n + 1/3) × p` | Primitive numbers | `Double` |

`n` counts non-null observations after optionally excluding NaN.
R3 is the Hyndman–Fan nearest-observation estimator, rather than R7-based nearest interpolation.
The companion shortcuts retain their selecting/interpolating types so overload resolution determines the return type.

<!---FUN quantileSelectingMethods-->
<!---END-->

R7 and R8 can produce different estimates for small samples.

<!---FUN quantileInterpolatingMethods-->
<!---END-->

Selecting methods preserve primitive types even for a singleton and compare large Long values, precise decimals,
strings, dates, and other self-comparables without conversion to Double.

<!---FUN quantileExactSelection-->
<!---END-->

Interpolating methods support `Byte`, `Short`, `Int`, `Long`, `Float`, and `Double` columns and return Double.
Converting large Long values can lose precision. Arbitrary-precision and mixed Number columns are not interpolated.
R2, R4, R5, R6, R9, vector probabilities, and dedicated dataframe/group selector overloads are outside this first scope.

Null cells are ignored. NaN propagates by default; use `skipNaN = true` to exclude NaN observations.
Float selecting results retain Float NaN.

<!---FUN quantileSkippingNaN-->
<!---END-->

`quantileOrNull` returns null when no observations remain. `quantile` throws `NoSuchElementException` instead.
Invalid probabilities, including NaN and infinities, are rejected even for empty input.

<!---FUN quantileEmptyColumn-->
<!---END-->

Kotlin Comparable ordering is used, including signed zeros. Input values are not modified.
Interpolation returns exact observations directly at integral estimated positions and preserves identical infinite
observations. Other interpolation arithmetic, including opposite infinities, can produce NaN.
Returning an exact observation preserves its signed zero too.
These exact-observation corrections also apply to the shared percentile/median engine.

For a numeric column, its existing percentile default is R8, not R7.

<!---FUN quantileAndPercentileDefaults-->
<!---END-->
