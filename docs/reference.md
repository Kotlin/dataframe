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
    * by column
    * by row
    * as iterable
* Modify rows
    * `filter` 
    * `sortBy` / `sortByDesc`
    * `mergeRows`
    * `distinct`
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
