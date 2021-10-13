package org.jetbrains.kotlinx.dataframe.internal.codeGen

import io.kotest.matchers.shouldBe
import org.jetbrains.dataframe.impl.codeGen.*
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrameBase
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.DataRowBase
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.under
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGeneratorImpl
import org.jetbrains.kotlinx.dataframe.internal.schema.extractSchema
import org.jetbrains.kotlinx.dataframe.person.BaseTest
import org.jetbrains.kotlinx.dataframe.person.Person
import org.junit.Test

class CodeGenerationTests : BaseTest() {

    val personClassName = Person::class.qualifiedName!!

    val personShortName = Person::class.simpleName!!

    val dfName = (DataFrameBase::class).qualifiedName
    val dfRowName = (DataRowBase::class).qualifiedName
    val dataCol = (DataColumn::class).qualifiedName!!
    val dataRow = (DataRow::class).qualifiedName!!
    val colGroup = (ColumnGroup::class).qualifiedName!!

    fun expectedProperties(fullTypeName: String, shortTypeName: String) = """
            val $dfName<$fullTypeName>.age: $dataCol<kotlin.Int> @JvmName("${shortTypeName}_age") get() = this["age"] as $dataCol<kotlin.Int>
            val $dfRowName<$fullTypeName>.age: kotlin.Int @JvmName("${shortTypeName}_age") get() = this["age"] as kotlin.Int
            val $dfName<$fullTypeName>.city: $dataCol<kotlin.String?> @JvmName("${shortTypeName}_city") get() = this["city"] as $dataCol<kotlin.String?>
            val $dfRowName<$fullTypeName>.city: kotlin.String? @JvmName("${shortTypeName}_city") get() = this["city"] as kotlin.String?
            val $dfName<$fullTypeName>.name: $dataCol<kotlin.String> @JvmName("${shortTypeName}_name") get() = this["name"] as $dataCol<kotlin.String>
            val $dfRowName<$fullTypeName>.name: kotlin.String @JvmName("${shortTypeName}_name") get() = this["name"] as kotlin.String
            val $dfName<$fullTypeName>.weight: $dataCol<kotlin.Int?> @JvmName("${shortTypeName}_weight") get() = this["weight"] as $dataCol<kotlin.Int?>
            val $dfRowName<$fullTypeName>.weight: kotlin.Int? @JvmName("${shortTypeName}_weight") get() = this["weight"] as kotlin.Int?
    """.trimIndent()

    @Test
    fun `generate marker interface`() {
        val codeGen = ReplCodeGenerator.create()
        val generated = codeGen.process(df, ::df)
        val typeName = ReplCodeGeneratorImpl.markerInterfacePrefix
        val expectedDeclaration = """
            @DataSchema(isOpen = false)
            interface $typeName
        """.trimIndent() + "\n" + expectedProperties(typeName, typeName)

        val expectedConverter = "it.typed<$typeName>()"

        generated.declarations shouldBe expectedDeclaration
        generated.converter("it") shouldBe expectedConverter

        val rowGenerated = codeGen.process(df[0], ::typedRow)
        rowGenerated.hasDeclarations shouldBe false
        rowGenerated.hasConverter shouldBe false
    }

    val row: AnyRow? = null

    val typedRow: DataRow<Person> = typed[0]

    @Test
    fun `generate marker interface for row`() {
        val property = ::row
        val generated = ReplCodeGenerator.create().process(df[0], property)
        val typeName = ReplCodeGeneratorImpl.markerInterfacePrefix
        val expectedDeclaration = """
            @DataSchema(isOpen = false)
            interface $typeName
        """.trimIndent() + "\n" + expectedProperties(typeName, typeName)

        val expectedConverter = "it.typed<$typeName>()"

        generated.declarations shouldBe expectedDeclaration
        generated.converter("it") shouldBe expectedConverter
    }

