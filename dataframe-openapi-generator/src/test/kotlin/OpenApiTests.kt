import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.should
import io.kotest.matchers.string.haveSubstring
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.isNotEmpty
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.codeGen.Code
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName
import org.jetbrains.kotlinx.dataframe.io.OpenApi
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.io.File.separatorChar
import java.io.InputStream

class OpenApiTests : JupyterReplTestCase() {

    private val openApi = OpenApi()
    private val additionalImports = openApi.createDefaultReadMethod().additionalImports.joinToString("\n")

    private fun execGeneratedCode(code: List<Code>): Code {
        execSuccess(additionalImports)
        code.forEach { snippet ->
            execSuccess(snippet)
        }
        return code.joinToString("\n")
    }

    private fun execGeneratedCode(file: File, name: String) =
        execGeneratedCode(
            code = openApi.readCodeForGeneration(
                file = file,
                name = name,
                extensionProperties = true,
                generateHelperCompanionObject = false,
            ),
        )

    private fun execGeneratedCode(stream: InputStream, name: String) =
        execGeneratedCode(
            code = openApi.readCodeForGeneration(
                stream = stream,
                name = name,
                extensionProperties = true,
                generateHelperCompanionObject = false,
            ),
        )

    private fun execGeneratedCode(text: String, name: String) =
        execGeneratedCode(
            code = openApi.readCodeForGeneration(
                text = text,
                name = name,
                extensionProperties = true,
                generateHelperCompanionObject = false,
            ),
        )

    private val petstoreJson = File("src/test/resources/petstore.json")
    private val petstoreAdvancedJson = File("src/test/resources/petstore_advanced.json")
    private val petstoreYaml = File("src/test/resources/petstore.yaml")
    private val someAdvancedPetsData = File("src/test/resources/some_advanced_pets.json").readText()
    private val someAdvancedOrdersData = File("src/test/resources/some_advanced_orders.json").readText()
    private val someAdvancedFailingOrdersData = File("src/test/resources/some_advanced_failing_orders.json").readText()
    private val advancedExample = File("src/test/resources/openapi_advanced_example.yaml")
    private val advancedData = File("src/test/resources/openapi_advanced_data.json").readText()
    private val advancedErrorData = File("src/test/resources/openapi_advanced_data2.json").readText()
    private val advancedErrorHolderData = File("src/test/resources/openapi_advanced_data3.json").readText()
    private val apiGuruYaml = File("src/test/resources/ApiGuruOpenApi.yaml")
    private val apiGuruData = File("src/test/resources/ApiGuruSample.json").readText()
    private val mlcYaml = File("src/test/resources/MlcGroupDataOpenApi.yaml")
    private val mlcLocationsWithPeopleData = File("src/test/resources/mlc_locations_with_people_data.json").readText()
    private val mlcPeopleWithLocationData = File("src/test/resources/mlc_people_with_location_data.json").readText()

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
        val fullFunctionName = ValidFieldName.of(::simpleTest.name)
        val functionName = fullFunctionName.quotedIfNeeded
        val code = execGeneratedCode(file, fullFunctionName.unquoted).trimLines()

        @Language("kt")
        val petInterface = """
            @DataSchema(isOpen = false)
            interface Pet {
                val id: kotlin.Long
                val name: kotlin.String
                val tag: kotlin.String?
                public companion object {
        """.trimLines()

        code should haveSubstring(petInterface)

