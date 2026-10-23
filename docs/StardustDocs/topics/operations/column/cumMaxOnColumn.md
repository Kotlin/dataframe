# cumMax

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.CumMaxSamples-->

Computes the cumulative maximum from the first value to the last value in a [`DataColumn`](DataColumn.md).
The result preserves the column name, row count, element type, declared nullability, and default value.
The values must be self-comparable; numbers are not converted to another numeric type.
An empty column remains empty. Comparison ties retain the first maximum.
Floating-point signed zeros follow `Comparable` ordering: positive zero is greater than negative zero.

With `skipNA = true` (the default), `null` and `NaN` remain at their original positions and are ignored
when computing subsequent maxima. This does not fill missing cells.

<!---FUN cumMaxOnColumn-->
<!---END-->

With `skipNA = false`, the first missing value is repeated through the rest of the column.
A first missing value of `null` propagates as `null`; a first missing value of `NaN` propagates as `NaN`.

<!---FUN cumMaxWithoutSkipping-->
<!---END-->

Use [`convert`](convert.md) column transformations to apply it to a dataframe.

<!---FUN cumMaxWithColumnConversion-->
<!---END-->

Use [`updateGroups`](groupBy.md#transformation) to reset the running maximum for each group.
When concatenating the results, rows are collected in group order.

<!---FUN cumMaxWithGroups-->
<!---END-->

Dedicated dataframe-selector and grouped overloads are outside this column API.
See also [`max`](minmax.md) for a single maximum and [`cumSum`](cumSum.md) for cumulative sums.
