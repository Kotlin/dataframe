[//]: # (title: KProperties API)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels-->

`Kotlin Dataframe` can be used as an intermediate structure for data transformation between two data formats. If either source or destination is a Kotlin object, e.g. data class, it is convenient to use its properties for typed data access in `DataFrame`.
This can be done using `::` expression that provides [property references](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/)

<!---FUN kproperties1-->

```kotlin
data class Passenger(
    val survived: Boolean,
    val home: String,
    val age: Int,
    val lastName: String
)

val passengers = DataFrame.read("titanic.csv", delimiter = ';')
    .add(Passenger::lastName) { "name"<String>().split(",").last() }
    .dropNulls(Passenger::age)
    .filter {
        it[Passenger::survived] &&
            it[Passenger::home].endsWith("NY") &&
            it[Passenger::age] in 10..20
    }
    .toListOf<Passenger>()
```

<!---END-->

By default, `DataFrame` uses `name` and `returnType` of `KProperty` for typed access to data. When column name differs from property name, use `ColumnName` annotation:  

<!---FUN kproperties2-->

```kotlin
data class Passenger(
    @ColumnName("survived") val isAlive: Boolean,
    @ColumnName("home") val city: String,
    val name: String
)

val passengers = DataFrame.read("titanic.csv", delimiter = ';')
    .filter { it.get(Passenger::city).endsWith("NY") }
    .toListOf<Passenger>()
```

<!---END-->
