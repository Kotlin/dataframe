[//]: # (title: Import OpenAPI Schemas in Gradle project (Experimental))

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

> The current Gradle plugin is **under consideration for deprecation** and may be officially marked as deprecated in future releases.
>
> At the moment, **[data schema generation is handled via dedicated methods](DataSchemaGenerationMethods.md)** instead of relying on the plugin.
{style="warning"}

<warning>
OpenAPI 3.0.0 schema support is marked as experimental. It might change or be removed in the future.
</warning>

JSON schema inference is great, but it's not perfect. However, more and more APIs offer
[OpenAPI (Swagger)](https://swagger.io/) specifications. 

Aside from API endpoints, they also hold
[Data Models](https://swagger.io/docs/specification/data-models/) which include all the information about the types
that can be returned from or supplied to the API. 

Why should we reinvent the wheel and write our own schema inference
when we can use the one provided by the API? 

Not only will we now get the proper names of the types, but we will also
get enums, correct inheritance and overall better type safety.

First of all, you will need the extra dependency:

```kotlin
implementation("org.jetbrains.kotlinx:dataframe-openapi:%dataframeVersion%")
```

OpenAPI type schemas can be generated using both methods described above:

```kotlin
@file:ImportDataSchema(
    path = "https://petstore3.swagger.io/api/v3/openapi.json",
    name = "PetStore",
    enableExperimentalOpenApi = true,
)

import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
```

```kotlin
dataframes {
    schema {
        data = "https://petstore3.swagger.io/api/v3/openapi.json"
        name = "PetStore"
    }
    enableExperimentalOpenApi = true
}
```

The only difference is that the name provided is now irrelevant, since the type names are provided by the OpenAPI spec.
(If you were wondering, yes, the Kotlin DataFrame library can tell the difference between an OpenAPI spec and normal JSON data)

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
the [GitHub repo](https://github.com/Kotlin/dataframe/issues).
