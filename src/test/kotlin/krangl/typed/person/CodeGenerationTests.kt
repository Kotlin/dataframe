package krangl.typed.person

import io.kotlintest.shouldBe
import krangl.typed.CodeGenerator
import org.junit.Test
import kotlin.reflect.full.memberProperties

class CodeGenerationTests : BaseTest(){

    val personClassName = Person::class.java.canonicalName

    @Test
    fun `generate marker interface`() {
        val property = TypedDataFrameTests::class.memberProperties.first { it.name == "df" }
        val code = CodeGenerator().generate(df, property)
        val expectedDeclaration = """
            @DataFrameType(isOpen = false)
            interface DataFrameType###{
                val name: String
                val age: Int
                val city: String?
                val weight: Int?
            }""".trimIndent()

        val expectedConverter = "$" + "it.typed<DataFrameType###>()"

        code.size shouldBe 2
        code[0].trimIndent() shouldBe expectedDeclaration
        code[1] shouldBe expectedConverter
    }

    @Test
    fun `generate extension properties`() {
        val code = CodeGenerator().generate(Person::class)


        val expected = """
            val TypedDataFrame<$personClassName>.age: krangl.typed.TypedColData<kotlin.Int> get() = (this["age"]) as krangl.typed.TypedColData<kotlin.Int>
            val TypedDataFrameRow<$personClassName>.age: Int get() = (this["age"]) as Int
            val TypedDataFrame<$personClassName>.city: krangl.typed.TypedColData<kotlin.String?> get() = (this["city"]) as krangl.typed.TypedColData<kotlin.String?>
            val TypedDataFrameRow<$personClassName>.city: String? get() = (this["city"]) as String?
            val TypedDataFrame<$personClassName>.name: krangl.typed.TypedColData<kotlin.String> get() = (this["name"]) as krangl.typed.TypedColData<kotlin.String>
            val TypedDataFrameRow<$personClassName>.name: String get() = (this["name"]) as String
            val TypedDataFrame<$personClassName>.weight: krangl.typed.TypedColData<kotlin.Int?> get() = (this["weight"]) as krangl.typed.TypedColData<kotlin.Int?>
            val TypedDataFrameRow<$personClassName>.weight: Int? get() = (this["weight"]) as Int?
        """.trimIndent()
        code.joinToString("\n") shouldBe expected
    }

    @Test
    fun `generate derived interface`() {
        val codeGen = CodeGenerator()
        codeGen.generate(Person::class)
        val property = TypedDataFrameTests::class.memberProperties.first { it.name == "df" }
        val code = codeGen.generate(df.filterNotNull(), property)
        val expected = """
            @DataFrameType(isOpen = false)
            interface DataFrameType### : $personClassName{
                override val city: String
                override val weight: Int
            }
        """.trimIndent()
        code[0] shouldBe expected
    }
}