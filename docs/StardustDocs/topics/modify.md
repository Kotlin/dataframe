[//]: # (title: Modify)
<show-structure depth="3"/>

<tip> 

[`DataFrame`](DataFrame.md) object is immutable and all operations return a new instance of [`DataFrame`](DataFrame.md).

</tip>

## Naming conventions

[`DataFrame`](DataFrame.md) is a columnar data structure and is more oriented to column-wise operations. Most transformation operations start with [column selector](ColumnSelectors.md) that selects target columns for the operation.
Syntax of most column operations assumes that they are applied to columns, so they don't include word `column` in their naming.    

On the other hand, Kotlin DataFrame library follows `Koltin Collections` naming for row-wise operations 
as [`DataFrame`](DataFrame.md) can be interpreted as a [`Collection`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/) of rows. The slight naming difference with `Kotlin Collection` is that all operations are named in imperative way: `sortBy`, `shuffle` etc. 

**Pairs of column/row operations:**
* [add](add.md) columns / [append](append.md) rows
* [remove](remove.md) columns / [drop](drop.md) rows
* [select](select.md) columns / [filter](filter.md) rows
* [group](group.md) columns / [groupBy](groupBy.md) for rows
* [reorder](reorder.md) columns / [sortBy](sortBy.md) for rows
* [join](join.md) to unite columns / [concat](concat.md) to unite rows

**Horizontal (column) operations:**
* [add](add.md) — add columns
* [addId](add.md#addid) — add `id` column
* [flatten](flatten.md) — remove column groupings recursively
* [group](group.md) — group columns into [`ColumnGroup`](DataColumn.md#columngroup)
* [insert](insert.md) — insert column
* [map](map.md) — map columns into new [`DataFrame`](DataFrame.md) or [`DataColumn`](DataColumn.md)
* [merge](merge.md) — merge several columns into one
* [move](move.md) — move columns or change column groupings
* [remove](remove.md) — remove columns
* [rename](rename.md) — rename columns
* [reorder](reorder.md) — reorder columns
* [replace](replace.md) — replace columns
* [select](select.md) — select subset of columns
* [split](split.md) — split values into new columns
* [ungroup](ungroup.md) — remove column grouping

**Vertical (row) operations:**
* [append](append.md) — add rows
* [concat](concat.md) — union rows from several [`DataFrames`](DataFrame.md)
* [distinct](distinct.md) / [distinctBy](distinct.md#distinctby) — remove duplicated rows
* [drop](drop.md) / [dropLast](sliceRows.md#droplast) / [dropWhile](sliceRows.md#dropwhile) / [dropNulls](drop.md#dropnulls) / [dropNA](drop.md#dropna) — remove rows by condition
* [duplicate](duplicate.md) — duplicate rows 
* [explode](explode.md) — spread lists and [`DataFrames`](DataFrame.md) vertically into new rows
* [filter](filter.md) / [filterBy](filter.md#filterby) — filter rows
* [implode](implode.md) — merge column values into lists grouping by other columns
* [reverse](reverse.md) — reverse rows 
* [shuffle](shuffle.md) — reorder rows randomly
* [sortBy](sortBy.md) / [sortByDesc](sortBy.md#sortbydesc) / [sortWith](sortBy.md#sortwith) — sort rows
* [split](split.md) — split values into new rows
* [take](sliceRows.md#take) / [takeLast](sliceRows.md#takelast) / [takeWhile](sliceRows.md#takewhile) — get first/last rows

**Value modification:**
* [convert](convert.md) — convert values into new types
* [parse](parse.md) — try to convert `String` values into appropriate types
* [unfold](unfold.md) — convert / "unfold" objects to [`ColumnGroup`](DataColumn.md#columngroup)
* [update](update.md) — update values preserving column types
* [fillNulls](fill.md#fillnulls) / [fillNaNs](fill.md#fillnans) / [fillNA](fill.md#fillna) — replace missing values

**Reshaping:**
* [pivot](pivot.md) / [pivotCounts](pivot.md#pivotcounts) / [pivotMatches](pivot.md#pivotmatches) — convert values into new columns
* [gather](gather.md) — convert pairs of column names and values into `key` and `value` columns

**Learn how to:**
* [Slice rows](sliceRows.md)
* [Select columns](select.md)
* [Filter rows](filterRows.md)
* [Reorder rows](reorderRows.md)
* [Group/concat rows](groupByConcat.md)
* [Update/convert values](updateConvert.md)
* [Split/merge values](splitMerge.md)
* [Add/map/remove columns](addRemove.md)
* [Move/rename columns](moveRename.md)
* [Group/ungroup/flatten columns](groupUngroupFlatten.md)
* [Insert/replace columns](insertReplace.md)
* [Explode/implode columns](explodeImplode.md)
* [Pivot/gather columns](pivotGather.md)
* [Append values](append.md)
* [Adjust schema](adjustSchema.md) 
