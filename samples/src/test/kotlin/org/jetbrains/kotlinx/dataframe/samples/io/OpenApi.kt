package org.jetbrains.kotlinx.dataframe.samples.io

import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl
import org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ANY_COLUMNS
import org.jetbrains.kotlinx.dataframe.io.OpenApi
import org.jetbrains.kotlinx.dataframe.io.convertDataRowsWithOpenApi
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.dataframe.io.readOpenApi
import org.jetbrains.kotlinx.dataframe.io.readOpenApiAsString
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.samples.io.OpenApiSamples.Result.PetStore
import org.junit.Ignore
import org.junit.Test
import java.io.InputStream
import java.net.URI
import java.net.URL

class OpenApiSamples : DataFrameSampleHelper(sampleName = "openapi", subFolder = "io") {

    @Ignore
    @Test
    fun readCodeForGeneration() {
        // SampleStart
        val url = "https://petstore3.swagger.io/api/v3/openapi.json"
        val code = OpenApi().readCodeForGeneration(
            stream = URI(url).toURL().openStream(),
            name = "PetStore",
            extensionProperties = false, // optional, only needed without compiler plugin
            generateHelperCompanionObject = false, // optional, used inside notebooks
        )
        println(code)
        // SampleEnd
    }

    @Ignore
    @Test
    fun readOpenApiFunction() {
        // SampleStart
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
        // SampleEnd
    }

    @Ignore
    @Test
    fun readOpenApiAsStringFunction() {
        // SampleStart
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
        // SampleEnd
    }

    @Suppress("ktlint")
    interface Result {
        // SampleStart
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
        // SampleEnd
    }

    @Test
    fun usingResult() {
        val baseUrl = ""
        runCatching {
            // SampleStart
            val df: DataFrame<PetStore.Pet> = PetStore.Pet.readJson("$baseUrl/pet/10")
            // SampleEnd
        }
        PetStore.Pet.readJsonStr(
            """
            {
              "id": 10,
              "name": "doggie",
              "category": {
                "id": 1,
                "name": "Dogs"
              },
              "photoUrls": [
                "string"
              ],
              "tags": [
                {
                  "id": 0,
                  "name": "string"
                }
              ],
              "status": "available"
            }
            """.trimIndent(),
        ).saveDfHtmlSample()
    }
}