        @Language("kt")
        val petExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<simpleTest.Pet>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long> by ColumnsContainerGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<simpleTest.Pet>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> by ColumnsContainerGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<simpleTest.Pet>.tag: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("tag")
            val org.jetbrains.kotlinx.dataframe.DataRow<simpleTest.Pet>.id: kotlin.Long by DataRowGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.DataRow<simpleTest.Pet>.name: kotlin.String by DataRowGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.DataRow<simpleTest.Pet>.tag: kotlin.String? by DataRowGeneratedPropertyDelegate("tag")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<simpleTest.Pet?>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> by ColumnsContainerGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<simpleTest.Pet?>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<simpleTest.Pet?>.tag: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("tag")
            val org.jetbrains.kotlinx.dataframe.DataRow<simpleTest.Pet?>.id: kotlin.Long? by DataRowGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.DataRow<simpleTest.Pet?>.name: kotlin.String? by DataRowGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.DataRow<simpleTest.Pet?>.tag: kotlin.String? by DataRowGeneratedPropertyDelegate("tag")
        """.trimLines()

        code should haveSubstring(petExtensions)

        @Language("kt")
        val petsTypeAlias = """
            typealias Pets = org.jetbrains.kotlinx.dataframe.DataFrame<$functionName.Pet>
        """.trimLines()

        code should haveSubstring(petsTypeAlias)

        @Language("kt")
        val errorInterface = """
            @DataSchema(isOpen = false)
            interface Error {
                val code: kotlin.Int
                val message: kotlin.String
                public companion object {
        """.trimLines()

        code should haveSubstring(errorInterface)

        @Language("kt")
        val errorExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<simpleTest.Error>.code: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int> by ColumnsContainerGeneratedPropertyDelegate("code")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<simpleTest.Error>.message: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> by ColumnsContainerGeneratedPropertyDelegate("message")
            val org.jetbrains.kotlinx.dataframe.DataRow<simpleTest.Error>.code: kotlin.Int by DataRowGeneratedPropertyDelegate("code")
            val org.jetbrains.kotlinx.dataframe.DataRow<simpleTest.Error>.message: kotlin.String by DataRowGeneratedPropertyDelegate("message")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<simpleTest.Error?>.code: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> by ColumnsContainerGeneratedPropertyDelegate("code")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<simpleTest.Error?>.message: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("message")
            val org.jetbrains.kotlinx.dataframe.DataRow<simpleTest.Error?>.code: kotlin.Int? by DataRowGeneratedPropertyDelegate("code")
            val org.jetbrains.kotlinx.dataframe.DataRow<simpleTest.Error?>.message: kotlin.String? by DataRowGeneratedPropertyDelegate("message")
        """.trimLines()

        code should haveSubstring(errorExtensions)

        @Language("kts")
        val res2 = execRaw("$functionName.Pet.readJsonStr($somePetsTripleQuotes)") as AnyFrame
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
        val fullFunctionName = ValidFieldName.of(::`Advanced test Petstore Json`.name)
        val functionName = fullFunctionName.quotedIfNeeded

        val code = execGeneratedCode(
            file = petstoreAdvancedJson,
            name = fullFunctionName.unquoted,
        ).trimLines()

        @Language("kts")
        val statusInterface = """
            enum class Status(override val value: kotlin.String) : org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum {
                PLACED("placed"),
                APPROVED("approved"),
                DELIVERED("delivered");
            }
        """.trimLines()

        code should haveSubstring(statusInterface)

        @Language("kt")
        val orderInterface = """
            @DataSchema(isOpen = false)
            interface Order {
                val id: kotlin.Long?
                val petId: kotlin.Long?
                val quantity: kotlin.Int?
                val shipDate: kotlinx.datetime.LocalDateTime?
                val status: $functionName.Status?
                val complete: kotlin.Boolean?
                public companion object {
        """.trimLines()

        code should haveSubstring(orderInterface)

        @Language("kt")
        val customerInterface = """
            @DataSchema(isOpen = false)
            interface Customer {
                val id: kotlin.Long?
                val username: kotlin.String?
                val address: org.jetbrains.kotlinx.dataframe.DataFrame<$functionName.Address?>
                public companion object {
        """.trimLines() // address is a nullable array of objects -> DataFrame<Address?>

        code should haveSubstring(customerInterface)

        @Language("kt")
        val customerExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Customer>.address: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Advanced test Petstore Json`.Address?>> by ColumnsContainerGeneratedPropertyDelegate("address")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Customer>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> by ColumnsContainerGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Customer>.username: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("username")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Customer>.address: org.jetbrains.kotlinx.dataframe.DataFrame<`Advanced test Petstore Json`.Address?> by DataRowGeneratedPropertyDelegate("address")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Customer>.id: kotlin.Long? by DataRowGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Customer>.username: kotlin.String? by DataRowGeneratedPropertyDelegate("username")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Customer?>.address: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Advanced test Petstore Json`.Address?>> by ColumnsContainerGeneratedPropertyDelegate("address")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Customer?>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> by ColumnsContainerGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Customer?>.username: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("username")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Customer?>.address: org.jetbrains.kotlinx.dataframe.DataFrame<`Advanced test Petstore Json`.Address?> by DataRowGeneratedPropertyDelegate("address")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Customer?>.id: kotlin.Long? by DataRowGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Customer?>.username: kotlin.String? by DataRowGeneratedPropertyDelegate("username")
        """.trimLines()

        code should haveSubstring(customerExtensions)

        @Language("kt")
        val orderExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Order>.complete: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?> by ColumnsContainerGeneratedPropertyDelegate("complete")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Order>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> by ColumnsContainerGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Order>.petId: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> by ColumnsContainerGeneratedPropertyDelegate("petId")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Order>.quantity: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> by ColumnsContainerGeneratedPropertyDelegate("quantity")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Order>.shipDate: org.jetbrains.kotlinx.dataframe.DataColumn<kotlinx.datetime.LocalDateTime?> by ColumnsContainerGeneratedPropertyDelegate("shipDate")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Order>.status: org.jetbrains.kotlinx.dataframe.DataColumn<`Advanced test Petstore Json`.Status?> by ColumnsContainerGeneratedPropertyDelegate("status")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Order>.complete: kotlin.Boolean? by DataRowGeneratedPropertyDelegate("complete")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Order>.id: kotlin.Long? by DataRowGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Order>.petId: kotlin.Long? by DataRowGeneratedPropertyDelegate("petId")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Order>.quantity: kotlin.Int? by DataRowGeneratedPropertyDelegate("quantity")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Order>.shipDate: kotlinx.datetime.LocalDateTime? by DataRowGeneratedPropertyDelegate("shipDate")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Order>.status: `Advanced test Petstore Json`.Status? by DataRowGeneratedPropertyDelegate("status")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Order?>.complete: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?> by ColumnsContainerGeneratedPropertyDelegate("complete")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Order?>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> by ColumnsContainerGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Order?>.petId: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> by ColumnsContainerGeneratedPropertyDelegate("petId")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Order?>.quantity: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> by ColumnsContainerGeneratedPropertyDelegate("quantity")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Order?>.shipDate: org.jetbrains.kotlinx.dataframe.DataColumn<kotlinx.datetime.LocalDateTime?> by ColumnsContainerGeneratedPropertyDelegate("shipDate")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Order?>.status: org.jetbrains.kotlinx.dataframe.DataColumn<`Advanced test Petstore Json`.Status?> by ColumnsContainerGeneratedPropertyDelegate("status")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Order?>.complete: kotlin.Boolean? by DataRowGeneratedPropertyDelegate("complete")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Order?>.id: kotlin.Long? by DataRowGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Order?>.petId: kotlin.Long? by DataRowGeneratedPropertyDelegate("petId")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Order?>.quantity: kotlin.Int? by DataRowGeneratedPropertyDelegate("quantity")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Order?>.shipDate: kotlinx.datetime.LocalDateTime? by DataRowGeneratedPropertyDelegate("shipDate")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Order?>.status: `Advanced test Petstore Json`.Status? by DataRowGeneratedPropertyDelegate("status")
        """.trimLines()

        code should haveSubstring(orderExtensions)

        @Language("kt")
        val addressInterface = """
            @DataSchema(isOpen = false)
            interface Address {
                val street: kotlin.String?
                val city: kotlin.String?
                val state: kotlin.String?
                val zip: kotlin.String?
                public companion object {
        """.trimLines()

        code should haveSubstring(addressInterface)

        @Language("kt")
        val addressExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Address>.city: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("city")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Address>.state: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("state")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Address>.street: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("street")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Address>.zip: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("zip")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Address>.city: kotlin.String? by DataRowGeneratedPropertyDelegate("city")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Address>.state: kotlin.String? by DataRowGeneratedPropertyDelegate("state")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Address>.street: kotlin.String? by DataRowGeneratedPropertyDelegate("street")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Address>.zip: kotlin.String? by DataRowGeneratedPropertyDelegate("zip")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Address?>.city: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("city")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Address?>.state: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("state")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Address?>.street: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("street")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Address?>.zip: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("zip")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Address?>.city: kotlin.String? by DataRowGeneratedPropertyDelegate("city")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Address?>.state: kotlin.String? by DataRowGeneratedPropertyDelegate("state")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Address?>.street: kotlin.String? by DataRowGeneratedPropertyDelegate("street")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Address?>.zip: kotlin.String? by DataRowGeneratedPropertyDelegate("zip")
        """.trimLines()

        code should haveSubstring(addressExtensions)

        @Language("kt")
        val categoryInterface = """
            @DataSchema(isOpen = false)
            interface Category {
                val id: kotlin.Long?
                val name: kotlin.String?
                public companion object {
        """.trimLines()

        code should haveSubstring(categoryInterface)

        @Language("kt")
        val categoryExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Category>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> by ColumnsContainerGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Category>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Category>.id: kotlin.Long? by DataRowGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Category>.name: kotlin.String? by DataRowGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Category?>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> by ColumnsContainerGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Category?>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Category?>.id: kotlin.Long? by DataRowGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Category?>.name: kotlin.String? by DataRowGeneratedPropertyDelegate("name")

        """.trimLines()

        code should haveSubstring(categoryExtensions)

        @Language("kt")
        val userInterface = """
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

        @Language("kt")
        val userExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User>.email: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("email")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User>.firstName: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("firstName")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> by ColumnsContainerGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User>.lastName: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("lastName")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User>.password: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("password")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User>.phone: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("phone")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User>.userStatus: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> by ColumnsContainerGeneratedPropertyDelegate("userStatus")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User>.username: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("username")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User>.email: kotlin.String? by DataRowGeneratedPropertyDelegate("email")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User>.firstName: kotlin.String? by DataRowGeneratedPropertyDelegate("firstName")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User>.id: kotlin.Long? by DataRowGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User>.lastName: kotlin.String? by DataRowGeneratedPropertyDelegate("lastName")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User>.password: kotlin.String? by DataRowGeneratedPropertyDelegate("password")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User>.phone: kotlin.String? by DataRowGeneratedPropertyDelegate("phone")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User>.userStatus: kotlin.Int? by DataRowGeneratedPropertyDelegate("userStatus")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User>.username: kotlin.String? by DataRowGeneratedPropertyDelegate("username")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User?>.email: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("email")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User?>.firstName: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("firstName")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User?>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> by ColumnsContainerGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User?>.lastName: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("lastName")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User?>.password: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("password")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User?>.phone: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("phone")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User?>.userStatus: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> by ColumnsContainerGeneratedPropertyDelegate("userStatus")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.User?>.username: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("username")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User?>.email: kotlin.String? by DataRowGeneratedPropertyDelegate("email")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User?>.firstName: kotlin.String? by DataRowGeneratedPropertyDelegate("firstName")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User?>.id: kotlin.Long? by DataRowGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User?>.lastName: kotlin.String? by DataRowGeneratedPropertyDelegate("lastName")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User?>.password: kotlin.String? by DataRowGeneratedPropertyDelegate("password")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User?>.phone: kotlin.String? by DataRowGeneratedPropertyDelegate("phone")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User?>.userStatus: kotlin.Int? by DataRowGeneratedPropertyDelegate("userStatus")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.User?>.username: kotlin.String? by DataRowGeneratedPropertyDelegate("username")
        """.trimLines()

        code should haveSubstring(userExtensions)

        @Language("kt")
        val tagInterface = """
            @DataSchema(isOpen = false)
            interface Tag {
                val id: kotlin.Long?
                val name: kotlin.String?
                public companion object {
        """.trimLines()

        code should haveSubstring(tagInterface)

        @Language("kt")
        val status1Enum = """
            enum class Status1(override val value: kotlin.String) : org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum {
                AVAILABLE("available"),
                PENDING("pending"),
                SOLD("sold");
            }
        """.trimLines()

        code should haveSubstring(status1Enum)

        @Language("kt")
        val petInterface = """
            @DataSchema(isOpen = false)
            interface Pet {
                val id: kotlin.Long?
                val name: kotlin.String
                val category: $functionName.Category?
                val photoUrls: kotlin.collections.List<kotlin.String>
                val tags: org.jetbrains.kotlinx.dataframe.DataFrame<$functionName.Tag?>
                val status: $functionName.Status1?
                public companion object {
        """.trimLines()
        // category is a single other object, photoUrls is a primitive array, tags is a nullable array of objects

        code should haveSubstring(petInterface)

        @Language("kt")
        val petExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Pet>.category: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<`Advanced test Petstore Json`.Category?> by ColumnsContainerGeneratedPropertyDelegate("category")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Pet>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> by ColumnsContainerGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Pet>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> by ColumnsContainerGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Pet>.photoUrls: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<kotlin.String>> by ColumnsContainerGeneratedPropertyDelegate("photoUrls")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Pet>.status: org.jetbrains.kotlinx.dataframe.DataColumn<`Advanced test Petstore Json`.Status1?> by ColumnsContainerGeneratedPropertyDelegate("status")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Pet>.tags: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Advanced test Petstore Json`.Tag?>> by ColumnsContainerGeneratedPropertyDelegate("tags")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Pet>.category: org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Category?> by DataRowGeneratedPropertyDelegate("category")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Pet>.id: kotlin.Long? by DataRowGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Pet>.name: kotlin.String by DataRowGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Pet>.photoUrls: kotlin.collections.List<kotlin.String> by DataRowGeneratedPropertyDelegate("photoUrls")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Pet>.status: `Advanced test Petstore Json`.Status1? by DataRowGeneratedPropertyDelegate("status")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Pet>.tags: org.jetbrains.kotlinx.dataframe.DataFrame<`Advanced test Petstore Json`.Tag?> by DataRowGeneratedPropertyDelegate("tags")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Pet?>.category: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<`Advanced test Petstore Json`.Category?> by ColumnsContainerGeneratedPropertyDelegate("category")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Pet?>.id: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Long?> by ColumnsContainerGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Pet?>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Pet?>.photoUrls: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<kotlin.String>?> by ColumnsContainerGeneratedPropertyDelegate("photoUrls")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Pet?>.status: org.jetbrains.kotlinx.dataframe.DataColumn<`Advanced test Petstore Json`.Status1?> by ColumnsContainerGeneratedPropertyDelegate("status")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.Pet?>.tags: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Advanced test Petstore Json`.Tag?>> by ColumnsContainerGeneratedPropertyDelegate("tags")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Pet?>.category: org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Category?> by DataRowGeneratedPropertyDelegate("category")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Pet?>.id: kotlin.Long? by DataRowGeneratedPropertyDelegate("id")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Pet?>.name: kotlin.String? by DataRowGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Pet?>.photoUrls: kotlin.collections.List<kotlin.String>? by DataRowGeneratedPropertyDelegate("photoUrls")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Pet?>.status: `Advanced test Petstore Json`.Status1? by DataRowGeneratedPropertyDelegate("status")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.Pet?>.tags: org.jetbrains.kotlinx.dataframe.DataFrame<`Advanced test Petstore Json`.Tag?> by DataRowGeneratedPropertyDelegate("tags")
        """.trimLines()

        code should haveSubstring(petExtensions)

        @Language("kt")
        val apiResponseInterface = """
            @DataSchema(isOpen = false)
            interface ApiResponse {
                val code: kotlin.Int?
                val type: kotlin.String?
                val message: kotlin.String?
                public companion object {
        """.trimLines()

        code should haveSubstring(apiResponseInterface)

        @Language("kt")
        val apiResponseExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.ApiResponse>.code: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> by ColumnsContainerGeneratedPropertyDelegate("code")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.ApiResponse>.message: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("message")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.ApiResponse>.type: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("type")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.ApiResponse>.code: kotlin.Int? by DataRowGeneratedPropertyDelegate("code")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.ApiResponse>.message: kotlin.String? by DataRowGeneratedPropertyDelegate("message")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.ApiResponse>.type: kotlin.String? by DataRowGeneratedPropertyDelegate("type")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.ApiResponse?>.code: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> by ColumnsContainerGeneratedPropertyDelegate("code")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.ApiResponse?>.message: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("message")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Advanced test Petstore Json`.ApiResponse?>.type: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("type")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.ApiResponse?>.code: kotlin.Int? by DataRowGeneratedPropertyDelegate("code")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.ApiResponse?>.message: kotlin.String? by DataRowGeneratedPropertyDelegate("message")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Advanced test Petstore Json`.ApiResponse?>.type: kotlin.String? by DataRowGeneratedPropertyDelegate("type")
        """.trimLines()

        code should haveSubstring(apiResponseExtensions)

        @Language("kts")
        val res2 =
            execRaw("$functionName.Pet.readJsonStr(\"\"\"$someAdvancedPetsData\"\"\")") as AnyFrame

        @Language("kts")
        val res3 =
            execRaw("$functionName.Order.readJsonStr(\"\"\"$someAdvancedOrdersData\"\"\")") as AnyFrame

        shouldThrowAny {
            @Language("kts")
            val res4 =
                execRaw("$functionName.Order.readJsonStr(\"\"\"$someAdvancedFailingOrdersData\"\"\")") as AnyFrame
            res4
        }
    }

    @Test
    fun `Other advanced test`() {
        val fullFunctionName = ValidFieldName.of(::`Other advanced test`.name)
        val functionName = fullFunctionName.quotedIfNeeded
        val code = execGeneratedCode(advancedExample, fullFunctionName.unquoted)
            .trimLines()

        @Language("kt")
        val breedEnum = """
            enum class Breed(override val value: kotlin.String) : org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum {
                DINGO("Dingo"),
                HUSKY("Husky"),
                RETRIEVER("Retriever"),
                SHEPHERD("Shepherd");
            }
        """.trimLines()

        code should haveSubstring(breedEnum)

        @Language("kt")
        val dogInterface = """
            @DataSchema(isOpen = false)
            interface Dog : $functionName.Pet {
                override val tag: kotlin.String
                val bark: kotlin.Boolean?
                val breed: $functionName.Breed
                public companion object {
                    public val keyValuePaths: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.api.JsonPath>
                        get() = listOf()

                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToDog(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<Dog>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<Dog> = convertTo<Dog> { 
                        convertDataRowsWithOpenApi() 
                        convertTo()
                    }
        """.trimLines() // tag is nullable in Pet but required in Dog

        code should haveSubstring(dogInterface)

        @Language("kt")
        val dogExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Dog>.bark: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?> by ColumnsContainerGeneratedPropertyDelegate("bark")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Dog>.breed: org.jetbrains.kotlinx.dataframe.DataColumn<`Other advanced test`.Breed> by ColumnsContainerGeneratedPropertyDelegate("breed")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Dog>.tag: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> by ColumnsContainerGeneratedPropertyDelegate("tag")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Dog>.bark: kotlin.Boolean? by DataRowGeneratedPropertyDelegate("bark")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Dog>.breed: `Other advanced test`.Breed by DataRowGeneratedPropertyDelegate("breed")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Dog>.tag: kotlin.String by DataRowGeneratedPropertyDelegate("tag")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Dog?>.bark: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?> by ColumnsContainerGeneratedPropertyDelegate("bark")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Dog?>.breed: org.jetbrains.kotlinx.dataframe.DataColumn<`Other advanced test`.Breed?> by ColumnsContainerGeneratedPropertyDelegate("breed")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Dog?>.tag: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("tag")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Dog?>.bark: kotlin.Boolean? by DataRowGeneratedPropertyDelegate("bark")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Dog?>.breed: `Other advanced test`.Breed? by DataRowGeneratedPropertyDelegate("breed")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Dog?>.tag: kotlin.String? by DataRowGeneratedPropertyDelegate("tag")
        """.trimLines()

        code should haveSubstring(dogExtensions)

        @Language("kt")
        val breed1Enum = """
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

        @Language("kt")
        val catInterface = """
            @DataSchema(isOpen = false)
            interface Cat : $functionName.Pet {
                val hunts: kotlin.Boolean?
                val age: kotlin.Float?
                val breed: $functionName.Breed1?
                public companion object {
                    public val keyValuePaths: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.api.JsonPath>
                        get() = listOf()
                
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToCat(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<Cat>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<Cat> = convertTo<Cat> { 
                          convertDataRowsWithOpenApi() 
                          convertTo()
                    }
        """.trimLines()
        // hunts is required but marked nullable, age is either integer or number, breed is nullable enum

        code should haveSubstring(catInterface)

        @Language("kt")
        val catExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Cat>.age: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Float?> by ColumnsContainerGeneratedPropertyDelegate("age")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Cat>.breed: org.jetbrains.kotlinx.dataframe.DataColumn<`Other advanced test`.Breed1?> by ColumnsContainerGeneratedPropertyDelegate("breed")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Cat>.hunts: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?> by ColumnsContainerGeneratedPropertyDelegate("hunts")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Cat>.age: kotlin.Float? by DataRowGeneratedPropertyDelegate("age")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Cat>.breed: `Other advanced test`.Breed1? by DataRowGeneratedPropertyDelegate("breed")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Cat>.hunts: kotlin.Boolean? by DataRowGeneratedPropertyDelegate("hunts")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Cat?>.age: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Float?> by ColumnsContainerGeneratedPropertyDelegate("age")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Cat?>.breed: org.jetbrains.kotlinx.dataframe.DataColumn<`Other advanced test`.Breed1?> by ColumnsContainerGeneratedPropertyDelegate("breed")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Cat?>.hunts: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Boolean?> by ColumnsContainerGeneratedPropertyDelegate("hunts")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Cat?>.age: kotlin.Float? by DataRowGeneratedPropertyDelegate("age")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Cat?>.breed: `Other advanced test`.Breed1? by DataRowGeneratedPropertyDelegate("breed")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Cat?>.hunts: kotlin.Boolean? by DataRowGeneratedPropertyDelegate("hunts")
        """.trimLines()

        code should haveSubstring(catExtensions)

        @Language("kt")
        val eyeColorEnum = """
            enum class EyeColor(override val value: kotlin.String) : org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum {
                BLUE("Blue"),
                YELLOW("Yellow"),
                BROWN("Brown"),
                GREEN("Green");
            }
        """.trimLines() // nullable enum, but taken care of in properties that use this enum

        code should haveSubstring(eyeColorEnum)

        @Language("kt")
        val petInterface = """
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
                val eyeColor: $functionName.EyeColor?
                public companion object {
                    public val keyValuePaths: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.api.JsonPath>
                        get() = listOf()
                
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToPet(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<Pet>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<Pet> = convertTo<Pet> { 
                          convertDataRowsWithOpenApi() 
                          convertTo()
                      }
        """.trimLines()
        // petType was named pet_type, id is either Long or String, other is not integer, eyeColor is a required but nullable enum

        code should haveSubstring(petInterface)

        @Language("kt")
        val petExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Pet>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> by ColumnsContainerGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Pet>.eyeColor: org.jetbrains.kotlinx.dataframe.DataColumn<`Other advanced test`.EyeColor?> by ColumnsContainerGeneratedPropertyDelegate("eye_color")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Pet>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> by ColumnsContainerGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Pet>.other: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> by ColumnsContainerGeneratedPropertyDelegate("other")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Pet>.petType: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> by ColumnsContainerGeneratedPropertyDelegate("pet_type")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Pet>.tag: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("tag")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Pet>.`value`: kotlin.Any? by DataRowGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Pet>.eyeColor: `Other advanced test`.EyeColor? by DataRowGeneratedPropertyDelegate("eye_color")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Pet>.name: kotlin.String by DataRowGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Pet>.other: kotlin.Any? by DataRowGeneratedPropertyDelegate("other")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Pet>.petType: kotlin.String by DataRowGeneratedPropertyDelegate("pet_type")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Pet>.tag: kotlin.String? by DataRowGeneratedPropertyDelegate("tag")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Pet?>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> by ColumnsContainerGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Pet?>.eyeColor: org.jetbrains.kotlinx.dataframe.DataColumn<`Other advanced test`.EyeColor?> by ColumnsContainerGeneratedPropertyDelegate("eye_color")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Pet?>.name: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Pet?>.other: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> by ColumnsContainerGeneratedPropertyDelegate("other")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Pet?>.petType: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("pet_type")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Pet?>.tag: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("tag")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Pet?>.`value`: kotlin.Any? by DataRowGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Pet?>.eyeColor: `Other advanced test`.EyeColor? by DataRowGeneratedPropertyDelegate("eye_color")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Pet?>.name: kotlin.String? by DataRowGeneratedPropertyDelegate("name")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Pet?>.other: kotlin.Any? by DataRowGeneratedPropertyDelegate("other")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Pet?>.petType: kotlin.String? by DataRowGeneratedPropertyDelegate("pet_type")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Pet?>.tag: kotlin.String? by DataRowGeneratedPropertyDelegate("tag")
        """.trimLines()

        code should haveSubstring(petExtensions)

        @Language("kt")
        val petRefTypeAlias = """
            typealias PetRef = $functionName.Pet
        """.trimLines() // is either Cat or Dog, we cannot merge objects, but they have the same ancestor, so Pet

        code should haveSubstring(petRefTypeAlias)

        @Language("kt")
        val alsoCatTypeAlias = """
            typealias AlsoCat = $functionName.Cat
        """.trimLines()

        code should haveSubstring(alsoCatTypeAlias)

        @Language("kt")
        val integerTypeAlias = """
            typealias Integer = kotlin.Int
        """.trimLines()

        code should haveSubstring(integerTypeAlias)

        @Language("kt")
        val intListInterface = """
            @DataSchema(isOpen = false)
            interface IntList {
                val list: kotlin.collections.List<kotlin.Int>
                public companion object {
                    public val keyValuePaths: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.api.JsonPath>
                        get() = listOf()
                
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToIntList(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<IntList>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<IntList> = convertTo<IntList> { 
                        convertDataRowsWithOpenApi() 
                        convertTo()
                    }
        """.trimLines() // array of primitives, so list int

        code should haveSubstring(intListInterface)

        @Language("kt")
        val intListExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.IntList>.list: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<kotlin.Int>> by ColumnsContainerGeneratedPropertyDelegate("list")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.IntList>.list: kotlin.collections.List<kotlin.Int> by DataRowGeneratedPropertyDelegate("list")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.IntList?>.list: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<kotlin.Int>?> by ColumnsContainerGeneratedPropertyDelegate("list")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.IntList?>.list: kotlin.collections.List<kotlin.Int>? by DataRowGeneratedPropertyDelegate("list")
        """.trimLines()

        code should haveSubstring(intListExtensions)

        @Language("kt")
        val objectWithAdditionalPropertiesInterface = """
            @DataSchema(isOpen = false)
            interface ObjectWithAdditionalProperties : org.jetbrains.kotlinx.dataframe.io.AdditionalProperty<kotlin.String> {
                @ColumnName("value")
                override val `value`: kotlin.String
                override val key: kotlin.String
                public companion object {
                    public val keyValuePaths: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.api.JsonPath>
                        get() = listOf(JsonPath(""${'"'}${'$'}""${'"'}))
                    
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToObjectWithAdditionalProperties(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<ObjectWithAdditionalProperties>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditionalProperties> = convertTo<ObjectWithAdditionalProperties> {
                        convertDataRowsWithOpenApi()
                        convertTo()
                    }
                    
                    public fun readJson(url: java.net.URL): org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditionalProperties> = org.jetbrains.kotlinx.dataframe.DataFrame
                        .readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)["value"].first().let { it as DataFrame<*> }
                        .convertToObjectWithAdditionalProperties()
        """.trimLines()

        code should haveSubstring(objectWithAdditionalPropertiesInterface)

        @Language("kt")
        val objectWithAdditionalPropertiesExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditionalProperties>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> by ColumnsContainerGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditionalProperties>.key: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> by ColumnsContainerGeneratedPropertyDelegate("key")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditionalProperties>.`value`: kotlin.String by DataRowGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditionalProperties>.key: kotlin.String by DataRowGeneratedPropertyDelegate("key")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditionalProperties?>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditionalProperties?>.key: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("key")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditionalProperties?>.`value`: kotlin.String? by DataRowGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditionalProperties?>.key: kotlin.String? by DataRowGeneratedPropertyDelegate("key")
        """.trimLines()

        code should haveSubstring(objectWithAdditionalPropertiesExtensions)

        @Language("kt")
        val objectWithAdditional2Interface = """
            @DataSchema(isOpen = false)
            interface ObjectWithAdditional2 : org.jetbrains.kotlinx.dataframe.io.AdditionalProperty<kotlin.Any> {
                @ColumnName("value")
                override val `value`: kotlin.Any
                override val key: kotlin.String
                public companion object {
                    public val keyValuePaths: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.api.JsonPath>
                        get() = listOf(JsonPath(""${'"'}${'$'}""${'"'}))
                    
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToObjectWithAdditional2(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<ObjectWithAdditional2>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional2> = convertTo<ObjectWithAdditional2> {
                        convertDataRowsWithOpenApi()
                        convertTo()
                    }
                    
                    public fun readJson(url: java.net.URL): org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional2> = org.jetbrains.kotlinx.dataframe.DataFrame
                        .readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)["value"].first().let { it as DataFrame<*> }
                        .convertToObjectWithAdditional2()
        """.trimLines()

        code should haveSubstring(objectWithAdditional2Interface)

        @Language("kt")
        val objectWithAdditional2Extensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditional2>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any> by ColumnsContainerGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditional2>.key: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> by ColumnsContainerGeneratedPropertyDelegate("key")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditional2>.`value`: kotlin.Any by DataRowGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditional2>.key: kotlin.String by DataRowGeneratedPropertyDelegate("key")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditional2?>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> by ColumnsContainerGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditional2?>.key: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("key")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditional2?>.`value`: kotlin.Any? by DataRowGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditional2?>.key: kotlin.String? by DataRowGeneratedPropertyDelegate("key")
        """.trimLines()

        code should haveSubstring(objectWithAdditional2Extensions)

        @Language("kt")
        val objectWithAdditional3Interface = """
            @DataSchema(isOpen = false)
            interface ObjectWithAdditional3 : org.jetbrains.kotlinx.dataframe.io.AdditionalProperty<kotlin.Any?> {
                @ColumnName("value")
                override val `value`: kotlin.Any?
                override val key: kotlin.String
                public companion object {
                    public val keyValuePaths: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.api.JsonPath>
                        get() = listOf(JsonPath(""${'"'}${'$'}""${'"'}))
                    
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToObjectWithAdditional3(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<ObjectWithAdditional3>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional3> = convertTo<ObjectWithAdditional3> {
                        convertDataRowsWithOpenApi()
                        convertTo()
                    }
                    
                    public fun readJson(url: java.net.URL): org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional3> = org.jetbrains.kotlinx.dataframe.DataFrame
                        .readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)["value"].first().let { it as DataFrame<*> }
                        .convertToObjectWithAdditional3()
        """.trimLines()

        code should haveSubstring(objectWithAdditional3Interface)

        @Language("kt")
        val objectWithAdditional3Extensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditional3>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> by ColumnsContainerGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditional3>.key: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> by ColumnsContainerGeneratedPropertyDelegate("key")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditional3>.`value`: kotlin.Any? by DataRowGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditional3>.key: kotlin.String by DataRowGeneratedPropertyDelegate("key")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditional3?>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Any?> by ColumnsContainerGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditional3?>.key: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("key")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditional3?>.`value`: kotlin.Any? by DataRowGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditional3?>.key: kotlin.String? by DataRowGeneratedPropertyDelegate("key")
        """.trimLines()

        code should haveSubstring(objectWithAdditional3Extensions)

        // TODO broke PetRef?
        @Language("kt")
        val errorInterface = """
            @DataSchema(isOpen = false)
            interface Error {
                val ints: $functionName.IntList?
                val petRef: PetRef
                val pets: org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?>
                val code: kotlin.Int
                val message: kotlin.String
                val objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataFrame<$functionName.ObjectWithAdditionalProperties>
                val objectWithAdditionalList: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.DataFrame<$functionName.ObjectWithAdditionalProperties>>?
                val objectWithAdditional2: org.jetbrains.kotlinx.dataframe.DataFrame<$functionName.ObjectWithAdditional2?>
                val objectWithAdditional3: org.jetbrains.kotlinx.dataframe.DataFrame<$functionName.ObjectWithAdditional3>
                val array: kotlin.collections.List<SomeArrayArray>?
                public companion object {
                    public val keyValuePaths: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.api.JsonPath>
                        get() = listOf(JsonPath(""${'"'}${'$'}["objectWithAdditional"]""${'"'}), JsonPath(""${'"'}${'$'}["objectWithAdditionalList"][*]""${'"'}), JsonPath(""${'"'}${'$'}["objectWithAdditional2"]""${'"'}), JsonPath(""${'"'}${'$'}["objectWithAdditional3"]""${'"'}), JsonPath(""${'"'}${'$'}["array"][*][*][*]["objectWithAdditional"]""${'"'}))
                
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToError(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<Error>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<Error> = convertTo<Error> {
                        convertDataRowsWithOpenApi()
                        convertTo()
                    }
        """.trimLines()

        code should haveSubstring(errorInterface)

        @Language("kt")
        val errorExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error>.array: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<SomeArrayArray>?> by ColumnsContainerGeneratedPropertyDelegate("array")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error>.code: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int> by ColumnsContainerGeneratedPropertyDelegate("code")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error>.ints: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<`Other advanced test`.IntList?> by ColumnsContainerGeneratedPropertyDelegate("ints")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error>.message: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> by ColumnsContainerGeneratedPropertyDelegate("message")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error>.objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditionalProperties>> by ColumnsContainerGeneratedPropertyDelegate("objectWithAdditional")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error>.objectWithAdditional2: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditional2?>> by ColumnsContainerGeneratedPropertyDelegate("objectWithAdditional2")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error>.objectWithAdditional3: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditional3>> by ColumnsContainerGeneratedPropertyDelegate("objectWithAdditional3")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error>.objectWithAdditionalList: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditionalProperties>>?> by ColumnsContainerGeneratedPropertyDelegate("objectWithAdditionalList")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error>.petRef: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<PetRef> by ColumnsContainerGeneratedPropertyDelegate("petRef")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error>.pets: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?>> by ColumnsContainerGeneratedPropertyDelegate("pets")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error>.array: kotlin.collections.List<SomeArrayArray>? by DataRowGeneratedPropertyDelegate("array")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error>.code: kotlin.Int by DataRowGeneratedPropertyDelegate("code")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error>.ints: org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.IntList?> by DataRowGeneratedPropertyDelegate("ints")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error>.message: kotlin.String by DataRowGeneratedPropertyDelegate("message")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error>.objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditionalProperties> by DataRowGeneratedPropertyDelegate("objectWithAdditional")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error>.objectWithAdditional2: org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditional2?> by DataRowGeneratedPropertyDelegate("objectWithAdditional2")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error>.objectWithAdditional3: org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditional3> by DataRowGeneratedPropertyDelegate("objectWithAdditional3")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error>.objectWithAdditionalList: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditionalProperties>>? by DataRowGeneratedPropertyDelegate("objectWithAdditionalList")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error>.petRef: org.jetbrains.kotlinx.dataframe.DataRow<PetRef> by DataRowGeneratedPropertyDelegate("petRef")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error>.pets: org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?> by DataRowGeneratedPropertyDelegate("pets")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error?>.array: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<SomeArrayArray>?> by ColumnsContainerGeneratedPropertyDelegate("array")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error?>.code: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> by ColumnsContainerGeneratedPropertyDelegate("code")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error?>.ints: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<`Other advanced test`.IntList?> by ColumnsContainerGeneratedPropertyDelegate("ints")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error?>.message: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("message")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error?>.objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditionalProperties?>> by ColumnsContainerGeneratedPropertyDelegate("objectWithAdditional")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error?>.objectWithAdditional2: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditional2?>> by ColumnsContainerGeneratedPropertyDelegate("objectWithAdditional2")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error?>.objectWithAdditional3: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditional3?>> by ColumnsContainerGeneratedPropertyDelegate("objectWithAdditional3")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error?>.objectWithAdditionalList: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.collections.List<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditionalProperties>>?> by ColumnsContainerGeneratedPropertyDelegate("objectWithAdditionalList")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error?>.petRef: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<PetRef?> by ColumnsContainerGeneratedPropertyDelegate("petRef")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.Error?>.pets: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?>> by ColumnsContainerGeneratedPropertyDelegate("pets")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error?>.array: kotlin.collections.List<SomeArrayArray>? by DataRowGeneratedPropertyDelegate("array")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error?>.code: kotlin.Int? by DataRowGeneratedPropertyDelegate("code")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error?>.ints: org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.IntList?> by DataRowGeneratedPropertyDelegate("ints")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error?>.message: kotlin.String? by DataRowGeneratedPropertyDelegate("message")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error?>.objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditionalProperties?> by DataRowGeneratedPropertyDelegate("objectWithAdditional")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error?>.objectWithAdditional2: org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditional2?> by DataRowGeneratedPropertyDelegate("objectWithAdditional2")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error?>.objectWithAdditional3: org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditional3?> by DataRowGeneratedPropertyDelegate("objectWithAdditional3")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error?>.objectWithAdditionalList: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditionalProperties>>? by DataRowGeneratedPropertyDelegate("objectWithAdditionalList")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error?>.petRef: org.jetbrains.kotlinx.dataframe.DataRow<PetRef?> by DataRowGeneratedPropertyDelegate("petRef")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Error?>.pets: org.jetbrains.kotlinx.dataframe.DataFrame<kotlin.Any?> by DataRowGeneratedPropertyDelegate("pets")
        """.trimLines()

        code should haveSubstring(errorExtensions)

        @Language("kt")
        val valueInterface = """
            @DataSchema(isOpen = false)
            interface Value
        """.trimLines()

        code should haveSubstring(valueInterface)

        @Language("kt")
        val objectWithAdditionalInterface = """
            @DataSchema(isOpen = false)
            interface ObjectWithAdditional : org.jetbrains.kotlinx.dataframe.io.AdditionalProperty<kotlin.Int> {
                @ColumnName("value")
                override val `value`: kotlin.Int
                override val key: kotlin.String
                public companion object {
                    public val keyValuePaths: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.api.JsonPath>
                        get() = listOf(JsonPath(""${'"'}${'$'}""${'"'}))
                    
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToObjectWithAdditional(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<ObjectWithAdditional>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional> = convertTo<ObjectWithAdditional> {
                        convertDataRowsWithOpenApi()
                        convertTo()
                    }
                    
                    public fun readJson(url: java.net.URL): org.jetbrains.kotlinx.dataframe.DataFrame<ObjectWithAdditional> = org.jetbrains.kotlinx.dataframe.DataFrame
                        .readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)["value"].first().let { it as DataFrame<*> }
                        .convertToObjectWithAdditional()
        """.trimLines()

        code should haveSubstring(objectWithAdditionalInterface)

        @Language("kt")
        val objectWithAdditionalExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditional>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int> by ColumnsContainerGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditional>.key: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> by ColumnsContainerGeneratedPropertyDelegate("key")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditional>.`value`: kotlin.Int by DataRowGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditional>.key: kotlin.String by DataRowGeneratedPropertyDelegate("key")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditional?>.`value`: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int?> by ColumnsContainerGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ObjectWithAdditional?>.key: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("key")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditional?>.`value`: kotlin.Int? by DataRowGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ObjectWithAdditional?>.key: kotlin.String? by DataRowGeneratedPropertyDelegate("key")
        """.trimLines()

        code should haveSubstring(objectWithAdditionalExtensions)

        @Language("kt")
        val someArrayContentInterface = """
            @DataSchema(isOpen = false)
            interface SomeArrayContent {
                val op: $functionName.Op
                val path: kotlin.String
                @ColumnName("value")
                val `value`: $functionName.Value?
                val objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataFrame<$functionName.ObjectWithAdditional?>
                public companion object {
                    public val keyValuePaths: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.api.JsonPath>
                        get() = listOf(JsonPath(""${'"'}${'$'}["objectWithAdditional"]""${'"'}))
                
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToSomeArrayContent(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<SomeArrayContent>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<SomeArrayContent> = convertTo<SomeArrayContent> {
                        convertDataRowsWithOpenApi()
                        convertTo()
                    }
                    
                    public fun readJson(url: java.net.URL): org.jetbrains.kotlinx.dataframe.DataFrame<SomeArrayContent> = org.jetbrains.kotlinx.dataframe.DataFrame
                        .readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                        .convertToSomeArrayContent()
        """.trimLines()

        code should haveSubstring(someArrayContentInterface)

        @Language("kt")
        val someArrayContentExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.SomeArrayContent>.`value`: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<`Other advanced test`.Value?> by ColumnsContainerGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.SomeArrayContent>.objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditional?>> by ColumnsContainerGeneratedPropertyDelegate("objectWithAdditional")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.SomeArrayContent>.op: org.jetbrains.kotlinx.dataframe.DataColumn<`Other advanced test`.Op> by ColumnsContainerGeneratedPropertyDelegate("op")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.SomeArrayContent>.path: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> by ColumnsContainerGeneratedPropertyDelegate("path")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.SomeArrayContent>.`value`: org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Value?> by DataRowGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.SomeArrayContent>.objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditional?> by DataRowGeneratedPropertyDelegate("objectWithAdditional")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.SomeArrayContent>.op: `Other advanced test`.Op by DataRowGeneratedPropertyDelegate("op")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.SomeArrayContent>.path: kotlin.String by DataRowGeneratedPropertyDelegate("path")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.SomeArrayContent?>.`value`: org.jetbrains.kotlinx.dataframe.columns.ColumnGroup<`Other advanced test`.Value?> by ColumnsContainerGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.SomeArrayContent?>.objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditional?>> by ColumnsContainerGeneratedPropertyDelegate("objectWithAdditional")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.SomeArrayContent?>.op: org.jetbrains.kotlinx.dataframe.DataColumn<`Other advanced test`.Op?> by ColumnsContainerGeneratedPropertyDelegate("op")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.SomeArrayContent?>.path: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String?> by ColumnsContainerGeneratedPropertyDelegate("path")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.SomeArrayContent?>.`value`: org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.Value?> by DataRowGeneratedPropertyDelegate("value")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.SomeArrayContent?>.objectWithAdditional: org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.ObjectWithAdditional?> by DataRowGeneratedPropertyDelegate("objectWithAdditional")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.SomeArrayContent?>.op: `Other advanced test`.Op? by DataRowGeneratedPropertyDelegate("op")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.SomeArrayContent?>.path: kotlin.String? by DataRowGeneratedPropertyDelegate("path")
        """.trimLines()

        code should haveSubstring(someArrayContentExtensions)

        @Language("kt")
        val someArrayTypeAlias = """
            typealias SomeArray = org.jetbrains.kotlinx.dataframe.DataFrame<$functionName.SomeArrayContent>
        """.trimLines()

        code should haveSubstring(someArrayTypeAlias)

        @Language("kt")
        val errorHolderInterface = """
            @DataSchema(isOpen = false)
            interface ErrorHolder {
                val errors: org.jetbrains.kotlinx.dataframe.DataFrame<$functionName.Error>
                public companion object {
                    public val keyValuePaths: kotlin.collections.List<org.jetbrains.kotlinx.dataframe.api.JsonPath>
                        get() = listOf(JsonPath(""${'"'}${'$'}["errors"][*]["objectWithAdditional"]""${'"'}), JsonPath(""${'"'}${'$'}["errors"][*]["objectWithAdditionalList"][*]""${'"'}), JsonPath(""${'"'}${'$'}["errors"][*]["objectWithAdditional2"]""${'"'}), JsonPath(""${'"'}${'$'}["errors"][*]["objectWithAdditional3"]""${'"'}), JsonPath(""${'"'}${'$'}["errors"][*]["array"][*][*][*]["objectWithAdditional"]""${'"'}))
                    
                    public fun org.jetbrains.kotlinx.dataframe.DataFrame<*>.convertToErrorHolder(convertTo: org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl<ErrorHolder>.() -> kotlin.Unit = {}): org.jetbrains.kotlinx.dataframe.DataFrame<ErrorHolder> = convertTo<ErrorHolder> {
                        convertDataRowsWithOpenApi()
                        convertTo()
                    }
                    
                    public fun readJson(url: java.net.URL): org.jetbrains.kotlinx.dataframe.DataFrame<ErrorHolder> = org.jetbrains.kotlinx.dataframe.DataFrame
                        .readJson(url, typeClashTactic = ANY_COLUMNS, keyValuePaths = keyValuePaths)
                        .convertToErrorHolder()
        """.trimLines()

        code should haveSubstring(errorHolderInterface)

        @Language("kt")
        val errorHolderExtensions = """
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ErrorHolder>.errors: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.Error>> by ColumnsContainerGeneratedPropertyDelegate("errors")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ErrorHolder>.errors: org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.Error> by DataRowGeneratedPropertyDelegate("errors")
            val org.jetbrains.kotlinx.dataframe.ColumnsContainer<`Other advanced test`.ErrorHolder?>.errors: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.Error?>> by ColumnsContainerGeneratedPropertyDelegate("errors")
            val org.jetbrains.kotlinx.dataframe.DataRow<`Other advanced test`.ErrorHolder?>.errors: org.jetbrains.kotlinx.dataframe.DataFrame<`Other advanced test`.Error?> by DataRowGeneratedPropertyDelegate("errors")
        """.trimLines()

        code should haveSubstring(errorHolderExtensions)

        @Language("kt")
        val res1 = execRaw(
            "$functionName.Pet.readJsonStr(\"\"\"$advancedData\"\"\").filter { petType == \"Cat\" }.convertTo<$functionName.Cat>(ExcessiveColumns.Remove)",
        ) as AnyFrame
        val res1Schema = res1.schema()

        @Language("kts")
        val res2 = execRaw(
            "$functionName.Pet.readJsonStr(\"\"\"$advancedData\"\"\").filter { petType == \"Dog\" }.convertTo<$functionName.Dog>(ExcessiveColumns.Remove)",
        ) as AnyFrame
        val res2Schema = res2.schema()

        @Language("kts")
        val res3 = execRaw(
            "$functionName.Error.readJsonStr(\"\"\"$advancedErrorData\"\"\")",
        ) as AnyFrame
        val res3Schema = res3.schema()

        @Language("kts")
        val res4 = execRaw(
            "$functionName.ErrorHolder.readJsonStr(\"\"\"$advancedErrorHolderData\"\"\")",
        ) as AnyFrame
        val res4Schema = res4.schema()
    }

    @Ignore // TODO Not sure why this is failing
    @Test
    fun `Apis guru Test`() {
        val fullFunctionName = ValidFieldName.of(::`Apis guru Test`.name)
        val functionName = fullFunctionName.quotedIfNeeded
        val code = execGeneratedCode(apiGuruYaml, fullFunctionName.unquoted)

        val apiGuruDataTripleQuote = "\"\"\"${apiGuruData.replace("$", "\${'$'}")}\"\"\""

        @Language("kts")
        execRaw("val df = $functionName.APIs.readJsonStr($apiGuruDataTripleQuote)")

        @Language("kts")
        val df = execRaw(
            """
            df.filter {
                value.versions.value.any {
                    (updated ?: added).year > 2019
                }
            }                   
            """.trimIndent(),
        ) as AnyFrame

        df.isNotEmpty().shouldBeTrue()
    }

    @Test
    fun `MLC Test 1`() {
        val code = execGeneratedCode(mlcYaml, ::`MLC Test 1`.name)
        println(code)

        val mlcLocationsWithPeopleDataTripleQuote = "\"\"\"${mlcLocationsWithPeopleData.replace("$", "\${'$'}")}\"\"\""

        @Language("kts")
        val df1 = execRaw(
            """
            `${::`MLC Test 1`.name}`.LocationsWithPeople.readJsonStr($mlcLocationsWithPeopleDataTripleQuote)
            .also { it.print() }
            """.trimIndent(),
        ) as AnyFrame

        df1.isNotEmpty().shouldBeTrue()
    }

    @Test
    fun `MLC Test 2`() {
        val code = execGeneratedCode(mlcYaml, ::`MLC Test 2`.name)
        println(code)

        val mlcPeopleWithLocationDataTripleQuote = "\"\"\"${mlcPeopleWithLocationData.replace("$", "\${'$'}")}\"\"\""

        @Language("kts")
        val df2 = execRaw(
            """
            `${::`MLC Test 2`.name}`.PeopleWithLocation.readJsonStr($mlcPeopleWithLocationDataTripleQuote)
            .also { it.print() }
            """.trimIndent(),
        ) as AnyFrame

        df2.isNotEmpty().shouldBeTrue()
    }

    @Ignore // TODO Failing for some reason
    @Suppress("LocalVariableName")
    @Test
    fun `Jupyter importDataSchema`() {
        val filePath = apiGuruYaml.absolutePath.let {
            if (separatorChar == '\\') {
                it.replace("\\", "\\\\")
            } else {
                it
            }
        }

        @Language("kt")
        val _1 = execRaw(
            """
            val ApiGuru = importDataSchema(File("$filePath"))
            """.trimIndent(),
        )

        val apiGuruDataTripleQuote = "\"\"\"${apiGuruData.replace("$", "\${'$'}")}\"\"\""

        @Language("kt")
        val _2 = execRaw(
            """
            val df = ApiGuru.APIs.readJsonStr($apiGuruDataTripleQuote)
            df
            """.trimIndent(),
        ) as AnyFrame

        println(_2)

        @Language("kt")
        val _3 = execRaw(
            """
            df.filter {
              value.versions.value.any {
                (updated ?: added).year >= 2021
              }
            }
            """.trimIndent(),
        ) as AnyFrame

        @Language("kt")
        val _4 = execRaw(
            """
            ApiGuru.APIs.readJsonStr($apiGuruDataTripleQuote).filter {
              value.versions.value.any {
                (updated ?: added).year >= 2021
              }
            }
            """.trimIndent(),
        ) as AnyFrame
    }

    private fun String.trimLines(): String = trim().removeSurrounding("\n").lines().joinToString("\n") { it.trim() }
}
