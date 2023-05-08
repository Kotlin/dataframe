[//]: # (title: split)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Splits every value in the given columns into several values and optionally spreads them horizontally or vertically.

```text
df.split { columns }
    [.cast<Type>()]
    [.by(delimeters) | .by { splitter } | .match(regex)] // how to split cell value
    [.default(value)] // how to fill nulls
    .into(columnNames) [ { columnNamesGenerator } ] | .inward(columnNames) [ { columnNamesGenerator } | .inplace() | .intoRows() | .intoColumns() ] // where to store results

splitter = DataRow.(T) -> Iterable<Any>
columnNamesGenerator = DataColumn.(columnIndex: Int) -> String
```
The following types of columns can be split without any _splitter_ configuration:
* `String`: split by `,` and trim
* `List`: split into elements
* [`DataFrame`](DataFrame.md): split into rows

## Split inplace

Stores split values as lists in original columns.

Use `.inplace()` terminal operation in `split` configuration to spread split values inplace:

<!---FUN splitInplace-->
<tabs>
<tab title="Properties">

```kotlin
df.split { name.firstName }.by { it.chars().toList() }.inplace()
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val firstName by name.column<String>()

df.split { firstName }.by { it.chars().toList() }.inplace()
```

</tab>
<tab title="Strings">

```kotlin
df.split { "name"["firstName"]<String>() }.by { it.chars().toList() }.inplace()
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.splitInplace.html"/>
<!---END-->

## Split horizontally

Stores split values in new columns.
* `into(col1, col2, ... )` — store split values in new top-level columns
* `inward(col1, col2, ...)` — store split values in new columns nested inside original column
* `intoColumns` — split [`FrameColumn`](DataColumn.md#framecolumn) into [`ColumnGroup`](DataColumn.md#columngroup) storing in every cell a `List` of original values per every column

**Reverse operation:** [`merge`](merge.md)

`columnNamesGenerator` is used to generate names for additional columns when the list of explicitly specified `columnNames` was not long enough. `columnIndex` starts with `1` for the first additional column name.

Default `columnNamesGenerator` generates column names `split1`, `split2`...

<!---FUN split-->
<tabs>
<tab title="Properties">

```kotlin
df.split { name.lastName }.by { it.asIterable() }.into("char1", "char2")
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val lastName by name.column<String>()

df.split { lastName }.by { it.asIterable() }.into("char1", "char2")
```

</tab>
<tab title="Strings">

```kotlin
df.split { "name"["lastName"]<String>() }.by { it.asIterable() }.into("char1", "char2")
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.split.html"/>
<!---END-->

<!---FUN split1-->
<tabs>
<tab title="Properties">

```kotlin
df.split { name.lastName }.by { it.asIterable() }.default(' ').inward { "char$it" }
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val lastName by name.column<String>()

df.split { lastName }.by { it.asIterable() }.default(' ').inward { "char$it" }
```

</tab>
<tab title="Strings">

```kotlin
df.split { "name"["lastName"]<String>() }.by { it.asIterable() }.default(' ').inward { "char$it" }
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.split1.html"/>
<!---END-->

`String` columns can also be split into group matches of [`Regex`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/) pattern:

<!---FUN splitRegex-->

```kotlin
val merged = df.merge { name.lastName and name.firstName }.by { it[0] + " (" + it[1] + ")" }.into("name")
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.splitRegex.html"/>
<!---END-->

<!---FUN splitRegex1-->

```kotlin
val name by column<String>()

merged.split { name }
    .match("""(.*) \((.*)\)""")
    .inward("firstName", "lastName")
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.splitRegex1.html"/>
<!---END-->

[`FrameColumn`](DataColumn.md#framecolumn) can be split into columns:

<!---FUN splitFrameColumn-->

```kotlin
val df1 = dataFrameOf("a", "b", "c")(
    1, 2, 3,
    4, 5, 6
)
val df2 = dataFrameOf("a", "b")(
    5, 6,
    7, 8,
    9, 10
)
val group by columnOf(df1, df2)
val id by columnOf("x", "y")
val df = dataFrameOf(id, group)

df.split { group }.intoColumns()
```

<!---END-->

## Split vertically

Stores split values in new rows duplicating values in other columns.

**Reverse operation:** [`implode`](implode.md)

Use `.intoRows()` terminal operation in `split` configuration to spread split values vertically:

<!---FUN splitIntoRows-->
<tabs>
<tab title="Properties">

```kotlin
df.split { name.firstName }.by { it.chars().toList() }.intoRows()

df.split { name }.by { it.values() }.intoRows()
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val firstName by name.column<String>()

df.split { firstName }.by { it.chars().toList() }.intoRows()

df.split { name }.by { it.values() }.intoRows()
```

</tab>
<tab title="Strings">

```kotlin
df.split { "name"["firstName"]<String>() }.by { it.chars().toList() }.intoRows()

df.split { colGroup("name") }.by { it.values() }.intoRows()
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.splitIntoRows.html"/>
<!---END-->

Equals to `split { column }...inplace().explode { column }`. See [`explode`](explode.md) for details.
