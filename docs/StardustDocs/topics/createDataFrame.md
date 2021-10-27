[//]: # (title: Create DataFrame)

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

DataFrame with 3 columns, fill each column with 15 `true` values:

<!---FUN createDataFrameFillConstant-->

```kotlin
val names = listOf("first", "second", "third")
val df = dataFrameOf(names).fill(15, true)
```

<!---END-->

DataFrame from [DataColumns](DataColumn.md)

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

To create `DataFrame` from list of any objects use `createDataFrame` extension available for any `Iterable`:

<!---FUN createDataFrameFromObject-->

```kotlin
data class Person(val name: String, val age: Int)
val persons = listOf(Person("Alice", 15), Person("Bob", 20))

val df = persons.createDataFrame()
```

<!---END-->

It reads object properties using reflection and creates [ValueColumns](DataColumn.md#valuecolumn) for every property. Scope of properties is currently limited by the observed at compile time type of elements

specify `depth` parameter to perform object graph traversal and convert nested objects into [ColumnGroups](DataColumn.md#columngroup) and [FrameColumns](DataColumn.md#framecolumn): 

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

For detailed control over object graph transformation use configuration DSL. It allows you to exclude particular properties or classes from object graph traversal, compute additional columns and configure column grouping.

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
