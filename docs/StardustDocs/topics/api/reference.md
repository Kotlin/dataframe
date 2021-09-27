[//]: # (title: API Reference)

# `DataFrame` info
## describe
Generates descriptive statistics
# Compute statistics

See [Row Expressions](#row-expressions) for details
# Modify cells

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
See [Working with `GroupedDataFrame`](#working-with-groupeddataframe) for details
For `String` or `ColumnAccessor` API, `aggregate` can be omitted:
```kotlin
val name by column<String>()
val city by column<String>()
val alive by column<Boolean>()

df.groupBy(name, city) {
    nrow() into "total"
    count { alive() } into "alive"
}
```

## Column operations
Operations for a single `DataColumn` object

### rename
{id = "ColumnRename"}

Returns `DataColumn` with a new name
```kotlin
column.rename("newName")
```

### distinct
{id="ColumnDistinct"}

Return `DataColumn` with unique values
```
column.distinct()
``` 
### guessType
Changes type of `DataColumn` based on its actual values
```kotlin
val mixed by columnOf("Alice", 3, true)
mixed.type() // Any
```
```kotlin
val filtered = mixed.filter { it is String}
filtered.type() // Any
```
```kotlin
val guessed = filtered.guessType()
guessed.type() // String
```
### digitize
Return `DataColumn` with indices of the bins to which each value in original `DataColumn` belongs
```
column.digitize(bins, right = false)
column.digitize(bin0, bin1, bin2)
column.digitize(bin0, bin1, bin2, right = true)
```
`right` is an optional parameter indicating whether to include right edge of the bin into interval instead of left edge. By default, `right = false`

For value `x` an index `i` is returned when
`bins[i-1] <= x < bins[i]` if `right = false`
`bins[i-1] < x <= bins[i]` if `right = true`
If `x` is below all bins, 0 is returned. If `x` is above all bins, `bins.length` is returned
### Arithmetic operations
For `Int`, `Double`, `Long` and `BigDecimal` columns `+`, `-`, `*`, `/` operations with scalar values are supported
### Column statistics

Statistics for `Number` columns:
```kotlin
column.sum()
column.mean()
column.median()
```

Statistics for `Comparable` columns:
```kotlin
column.max()
column.maxBy { expression }
column.min()
column.maxBy { expression }
```
## `DataFrame` Presentation
### format
Applies conditional formatting to cells. Returns `FormattedFrame` that can be rendered in Jupyter notebook
```
df.format { columns }
    [.where { rowFilter } ]
    .with { formatter }

formatter = FormattingDSL.(Value) -> CellFormat
CellFormat = bold | italic | underline | background(color) | textColor(color) | format1 and format2 | linearBg(value, v1 to color1, v2 to color2)
color = rgb(r,g,b) | linear(value, v1 to color1, v2 to color2) | white | gray | red | green | ...
```
If `columns` are not specified, formatting is applied to all numeric columns without `null` values
```kotlin
df.format().with { if(it >= 0) background(green) else background(red) }
```
Several formatters can be chained:
```kotlin
df.format { all() }.where { index % 0 == 2 }.with { background(lightGray) }
  .format { age }.where { it > 20 }.with { bold and underline }
  .format { score }.with { linearBg(it, 0 to red, 100 to green) }
```

## Export `DataFrame`
### writeCSV
Exports `DataFrame` to `CSV` file
```kotlin
df.writeCSV("output.csv")
```
### writeClass
Exports `DataFrame` to `List` of auto-generated data classes. Only for `Jupyter` environment.
```kotlin
val list = df.writeClass("Person")
```
### toMap
Converts `DataFrame` to `Map<String, List<Any?>>`
```kotlin
df.toMap()
```
## Column kinds
There are three kinds of `DataColumn`:
* `ColumnGroup`: every element is `DataRow`, implements 'DataFrame' interface
* `FrameColumn`: every element is `DataFrame`
* `ValueColumn`: all other types of elements

### ValueColumn
`ValueColumn` stores one dimensional array of elements

### ColumnGroup
`ColumnGroup` is a named column that stores a list of columns. It supports both 'Column' and 'DataFrame' operations 

### FrameColumn
Every element of `FrameColumn` is `DataFrame`, so `FrameColumn` can be interpreted as groups of rows. 
Any `DataFrame` with `FrameColumn` can be [converted](#working-with-groupeddataframe) to `GroupedDataFrame`:
```kotlin
df.asGrouped { groups }
```
`DataFrame`s stored in `FrameColumn` can be [unioned](#union) into single `DataFrame`:
```kotlin
val df = frameColumn.union()
```
## Column Selectors
`DataFrame` provides a column selection DSL for selecting arbitrary set of columns.
Column selectors are used in many operations:
```
df.select { columns }
df.remove { columns }
df.update { columns }.with { expression }
df.gather { columns }.into(keyName, valueName)
df.move { columns }.under(groupName)
```
### Select single column
```
columnName // column by extension property
it.columnName // column by extension property
column // column by accessor
it[column] // column by accessor
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
allAfter(column) // all columns that are located to the right from target column, excluding target column
allSince(column) // all columns that are located to the right from target column, including target column
allBefore(column) // all columns that are located to the left from target column, excluding target column
allUntil(column) // all columns that are located to the left from target column, including target column
```
### Special column selectors
```
// Select columns of specific type, with optional predicate
stringCols { condition }
intCols { condition }
booleanCols { condition }
doubleCols { condition }

// Select columns by column name condition
nameContains(text)
startsWith(prefix)
endsWith(suffix)
```
### Modify resulting column set
```
columnSet.drop(n) // remove first 'n' columns from column set
columnSet.take(n) // take first 'n' columns of column sest
columnSet.filter { condition } // filter columns set by condition
columnSet.except { otherColumnSet }
columnSet.except ( otherColumnSet )
```
Column selectors can be used to select subcolumns of a `ColumnGroup`
```kotlin
val firstName by column("Alice", "Bob")
val middleName by column("Jr", null)
val lastName by column("Merton", "Marley")
val age by column(15, 20)

val fullName by column(firstName, middleName, lastName) // create column group of three columns
val df = fullName + age

df.select { fullName.cols { !it.hasNulls } } // firstName, lastName
df.select { fullName.cols(0, 2) } // firstName, lastName
df.select { fullName.cols(0..1) } // firstName, middleName
df.select { fullName[firstName] }
df.select { fullName.cols(middleName, lastName) }
df.select { fullName.cols().drop(1) }
```
## Column properties
