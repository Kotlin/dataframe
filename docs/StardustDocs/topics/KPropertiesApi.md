[//]: # (title: KProperties API)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

`Kotlin Dataframe` can be used as an intermediate structure for data transformation between two data formats. If either source or destination is a Kotlin object, e.g. data class, it is convenient to use its properties for typed data access in `DataFrame`.
This can be done using `::` expression that provides [property references](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/)

<!---FUN kpropertiesApi-->

```kotlin
data class Passenger(val survived: Boolean, val home: String, val age: Int, val lastName: String)

val passengers = DataFrame.read("titanic.csv")
    .add(Passenger::lastName) { "name"<String>().split(",").last() }
    .dropNulls(Passenger::age)
    .filter { it[Passenger::home].endsWith("NY") }
    .toListOf<Passenger>()
```

<!---END-->

By default, `DataFrame` uses `name` and `returnType` of `KProperty` for typed access to data. When column name differs from property name, use `ColumnName` annotation:  

<!---FUN kpropertyWithColumnNames-->

```kotlin
data class Passenger(
@ColumnName("survived") val isAlive: Boolean,
@ColumnName("home") val city: String,
val name: String
)

        val passengers = DataFrame.read("titanic.csv")
            .filter { it[Passenger::city].endsWith("NY") }
            .toListOf<Passenger>()
```

<!---END-->
