[//]: # (title: append)

Adds one or several rows to [`DataFrame`](DataFrame.md)
```kotlin
df.append(
    "Mike", 15,
    "John", 17, 
    "Bill", 30,
)
```

If the [compiler plugin](Compiler-Plugin.md) is enabled, a typesafe overload of `append` is available for `@DataSchema` classes.

```kotlin
@DataSchema
data class Person(val name: String, val age: Int)
```

```kotlin
val df = dataFrameOf(
    Person("Mike", 15),
    Person("John", 17),
)

df.append(Person("Bill", 30))
```

**Related operations**: [](appendDuplicate.md)
