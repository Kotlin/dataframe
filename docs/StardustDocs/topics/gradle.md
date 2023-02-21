[//]: # (title: Data Schemas in Gradle projects)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

In Gradle project Kotlin DataFrame library provides

1. Annotation processing for generation of extension properties
2. Annotation processing for `DataSchema` inference from datasets.
3. Gradle task for `DataSchema` inference from datasets.

### Configuration

To use [extension properties API](extensionPropertiesApi.md) in Gradle project you
should [configure Kotlin DataFrame plugin](installation.md#data-schema-preprocessor).

### Annotation processing

Declare data schemas in your code and use them to access data in [`DataFrames`](DataFrame.md).
A data schema is a class or interface annotated with `@DataSchema`:

```kotlin
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

<tabs>
<tab title="Method 1. Annotation processing">

ImportDataSchema annotation must be above package directive. You can put this annotation in the same file as data
processing code. You can import schema from URL or relative path of the file. Relative path by default is resolved to
project root directory. You can configure it
by [passing](https://kotlinlang.org/docs/ksp-quickstart.html#pass-options-to-processors) `dataframe.resolutionDir`
option to preprocessor

**Note that due to incremental processing, imported schema will be re-generated only if some source code has changed
from previous invocation, at least one character**

For the following configuration, file `Repository.Generated.kt` will be generated to `build/generated/ksp/` folder in
the same package as file containing the annotation.

```kotlin
@file:ImportDataSchema(
    "Repository",
    "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv",
)

import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
```

See KDocs for `ImportDataSchema` in IDE
or [github](https://github.com/Kotlin/dataframe/blob/master/core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/annotations/ImportDataSchema.kt)
for more details

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

See [reference](gradleReference.md) and [examples](gradleReference.md#examples) for more details.

</tab>
</tabs>

After `build`, the following code should compile and run:

<!---FUN useInferredSchema-->

```kotlin
// Repository.readCSV() has argument 'path' with default value https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv
val df = Repository.readCSV()
// Use generated properties to access data in rows
df.maxBy { stargazersCount }.print()
// Or to access columns in dataframe.
print(df.fullName.count { it.contains("kotlin") })
```

<!---END-->

### OpenAPI Schemas

JSON schema inference is great, but it's not perfect. However, more and more APIs offer
[OpenAPI (Swagger)](https://swagger.io/) specifications. Aside from API endpoints, they also hold
[Data Models](https://swagger.io/docs/specification/data-models/) which include all the information about the types
that can be returned from or supplied to the API. Why should we reinvent the wheel and write our own schema inference
when we can use the one provided by the API? Not only will we now get the proper names of the types, but we will also
get enums, correct inheritance and overall better type safety.

First of all, you will need the extra dependency:

```kotlin
implementation("org.jetbrains.kotlinx:dataframe-openapi:$dataframe_version")
```

OpenAPI type schemas can be generated using both methods described above:

```kotlin
@file:ImportDataSchema(
    path = "https://petstore3.swagger.io/api/v3/openapi.json",
    name = "PetStore",
)

import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
```

```kotlin
dataframes {
    schema {
        data = "https://petstore3.swagger.io/api/v3/openapi.json"
        name = "PetStore"
    }
}
```

The only difference is that the name provided is now irrelevant, since the type names are provided by the OpenAPI spec.
(If you were wondering, yes, Kotlin DataFrame library can tell the difference between an OpenAPI spec and normal JSON data)

After importing the data schema, you can now start to import any JSON data you like using the generated schemas.
For instance, one of the types in the schema above is `PetStore.Pet` (which can also be
explored [here](https://petstore3.swagger.io/)),
so let's parse some Pets:

```kotlin
val df: DataFrame<PetStore.Pet> =
    PetStore.Pet.readJson("https://petstore3.swagger.io/api/v3/pet/findByStatus?status=available")
```

Now you will have a correctly typed [`DataFrame`](DataFrame.md)!

You can also always ctrl+click on the `PetStore.Pet` type to see all the generated schemas.

If you experience any issues with the OpenAPI support (since there are many gotchas and edge-cases when converting
something as
type-fluid as JSON to a strongly typed language), please open an issue on
the [Github repo](https://github.com/Kotlin/dataframe/issues).
