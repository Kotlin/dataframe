[//]: # (title: String API)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels-->

String column names are the easiest way to access data in DataFrame:

<!---FUN strings-->

```kotlin
DataFrame.read("titanic.csv")
    .add("lastName") { "name"<String>().split(",").last() }
    .dropNulls("age")
    .filter { "survived"<Boolean>() && "home"<String>().endsWith("NY") && "age"<Int>() in 10..20 }
```

<!---END-->

<warning>
Note that if data frame doesn’t contain column with the string provided, or you try to cast to the wrong type it will lead to runtime exception.
</warning>
