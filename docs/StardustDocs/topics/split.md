[//]: # (title: split)

Splits cell value into several values and spreads them horizontally or vertically.

Default split behavior:
* for `String` values: split by `,` and trim (leading and trailing whitespace removed)
* for `List` values: split into list elements

## Split horizontally
Reverse operation to [merge](#merge)
```
df.split { columns }
    [.by(delimeters) | .by { splitter }] // how to split cell value
    [.inward()] // nest resulting columns into original column
    .into(columnNames) [ { columnNamesGenerator } ]

splitter = (T) -> List<Any>
columnNamesGenerator = DataColumn.(columnIndex: Int) -> String
```
`columnNamesGenerator` is used to generate names for additional columns when the list of explicitly specified `columnNames` was not long enough.  
`columnIndex` in `columnNamesGenerator` starts with `1` for the first additional column name.  
Default `columnNamesGenerator` generates column names `splitted1`, `splitted2`...

Examples:
```kotlin
// artifactId -> groupId, artifactId, version 
df.split { artifactId }.by(":").into("groupId", "artifactId", "version")

// info: List<String> -> info.age, info.weight
df.split { info }.inward().into("age", "weight")

// address -> address1, address2, address3, address4 ...
df.split { address }.into { "address$it" } // splits by ','

// text -> firstWord, secondWord, extraWord1, extraWord2 ...
df.split { text }.by(" ").into("firstWord", "secondWord") { "extraWord$it" }
```

Split `Int` column into digits using `splitter`:
```kotlin
fun digits(num: Int) = sequence {
        var k = num
        if(k == 0) yield(0)
        while(k > 0) {
            yield(k % 10)
            k /= 10
        }
    }.toList()

// number -> number.digit1, number.digit2, number.digit3...
df.split { number }.by { digits(it) }.inward().into { "digit$it" }
```
## Split vertically
Reverse operation to [mergeRows](#mergerows). See [explode](#explode) for details
```
df.split { columns }.intoRows()
df.split { columns }.by(delimeters).intoRows()
df.split { columns }.by { splitter }.intoRows()

splitter = (T) -> List<Any>
```
When neither `delimeters` or `splitter` are specified, `split().intoRows()` is equivalent of [explode](#explode)
