@file:ImportDataSchema(
    name = "ApiGuru", path = "src/test/resources/ApiGuruOpenApi.yaml"
)

@file:ImportDataSchema(
    name = "MlcGroupData", path = "src/test/resources/MlcGroupDataOpenApi.yaml"
)

package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.should
import io.kotest.matchers.string.haveSubstring
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
import org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithConverter
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ANY_COLUMNS
import org.jetbrains.kotlinx.dataframe.io.OpenApiTests.Error.Companion.convertToError
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase
import org.junit.Test
import java.io.File
import java.io.InputStream
import java.net.URL

class OpenApiTests : JupyterReplTestCase() {

    private val openApi = OpenApi()
    private val additionalImports = openApi.createDefaultReadMethod().additionalImports.joinToString("\n")

    private fun execGeneratedCode(code: CodeWithConverter): CodeWithConverter {
        @Language("kts") val res1 = exec(
            """
            $additionalImports
            ${code.declarations}
            """.trimLines()
        )
        return code
    }

    private fun execGeneratedCode(file: File) = execGeneratedCode(code = openApi.readCodeForGeneration(file, true))
    private fun execGeneratedCode(stream: InputStream) =
        execGeneratedCode(code = openApi.readCodeForGeneration(stream, true))

    private fun execGeneratedCode(text: String) = execGeneratedCode(code = openApi.readCodeForGeneration(text, true))

