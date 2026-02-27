[//]: # (title: require)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Require-->

Throws an exception if the specified column is missing or its type is not subtype of `C`.
From the compiler plugin perspective, a new column will appear in the compile-time schema as a result of this operation.
The aim here is to help incrementally migrate workflows to extension properties API.

However, this operation is new and will work in compiler plugin starting from IntelliJ IDEA 2026.2 and Kotlin 2.4.0

```text
require { column }
```

**Related operations**: [](cast.md), [](convertTo)

```kotlin
val df = peopleDf.require { "name"["firstName"]<String>() }
val v: String = df.name.firstName[0]
```
