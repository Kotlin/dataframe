[//]: # (title: Data Schemas in Gradle projects)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

> The current Gradle plugin is **under consideration for deprecation** and may be officially marked as deprecated in future releases.
>
> At the moment, **[data schema generation is handled via dedicated methods](DataSchemaGenerationMethods.md)** instead of relying on the plugin.
{style="warning"}

In Gradle projects, the Kotlin DataFrame library provides

1. Annotation processing for generation of extension properties
2. Annotation processing for [`DataSchema`](schemas.md) inference from datasets.
3. Gradle task for [`DataSchema`](schemas.md) inference from datasets.

### Configuration

To use the [extension properties API](extensionPropertiesApi.md) in Gradle project add the `dataframe` plugin as follows:

<tabs>
<tab title="Kotlin DSL">

```kotlin
plugins {
    id("org.jetbrains.kotlinx.dataframe") version "%dataFrameVersion%"
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
plugins {
    id("org.jetbrains.kotlinx.dataframe") version "%dataFrameVersion%"
}

dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe:%dataFrameVersion%'
}
```

</tab>

</tabs>

### Annotation processing

Declare data schemas in your code and use them to access data in [`DataFrame`](DataFrame.md) objects.
A data schema is a class or interface annotated with [`@DataSchema`](schemas.md):

```kotlin
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

@DataSchema
interface Person {
    val name: String
    val age: Int
}
```

#### Execute the `assemble` task to generate type-safe accessors for schemas:

<!---FUN useProperties-->

```kotlin
val df = dataFrameOf("name", "age")(
    "Alice", 15,
    "Bob", 20,
).cast<Person>()
// age only available after executing `build` or `kspKotlin`!
val teens = df.filter { age in 10..19 }
teens.print()
```

<!---END-->

### Schema inference

Specify schema with preferred method and execute the `assemble` task.

<tabs>
<tab title="Method 1. Annotation processing">

`@ImportDataSchema` annotation must be above package directive.
You can import schemas from a URL or from the relative path of a file.
Relative path by default is resolved to the project root directory.
You can configure it by [passing](https://kotlinlang.org/docs/ksp-quickstart.html#pass-options-to-processors) `dataframe.resolutionDir`
option to preprocessor.
For example:

```kotlin
ksp {
    arg("dataframe.resolutionDir", file("data").absolutePath)
}
```

**Note that due to incremental processing, imported schema will be re-generated only if some source code has changed
from the previous invocation, at least one character.**

For the following configuration, file `Repository.Generated.kt` will be generated to `build/generated/ksp/` folder in
the same package as file containing the annotation.

```kotlin
@file:ImportDataSchema(
    "Repository",
    "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv",
)

import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
import org.jetbrains.kotlinx.dataframe.api.*
```

See KDocs for `@ImportDataSchema` in IDE
or [GitHub](https://github.com/Kotlin/dataframe/blob/master/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/annotations/ImportDataSchema.kt)
for more details.

</tab>

<tab title="Method 2. Gradle task">

Put this in `build.gradle` or `build.gradle.kts`
For the following configuration, file `Repository.Generated.kt` will be generated
to `build/generated/dataframe/org/example` folder.

```kotlin
dataframes {
    schema {
        data = "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"
        name = "org.example.Repository"
    }
}
```

See [reference](Gradle-Plugin.md) and [examples](Gradle-Plugin.md#examples) for more details.

</tab>
</tabs>

After `assemble`, the following code should compile and run:

<!---FUN useInferredSchema-->

```kotlin
// Repository.readCsv() has argument 'path' with default value https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv
val df = Repository.readCsv()
// Use generated properties to access data in rows
df.maxBy { stargazersCount }.print()
// Or to access columns in dataframe.
print(df.fullName.count { it.contains("kotlin") })
```

<!---END-->
