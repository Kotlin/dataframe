[//]: # (title: Import Data Schemas, e.g. from OpenAPI, in Kotlin Notebook)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

> **Experimental**: Support for OpenAPI 3.0.0 schemas is demoted to experimental
> and may change or be removed in future releases. This is because OpenAPI 3.1 (and 3.2) have
> introduced significant changes that require specialized handling.
> Follow https://github.com/Kotlin/dataframe/issues/897 for updates and please leave your feedback.
> {style="warning"}

Similar to [importing OpenAPI Data Schemas in Gradle projects](schemasImportOpenApiGradle.md),
you can also do this in Kotlin Notebook.
This requires enabling the `enableExperimentalOpenApi` setting, like:
```
%use dataframe(..., enableExperimentalOpenApi=true)
```

There is only a slight difference in notation:

Import the schema using any path (`String`), `URL`, or `File`:

```kotlin
val PetStore = importDataSchema("https://petstore3.swagger.io/api/v3/openapi.json")
```

and then from the next cell you run and onwards, you can call, for example:

```kotlin
val df = PetStore.Pet.readJson("https://petstore3.swagger.io/api/v3/pet/findByStatus?status=available")
```

So, very similar indeed!

(Note: The type of `PetStore` will be generated as `PetStoreDataSchema`, but this doesn't affect the way you can use
it.)
