[//]: # (title: Create)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

## Columns
Create [ValueColumn](columns.md#valuecolumn) with values and name: 

<!---FUN createValueColumn-->

```kotlin
val name by columnOf("Alice", "Bob")
// or
listOf("Alice", "Bob").toColumn("name")
```

<!---END-->

By default, column type is determined in compile time using [reified type parameters](https://kotlinlang.org/docs/inline-functions.html#reified-type-parameters)
If you want column type to be computed dynamically based on actual values, set `inferType` flag. To check values only for nullability set 'inferNulls' flag:

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

You can assign column name explicitly, if you don't want to infer it from variable name:

<!---FUN createColumnRenamed-->

```kotlin
val column = columnOf("Alice", "Bob") named "name"
```

<!---END-->

Create [ColumnGroup](columns.md#columngroup) with several [columns](columns.md):

<!---FUN createColumnGroup-->

```kotlin
val firstName by columnOf("Alice", "Bob")
val lastName by columnOf("Cooper", "Marley")

val name by columnOf(firstName, lastName)
// or
listOf(firstName, lastName).toColumn("name")
```

<!---END-->

Create [FrameColumn](columns.md#framecolumn) with several DataFrames:

<!---FUN createFrameColumn-->

```kotlin
val df1 = dataFrameOf("name", "age")("Alice", 20, "Bob", 25)
val df2 = dataFrameOf("name", "age")("Mark", 30)

val groups by columnOf(df1, df2)
// or
listOf(df1, df2).toColumn("groups")
```

<!---END-->

### Column Accessors

Create [column accessors](columns.md#column-accessors) and store it in the variable with the same name as column name:

<!---FUN createColumnAccessor-->

```kotlin
val name by column<String>()
```

<!---END-->

To explicitly specify column name pass it as an argument:

<!---FUN createColumnAccessorRenamed-->

```kotlin
val accessor = column<String>("complex column name")
```

<!---END-->

You can also create column accessors to access [ColumnGroup](columns.md#columngroup) or [FrameColumn](columns.md#framecolumn)

<!---FUN createGroupOrFrameColumnAccessor-->

```kotlin
val columns by columnGroup()
val frames by frameColumn()
```

<!---END-->

And you can create deep column accessors for columns within [ColumnGroup](columns.md#columngroup)

<!---FUN createDeepColumnAccessor-->

```kotlin
val name by columnGroup()
val firstName by name.column<String>()
```

<!---END-->

## DataFrame

There are several ways to convert a piece of data into `DataFrame`.

DataFrame with 2 columns and 3 rows:
```kotlin
val df = dataFrameOf("name", "age")(
   "Alice", 15,
   "Bob", 20,
   "Mark", 100
)
```
DataFrame with columns from 'a' to 'z' and values from 1 to 10 for each column:
```kotlin
val df = dataFrameOf('a'..'z') { 1..10 }
```
DataFrame with columns from 1 to 5 filled with 7 random double values:
```kotlin
val df = dataFrameOf(1..5).randomDouble(7)
```
DataFrame with 3 columns, fill each column with 15 'true' values:
```kotlin
val names = listOf("first", "second", "third")
val df = dataFrameOf(names).fill(15, true)
```

### from columns
`DataFrame` can be created from one or several [columns](#columns)

```kotlin
val name by columnOf("Alice", "Bob")
val age by columnOf(15, 20)

val df1 = dataFrameOf(name, age)
val df2 = listOf(name, age).toDataFrame()
val df3 = name + age
```
### from map
`Map<String, Iterable<Any?>>` can be converted to `DataFrame`:
```kotlin
val data = mapOf("name" to listOf("Alice", "Bob"), "age" to listOf(15, 20))
val df = data.toDataFrame()
```
### from objects

DataFrame can be created from a list of any objects.
Assume we have a list of `Person` objects:
```kotlin
data class Person(val name: String, val age: Int)
val persons = listOf(Person("Alice", 15), Person("Bob", 20))
```
This list can be converted to `DataFrame` with columns for every public property of `Person` class:
```kotlin
persons.toDataFrameByProperties()
```

name | age
---|---
Alice | 15
Bob | 20

You can also specify custom expressions for every column:
```kotlin
val df = persons.toDataFrame {
   "name" { name }
   "year of birth" { 2021 - age }
}
```

name | year of birth
---|---
Alice | 2006
Bob | 2001
