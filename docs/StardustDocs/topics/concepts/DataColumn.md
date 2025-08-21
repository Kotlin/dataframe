[//]: # (title: DataColumn)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

[`DataColumn`](DataColumn.md) represents a column of values.
It can store objects of primitive or reference types, 
or other [`DataFrame`](DataFrame.md) objects.

See [how to create columns](createColumn.md)

### Properties
* `name: String` — name of the column; should be unique within containing dataframe
* `path: ColumnPath` — path to the column; depends on the way column was retrieved from dataframe
* `type: KType` — type of elements in the column
* `hasNulls: Boolean` — flag indicating whether column contains `null` values
* `values: Iterable<T>` — column data
* `size: Int` — number of elements in the column

### Column kinds
[`DataColumn`](DataColumn.md) instances can be one of three subtypes: `ValueColumn`, [`ColumnGroup`](DataColumn.md#columngroup) or [`FrameColumn`](DataColumn.md#framecolumn)

#### ValueColumn

Represents a sequence of values. 

It can store values of primitive (integers, strings, decimals, etc.) or reference types.
Currently, it uses [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/) as underlying data storage.

#### ColumnGroup

Container for nested columns. Used to create column hierarchy.

You can create column groups using the group operation or by splitting inward — see [group](group.md) and [split](split.md) for details.

#### FrameColumn

Special case of [`ValueColumn`](#valuecolumn) that stores another [`DataFrame`](DataFrame.md) objects as elements. 

[`DataFrame`](DataFrame.md) stored in [`FrameColumn`](DataColumn.md#framecolumn) may have different schemas. 

[`FrameColumn`](DataColumn.md#framecolumn) may appear after [reading](read.md) from JSON or other hierarchical data structures, or after grouping operations such as [groupBy](groupBy.md) or [pivot](pivot.md).

