[//]: # (title: Modify)

Note that all update operations return a new instance of `DataFrame`
## update
Changes values in some cells
```kotlin
df.update { columns }
   [.where { filter } | .at(rowIndices) | .at(rowRange) ] // filter cells to be updated 
    .with { valueExpression } | .withNull() | .notNull { valueExpression }

filter = DataRow.(OldValue) -> Boolean
valueExpression = DataRow.(OldValue) -> NewValue
```
Examples
```kotlin
df.update { price }.with { it * 2 }
df.update { age }.where { name == "Alice" }.with { 20 }
df.update { column }.at(4,6,10).with { "value" } 
df.update { column }.at(5..15).withNull() 
df.update { price }.with { (it + (prev?.price ?: it) + (next?.price ?: it)) / 3 } // moving average
df.update { cases }.with { it.toDouble() / population * 100 }
```
## fillNulls
Replaces `null` values with expression. Equivalent to
```kotlin
update { columns }.where { it == null }
```
Example
```kotlin
df.fillNulls { intCols() }.with { -1 } 
```
## nullToZero
Replace `null` values with `0`. Works for `Int`, `Double`, `Long` and `BigDecimal` columns.
```kotlin
df.nullToZero { columns }
```
### sortBy
Sorts `DataFrame` by one or several columns.
Several sort columns can be combined by `and` operator.
By default, columns are sorted in ascending order with null values going first.
To change column sort order to descending use `.desc` modifier.
To get `null` values in the end of the order use `.nullsLast` modifier

String API:
```kotlin
df.sortBy("age", "name")
```
Column accessors API:
```kotlin
val age by column<Int>()
val name by column<String>()
df.sortBy { age and name.desc }
df.sortBy { name.nullsLast and age.desc }
```
Extension properties API:
```kotlin
df.sortBy { age }
df.sortBy { age and name.desc }
df.sortBy { name.nullsLast and age.desc }
```
To apply descending order to all columns use `sortByDesc` function
```kotlin
df.sortByDesc { name and age}
```
To sort by a continuous range of columns use `cols` function
```kotlin
df.sortBy { cols(0..2) }
```
#### sortByDesc
Similar to [sortBy](#sortby), but uses reversed sort direction
#### sortWith
Sorts `DataFrame` with comparator
```kotlin
val comparator = Comparator { row1, rpw2 -> row1.age.compareTo(row2.age) }
df.sortWith(comparator)

df.sortWith { row1, row2 -> when {
       row1.age < row2.age -> -1
       row1.age > row2.age -> 1
       else -> row1.name.compareTo(row2.name)
    } 
}
```
### mergeRows
Merges values in selected columns into lists grouped by other columns

Input:

|name   | city    | age |
|-------|---------|-----|
| Alice | London  | 15  |
| Bob   | Milan   | 20  |
| Alice | Moscow  | 23  |
| Alice | London  | 30  |
| Bob   | Milan   | 11  |

```kotlin
df.mergeRows { age }
```
Output:

|name   | city   | age
|-------|--------|-----
| Alice | London | [15, 30]
| Bob   | Milan  | [20, 11]
| Alice | Moscow | [23]
### explode
Splits list-like values and spreads them vertically. Reverse to [mergeRows](#mergerows)
The following types of values will be splitted:
* List
* DataFrame
* String (splits by ',')
  Scalar values will not be transformed. Empty lists will result in `null`
  Row values in other columns will be duplicated

Input:

| A | B
|---|---
| 1 | [1, 2, 3]
| 2 | null
| 3 | [4, 5]
| 4 | []
```kotlin
df.explode { B }
```
Output:

| A | B
|---|---
| 1 | 1
| 1 | 2
| 1 | 3
| 2 | null
| 3 | 4
| 3 | 5
| 4 | null

Note: exploded `FrameColumn` turns into `ColumnGroup`

When several columns are exploded, lists in different columns are aligned:

Input:

| A | B | C
|---|---|---
| 1 | [1, 2] | [1, 2]
| 2 | [] | [3, 4]
| 3 | [3, 4, 5] | [5, 6]
```kotlin
df.explode { B and C }
```
Output:

| A | B | C
|---|---|---
| 1 | 1 | 1
| 1 | 2 | 2
| 2 | null | 3
| 2 | null | 4
| 3 | 3 | 5
| 3 | 4 | 6
| 3 | 5 | null

### append
Adds one or several rows to `DataFrame`
```kotlin
df.append (
    "Mike", 15,
    "John", 17, 
    "Bill", 30)
```
### shuffled
Reorders rows randomly
```kotlin
df.shuffled()
```
## Modify columns
Note that `DataFrame` object is immutable, so all modification operations return a new instance of `DataFrame`
### add
Adds new column to `DataFrame`
```kotlin
add(columnName) { rowExpression }
```
See [row expressions](rows.md#row-expressions)
```kotlin
df.add("year of birth") { 2021 - age }
df.add("diff") { temperature - (prev?.temperature ?: 0) }
```
Add several columns:
```kotlin
df.add {
   "is adult" { age > 18 }
   "name length" { name.length } 
}
```
or with `+` operator
```kotlin
df + {
   "is adult" { age > 18 }
   "name length" { name.length } 
}
```
### remove
Removes columns from `DataFrame`
```kotlin
df.remove { columns }
df - { columns }
```
See [Column Selectors](columns.md#column-selectors) for column selection syntax
### convert
Changes the type of columns. Supports automatic type conversions between value types `Int`, `String`, `Double`, `Long`, `Short`, `Float`,`BigDecimal`, 'LocalDateTime', 'LocalDate', 'LocalTime'
```kotlin
df.convert { age }.to<Double>()
df.convert { age }.with { it.toString() }
```
Helper functions for value types are also available
```kotlin
df.convert { age }.toFloat()
df.convert { all() }.toStr()
df.convert { timestamp }.toDateTime()
``` 
### split
Splits cell value into several values and spreads them horizontally or vertically.

Default split behavior:
* for `String` values: split by `,` and trim (leading and trailing whitespace removed)
* for `List` values: split into list elements

#### Split horizontally
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
#### Split vertically
Reverse operation to [mergeRows](#mergerows). See [explode](#explode) for details
```
df.split { columns }.intoRows()
df.split { columns }.by(delimeters).intoRows()
df.split { columns }.by { splitter }.intoRows()

splitter = (T) -> List<Any>
```
When neither `delimeters` or `splitter` are specified, `split().intoRows()` is equivalent of [explode](#explode)
### merge
Merges several columns into a single column. Reverse operation to [split](#split)
```
df.merge { columns }.into(columnPath)
df.merge { columns }.by(delimeters, options).into(columnPath)
df.merge { columns }.by { merger }.into(columnPath)

merger = List<T> -> Any
```
When no `delimeter` or `merger` are defined, values will be merged into the `List`
```kotlin
df.merge { firstName and lastName }.by(" ").into("fullName")

df.merge { cols { it.name.startsWith("value") } }.into("values")

df.merge { protocol and host and port and path }.by { it[0] + "://" + it[1] + ":" + it[2] + "/" + it[3] }.into("address")
```
### rename
Renames one or several columns without changing its location in `DataFrame`
```
df.rename { columns }.into(name)
df.rename { columns }.into { nameExpression }

nameExpression = (DataColumn) -> String
```
### replace
Replaces one or several columns with new columns
```
df.replace { columns }.with(newColumns)
df.replace { columns }.with { columnExpression }

columnExpression = DataFrame.(DataColumn) -> DataColumn
```
Examples
```kotlin
df.replace { col1 and col2 }.with(newCol1, newCol2)
df.replace { stringCols() }.with { it.lower() }
df.replace { oldColumn }.with(newColumn.rename("newName"))
df.replace { age }.with { 2021 - age named "year" } // another syntax for renaming columns within replace expression
```
### move
Moves one or several columns within `DataFrame`.
```kotlin
df.move { column }.into(columnPath)
df.move { columns }.into { columnPathExpression }
df.move { columns }.under(parentPath)
df.move { columns }.under { parentPathExpression }
df.move { columns }.toTop { columnNameExpression }
df.move { columns }.to(position)
df.move { columns }.toLeft()
df.move { columns }.toRight()
df.move { columns }.after { column }
```
See [Column Selectors](columns.md) for column selection syntax.

Columns in `DataFrame` can be ordered hierarchically and form a tree structure. Therefore column can be addressed by `ColumnPath` that represents a list of column names.

`move` operation allows to change hierarchical order of columns in `DataFrame` by providing a new `ColumnPath` for every column

```kotlin
// name, age, weight -> age, name, weight
df.move { age }.toLeft()

// name, age, weight -> name, weight, age
df.move { weight }.to(1)

// name -> info.name
df.move { name }.into("info", "name")

// name -> info.name
df.move { name }.into { info + "name" } // 'info' column should already exist

// firstName -> fullName.firstName
// lastName -> fullName.lastName
df.move { firstName and lastName }.under("fullName")

// firstName -> fullName.first
// lastName -> fullName.last
df.move { firstName and lastName }.into { path("fullName", it.name.dropLast(4)) }

// a:b:c -> a.b.c
df.move { all() }.into { it.name.split(":") }

// totalCases -> total.cases
// totalRecovered -> total.recovered
df.move { cols { it.name.startsWith("total") } }.into { path("total", it.name.substring(5).decapitalize()) }

// some.path.data1 -> new.column.path.data1
// another.path.data2 -> new.column.path.data2
df.move { dfs { it.parent.name == "path" } }.under { new.column.path } // new.column.path should aready exists

// info.default.data -> default
// some.field.data -> field
df.move { dfs { it.name == "data" } }.toTop { it.parent.name }

// a.b -> b.a
// a.b.c -> a.b.c
df.move { dfs { it.path.length == 2 } }.into { it.path.reverse() }
```
### group
Group columns into column groups. It is a special case of [move](#move) operation
```
df.group { columns }.into(groupName)
df.group { columns }.into { groupNameExpression }

groupNameExpression = DataColumn.(DataColumn) -> String
```
Examples
```kotlin
df.group { firstName and lastName }.into("name")
df.group { nameContains(":") }.into { name.substringBefore(":") }
```
### ungroup
Replaces `ColumnGroup` with its nested columns. Reverse operation to [group](#group)
```kotlin
// fullName.firstName -> firstName
// fullName.lastName -> lastName
df.ungroup { fullName }
``` 
### flatten
Removes all column grouping under selected columns. Potential column name clashes are resolved by adding minimal required prefix from ancestor column names
```
df.flatten()
df.flatten { rootColumns }
```
Example
```kotlin
// a.b.c.d -> "d"
// a.f -> "f"
// a.c.d.e -> "d.e"
// a.b.e -> "b.e"
df.flatten { a }
```
### gather
Converts several columns into two `key-value` columns, where `key` is a name of original column and `value` is column data
This is reverse to [pivot](#pivot)
```
df.gather { columns }.into(keyColumnName)

df.gather { columns }.where { valueFilter }.map { valueTransform }.mapNames { keyTransform }.into(keyColumnName, valueColumnName)
```
**Input**

| city | Feb, 18 | Feb, 19 | Feb, 20 | Feb, 21
|--------|---------|---------|--------|---
| London | 3 | 5 | 4 | null
| Milan  | 7 | null | 3 | 5
```kotlin
df.gather { cols(1..4) }.where { it != null}.mapNames { it.substring(5) }.into("day", "temperature")
```
**Output**

| city | day | temperature
|--------|---------|---------
| London | 18 | 3
| London | 19 | 5
| London | 20 | 4
| Milan | 18 | 7
| Milan | 20 | 3
| Milan | 21 | 5

When `valueColumnName` is not defined, only 'key' column is added. In this case `valueFilter` will default to `{ it }` for `Boolean` columns and `{ it != null }` for other columns

**Input**

name | London | Paris | Milan
-----|--------|-------|-------
Alice| true | false | true
Bob | false | true | true
```kotlin
df.gather { cols(1..4) }.into("visited")
```
**Output**

name | visited
-----|--------
Alice | London
Alice | Milan
Bob | Paris
Bob | Milan

### pivot
Converts two key-value columns into several columns using values in `key` column as new column names and values in `value` column as new column values.
This is reverse to [gather](#gather)
```
df.pivot { keyColumns }.withIndex { indexColumns }.into { rowExpression }
```
**Input**

| city | day | temperature
|--------|---------|---------
| London | 18 | 3
| London | 19 | 5
| London | 20 | 4
| Milan | 18 | 7
| Milan | 20 | 3
| Milan | 21 | 5

```kotlin
df.pivot { day.map { "Feb, $it" } }.withIndex { city }.into { temperature }
```
**Output**

| city | Feb, 18 | Feb, 19 | Feb, 20 | Feb, 21
|--------|---------|---------|--------|---
| London | 3 | 5 | 4 | null
| Milan  | 7 | null | 3 | 5
