[//]: # (title: Operations)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Data transformation pipeline usually consists of several modification operations, such as filtering, sorting, grouping, pivoting, adding/removing columns etc. 
DataFrame API is designed in functional style so that the whole processing pipeline can be represented as a single statement with a sequential chain of operations.
`DataFrame` object is immutable and all operations return a new `DataFrame` instance reusing underlying data structures as much as possible.

<!---FUN multiCallOperations-->

```kotlin
df.update { age }.where { city == "Paris" }.with { it - 5 }
    .filter { isHappy && age > 100 }
    .move { name.firstName and name.lastName }.after { isHappy }
    .merge { age and weight }.by { "Age: ${it[0]}, weight: ${it[1]}" }.into("info")
    .rename { isHappy }.into("isOK")
```

<!---END-->

## Multiplex operations

Simple operations (such as [`filter`](filter.md) or [`select`](select.md)) return new `DataFrame` immediately, while more complex operations return an intermediate object that is used for further configuration of the operation. Let's call such operations **multiplex**.

Every multiplex operation configuration consists of:
- [column selector](ColumnSelectors.md) that is used to select target columns for the operation
- additional configuration functions
- terminal function that returns modified `DataFrame`

Multiplex operations usually end with `into` or `with` function. The following naming convention is used:
* `into` defines column names for storing operation results. Used in [`move`](move.md), [`group`](group.md), [`split`](split.md), [`merge`](merge.md), [`gather`](gather.md), [`groupBy`](groupBy.md), [`rename`](rename.md).
* `with` defines row-wise data transformation using [`row expression`](DataRow.md#row-expressions). Used in [`update`](update.md), [`convert`](convert.md), [`replace`](replace.md), [`pivot`](pivot.md).

## List of all DataFrame operations

* [add](add.md) — add columns
* [append](append.md) — add rows
* [columns](columns.md) / [columnNames](columnNames.md) / [columnTypes](columnTypes.md) — get list of columns, column names or column types
* [concat](concat.md) — union rows from several dataframes
* [convert](convert.md) — change column values and/or column types
* [corr](corr.md) — pairwise correlation of columns
* [cumSum](cumSum.md) — cumulative sum of column values
* [describe](describe.md) — basic column statistics
* [distinct](distinct.md) / [distinctBy](distinct.md#distinctby) — remove duplicated rows
* [drop](drop.md) / [dropLast](sliceRows.md#droplast) / [dropNulls](drop.md#dropnulls) / [dropNA](drop.md#dropna) — remove rows by condition
* [explode](explode.md) — spread lists and dataframes vertically into new rows
* [fillNulls](fill.md#fillnulls) / [fillNaNs](fill.md#fillnans) / [fillNA](fill.md#fillna) — replace missing values
* [filter](filter.md) / [filterBy](filter.md#filterby) — filter rows
* [first](first.md) / [firstOrNull](first.md#firstornull) — find first row by condition
* [flatten](flatten.md) — remove column groupings recursively
* [forEachRow](iterate.md) / [forEachColumn](iterate.md) — iterate over rows or columns
* [format](format.md) — conditional formatting for cell rendering
* [gather](gather.md) — convert pairs of column names and values into new columns
* [getColumn](getColumn.md) / [getColumnOrNull](getColumn.md#getcolumnornull) / [getColumnGroup](getColumn.md#getcolumngroup) / [getColumns](getColumn.md#getcolumns) — get one or several columns
* [group](group.md) — group columns into [`ColumnGroup`](DataColumn.md#columngroup)
* [groupBy](groupBy.md) — group rows by key columns
* [head](head.md) — get first 5 rows of dataframe
* [implode](implode.md) — collapse column values into lists grouping by other columns
* [insert](insert.md) — insert column
* [join](join.md) — join dataframes by key columns
* [last](last.md) / [lastOrNull](last.md#lastornull) — find last row by condition 
* [map](map.md) — map [`DataFrame`](DataFrame.md) columns to a new [`DataFrame`](DataFrame.md) or [`DataColumn`](DataColumn.md)
* [max](minmax.md) / [maxBy](minmax.md) / [maxOf](minmax.md) / [maxFor](minmax.md) — max of values 
* [mean](mean.md) / [meanOf](mean.md) / [meanFor](mean.md) — average of values
* [median](median.md) / [medianOf](median.md) / [medianFor](median.md) — median of values
* [merge](merge.md) — merge several columns into one
* [min](minmax.md) / [minBy](minmax.md) / [minOf](minmax.md) / [minFor](minmax.md) — min of values
* [move](move.md) — move columns or change column groupings
* [ncol](ncol.md) — number of columns
* [ndistinct](ndistinct.md) — number of unique rows 
* [nrow](nrow.md) — total number of rows
* [parse](parse.md) — try to convert `String` values into other types
* [pivot](pivot.md) / [pivotCounts](pivot.md#pivotcounts) / [pivotMatches](pivot.md#pivotmatches) — convert column values into new columns
* [remove](remove.md) — remove columns
* [rename](rename.md) — rename columns
* [replace](replace.md) — replace columns
* [rows](rows.md) / [rowsReversed](rows.md#rowsreversed) - get rows in direct or reversed order
* [schema](schema.md) — schema of column hierarchy
* [select](select.md) — select subset of columns
* [shuffle](shuffle.md) — reorder rows randomly 
* [single](single.md) / [singleOrNull](single.md#singleornull) — get single row by condition
* [sortBy](sortBy.md) / [sortByDesc](sortBy.md#sortbydesc) / [sortWith](sortBy.md#sortwith) — sort rows
* [sortColumnsBy](sortColumnsBy.md) — sort columns
* [split](split.md) — split column values into several columns or new rows
* [std](std.md) / [stdOf](std.md) / [stdFor](std.md) — standard deviation of values
* [sum](sum.md) / [sumOf](sum.md) / [sumFor](sum.md) — sum of values
* [take](sliceRows.md#take) / [takeLast](sliceRows.md#takelast) — get first/last rows
* [ungroup](ungroup.md) — remove column grouping
* [update](update.md) — update column values preserving column types
* [values](values.md) — `Sequence` of values traversed by row or by column 
* [valueCounts](valueCounts.md) — counts for unique values 
* [xs](xs.md) — slice dataframe by given key values

## Shortcut operations
Some operations are shortcuts for more general operations:
* [rename](rename.md), [group](group.md), [flatten](flatten.md) are special cases of [move](move.md)
* [valueCounts](valueCounts.md) is a special cases of [groupBy](groupBy.md)
* [pivotCounts](pivotCounts.md), [pivotMatches](pivotMatches.md) are special cases of [pivot](pivot.md)
* [fillNulls](fillNulls.md), [fillNaNs](fillNaNs.md), [fillNA](fillNA.md) are special cases of [update](update.md)
* [convert](convert.md) is a special case of [replace](replace.md)

Use these shortcuts to apply the most common `DataFrame` transformations easier. You can always fall back to the basic operations if you need more customization.  
