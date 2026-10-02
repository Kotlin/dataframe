[//]: # (title: appendNulls)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Append-->

Returns a new [`DataFrame`](DataFrame.md) containing the existing rows followed by the requested number of rows
filled with `null` values.

```kotlin
fun <T> DataFrame<T>.appendNulls(numberOfRows: Int = 1): DataFrame<T>
```

**Parameters:**

`numberOfRows` defaults to `1` and must not be negative.
When `numberOfRows` is `0`, it returns the original [`DataFrame`](DataFrame.md) instance.
Passing a negative `numberOfRows` results in an `IllegalArgumentException`.

`appendNulls` does not modify the original [`DataFrame`](DataFrame.md).

<warning>

Appending `null` widens the runtime type of each affected value column to nullable, while the compile-time schema
of the returned [`DataFrame`](DataFrame.md) is not refined. Use [`cast`](cast.md) or [`convertTo`](convertTo.md)
with a nullable schema before accessing these values through typed accessors.

</warning>

<warning>

Adding rows creates a new dataframe and rebuilds its columns using the existing and appended values.
Repeatedly appending rows one at a time in a loop is a performance antipattern.
Prefer building a dataframe at once or appending/concatenating rows in batches.

</warning>

**Related operations**
* [`append`](append.md) — appends rows containing specified values;
* [`concat`](concat.md) — vertically combines dataframes or rows;
* [`fillNulls`](fill.md#fillnulls) — replaces `null` values in existing rows;
* [`duplicate`](duplicate.md) — repeats existing rows.

## Basic usage

The examples in this section use the following dataframe:

<!---FUN appendDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/appendDf.html" width="100%" height="500px"></inline-frame>

Omit `numberOfRows` to append one row containing a null value in every column:

<!---FUN appendOneNullRow-->

```kotlin
df.appendNulls()
```

<!---END-->
<inline-frame src="./resources/appendOneNullRow.html" width="100%" height="500px"></inline-frame>

Set `numberOfRows` to append several null-filled rows at once:

<!---FUN appendSeveralNullRows-->

```kotlin
df.appendNulls(numberOfRows = 3)
```

<!---END-->
<inline-frame src="./resources/appendSeveralNullRows.html" width="100%" height="500px"></inline-frame>

Passing `0` returns the original dataframe instance:

<!---FUN appendZeroNullRows-->

```kotlin
df.appendNulls(numberOfRows = 0)
```

<!---END-->
<inline-frame src="./resources/appendZeroNullRows.html" width="100%" height="500px"></inline-frame>

## `appendNulls` with a [`ColumnGroup`](DataColumn.md#columngroup)

For a [`ColumnGroup`](DataColumn.md#columngroup), the appended `null` is passed to its nested columns.

Consider the following example:

<!---FUN appendColumnGroupDf-->

```kotlin
columnGroupDf
```

<!---END-->
<inline-frame src="./resources/appendColumnGroupDf.html" width="100%" height="500px"></inline-frame>

`appendNulls()` adds a row in which nested and top-level values are `null`:

<!---FUN appendNullsColumnGroup-->

```kotlin
columnGroupDf.appendNulls()
```

<!---END-->
<inline-frame src="./resources/appendNullsColumnGroup.html" width="100%" height="500px"></inline-frame>

## `appendNulls` with a [`FrameColumn`](DataColumn.md#framecolumn)

For a [`FrameColumn`](DataColumn.md#framecolumn), an appended `null` is represented
by an empty [`DataFrame`](DataFrame.md) created using the schema available
from that [`FrameColumn`](DataColumn.md#framecolumn) at the time of the call.

Consider the following example:

<!---FUN appendFrameColumnDf-->

```kotlin
frameColumnDf
```

<!---END-->
<inline-frame src="./resources/appendFrameColumnDf.html" width="100%" height="500px"></inline-frame>

The new [`FrameColumn`](DataColumn.md#framecolumn) cell contains an empty [`DataFrame`](DataFrame.md) with the schema
taken from the [`FrameColumn`](DataColumn.md#framecolumn):

<!---FUN appendNullsFrameColumn-->

```kotlin
frameColumnDf.appendNulls()
```

<!---END-->
<inline-frame src="./resources/appendNullsFrameColumn.html" width="100%" height="500px"></inline-frame>