    @Test
    fun `generate marker interface for nested data frame`() {
        val property = ::df
        val grouped = df.move { name and city }.under("nameAndCity")
        val generated = ReplCodeGenerator.create().process(grouped, property)
        val type1 = ReplCodeGeneratorImpl.markerInterfacePrefix + "1"
        val type2 = ReplCodeGeneratorImpl.markerInterfacePrefix
        val declaration1 = """
            @DataSchema(isOpen = false)
            interface $type1
            val $dfName<$type1>.city: $dataCol<kotlin.String?> @JvmName("${type1}_city") get() = this["city"] as $dataCol<kotlin.String?>
            val $dfRowName<$type1>.city: kotlin.String? @JvmName("${type1}_city") get() = this["city"] as kotlin.String?
            val $dfName<$type1>.name: $dataCol<kotlin.String> @JvmName("${type1}_name") get() = this["name"] as $dataCol<kotlin.String>
            val $dfRowName<$type1>.name: kotlin.String @JvmName("${type1}_name") get() = this["name"] as kotlin.String
        """.trimIndent()

        val declaration2 = """
            @DataSchema(isOpen = false)
            interface $type2
            val $dfName<$type2>.age: $dataCol<kotlin.Int> @JvmName("${type2}_age") get() = this["age"] as $dataCol<kotlin.Int>
            val $dfRowName<$type2>.age: kotlin.Int @JvmName("${type2}_age") get() = this["age"] as kotlin.Int
            val $dfName<$type2>.nameAndCity: $colGroup<$type1> @JvmName("${type2}_nameAndCity") get() = this["nameAndCity"] as $colGroup<$type1>
            val $dfRowName<$type2>.nameAndCity: $dataRow<$type1> @JvmName("${type2}_nameAndCity") get() = this["nameAndCity"] as $dataRow<$type1>
            val $dfName<$type2>.weight: $dataCol<kotlin.Int?> @JvmName("${type2}_weight") get() = this["weight"] as $dataCol<kotlin.Int?>
            val $dfRowName<$type2>.weight: kotlin.Int? @JvmName("${type2}_weight") get() = this["weight"] as kotlin.Int?
        """.trimIndent()

        val expectedConverter = "it.typed<$type2>()"

        generated.declarations shouldBe declaration1 + "\n" + declaration2
        generated.converter("it") shouldBe expectedConverter
    }

    @Test
    fun `generate extension properties`() {
        val personClass = (Person::class).qualifiedName!!
        val expected = """
            @DataSchema
            interface $personClass
        """.trimIndent() + "\n" + expectedProperties(personClassName, personShortName)

        val code = CodeGenerator.create().generate<Person>(InterfaceGenerationMode.NoFields, extensionProperties = true).declarations
        code shouldBe expected
    }

    @Test
    fun `frame to markers`() {
        val f = SchemaProcessor.create("Temp")
        val marker = f.process(df.extractSchema(), true)
        marker.isOpen shouldBe true
        f.generatedMarkers shouldBe listOf(marker)
    }

    @Test
    fun `generate derived interface`() {
        val codeGen = CodeGenerator.create()
        val schema = df.dropNulls().extractSchema()
        val code = codeGen.generate(
            schema, "ValidPerson", true, true, isOpen = true, MarkerVisibility.IMPLICIT_PUBLIC,
            listOf(
                MarkersExtractor.get<Person>()
            )
        ).code.declarations
        val packageName = "org.jetbrains.kotlinx.dataframe"
        val expected = """
            @DataSchema
            interface ValidPerson : $personClassName{
                override val city: kotlin.String
                override val weight: kotlin.Int
            }
            val $packageName.DataFrameBase<ValidPerson>.city: $packageName.DataColumn<kotlin.String> @JvmName("ValidPerson_city") get() = this["city"] as $packageName.DataColumn<kotlin.String>
            val $packageName.DataRowBase<ValidPerson>.city: kotlin.String @JvmName("ValidPerson_city") get() = this["city"] as kotlin.String
            val $packageName.DataFrameBase<ValidPerson>.weight: $packageName.DataColumn<kotlin.Int> @JvmName("ValidPerson_weight") get() = this["weight"] as $packageName.DataColumn<kotlin.Int>
            val $packageName.DataRowBase<ValidPerson>.weight: kotlin.Int @JvmName("ValidPerson_weight") get() = this["weight"] as kotlin.Int
        """.trimIndent()
        code shouldBe expected
    }

    @Test
    fun `empty interface with properties`() {
        val codeGen = CodeGenerator.create()
        val code = codeGen.generate(df.extractSchema(), "Person", false, true, true).code.declarations
        val expected = """
            @DataSchema
            interface Person
        """.trimIndent() + "\n" + expectedProperties("Person", "Person")
        code shouldBe expected
    }

    @Test
    fun `interface with fields`() {
        val repl = CodeGenerator.create()
        val code = repl.generate(typed.extractSchema(), "DataType", true, false, false).code.declarations
        code shouldBe """
            @DataSchema(isOpen = false)
            interface DataType{
                val age: kotlin.Int
                val city: kotlin.String?
                val name: kotlin.String
                val weight: kotlin.Int?
            }
        """.trimIndent()
    }

