[//]: # (title: Extension properties API)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels-->

When `DataFrame` is used within Jupyter Notebooks or Datalore with Kotlin Kernel, after every cell execution all new global variables of type DataFrame are analyzed and replaced with typed DataFrame wrapper with auto-generated extension properties for data access:

<!---FUN extensionProperties1-->

```kotlin
val df = DataFrame.read("titanic.csv")
```

<!---END-->

Now data can be accessed by `.` member accessor

<!---FUN extensionProperties2-->

```kotlin
df.add("lastName") { name.split(",").last() }
    .dropNulls { age }
    .filter { survived && home.endsWith("NY") && age in 10..20 }
```

<!---END-->

Extension properties are generated for DataSchema that is extracted from `DataFrame` instance after REPL line execution. After that `DataFrame` variable is typed with its own `DataSchema`, so only valid extension properties corresponding to actual columns in DataFrame will be allowed by the compiler and suggested by completion.

Also, extension properties [can be generated in IntelliJ IDEA](gradle.md) using [Kotlin Dataframe Gradle plugin](installation.md#data-schema-preprocessor).

<warning>
In notebooks generated properties won't appear and be updated until the cell has been executed. It often means that you have to introduce new variable frequently to sync extension properties with actual schema
</warning>
