[//]: # (title: explode)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Splits list-like values in given columns and spreads them vertically. Values in other columns are duplicated.

```text
explode(dropEmpty = true) [ { columns } ]
```

**Parameters:**
* `dropEmpty` — if `true`, removes rows with empty lists or dataframes. Otherwise, they will be exploded into `null`.

**Available for:**
* [`DataFrame`](DataFrame.md)
* [`FrameColumn`](DataColumn.md#framecolumn)
* `DataColumn<Collection>`

**Reverse operation:** [`implode`](implode.md)

Exploded columns will change their types:
* `List<T>` to `T`
* [`DataFrame`](DataFrame.md) to [`DataRow`](DataRow.md)

Exploded [`FrameColumn`](DataColumn.md#framecolumn) will be converted into [`ColumnGroup`](DataColumn.md#columngroup).

Explode [`DataFrame`](DataFrame.md):

<!---FUN explode-->
<tabs>
<tab title="Accessors">

```kotlin
val a by columnOf(1, 2)
val b by columnOf(listOf(1, 2), listOf(3, 4))

val df = dataFrameOf(a, b)

df.explode { b }
```

</tab>
<tab title="Strings">

```kotlin
val df = dataFrameOf("a", "b")(
    1, listOf(1, 2),
    2, listOf(3, 4)
)

df.explode("b")
```

</tab></tabs>
<!---END-->

When several columns are exploded in one operation, lists in different columns will be aligned.

<!---FUN explodeSeveral-->

```kotlin
val a by columnOf(listOf(1, 2), listOf(3, 4, 5))
val b by columnOf(listOf(1, 2, 3), listOf(4, 5))

val df = dataFrameOf(a, b)
df.explode { a and b }
```

<!---END-->

Explode `DataColumn<Collection>`:

<!---FUN explodeColumnList-->

```kotlin
val col by columnOf(listOf(1, 2), listOf(3, 4))

col.explode()
```

<!---END-->

Explode [`FrameColumn`](DataColumn.md#framecolumn):

<!---FUN explodeColumnFrames-->

```kotlin
val col by columnOf(
    dataFrameOf("a", "b")(1, 2, 3, 4),
    dataFrameOf("a", "b")(5, 6, 7, 8)
)

col.explode()
```

<!---END-->
