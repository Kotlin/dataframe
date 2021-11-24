[//]: # (title: Hierarchical data structures)

DataFrame can represent hierarchical data structures using two special types of columns:

* [`ColumnGroup`](DataColumn.md#columngroup) is a group of [columns](DataColumn.md)
* [`FrameColumn`](DataColumn.md#framecolumn) is a column of [dataframes](DataFrame.md)

You can create `DataFrame` [from json](read.md#reading-json) or [from graph of Kotlin objects](createDataFrame.md#createdataframe) preserving original tree structure.

Hierarchical columns can also appear as a result of some [modification operations](Modify.md):
* [group](group.md) produces `ColumnGroup` 
* [groupBy](groupBy.md) produces `FrameColumn`
* [pivot](pivot.md) may produce `FrameColumn`
* [split](split.md) may produce `ColumnGroup`
* [implode](implode.md) converts `ColumnGroup` into `FrameColumn`
* [explode](explode.md) converts `FrameColumn` into `ColumnGroup`

