[//]: # (title: Rows)

## Row members
`DataRow` properties:
* `index` - sequential row number in `DataFrame`, starts from 0
* `prev` - previous row (`null` for the first row)
* `next` - next row (`null` for the last row)

`DataRow` functions:
* `getRow(Int)` - row from `DataFrame` by row index
* `neighbours(Iterable<Int>)` - sequence of nearest rows by relative index: `neighbours(-1..1)` will return previous, current and next row
* `get(column)` - cell value from column by current row index
* `values()` - list of all cell values from current row
* `df()` - `DataFrame` that current row belongs to

If some of these properties clash with generated extension properties, they still can be accessed as functions `index()`, `prev()`, `next()`
