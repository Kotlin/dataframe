[//]: # (title: Create DataColumn)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

This section describes ways to create a [`DataColumn`](DataColumn.md).

**API reference**: [`columnOf`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/column-of.html), [`column`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/column.html), [`toColumn`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/to-column.html)

### columnOf

Returns a new column with the given elements.
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

When column elements are [`DataFrame`](DataFrame.md) objects, it returns a [`FrameColumn`](DataColumn.md#framecolumn):

<!---FUN createFrameColumn-->

```kotlin
val df1 = dataFrameOf("name", "age")("Alice", 20, "Bob", 25)
val df2 = dataFrameOf("name", "temp")("Charlie", 36.6)

// Create FrameColumn with two elements of type DataFrame
val frames by columnOf(df1, df2)
```

<!---END-->

When the elements mix different numeric types, pass the column type explicitly —
without an explicit type argument such a call does not compile:

<!---FUN createColumnOfMixedValues-->

```kotlin
// Mixed values need an explicit type argument
val weights = columnOf<Number>(1, 2.5)
```

<!---END-->

Lists are stored as cell values, so a column of lists is a [`ValueColumn`](DataColumn.md#valuecolumn),
not a [`ColumnGroup`](DataColumn.md#columngroup):

<!---FUN createColumnOfLists-->

```kotlin
// Lists are stored as cell values: a ValueColumn of List<Int>, not a ColumnGroup
val numbers by columnOf(listOf(1, 2), listOf(3, 4))
```

<!---END-->

### column

Returns a `ColumnAccessor` — a reference to a column that doesn't have to exist in a
[`DataFrame`](DataFrame.md) yet. Use the reference later to select, add or update the column.

<!---FUN createColumnAccessor-->

```kotlin
val df = dataFrameOf("age", "city")(20, "London", 15, "Milan")

// Reference to the column named "age"
val age = column<Int>("age")
// The name is taken from the property
val city by column<String>()

val selected = df.select { age and city }
```

<!---END-->

`valueColumn`, `columnGroup` and `frameColumn` create the same kind of reference for a value column,
a column group and a frame column.

To reference a column nested inside a group, create the reference on the group reference —
the property name is appended to the group's path:

<!---FUN createNestedColumnAccessor-->

```kotlin
val personal = columnGroup("personal")
// Reference to the nested column "personal"/"age"
val age by personal.column<Int>()
```

<!---END-->

`column` with an expression creates a reference to a column computed from a row:

<!---FUN createComputedColumnReference-->
<tabs>
<tab title="Properties">

```kotlin
val isAdult = column(df, "isAdult") { age > 18 }
val withColumn = df.add { add(isAdult) }
```

</tab>
<tab title="Strings">

```kotlin
val isAdult = column<Boolean>("isAdult") { "age"<Int>() > 18 }
val withColumn = dataFrameOf("age")(20, 15).add { add(isAdult) }
```

</tab></tabs>
<!---END-->

For the full story on column references and paths,
see [`Column Selectors`](ColumnSelectors.md#column-resolvers).

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


