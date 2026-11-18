[//]: # (title: getColumn)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples-->

<web-summary>
Discover the `getColumn` operation for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover the `getColumn` operation for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover the `getColumn` operation for Kotlin DataFrame.
</link-summary>

## getColumn

Returns a column from a [`DataFrame`](DataFrame.md) or [`ColumnGroup`](DataColumn.md#columngroup) as a
[`DataColumn`](DataColumn.md). The overloads find a column by its top-level name, its [`ColumnPath`](ColumnSelectors.md),
its zero-based top-level index, or a single [column selector](ColumnSelectors.md). The selector and path overloads can
find nested columns; a name or index only looks among the top-level columns.

The name, path, and index overloads return `DataColumn<*>`; the selector overload returns a typed `DataColumn<C>`.
They throw if the requested column does not exist. A selector must resolve to exactly one column: it throws
`IllegalStateException` if the selector refers to a missing column, `NoSuchElementException` if it resolves to no columns,
or `IllegalArgumentException` if it resolves to more than one column.

```text
df.getColumn(name: String): DataColumn<*>
df.getColumn(path: ColumnPath): DataColumn<*>
df.getColumn(index: Int): DataColumn<*>
df.getColumn(selector: ColumnSelector<T, C>): DataColumn<C>
```

**API reference**: [`getColumn`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/get-column.html)

See also [select](select.md), which returns a new dataframe with selected columns.

### Parameters {id="parameters_getColumn"}

* `name: String` — the name of a top-level column.
* `path: ColumnPath` — the path to a column, including its parent column groups.
* `index: Int` — the zero-based index of a top-level column.
* `selector: ColumnSelector<T, C>` — a [single-column selector](ColumnSelectors.md) that can select a nested column.

### Examples {id="examples_getColumn"}

The following dataframe will be used in the examples below:

<!---FUN getColumnDf-->

```kotlin
val input = df
```

<inline-frame src="./resources/getColumnDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

Get the `age` column by its top-level name:

<!---FUN getColumnByName-->
<tabs>
<tab title="Properties">

```kotlin
val column = df.getColumn { age }
```

</tab>
<tab title="Strings">

```kotlin
df.getColumn("age")
```

</tab></tabs>
<inline-frame src="./resources/getColumnByName.html" width="100%" height="500px"></inline-frame>

<!---END-->

Use a path to get a nested column:

<!---FUN getColumnByPath-->

```kotlin
val column = df.getColumn(pathOf("name", "firstName"))
```

<inline-frame src="./resources/getColumnByPath.html" width="100%" height="500px"></inline-frame>

<!---END-->

Get a top-level column by its zero-based index:

<!---FUN getColumnByIndex-->

```kotlin
val column = df.getColumn(1)
```

<inline-frame src="./resources/getColumnByIndex.html" width="100%" height="500px"></inline-frame>

<!---END-->

## getColumnOrNull

Returns a column as a [`DataColumn`](DataColumn.md), or `null` if it is missing. It works on a [`DataFrame`](DataFrame.md)
or a [`ColumnGroup`](DataColumn.md#columngroup). A name looks up only top-level columns; use a [`ColumnPath`](ColumnSelectors.md)
or a single [column selector](ColumnSelectors.md) for a nested column. An index is zero-based and looks up a top-level
column; an out-of-range index returns `null`. A selector that resolves to anything other than one column also returns
`null`.

```text
df.getColumnOrNull(name: String): DataColumn<*>?
df.getColumnOrNull(path: ColumnPath): DataColumn<*>?
df.getColumnOrNull(index: Int): DataColumn<*>?
df.getColumnOrNull(column: ColumnSelector<T, C>): DataColumn<C>?
```

**API reference**: [`getColumnOrNull`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe/-columns-container/get-column-or-null.html)

### Parameters {id="parameters_getColumnOrNull"}

* `name: String` — the name of a top-level column.
* `path: ColumnPath` — the path to a column, including its parent column groups.
* `index: Int` — the zero-based index of a top-level column.
* `column: ColumnSelector<T, C>` — a [single-column selector](ColumnSelectors.md) that can select a nested column.

### Examples {id="examples_getColumnOrNull"}

Get a column that exists, or safely handle a missing column:

<!---FUN getColumnOrNull-->
<tabs>
<tab title="Properties">

```kotlin
val column = df.getColumnOrNull { age }
df.getColumnOrNull(-1)
```

</tab>
<tab title="Strings">

```kotlin
df.getColumnOrNull("address")
```

</tab></tabs>
<inline-frame src="./resources/getColumnOrNull.html" width="100%" height="500px"></inline-frame>

<!---END-->

## getColumnGroup

Returns a column as a [`ColumnGroup`](DataColumn.md#columngroup) from a [`DataFrame`](DataFrame.md) or another
[`ColumnGroup`](DataColumn.md#columngroup). The overloads find it by its top-level name, its [`ColumnPath`](ColumnSelectors.md),
its zero-based top-level index, or a single [column selector](ColumnSelectors.md). Only the path and selector overloads can
find nested columns. A name, path, or index that does not resolve to a column throws; an existing column that is not a
`ColumnGroup` causes a `ClassCastException`.
To get `null` instead of an exception when the column is missing, combine [`getColumnOrNull`](#getcolumnornull) with
`asColumnGroup()` (see the examples below).

```text
df.getColumnGroup(name: String): ColumnGroup<*>
df.getColumnGroup(columnPath: ColumnPath): ColumnGroup<*>
df.getColumnGroup(index: Int): ColumnGroup<*>
df.getColumnGroup(column: ColumnSelector<T, DataRow<C>>): ColumnGroup<C>
```

**API reference**: [`getColumnGroup`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/get-column-group.html)

### Parameters {id="parameters_getColumnGroup"}

* `name: String` — the name of a top-level column group.
* `columnPath: ColumnPath` — the path to a column group, including its parent column groups.
* `index: Int` — the zero-based index of a top-level column group.
* `column: ColumnSelector<T, DataRow<C>>` — a [single-column selector](ColumnSelectors.md) that selects a column group.

### Examples {id="examples_getColumnGroup"}

Get the `name` column group by its name:

<!---FUN getColumnGroupByName-->
<tabs>
<tab title="Properties">

```kotlin
val group = df.getColumnGroup { name }
```

</tab>
<tab title="Strings">

```kotlin
df.getColumnGroup("name")
```

</tab></tabs>
<inline-frame src="./resources/getColumnGroupByName.html" width="100%" height="500px"></inline-frame>

<!---END-->

Get a column group, or `null` if there is no column with that name.
Only a missing column gives `null`; an existing column that is not a column group still causes a `ClassCastException`:

<!---FUN getColumnOrNullAsColumnGroup-->

```kotlin
val group = df.getColumnOrNull("name")?.asColumnGroup()
df.getColumnOrNull("address")?.asColumnGroup()
```

<inline-frame src="./resources/getColumnOrNullAsColumnGroup.html" width="100%" height="500px"></inline-frame>

<!---END-->

## getFrameColumn

Returns a column as a [`FrameColumn`](DataColumn.md#framecolumn) from a [`DataFrame`](DataFrame.md) or a
[`ColumnGroup`](DataColumn.md#columngroup). The name and index overloads look up only top-level columns; a
[`ColumnPath`](ColumnSelectors.md) or [column selector](ColumnSelectors.md) can find a nested column. Only the selector
overload returns a typed `FrameColumn`; the others return `FrameColumn<*>`. The index is zero-based. The functions throw
if there is no such column, if the selector doesn't select exactly one column, or if the selected column is not a frame
column; the last case causes a `ClassCastException`.

```text
df.getFrameColumn(columnName: String): FrameColumn<*>
df.getFrameColumn(columnPath: ColumnPath): FrameColumn<*>
df.getFrameColumn(index: Int): FrameColumn<*>
df.getFrameColumn(selector: ColumnSelector<T, DataFrame<C>>): FrameColumn<C>
```

**API reference**: [`getFrameColumn`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/get-frame-column.html)

### Parameters {id="parameters_getFrameColumn"}

* `columnName: String` — the name of a top-level column.
* `columnPath: ColumnPath` — the path to a column, including its parent column groups.
* `index: Int` — the zero-based index of a top-level column.
* `selector: ColumnSelector<T, DataFrame<C>>` — a [column selector](ColumnSelectors.md) that selects exactly one frame column.

### Examples {id="examples_getFrameColumn"}

The following dataframe has a `FrameColumn` named `frame`:

<!---FUN getFrameColumnDf-->

```kotlin
val grouped = df.groupBy { city }.toDataFrame("frame")
```

<inline-frame src="./resources/getFrameColumnDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

Get the frame column by name:

<!---FUN getFrameColumnByName-->

```kotlin
val grouped = df.groupBy { city }.toDataFrame("frame")
val frameColumn = grouped.getFrameColumn("frame")
```

<inline-frame src="./resources/getFrameColumnByName.html" width="100%" height="500px"></inline-frame>

<!---END-->

Get the frame column by path:

<!---FUN getFrameColumnByPath-->

```kotlin
val grouped = df.groupBy { city }.toDataFrame("frame")
val frameColumn = grouped.getFrameColumn(pathOf("frame"))
```

<inline-frame src="./resources/getFrameColumnByPath.html" width="100%" height="500px"></inline-frame>

<!---END-->

Get the frame column by its zero-based index:

<!---FUN getFrameColumnByIndex-->

```kotlin
val grouped = df.groupBy { city }.toDataFrame("frame")
val frameColumn = grouped.getFrameColumn(1)
```

<inline-frame src="./resources/getFrameColumnByIndex.html" width="100%" height="500px"></inline-frame>

<!---END-->

Select the frame column with a column selector:

<!---FUN getFrameColumnBySelector-->
<tabs>
<tab title="Properties">

```kotlin
val grouped = df.groupBy { city }.toDataFrame("frame")
val frameColumn = grouped.getFrameColumn { frame }
```

</tab>
<tab title="Strings">

```kotlin
val grouped = df.groupBy { city }.toDataFrame("frame")
grouped.getFrameColumn { frameCol<Any?>("frame") }
```

</tab></tabs>
<inline-frame src="./resources/getFrameColumnBySelector.html" width="100%" height="500px"></inline-frame>

<!---END-->

## getColumns

Returns the selected columns as a list, in the order they are selected, from a [`DataFrame`](DataFrame.md). The `String`
overload accepts top-level column names; the [Columns Selection DSL](ColumnSelectors.md) overload can also select columns
nested inside column groups. A missing name or a selector referring to a missing column throws `IllegalStateException`.

```text
df.getColumns(vararg columns: String): List<DataColumn<*>>
df.getColumns(selector: ColumnsSelector<T, C>): List<DataColumn<C>>
```

**API reference**: [`getColumns`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/get-columns.html)

### Parameters {id="parameters_getColumns"}

* `columns: String` — the names of top-level columns to return, in the order of the arguments.
* `selector: ColumnsSelector<T, C>` — a [Columns Selection DSL](ColumnSelectors.md) selector that chooses columns at any depth.

### Examples {id="examples_getColumns"}

Get columns by their names or with a selector:

<!---FUN getColumnsByNames-->
<tabs>
<tab title="Properties">

```kotlin
val columns = df.getColumns { age and name.firstName }
```

</tab>
<tab title="Strings">

```kotlin
df.getColumns("age", "city")
```

</tab></tabs>
<inline-frame src="./resources/getColumnsByNames.html" width="100%" height="500px"></inline-frame>

<!---END-->

## getColumnIndex

Returns the zero-based index of a top-level column with the given name, or `-1` if there is no such top-level column. A
nested column's name is not looked up, so its index is `-1`.

```text
df.getColumnIndex(name: String): Int
df.getColumnIndex(col: DataColumn<*>): Int
```

**API reference**: [`getColumnIndex(name)`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe/-columns-container/get-column-index.html), [`getColumnIndex(col)`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/get-column-index.html)

### Parameters {id="parameters_getColumnIndex"}

* `name: String` — the name to look up among top-level columns.
* `col: DataColumn<*>` — a column whose name is looked up among top-level columns.

### Examples {id="examples_getColumnIndex"}

Look up the indices of an existing column, a missing name, and a nested column:

<!---FUN getColumnIndex-->

```kotlin
val indices = listOf(
    df.getColumnIndex("city"),
    df.getColumnIndex("address"),
    df.getColumnIndex(df.name.firstName),
)
val output = indices.joinToString()
```

Output:

```text
2, -1, -1
```

<!---END-->

## getColumnPath and getColumnPaths

`getColumnPath` returns the [`ColumnPath`](ColumnSelectors.md) for the single selected column. `getColumnPaths` returns a
list of paths for all selected columns, in the order they are selected. Both functions can find columns at any depth with
the [Columns Selection DSL](ColumnSelectors.md). The selector for `getColumnPath` must select exactly one column; it throws
`IllegalStateException` if it refers to a missing column, `NoSuchElementException` if it resolves to no columns, or
`IllegalArgumentException` if it resolves to more than one column. An unresolved selector passed to `getColumnPaths` throws
`IllegalStateException`.

```text
df.getColumnPath(selector: ColumnSelector<T, C>): ColumnPath
df.getColumnPaths(selector: ColumnsSelector<T, C>): List<ColumnPath>
```

**API reference**: [`getColumnPath`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/get-column-path.html), [`getColumnPaths`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/get-column-paths.html), [`ColumnPath`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.columns/-column-path/index.html)

### Parameters {id="parameters_getColumnPath"}

* `selector: ColumnSelector<T, C>` — a [single-column selector](ColumnSelectors.md) that selects exactly one column.
* `selector: ColumnsSelector<T, C>` — a [Columns Selection DSL](ColumnSelectors.md) selector that selects one or more columns.

### Examples {id="examples_getColumnPath"}

Get the path to one nested column or the paths of all columns in a group:

<!---FUN getColumnPaths_web-->

```kotlin
val singlePath = df.getColumnPath { name.firstName }
val paths = df.getColumnPaths { name.allCols() }
val output = "Single path: $singlePath\nAll paths: $paths"
```

Output:

```text
Single path: [name, firstName]
All paths: [[name, firstName], [name, lastName]]
```

<!---END-->

## getColumnWithPath and getColumnsWithPaths

`getColumnWithPath` returns the single selected column together with its [`ColumnPath`](ColumnSelectors.md) as a
`ColumnWithPath`. `getColumnsWithPaths` returns each selected column paired with its path, in the
order the columns are selected. Both functions can select nested columns with the [Columns Selection DSL](ColumnSelectors.md).
The selector for `getColumnWithPath` must select exactly one column; it throws `IllegalStateException` if it refers to a
missing column, `NoSuchElementException` if it resolves to no columns, or `IllegalArgumentException` if it resolves to more
than one column. An unresolved selector passed to `getColumnsWithPaths` throws `IllegalStateException`.

```text
df.getColumnWithPath(selector: ColumnSelector<T, C>): ColumnWithPath<C>
df.getColumnsWithPaths(selector: ColumnsSelector<T, C>): List<ColumnWithPath<C>>
```

**API reference**: [`getColumnWithPath`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/get-column-with-path.html),
[`getColumnsWithPaths`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/get-columns-with-paths.html),
[`ColumnWithPath`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.columns/-column-with-path/index.html)

### Parameters {id="parameters_getColumnWithPath"}

* `selector: ColumnSelector<T, C>` — a [single-column selector](ColumnSelectors.md) that selects exactly one column.
* `selector: ColumnsSelector<T, C>` — a [Columns Selection DSL](ColumnSelectors.md) selector that selects one or more columns.

### Examples {id="examples_getColumnWithPath"}

Get a column and its path, or several columns paired with their paths:

<!---FUN getColumnsWithPaths_web-->

```kotlin
val columnWithPath = df.getColumnWithPath { name.firstName }
val columnsWithPaths = df.getColumnsWithPaths { age and name.firstName }
val output = "Single column: ${columnWithPath.name()} at ${columnWithPath.path}\n" +
    "Columns: ${columnsWithPaths.map { it.name() to it.path }}"
```

Output:

```text
Single column: firstName at [name, firstName]
Columns: [(age, [age]), (firstName, [name, firstName])]
```

<!---END-->
