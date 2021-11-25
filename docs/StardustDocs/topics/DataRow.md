[//]: # (title: DataRow)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.DataRowApi-->

`DataRow` represents a single record, one piece of data within a [`DataFrame`](DataFrame.md)

## Row functions

* `index(): Int` - sequential row number in `DataFrame`, starts from 0
* `prev(): DataRow?` - previous row (`null` for the first row)
* `next(): DataRow?` - next row (`null` for the last row)
* `diff { rowExpression }: T` - difference between results of [row expression](#row-expressions) calculated for current and previous rows
* `values(): List<Any?>` - list of all cell values from the current row
* `valuesOf<T>(): List<T>` - list of values of given type 
* `columnNames(): List<String>` - list of all column names 
* `namedValues(): List<NameValuePair<Any?>>` - list of name-value pairs where `name` is a column name and `value` is cell value
* `namedValuesOf<T>(): List<NameValuePair<T>>` - list of name-value pairs where value has given type 
* `getRow(Int): DataRow` - row from `DataFrame` by row index
* `near(Iterable<Int>): Sequence<DataRow>` - sequence of the nearest rows by relative index: `near(-1..1)` will return previous, current and next row. Requested indices will be coerced to valid range and invalid indices will be skipped
* `rows(Iterable<Int>): Sequence<DataRow>` - sequence of the rows by absolute index. Requested indices are not coerced to valid boundaries and you should care about it
* `get(column): T` - cell value by this row and given `column`
* `df()` - `DataFrame` that current row belongs to

## Row expressions
Row expressions provide a value for every row of `DataFrame` and are used in [add](add.md), [filter](filter.md), [forEach](iterate.md), [update](update.md) and other operations.

<!---FUN expressions-->

```kotlin
// Row expression computes values for a new column
df.add("fullName") { name.firstName + " " + name.lastName }

// Row expression computes updated values
df.update { weight }.at(1, 3, 4).with { prev()?.weight }

// Row expression computes cell content for values of pivoted column
df.pivot { city }.with { name.lastName.uppercase() }
```

<!---END-->

Row expression signature: ```DataRow.(DataRow) -> T```. Row values can be accessed with or without ```it``` keyword. Implicit and explicit argument represent the same `DataRow` object.

## Row conditions
Row condition is a special case of [row expression](#row-expressions) that returns `Boolean`. 

<!---FUN conditions-->

```kotlin
// Row condition is used to filter rows by index
df.filter { index() % 5 == 0 }

// Row condition is used to drop rows where `age` is the same as in previous row
df.drop { diff { age } == 0 }

// Row condition is used to filter rows for value update
df.update { weight }.where { index() > 4 && city != "Paris" }.withValue(50)
```

<!---END-->

Row condition signature: ```DataRow.(DataRow) -> Boolean```

## Row statistics

The following [statistics](statistics.md) are available for `DataRow`:
* `rowMax`
* `rowMin`
* `rowSum`
* `rowMean`
* `rowStd`

All these statistics are applied only to values of appropriate types and incompatible values will be ignored.
For example, if `DataFrame` has columns of type `String` and `Int`, `rowSum()` will successfully compute sum of `Int` values in a row and ignore `String` values.
