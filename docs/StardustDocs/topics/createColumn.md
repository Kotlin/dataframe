[//]: # (title: Create DataColumn)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

Create [ValueColumn](DataColumn.md#valuecolumn) with `columnOf`:
* column name is defined by variable name
* column type is detected in compile time using [reified type parameters](https://kotlinlang.org/docs/inline-functions.html#reified-type-parameters)

<!---FUN createValueByColumnOf-->

```kotlin
val name by columnOf("Alice", "Bob")
// or
listOf("Alice", "Bob").toColumn("name")
```

<!---END-->

You can assign column name explicitly using `named` infix function:

<!---FUN createColumnRenamed-->

```kotlin
val column = columnOf("Alice", "Bob") named "name"
```

<!---END-->

Convert `Iterable` of values into column:

<!---FUN createValueByToColumn-->

To compute column type at runtime by scanning through actual values, set `inferType` flag. To inspect values only for nullability set `inferNulls` flag:

<!---FUN createValueColumnInferred-->

```kotlin
val values = listOf("Alice", null, 1, 2.5).subList(2, 4)

values.toColumn("data").type willBe typeOf<Any?>()
values.toColumn("data", inferType = true).type willBe typeOf<Number>()
values.toColumn("data", inferNulls = true).type willBe typeOf<Any>()
values.toColumn("data", inferType = true, inferNulls = false).type willBe typeOf<Number?>()
values.toColumnOf<Number?>("data").type willBe typeOf<Number?>()
```

<!---END-->

Create [ColumnGroup](DataColumn.md#columngroup) with several [columns](DataColumn.md):

<!---FUN createColumnGroup-->

```kotlin
val firstName by columnOf("Alice", "Bob")
val lastName by columnOf("Cooper", "Marley")

val name by columnOf(firstName, lastName)
// or
listOf(firstName, lastName).toColumn("name")
```

<!---END-->

Create [FrameColumn](DataColumn.md#framecolumn) with several DataFrames:

<!---FUN createFrameColumn-->

```kotlin
val df1 = dataFrameOf("name", "age")("Alice", 20, "Bob", 25)
val df2 = dataFrameOf("name", "temp")("Mark", 36.6)

val groups by columnOf(df1, df2)
// or
listOf(df1, df2).toColumn("groups")
```

<!---END-->


