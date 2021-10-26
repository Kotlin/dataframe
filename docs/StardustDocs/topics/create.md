[//]: # (title: Create)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

## Columns
Create [ValueColumn](DataColumn.md#valuecolumn) with values and name: 

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

### Column Accessors

Create [column accessors](DataColumn.md#column-accessors) and store it in the variable with the same name as column name:

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

You can also create column accessors to access [ColumnGroup](DataColumn.md#columngroup) or [FrameColumn](DataColumn.md#framecolumn)

<!---FUN createGroupOrFrameColumnAccessor-->

```kotlin
val columns by columnGroup()
val frames by frameColumn()
```

<!---END-->

And you can create deep column accessors for columns within [ColumnGroup](DataColumn.md#columngroup)

<!---FUN createDeepColumnAccessor-->

```kotlin
val name by columnGroup()
val firstName by name.column<String>()
```

<!---END-->

## DataFrame

There are several ways to convert a piece of data into `DataFrame`.

DataFrame with 2 columns and 3 rows:

<!---FUN createDataFrameOf-->

```kotlin
val df = dataFrameOf("name", "age")(
    "Alice", 15,
    "Bob", 20,
    "Mark", 100
)
```

<!---END-->

DataFrame with columns from 'a' to 'z' and values from 1 to 10 for each column:

<!---FUN createDataFrameWithFill-->

```kotlin
val df = dataFrameOf('a'..'z') { 1..10 }
```

<!---END-->

DataFrame with columns from 1 to 5 filled with 7 random double values:

<!---FUN createDataFrameWithRandom-->

```kotlin
val df = dataFrameOf(1..5).randomDouble(7)
```

<!---END-->

DataFrame with 3 columns, fill each column with 15 'true' values:

<!---FUN createDataFrameFillConstant-->

```kotlin
val names = listOf("first", "second", "third")
val df = dataFrameOf(names).fill(15, true)
```

<!---END-->

DataFrame from [columns](#columns)

<!---FUN createDataFrameFromColumns-->

```kotlin
val name by columnOf("Alice", "Bob")
val age by columnOf(15, 20)

val df1 = dataFrameOf(name, age)
val df2 = listOf(name, age).toDataFrame()
```

<!---END-->

DataFrame from `Map`:

<!---FUN createDataFrameFromMap-->

```kotlin
val map = mapOf("name" to listOf("Alice", "Bob"), "age" to listOf(15, 20))
val df = map.toDataFrame()
```

<!---END-->

### from objects

To create `DataFrame` from list of any objects use `createDataFrame`:

<!---FUN createDataFrameFromObject-->

```kotlin
data class Person(val name: String, val age: Int)
val persons = listOf(Person("Alice", 15), Person("Bob", 20))

val df = persons.createDataFrame()
```

<!---END-->

It reads object properties using reflection and creates [ValueColumns](DataColumn.md#valuecolumn) for every property. Scope of properties is currently limited by the observed at compile time type of elements 

To object graph traversal and convert nested objects into [ColumnGroups](DataColumn.md#columngroup) and [FrameColumns](DataColumn.md#framecolumn) you should specify `depth` parameter

<!---FUN createDataFrameFromDeepObject-->

```kotlin
data class Name(val firstName: String, val lastName: String)
data class Score(val subject: String, val value: Int)
data class Student(val name: Name, val age: Int, val scores: List<Score>)

val students = listOf(
    Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
    Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5)))
)

val df = students.createDataFrame(depth = 2)
```

<!---END-->

If you want to have more detailed control over object graph transformation you can use configuration DSL.
It will allow you to exclude particular properties or classes from object graph traversal, compute additional columns and configure column grouping.

<!---FUN createDataFrameFromDeepObjectWithExclude-->

```kotlin
val df = students.createDataFrame {
    // add value column
    "year of birth" from { 2021 - it.age }

    // scan properties
    properties(depth = 2) {
        exclude(Score::subject) // `subject` property will be skipped from object graph traversal
        preserve<Name>() // `Name` objects will be stored as-is without transformation into DataFrame
    }

    // add column group
    "summary" {
        "max score" from { it.scores.maxOf { it.value } }
        "min score" from { it.scores.minOf { it.value } }
    }
}
```

<!---END-->
