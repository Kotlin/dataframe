[//]: # (title: Extension Properties API)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels-->

Auto-generated extension properties are the safest and easiest way to access columns in a [`DataFrame`](DataFrame.md).
They are generated based on a [dataframe schema](schemas.md), 
with the name and type of properties inferred from the name and type of the corresponding columns.

Having these, it allows you to work with your dataframe like:
```kotlin
val peopleDf /* : DataFrame<Person> */ = DataFrame.read("people.csv").cast<Person>()
val nameColumn /* : DataColumn<String> */ = peopleDf.name
val ageColumn /* : DataColumn<Int> */ = peopleDf.personData.age
```
and of course
```kotlin
peopleDf.add("lastName") { name.split(",").last() }
    .dropNulls { personData.age }
    .filter { survived && home.endsWith("NY") && personData.age in 10..20 }
```

To find out how to use this API in your environment, check out [Working with Data Schemas](schemas.md)
or jump straight to [Data Schemas in Gradle projects](schemasGradle.md), 
or [Data Schemas in Jupyter notebooks](schemasJupyter.md).
