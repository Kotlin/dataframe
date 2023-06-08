[//]: # (title: Import Data Schemas, e.g. from OpenAPI, in Jupyter)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

Similar to [importing OpenAPI data schemas in Gradle projects](gradle.md#openapi-schemas), you can also
do this in Jupyter notebooks. There is only a slight difference in notation:

Import the schema using any path (`String`), `URL`, or `File`:

```kotlin
val PetStore = importDataSchema("https://petstore3.swagger.io/api/v3/openapi.json")
```

and then from next cell you run and onwards, you can call, for example:

```kotlin
val df = PetStore.Pet.readJson("https://petstore3.swagger.io/api/v3/pet/findByStatus?status=available")
```

So, very similar indeed!

(Note: The type of `PetStore` will be generated as `PetStoreDataSchema`, but this doesn't affect the way you can use
it.)
