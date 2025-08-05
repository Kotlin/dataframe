[//]: # (title: DataRow)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.DataRowApi-->

`DataRow` represents a single record, one piece of data within a [`DataFrame`](DataFrame.md)

## Row functions

<snippet id="rowFunctions">

* `index(): Int` — sequential row number in [`DataFrame`](DataFrame.md), starts from 0
* `prev(): DataRow?` — previous row (`null` for the first row)
* `next(): DataRow?` — next row (`null` for the last row)
* `diff(T) { rowExpression }: T / diffOrNull { rowExpression }: T?` — difference between the results of a [row expression](DataRow.md#row-expressions) calculated for current and previous rows
* `explode(columns): DataFrame<T>` — spread lists and [`DataFrame`](DataFrame.md) objects vertically into new rows
* `values(): List<Any?>` — list of all cell values from the current row
* `valuesOf<T>(): List<T>` — list of values of the given type 
* `columnsCount(): Int` — number of columns
* `columnNames(): List<String>` — list of all column names
* `columnTypes(): List<KType>` — list of all column types 
* `namedValues(): List<NameValuePair<Any?>>` — list of name-value pairs where `name` is a column name and `value` is cell value
* `namedValuesOf<T>(): List<NameValuePair<T>>` — list of name-value pairs where value has given type 
* `transpose(): DataFrame<NameValuePair<*>>` — [`DataFrame`](DataFrame.md) of two columns: `name: String` is column names and `value: Any?` is cell values
* `transposeTo<T>(): DataFrame<NameValuePair<T>>`— [`DataFrame`](DataFrame.md) of two columns: `name: String` is column names and `value: T` is cell values
* `getRow(Int): DataRow` — row from [`DataFrame`](DataFrame.md) by row index
* `getRows(Iterable<Int>): DataFrame` — [`DataFrame`](DataFrame.md) with subset of rows selected by absolute row index. 
* `relative(Iterable<Int>): DataFrame` — [`DataFrame`](DataFrame.md) with subset of rows selected by relative row index: `relative(-1..1)` will return previous, current and next row. Requested indices will be coerced to the valid range and invalid indices will be skipped
* `getValue<T>(columnName)` — cell value of type `T` by this row and given `columnName`
* `getValueOrNull<T>(columnName)` — cell value of type `T?` by this row and given `columnName` or `null` if there's no such column
* `get(column): T` — cell value by this row and given `column`
* `String.invoke<T>(): T` — cell value of type `T` by this row and given `this` column name
* `ColumnPath.invoke<T>(): T` — cell value of type `T` by this row and given `this` column path
* `ColumnReference.invoke(): T` — cell value of type `T` by this row and given `this` column
* `df()` — [`DataFrame`](DataFrame.md) that current row belongs to

</snippet>

## Row expressions
Row expressions provide a value for every row of [`DataFrame`](DataFrame.md) and are used in [add](add.md), [filter](filter.md), [forEach](iterate.md), [update](update.md) and other operations.

<!---FUN expressions-->

```kotlin
// Row expression computes values for a new column
df.add("fullName") { name.firstName + " " + name.lastName }

// Row expression computes updated values
df.update { weight }.at(1, 3, 4).with { prev()?.weight }

// Row expression computes cell content for values of pivoted column
df.pivot { city }.with { name.lastName.uppercase() }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.DataRowApi.expressions.html" width="100%"/>
<!---END-->

Row expression signature: ```DataRow.(DataRow) -> T```. Row values can be accessed with or without ```it``` keyword. Implicit and explicit argument represent the same `DataRow` object.

## Row conditions
Row condition is a special case of [row expression](#row-expressions) that returns `Boolean`. 

<!---FUN conditions-->

```kotlin
// Row condition is used to filter rows by index
df.filter { index() % 5 == 0 }

// Row condition is used to drop rows where `age` is the same as in the previous row
df.drop { diffOrNull { age } == 0 }

// Row condition is used to filter rows for value update
df.update { weight }.where { index() > 4 && city != "Paris" }.with { 50 }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.DataRowApi.conditions.html" width="100%"/>
<!---END-->

Row condition signature: ```DataRow.(DataRow) -> Boolean```



## Row statistics

<snippet id="rowStatistics">

The following [statistics](summaryStatistics.md) are available for `DataRow`:
* `rowSum`
* `rowMean`
* `rowStd`

These statistics will be applied only to values of appropriate types, and incompatible values will be ignored.
For example, if a [dataframe](DataFrame.md) has columns of types `String` and `Int`,
`rowSum()` will compute the sum of the `Int` values in the row and ignore `String` values.

To apply statistics only to values of a particular type use `-Of` versions:
* `rowSumOf<T>`
* `rowMeanOf<T>`
* `rowStdOf<T>`
* `rowMinOf<T>`
* `rowMaxOf<T>`
* `rowMedianOf<T>`
* `rowPercentileOf<T>`

</snippet>
