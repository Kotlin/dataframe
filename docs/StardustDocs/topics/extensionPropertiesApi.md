[//]: # (title: Extension properties API)

When `DataFrame` is used within Jupyter Notebooks or Datalore with Kotlin Kernel, after every cell execution all new global variables of type DataFrame are analyzed and replaced with typed DataFrame wrapper with auto-generated extension properties for data access:
```kotlin
val df = DataFrame.read("titanic.csv")
```
Now data can be accessed by . member accessor
```kotlin
df.filter { it.survived && it.home.endsWith("NY") && it.age in 10..20 }
//it can be omitted
df.filter { survived && home.endsWith("NY") && age in 10..20 }
```

Extension properties are generated for DataSchema that is extracted from `DataFrame` instance after REPL line execution. After that `DataFrame` variable is typed with its own `DataSchema`, so only valid extension properties corresponding to actual columns in DataFrame will be allowed by the compiler and suggested by completion.

Also, extension properties can be generated in IntelliJ IDEA based on `csv` or `json` files using `Kotlin Dataframe` Gradle plugin.

// TODO: Link to plugin here

<warning>
In notebooks generated properties won't appear and be updated until the cell has been executed. It often means that you have to introduce new variable frequently to sync extension properties with actual schema
</warning>
