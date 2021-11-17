[//]: # (title: Create DataColumn)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

This section describes ways to create [`DataColumn`](DataColumn.md).

### columnOf

Returns new column with given elements. Column [`type`](DataColumn.md#properties) is deduced from compile-time type of elements, column [`name`](DataColumn.md#properties) is taken from the name of the variable.

<!---FUN createValueByColumnOf-->

```kotlin
// Create ValueColumn with name 'student' and two elements of type String
val student by columnOf("Alice", "Bob")
```

<!---END-->

To assign column name explicitly, use `named` infix function and replace `by` with `=`.

<!---FUN createColumnRenamed-->

```kotlin
val column = columnOf("Alice", "Bob") named "student"
```

<!---END-->

When column elements are columns themselves, it returns [`ColumnGroup`](DataColumn.md#columngroup):

<!---FUN createColumnGroup-->

```kotlin
val firstName by columnOf("Alice", "Bob")
val lastName by columnOf("Cooper", "Marley")

// Create ColumnGroup with two nested columns
val fullName by columnOf(firstName, lastName)
```

<!---END-->

When column elements are [`DataFrames`](DataColumn.md) it returns [`FrameColumn`](DataColumn.md#framecolumn):

<!---FUN createFrameColumn-->

```kotlin
val df1 = dataFrameOf("name", "age")("Alice", 20, "Bob", 25)
val df2 = dataFrameOf("name", "temp")("Mark", 36.6)

// Create FrameColumn with two elements of type DataFrame
val frames by columnOf(df1, df2)
```

<!---END-->

### toColumn

Converts `Iterable` of values into column.

<!---FUN createValueByToColumn-->

```kotlin
listOf("Alice", "Bob").toColumn("name")
```

<!---END-->

To compute column type at runtime by scanning through actual values, set `inferType` flag. 

To inspect values only for nullability set `inferNulls` flag.

Default value for both flags is `false`.

<!---FUN createValueColumnInferred-->

```kotlin
val values: List<Any?> = listOf(1, 2.5)

values.toColumn("data") // type: Any?
values.toColumn("data", inferType = true) // type: Number
values.toColumn("data", inferNulls = true) // type: Any
values.toColumn("data", inferType = true, inferNulls = false) // type: Number?
values.toColumnOf<Number?>("data") // type: Number?
```

<!---END-->

### toColumnOf

Converts `Iterable` of values into column of specific type

<!---FUN createValueColumnOfType-->

```kotlin
val values: List<Any?> = listOf(1, 2.5)

values.toColumnOf<Number?>("data") // type: Number?
```

<!---END-->


