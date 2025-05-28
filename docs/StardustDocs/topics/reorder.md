[//]: # (title: reorder)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) with a new order of selected columns.

```text
reorder { columns }
  [.cast<ColumnType>() ]
   .by { columnExpression } | .byDesc { columnExpression } | .byName(desc = false)
    
columnExpression: DataColumn.(DataColumn) -> Value
```

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

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
}
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.reorder.html" width="100%"/>
<!---END-->

When a subset of columns is selected they will be reordered among their original positions. Positions of other columns will not change. 

If selected columns belong to different column groups they will be reordered within their groups, so column grouping will be preserved.

<!---FUN reorderSome-->

```kotlin
val df = dataFrameOf("c", "d", "a", "b")(
    3, 4, 1, 2,
    1, 1, 1, 1,
)
df.reorder("d", "b").cast<Int>().by { sum() } // [c, b, a, d]
```

<!---END-->

When exactly one [`ColumnGroup`](DataColumn.md#columngroup) is selected, reordering is applied to its nested columns.

<!---FUN reorderInGroup-->

```kotlin
df.reorder { name }.byName(desc = true) // [name.lastName, name.firstName]
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.reorderInGroup.html" width="100%"/>
<!---END-->

## reorderColumnsBy

Reorders all columns

```text
reorderColumnsBy(atAnyDepth = true, desc = false) { columnExpression }
```

**Parameters:**
* `atAnyDepth` — reorder columns inside [`ColumnGroups`](DataColumn.md#columngroup) and [`FrameColumn`](DataColumn.md#framecolumn) recursively
* `desc` — apply descending order

## reorderColumnsByName

```text
reorderColumnsByName(atAnyDepth = true, desc = false)
```

**Parameters:**
* `atAnyDepth` — reorder columns inside [`ColumnGroups`](DataColumn.md#columngroup) and [`FrameColumn`](DataColumn.md#framecolumn) recursively
* `desc` — apply descending order
