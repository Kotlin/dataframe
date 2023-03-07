[//]: # (title: Hierarchical data structures)

[`DataFrame`](DataFrame.md) can represent hierarchical data structures using two special types of columns:

* [`ColumnGroup`](DataColumn.md#columngroup) is a group of [columns](DataColumn.md)
* [`FrameColumn`](DataColumn.md#framecolumn) is a column of [dataframes](DataFrame.md)

You can read [`DataFrame`](DataFrame.md) [from json](read.md#reading-json) or [from in-memory object graph](createDataFrame.md#todataframe) preserving original tree structure.

Hierarchical columns can also appear as a result of some [modification operations](modify.md):
* [group](group.md) produces [`ColumnGroup`](DataColumn.md#columngroup) 
* [groupBy](groupBy.md) produces [`FrameColumn`](DataColumn.md#framecolumn)
* [pivot](pivot.md) may produce [`FrameColumn`](DataColumn.md#framecolumn)
* [split](split.md) of [`FrameColumn`](DataColumn.md#framecolumn) will produce several [`ColumnGroup`](DataColumn.md#columngroup)
* [implode](implode.md) converts [`ColumnGroup`](DataColumn.md#columngroup) into [`FrameColumn`](DataColumn.md#framecolumn)
* [explode](explode.md) converts [`FrameColumn`](DataColumn.md#framecolumn) into [`ColumnGroup`](DataColumn.md#columngroup)
* [merge](merge.md) converts [`ColumnGroup`](DataColumn.md#columngroup) into [`FrameColumn`](DataColumn.md#framecolumn)
* etc.

Operations in the navigation tree are grouped such that you can find operations and their respective inverse together, like `group` and `ungroup`. This allows you to quickly find out how to simplify any hierarchical structure you come across.
