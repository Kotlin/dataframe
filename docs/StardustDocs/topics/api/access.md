[//]: # (title: Access)

<!---IMPORT docs.api.Access-->

This section describes various ways to get a piece of data out from `DataFrame`
## Get column
Get single column by column name:

<!---FUN getColumnByName-->

Get single column by index (starting from 0):

<!---FUN getColumnByIndex-->
Get single column by [condition](columns.md#column-conditions):

<!---FUN getColumnByCondition-->

## Get row

Get single row by index (starting from 0):

<!---FUN getRowByIndex-->

Get single row by [condition](rows.md#row-conditions):

<!---FUN getRowByCondition-->

## Get cell

<!---FUN getCell-->
## Get several columns

Returns `DataFrame` with subset of columns

<!---FUN getColumnsByName-->

## Get several rows

The following operations return `DataFrame` with a subset of rows from original `DataFrame`.

<!---FUN getSeveralRows-->
To select several top / bottom rows see [take / takeLast / drop / dropLast](#take--takelast--drop--droplast) operations

To select several rows based on [row condition](rows.md#row-conditions) see [filter / drop](#filter-drop) operations

#### filter / drop
Filter rows by [row condition](rows.md#row-conditions)
`filter` keeps only rows that satisfy condition
`drop` removes all rows that satisfy condition

<!---FUN filterDrop-->

### dropNulls / dropNa
`dropNulls` removes rows with `null` values

<!---FUN dropNulls-->

If you want to remove not only `null`, but also `Double.NaN` values, use `dropNa` 

<!---FUN dropNa-->

### distinct

Removes duplicate rows.
The rows in the resulting `DataFrame` are in the same order as they were in the original `DataFrame`.

<!---FUN distinct-->

If columns are specified, resulting `DataFrame` will have only given columns with distinct values.

<!---FUN distinctColumns-->

To keep only the first row for every group of rows, grouped by some condition, use `distinctBy` or `distinctByExpr`
* `distinctBy` returns `DataFrame` with rows having distinct values in given columns.
* `distinctByExpr` returns `DataFrame` with rows having distinct values returned by given [row expression](rows.md#row-expressions).

<!---FUN distinctBy-->

## stdlib interop

`DataFrame` can be interpreted as an `Iterable<DataRow>`. Although `DataFrame` doesn't implement `Iterable` interface, it defines most extension functions available for `Iterable`
<!---FUN iterableApi-->

### asIterable / asSequence

`DataFrame` can be converted to `Iterable` or to `Sequence`:
<!---FUN asIterableOrSequence-->