    private val petstoreJson = File("src/test/resources/petstore.json")
    private val petstoreAdvancedJson = File("src/test/resources/petstore_advanced.json")
    private val petstoreYaml = File("src/test/resources/petstore.yaml")
    private val someAdvancedPets = File("src/test/resources/some_advanced_pets.json").readText()
    private val someAdvancedOrders = File("src/test/resources/some_advanced_orders.json").readText()
    private val someAdvancedFailingOrders = File("src/test/resources/some_advanced_failing_orders.json").readText()
    private val advancedExample = File("src/test/resources/openapi_advanced_example.yaml")
    private val advancedData = File("src/test/resources/openapi_advanced_data.json").readText()
    private val advancedDataError = File("src/test/resources/openapi_advanced_data2.json").readText()
    private val apiGuruData = File("src/test/resources/ApiGuruSample.json").readText()

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
    """.trimLines()

    private val somePetsTripleQuotes = "\"\"\"$somePets\"\"\""

    private fun simpleTest(file: File) {
        val code = execGeneratedCode(file).declarations.trimLines()

        @Language("kt") val petInterface = """
            @DataSchema(isOpen = false)
            interface Pet {
                val id: kotlin.Long
                val name: kotlin.String
                val tag: kotlin.String?
                public companion object {
        """.trimLines()

        code should haveSubstring(petInterface)

        @Language("kt") val petExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long> @JvmName("Pet_id") get() = this["id"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.id: kotlin.Long @JvmName("Pet_id") get() = this["id"] as kotlin.Long
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> @JvmName("NullablePet_id") get() = this["id"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.id: kotlin.Long? @JvmName("NullablePet_id") get() = this["id"] as kotlin.Long?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> @JvmName("Pet_name") get() = this["name"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.name: kotlin.String @JvmName("Pet_name") get() = this["name"] as kotlin.String
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullablePet_name") get() = this["name"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.name: kotlin.String? @JvmName("NullablePet_name") get() = this["name"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.tag: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("Pet_tag") get() = this["tag"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.tag: kotlin.String? @JvmName("Pet_tag") get() = this["tag"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.tag: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullablePet_tag") get() = this["tag"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.tag: kotlin.String? @JvmName("NullablePet_tag") get() = this["tag"] as kotlin.String?
        """.trimLines()

        code should haveSubstring(petExtensions)

        @Language("kt") val petsTypeAlias = """
            typealias Pets = org.jetbrains.kotlinx.dataframe.DataFrame<Pet>
        """.trimLines()

        code should haveSubstring(petsTypeAlias)

        @Language("kt") val errorInterface = """
            @DataSchema(isOpen = false)
            interface Error {
                val code: kotlin.Int
                val message: kotlin.String
                public companion object {
        """.trimLines()

        code should haveSubstring(errorInterface)

        @Language("kt") val errorExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error>.code: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int> @JvmName("Error_code") get() = this["code"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error>.code: kotlin.Int @JvmName("Error_code") get() = this["code"] as kotlin.Int
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error?>.code: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> @JvmName("NullableError_code") get() = this["code"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error?>.code: kotlin.Int? @JvmName("NullableError_code") get() = this["code"] as kotlin.Int?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error>.message: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> @JvmName("Error_message") get() = this["message"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error>.message: kotlin.String @JvmName("Error_message") get() = this["message"] as kotlin.String
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error?>.message: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableError_message") get() = this["message"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error?>.message: kotlin.String? @JvmName("NullableError_message") get() = this["message"] as kotlin.String?
        """.trimLines()

        code should haveSubstring(errorExtensions)

        @Language("kts") val res2 = execRaw("Pet.readJsonStr($somePetsTripleQuotes)") as AnyFrame
    }

    @Test
    fun `Simple test Petstore Json`() {
        simpleTest(petstoreJson)
    }

    @Test
    fun `Simple test Petstore Yaml`() {
        simpleTest(petstoreYaml)
    }

    @Test
    fun `Advanced test Petstore Json`() {
        val code = execGeneratedCode(petstoreAdvancedJson).declarations.trimLines()

        @Language("kts") val statusInterface = """
            enum class Status(override val value: kotlin.String) : org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum {
                PLACED("placed"),
                APPROVED("approved"),
                DELIVERED("delivered");
            }
        """.trimLines()

        code should haveSubstring(statusInterface)

        @Language("kt") val orderInterface = """
            @DataSchema(isOpen = false)
            interface Order {
                val id: kotlin.Long?
                val petId: kotlin.Long?
                val quantity: kotlin.Int?
                val shipDate: kotlinx.datetime.LocalDateTime?
                val status: Status?
                val complete: kotlin.Boolean?
                public companion object {
        """.trimLines()

        code should haveSubstring(orderInterface)

        @Language("kt") val customerInterface = """
            @DataSchema(isOpen = false)
            interface Customer {
                val id: kotlin.Long?
                val username: kotlin.String?
                val address: org.jetbrains.kotlinx.dataframe.DataFrame<Address?>
                public companion object {
        """.trimLines() // address is a nullable array of objects -> DataFrame<Address?>

        code should haveSubstring(customerInterface)

        @Language("kt") val customerExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer>.address: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<Address?>> @JvmName("Customer_address") get() = this["address"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<Address?>>
            val org.jetbrains.kotlinx.dataframe.DataRow<Customer>.address: org.jetbrains.kotlinx.dataframe.DataFrame<Address?> @JvmName("Customer_address") get() = this["address"] as org.jetbrains.kotlinx.dataframe.DataFrame<Address?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer?>.address: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<Address?>> @JvmName("NullableCustomer_address") get() = this["address"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<Address?>>
            val org.jetbrains.kotlinx.dataframe.DataRow<Customer?>.address: org.jetbrains.kotlinx.dataframe.DataFrame<Address?> @JvmName("NullableCustomer_address") get() = this["address"] as org.jetbrains.kotlinx.dataframe.DataFrame<Address?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> @JvmName("Customer_id") get() = this["id"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Customer>.id: kotlin.Long? @JvmName("Customer_id") get() = this["id"] as kotlin.Long?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer?>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> @JvmName("NullableCustomer_id") get() = this["id"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Customer?>.id: kotlin.Long? @JvmName("NullableCustomer_id") get() = this["id"] as kotlin.Long?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer>.username: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("Customer_username") get() = this["username"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Customer>.username: kotlin.String? @JvmName("Customer_username") get() = this["username"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer?>.username: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableCustomer_username") get() = this["username"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Customer?>.username: kotlin.String? @JvmName("NullableCustomer_username") get() = this["username"] as kotlin.String?
        """.trimLines()

        code should haveSubstring(customerExtensions)

        @Language("kt") val orderExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Order>.complete: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?> @JvmName("Order_complete") get() = this["complete"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Order>.complete: kotlin.Boolean? @JvmName("Order_complete") get() = this["complete"] as kotlin.Boolean?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Order?>.complete: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?> @JvmName("NullableOrder_complete") get() = this["complete"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Order?>.complete: kotlin.Boolean? @JvmName("NullableOrder_complete") get() = this["complete"] as kotlin.Boolean?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Order>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> @JvmName("Order_id") get() = this["id"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Order>.id: kotlin.Long? @JvmName("Order_id") get() = this["id"] as kotlin.Long?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Order?>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> @JvmName("NullableOrder_id") get() = this["id"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Order?>.id: kotlin.Long? @JvmName("NullableOrder_id") get() = this["id"] as kotlin.Long?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Order>.petId: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> @JvmName("Order_petId") get() = this["petId"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Order>.petId: kotlin.Long? @JvmName("Order_petId") get() = this["petId"] as kotlin.Long?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Order?>.petId: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> @JvmName("NullableOrder_petId") get() = this["petId"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Order?>.petId: kotlin.Long? @JvmName("NullableOrder_petId") get() = this["petId"] as kotlin.Long?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Order>.quantity: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> @JvmName("Order_quantity") get() = this["quantity"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Order>.quantity: kotlin.Int? @JvmName("Order_quantity") get() = this["quantity"] as kotlin.Int?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Order?>.quantity: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> @JvmName("NullableOrder_quantity") get() = this["quantity"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Order?>.quantity: kotlin.Int? @JvmName("NullableOrder_quantity") get() = this["quantity"] as kotlin.Int?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Order>.shipDate: org.jetbrains.kotlinx.dataframe.DataColumn<kotlinx.datetime.LocalDateTime?> @JvmName("Order_shipDate") get() = this["shipDate"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlinx.datetime.LocalDateTime?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Order>.shipDate: kotlinx.datetime.LocalDateTime? @JvmName("Order_shipDate") get() = this["shipDate"] as kotlinx.datetime.LocalDateTime?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Order?>.shipDate: org.jetbrains.kotlinx.dataframe.DataColumn<kotlinx.datetime.LocalDateTime?> @JvmName("NullableOrder_shipDate") get() = this["shipDate"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlinx.datetime.LocalDateTime?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Order?>.shipDate: kotlinx.datetime.LocalDateTime? @JvmName("NullableOrder_shipDate") get() = this["shipDate"] as kotlinx.datetime.LocalDateTime?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Order>.status: org.jetbrains.kotlinx.dataframe.DataColumn<Status?> @JvmName("Order_status") get() = this["status"] as org.jetbrains.kotlinx.dataframe.DataColumn<Status?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Order>.status: Status? @JvmName("Order_status") get() = this["status"] as Status?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Order?>.status: org.jetbrains.kotlinx.dataframe.DataColumn<Status?> @JvmName("NullableOrder_status") get() = this["status"] as org.jetbrains.kotlinx.dataframe.DataColumn<Status?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Order?>.status: Status? @JvmName("NullableOrder_status") get() = this["status"] as Status?
        """.trimLines()

        code should haveSubstring(orderExtensions)

        @Language("kt") val addressInterface = """
            @DataSchema(isOpen = false)
            interface Address {
                val street: kotlin.String?
                val city: kotlin.String?
                val state: kotlin.String?
                val zip: kotlin.String?
                public companion object {
        """.trimLines()

        code should haveSubstring(addressInterface)

        @Language("kt") val addressExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Address>.city: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("Address_city") get() = this["city"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Address>.city: kotlin.String? @JvmName("Address_city") get() = this["city"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Address?>.city: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableAddress_city") get() = this["city"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Address?>.city: kotlin.String? @JvmName("NullableAddress_city") get() = this["city"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Address>.state: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("Address_state") get() = this["state"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Address>.state: kotlin.String? @JvmName("Address_state") get() = this["state"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Address?>.state: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableAddress_state") get() = this["state"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Address?>.state: kotlin.String? @JvmName("NullableAddress_state") get() = this["state"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Address>.street: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("Address_street") get() = this["street"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Address>.street: kotlin.String? @JvmName("Address_street") get() = this["street"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Address?>.street: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableAddress_street") get() = this["street"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Address?>.street: kotlin.String? @JvmName("NullableAddress_street") get() = this["street"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Address>.zip: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("Address_zip") get() = this["zip"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Address>.zip: kotlin.String? @JvmName("Address_zip") get() = this["zip"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Address?>.zip: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableAddress_zip") get() = this["zip"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Address?>.zip: kotlin.String? @JvmName("NullableAddress_zip") get() = this["zip"] as kotlin.String?
        """.trimLines()

        code should haveSubstring(addressExtensions)

        @Language("kt") val categoryInterface = """
            @DataSchema(isOpen = false)
            interface Category {
                val id: kotlin.Long?
                val name: kotlin.String?
                public companion object {
        """.trimLines()

        code should haveSubstring(categoryInterface)

        @Language("kt") val categoryExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Category>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> @JvmName("Category_id") get() = this["id"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Category>.id: kotlin.Long? @JvmName("Category_id") get() = this["id"] as kotlin.Long?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Category?>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> @JvmName("NullableCategory_id") get() = this["id"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Category?>.id: kotlin.Long? @JvmName("NullableCategory_id") get() = this["id"] as kotlin.Long?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Category>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("Category_name") get() = this["name"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Category>.name: kotlin.String? @JvmName("Category_name") get() = this["name"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Category?>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableCategory_name") get() = this["name"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Category?>.name: kotlin.String? @JvmName("NullableCategory_name") get() = this["name"] as kotlin.String?
        """.trimLines()

        code should haveSubstring(categoryExtensions)

        @Language("kt") val userInterface = """
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
                public companion object {
        """.trimLines()

        code should haveSubstring(userInterface)

        @Language("kt") val userExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User>.email: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("User_email") get() = this["email"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User>.email: kotlin.String? @JvmName("User_email") get() = this["email"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User?>.email: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableUser_email") get() = this["email"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User?>.email: kotlin.String? @JvmName("NullableUser_email") get() = this["email"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User>.firstName: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("User_firstName") get() = this["firstName"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User>.firstName: kotlin.String? @JvmName("User_firstName") get() = this["firstName"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User?>.firstName: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableUser_firstName") get() = this["firstName"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User?>.firstName: kotlin.String? @JvmName("NullableUser_firstName") get() = this["firstName"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> @JvmName("User_id") get() = this["id"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User>.id: kotlin.Long? @JvmName("User_id") get() = this["id"] as kotlin.Long?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User?>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> @JvmName("NullableUser_id") get() = this["id"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User?>.id: kotlin.Long? @JvmName("NullableUser_id") get() = this["id"] as kotlin.Long?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User>.lastName: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("User_lastName") get() = this["lastName"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User>.lastName: kotlin.String? @JvmName("User_lastName") get() = this["lastName"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User?>.lastName: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableUser_lastName") get() = this["lastName"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User?>.lastName: kotlin.String? @JvmName("NullableUser_lastName") get() = this["lastName"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User>.password: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("User_password") get() = this["password"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User>.password: kotlin.String? @JvmName("User_password") get() = this["password"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User?>.password: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableUser_password") get() = this["password"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User?>.password: kotlin.String? @JvmName("NullableUser_password") get() = this["password"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User>.phone: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("User_phone") get() = this["phone"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User>.phone: kotlin.String? @JvmName("User_phone") get() = this["phone"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User?>.phone: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableUser_phone") get() = this["phone"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User?>.phone: kotlin.String? @JvmName("NullableUser_phone") get() = this["phone"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User>.userStatus: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> @JvmName("User_userStatus") get() = this["userStatus"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User>.userStatus: kotlin.Int? @JvmName("User_userStatus") get() = this["userStatus"] as kotlin.Int?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User?>.userStatus: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> @JvmName("NullableUser_userStatus") get() = this["userStatus"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User?>.userStatus: kotlin.Int? @JvmName("NullableUser_userStatus") get() = this["userStatus"] as kotlin.Int?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User>.username: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("User_username") get() = this["username"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User>.username: kotlin.String? @JvmName("User_username") get() = this["username"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<User?>.username: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableUser_username") get() = this["username"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<User?>.username: kotlin.String? @JvmName("NullableUser_username") get() = this["username"] as kotlin.String?
        """.trimLines()

        code should haveSubstring(userExtensions)

        @Language("kt") val tagInterface = """
            @DataSchema(isOpen = false)
            interface Tag {
                val id: kotlin.Long?
                val name: kotlin.String?
                public companion object {
        """.trimLines()

        code should haveSubstring(tagInterface)

        @Language("kt") val status1Enum = """
            enum class Status1(override val value: kotlin.String) : org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum {
                AVAILABLE("available"),
                PENDING("pending"),
                SOLD("sold");
            }
        """.trimLines()

        code should haveSubstring(status1Enum)

        @Language("kt") val petInterface = """
            @DataSchema(isOpen = false)
            interface Pet {
                val id: kotlin.Long?
                val name: kotlin.String
                val category: Category?
                val photoUrls: kotlin.collections.List<kotlin.String>
                val tags: org.jetbrains.kotlinx.dataframe.DataFrame<Tag?>
                val status: Status1?
                public companion object {
        """.trimLines() // category is a single other object, photoUrls is a primitive array, tags is a nullable array of objects

        code should haveSubstring(petInterface)

        @Language("kt") val petExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.category: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<Category?> @JvmName("Pet_category") get() = this["category"] as org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<Category?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.category: org.jetbrains.kotlinx.dataframe.DataRow<Category?> @JvmName("Pet_category") get() = this["category"] as org.jetbrains.kotlinx.dataframe.DataRow<Category?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.category: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<Category?> @JvmName("NullablePet_category") get() = this["category"] as org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<Category?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.category: org.jetbrains.kotlinx.dataframe.DataRow<Category?> @JvmName("NullablePet_category") get() = this["category"] as org.jetbrains.kotlinx.dataframe.DataRow<Category?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> @JvmName("Pet_id") get() = this["id"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.id: kotlin.Long? @JvmName("Pet_id") get() = this["id"] as kotlin.Long?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> @JvmName("NullablePet_id") get() = this["id"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.id: kotlin.Long? @JvmName("NullablePet_id") get() = this["id"] as kotlin.Long?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> @JvmName("Pet_name") get() = this["name"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.name: kotlin.String @JvmName("Pet_name") get() = this["name"] as kotlin.String
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullablePet_name") get() = this["name"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.name: kotlin.String? @JvmName("NullablePet_name") get() = this["name"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.photoUrls: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<kotlin.String>> @JvmName("Pet_photoUrls") get() = this["photoUrls"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<kotlin.String>>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.photoUrls: kotlin.collections.List<kotlin.String> @JvmName("Pet_photoUrls") get() = this["photoUrls"] as kotlin.collections.List<kotlin.String>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.photoUrls: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<kotlin.String>?> @JvmName("NullablePet_photoUrls") get() = this["photoUrls"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<kotlin.String>?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.photoUrls: kotlin.collections.List<kotlin.String>? @JvmName("NullablePet_photoUrls") get() = this["photoUrls"] as kotlin.collections.List<kotlin.String>?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.status: org.jetbrains.kotlinx.dataframe.DataColumn<Status1?> @JvmName("Pet_status") get() = this["status"] as org.jetbrains.kotlinx.dataframe.DataColumn<Status1?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.status: Status1? @JvmName("Pet_status") get() = this["status"] as Status1?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.status: org.jetbrains.kotlinx.dataframe.DataColumn<Status1?> @JvmName("NullablePet_status") get() = this["status"] as org.jetbrains.kotlinx.dataframe.DataColumn<Status1?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.status: Status1? @JvmName("NullablePet_status") get() = this["status"] as Status1?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.tags: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<Tag?>> @JvmName("Pet_tags") get() = this["tags"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<Tag?>>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.tags: org.jetbrains.kotlinx.dataframe.DataFrame<Tag?> @JvmName("Pet_tags") get() = this["tags"] as org.jetbrains.kotlinx.dataframe.DataFrame<Tag?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.tags: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<Tag?>> @JvmName("NullablePet_tags") get() = this["tags"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<Tag?>>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.tags: org.jetbrains.kotlinx.dataframe.DataFrame<Tag?> @JvmName("NullablePet_tags") get() = this["tags"] as org.jetbrains.kotlinx.dataframe.DataFrame<Tag?>
        """.trimLines()

        code should haveSubstring(petExtensions)

        @Language("kt") val apiResponseInterface = """
            @DataSchema(isOpen = false)
            interface ApiResponse {
                val code: kotlin.Int?
                val type: kotlin.String?
                val message: kotlin.String?
                public companion object {
        """.trimLines()

        code should haveSubstring(apiResponseInterface)

        @Language("kt") val apiResponseExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<ApiResponse>.code: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> @JvmName("ApiResponse_code") get() = this["code"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?>
            val org.jetbrains.kotlinx.dataframe.DataRow<ApiResponse>.code: kotlin.Int? @JvmName("ApiResponse_code") get() = this["code"] as kotlin.Int?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<ApiResponse?>.code: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> @JvmName("NullableApiResponse_code") get() = this["code"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?>
            val org.jetbrains.kotlinx.dataframe.DataRow<ApiResponse?>.code: kotlin.Int? @JvmName("NullableApiResponse_code") get() = this["code"] as kotlin.Int?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<ApiResponse>.message: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("ApiResponse_message") get() = this["message"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<ApiResponse>.message: kotlin.String? @JvmName("ApiResponse_message") get() = this["message"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<ApiResponse?>.message: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableApiResponse_message") get() = this["message"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<ApiResponse?>.message: kotlin.String? @JvmName("NullableApiResponse_message") get() = this["message"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<ApiResponse>.type: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("ApiResponse_type") get() = this["type"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<ApiResponse>.type: kotlin.String? @JvmName("ApiResponse_type") get() = this["type"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<ApiResponse?>.type: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableApiResponse_type") get() = this["type"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<ApiResponse?>.type: kotlin.String? @JvmName("NullableApiResponse_type") get() = this["type"] as kotlin.String?
        """.trimLines()

        code should haveSubstring(apiResponseExtensions)

        @Language("kts") val res2 = execRaw("Pet.readJsonStr(\"\"\"$someAdvancedPets\"\"\")") as AnyFrame

        @Language("kts") val res3 = execRaw("Order.readJsonStr(\"\"\"$someAdvancedOrders\"\"\")") as AnyFrame

        shouldThrowAny {
            @Language("kts") val res4 = execRaw("Order.readJsonStr(\"\"\"$someAdvancedFailingOrders\"\"\")") as AnyFrame
            res4
        }
    }

    @Test
    fun `Other advanced test`() {
        val code = execGeneratedCode(advancedExample).declarations.trimLines()

        @Language("kt") val breedEnum = """
            enum class Breed(override val value: kotlin.String) : org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum {
                DINGO("Dingo"),
                HUSKY("Husky"),
                RETRIEVER("Retriever"),
                SHEPHERD("Shepherd");
            }
        """.trimLines()

        code should haveSubstring(breedEnum)

        @Language("kt") val dogInterface = """
            @DataSchema(isOpen = false)
            interface Dog : Pet {
                override val tag: kotlin.String
                val bark: kotlin.Boolean?
                val breed: Breed
                public companion object {
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToDog(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<Dog>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<Dog> = convertTo<Dog> { 
                        convertDataRowsWithOpenApi() 
                        convertTo()
                    }
        """.trimLines() // tag is nullable in Pet but required in Dog

        code should haveSubstring(dogInterface)

        @Language("kt") val dogExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Dog>.bark: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?> @JvmName("Dog_bark") get() = this["bark"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Dog>.bark: kotlin.Boolean? @JvmName("Dog_bark") get() = this["bark"] as kotlin.Boolean?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Dog?>.bark: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?> @JvmName("NullableDog_bark") get() = this["bark"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Dog?>.bark: kotlin.Boolean? @JvmName("NullableDog_bark") get() = this["bark"] as kotlin.Boolean?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Dog>.breed: org.jetbrains.kotlinx.dataframe.DataColumn<Breed> @JvmName("Dog_breed") get() = this["breed"] as org.jetbrains.kotlinx.dataframe.DataColumn<Breed>
            val org.jetbrains.kotlinx.dataframe.DataRow<Dog>.breed: Breed @JvmName("Dog_breed") get() = this["breed"] as Breed
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Dog?>.breed: org.jetbrains.kotlinx.dataframe.DataColumn<Breed?> @JvmName("NullableDog_breed") get() = this["breed"] as org.jetbrains.kotlinx.dataframe.DataColumn<Breed?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Dog?>.breed: Breed? @JvmName("NullableDog_breed") get() = this["breed"] as Breed?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Dog>.tag: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> @JvmName("Dog_tag") get() = this["tag"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
            val org.jetbrains.kotlinx.dataframe.DataRow<Dog>.tag: kotlin.String @JvmName("Dog_tag") get() = this["tag"] as kotlin.String
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Dog?>.tag: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableDog_tag") get() = this["tag"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Dog?>.tag: kotlin.String? @JvmName("NullableDog_tag") get() = this["tag"] as kotlin.String?
        """.trimLines()

        code should haveSubstring(dogExtensions)

        @Language("kt") val breed1Enum = """
            enum class Breed1(override val value: kotlin.String) : org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum {
                RAGDOLL("Ragdoll"),
                SHORTHAIR("Shorthair"),
                PERSIAN("Persian"),
                MAINE_COON("Maine Coon"),
                MAINE_COON_1("maine_coon"),
                EMPTY_STRING(""),
                `1`("1");
            }
        """.trimLines() // nullable enum, but taken care of in properties that use this enum

        code should haveSubstring(breed1Enum)

        @Language("kt") val catInterface = """
            @DataSchema(isOpen = false)
            interface Cat : Pet {
                val hunts: kotlin.Boolean?
                val age: kotlin.Float?
                val breed: Breed1?
                public companion object {
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToCat(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<Cat>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<Cat> = convertTo<Cat> { 
                          convertDataRowsWithOpenApi() 
                          convertTo()
                    }
        """.trimLines() // hunts is required but marked nullable, age is either integer or number, breed is nullable enum

        code should haveSubstring(catInterface)

        @Language("kt") val catExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Cat>.age: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Float?> @JvmName("Cat_age") get() = this["age"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Float?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Cat>.age: kotlin.Float? @JvmName("Cat_age") get() = this["age"] as kotlin.Float?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Cat?>.age: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Float?> @JvmName("NullableCat_age") get() = this["age"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Float?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Cat?>.age: kotlin.Float? @JvmName("NullableCat_age") get() = this["age"] as kotlin.Float?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Cat>.breed: org.jetbrains.kotlinx.dataframe.DataColumn<Breed1?> @JvmName("Cat_breed") get() = this["breed"] as org.jetbrains.kotlinx.dataframe.DataColumn<Breed1?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Cat>.breed: Breed1? @JvmName("Cat_breed") get() = this["breed"] as Breed1?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Cat?>.breed: org.jetbrains.kotlinx.dataframe.DataColumn<Breed1?> @JvmName("NullableCat_breed") get() = this["breed"] as org.jetbrains.kotlinx.dataframe.DataColumn<Breed1?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Cat?>.breed: Breed1? @JvmName("NullableCat_breed") get() = this["breed"] as Breed1?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Cat>.hunts: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?> @JvmName("Cat_hunts") get() = this["hunts"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Cat>.hunts: kotlin.Boolean? @JvmName("Cat_hunts") get() = this["hunts"] as kotlin.Boolean?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Cat?>.hunts: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?> @JvmName("NullableCat_hunts") get() = this["hunts"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Cat?>.hunts: kotlin.Boolean? @JvmName("NullableCat_hunts") get() = this["hunts"] as kotlin.Boolean?
        """.trimLines()

        code should haveSubstring(catExtensions)

        @Language("kt") val eyeColorEnum = """
            enum class EyeColor(override val value: kotlin.String) : org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum {
                BLUE("Blue"),
                YELLOW("Yellow"),
                BROWN("Brown"),
                GREEN("Green");
            }
        """.trimLines() // nullable enum, but taken care of in properties that use this enum

        code should haveSubstring(eyeColorEnum)

        @Language("kt") val petInterface = """
            @DataSchema(isOpen = false)
            interface Pet {
                @ColumnName("pet_type")
                val petType: kotlin.String
                @ColumnName("value")
                val `value`: kotlin.Any?
                val name: kotlin.String
                val tag: kotlin.String?
                val other: kotlin.Any?
                @ColumnName("eye_color")
                val eyeColor: EyeColor?
                public companion object {
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToPet(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<Pet>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<Pet> = convertTo<Pet> { 
                          convertDataRowsWithOpenApi() 
                          convertTo()
                      }
        """.trimLines() // petType was named pet_type, id is either Long or String, other is not integer, eyeColor is a required but nullable enum

        code should haveSubstring(petInterface)

        @Language("kt") val petExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> @JvmName("Pet_value") get() = this["value"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.`value`: kotlin.Any? @JvmName("Pet_value") get() = this["value"] as kotlin.Any?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> @JvmName("NullablePet_value") get() = this["value"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.`value`: kotlin.Any? @JvmName("NullablePet_value") get() = this["value"] as kotlin.Any?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.eyeColor: org.jetbrains.kotlinx.dataframe.DataColumn<EyeColor?> @JvmName("Pet_eyeColor") get() = this["eye_color"] as org.jetbrains.kotlinx.dataframe.DataColumn<EyeColor?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.eyeColor: EyeColor? @JvmName("Pet_eyeColor") get() = this["eye_color"] as EyeColor?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.eyeColor: org.jetbrains.kotlinx.dataframe.DataColumn<EyeColor?> @JvmName("NullablePet_eyeColor") get() = this["eye_color"] as org.jetbrains.kotlinx.dataframe.DataColumn<EyeColor?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.eyeColor: EyeColor? @JvmName("NullablePet_eyeColor") get() = this["eye_color"] as EyeColor?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> @JvmName("Pet_name") get() = this["name"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.name: kotlin.String @JvmName("Pet_name") get() = this["name"] as kotlin.String
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullablePet_name") get() = this["name"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.name: kotlin.String? @JvmName("NullablePet_name") get() = this["name"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.other: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> @JvmName("Pet_other") get() = this["other"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.other: kotlin.Any? @JvmName("Pet_other") get() = this["other"] as kotlin.Any?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.other: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> @JvmName("NullablePet_other") get() = this["other"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.other: kotlin.Any? @JvmName("NullablePet_other") get() = this["other"] as kotlin.Any?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.petType: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> @JvmName("Pet_petType") get() = this["pet_type"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.petType: kotlin.String @JvmName("Pet_petType") get() = this["pet_type"] as kotlin.String
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.petType: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullablePet_petType") get() = this["pet_type"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.petType: kotlin.String? @JvmName("NullablePet_petType") get() = this["pet_type"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet>.tag: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("Pet_tag") get() = this["tag"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet>.tag: kotlin.String? @JvmName("Pet_tag") get() = this["tag"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Pet?>.tag: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullablePet_tag") get() = this["tag"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Pet?>.tag: kotlin.String? @JvmName("NullablePet_tag") get() = this["tag"] as kotlin.String?
        """.trimLines()

        code should haveSubstring(petExtensions)

        @Language("kt") val petRefTypeAlias = """
            typealias PetRef = Pet
        """.trimLines() // is either Cat or Dog, we cannot merge objects, but they have the same ancestor, so Pet

        code should haveSubstring(petRefTypeAlias)

        @Language("kt") val alsoCatTypeAlias = """
            typealias AlsoCat = Cat
        """.trimLines()

        code should haveSubstring(alsoCatTypeAlias)

        @Language("kt") val integerTypeAlias = """
            typealias Integer = kotlin.Int
        """.trimLines()

        code should haveSubstring(integerTypeAlias)

        @Language("kt") val intListInterface = """
            @DataSchema(isOpen = false)
            interface IntList {
                val list: kotlin.collections.List<kotlin.Int>
                public companion object {
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToIntList(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<IntList>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<IntList> = convertTo<IntList> { 
                        convertDataRowsWithOpenApi() 
                        convertTo()
                    }
        """.trimLines() // array of primitives, so list int

        code should haveSubstring(intListInterface)

        @Language("kt") val intListExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<IntList>.list: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<kotlin.Int>> @JvmName("IntList_list") get() = this["list"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<kotlin.Int>>
            val org.jetbrains.kotlinx.dataframe.DataRow<IntList>.list: kotlin.collections.List<kotlin.Int> @JvmName("IntList_list") get() = this["list"] as kotlin.collections.List<kotlin.Int>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<IntList?>.list: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<kotlin.Int>?> @JvmName("NullableIntList_list") get() = this["list"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<kotlin.Int>?>
            val org.jetbrains.kotlinx.dataframe.DataRow<IntList?>.list: kotlin.collections.List<kotlin.Int>? @JvmName("NullableIntList_list") get() = this["list"] as kotlin.collections.List<kotlin.Int>?
        """.trimLines()

        code should haveSubstring(intListExtensions)

        @Language("kt")
        val objectWithAdditionalPropertiesInterface = """
            @DataSchema(isOpen = false)
            interface ObjectWithAdditionalProperties : org.jetbrains.kotlinx.dataframe.io.AdditionalProperty<kotlin.String> {
                @ColumnName("value")
                override val `value`: kotlin.String
                public companion object {
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToObjectWithAdditionalProperties(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<ObjectWithAdditionalProperties>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditionalProperties> = convertToAdditionalProperties<ObjectWithAdditionalProperties>() { 
                        convertDataRowsWithOpenApi() 
                        convertTo()
                    }
        """.trimLines()

        code should haveSubstring(objectWithAdditionalPropertiesInterface)

        @Language("kt")
        val objectWithAdditionalPropertiesExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<ObjectWithAdditionalProperties>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> @JvmName("ObjectWithAdditionalProperties_value") get() = this["value"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
            val org.jetbrains.kotlinx.dataframe.DataRow<ObjectWithAdditionalProperties>.`value`: kotlin.String @JvmName("ObjectWithAdditionalProperties_value") get() = this["value"] as kotlin.String
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<ObjectWithAdditionalProperties?>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableObjectWithAdditionalProperties_value") get() = this["value"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<ObjectWithAdditionalProperties?>.`value`: kotlin.String? @JvmName("NullableObjectWithAdditionalProperties_value") get() = this["value"] as kotlin.String?
        """.trimLines()

        code should haveSubstring(objectWithAdditionalPropertiesExtensions)

        @Language("kt")
        val objectWithAdditional2Interface = """
            @DataSchema(isOpen = false)
            interface ObjectWithAdditional2 : org.jetbrains.kotlinx.dataframe.io.AdditionalProperty<kotlin.Any> {
                @ColumnName("value")
                override val `value`: kotlin.Any
                public companion object {
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToObjectWithAdditional2(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<ObjectWithAdditional2>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional2> = convertToAdditionalProperties<ObjectWithAdditional2>() { 
                        convertDataRowsWithOpenApi() 
                        convertTo()
                    }
        """.trimLines()

        code should haveSubstring(objectWithAdditional2Interface)

        @Language("kt")
        val objectWithAdditional2Extensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<ObjectWithAdditional2>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any> @JvmName("ObjectWithAdditional2_value") get() = this["value"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any>
            val org.jetbrains.kotlinx.dataframe.DataRow<ObjectWithAdditional2>.`value`: kotlin.Any @JvmName("ObjectWithAdditional2_value") get() = this["value"] as kotlin.Any
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<ObjectWithAdditional2?>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> @JvmName("NullableObjectWithAdditional2_value") get() = this["value"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?>
            val org.jetbrains.kotlinx.dataframe.DataRow<ObjectWithAdditional2?>.`value`: kotlin.Any? @JvmName("NullableObjectWithAdditional2_value") get() = this["value"] as kotlin.Any?
        """.trimLines()

        code should haveSubstring(objectWithAdditional2Extensions)

        @Language("kt")
        val objectWithAdditional3Interface = """
            @DataSchema(isOpen = false)
            interface ObjectWithAdditional3 : org.jetbrains.kotlinx.dataframe.io.AdditionalProperty<kotlin.Any?> {
                @ColumnName("value")
                override val `value`: kotlin.Any?
                public companion object {
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToObjectWithAdditional3(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<ObjectWithAdditional3>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional3> = convertToAdditionalProperties<ObjectWithAdditional3>() { 
                        convertDataRowsWithOpenApi() 
                        convertTo()
                    }
        """.trimLines()

        code should haveSubstring(objectWithAdditional3Interface)

        @Language("kt")
        val objectWithAdditional3Extensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<ObjectWithAdditional3>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> @JvmName("ObjectWithAdditional3_value") get() = this["value"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?>
            val org.jetbrains.kotlinx.dataframe.DataRow<ObjectWithAdditional3>.`value`: kotlin.Any? @JvmName("ObjectWithAdditional3_value") get() = this["value"] as kotlin.Any?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<ObjectWithAdditional3?>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> @JvmName("NullableObjectWithAdditional3_value") get() = this["value"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?>
            val org.jetbrains.kotlinx.dataframe.DataRow<ObjectWithAdditional3?>.`value`: kotlin.Any? @JvmName("NullableObjectWithAdditional3_value") get() = this["value"] as kotlin.Any?
        """.trimLines()

        code should haveSubstring(objectWithAdditional3Extensions)

        // TODO broke PetRef?
        @Language("kt") val errorInterface = """
            @DataSchema(isOpen = false)
            interface Error {
                val ints: IntList?
                val petRef: PetRef
                val pets: org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?>
                val code: kotlin.Int
                val message: kotlin.String
                val objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditionalProperties>
                val objectWithAdditional2: org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional2?>
                val objectWithAdditional3: org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional3>
                val array: kotlin.collections.List<SomeArrayArray>?
                public companion object {
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToError(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<Error>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<Error> = convertTo<Error> { 
                        convertDataRowsWithOpenApi() 
                        convertTo()
                    }
        """.trimLines()

        code should haveSubstring(errorInterface)

        @Language("kt") val errorExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error>.code: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int> @JvmName("Error_code") get() = this["code"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error>.code: kotlin.Int @JvmName("Error_code") get() = this["code"] as kotlin.Int
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error?>.code: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> @JvmName("NullableError_code") get() = this["code"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error?>.code: kotlin.Int? @JvmName("NullableError_code") get() = this["code"] as kotlin.Int?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error>.ints: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<IntList?> @JvmName("Error_ints") get() = this["ints"] as org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<IntList?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error>.ints: org.jetbrains.kotlinx.dataframe.DataRow<IntList?> @JvmName("Error_ints") get() = this["ints"] as org.jetbrains.kotlinx.dataframe.DataRow<IntList?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error?>.ints: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<IntList?> @JvmName("NullableError_ints") get() = this["ints"] as org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<IntList?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error?>.ints: org.jetbrains.kotlinx.dataframe.DataRow<IntList?> @JvmName("NullableError_ints") get() = this["ints"] as org.jetbrains.kotlinx.dataframe.DataRow<IntList?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error>.message: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> @JvmName("Error_message") get() = this["message"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error>.message: kotlin.String @JvmName("Error_message") get() = this["message"] as kotlin.String
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error?>.message: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableError_message") get() = this["message"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error?>.message: kotlin.String? @JvmName("NullableError_message") get() = this["message"] as kotlin.String?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error>.objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditionalProperties>> @JvmName("Error_objectWithAdditional") get() = this["objectWithAdditional"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditionalProperties>>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error>.objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditionalProperties> @JvmName("Error_objectWithAdditional") get() = this["objectWithAdditional"] as org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditionalProperties>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error?>.objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditionalProperties?>> @JvmName("NullableError_objectWithAdditional") get() = this["objectWithAdditional"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditionalProperties?>>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error?>.objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditionalProperties?> @JvmName("NullableError_objectWithAdditional") get() = this["objectWithAdditional"] as org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditionalProperties?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error>.objectWithAdditional2: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional2?>> @JvmName("Error_objectWithAdditional2") get() = this["objectWithAdditional2"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional2?>>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error>.objectWithAdditional2: org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional2?> @JvmName("Error_objectWithAdditional2") get() = this["objectWithAdditional2"] as org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional2?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error?>.objectWithAdditional2: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional2?>> @JvmName("NullableError_objectWithAdditional2") get() = this["objectWithAdditional2"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional2?>>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error?>.objectWithAdditional2: org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional2?> @JvmName("NullableError_objectWithAdditional2") get() = this["objectWithAdditional2"] as org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional2?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error>.objectWithAdditional3: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional3>> @JvmName("Error_objectWithAdditional3") get() = this["objectWithAdditional3"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional3>>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error>.objectWithAdditional3: org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional3> @JvmName("Error_objectWithAdditional3") get() = this["objectWithAdditional3"] as org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional3>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error?>.objectWithAdditional3: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional3?>> @JvmName("NullableError_objectWithAdditional3") get() = this["objectWithAdditional3"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional3?>>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error?>.objectWithAdditional3: org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional3?> @JvmName("NullableError_objectWithAdditional3") get() = this["objectWithAdditional3"] as org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional3?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error>.petRef: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<PetRef> @JvmName("Error_petRef") get() = this["petRef"] as org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<PetRef>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error>.petRef: org.jetbrains.kotlinx.dataframe.DataRow<PetRef> @JvmName("Error_petRef") get() = this["petRef"] as org.jetbrains.kotlinx.dataframe.DataRow<PetRef>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error?>.petRef: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<PetRef?> @JvmName("NullableError_petRef") get() = this["petRef"] as org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<PetRef?>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error?>.petRef: org.jetbrains.kotlinx.dataframe.DataRow<PetRef?> @JvmName("NullableError_petRef") get() = this["petRef"] as org.jetbrains.kotlinx.dataframe.DataRow<PetRef?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error>.pets: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?>> @JvmName("Error_pets") get() = this["pets"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?>>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error>.pets: org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?> @JvmName("Error_pets") get() = this["pets"] as org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Error?>.pets: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?>> @JvmName("NullableError_pets") get() = this["pets"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?>>
            val org.jetbrains.kotlinx.dataframe.DataRow<Error?>.pets: org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?> @JvmName("NullableError_pets") get() = this["pets"] as org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?>
        """.trimLines()

        code should haveSubstring(errorExtensions)

        @Language("kt") val valueInterface = """
            @DataSchema(isOpen = false)
            interface Value
        """.trimLines()

        code should haveSubstring(valueInterface)

        @Language("kt") val someArrayContentInterface = """
            @DataSchema(isOpen = false)
            interface SomeArrayContent {
                val op: Op
                val path: kotlin.String
                @ColumnName("value")
                val `value`: Value?
                public companion object {
                   public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToSomeArrayContent(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<SomeArrayContent>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<SomeArrayContent> = convertTo<SomeArrayContent> { 
                       convertDataRowsWithOpenApi() 
                       convertTo()
                   }
        """.trimLines()

        code should haveSubstring(someArrayContentInterface)

        @Language("kt") val someArrayContentExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<SomeArrayContent>.`value`: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<Value?> @JvmName("SomeArrayContent_value") get() = this["value"] as org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<Value?>
            val org.jetbrains.kotlinx.dataframe.DataRow<SomeArrayContent>.`value`: org.jetbrains.kotlinx.dataframe.DataRow<Value?> @JvmName("SomeArrayContent_value") get() = this["value"] as org.jetbrains.kotlinx.dataframe.DataRow<Value?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<SomeArrayContent?>.`value`: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<Value?> @JvmName("NullableSomeArrayContent_value") get() = this["value"] as org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<Value?>
            val org.jetbrains.kotlinx.dataframe.DataRow<SomeArrayContent?>.`value`: org.jetbrains.kotlinx.dataframe.DataRow<Value?> @JvmName("NullableSomeArrayContent_value") get() = this["value"] as org.jetbrains.kotlinx.dataframe.DataRow<Value?>
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<SomeArrayContent>.op: org.jetbrains.kotlinx.dataframe.DataColumn<Op> @JvmName("SomeArrayContent_op") get() = this["op"] as org.jetbrains.kotlinx.dataframe.DataColumn<Op>
            val org.jetbrains.kotlinx.dataframe.DataRow<SomeArrayContent>.op: Op @JvmName("SomeArrayContent_op") get() = this["op"] as Op
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<SomeArrayContent?>.op: org.jetbrains.kotlinx.dataframe.DataColumn<Op?> @JvmName("NullableSomeArrayContent_op") get() = this["op"] as org.jetbrains.kotlinx.dataframe.DataColumn<Op?>
            val org.jetbrains.kotlinx.dataframe.DataRow<SomeArrayContent?>.op: Op? @JvmName("NullableSomeArrayContent_op") get() = this["op"] as Op?
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<SomeArrayContent>.path: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> @JvmName("SomeArrayContent_path") get() = this["path"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
            val org.jetbrains.kotlinx.dataframe.DataRow<SomeArrayContent>.path: kotlin.String @JvmName("SomeArrayContent_path") get() = this["path"] as kotlin.String
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<SomeArrayContent?>.path: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> @JvmName("NullableSomeArrayContent_path") get() = this["path"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?>
            val org.jetbrains.kotlinx.dataframe.DataRow<SomeArrayContent?>.path: kotlin.String? @JvmName("NullableSomeArrayContent_path") get() = this["path"] as kotlin.String?
        """.trimLines()

        code should haveSubstring(someArrayContentExtensions)

        @Language("kt") val someArrayTypeAlias = """
            typealias SomeArray = org.jetbrains.kotlinx.dataframe.DataFrame<SomeArrayContent>
        """.trimLines()

        code should haveSubstring(someArrayTypeAlias)

        @Language("kts") val res1 = execRaw(
            "Pet.readJsonStr(\"\"\"$advancedData\"\"\").filter { petType == \"Cat\" }.convertTo<Cat>(ExcessiveColumns.Remove)"
        ) as AnyFrame
        val res1Schema = res1.schema()

        @Language("kts") val res2 = execRaw(
            "Pet.readJsonStr(\"\"\"$advancedData\"\"\").filter { petType == \"Dog\" }.convertTo<Dog>(ExcessiveColumns.Remove)"
        ) as AnyFrame
        val res2Schema = res2.schema()

        @Language("kts") val res3 = execRaw(
            "Error.readJsonStr(\"\"\"$advancedDataError\"\"\")"
        ) as AnyFrame
        val res3Schema = res3.schema()
    }

    @Test
    fun `Apis guru Test`() {
        val df = APIs.readJsonStr(apiGuruData)
            .alsoDebug(rowsLimit = Int.MAX_VALUE)
    }

//    @Test
//    fun `MLC Test`() {
//        val url =
//            "https://us-central1-jrclockwidget.cloudfunctions.net/getGroupData/1544198656898889/peopleWithLocation?&apiKey=wachtwoord&type=json"
//
//        DataFrame.read(url).alsoDebug()
//        val df = PeopleWithLocation.readJson(url)
//            .filter { (value.gps.latitude ?: 0.0) > 0.0 }
//            .alsoDebug("first mlc:")
//    }

    enum class EyeColor(override val value: String) : org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum {
        BLUE("Blue"), YELLOW("Yellow"), BROWN("Brown"), GREEN("Green");
    }

    @DataSchema(isOpen = false)
    interface Pet {
        @ColumnName("pet_type")
        val petType: String

        @ColumnName("value")
        val `value`: Any?
        val name: String
        val tag: String?
        val other: Any?

        @ColumnName("eye_color")
        val eyeColor: EyeColor?

        companion object {
            fun DataFrame<*>.convertToPet(convertTo: ConvertSchemaDsl<Pet>.() -> Unit = {}): DataFrame<Pet> =
                convertTo<Pet> {
                    convertDataRowsWithOpenApi()
                    convertTo()
                }

            fun readJson(url: URL): DataFrame<Pet> =
                DataFrame.readJson(url, typeClashTactic = ANY_COLUMNS).convertToPet()

            fun readJson(path: String): DataFrame<Pet> =
                DataFrame.readJson(path, typeClashTactic = ANY_COLUMNS).convertToPet()

            fun readJson(stream: InputStream): DataFrame<Pet> =
                DataFrame.readJson(stream, typeClashTactic = ANY_COLUMNS).convertToPet()

            fun readJsonStr(text: String): DataFrame<Pet> =
                DataFrame.readJsonStr(text, typeClashTactic = ANY_COLUMNS).convertToPet()
        }
    }

    @DataSchema(isOpen = false)
    interface IntList {
        val list: List<Int>

        companion object {

            fun DataFrame<*>.convertToIntList(convertTo: ConvertSchemaDsl<IntList>.() -> Unit = {}): DataFrame<IntList> =
                convertTo {
                    convertDataRowsWithOpenApi()
                    convertTo()
                }

            fun readJson(url: URL): DataFrame<IntList> =
                DataFrame.readJson(url, typeClashTactic = ANY_COLUMNS).convertToIntList()

            fun readJson(path: String): DataFrame<IntList> =
                DataFrame.readJson(path, typeClashTactic = ANY_COLUMNS).convertToIntList()

            fun readJson(stream: InputStream): DataFrame<IntList> =
                DataFrame.readJson(stream, typeClashTactic = ANY_COLUMNS).convertToIntList()

            fun readJsonStr(text: String): DataFrame<IntList> =
                DataFrame.readJsonStr(text, typeClashTactic = ANY_COLUMNS).convertToIntList()
        }
    }

    @DataSchema(isOpen = false)
    interface ObjectWithAdditionalProperties : AdditionalProperty<String> {
        @ColumnName("value")
        override val `value`: String

        companion object {
            fun DataFrame<*>.convertToObjectWithAdditionalProperties(
                convertTo: ConvertSchemaDsl<ObjectWithAdditionalProperties>.() -> Unit = {},
            ): DataFrame<ObjectWithAdditionalProperties> = convertTo {
                convertDataRowsWithOpenApi()
                convertTo()
            }

            fun readJson(url: URL): DataFrame<ObjectWithAdditionalProperties> =
                DataFrame.readJson(url, typeClashTactic = ANY_COLUMNS).convertToObjectWithAdditionalProperties()

            fun readJson(path: String): DataFrame<ObjectWithAdditionalProperties> =
                DataFrame.readJson(path, typeClashTactic = ANY_COLUMNS).convertToObjectWithAdditionalProperties()

            fun readJson(stream: InputStream): DataFrame<ObjectWithAdditionalProperties> =
                DataFrame.readJson(stream, typeClashTactic = ANY_COLUMNS).convertToObjectWithAdditionalProperties()

            fun readJsonStr(text: String): DataFrame<ObjectWithAdditionalProperties> =
                DataFrame.readJsonStr(text, typeClashTactic = ANY_COLUMNS).convertToObjectWithAdditionalProperties()
        }
    }

    @DataSchema(isOpen = false)
    interface ObjectWithAdditional2 : AdditionalProperty<Any> {
        @ColumnName("value")
        override val `value`: Any

        companion object {
            fun DataFrame<*>.convertToObjectWithAdditional2(
                convertTo: ConvertSchemaDsl<ObjectWithAdditional2>.() -> Unit = {},
            ): DataFrame<ObjectWithAdditional2> = convertTo<ObjectWithAdditional2> {
                convertDataRowsWithOpenApi()
                convertTo()
            }

            fun readJson(url: URL): DataFrame<ObjectWithAdditional2> =
                DataFrame.readJson(url, typeClashTactic = ANY_COLUMNS).convertToObjectWithAdditional2()

            fun readJson(path: String): DataFrame<ObjectWithAdditional2> =
                DataFrame.readJson(path, typeClashTactic = ANY_COLUMNS).convertToObjectWithAdditional2()

            fun readJson(stream: InputStream): DataFrame<ObjectWithAdditional2> =
                DataFrame.readJson(stream, typeClashTactic = ANY_COLUMNS).convertToObjectWithAdditional2()

            fun readJsonStr(text: String): DataFrame<ObjectWithAdditional2> =
                DataFrame.readJsonStr(text, typeClashTactic = ANY_COLUMNS).convertToObjectWithAdditional2()
        }
    }

    @DataSchema(isOpen = false)
    interface ObjectWithAdditional3 : AdditionalProperty<Any?> {
        @ColumnName("value")
        override val `value`: Any?

        companion object {
            fun DataFrame<*>.convertToObjectWithAdditional3(
                convertTo: ConvertSchemaDsl<ObjectWithAdditional3>.() -> Unit = {},
            ): DataFrame<ObjectWithAdditional3> = convertTo<ObjectWithAdditional3> {
                convertDataRowsWithOpenApi()
                convertTo()
            }

            fun readJson(url: URL): DataFrame<ObjectWithAdditional3> =
                DataFrame.readJson(url, typeClashTactic = ANY_COLUMNS).convertToObjectWithAdditional3()

            fun readJson(path: String): DataFrame<ObjectWithAdditional3> =
                DataFrame.readJson(path, typeClashTactic = ANY_COLUMNS).convertToObjectWithAdditional3()

            fun readJson(stream: InputStream): DataFrame<ObjectWithAdditional3> =
                DataFrame.readJson(stream, typeClashTactic = ANY_COLUMNS).convertToObjectWithAdditional3()

            fun readJsonStr(text: String): DataFrame<ObjectWithAdditional3> =
                DataFrame.readJsonStr(text, typeClashTactic = ANY_COLUMNS).convertToObjectWithAdditional3()
        }
    }

    @DataSchema(isOpen = false)
    interface Error {
        val ints: IntList?
        val petRef: PetRef
        val pets: DataFrame<Any?>
        val code: Int
        val message: String
        val objectWithAdditional: DataFrame<ObjectWithAdditionalProperties>
        val objectWithAdditional2: DataFrame<ObjectWithAdditional2?>
        val objectWithAdditional3: DataFrame<ObjectWithAdditional3>
        val array: List<SomeArrayArray?>

        companion object {
            fun DataFrame<*>.convertToError(convertTo: ConvertSchemaDsl<Error>.() -> Unit = {}): DataFrame<Error> =
                convertTo<Error> {
                    convertDataRowsWithOpenApi()
                    convertTo()
                }

            fun readJson(url: URL): DataFrame<Error> =
                DataFrame.readJson(url, typeClashTactic = ANY_COLUMNS).convertToError()

            fun readJson(path: String): DataFrame<Error> =
                DataFrame.readJson(path, typeClashTactic = ANY_COLUMNS).convertToError()

            fun readJson(stream: InputStream): DataFrame<Error> =
                DataFrame.readJson(stream, typeClashTactic = ANY_COLUMNS)
                    .convertToError()

            fun readJsonStr(text: String): DataFrame<Error> =
                DataFrame.readJsonStr(text, typeClashTactic = ANY_COLUMNS)
                    .convertToError()
        }
    }

    enum class Op(override val value: String) : org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum {
        ADD("add"),
        REMOVE("remove"),
        REPLACE("replace");
    }

    @DataSchema(isOpen = false)
    interface Value

    @DataSchema(isOpen = false)
    interface SomeArrayContent {
        val op: Op
        val path: String

        @ColumnName("value")
        val `value`: Value?

        companion object {
            fun DataFrame<*>.convertToSomeArrayContent(convertTo: ConvertSchemaDsl<SomeArrayContent>.() -> Unit = {}): DataFrame<SomeArrayContent> =
                convertTo<SomeArrayContent> {
                    convertDataRowsWithOpenApi()
                    convertTo()
                }

            fun readJson(url: URL): DataFrame<SomeArrayContent> =
                DataFrame.readJson(url, typeClashTactic = ANY_COLUMNS)
                    .convertToSomeArrayContent()

            fun readJson(path: String): DataFrame<SomeArrayContent> =
                DataFrame.readJson(path, typeClashTactic = ANY_COLUMNS)
                    .convertToSomeArrayContent()

            fun readJson(stream: InputStream): DataFrame<SomeArrayContent> =
                DataFrame.readJson(stream, typeClashTactic = ANY_COLUMNS)
                    .convertToSomeArrayContent()

            fun readJsonStr(text: String): DataFrame<SomeArrayContent> =
                DataFrame.readJsonStr(text, typeClashTactic = ANY_COLUMNS)
                    .convertToSomeArrayContent()
        }
    }

    fun ConvertSchemaDsl<*>.convertDataRowsWithOpenApi() {
        // TODO convert DataRow
    }

    @Test
    fun main() {
        val df = DataFrame.readJsonStr(OpenApiTests().advancedDataError, typeClashTactic = ANY_COLUMNS)
            .alsoDebug("error data:")

        val error = df.convertToError().alsoDebug("result:")
    }

    private fun String.trimLines(): String = trim().removeSurrounding("\n").lines().joinToString("\n") { it.trim() }
}

typealias PetRef = OpenApiTests.Pet
typealias SomeArray = DataFrame<OpenApiTests.SomeArrayContent>
typealias SomeArrayArray = List<SomeArray>
