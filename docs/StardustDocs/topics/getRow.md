[//]: # (title: Get rows)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.GetRowSamples-->

<web-summary>
Discover the `get`, `getOrNull`, and `getRows` operations for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover the `get`, `getOrNull`, and `getRows` operations for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover the `get`, `getOrNull`, and `getRows` operations for Kotlin DataFrame.
</link-summary>

## get

Returns the row of a [`DataFrame`](DataFrame.md) at a zero-based [index](indexing.md) as a [`DataRow`](DataRow.md).
`df[index]` is the operator form of `df.get(index)`. The index must be inside the dataframe:
an out-of-bounds index throws `IndexOutOfBoundsException`.

The `get` operator also accepts several indices or a range of indices — it then returns a new `DataFrame` with
those rows; see [`getRows`](#getrows).

```kotlin
df.get(index: Int): DataRow
df[index]: DataRow
```

**API reference**: [`get`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe/-data-frame/get.html)

See also:

* [`filter`](filter.md), which returns all rows that satisfy a [row condition](DataRow.md#row-conditions).
* [`rows`](rows.md), which iterates over all rows of the dataframe.

### Parameters {id="parameters_get"}

* `index: Int` — the zero-based index of the row.

### Examples {id="examples_get"}

The following dataframe will be used in the examples below:

<!---FUN getRowDf-->

```kotlin
val input = df
```

<inline-frame src="./resources/getRowDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

Get the row at index 2:

<!---FUN getRowByIndex-->

```kotlin
val row = df[2] // the row at index 2
```

<inline-frame src="./resources/getRowByIndex.html" width="100%" height="500px"></inline-frame>

<!---END-->

## getOrNull

Returns the row of a [`DataFrame`](DataFrame.md) at a zero-based index as a [`DataRow`](DataRow.md), or `null` if
the index is out of bounds. It is the safe counterpart of `get`, which throws instead. A negative index also
returns `null`.

For the first or the last row of a dataframe, or `null` if it is empty, see
[`firstOrNull`](first.md#firstornull) and [`lastOrNull`](last.md#lastornull).

```kotlin
df.getOrNull(index: Int): DataRow?
```

**API reference**: [`getOrNull`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/get-or-null.html)

### Parameters {id="parameters_getOrNull"}

* `index: Int` — the zero-based index of the row.

### Examples {id="examples_getOrNull"}

Get a row that exists, or `null` when the index is out of bounds:

<!---FUN getOrNull-->

```kotlin
df.getOrNull(10) // null: df has only 10 rows
df.getOrNull(-1) // null
df.getOrNull(1) // the row with index 1
```

<inline-frame src="./resources/getOrNull.html" width="100%" height="500px"></inline-frame>

<!---END-->

## getRows

Returns a new [`DataFrame`](DataFrame.md) with the rows at the given zero-based [indices](indexing.md).
The `range` overload takes a contiguous range of row indices; the `indices` overload takes an `Iterable` of row
indices and returns the rows in the order of the indices — the same index can occur several times, and then the row
is repeated in the result. An empty range or an empty iterable returns an empty dataframe.

Both overloads throw `IndexOutOfBoundsException` if any of the indices is out of bounds.
If the range is exactly the range of all row indices, the dataframe itself is returned.

`getRows` is the same as the `get` operator with a range or an iterable of indices.

```kotlin
df.getRows(range: IntRange): DataFrame
df.getRows(indices: Iterable<Int>): DataFrame
```

**API reference**: [`getRows`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/get-rows.html)

### Parameters {id="parameters_getRows"}

* `range: IntRange` — the range of row indices to return.
* `indices: Iterable<Int>` — the row indices to return; the rows appear in the result in this order,
  and the same index can occur several times.

### Examples {id="examples_getRows"}

Get the first three rows with a range of indices:

<!---FUN getRowsByRange-->

```kotlin
df.getRows(0..2) // a DataFrame with the first three rows
```

<inline-frame src="./resources/getRowsByRange.html" width="100%" height="500px"></inline-frame>

<!---END-->

Get the rows 4 and 1, in this order, with an iterable of indices:

<!---FUN getRowsByIndices-->

```kotlin
df.getRows(listOf(4, 1)) // a DataFrame with the rows 4 and 1, in this order
```

<inline-frame src="./resources/getRowsByIndices.html" width="100%" height="500px"></inline-frame>

<!---END-->

## Get a row by condition

Instead of an index, a [row condition](DataRow.md#row-conditions) can select the row:

* [`single`](single.md) returns the only row that satisfies the condition.
* [`first`](first.md) and [`last`](last.md) return the first or the last row that satisfies the condition.
* [`minBy`](minBy.md) and [`maxBy`](maxBy.md) return the row with the smallest or the largest value computed
  by a row expression.

The `*OrNull` versions of these operations — `singleOrNull`, `firstOrNull`, `lastOrNull`, `minByOrNull`,
`maxByOrNull` — return `null` instead of throwing when no row matches; `singleOrNull` also returns `null`
if there are several matching rows.

The row condition receives each row both as `this` and as `it`, so the values of the row can be accessed directly
using [Extension Properties](extensionPropertiesApi.md) or
[`String` column accessors](StringApi.md#string-column-accessors),
including the values of nested columns of a [`ColumnGroup`](DataColumn.md#columngroup).
The row expression of `minBy` and `maxBy` receives the row the same way and returns the value to compare.

### Examples {id="examples_getRowByCondition"}

Get a row by a condition on its values or by an extreme value of a column:

<!---FUN getRowByCondition-->
<tabs>
<tab title="Properties">

```kotlin
df.first { weight != null } // the first person with a known weight
df.minBy { age } // the youngest person
df.maxBy { age } // the oldest person
df.maxByOrNull { weight } // the heaviest person
df.single { age == 45 } // the only person aged 45
```

</tab>
<tab title="Strings">

```kotlin
df.first { it["weight"] != null } // the first person with a known weight
df.minBy("age") // the youngest person
df.maxBy("age") // the oldest person
df.maxByOrNull("weight") // the heaviest person
df.single { it["age"] == 45 } // the only person aged 45
```

</tab></tabs>
<inline-frame src="./resources/getRowByCondition.html" width="100%" height="500px"></inline-frame>

<!---END-->
