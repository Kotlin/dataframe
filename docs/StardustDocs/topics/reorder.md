[//]: # (title: reorder)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ReorderSamples-->

Changes the order of the selected columns.
`reorder` selects the columns; finish it with `by`, `byDesc` or `byName` to get a new [`DataFrame`](DataFrame.md).

```text
reorder { columns }
  [.cast<ColumnType>() ]
   .by { columnExpression } | .byDesc { columnExpression } | .byName(desc = false)
    
columnExpression: DataColumn.(DataColumn) -> Value
```

**Related operations**: [](moveRename.md)

Don't confuse `reorder` with [`sortBy`](sortBy.md), which changes the order of rows, not columns.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

The examples on this page use the following dataframe:

<!---FUN reorderDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/reorderDf.html" width="100%"/>

<!---FUN reorder-->
<tabs>
<tab title="Properties">

```kotlin
df.reorder { age..isHappy }.byName()
```

</tab>
<tab title="Strings">

```kotlin
df.reorder { "age".."isHappy" }.byName()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/reorder_properties.html" width="100%"/>

The selected columns change places only with each other: they take the positions of the selected columns,
in the new order. Positions of other columns do not change. The values in the columns do not change either.

`columnExpression` is computed for every selected column. The column is passed to it both as the receiver (`this`)
and as the argument (`it`). `by` puts the columns in ascending order of the computed value:

<!---FUN reorderSome-->

```kotlin
val df = dataFrameOf("c", "d", "a", "b")(
    3, 4, 1, 2,
    1, 1, 1, 1,
)
df.reorder("d", "b").cast<Int>().by { sum() } // [c, b, a, d]
```

<!---END-->
<inline-frame src="./resources/reorderSome.html" width="100%"/>

`byDesc` puts them in descending order:

<!---FUN reorderByDesc-->

```kotlin
val df = dataFrameOf("c", "d", "a", "b")(
    3, 4, 1, 2,
    1, 1, 1, 1,
)
df.reorder("a", "b").cast<Int>().byDesc { sum() } // [c, d, b, a]
```

<!---END-->
<inline-frame src="./resources/reorderByDesc.html" width="100%"/>

Columns with equal values keep their original order. Here `name` and `city` have names of the same length,
so `name` stays before `city`:

<!---FUN reorderEqualKeys-->
<tabs>
<tab title="Properties">

```kotlin
df.reorder { age and name and city }.byDesc { it.name().length }
```

</tab>
<tab title="Strings">

```kotlin
df.reorder("age", "name", "city").byDesc { it.name().length }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/reorderEqualKeys_properties.html" width="100%"/>

If selected columns belong to different column groups, they are reordered within their groups,
so no column moves to another group. Here `age` and `city` change places at the top level,
and `firstName` and `lastName` change places inside `name`:

<!---FUN reorderInDifferentGroups-->
<tabs>
<tab title="Properties">

```kotlin
df.reorder { age and city and name.firstName and name.lastName }.byName(desc = true)
```

</tab>
<tab title="Strings">

```kotlin
df.reorder { "age" and "city" and "name"["firstName"] and "name"["lastName"] }.byName(desc = true)
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/reorderInDifferentGroups_properties.html" width="100%"/>

When exactly one [`ColumnGroup`](DataColumn.md#columngroup) is selected, reordering is applied to its nested columns.
A column group selected together with other columns moves as a whole, and its nested columns keep their order.

<!---FUN reorderInGroup-->
<tabs>
<tab title="Properties">

```kotlin
df.reorder { name }.byName(desc = true)
```

</tab>
<tab title="Strings">

```kotlin
df.reorder("name").byName(desc = true)
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/reorderInGroup_properties.html" width="100%"/>

## reorderColumnsBy

Reorders all columns of the dataframe by the value of `columnExpression`.

```text
reorderColumnsBy(atAnyDepth = true, desc = false) { columnExpression }
```

**Parameters:**
* `atAnyDepth` — if `true`, also reorder the columns inside every [`ColumnGroup`](DataColumn.md#columngroup)
  and inside every dataframe of a [`FrameColumn`](DataColumn.md#framecolumn), each within its own group or dataframe.
  If `false`, only the top-level columns are reordered.
* `desc` — apply descending order

As in `reorder`, columns with equal values keep their original order.
The examples use the dataframe from the top of the page.
With the default `atAnyDepth = true`, the columns inside `name` are reordered too:

<!---FUN reorderColumnsBy-->

```kotlin
df.reorderColumnsBy { name().length }
```

<!---END-->
<inline-frame src="./resources/reorderColumnsBy.html" width="100%"/>

With `atAnyDepth = false`, the columns inside `name` keep their order:

<!---FUN reorderColumnsByTopLevel-->

```kotlin
df.reorderColumnsBy(atAnyDepth = false) { name().length }
```

<!---END-->
<inline-frame src="./resources/reorderColumnsByTopLevel.html" width="100%"/>

## reorderColumnsByName

Reorders all columns of the dataframe by their names. It is a shortcut for `reorderColumnsBy { name() }`.

```text
reorderColumnsByName(atAnyDepth = true, desc = false)
```

**Parameters:**
* `atAnyDepth` — if `true`, also reorder the columns inside every [`ColumnGroup`](DataColumn.md#columngroup)
  and inside every dataframe of a [`FrameColumn`](DataColumn.md#framecolumn), each within its own group or dataframe.
  If `false`, only the top-level columns are reordered.
* `desc` — apply descending order

The examples use the dataframe from the top of the page.
With the default `atAnyDepth = true`, the columns inside `name` are reordered too:

<!---FUN reorderColumnsByName-->

```kotlin
df.reorderColumnsByName(desc = true)
```

<!---END-->
<inline-frame src="./resources/reorderColumnsByName.html" width="100%"/>

With `atAnyDepth = false`, the columns inside `name` keep their order:

<!---FUN reorderColumnsByNameTopLevel-->

```kotlin
df.reorderColumnsByName(atAnyDepth = false, desc = true)
```

<!---END-->
<inline-frame src="./resources/reorderColumnsByNameTopLevel.html" width="100%"/>
