[//]: # (title: Convert objects to DataFrame)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

To create `DataFrame` from `Iterable` of any objects use `createDataFrame` extension:

<!---FUN createDataFrameFromObject-->

```kotlin
data class Person(val name: String, val age: Int)
val persons = listOf(Person("Alice", 15), Person("Bob", 20), Person("Mark", 22))

val df = persons.toDataFrame()
```

<!---END-->

It scans object properties using reflection and creates [ValueColumns](DataColumn.md#valuecolumn) for every property. Scope of properties is defined at compile-time by declared types of objects, so properties of implementation classes will not be scanned. 

Specify `depth` parameter to perform deep object graph traversal and convert nested objects into [ColumnGroups](DataColumn.md#columngroup) and [FrameColumns](DataColumn.md#framecolumn):

<!---FUN createDataFrameFromDeepObject-->

```kotlin
data class Name(val firstName: String, val lastName: String)
data class Score(val subject: String, val value: Int)
data class Student(val name: Name, val age: Int, val scores: List<Score>)

val students = listOf(
    Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
    Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5)))
)

val df = students.toDataFrame(depth = 2)
```

<!---END-->

For detailed control over object graph transformation use configuration DSL. It allows you to exclude particular properties or classes from object graph traversal, compute additional columns and configure column grouping.

<!---FUN createDataFrameFromDeepObjectWithExclude-->

```kotlin
val df = students.createDataFrame {
    // add value column
    "year of birth" from { 2021 - it.age }

    // scan all properties
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
