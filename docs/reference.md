# DataFrame API Reference

**Table of contents**
<!--- TOC -->

* [Build](#build)
    * [from values](#from-values)
    * [from columns](#from-columns)
    * [from objects](#from-objects)
* [Read](#read)
    * [from CSV](#read-csv)
    * [from JSON](#read-json)
* Quick info
    * `schema`
    * `summary`
* [Access data](#access-data)
    * [by column](#by-column)
    * [by row](#by-row)
    * [as Iterable](#as-iterable)
* [Modify rows](#modify-rows)
    * [`filter`](#filter)
    * [`sortBy`](#sortBy)
    * [`distinct`](#distinct)
    * [`mergeRows`](#mergeRows)
    * [`append`](#append)
    * [`groupBy`](#groupBy)
    * [`shuffled`](#shuffled)
    * [`take`/`takeLast`](#take--takelast)
    * [`drop`/`dropLast`](#drop--droplast)
* [Modify schema](#modify-schema)
    * [`select`](#select)
    * [`add`](#add)
    * [`remove`](#remove)
    * [`cast`](#cast)
    * [`split`](#split)
    * [`merge`](#merge)
    * [`rename`](#rename)
    * [`replace`](#replace)
    * [`move`](#move)    
    * [`group`](#group)
    * `ungroup`
    * `flatten`    
    * `gather`
    * `spread`    
* Update data
    * `update`
    * `fillNulls`
    * `nullToZero`
* Merge several data frames
    * `union`
    * `join`
* Modify column
    * `distinct`
    * `digitize`
    * arithmetic operations
* Column statistics
    * `sum`
    * `min` / `max`
    * `mean`
    * `median`
* Grouped data frame aggregation
    * `spread`
    * `countBy`
* Export
    * `writeCSV`
* [Column selectors](#column-selectors)
* [Row expressions](#row-expressions)
* [Row properties](#row-properties)


<!--- END -->

## Build

Several ways to convert data into `DataFrame`
### from values
```kotlin
val df = dataFrameOf("name", "age")(
   "Alice", 15,
   "Bob", 20
)
```

### from columns
`DataFrame` can be created from one o several `DataColumns`. Two columns with data
```kotlin
val name by column("Alice", "Bob")
val age by column(15, 20)
```
can be converted into `DataFrame` in several ways
```kotlin
val df = dataFrameOf(name, age)
```
```kotlin
val df = listOf(name, age).toDataFrame()
```
```kotlin
val df = name + age
```

### from objects

DataFrame can be created from a list of any objects.
Assume we have a list of `Person` objects:
```kotlin
data class Person(val name: String, val age: Int)
val persons = listOf(Person("Alice", 15), Person("Bob", 20))
```
This list can be converted to `DataFrame` either by inspecting all public fields of `Person` class:
```kotlin
val df = persons.toDataFrame()
```
or by explicit expressions for every column:
```kotlin
val df = persons.toDataFrame {
   "name" { name }
   "year of birth" { 2021 - age }
}
```

## Read

DataFrame supports CSV and JSON input formats.
Use `read` method to guess input format based on file extension and content
```kotlin
DataFrame.read("input.csv")
```
Input string can be a file path or URL.

### Read CSV
```kotlin
DataFrame.readCSV("input.csv")
```
### Read JSON
```kotlin
DataFrame.readJSON("https://covid.ourworldindata.org/data/owid-covid-data.json")
```

## Access Data
### by column
```kotlin
df["name"][0]
```
### by row
```kotlin
df[0]["name"]
```
### as iterable
`DataFrame` can be interpreted as an `Iterable` of `DataRow`. Although `DataFrame` doesn't implement `Iterable` interface, it defines most extension functions available for `Iterable`
```kotlin
df.forEach { println(it) }
df.take(5)
df.drop(2)
df.chunked(10)
```
For compatibility with stdlib, `DataFrame` can be converted to `Iterable`
```kotlin
df.asIterable()
```
or to `Sequence`
```kotlin
df.asSequence()
```
## Modify rows
`DataFrame` is immutable, so all modification operations return a new instance
### filter
Filter rows by row predicate

String API:
```kotlin
df.filter { "age"<Int>() > 10 && "name"<String>().startsWith("A") }
```
Column accessors API:
```kotlin
val age by column<Int>()
val name by column<String>()
df.filter { age() > 10 && name().startsWith("A") }
```
Extension properties API:
```kotlin
df.filter { age > 10 && name.startsWith("A") }
```
More examples:
```kotlin
df.filter { index % 2 == 0} // keep even rows
df.filter { age != prev?.age }
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
### distinct
Removes duplicate rows
```kotlin
df.distinct()
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

### append
Adds one or several rows to `DataFrame`
```kotlin
df.append (
    "Mike", 15,
    "John", 17, 
    "Bill", 30)
```
### groupBy
Groups rows in `DataFrame` by one or several key columns. Returns `GroupedDataFrame` that supports various aggregation operations
```kotlin
df.groupBy { name and city }.aggregate {
    nrow() into "num"
    count { age > 18 } into "number of adults"
    median { age } into "median age"
}
df.groupBy { name }.aggregate {
    maxBy { age }.city into "city of oldest person"
    countBy { city } into { "from $it" }
}
```
For single aggregation there is simplier syntax:
```kotlin
df.groupBy { name }.max { age }
df.groupBy { city }.count()
df.groupBy { name }.countInto("count")
```

### shuffled
Reorders rows randomly
```kotlin
df.shuffled()
```
### take / takeLast
Returns `DataFrame` containing first/last `n` rows
```kotlin
df.take(10) // first 10 rows
df.takeLast(20) // last 20 rows
```
### drop / dropLast
Returns `DataFrame` containing all rows except first/last `n` rows
```kotlin
df.drop(10)
df.dropLast(20)
```
## Modify schema
Note that `DataFrame` object is immutable, so all modification operations return a new instance of `DataFrame`
### add
Adds new column to `DataFrame`
```kotlin
add(columnName) { rowExpression }
```
See [row expressions](#row-expressions)
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
See [Column Selectors](#column-selectors) for column selection syntax
### cast
Changes the type of columns. Supports automatic type conversions between value types `Int`, `String`, `Double`, `Long`, `Short`, `Float`,`BigDecimal`
```kotlin
df.cast { age }.to<Double>()
```
Helper functions for value types are also available
```kotlin
df.cast { age }.toFloat()
df.cast { all() }.toStr()
``` 
### split
Splits cell value into several values and spreads them horizontally or vertically.

#### Split horizontally
Reverse oeration to [merge](#merge)
```
df.split { columns }
    [.by(delimeters) | .by { splitter }] // how to split cell value
    [.inward()] // nest resulting columns into original column
    .into(columnNames) | .into { columnNamesGenerator } // how to get new column names

splitter = (T) -> List<Any>
columnNamesGenerator = DataColumn.(columnIndex: Int) -> String
```
Examples:
```kotlin
// artifactId -> groupId, artifactId, version 
df.split { artifactId }.by(":").into("groupId", "artifactId", "version")

// info -> info.age, info.weight
df.split { info }.inward().into("age", "weight")

// address -> address0, address1, address2, address3 ...
df.split { address }.into { "address$it" }
```
#### Split vertically
Reverse oeration to [mergeRows](#mergeRows)
```
df.split { columns } 
    [.by(delimeters) | .by { splitter }]
    .intoRows()

splitter = (T) -> List<Any>
```
Row values in other columns will be duplicated
### merge
Merges several columns into a single column. Reverse operation to [split](#split)
```
df.merge { columns }
    [.by(delimeter) | .by { merger } ]
    .into(columnName) | .into(columnPath)

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
df.replace { columns }.with { columnExpression }

columnExpression = DataFrame.(DataColumn) -> DataColumn
```
Examples
```kotlin
df.replace { age }.with { 2021 - age named "year" }
df.replace { stringCols() }.with { it.lower() }
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
```
See [Column Selectors](#column-selectors) for column selection syntax.

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
df.move { colsDfs { it.parent.name == "path" } }.under { new.column.path } // new.column.path should aready exists

// info.default.data -> default
// some.field.data -> field
df.move { colsDfs { it.name == "data" } }.toTop { it.parent.name }

// a.b -> b.a
// a.b.c -> a.b.c
df.move { colsDfs { it.path.length == 2 } }.into { it.path.reverse() }
```
## Column Selectors
`DataFrame` provides a column selection DSL for selecting arbitrary set of columns.
Column selectors are used in many operations, such as [select](#select), [move](#move), [remove](#remove), [gather](#gather), [update](#update), [sortBy](#sortBy)
Common syntax for using column selector is
```
df.operation { columnSelector }
```
### Select single column
```
columnName // column by extension property
it.columnName // column by extension property
columnName() // column by accessor
it["columnName"] // column by name
"columnName"<Type>() // typed column by name
col(index) // column by index
column.rename("newName") // column with a new name
```
### Select several columns
```
columnSet1 and columnSet2 // union of column sets
cols(index1, ..., indexN) // columns by indices
cols(index1..index2) // columns by range of indices
cols { condition } // columns by condition
colsOf<Type>() // columns of specific type
colsOf<Type> { condition } // columns of specfic type that match condition
dfs { condition } // traverse column tree and yield top-level columns that match condition
dfsOf<Type>() // traverse column tree and yield columns of specific type
dfsOf<Type> { condition } // traverse column tree and yield columns of specific type that match condition
all() // all columns
```
### Select columns of value types
```
stringCols { condition }
intCols { condition }
booleanCols { condition }
doubleCols { condition }
```
### Modify resulting column set
```
columnSet.drop(n) // remove first 'n' columns from column set
columnSet.take(n) // take first 'n' columns of column sest
columnSet.filter { condition } // filter columns set by condition
columnSet.except { otherColumnSet }
columnSet.except ( otherColumnSet )
```
Column selectors can be used to select subcolumns of a particular `MapColumn`
```kotlin
val firstName by column("Alice", "Bob")
val middleName by column("Jr", null)
val lastName by column("Merton", "Marley")
val age by column(15, 20)

val fullName by column(firstName, middleName, lastName) // create MapColumn consisting of three columns
val df = fullName + age

df.select { fullName.cols { !it.hasNulls } } // firstName, lastName
df.select { fullName.cols(0, 2) } // firstName, middleName, lastName
df.select { fullName[firstName] }
df.select { fullName.cols(middleName, lastName) }
df.select { fullName.cols().drop(1) }
```
## Row expressions
Row expression provide a value for every row of `DataFrame` and is used in [add](#add), [filter](#filter), [forEach](#forEach), [update](#update) and other opertaions

Row expression syntax is ```DataRow.(DataRow) -> T``` so row values can be accessed with or without ```it``` keyword
```kotlin
df.filter { it.name.startsWith("A") }
df.filter { name.length == 5 }
```
Within row expression you can access [row-properties](#row-properties)
```kotlin
df.add("diff") { value - prev?.value }
df.filter { index % 5 == 0 }
```
## Row properties
`DataRow` object provides three properties:
* `index` - sequential row number in `DataFrame`, starts from 0
* `prev` - previous row (`null` for the first row)
* `next` - next row (`null` for the last row)

If some of these properties clash with generated extension properties, they still can be accessed as functions `index()`, `prev()`, `next()`