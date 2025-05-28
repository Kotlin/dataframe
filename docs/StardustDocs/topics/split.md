[//]: # (title: split)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

This operation splits every value in the given columns into several values 
and optionally spreads them horizontally or vertically.

```text
df.split { columns }
    [.cast<Type>()]
    [.by(delimiters|regex [,trim=true][,ignoreCase=true][,limit=0]) | .by { splitter } | .match(regex)] // how to split cell value
    [.default(value)] // how to fill nulls
    .into(columnNames) [ { columnNamesGenerator } ] | .inward(columnNames) [ { columnNamesGenerator } | .inplace() | .intoRows() | .intoColumns() ] // where to store results

splitter = DataRow.(T) -> Iterable<Any>
columnNamesGenerator = DataColumn.(columnIndex: Int) -> String
```
The following types of columns can be split easily:
* `String`: for instance, by `","`
* `List`: splits into elements, no `by` required!
* [`DataFrame`](DataFrame.md): splits into rows, no `by` required!

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

## Split in place

Stores split values as lists in their original columns.

Use the `.inplace()` terminal operation in your `split` configuration to spread split values in place:

<!---FUN splitInplace-->
<tabs>
<tab title="Properties">

```kotlin
df.split { name.firstName }.by { it.asIterable() }.inplace()
```

</tab>
<tab title="Strings">

```kotlin
df.split { "name"["firstName"]<String>() }.by { it.asIterable() }.inplace()
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.splitInplace.html" width="100%"/>
<!---END-->

## Split horizontally

Stores split values in new columns.
* `into(col1, col2, ... )` — stores split values in new top-level columns
* `inward(col1, col2, ...)` — stores split values in new columns nested inside the original column
* `intoColumns` — splits [`FrameColumns`](DataColumn.md#framecolumn) into [`ColumnGroups`](DataColumn.md#columngroup) storing in every cell in a `List` of the original values per column

**Reverse operation:** [`merge`](merge.md)

`columnNamesGenerator` is used to generate names for additional columns when the list of explicitly specified `columnNames` is not long enough.
`columnIndex` starts with `1` for the first additional column name.

The default `columnNamesGenerator` generates column names like `split1`, `split2`, etc.

Some examples:

<!---FUN split-->
<tabs>
<tab title="Properties">

```kotlin
df.split { name.lastName }.by { it.asIterable() }.into("char1", "char2")
```

</tab>
<tab title="Strings">

```kotlin
df.split { "name"["lastName"]<String>() }.by { it.asIterable() }.into("char1", "char2")
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.split.html" width="100%"/>
<!---END-->

<!---FUN split1-->
<tabs>
<tab title="Properties">

```kotlin
df.split { name.lastName }
    .by { it.asIterable() }.default(' ')
    .inward { "char$it" }
```

</tab>
<tab title="Strings">

```kotlin
df.split { "name"["lastName"]<String>() }
    .by { it.asIterable() }.default(' ')
    .inward { "char$it" }
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.split1.html" width="100%"/>
<!---END-->

`String` columns can also be split into group matches of [`Regex`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/) patterns:

<!---FUN splitRegex1-->

```kotlin
val name by column<String>()

merged.split { name }
    .match("""(.*) \((.*)\)""")
    .inward("firstName", "lastName")
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.splitRegex1.html" width="100%"/>
<!---END-->

[`FrameColumn`](DataColumn.md#framecolumn) can be split into columns:

<!---FUN splitFrameColumn-->

```kotlin
val df1 = dataFrameOf("a", "b", "c")(
    1, 2, 3,
    4, 5, 6,
)
val df2 = dataFrameOf("a", "b")(
    5, 6,
    7, 8,
    9, 10,
)
val group by columnOf(df1, df2)
val id by columnOf("x", "y")
val df = dataFrameOf(id, group)

df.split { group }.intoColumns()
```

<!---END-->

## Split vertically

Stores split values in new rows, duplicating values in other columns.

**Reverse operation:** [`implode`](implode.md)

Use the `.intoRows()` terminal operation in your `split` configuration to spread split values vertically:

<!---FUN splitIntoRows-->
<tabs>
<tab title="Properties">

```kotlin
df.split { name.firstName }.by { it.asIterable() }.intoRows()

df.split { name }.by { it.values() }.intoRows()
```

</tab>
<tab title="Strings">

```kotlin
df.split { "name"["firstName"]<String>() }.by { it.asIterable() }.intoRows()

df.split { colGroup("name") }.by { it.values() }.intoRows()
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.splitIntoRows.html" width="100%"/>
<!---END-->

Equals to `split { column }...inplace().explode { column }`. See [`explode`](explode.md) for details.
