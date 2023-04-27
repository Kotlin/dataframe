[//]: # (title: Extension properties API)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels-->

When [`DataFrame`](DataFrame.md) is used within Jupyter Notebooks or Datalore with Kotlin Kernel, 
after every cell execution all new global variables of type DataFrame are analyzed and replaced 
with typed [`DataFrame`](DataFrame.md) wrapper with auto-generated extension properties for data access:

<!---FUN extensionProperties1-->

```kotlin
val df = DataFrame.read("titanic.csv")
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.extensionProperties1.html"/>
<!---END-->

Now data can be accessed by `.` member accessor

<!---FUN extensionProperties2-->

```kotlin
df.add("lastName") { name.split(",").last() }
    .dropNulls { age }
    .filter { survived && home.endsWith("NY") && age in 10..20 }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.extensionProperties2.html"/>
<!---END-->

In notebooks, extension properties are generated for [`DataSchema`](schemas.md) that is extracted from [`DataFrame`](DataFrame.md) 
instance after REPL line execution. 
After that [`DataFrame`](DataFrame.md)  variable is typed with its own [`DataSchema`](schemas.md), so only valid extension properties corresponding to actual columns in DataFrame will be allowed by the compiler and suggested by completion.

Extension properties can be generated in IntelliJ IDEA using the [Kotlin Dataframe Gradle plugin](gradle.md#configuration).

<warning>
In notebooks generated properties won't appear and be updated until the cell has been executed. It often means that you have to introduce new variable frequently to sync extension properties with actual schema
</warning>
