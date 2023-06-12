[//]: # (title: duplicate)

Returns [`DataFrame`](DataFrame.md) with original [`DataRow`](DataRow.md) repeated `n` times.
```text
DataRow.duplicate(n): DataFrame
```

Returns [`FrameColumn`](DataColumn.md#framecolumn) with original [`DataFrame`](DataFrame.md) repeated `n` times. 
Resulting [`FrameColumn`](DataColumn.md#framecolumn) will have an empty [`name`](DataColumn.md#properties).
```text
DataFrame.duplicate(n): FrameColumn
```

Returns [`DataFrame`](DataFrame.md) where rows that satisfy to the given [condition](DataRow.md#row-conditions) are repeated `n` times. If `rowCondition` is not specified all rows will be duplicated.
```text
DataFrame.duplicateRows(n) [ { rowCondition } ]: DataFrame
```
