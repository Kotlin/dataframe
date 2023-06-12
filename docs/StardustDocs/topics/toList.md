[//]: # (title: toList)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Collections-->

Converts [`DataFrame`](DataFrame.md) into a [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/) 
of data class instances by current [`DataFrame`](DataFrame.md) type argument.

```
toList()
```

Type of data class is defined by current type argument of [`DataFrame`](DataFrame.md). If this type argument is not data class, exception will be thrown.

Data class properties are matched with [`DataFrame`](DataFrame.md) columns by name. If property type differs from column type [type conversion](convert.md) will be performed. If no automatic type conversion was found, exception will be thrown. 

To export [`DataFrame`](DataFrame.md) into specific type of data class, use `toListOf`:

## toListOf

Converts [`DataFrame`](DataFrame.md) into a [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/) of instances of given data class.

<!---FUN listInterop5-->

```kotlin
val df = dataFrameOf("name", "lastName", "age")("John", "Doe", 21)
    .group("name", "lastName").into("fullName")

data class FullName(val name: String, val lastName: String)
data class Person(val fullName: FullName, val age: Int)

val persons = df.toListOf<Person>() // [Person(fullName = FullName(name = "John", lastName = "Doe"), age = 21)]
```

<!---END-->
