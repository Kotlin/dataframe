[//]: # (title: Data Schemas in Gradle projects)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

In Gradle project `DataFrame` uses annotation processing for [extension properties generation](extensionPropertiesApi.md) and gradle tasks to infer `DataSchema` from data sets.  

To use [extension properties API](extensionPropertiesApi.md) in Gradle project you should [configure DataFrame plugin](installation.md#gradle-plugin-configuration).

### Annotation processing
Declare data schemas in your code and use them to access data in DataFrames.
A data schema is an interface with properties and no type parameters annotated with `@DataSchema`:
```kotlin
package org.example

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

@DataSchema
interface Person {
    val name: String
    val age: Int
}
```

#### Execute kspKotlin task to generate type-safe accessors for schemas: 

<!---FUN useProperties-->

```kotlin
val df = dataFrameOf("name", "age")(
    "Alice", 15,
    "Bob", 20
).cast<Person>()
// age only available after executing `build` or `kspKotlin`!
val teens = df.filter { age in 10..19 }
teens.print()
```

<!---END-->

### Schema inference
Specify schema's configurations in `dataframes`  and execute the `build` task.
For the following configuration, file `Repository.Generated.kt` will be generated.
See [reference](gradleReference.md) and [examples](gradleReference.md#examples) for more details.

#### build.gradle
```kotlin
dataframes {
    schema {
        data = "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"
        name = "org.example.Repository"
    }
}
```

After `build`, the following code should compile and run:

<!---FUN useInferredSchema-->

```kotlin
val REPOSITORIES_DATA = "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"
val df = DataFrame.read(REPOSITORIES_DATA).cast<Repository>()
// Use generated properties to access data in rows
df.maxBy { stargazers_count }.print()
// Or to access columns in dataframe.
print(df.full_name.count { it.contains("kotlin") })
```

<!---END-->

