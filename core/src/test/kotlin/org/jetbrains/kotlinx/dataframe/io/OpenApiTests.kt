package org.jetbrains.kotlinx.dataframe.io

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithConverter
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase
import org.junit.Test
import java.io.File
import java.io.InputStream

class OpenApiTests : JupyterReplTestCase() {

    private val openApi = OpenApi()
    private val additionalImports = openApi.createDefaultReadMethod().additionalImports.joinToString("\n")

    private fun execGeneratedCode(code: CodeWithConverter) {
        @Language("kts")
        val res1 = exec(
            """
            $additionalImports
            ${code.declarations}
            """.trimIndent()
        )
    }

    private fun execGeneratedCode(file: File) = execGeneratedCode(openApi.readCodeForGeneration(file))
    private fun execGeneratedCode(stream: InputStream) = execGeneratedCode(openApi.readCodeForGeneration(stream))
    private fun execGeneratedCode(text: String) = execGeneratedCode(openApi.readCodeForGeneration(text))

    private val petstoreJson = File("../data/petstore.json")
    private val petstoreAdvancedJson = File("../data/petstore_advanced.json")
    private val petstoreYaml = File("../data/petstore.yaml")
    private val someAdvancedPets = File("../data/some_advanced_pets.json").readText()

    @Language("json")
    private val somePets = """
        [
            {
              "id": 0,
              "name": "doggie",
              "tag": "Dogs"
            },
            {
              "id": 1,
              "name": "kitty",
              "tag": "Cats"
            },
            {
              "id": 2,
              "name": "puppy"
            }
        ]
    """.trimIndent()

    private val somePetsTripleQuotes = "\"\"\"$somePets\"\"\""

    @DataSchema(isOpen = false)
    interface Pet {
        val id: Long
        val name: String
        val tag: String?
    }

    @Test
    fun `Simple test Petstore Json`() {
        execGeneratedCode(petstoreJson)

        @Language("kts")
        val res2 = execRaw("Pet.readJsonStr($somePetsTripleQuotes)") as AnyFrame

        val res3 = res2.cast<Pet>(verify = true)
        res3.print(borders = true, columnTypes = true, title = true)
    }

    @Test
    fun `Simple test Petstore Yaml`() {
        execGeneratedCode(petstoreYaml)

        @Language("kts")
        val res2 = execRaw("Pet.readJsonStr($somePetsTripleQuotes)") as AnyFrame

        val res3 = res2.cast<Pet>(verify = true)
        res3.print(borders = true, columnTypes = true, title = true)
    }

    enum class Status {
        placed,
        approved,
        delivered;
    }

    @DataSchema(isOpen = false)
    interface Order {
        val id: kotlin.Long?
        val petId: kotlin.Long?
        val quantity: kotlin.Int?
        val shipDate: kotlinx.datetime.LocalDateTime?
        val status: Status
        val complete: kotlin.Boolean?
    }

    @DataSchema(isOpen = false)
    interface Address {
        val street: kotlin.String?
        val city: kotlin.String?
        val state: kotlin.String?
        val zip: kotlin.String?
    }

    @DataSchema(isOpen = false)
    interface Customer {
        val id: kotlin.Long?
        val username: kotlin.String?
        val address: org.jetbrains.kotlinx.dataframe.DataFrame<Address>?
    }

    @DataSchema(isOpen = false)
    interface Category {
        val id: kotlin.Long?
        val name: kotlin.String?
    }

    @DataSchema(isOpen = false)
    interface User {
        val id: kotlin.Long?
        val username: kotlin.String?
        val firstName: kotlin.String?
        val lastName: kotlin.String?
        val email: kotlin.String?
        val password: kotlin.String?
        val phone: kotlin.String?
        val userStatus: kotlin.Int?
    }

    @DataSchema(isOpen = false)
    interface Tag {
        val id: kotlin.Long?
        val name: kotlin.String?
    }

    enum class Status1 {
        available,
        pending,
        sold;
    }

    @DataSchema(isOpen = false)
    interface Pet2 {
        val id: kotlin.Long?
        val name: kotlin.String
        val category: org.jetbrains.kotlinx.dataframe.DataRow<Category>
        val photoUrls: kotlin.collections.List<kotlin.String>
        val tags: org.jetbrains.kotlinx.dataframe.DataFrame<Tag>?
        val status: Status1
    }

    @DataSchema(isOpen = false)
    interface ApiResponse {
        val code: kotlin.Int?
        val type: kotlin.String?
        val message: kotlin.String?
    }

    @Test
    fun `Advanced test Petstore Json`() {
        execGeneratedCode(petstoreAdvancedJson)
//        execGeneratedCode(URL("https://petstore3.swagger.io/api/v3/openapi.json").openStream())

        @Language("kts")
        val res2 = execRaw("Pet.readJsonStr(\"\"\"$someAdvancedPets)\"\"\"") as AnyFrame

        res2.print()

//        val res3 = res2.cast<Pet2>(verify = true)
//        res3.print(borders = true, columnTypes = true, title = true)
    }
}
