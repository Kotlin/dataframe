[//]: # (title: split)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Splits every value in the given columns into several values. Splitted values can be spread horizontally or vertically or remain inside the original column as `List`

The following types of columns can be splitted by default:
* `String`: split by `,` and trim
* `List`: split into elements
* `DataFrame`: split into rows

```kotlin
df.split { columns }
    [.by(delimeters) | .by { splitter } | .match(regex)] // how to split cell value
    [.default(value)] // how to fill nulls
    .into(columnNames) [ { columnNamesGenerator } ] | .inward(columnNames) [ { columnNamesGenerator } | .inplace() | .intoRows() | .intoColumns() ] // where to store results

splitter = DataRow.(T) -> Iterable<Any>
columnNamesGenerator = DataColumn.(columnIndex: Int) -> String
```

Storage options:
* `into(col1, col2, ... )` - store splitted values in new top-level columns
* `inward(col1, col2, ...)` - store splitted values in new columns nested inside original column
* `inplace` - store splitted values in original column as `List`
* `intoRows` - spread splitted values vertically into new rows
* `intoColumns` - split `FrameColumn` into `ColumnGroup` storing in every cell a `List` of original values per every column

`columnNamesGenerator` is used to generate names for additional columns when the list of explicitly specified `columnNames` was not long enough. `columnIndex` starts with `1` for the first additional column name.  

Default `columnNamesGenerator` generates column names `splitted1`, `splitted2`...

## Split horizontally
Reverse operation to [`merge`](merge.md)

<!---FUN split-->
<tabs>
<tab title="Properties">

```kotlin
df.split { name.firstName }.by { it.chars().toList() }.inplace()

df.split { name }.by { it.values() }.into("nameParts")

df.split { name.lastName }.by(" ").default("").inward { "word$it" }
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val firstName by name.column<String>()
val lastName by name.column<String>()

df.split { firstName }.by { it.chars().toList() }.inplace()

df.split { name }.by { it.values() }.into("nameParts")

df.split { lastName }.by(" ").default("").inward { "word$it" }
```

</tab>
<tab title="Strings">

```kotlin
df.split { "name"["firstName"]<String>() }.by { it.chars().toList() }.inplace()

df.split { name }.by { it.values() }.into("nameParts")

df.split { "name"["lastName"] }.by(" ").default("").inward { "word$it" }
```

</tab></tabs>
<!---END-->

`String` columns can also be splitted into group matches of [`Regex`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/) pattern:

<!---FUN splitRegex-->

```kotlin
merged.split { name }
    .match("""(.*) \((.*)\)""")
    .inward("firstName", "lastName")
```

<!---END-->

`FrameColumn` can be splitted into columns:

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
Returns `DataFrame` with duplicated rows for every splitted value. 

Reverse operation to [`implode`](implode.md).

Use `.intoRows()` terminal operation in `split` configuration to spread splitted values vertically:

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

df.split { group("name") }.by { it.values() }.intoRows()
```

</tab></tabs>
<!---END-->

Equals to `split { column }...inplace().explode { column }`. See [`explode`](explode.md) for details.
