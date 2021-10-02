[//]: # (title: Rows)

## Row properties
`DataRow` properties:
* `index: Int` - sequential row number in `DataFrame`, starts from 0
* `prev: DataRow?` - previous row (`null` for the first row)
* `next: DataRow?` - next row (`null` for the last row)

`DataRow` functions:
* `getRow(Int): DataRow` - row from `DataFrame` by row index
* `neighbours(Iterable<Int>)` - sequence of nearest rows by relative index: `neighbours(-1..1)` will return previous, current and next row
* `get(column)` - cell value from column by current row index
* `values()` - list of all cell values from current row
* `df()` - `DataFrame` that current row belongs to

If some of these properties clash with generated extension properties, they still can be accessed as functions `index()`, `prev()`, `next()`

## Row expressions
Row expressions provide a value for every row of `DataFrame` and are used in [add](modify.md#add), [filter](access.md#filter-drop), forEach, [update](modify.md#update) and other operations
```kotlin
df.add("fullName") { firstName + " " + lastName }
```
Within row expression you can access [row-properties](#row-properties)
```kotlin
df.add("diff") { value - prev?.value }
df.filter { index % 5 == 0 }
```
Row expression signature is ```DataRow.(DataRow) -> T```, so row values can be accessed with or without ```it``` keyword

## Row conditions
Row condition is a [row expression](#row-expressions) that returns `Boolean`. Its signature is ```DataRow.(DataRow) -> Boolean```

Row conditions are used in various filtration operations:

```kotlin
df.filter { it.name.startsWith("A") }
df.filter { name.length == 5 }
```
[Row properties](#row-properties) can be also used in row conditions:
```kotlin
df.filter { index % 2 == 0}
df.drop { age == prev?.age }
df.update { score }.where { index > 20}.with { prev?.score } 
```
