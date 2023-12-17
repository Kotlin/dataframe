[//]: # (title: Create DataColumn)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

This section describes ways to create a [`DataColumn`](DataColumn.md).

### columnOf

Returns new column with the given elements.
The column [`type`](DataColumn.md#properties) is deduced from the compile-time type of the elements inside.
The column [`name`](DataColumn.md#properties) is taken from the name of the variable.

<!---FUN createValueByColumnOf-->

```kotlin
// Create ValueColumn with name 'student' and two elements of type String
val student by columnOf("Alice", "Bob")
```

<!---END-->

To assign column name explicitly, use the `named` infix function and replace `by` with `=`.

<!---FUN createColumnRenamed-->

```kotlin
val column = columnOf("Alice", "Bob") named "student"
```

<!---END-->

When column elements are columns themselves, it returns a [`ColumnGroup`](DataColumn.md#columngroup):

<!---FUN createColumnGroup-->

```kotlin
val firstName by columnOf("Alice", "Bob")
val lastName by columnOf("Cooper", "Marley")

// Create ColumnGroup with two nested columns
val fullName by columnOf(firstName, lastName)
```

<!---END-->

When column elements are [`DataFrames`](DataFrame.md) it returns a [`FrameColumn`](DataColumn.md#framecolumn):

<!---FUN createFrameColumn-->

```kotlin
val df1 = dataFrameOf("name", "age")("Alice", 20, "Bob", 25)
val df2 = dataFrameOf("name", "temp")("Charlie", 36.6)

// Create FrameColumn with two elements of type DataFrame
val frames by columnOf(df1, df2)
```

<!---END-->

### toColumn

Converts an `Iterable` of values into a column.

<!---FUN createValueByToColumn-->

```kotlin
listOf("Alice", "Bob").toColumn("name")
```

<!---END-->

To compute a column type at runtime by scanning through the actual values, enable the `Infer.Type` option. 

To inspect values only for nullability, enable the `Infer.Nulls` option.

<!---FUN createValueColumnInferred-->

```kotlin
val values: List<Any?> = listOf(1, 2.5)

values.toColumn("data") // type: Any?
values.toColumn("data", Infer.Type) // type: Number
values.toColumn("data", Infer.Nulls) // type: Any
```

<!---END-->

### toColumnOf

Converts an [`Iterable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/)
of values into a column of a given type:

<!---FUN createValueColumnOfType-->

```kotlin
val values: List<Any?> = listOf(1, 2.5)

values.toColumnOf<Number?>("data") // type: Number?
```

<!---END-->


