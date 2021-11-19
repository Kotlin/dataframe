[//]: # (title: Group/ungroup/flatten columns)

* [`group`](group.md) - groups given columns into [`ColumnGroups`](DataColumn.md#columngroup).
* [`ungroup`](ungroup.md) - ungroups given [`ColumnGroups`](DataColumn.md#columngroup) by replacing them with their children columns
* [`flatten`](flatten.md) - recursively removes all column groupings under given [`ColumnGroups`](DataColumn.md#columngroup), remaining only [`ValueColumns`](DataColumn.md#valuecolumn) and [`FrameColumns`](DataColumn.md#framecolumn)

These operations are special cases of general [`move`](move.md) operation.
