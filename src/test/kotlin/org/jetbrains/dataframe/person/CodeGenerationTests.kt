package org.jetbrains.dataframe.person

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.*
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
            interface DataFrameType1{
                val name: String
                val age: Int
                val city: String?
                val weight: Int?
            }""".trimIndent()

        val expectedConverter = "$" + "it.retype<DataFrameType1>()"

        code.size shouldBe 2
        code[0].trimIndent() shouldBe expectedDeclaration
        code[1] shouldBe expectedConverter
    }

    @Test
    fun `generate marker interface for nested data frame`() {
        val property = TypedDataFrameTests::class.memberProperties.first { it.name == "df" }
        val grouped = df.groupCols { name and city }.into("nameAndCity")
        val code = CodeGenerator().generate(grouped, property)
        val declaration1 = """
            @DataFrameType(isOpen = false)
            interface DataFrameType2{
                val name: String
                val city: String?
            }""".trimIndent()

        val declaration2 = """
            @DataFrameType(isOpen = false)
            interface DataFrameType1{
                val nameAndCity: TypedDataFrameRow<DataFrameType2>
                val age: Int
                val weight: Int?
            }""".trimIndent()

        val expectedConverter = "$" + "it.retype<DataFrameType1>()"

        code.size shouldBe 3
        code[0].trimIndent() shouldBe declaration1
        code[1].trimIndent() shouldBe declaration2
        code[2] shouldBe expectedConverter
    }

    @Test
    fun `generate extension properties`() {
        val code = CodeGenerator().generate(Person::class)

        val dfName = (DataFrameBase::class).simpleName
        val dfRowName = (DataFrameRowBase::class).simpleName

        val expected = """
            val $dfName<$personClassName>.age: org.jetbrains.dataframe.ColumnData<kotlin.Int> get() = this["age"] as org.jetbrains.dataframe.ColumnData<kotlin.Int>
            val $dfRowName<$personClassName>.age: Int get() = this["age"] as Int
            val $dfName<$personClassName>.city: org.jetbrains.dataframe.ColumnData<kotlin.String?> get() = this["city"] as org.jetbrains.dataframe.ColumnData<kotlin.String?>
            val $dfRowName<$personClassName>.city: String? get() = this["city"] as String?
            val $dfName<$personClassName>.name: org.jetbrains.dataframe.ColumnData<kotlin.String> get() = this["name"] as org.jetbrains.dataframe.ColumnData<kotlin.String>
            val $dfRowName<$personClassName>.name: String get() = this["name"] as String
            val $dfName<$personClassName>.weight: org.jetbrains.dataframe.ColumnData<kotlin.Int?> get() = this["weight"] as org.jetbrains.dataframe.ColumnData<kotlin.Int?>
            val $dfRowName<$personClassName>.weight: Int? get() = this["weight"] as Int?
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
            interface DataFrameType1 : $personClassName{
                override val city: String
                override val weight: Int
            }
        """.trimIndent()
        code[0] shouldBe expected
    }
}