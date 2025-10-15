[//]: # (title: Create DataFrame)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

This section describes ways to create a [`DataFrame`](DataFrame.md) instance.

### emptyDataFrame

Returns a [`DataFrame`](DataFrame.md) with no rows and no columns.

<!---FUN createEmptyDataFrame-->

```kotlin
val df = emptyDataFrame<Any>()
```

<!---END-->

### dataFrameOf

<!---FUN createDataFrameOfPairs-->

```kotlin
// DataFrame with 2 columns and 3 rows
val df = dataFrameOf(
    "name" to listOf("Alice", "Bob", "Charlie"),
    "age" to listOf(15, 20, 100),
)
```

<!---END-->

Create DataFrame with nested columns inplace:

<!---FUN createNestedDataFrameInplace-->

```kotlin
// DataFrame with 2 columns and 3 rows
val df = dataFrameOf(
    "name" to columnOf(
        "firstName" to columnOf("Alice", "Bob", "Charlie"),
        "lastName" to columnOf("Cooper", "Dylan", "Daniels"),
    ),
    "age" to columnOf(15, 20, 100),
)
```

<!---END-->

<!---FUN createDataFrameFromColumns-->

```kotlin
// DataFrame with 2 columns
val df = dataFrameOf(
    "name" to columnOf("Alice", "Bob", "Charlie"),
    "age" to columnOf(15, 20, 22)
)
```

<!---END-->

Returns a [`DataFrame`](DataFrame.md) with given column names and values.

<!---FUN createDataFrameOf-->

```kotlin
// DataFrame with 2 columns and 3 rows
val df = dataFrameOf("name", "age")(
    "Alice", 15,
    "Bob", 20,
    "Charlie", 100,
)
```

<!---END-->

<!---FUN createDataFrameWithFill-->

```kotlin
// Multiplication table
dataFrameOf(1..10) { x -> (1..10).map { x * it } }
```

<!---END-->

<!---FUN createDataFrameWithRandom-->

```kotlin
// 5 columns filled with 7 random double values:
val names = (1..5).map { "column$it" }
dataFrameOf(names).randomDouble(7)

// 5 columns filled with 7 random double values between 0 and 1 (inclusive)
dataFrameOf(names).randomDouble(7, 0.0..1.0).print()

// 5 columns filled with 7 random int values between 0 and 100 (inclusive)
dataFrameOf(names).randomInt(7, 0..100).print()
```

<!---END-->

<!---FUN createDataFrameFillConstant-->

```kotlin
val names = listOf("first", "second", "third")

// DataFrame with 3 columns, fill each column with 15 `true` values
val df = dataFrameOf(names).fill(15, true)
```

<!---END-->

### toDataFrame

#### `DataFrame` from `Map<String, List<*>>`:

<!---FUN createDataFrameFromMap-->

```kotlin
val map = mapOf("name" to listOf("Alice", "Bob", "Charlie"), "age" to listOf(15, 20, 22))

// DataFrame with 2 columns
map.toDataFrame()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Create.createDataFrameFromMap.html" width="100%"/>
<!---END-->

#### `DataFrame` from [`Iterable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/) of [basic types](https://kotlinlang.org/docs/basic-types.html) (except arrays):

The return type of these overloads is a typed [`DataFrame`](DataFrame.md).
Its data schema defines the column that can be used right after the conversion for additional computations.

<!---FUN readDataFrameFromValues-->

```kotlin
val names = listOf("Alice", "Bob", "Charlie")
// TODO fix with plugin???
val df = names.toDataFrame() as DataFrame<ValueProperty<String>>
df.add("length") { value.length }
```

<!---END-->

#### [`DataFrame`](DataFrame.md) with one column from [`Iterable<T>`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/) 

This is an easy way to create a [`DataFrame`](DataFrame.md) when you have a list of Files, URLs, or a structure
you want to extract data from.

In a notebook,
it can be convenient to start from the column of these values to see the number of rows, their `toString` in a table
and then iteratively add columns with the parts of the data you're interested in.
It could be a File's content, a specific section of an HTML document, some metadata, etc.

<!---FUN toDataFrameColumn-->

```kotlin
val files = listOf(File("data.csv"), File("data1.csv"))
val df = files.toDataFrame(columnName = "data")
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Create.toDataFrameColumn.html" width="100%"/>
<!---END-->

#### [`DataFrame`](DataFrame.md) from `List<List<T>>`:

This is useful for parsing text files. For example, the `.srt` subtitle format can be parsed like this:

<!---FUN toDataFrameLists-->

```kotlin
val lines = """
    1
    00:00:05,000 --> 00:00:07,500
    This is the first subtitle.

    2
    00:00:08,000 --> 00:00:10,250
    This is the second subtitle.
""".trimIndent().lines()

lines.chunked(4) { it.take(3) }.toDataFrame(header = listOf("n", "timestamp", "text"))
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Create.toDataFrameLists.html" width="100%"/>
<!---END-->

#### [`DataFrame`](DataFrame.md) from [`Iterable<T>`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/):

<!---FUN readDataFrameFromObject-->

```kotlin
data class Person(val name: String, val age: Int)

val persons = listOf(Person("Alice", 15), Person("Bob", 20), Person("Charlie", 22))

val df = persons.toDataFrame()
```

<!---END-->

Scans object properties using reflection and creates a [ValueColumn](DataColumn.md#valuecolumn) for every property. 
The scope of properties for scanning is defined at compile-time by the formal types of the objects in the [`Iterable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/),
so the properties of implementation classes will not be scanned.

Specify the `depth` parameter to perform deep object graph traversal
and convert nested objects into [ColumnGroups](DataColumn.md#columngroup) and [FrameColumns](DataColumn.md#framecolumn):

<!---FUN readDataFrameFromDeepObject-->

```kotlin
data class Name(val firstName: String, val lastName: String)

data class Score(val subject: String, val value: Int)

data class Student(val name: Name, val age: Int, val scores: List<Score>)

val students = listOf(
    Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
    Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
)

val df = students.toDataFrame(maxDepth = 1)
```

<!---END-->

For detailed control over object graph transformations, use the configuration DSL.
It allows you to exclude particular properties or classes from the object graph traversal,
compute additional columns, and configure column grouping.

<!---FUN readDataFrameFromDeepObjectWithExclude-->

```kotlin
val df = students.toDataFrame {
    // add column
    "year of birth" from { 2021 - it.age }

    // scan all properties
    properties(maxDepth = 1) {
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

### DynamicDataFrameBuilder

Previously mentioned [`DataFrame`](DataFrame.md) constructors throw an exception when column names are duplicated. 
When implementing a custom operation involving multiple [`DataFrame`](DataFrame.md) objects,
or computed columns or when parsing some third-party data,
it might be desirable to disambiguate column names instead of throwing an exception. 

<!---FUN duplicatedColumns-->

```kotlin
fun peek(vararg dataframes: AnyFrame): AnyFrame {
    val builder = DynamicDataFrameBuilder()
    for (df in dataframes) {
        df.columns().firstOrNull()?.let { builder.add(it) }
    }
    return builder.toDataFrame()
}

val col by columnOf(1, 2, 3)
peek(dataFrameOf(col), dataFrameOf(col))
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Create.duplicatedColumns.html" width="100%"/>
<!---END-->

