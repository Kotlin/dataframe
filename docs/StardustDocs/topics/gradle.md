[//]: # (title: Data Schemas in Gradle projects)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

In Gradle project `Kotlin DataFrame` provides
1. Annotation processing for generation of extension properties
2. Annotation processing for `DataSchema` inference from datasets.  
3. Gradle task for `DataSchema` inference from datasets.

To use [extension properties API](extensionPropertiesApi.md) in Gradle project you should [configure Kotlin DataFrame plugin](installation.md#gradle-plugin-configuration).

### Annotation processing
Declare data schemas in your code and use them to access data in DataFrames.
A data schema is a class or interface annotated with `@DataSchema`:
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
Specify schema with preferred method and execute the `build` task.
For the following configuration, file `Repository.Generated.kt` will be generated to `build/generated/dataframe/org/example` folder
See [reference](gradleReference.md) and [examples](gradleReference.md#examples) for more details.

<tabs>
<tab title="Method 1. Annotation processing">

ImportDataSchema annotation must be above package directive. 
You can put this annotation in the same file as data processing code
```kotlin
@file:ImportDataSchema(
    "Repository",
    "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv",
)

package org.example

import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
```

</tab>

<tab title="Method 2. Gradle task">

Put this in `build.gradle` or `build.gradle.kts`
```kotlin
dataframes {
    schema {
        data = "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"
        name = "org.example.Repository"
    }
}
```

</tab>
</tabs>

After `build`, the following code should compile and run:

<!---FUN useInferredSchema-->

```kotlin
// Repository.readCSV() has argument 'path' with default value https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv
val df = Repository.readCSV()
// Use generated properties to access data in rows
df.maxBy { stargazers_count }.print()
// Or to access columns in dataframe.
print(df.full_name.count { it.contains("kotlin") })
```

<!---END-->

