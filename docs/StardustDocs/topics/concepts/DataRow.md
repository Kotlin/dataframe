[//]: # (title: DataRow)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.DataRowApiSamples-->

`DataRow` represents a single record, one piece of data within a [`DataFrame`](DataFrame.md)

## Row functions

<snippet id="rowFunctions">

* `index(): Int` — sequential row number in [`DataFrame`](DataFrame.md), starts from 0;
* `prev(): DataRow?` — previous row (`null` for the first row);
* `next(): DataRow?` — next row (`null` for the last row);
* `diff(T) { rowExpression }: T / diffOrNull { rowExpression }: T?` — difference between the results 
of a [row expression](DataRow.md#row-expressions) calculated for the current and previous rows;
* `explode(columns): DataFrame<T>` — spread lists and [`DataFrame`](DataFrame.md) objects vertically into new rows;
* `values(): List<Any?>` — list of all cell values from the current row;
* `valuesOf<T>(): List<T>` — list of values of the given type ;
* `columnsCount(): Int` — number of columns;
* `columnNames(): List<String>` — list of all column names;
* `columnTypes(): List<KType>` — list of all column types;
* `namedValues(): List<NameValuePair<Any?>>` — list of name-value pairs where `name` is a column name 
and `value` is a cell value;
* `namedValuesOf<T>(): List<NameValuePair<T>>` — list of name-value pairs where the value has the given type;
* `transpose(): DataFrame<NameValuePair<*>>` — [`DataFrame`](DataFrame.md) with two columns: `name: String` for column names 
and `value: Any?` for cell values;
* `transposeTo<T>(): DataFrame<NameValuePair<T>>` — [`DataFrame`](DataFrame.md) with two columns: `name: String` for column names 
and `value: T` for cell values;
* `getRow(Int): DataRow` — row from the [`DataFrame`](DataFrame.md) by a row index;
* `getRows(Iterable<Int>): DataFrame` — [`DataFrame`](DataFrame.md) with a subset of rows selected by absolute row indices;
* `relative(Iterable<Int>): DataFrame` — [`DataFrame`](DataFrame.md) with a subset of rows selected by relative row indices:
`relative(-1..1)` will return the previous, current, and next row. Requested indices will be coerced to the valid range
and invalid indices will be skipped;
* `getValue<T>(columnName)` — cell value of type `T` by this row and the given `columnName`;
* `getValueOrNull<T>(columnName)` — cell value of type `T?` by this row 
and the given `columnName` or `null` if there's no such column;
* `get(column): T` — cell value by this row and the given `column`;
* `String.invoke<T>(): T` — cell value of type `T` by this row and the given `this` column name;
* `ColumnPath.invoke<T>(): T` — cell value of type `T` by this row and the given `this` column path;
* `ColumnReference.invoke(): T` — cell value of type `T` by this row and the given `this` column;
* `df()` — [`DataFrame`](DataFrame.md) that the current row belongs to.

</snippet>

The following dataframe will be used in the examples below:

<!---FUN dfDataRow-->

```kotlin
df
```

<!---END-->
<inline-frame src="resources/dfDataRow.html" width="100%"/>

## Row expressions
Row expressions provide a value for every row of [`DataFrame`](DataFrame.md) 
and are used in [add](add.md), [filter](filter.md), [forEach](iterate.md), [update](update.md), and other operations.
There are two types of row expressions, differing in what the `it` argument refers to:

### `RowExpression`
`RowExpression` computes a new value for every selected cell given the [`DataRow`](DataRow.md) of that cell.
Both `this` and `it` keywords in `RowExpression` refer to the same [`DataRow`](DataRow.md). 
Row values can be accessed with or without these keywords.

`RowExpression` signature: ```DataRow.(DataRow) -> T```.

#### `RowExpression` examples

##### add {collapsible="true"}

<!---FUN addWithExpression-->
<tabs>
<tab title="Properties">

```kotlin
// Row expression computes values for a new column
df.add("fullName") { name.firstName + " " + name.lastName }
```

</tab>
<tab title="Strings">

```kotlin
// Row expression computes values for a new column
df.add("fullName") { "name"["firstName"] + " " + "name"["lastName"] }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/addWithExpression_properties.html" width="100%"/>

##### pivot {collapsible="true"}

<!---FUN pivotWithExpression-->
<tabs>
<tab title="Properties">

```kotlin
// Row expression computes cell content for values of pivoted column
df.pivot { city }.with { name.lastName.uppercase() }
```

</tab>
<tab title="Strings">

```kotlin
// Row expression computes cell content for values of pivoted column
df.pivot { city }.with { "name"["lastName"]<String>().uppercase() }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/pivotWithExpression_properties.html" width="100%"/>

### `RowValueExpression`
`RowValueExpression` computes a new value for every selected cell given the [`DataRow`](DataRow.md) of that cell 
and the current value of that cell. `this` refers to the current [`DataRow`](DataRow.md), 
and `it` refers to the current value of the cell.
`RowValueExpression` is used after selecting columns in functions such as [`update`](update.md) or [`convert`](convert.md).

`RowValueExpression` signature: ```DataRow.(C) -> T```.

#### `RowValueExpression` examples

##### update (expression) {collapsible="true"}

<!---FUN updateWithExpression-->
<tabs>
<tab title="Properties">

```kotlin
// "it" refers to the current "weight" cell, and "prev()" is called on the row "this"
df.update { weight }.at(2, 3, 5).with { it ?: prev()?.weight }
```

</tab>
<tab title="Strings">

```kotlin
// "it" refers to the current "weight" cell, and "prev()" is called on the row "this"
df.update("weight").at(2, 3, 5).with { it ?: prev()?.get("weight") }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/updateWithExpression_properties.html" width="100%"/>

##### convert {collapsible="true"}
<!---FUN convertExpression-->
<tabs>
<tab title="Properties">

```kotlin
// "it" refers to the current "city" cell
df.convert { city }.notNull { it.uppercase() }
```

</tab>
<tab title="Strings">

```kotlin
// "it" refers to the current "city" cell
df.convert("city").notNull { (it as String).uppercase() }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/convertExpression_properties.html" width="100%"/>

## Row conditions
Row condition is a special case of [row expression](#row-expressions) that returns `Boolean`.
There are two types of row conditions:

### `RowFilter`
`RowFilter` evaluates a [`DataRow`](DataRow.md) 
and returns a `Boolean` indicating whether the row should be included in the result.
Both `this` and `it` in `RowFilter` refer to the same [`DataRow`](DataRow.md).
`RowFilter` is used in functions such as [`filter`](filter.md), [`drop`](drop.md), 
[`first`](first.md), and [`count`](count.md). 

`RowFilter` signature: ```DataRow.(DataRow) -> Boolean```.

#### `RowFilter` examples

##### filter {collapsible="true"}

<!---FUN dfDataRow-->

```kotlin
df
```

<!---END-->
<inline-frame src="resources/filterWithConditionDf.html" width="100%"/>

<!---FUN filterWithCondition-->
<tabs>
<tab title="Properties">

```kotlin
// Row filter is used to filter rows
df.filter { name.firstName == "Alice" && age >= 18 }
```

</tab>
<tab title="Strings">

```kotlin
// Row filter is used to filter rows
df.filter { "name"["firstName"]<String>() == "Alice" && "age"<Int>() >= 18 }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/filterWithCondition_properties.html" width="100%"/>

##### drop {collapsible="true"}

<!---FUN dropWithCondition-->
<tabs>
<tab title="Properties">

```kotlin
// Row filter is used to drop rows where `city` or `weight` is null
df.drop { city == null || weight == null }
```

</tab>
<tab title="Strings">

```kotlin
// Row filter is used to drop rows where `city` or `weight` is null
df.drop { "city"<String?>() == null || "weight"<Int?>() == null }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/dropWithCondition_properties.html" width="100%"/>

##### first {collapsible="true"}

<!---FUN firstWithCondition-->
<tabs>
<tab title="Properties">

```kotlin
// Row filter is used to take the first row where `city` is Milan
df.first { city == "Milan" }
```

</tab>
<tab title="Strings">

```kotlin
// Row filter is used to take the first row where `city` is Milan
df.first { "city"<String?>() == "Milan" }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/firstWithCondition_properties.html" width="100%"/>

##### count {collapsible="true"}

<!---FUN countWithCondition-->
<tabs>
<tab title="Properties">

```kotlin
// Row filter is used to count happy people
df.count { isHappy } // the result is 5
```

</tab>
<tab title="Strings">

```kotlin
// Row filter is used to count happy people
df.count { "isHappy"() } // the result is 5
```

</tab></tabs>
<!---END-->

### `RowValueFilter`
`RowValueFilter` is used after selecting columns in functions
such as [`update`](update.md), [`gather`](gather.md), and [`format`](format.md).
Like `RowFilter`, it returns a `Boolean` indicating whether the row should be included in the result.
However, unlike `RowFilter`, where both `this` and `it` refer to the current [`DataRow`](DataRow.md), 
`RowValueFilter` uses the current row as `this` and can also access the selected column value from this row as `it`.

`RowValueFilter` signature: ```DataRow.(C) -> Boolean```.

#### `RowValueFilter` examples

##### update (condition) {collapsible="true"}

<!---FUN updateWithCondition-->
<tabs>
<tab title="Properties">

```kotlin
// Row value filter is used to filter rows for value update
df.update { age }.where { name.firstName == "Alice" && name.lastName == "Cooper" }.with { 16 }
```

</tab>
<tab title="Strings">

```kotlin
// Row value filter is used to filter rows for value update
df.update("age")
    .where {
        "name"["firstName"]<String>() == "Alice" &&
            "name"["lastName"]<String>() == "Cooper"
    }
    .with { 16 }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/updateWithCondition_properties.html" width="100%"/>

##### gather {collapsible="true"}

<!---FUN gatherWithCondition-->
<tabs>
<tab title="Properties">

```kotlin
// Row value filter is used to gather only unfilled profile fields
df.gather { age and city and weight and isHappy }
    .where { it == null }
    .into("field", "value")
```

</tab>
<tab title="Strings">

```kotlin
// Row value filter is used to gather only unfilled profile fields
df.gather("age", "city", "weight", "isHappy")
    .where { it == null }
    .into("field", "value")
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/gatherWithCondition_properties.html" width="100%"/>

##### format {collapsible="true"}

<!---FUN formatWithCondition-->
<tabs>
<tab title="Properties">

```kotlin
// Row value filter is used to format only rows with minors
df
    .format()
    .where { age < 18 }
    .with { background(RgbColor(242, 210, 189)) and textColor(black) }
```

</tab>
<tab title="Strings">

```kotlin
// Row value filter is used to format only rows with minors
df
    .format()
    .where { "age"<Int>() < 18 }
    .with { background(RgbColor(242, 210, 189)) and textColor(black) }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/formatWithCondition_properties.html" width="100%"/>

## Row statistics

<snippet id="rowStatistics">

The following [statistics](summaryStatistics.md) are available for `DataRow`:
* `rowSum`
* `rowMean`
* `rowStd`

These statistics will be applied only to values of appropriate types, and incompatible values will be ignored.
For example, if a [dataframe](DataFrame.md) has columns of types `String` and `Int`,
`rowSum()` will compute the sum of the `Int` values in the row and ignore `String` values.

To apply statistics only to values of a particular type, use `-Of` versions:
* `rowSumOf<T>`
* `rowMeanOf<T>`
* `rowStdOf<T>`
* `rowMinOf<T>`
* `rowMaxOf<T>`
* `rowMedianOf<T>`
* `rowPercentileOf<T>`

</snippet>