    @Test
    fun `declaration with internal visibility`() {
        val repl = CodeGenerator.create()
        val code = repl.generate(typed.extractSchema(), "DataType", true, true, false, MarkerVisibility.INTERNAL).code.declarations
        val packageName = "org.jetbrains.kotlinx.dataframe"
        code shouldBe """
            @DataSchema(isOpen = false)
            internal interface DataType{
                val age: kotlin.Int
                val city: kotlin.String?
                val name: kotlin.String
                val weight: kotlin.Int?
            }
            internal val $packageName.DataFrameBase<DataType>.age: $packageName.DataColumn<kotlin.Int> @JvmName("DataType_age") get() = this["age"] as $packageName.DataColumn<kotlin.Int>
            internal val $packageName.DataRowBase<DataType>.age: kotlin.Int @JvmName("DataType_age") get() = this["age"] as kotlin.Int
            internal val $packageName.DataFrameBase<DataType>.city: $packageName.DataColumn<kotlin.String?> @JvmName("DataType_city") get() = this["city"] as $packageName.DataColumn<kotlin.String?>
            internal val $packageName.DataRowBase<DataType>.city: kotlin.String? @JvmName("DataType_city") get() = this["city"] as kotlin.String?
            internal val $packageName.DataFrameBase<DataType>.name: $packageName.DataColumn<kotlin.String> @JvmName("DataType_name") get() = this["name"] as $packageName.DataColumn<kotlin.String>
            internal val $packageName.DataRowBase<DataType>.name: kotlin.String @JvmName("DataType_name") get() = this["name"] as kotlin.String
            internal val $packageName.DataFrameBase<DataType>.weight: $packageName.DataColumn<kotlin.Int?> @JvmName("DataType_weight") get() = this["weight"] as $packageName.DataColumn<kotlin.Int?>
            internal val $packageName.DataRowBase<DataType>.weight: kotlin.Int? @JvmName("DataType_weight") get() = this["weight"] as kotlin.Int?
        """.trimIndent()
    }

    @Test
    fun `declaration with explicit public visibility`() {
        val repl = CodeGenerator.create()
        val code = repl.generate(typed.extractSchema(), "DataType", true, true, false, MarkerVisibility.EXPLICIT_PUBLIC).code.declarations
        val packageName = "org.jetbrains.kotlinx.dataframe"
        code shouldBe """
            @DataSchema(isOpen = false)
            public interface DataType{
                public val age: kotlin.Int
                public val city: kotlin.String?
                public val name: kotlin.String
                public val weight: kotlin.Int?
            }
            public val $packageName.DataFrameBase<DataType>.age: $packageName.DataColumn<kotlin.Int> @JvmName("DataType_age") get() = this["age"] as $packageName.DataColumn<kotlin.Int>
            public val $packageName.DataRowBase<DataType>.age: kotlin.Int @JvmName("DataType_age") get() = this["age"] as kotlin.Int
            public val $packageName.DataFrameBase<DataType>.city: $packageName.DataColumn<kotlin.String?> @JvmName("DataType_city") get() = this["city"] as $packageName.DataColumn<kotlin.String?>
            public val $packageName.DataRowBase<DataType>.city: kotlin.String? @JvmName("DataType_city") get() = this["city"] as kotlin.String?
            public val $packageName.DataFrameBase<DataType>.name: $packageName.DataColumn<kotlin.String> @JvmName("DataType_name") get() = this["name"] as $packageName.DataColumn<kotlin.String>
            public val $packageName.DataRowBase<DataType>.name: kotlin.String @JvmName("DataType_name") get() = this["name"] as kotlin.String
            public val $packageName.DataFrameBase<DataType>.weight: $packageName.DataColumn<kotlin.Int?> @JvmName("DataType_weight") get() = this["weight"] as $packageName.DataColumn<kotlin.Int?>
            public val $packageName.DataRowBase<DataType>.weight: kotlin.Int? @JvmName("DataType_weight") get() = this["weight"] as kotlin.Int?
        """.trimIndent()
    }

    @Test
    fun `column starts with number`() {
        val df = dataFrameOf("1a", "-b", "?c")(1, 2, 3)
        val repl = CodeGenerator.create()
        val declarations = repl.generate(df.extractSchema(), "DataType", false, true, false).code.declarations
        df.columnNames().forEach {
            val matches = "`$it`".toRegex().findAll(declarations).toList()
            matches.size shouldBe 2
        }
    }

    @Test
    fun patterns() {
        """^[\d]""".toRegex().matches("3fds")
    }
}
