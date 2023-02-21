[//]: # (title: reorder)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) with a new order of selected columns.

```text
reorder { columns }
  [.cast<ColumnType>() ]
   .by { columnExpression } | .byDesc { columnExpression } | .byName(desc = false) { columnExpression } 
    
columnExpression: DataColumn.(DataColumn) -> Value
```

<!---FUN reorder-->
<tabs>
<tab title="Properties">

```kotlin
df.reorder { age..isHappy }.byName()
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val isHappy by column<Boolean>()

df.reorder { age..isHappy }.byName()
```

</tab>
<tab title="Strings">

```kotlin
    df.reorder { "age".."isHappy" }.byName()
}
```

</tab></tabs>
<!---END-->

When a subset of columns is selected they will be reordered among their original positions. Positions of other columns will not change. 

If selected columns belong to different column groups they will be reordered within their groups, so column grouping will be preserved.

<!---FUN reorderSome-->

```kotlin
val df = dataFrameOf("c", "d", "a", "b")(
    3, 4, 1, 2,
    1, 1, 1, 1
)
df.reorder("d", "b").cast<Int>().by { sum() } // [c, b, a, d]
```

<!---END-->

When exactly one [`ColumnGroup`](DataColumn.md#columngroup) is selected, reordering is applied to its nested columns.

<!---FUN reorderInGroup-->

```kotlin
df.reorder { name }.byName(desc = true) // [name.lastName, name.firstName]
```

<!---END-->

## reorderColumnsBy

Reorders all columns

```text
reorderColumnsBy(dfs = true, desc = false) { columnExpression }
```

**Parameters:**
* `dfs` — reorder columns inside [`ColumnGroups`](DataColumn.md#columngroup) and [`FrameColumn`](DataColumn.md#framecolumn) recursively
* `desc` — apply descending order

## reorderColumnsByName

```text
reorderColumnsByName(dfs = true, desc = false)
```

**Parameters:**
* `dfs` — reorder columns inside [`ColumnGroups`](DataColumn.md#columngroup) and [`FrameColumn`](DataColumn.md#framecolumn) recursively
* `desc` — apply descending order
