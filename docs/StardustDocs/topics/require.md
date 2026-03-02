[//]: # (title: require)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Require-->

Throws an exception if the specified column is missing or its type is not subtype of `C`.
From the compiler plugin perspective, a new column will appear in the compile-time schema as a result of this operation.
The aim here is to help incrementally migrate workflows to [extension properties API](extensionPropertiesApi.md).

Will work in compiler plugin starting from IntelliJ IDEA 2026.2 and Kotlin 2.4.0.

```text
require { column }
```

**Related operations**: [](cast.md), [](convertTo)

```kotlin
// Before `require` extension property will not be resolved
// peopleDf.select { name.firstName }

// Require a column with a runtime check
val df = peopleDf.require { "name"["firstName"]<String>() }
// Use extension property after `require`
val v: String = df.name.firstName[0]
```
