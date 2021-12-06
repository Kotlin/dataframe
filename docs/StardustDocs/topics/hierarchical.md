[//]: # (title: Hierarchical data structures)

DataFrame can represent hierarchical data structures using two special types of columns:

* [`ColumnGroup`](DataColumn.md#columngroup) is a group of [columns](DataColumn.md)
* [`FrameColumn`](DataColumn.md#framecolumn) is a column of [dataframes](DataFrame.md)

You can read `DataFrame` [from json](read.md#reading-json) or [from in-memory object graph](createDataFrame.md#todataframe) preserving original tree structure.

Hierarchical columns can also appear as a result of some [modification operations](modify.md):
* [group](group.md) produces `ColumnGroup` 
* [groupBy](groupBy.md) produces `FrameColumn`
* [pivot](pivot.md) may produce `FrameColumn`
* [split](split.md) of `FrameColumn` will produce several `ColumnGroups`
* [implode](implode.md) converts `ColumnGroup` into `FrameColumn`
* [explode](explode.md) converts `FrameColumn` into `ColumnGroup`
* [merge](merge.md) converts `ColumnGroups` into `FrameColumn`
* etc.

