package org.jetbrains.dataframe.codeGen

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.person.BaseTest
import org.jetbrains.dataframe.person.DataFrameTests
import org.junit.Test
import kotlin.reflect.full.memberProperties

class CodeGenerationTests : BaseTest(){

    val personClassName = Person::class.qualifiedName

    val personShortName = Person::class.simpleName!!

    @Test
    fun `generate marker interface`() {
        val property = DataFrameTests::class.memberProperties.first { it.name == "df" }
        val code = CodeGenerator().generate(df, property)
        val expectedDeclaration = """
            @DataFrameType(isOpen = false)
            interface DataFrameType1{
                val name: String
                val age: Int
                val city: String?
                val weight: Int?
            }""".trimIndent()

        val expectedConverter = "$" + "it.typed<DataFrameType1>()"

        code.size shouldBe 2
        code[0].trimIndent() shouldBe expectedDeclaration
        code[1] shouldBe expectedConverter
    }

    @Test
    fun `generate marker interface for nested data frame`() {
        val property = DataFrameTests::class.memberProperties.first { it.name == "df" }
        val grouped = df.move { name and city }.intoGroup("nameAndCity")
        val code = CodeGenerator().generate(grouped, property)
        val rowType = DataRow::class.simpleName
        val declaration1 = """
            @DataFrameType(isOpen = false)
            interface DataFrameType2{
                val name: String
                val city: String?
            }""".trimIndent()

        val declaration2 = """
            @DataFrameType(isOpen = false)
            interface DataFrameType1{
                val nameAndCity: $rowType<DataFrameType2>
                val age: Int
                val weight: Int?
            }""".trimIndent()

        val expectedConverter = "$" + "it.typed<DataFrameType1>()"

        code.size shouldBe 3
        code[0].trimIndent() shouldBe declaration1
        code[1].trimIndent() shouldBe declaration2
        code[2] shouldBe expectedConverter
    }

    @Test
    fun `generate extension properties`() {
        val code = CodeGenerator().generate(Person::class)

        val dfName = (DataFrameBase::class).simpleName
        val dfRowName = (DataRowBase::class).simpleName

        val expected = """
            val $dfName<$personClassName>.age: org.jetbrains.dataframe.api.columns.ColumnData<kotlin.Int> @JvmName("${personShortName}_age") get() = this["age"] as org.jetbrains.dataframe.api.columns.ColumnData<kotlin.Int>
            val $dfRowName<$personClassName>.age: Int @JvmName("${personShortName}_age") get() = this["age"] as Int
            val $dfName<$personClassName>.city: org.jetbrains.dataframe.api.columns.ColumnData<kotlin.String?> @JvmName("${personShortName}_city") get() = this["city"] as org.jetbrains.dataframe.api.columns.ColumnData<kotlin.String?>
            val $dfRowName<$personClassName>.city: String? @JvmName("${personShortName}_city") get() = this["city"] as String?
            val $dfName<$personClassName>.name: org.jetbrains.dataframe.api.columns.ColumnData<kotlin.String> @JvmName("${personShortName}_name") get() = this["name"] as org.jetbrains.dataframe.api.columns.ColumnData<kotlin.String>
            val $dfRowName<$personClassName>.name: String @JvmName("${personShortName}_name") get() = this["name"] as String
            val $dfName<$personClassName>.weight: org.jetbrains.dataframe.api.columns.ColumnData<kotlin.Int?> @JvmName("${personShortName}_weight") get() = this["weight"] as org.jetbrains.dataframe.api.columns.ColumnData<kotlin.Int?>
            val $dfRowName<$personClassName>.weight: Int? @JvmName("${personShortName}_weight") get() = this["weight"] as Int?
        """.trimIndent()
        code.joinToString("\n") shouldBe expected
    }

    @Test
    fun `generate derived interface`() {
        val codeGen = CodeGenerator()
        codeGen.generate(Person::class)
        val property = DataFrameTests::class.memberProperties.first { it.name == "df" }
        val code = codeGen.generate(df.filterNotNull { all() }, property)
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