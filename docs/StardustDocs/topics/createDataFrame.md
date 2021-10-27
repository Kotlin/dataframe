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
