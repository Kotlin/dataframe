# OpenAPI

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.io.OpenApiSamples-->

<web-summary>
Work with JSON data based on OpenAPI 3.0 schemas using Kotlin DataFrame — helpful for consuming structured API responses.
</web-summary>

<card-summary>
Use Kotlin DataFrame to read and write data that conforms to OpenAPI specifications. Great for API-driven data workflows.
</card-summary>

<link-summary>
Learn how to use OpenAPI 3.0 JSON schemas with Kotlin DataFrame to load and manipulate API-defined data.
</link-summary>


> **Experimental**: Support for OpenAPI 3.0 schemas is demoted to experimental
> and may change or be removed in future releases. This is because OpenAPI 3.1 (and 3.2) have
> introduced significant changes that require specialized handling.
> Follow https://github.com/Kotlin/dataframe/issues/897 for updates and please leave your feedback.
> {style="warning"}

Kotlin DataFrame provides support for reading and writing JSON data
that conforms to [OpenAPI 3.0 specifications](https://www.openapis.org).
This feature is useful when working with APIs that expose structured data defined via OpenAPI schemas.

### `openapi` modules

About the [`dataframe-openapi`](Modules.md#dataframe-openapi)/
[`dataframe-openapi-generator`](Modules.md#dataframe-openapi-generator) modules:

- You only need [`dataframe-openapi-generator` module](Modules.md#dataframe-openapi-generator) to generate `DataSchema`
  interfaces and helper functions for reading JSON.
  This does not need to be included in your published artifact.
- You do need to include [`dataframe-openapi` module](Modules.md#dataframe-openapi) in your artifact, as it contains
  some small helper functions that are used when reading JSON using the generated `DataSchema` interfaces.

These are **is not included** in the general [`dataframe`](Modules.md#dataframe-general) artifact due to their
_experimental_ status.

### HTTP API calls

DataFrame uses only the types inside the OpenAPI spec, not the API paths.
If you want to use all OpenAPI has to offer, check out
[OpenAPI generator support in IntelliJ IDEA](https://www.jetbrains.com/help/idea/openapi.html#codegen), which
can use a multitude of libraries, like [Ktor](https://ktor.io) to handle the API calls.

If you get raw API results in JSON format, you can convert the results to DataFrame-generated data schema types
like:

```kotlin
MyName.SomeOpenApiType.readJsonStr(text = rawJsonArrayString): DataFrame<SomeOpenApiType>
```

See [](#examples) for how to generate these.

If you receive maps or lists of objects, those can easily be
[converted to a dataframe](createDataFrame.md#todataframe) too.

You can still perform simple `GET` calls with DataFrame functions,
like `MyName.SomeOpenApiType.readJson("some/api/url")`, see [](#examples).

### Examples

Here is an example showing how to turn an OpenAPI spec into usable data schemas:

<!---FUN readCodeForGeneration-->

```kotlin
val url = "https://petstore3.swagger.io/api/v3/openapi.json"
val code = OpenApi().readCodeForGeneration(
    stream = URI(url).toURL().openStream(),
    name = "PetStore",
    extensionProperties = false, // optional, only needed without compiler plugin
    generateHelperCompanionObject = false, // optional, used inside notebooks
)
println(code)
```

<!---END-->

This uses the generic DataFrame `SupportedCodeGenerationFormat` interface,
which is also used by [`importDataSchema()` in notebooks](schemasImportOpenApiJupyter.md).

To provide some Swagger-parser-specific arguments, like `auth` and `options`, use:

<!---FUN readOpenApiFunction-->

```kotlin
val url = "https://petstore3.swagger.io/api/v3/openapi.json"
val code = readOpenApi(
    uri = url,
    name = "PetStore",
    extensionProperties = false, // only needed without compiler plugin
    generateHelperCompanionObject = false, // optional, used inside notebooks
    auth = null, // optional, if authentication is needed to access the url
    options = null, // optional, Swagger parse options
)
println(code)
```

<!---END-->

or, if you've already read your OpenAPI file as `String`:

<!---FUN readOpenApiAsStringFunction-->

```kotlin
val openApiAsString = URI("https://petstore3.swagger.io/api/v3/openapi.json").toURL().readText()
val code = readOpenApiAsString(
    openApiAsString = openApiAsString,
    name = "PetStore",
    extensionProperties = false, // only needed without compiler plugin
    generateHelperCompanionObject = false, // optional, used inside notebooks
    auth = null, // optional, if authentication is needed to access the url
    options = null, // optional, Swagger parse options
)
println(code)
```

<!---END-->

<procedure title="Generated code:" collapsible="true">

<!---FUN Result-->

```kotlin
interface PetStore {

    @DataSchema(isOpen = false)
    interface Order {
        val id: Long?
        val petId: Long?
        val quantity: Int?
        val shipDate: LocalDateTime?
        val status: Status?
        val complete: Boolean?

        public companion object {
            public val keyValuePaths: List<JsonPath>
                get() = listOf()

            public fun DataFrame<*>.convertToOrder(convertTo: ConvertSchemaDsl<Order>.() -> Unit = {}): DataFrame<Order> =
                convertTo<Order> {
                    convertDataRowsWithOpenApi()
                    convertTo()
                }

            public fun readJson(url: URL): DataFrame<Order> = DataFrame
                .readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToOrder()

            public fun readJson(path: String): DataFrame<Order> = DataFrame
                .readJson(path, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToOrder()

            public fun readJson(stream: InputStream): DataFrame<Order> = DataFrame
                .readJson(stream, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToOrder()

            public fun readJsonStr(text: String): DataFrame<Order> = DataFrame
                .readJsonStr(text, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToOrder()
        }

    }

    enum class Status(override val value: String) : DataSchemaEnum {
        PLACED("placed"),
        APPROVED("approved"),
        DELIVERED("delivered");
    }

    @DataSchema(isOpen = false)
    interface Category {
        val id: Long?
        val name: String?

        public companion object {
            public val keyValuePaths: List<JsonPath>
                get() = listOf()

            public fun DataFrame<*>.convertToCategory(convertTo: ConvertSchemaDsl<Category>.() -> Unit = {}): DataFrame<Category> =
                convertTo<Category> {
                    convertDataRowsWithOpenApi()
                    convertTo()
                }

            public fun readJson(url: URL): DataFrame<Category> = DataFrame
                .readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToCategory()

            public fun readJson(path: String): DataFrame<Category> = DataFrame
                .readJson(path, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToCategory()

            public fun readJson(stream: InputStream): DataFrame<Category> = DataFrame
                .readJson(stream, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToCategory()

            public fun readJsonStr(text: String): DataFrame<Category> = DataFrame
                .readJsonStr(text, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToCategory()
        }

    }

    @DataSchema(isOpen = false)
    interface User {
        val id: Long?
        val username: String?
        val firstName: String?
        val lastName: String?
        val email: String?
        val password: String?
        val phone: String?
        val userStatus: Int?

        public companion object {
            public val keyValuePaths: List<JsonPath>
                get() = listOf()

            public fun DataFrame<*>.convertToUser(convertTo: ConvertSchemaDsl<User>.() -> Unit = {}): DataFrame<User> =
                convertTo<User> {
                    convertDataRowsWithOpenApi()
                    convertTo()
                }

            public fun readJson(url: URL): DataFrame<User> = DataFrame
                .readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToUser()

            public fun readJson(path: String): DataFrame<User> = DataFrame
                .readJson(path, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToUser()

            public fun readJson(stream: InputStream): DataFrame<User> = DataFrame
                .readJson(stream, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToUser()

            public fun readJsonStr(text: String): DataFrame<User> = DataFrame
                .readJsonStr(text, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToUser()
        }

    }

    @DataSchema(isOpen = false)
    interface Tag {
        val id: Long?
        val name: String?

        public companion object {
            public val keyValuePaths: List<JsonPath>
                get() = listOf()

            public fun DataFrame<*>.convertToTag(convertTo: ConvertSchemaDsl<Tag>.() -> Unit = {}): DataFrame<Tag> =
                convertTo<Tag> {
                    convertDataRowsWithOpenApi()
                    convertTo()
                }

            public fun readJson(url: URL): DataFrame<Tag> = DataFrame
                .readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToTag()

            public fun readJson(path: String): DataFrame<Tag> = DataFrame
                .readJson(path, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToTag()

            public fun readJson(stream: InputStream): DataFrame<Tag> = DataFrame
                .readJson(stream, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToTag()

            public fun readJsonStr(text: String): DataFrame<Tag> = DataFrame
                .readJsonStr(text, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToTag()
        }

    }

    @DataSchema(isOpen = false)
    interface Pet {
        val id: Long?
        val name: String
        val category: Category?
        val photoUrls: List<String>
        val tags: DataFrame<Tag?>
        val status: Status1?

        public companion object {
            public val keyValuePaths: List<JsonPath>
                get() = listOf()

            public fun DataFrame<*>.convertToPet(convertTo: ConvertSchemaDsl<Pet>.() -> Unit = {}): DataFrame<Pet> =
                convertTo<Pet> {
                    convertDataRowsWithOpenApi()
                    convertTo()
                }

            public fun readJson(url: URL): DataFrame<Pet> = DataFrame
                .readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToPet()

            public fun readJson(path: String): DataFrame<Pet> = DataFrame
                .readJson(path, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToPet()

            public fun readJson(stream: InputStream): DataFrame<Pet> = DataFrame
                .readJson(stream, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToPet()

            public fun readJsonStr(text: String): DataFrame<Pet> = DataFrame
                .readJsonStr(text, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToPet()
        }

    }

    enum class Status1(override val value: String) : DataSchemaEnum {
        AVAILABLE("available"),
        PENDING("pending"),
        SOLD("sold");
    }

    @DataSchema(isOpen = false)
    interface ApiResponse {
        val code: Int?
        val type: String?
        val message: String?

        public companion object {
            public val keyValuePaths: List<JsonPath>
                get() = listOf()

            public fun DataFrame<*>.convertToApiResponse(convertTo: ConvertSchemaDsl<ApiResponse>.() -> Unit = {}): DataFrame<ApiResponse> =
                convertTo<ApiResponse> {
                    convertDataRowsWithOpenApi()
                    convertTo()
                }

            public fun readJson(url: URL): DataFrame<ApiResponse> = DataFrame
                .readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToApiResponse()

            public fun readJson(path: String): DataFrame<ApiResponse> = DataFrame
                .readJson(path, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToApiResponse()

            public fun readJson(stream: InputStream): DataFrame<ApiResponse> = DataFrame
                .readJson(stream, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToApiResponse()

            public fun readJsonStr(text: String): DataFrame<ApiResponse> = DataFrame
                .readJsonStr(text, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                .convertToApiResponse()
        }

    }
}
```

<!---END-->

</procedure>

<procedure title="Using the generated code:" collapsible="true" default-state="expanded">

<!---FUN usingResult -->

```kotlin
val df: DataFrame<PetStore.Pet> = PetStore.Pet.readJson("$baseUrl/pet/10")
```

<!---END-->

<inline-frame src="resources/usingResult.html" width="100%"/>

</procedure>

See our [json-openapi example project](https://github.com/Kotlin/dataframe/tree/master/examples/projects/json-openapi)
for how to use OpenAPI in combination with DataFrame inside a Gradle project.

### Notebooks

To enable it in Kotlin Notebook, use:

```jupyter
%use dataframe(enableExperimentalOpenApi = true)
```

See [](schemasImportOpenApiJupyter.md) for details on how to work with OpenAPI-based data in notebooks, as well as
[the OpenAPI guide notebook](https://github.com/Kotlin/dataframe/blob/master/examples/notebooks/json/KeyValueAndOpenApi.ipynb)

### Some Background around OpenAPI Support in DataFrame

DataFrame does not (yet) have a go-to system for reading just types to generate data schemas with.
Usually, you read a sample of your data into a [dataframe](DataFrame.md),
generate the [data schema](dataSchema.md) from it, and then use it in your pipeline.

However, what if your data source already provides its own types, like OpenAPI does for JSON?
Surely that will be safer than using a sample.

This is, for instance, why in [dataframe-jdbc](SQL.md) we have
`DataFrameSchema.readSqlTable(): DataFrameSchema` which you can print, copy-paste into your project,
and use to safely cast your data to.

However, for OpenAPI, we identified that just generating `DataSchema` interfaces was not enough to
properly read JSON with types defined in an OpenAPI schema. OpenAPI schemas support inheritance, enums,
and other complex features that require specialized handling.
This is why we have a separate '-generator' module which can generate both `DataSchema` interfaces,
helper functions for reading JSON and any type aliases and enums that are needed to correctly read
the JSON according to the OpenAPI schema.
