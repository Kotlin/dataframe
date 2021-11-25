[//]: # (title: DataRow)

`DataRow` represents a single record, one piece of data within a [`DataFrame`](DataFrame.md)

## Row functions

* `index(): Int` - sequential row number in `DataFrame`, starts from 0
* `prev(): DataRow?` - previous row (`null` for the first row)
* `next(): DataRow?` - next row (`null` for the last row)
* `getRow(Int): DataRow` - row from `DataFrame` by row index
* `neighbours(Iterable<Int>)` - sequence of the nearest rows by relative index: `neighbours(-1..1)` will return previous, current and next row
* `get(column)` - cell value from `column` in this row
* `values()` - list of all cell values from the current row
* `df()` - `DataFrame` that current row belongs to

## Row expressions
Row expressions provide a value for every row of `DataFrame` and are used in [add](add.md), [filter](filter.md), [forEach](iterate.md), [update](update.md) and other operations:
```kotlin
df.add("fullName") { firstName + " " + lastName }
```
[Row functions](#row-functions) can be used inside row expressions:
```kotlin
df.add("diff") { value - prev()?.value }
df.filter { index() % 5 == 0 }
```
Row expression signature is ```DataRow.(DataRow) -> T```, so row values can be accessed with or without ```it``` keyword. Implicit and explicit argument represent the same `DataRow` object.

## Row conditions
Row condition is a [row expression](#row-expressions) that returns `Boolean`. Its signature is ```DataRow.(DataRow) -> Boolean```

Row conditions are used in various filtration operations:

```kotlin
df.filter { it.name.startsWith("A") }
df.filter { name.length == 5 }
```
[Row functions](#row-functions) can be used inside row conditions:
```kotlin
df.filter { index() % 2 == 0}
df.drop { age == prev()?.age }
df.update { score }.where { index() > 20}.with { prev()?.score } 
```
