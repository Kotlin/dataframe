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
* Analyze
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
    * `mergeRows`
    * `append`
    * `groupBy`
    * `shuffled`
    * `take` / `takeLast`
    * `drop` / `dropLast`
* Modify schema
    * `select`
    * `add`
    * `move`
    * `remove`
    * `cast`
    * `parse`
    * `split`
    * `mergeCols`
    * `group`
    * `ungroup`
    * `flatten`
    * `rename`
    * `replace`
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