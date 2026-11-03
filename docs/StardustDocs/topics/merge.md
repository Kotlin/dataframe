[//]: # (title: merge)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.MergeSamples-->

<web-summary>
Discover the `merge` operation for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover the `merge` operation for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover the `merge` operation for Kotlin DataFrame.
</link-summary>

Merges the values of several columns of a [`DataFrame`](DataFrame.md) into a single value per row
and stores the merged values in one column. The values of the selected columns are collected per row,
in the order the columns were selected, and merged into a single value: by default,
a [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/) of the collected values.

The selected columns are removed and replaced by a single column with the merged values.
The merged column takes the position of the first selected column; all other columns stay unchanged.

`merge` itself does not merge anything: it returns an intermediate object that must be completed
by one of the final steps — `into` or `intoList`.

Returns a new [`DataFrame`](DataFrame.md) with the merged column (`into`), or the merged values as a
[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/) without changing
the [`DataFrame`](DataFrame.md) (`intoList`).

**API reference**: [`merge`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/merge.html)

```text
df.merge(columns: String... | selector: ColumnsSelector)
    [.notNull()]
    [.by(separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "",
         limit: Int = -1, truncated: CharSequence = "...")
     | .by { transform: DataRow.(List) -> Any }
     | .asStrings()]
    .into(columnName: String | path: ColumnPath) | .intoList()

transform: DataRow.(List) -> Any
```

Notes:

* By default, when no `by` or `asStrings` is specified, the values are merged into a
  [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/).
* `notNull()` excludes `null` values from the merged values. A row value collected from a
  [`ColumnGroup`](DataColumn.md#columngroup) is excluded as well when all of its values are `null`.
  The filter applies to both `into` and `intoList`.
* `by(separator, ...)` joins the collected values into a `String`. A `null` value is rendered as `null`.
* The `transform` lambda is a [row expression](DataRow.md#row-expressions): it receives the source row as `this`
  and the collected values as `it` — a `List` in the order the columns were selected. When the selected columns
  have different value types, the values have their common supertype, so the `transform` may need to cast them.
* `into(columnName)` or `into(path)` stores the merged values in a new column and returns the updated
  [`DataFrame`](DataFrame.md). If a column already exists at the target, it is removed — including all its nested
  columns — and replaced by the merged column. If the parent [column groups](DataColumn.md#columngroup) of `path`
  do not exist, they are created.
* `intoList()` returns the merged values as a [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/),
  one merged value per row, without changing the [`DataFrame`](DataFrame.md): the selected columns stay in place.

`merge` is the reverse of [`split`](split.md).

See also:

* [`split`](split.md), the reverse operation: splits one column into several.
* [Split and Merge](splitMerge.md), an overview of the two operations.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

### Parameters

* `columns: String...` — the names of the top-level columns to merge.
* `selector: ColumnsSelector` — the columns to merge, selected with the
  [Columns Selection DSL](ColumnSelectors.md).
* `separator: CharSequence = ", "` — the string used to separate the joined values.
* `prefix: CharSequence = ""` — the string prepended to the joined string.
* `postfix: CharSequence = ""` — the string appended to the joined string.
* `limit: Int = -1` — the maximum number of values to include in the joined string;
  all values are included by default.
* `truncated: CharSequence = "..."` — the string appended to the joined string if the values were truncated.
* `transform: DataRow.(List) -> Any` — a [row expression](DataRow.md#row-expressions) that computes
  the merged value from the collected values.
* `infer: Infer = Infer.Nulls` — the type inference of the merged column
  (the first parameter of the `by { transform }` overload).
* `columnName: String` — the name of the column to store the merged values in.
* `path: ColumnPath` — the path of the column to store the merged values in;
  the missing parent [column groups](DataColumn.md#columngroup) of the path are created.

### Examples

The following dataframe will be used in the examples below:

<!---FUN mergeDf-->

```kotlin
peopleDf
```

<inline-frame src="./resources/mergeDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

Merge the first and the last name into a single `fullName` column:

<!---FUN merge-->
<tabs>
<tab title="Properties">

```kotlin
// Merge two columns into one column "fullName"
peopleDf.merge { name.firstName and name.lastName }.by(" ").into("fullName")
```

</tab>
<tab title="Strings">

```kotlin
// Merge two columns into one column "fullName"
peopleDf.merge { colGroup("name").col("firstName") and colGroup("name").col("lastName") }
    .by(" ").into("fullName")
```

</tab></tabs>
<inline-frame src="./resources/merge.html" width="100%" height="500px"></inline-frame>

<!---END-->

Top-level columns can also be merged by their plain names:

<!---FUN mergeColumnNames-->

```kotlin
// Merge top-level columns by their names into one column "data" of lists
peopleDf.merge("age", "weight").into("data")
```

<inline-frame src="./resources/mergeColumnNames.html" width="100%" height="500px"></inline-frame>

<!---END-->

When no `by` or `asStrings` is specified, the values are merged into a `List`.
Note that `colsOf<Number>()` selects only the non-nullable numeric columns here:
`weight` has the type `Int?` and is not selected.

<!---FUN mergeDefault-->

```kotlin
// By default, when no `by` or `asStrings` is specified, values will be merged into the List
peopleDf.merge { colsOf<Number>() }.into("data")
```

<inline-frame src="./resources/mergeDefault.html" width="100%" height="500px"></inline-frame>

<!---END-->

The `by { transform }` lambda receives the collected values as `it` and the source row as `this`:

<!---FUN mergeSameWith-->

```kotlin
// "transform" accepts a List of collected values for every row typed by their common type
peopleDf.merge { name.firstName and name.lastName }
    .by { it.joinToString(" ") }
    .into("fullName")
```

<inline-frame src="./resources/mergeSameWith.html" width="100%" height="500px"></inline-frame>

<!---END-->

When heterogeneous columns are merged, the values have their common supertype
and the `transform` may need to cast them:

<!---FUN mergeDifferentWith-->

```kotlin
// When heterogeneous columns are merged, they may need to be cast to valid types in "transform"
peopleDf.merge { name.firstName and age and isHappy }
    .by { "${it[0]} aged ${it[1]} is " + (if (it[2] as Boolean) "" else "not ") + "happy" }
    .into("status")
```

<inline-frame src="./resources/mergeDifferentWith.html" width="100%" height="500px"></inline-frame>

<!---END-->

`asStrings()` joins the values with the default separator `", "` — the same as `by(", ")`:

<!---FUN mergeAsStrings-->

```kotlin
// Merge values into a single String
peopleDf.merge { name.firstName and name.lastName }.asStrings().into("fullName")
```

<inline-frame src="./resources/mergeAsStrings.html" width="100%" height="500px"></inline-frame>

<!---END-->

`notNull()` excludes `null` values from the merged values — here the `weight` of some people is `null`:

<!---FUN mergeNotNull-->

```kotlin
// Merge two columns into one column "info" excluding null values
peopleDf.merge { name.firstName and weight }.notNull().by(" ").into("info")
```

<inline-frame src="./resources/mergeNotNull.html" width="100%" height="500px"></inline-frame>

<!---END-->

`intoList()` returns the merged values without changing the `DataFrame`:

<!---FUN mergeIntoList-->

```kotlin
// Merge data from two columns into List<String>
peopleDf.merge { name.firstName and name.lastName }.by(",").intoList()
```

Output:

```text
Alice,Cooper
Bob,Dylan
Charlie,Daniels
Charlie,Chaplin
Bob,Marley
Alice,Wolf
Charlie,Byrd
Alice,Smith
Bob,Brown
Charlie,Johnson
```

<!---END-->

`into(path)` stores the merged values in a nested column; the missing parent column groups
of the path are created:

<!---FUN mergeIntoPath-->

```kotlin
// Merge the two name columns into a new nested column; the "person" column group is created
peopleDf.merge { name.firstName and name.lastName }.by(" ").into(pathOf("person", "fullName"))
```

<inline-frame src="./resources/mergeIntoPath.html" width="100%" height="500px"></inline-frame>

<!---END-->

`merge` is the reverse of [`split`](split.md): the merged `fullName` column can be split back into two columns:

<!---FUN mergeSplitRoundTrip-->

```kotlin
// merge is the reverse of split: the merged column can be split back into two columns
val merged = peopleDf.merge { name.firstName and name.lastName }.by(" ").into("fullName")
merged.split { col("fullName") }.by(" ").into("firstName", "lastName")
```

<inline-frame src="./resources/mergeSplitRoundTrip.html" width="100%" height="500px"></inline-frame>

<!---END-->
