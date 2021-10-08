[//]: # (title: Columns)

## Column types

### ValueColumn

### ColumnGroup

### FrameColumn

## Column conditions
When one or several columns are selected based no condition, the following column properties are available:
* `name: String`
* `path: ColumnPath`
* `type: KType`
* `hasNulls: Boolean`

## Column selectors
`DataFrame` provides a column selection DSL for selecting arbitrary set of columns.
Column selectors are used in many operations:
```kotlin
df.select { columns }
df.remove { columns }
df.update { columns }.with { expression }
df.gather { columns }.into(keyName, valueName)
df.move { columns }.under(groupName)
```

### Select single column
<tabs>
<tab title="Generated properties">
``` kotlin 
columnName // column by extension property
it.columnName // column by extension property
col(index) // column by index
column.rename("newName") // column with a new name
```
</tab>
<tab title="Column definitions">

``` kotlin 
column // column by accessor
it[column] // column by accessor
col(index) // column by index
column.rename("newName") // column with a new name
```

</tab>
<tab title="String syntax">

``` kotlin 
it["columnName"] // column by name
"columnName"<tabs>() // typed column by name
col(index) // column by index
column.rename("newName") // column with a new name
```

</tab>
</tabs>

### Select several columns

```kotlin
columnSet1 and columnSet2 // union of column sets
cols(index1, index2, indexN) // columns by indices
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

# Special column selectors
```kotlin
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
```kotlin
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
