[//]: # (title: split)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Splits every value in the given columns into several values. Splitted values can be spread horizontally or vertically or remain inside the original column as `List`

Default split behavior:
* for `String` values: split by `,` and trim (leading and trailing whitespace removed)
* for `List` values: split into list elements

## Split horizontally
Reverse operation to [merge](merge.md)

```kotlin
df.split { columns }
    [.by(delimeters) | .by { splitter }] // how to split cell value
    [.inward()] // nest resulting columns into original column
    .into(columnNames) [ { columnNamesGenerator } ]

splitter = DataRow.(T) -> Iterable<Any>
columnNamesGenerator = DataColumn.(columnIndex: Int) -> String
```

`columnNamesGenerator` is used to generate names for additional columns when the list of explicitly specified `columnNames` was not long enough.  
`columnIndex` in `columnNamesGenerator` starts with `1` for the first additional column name.  
Default `columnNamesGenerator` generates column names `splitted1`, `splitted2`...

Examples:

<!---FUN split-->
<tabs>
<tab title="Properties">

```kotlin
df.split { name.firstName }.with { it.chars().toList() }.inplace()

df.split { name }.with { it.values() }.into("nameParts")

df.split { name.lastName }.by(" ").inward { "word$it" }
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val firstName by name.column<String>()
val lastName by name.column<String>()

df.split { firstName }.with { it.chars().toList() }.inplace()

df.split { name }.with { it.values() }.into("nameParts")

df.split { lastName }.by(" ").inward { "word$it" }
```

</tab>
<tab title="Strings">

```kotlin
df.split { "name"["firstName"]<String>() }.with { it.chars().toList() }.inplace()

df.split { name }.with { it.values() }.into("nameParts")

df.split { "name"["lastName"] }.by(" ").inward { "word$it" }
```

</tab></tabs>
<!---END-->

## Split vertically
Returns `DataFrame` with duplicated rows for every splitted value. 

<!---FUN splitIntoRows-->
<tabs>
<tab title="Properties">

```kotlin
df.split { name.firstName }.with { it.chars().toList() }.intoRows()

df.split { name }.with { it.values() }.intoRows()
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val firstName by name.column<String>()

df.split { firstName }.with { it.chars().toList() }.intoRows()

df.split { name }.with { it.values() }.intoRows()
```

</tab>
<tab title="Strings">

```kotlin
df.split { "name"["firstName"]<String>() }.with { it.chars().toList() }.intoRows()

df.split { group("name") }.with { it.values() }.intoRows()
```

</tab></tabs>
<!---END-->

This operation is reverse to [mergeRows](mergeRows.md).

`split { column }...intoRows()` 

is equivalent to 

`split { column }...inplace().explode { column }`

See [explode](explode.md) for details
