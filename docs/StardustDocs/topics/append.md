[//]: # (title: append)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Append-->

Returns a new [`DataFrame`](DataFrame.md) containing the existing rows followed by rows constructed from the
supplied values.

```kotlin
fun <T> DataFrame<T>.append(vararg values: Any?): DataFrame<T>
```

**Parameters**:

`values` — a flat sequence of values to construct new rows from.
Each consecutive group containing one value for every top-level column forms one new
row, and the values within each group must follow the dataframe's column order. The number of values
must be a multiple of the number of columns.

Every appended value must be accepted by the corresponding column:

* a [`ValueColumn`](DataColumn.md#valuecolumn) accepts `null` or a value compatible with its type;
* a [`ColumnGroup`](DataColumn.md#columngroup) accepts `null`; a [`DataRow`](DataRow.md), whose values are matched
to the nested columns by name (a nested column whose name is absent from the row receives `null`); or a `List`
whose values follow the nested column order;
* a [`FrameColumn`](DataColumn.md#framecolumn) accepts `null` or a [`DataFrame`](DataFrame.md).

`append` does not modify the original [`DataFrame`](DataFrame.md).
When `values` is empty, it returns the original dataframe instance.

Passing values to a [`DataFrame`](DataFrame.md) without columns, appending an incomplete row,
or passing a value incompatible with its column results in an `IllegalArgumentException`.

<warning>

Adding rows creates a new dataframe and rebuilds its columns using the existing and appended values.
Repeatedly appending rows one at a time in a loop is a performance antipattern.
Prefer building a dataframe at once or appending/concatenating rows in batches.

</warning>

**Related operations**
* [`appendNulls`](appendNulls.md) — appends rows filled with `null` values;
* [`concat`](concat.md) — vertically combines dataframes or rows;
* [`duplicate`](duplicate.md) — repeats existing rows;
* [`add`](add.md) — adds columns rather than rows.

## Basic usage

The examples in this section use the following dataframe:

<!---FUN appendDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/appendDf.html" width="100%" height="500px"></inline-frame>

Pass one value for each column to append a single row:

<!---FUN appendOneRow-->

```kotlin
df.append("Bob", 30)
```

<!---END-->
<inline-frame src="./resources/appendOneRow.html" width="100%" height="500px"></inline-frame>

Pass several complete groups of values to append several rows in one call:

<!---FUN appendSeveralRows-->

```kotlin
df.append(
    "Bob", // name in the first new row
    30, // age in the first new row
    "Charlie", // name in the second new row
    25, // age in the second new row
)
```

<!---END-->
<inline-frame src="./resources/appendSeveralRows.html" width="100%" height="500px"></inline-frame>

`null` can be supplied for an individual value. Only that cell in the new row is null:

<!---FUN appendNullValue-->

```kotlin
df.append("Bob", null)
```

<!---END-->
<inline-frame src="./resources/appendNullValue.html" width="100%" height="500px"></inline-frame>

Calling `append()` without values returns the original [`DataFrame`](DataFrame.md) instance:

<!---FUN appendNoValues-->

```kotlin
df.append()
```

<!---END-->
<inline-frame src="./resources/appendNoValues.html" width="100%" height="500px"></inline-frame>

## Append objects as rows

```kotlin
inline fun <reified T : DataRowSchema> DataFrame<T>.append(vararg rows: T): DataFrame<T>
```

This overload appends one row for each object in `rows`. The [`DataFrame`](DataFrame.md) schema type `T` must
implement `DataRowSchema`, and every object in `rows` must be an instance of `T`.

### With the compiler plugin

When the [compiler plugin](Compiler-Plugin.md) is enabled, it makes a data class annotated with `@DataSchema`
implement `DataRowSchema`:

```kotlin
@DataSchema
data class Person(val name: String, val age: Int)
```

When the annotated class is used as the [`DataFrame`](DataFrame.md) schema type,
its instances can be passed directly to the type-safe `append` overload:

<!---FUN appendDataSchema-->

```kotlin
df.append(Person("Bob", 30))
```

<!---END-->
<inline-frame src="./resources/appendDataSchema.html" width="100%" height="500px"></inline-frame>

### Without the compiler plugin

The overload itself does not require the compiler plugin. A class can implement `DataRowSchema` explicitly:

```kotlin
data class Person(
    val name: String,
    val age: Int,
) : DataRowSchema
```

An interface can also extend `DataRowSchema` and serve as the schema. In that case, instances of a class
implementing that interface can be appended.

## `append` with a [`ColumnGroup`](DataColumn.md#columngroup)

The following dataframe is used in the examples in this section:

<!---FUN columnGroupDf-->

```kotlin
columnGroupDf
```

<!---END-->
<inline-frame src="./resources/columnGroupDf.html" width="100%" height="500px"></inline-frame>

### List

Values can be supplied to a [`ColumnGroup`](DataColumn.md#columngroup) as a list:

<!---FUN appendColumnGroupList-->

```kotlin
columnGroupDf.append(listOf("Bob", "Dylan"), 30)
```

<!---END-->
<inline-frame src="./resources/appendColumnGroupList.html" width="100%" height="500px"></inline-frame>

The order of the values in the list must match the order of the columns
in the [`ColumnGroup`](DataColumn.md#columngroup).

### [`DataRow`](DataRow.md)

A [`DataRow`](DataRow.md) can be used to supply values to a [`ColumnGroup`](DataColumn.md#columngroup).
Its values are matched to the nested columns by name, not by position, and must be compatible with the
corresponding nested columns:

<!---FUN appendColumnGroupRow-->

```kotlin
val bobRow = dataFrameOf("lastName", "firstName")("Dylan", "Bob")[0]
columnGroupDf.append(bobRow, 30)
```

<!---END-->
<inline-frame src="./resources/appendColumnGroupRow.html" width="100%" height="500px"></inline-frame>

If a nested column's name is absent from the row, `null` is appended to that column:

<!---FUN appendColumnGroupRowWithMissingColumn-->

```kotlin
val bobRow = dataFrameOf("firstName")("Bob")[0]
columnGroupDf.append(bobRow, 30)
```

<!---END-->
<inline-frame src="./resources/appendColumnGroupRowWithMissingColumn.html" width="100%" height="500px"></inline-frame>

### `null` with [`ColumnGroup`](DataColumn.md#columngroup)

When `null` is passed for a [`ColumnGroup`](DataColumn.md#columngroup),
all the nested values become null in the new row.
Values for the other top-level columns are supplied as usual:

<!---FUN appendNullToColumnGroup-->

```kotlin
columnGroupDf.append(null, 30)
```

<!---END-->
<inline-frame src="./resources/appendNullToColumnGroup.html" width="100%" height="500px"></inline-frame>

## `append` with a [`FrameColumn`](DataColumn.md#framecolumn)

The next dataframe contains a [`FrameColumn`](DataColumn.md#framecolumn):

<!---FUN frameColumnDf-->

```kotlin
frameColumnDf
```

<!---END-->
<inline-frame src="./resources/frameColumnDf.html" width="100%" height="500px"></inline-frame>

### [`DataFrame`](DataFrame.md)

Pass a dataframe to place it in the new cell of the [`FrameColumn`](DataColumn.md#framecolumn):

<!---FUN appendFrameColumn-->

```kotlin
val bobDf = dataFrameOf(
    "name" to columnOf("Bob"),
    "age" to columnOf(30),
)
frameColumnDf.append(bobDf)
```

<!---END-->
<inline-frame src="./resources/appendFrameColumn.html" width="100%" height="500px"></inline-frame>

### `null` with [`FrameColumn`](DataColumn.md#framecolumn)

[`FrameColumn`](DataColumn.md#framecolumn) cannot contain `null` values. If a `null` is appended to it, this `null`
is represented in the [`FrameColumn`](DataColumn.md#framecolumn) by an empty [`DataFrame`](DataFrame.md).
This empty [`DataFrame`](DataFrame.md) is created using the schema available
from that [`FrameColumn`](DataColumn.md#framecolumn) at the time of the call:

<!---FUN appendNullToFrameColumn-->

```kotlin
frameColumnDf.append(null)
```

<!---END-->
<inline-frame src="./resources/appendNullToFrameColumn.html" width="100%" height="500px"></inline-frame>
