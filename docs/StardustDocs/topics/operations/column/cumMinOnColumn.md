# cumMin

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.CumMinSamples-->

Computes the cumulative minimum from the first value to the last value in a [`DataColumn`](DataColumn.md).
The result preserves the column name, row count, element type, and declared nullability.
The values must be self-comparable; numbers are not converted to another numeric type.
An empty column remains empty. Comparison ties retain the first minimum.

With `skipNA = true` (the default), `null` and `NaN` remain at their original positions and are ignored
when computing subsequent minima. This does not fill missing cells.

<!---FUN cumMinOnColumn-->
<!---END-->

With `skipNA = false`, the first missing value is repeated through the rest of the column.
A first missing value of `null` propagates as `null`; a first missing value of `NaN` propagates as `NaN`.

<!---FUN cumMinWithoutSkipping-->
<!---END-->

This first API operates on columns. Use existing [`convert`](convert.md) column transformations to apply it
to a dataframe, and [`updateGroups`](groupBy.md#transformation) to reset the running minimum for each group.
Dedicated dataframe-selector overloads are outside this API.

See also [`min`](minmax.md) for a single minimum and [`cumSum`](cumSum.md) for cumulative sums.
