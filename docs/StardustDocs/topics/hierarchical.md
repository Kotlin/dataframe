[//]: # (title: Hierarchical data structures)

DataFrame can represent hierarchical data structures using two special types of columns:

* [`ColumnGroup`](DataColumn.md#columngroup) is a group of [columns](DataColumn.md)
* [`FrameColumn`](DataColumn.md#framecolumn) is a column of [dataframes](DataFrame.md)

Therefore, you can create `DataFrame` [from json](read.md#reading-json) or [from graph of Kotlin objects](createDataFrame.md#createdataframe) preserving original tree structure.
