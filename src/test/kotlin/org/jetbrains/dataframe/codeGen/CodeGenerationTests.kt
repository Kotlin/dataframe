package org.jetbrains.dataframe.codeGen

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.DataColumn
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
        val generated = CodeGeneratorImpl().generate(df, property)!!
        val expectedDeclaration = """
            @DataFrameType(isOpen = false)
            interface DataFrameType1{
                val name: String
                val age: Int
                val city: String?
                val weight: Int?
            }""".trimIndent()

        val expectedConverter = "it.typed<DataFrameType1>()"

        generated.declarations shouldBe expectedDeclaration
        generated.converter("it") shouldBe expectedConverter
    }

    @Test
    fun `generate marker interface for nested data frame`() {
        val property = DataFrameTests::class.memberProperties.first { it.name == "df" }
        val grouped = df.move { name and city }.intoGroup("nameAndCity")
        val generated = CodeGeneratorImpl().generate(grouped, property)!!
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

        val expectedConverter = "it.typed<DataFrameType1>()"

        generated.declarations shouldBe declaration1 + "\n" + declaration2
        generated.converter("it") shouldBe expectedConverter
    }

    @Test
    fun `generate extension properties`() {
        val code = CodeGeneratorImpl().generateExtensionProperties(Person::class)

        val dfName = (DataFrameBase::class).simpleName
        val dfRowName = (DataRowBase::class).simpleName
        val dataCol = (DataColumn::class).qualifiedName!!
        val expected = """
            val $dfName<$personClassName>.age: $dataCol<kotlin.Int> @JvmName("${personShortName}_age") get() = this["age"] as $dataCol<kotlin.Int>
            val $dfRowName<$personClassName>.age: Int @JvmName("${personShortName}_age") get() = this["age"] as Int
            val $dfName<$personClassName>.city: $dataCol<kotlin.String?> @JvmName("${personShortName}_city") get() = this["city"] as $dataCol<kotlin.String?>
            val $dfRowName<$personClassName>.city: String? @JvmName("${personShortName}_city") get() = this["city"] as String?
            val $dfName<$personClassName>.name: $dataCol<kotlin.String> @JvmName("${personShortName}_name") get() = this["name"] as $dataCol<kotlin.String>
            val $dfRowName<$personClassName>.name: String @JvmName("${personShortName}_name") get() = this["name"] as String
            val $dfName<$personClassName>.weight: $dataCol<kotlin.Int?> @JvmName("${personShortName}_weight") get() = this["weight"] as $dataCol<kotlin.Int?>
            val $dfRowName<$personClassName>.weight: Int? @JvmName("${personShortName}_weight") get() = this["weight"] as Int?
        """.trimIndent()
        code shouldBe expected
    }

    @Test
    fun `generate derived interface`() {
        val codeGen = CodeGeneratorImpl()
        codeGen.generateExtensionProperties(Person::class)
        val property = DataFrameTests::class.memberProperties.first { it.name == "df" }
        val generated = codeGen.generate(df.filterNotNull { all() }, property)!!
        val expected = """
            @DataFrameType(isOpen = false)
            interface DataFrameType1 : $personClassName{
                override val city: String
                override val weight: Int
            }
        """.trimIndent()
        generated.declarations shouldBe expected
    }
}