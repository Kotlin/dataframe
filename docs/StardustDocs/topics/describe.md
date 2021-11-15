[//]: # (title: describe)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Returns `DataFrame` with general statistics for all [`ValueColumns`](DataColumn.md#valuecolumn).

`ColumnGroups` and `FrameColumns` are traversed recursively down to `ValueColumns`.

Collected statistics:
* name - column name
* path - path to the column (for hierarchical dataframes)
* type - type of values
* count - number of rows
* unique - number of unique values
* nulls - number of `null` values
* top - the most common not `null` value
* freq - `top` value frequency
* mean - mean value (for numeric columns)
* std - standard deviation (for numeric columns)
* min - minimal value (for comparable columns)
* median - median value (for comparable columns)
* max - maximum value (for comparable columns)

<!---FUN describe-->

To describe only specific columns, pass them as an argument:

<!---FUN describeColumns-->
