[//]: # (title: Operations)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

DataFrame transformation pipeline usually consists of several modification operations, such as filtering, sorting, grouping, pivoting, adding/removing columns etc. 
DataFrame API is designed in functional style so that the whole processing pipeline can be represented as a single statement with a sequential chain of operations.
`DataFrame` object is immutable, so all operations defined for `DataFrame` will return a new instance reusing underlying data structures as much as possible.

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

Simple operations (such as [`filter`](filter.md) or [`select`](select.md)) return `DataFrame`, but more complex operations return an intermediate object that is used for further configuration of the operation. Let's call such operations **multiplex**.

Every multiplex operation configuration consists of:
- [column selector](ColumnSelectors.md) that is used to select target columns for the operation
- additional configuration
- terminal function that returns modified `DataFrame`

Multiplex operations usually end with `into` or `with` function. The following naming convention is used:
* `into` defines column names for storing operation results. Used in [`move`](move.md), [`group`](group.md), [`split`](split.md), [`merge`](merge.md), [`gather`](gather.md), [`groupBy`](groupBy.md), [`rename`](rename.md).
* `with` defines row-wise data transformation using [`row expression`](DataRow.md#row-expressions). Used in [`update`](update.md), [`convert`](convert.md), [`replace`](replace.md), [`pivot`](pivot.md).

## List of all DataFrame operations

* [add](add.md)  - add columns
* [append](append.md) - add rows
* [columns](columns.md) - get list of columns
* [concat](concat.md) - union rows
* [convert](convert.md) - change column values and/or column types
* [describe](describe.md) - basic column statistics
* [distinct](distinct.md) / [distinctBy](distinct.md#distinctby) - remove duplicated rows
* [drop](drop.md) / [dropLast](sliceRows.md#droplast) / [dropNulls](drop.md#dropnulls) / [dropNa](drop.md#dropna) - remove rows be condition
* [explode](explode.md) - spread list-like values vertically
* [fillNulls](fill.md#fillnulls) / [fillNaNs](fill.md#fillnans) / [fillNA](fill.md#fillna) - replace missing values
* [filter](filter.md) / [filterBy](filter.md#filterby) - filter rows
* [first](first.md) / [firstOrNull](first.md#firstornull) - first row by condition
* [flatten](flatten.md) - remove column groupings recursively
* [forEachRow](iterate.md) / [forEachColumn](iterate.md) - iterate over rows or columns
* [format](format.md) - conditional formatting for cell rendering
* [gather](gather.md) - convert columns into key-value pairs 
* [getColumn](getColumn.md) / [getColumnOrNull](getColumn.md#getcolumnornull) / [getColumnGroup](getColumn.md#getcolumngroup) / [getColumns](getColumn.md#getcolumns) - get one or several columns
* [group](group.md) - group columns into [`ColumnGroup`](DataColumn.md#columngroup)
* [groupBy](groupBy.md) - group rows by key columns
* [head](head.md) - top 5 rows
* [implode](implode.md) - collapse column values into lists
* [insert](insert.md) - insert column
* [join](join.md) - join dataframes by key columns
* [last](last.md) / [lastOrNull](last.md#lastornull) - last row by condition 
* [map](map.md) - map [`DataFrame`](DataFrame.md) columns to a new [`DataFrame`](DataFrame.md) or [`DataColumn`](DataColumn.md)
* [max](minmax.md) / [maxBy](minmax.md) / [maxOf](minmax.md) / [maxFor](minmax.md) - max of values 
* [mean](mean.md) / [meanOf](mean.md) / [meanFor](mean.md) - average of values
* [median](median.md) / [medianOf](median.md) / [medianFor](median.md) - median of values
* [merge](merge.md) - merge several columns into one
* [min](minmax.md) / [minBy](minmax.md) / [minOf](minmax.md) / [minFor](minmax.md) - min of values
* [move](move.md) - move columns or change column groupings
* [ncol](ncol.md) - number of columns
* [ndistinct](ndistinct.md) - number of distinct rows 
* [nrow](nrow.md) - number of rows
* [parse](parse.md) - convert `String` values into appropriate types
* [pivot](pivot.md) - convert column values into new columns
* [remove](remove.md) - remove columns
* [rename](rename.md) - rename columns
* [replace](replace.md) - replace columns
* [rows](rows.md) / [rowsReversed](rows.md#rowsreversed)
* [schema](schema.md) - schema of column hierarchy
* [select](select.md) - select subset of columns
* [shuffled](shuffle.md) - reorder rows randomly 
* [single](single.md) / [singleOrNull](single.md#singleornull) - single row by condition
* [sortBy](sortBy.md) / [sortByDesc](sortBy.md#sortbydesc) / [sortWith](sortBy.md#sortwith) - sort rows
* [split](split.md) - split column values into several columns or new rows
* [std](std.md) / [stdOf](std.md) / [stdFor](std.md) - standard deviation of values
* [sum](sum.md) / [sumOf](sum.md) / [sumFor](sum.md) - sum of values
* [take](sliceRows.md#take) / [takeLast](sliceRows.md#takelast) - first/last rows
* [ungroup](ungroup.md) - remove column grouping
* [update](update.md) - change column values preserving column types
