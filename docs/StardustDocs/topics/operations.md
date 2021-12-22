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

<tip>

You can plain with "people" dataset that is used in present guide [here](https://datalore.jetbrains.com/view/notebook/aOTioEClQQrsZZBKeUPAQj)

</tip>

## Multiplex operations

Simple operations (such as [`filter`](filter.md) or [`select`](select.md)) return new `DataFrame` immediately, while more complex operations return an intermediate object that is used for further configuration of the operation. Let's call such operations **multiplex**.

Every multiplex operation configuration consists of:
- [column selector](ColumnSelectors.md) that is used to select target columns for the operation
- additional configuration functions
- terminal function that returns modified `DataFrame`

Most multiplex operations end with `into` or `with` function. The following naming convention is used:
* `into` defines column names for storing operation results. Used in [`move`](move.md), [`group`](group.md), [`split`](split.md), [`merge`](merge.md), [`gather`](gather.md), [`groupBy`](groupBy.md), [`rename`](rename.md).
* `with` defines row-wise data transformation with [`row expression`](DataRow.md#row-expressions). Used in [`update`](update.md), [`convert`](convert.md), [`replace`](replace.md), [`pivot`](pivot.md).

## List of DataFrame operations

* [add](add.md) — add columns
* [addId](add.md#addid) — add `id` column
* [append](append.md) — add rows
* [columns](columns.md) / [columnNames](columnNames.md) / [columnTypes](columnTypes.md) — get list of top-level columns, column names or column types
* [columnsCount](columnsCount.md) — number of top-level columns
* [concat](concat.md) — union rows from several dataframes
* [convert](convert.md) — change column values and/or column types
* [corr](corr.md) — pairwise correlation of columns
* [count](count.md) — number of rows that match condition 
* [countDistinct](countDistinct.md) — number of unique rows
* [cumSum](cumSum.md) — cumulative sum of column values
* [describe](describe.md) — basic column statistics
* [distinct](distinct.md) / [distinctBy](distinct.md#distinctby) — remove duplicated rows
* [drop](drop.md) / [dropLast](sliceRows.md#droplast) / [dropNulls](drop.md#dropnulls) / [dropNA](drop.md#dropna) — remove rows by condition
* [explode](explode.md) — spread lists and dataframes vertically into new rows
* [fillNulls](fill.md#fillnulls) / [fillNaNs](fill.md#fillnans) / [fillNA](fill.md#fillna) — replace missing values
* [filter](filter.md) / [filterBy](filter.md#filterby) — filter rows by condition
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
* [map](map.md) — map columns into new [`DataFrame`](DataFrame.md) or [`DataColumn`](DataColumn.md)
* [max](minmax.md) / [maxBy](minmax.md) / [maxOf](minmax.md) / [maxFor](minmax.md) — max of values 
* [mean](mean.md) / [meanOf](mean.md) / [meanFor](mean.md) — average of values
* [median](median.md) / [medianOf](median.md) / [medianFor](median.md) — median of values
* [merge](merge.md) — merge several columns into one
* [min](minmax.md) / [minBy](minmax.md) / [minOf](minmax.md) / [minFor](minmax.md) — min of values
* [move](move.md) — move columns or change column groupings
* [parse](parse.md) — try to convert strings into other types
* [pivot](pivot.md) / [pivotCounts](pivot.md#pivotcounts) / [pivotMatches](pivot.md#pivotmatches) — convert values into new columns
* [remove](remove.md) — remove columns
* [rename](rename.md) — rename columns
* [reorder](reorder.md) / [reorderColumnsBy](reorder.md#reordercolumnsby) / [reorderColumnsByName](reorder.md#reordercolumnsbyname) — reorder columns
* [replace](replace.md) — replace columns
* [rows](rows.md) / [rowsReversed](rows.md#rowsreversed) — get rows in direct or reversed order
* [rowsCount](rowsCount.md) — number of rows
* [schema](schema.md) — schema of columns: names, types and hierarchy
* [select](select.md) — select subset of columns
* [shuffle](shuffle.md) — reorder rows randomly 
* [single](single.md) / [singleOrNull](single.md#singleornull) — get single row by condition
* [sortBy](sortBy.md) / [sortByDesc](sortBy.md#sortbydesc) / [sortWith](sortBy.md#sortwith) — sort rows
* [split](split.md) — split column values into new rows/columns or inplace into lists
* [std](std.md) / [stdOf](std.md) / [stdFor](std.md) — standard deviation of values
* [sum](sum.md) / [sumOf](sum.md) / [sumFor](sum.md) — sum of values
* [take](sliceRows.md#take) / [takeLast](sliceRows.md#takelast) — get first/last rows
* [toList](toList.md) / [toListOf](toList.md#tolistof) — export dataframe into a list of data classes
* [toMap](toMap.md) — export dataframe into a map from column names to column values
* [ungroup](ungroup.md) — remove column groupings
* [update](update.md) — update column values preserving column types
* [values](values.md) — `Sequence` of values traversed by row or by column 
* [valueCounts](valueCounts.md) — counts for unique values 
* [xs](xs.md) — slice dataframe by given key values

## Shortcut operations
Some operations are shortcuts for more general operations:
* [rename](rename.md), [group](group.md), [flatten](flatten.md) are special cases of [move](move.md)
* [valueCounts](valueCounts.md) is a special case of [groupBy](groupBy.md)
* [pivotCounts](pivot.md#pivotcounts), [pivotMatches](pivot.md#pivotmatches) are special cases of [pivot](pivot.md)
* [fillNulls](fill.md#fillnulls), [fillNaNs](fill.md#fillnans), [fillNA](fill.md#fillna) are special cases of [update](update.md)
* [convert](convert.md) is a special case of [replace](replace.md)

You can use these shortcuts to apply the most common `DataFrame` transformations easier, but you can always fall back to general operations if you need more customization.    
