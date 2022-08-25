package org.jetbrains.kotlinx.dataframe.codeGen

import io.kotest.matchers.shouldBe
import org.jetbrains.dataframe.impl.codeGen.*
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColumnDescription
import org.jetbrains.kotlinx.dataframe.api.ValueCount
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.under
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGeneratorImpl
import org.jetbrains.kotlinx.dataframe.testSets.person.BaseTest
import org.jetbrains.kotlinx.dataframe.testSets.person.Person
import org.junit.Test

class CodeGenerationTests : BaseTest() {

    val personClassName = Person::class.qualifiedName!!

    val personShortName = Person::class.simpleName!!

    val dfName = (ColumnsContainer::class).simpleName!!
    val dfRowName = (DataRow::class).simpleName!!
    val dataCol = (DataColumn::class).simpleName!!
    val dataRow = (DataRow::class).simpleName!!
    val colGroup = (ColumnGroup::class).simpleName!!
    val stringName = String::class.simpleName!!
    val intName = Int::class.simpleName!!

    fun expectedProperties(fullTypeName: String, shortTypeName: String) = """
            val $dfName<$fullTypeName>.age: $dataCol<$intName> @JvmName("${shortTypeName}_age") get() = this["age"] as $dataCol<$intName>
            val $dfRowName<$fullTypeName>.age: $intName @JvmName("${shortTypeName}_age") get() = this["age"] as $intName
            val $dfName<$fullTypeName>.city: $dataCol<$stringName?> @JvmName("${shortTypeName}_city") get() = this["city"] as $dataCol<$stringName?>
            val $dfRowName<$fullTypeName>.city: $stringName? @JvmName("${shortTypeName}_city") get() = this["city"] as $stringName?
            val $dfName<$fullTypeName>.name: $dataCol<$stringName> @JvmName("${shortTypeName}_name") get() = this["name"] as $dataCol<$stringName>
            val $dfRowName<$fullTypeName>.name: $stringName @JvmName("${shortTypeName}_name") get() = this["name"] as $stringName
            val $dfName<$fullTypeName>.weight: $dataCol<$intName?> @JvmName("${shortTypeName}_weight") get() = this["weight"] as $dataCol<$intName?>
            val $dfRowName<$fullTypeName>.weight: $intName? @JvmName("${shortTypeName}_weight") get() = this["weight"] as $intName?
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

        val expectedConverter = "it.cast<$typeName>()"

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

        val expectedConverter = "it.cast<$typeName>()"

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
            
            val $dfName<$type1>.city: $dataCol<$stringName?> @JvmName("${type1}_city") get() = this["city"] as $dataCol<$stringName?>
            val $dfRowName<$type1>.city: $stringName? @JvmName("${type1}_city") get() = this["city"] as $stringName?
            val $dfName<$type1>.name: $dataCol<$stringName> @JvmName("${type1}_name") get() = this["name"] as $dataCol<$stringName>
            val $dfRowName<$type1>.name: $stringName @JvmName("${type1}_name") get() = this["name"] as $stringName
            
        """.trimIndent()

        val declaration2 = """
            @DataSchema(isOpen = false)
            interface $type2
            
            val $dfName<$type2>.age: $dataCol<$intName> @JvmName("${type2}_age") get() = this["age"] as $dataCol<$intName>
            val $dfRowName<$type2>.age: $intName @JvmName("${type2}_age") get() = this["age"] as $intName
            val $dfName<$type2>.nameAndCity: $colGroup<$type1> @JvmName("${type2}_nameAndCity") get() = this["nameAndCity"] as $colGroup<$type1>
            val $dfRowName<$type2>.nameAndCity: $dataRow<$type1> @JvmName("${type2}_nameAndCity") get() = this["nameAndCity"] as $dataRow<$type1>
            val $dfName<$type2>.weight: $dataCol<$intName?> @JvmName("${type2}_weight") get() = this["weight"] as $dataCol<$intName?>
            val $dfRowName<$type2>.weight: $intName? @JvmName("${type2}_weight") get() = this["weight"] as $intName?
        """.trimIndent()

        val expectedConverter = "it.cast<$type2>()"

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

        val code = CodeGenerator.create(useFqNames = false).generate<Person>(InterfaceGenerationMode.NoFields, extensionProperties = true).declarations
        code shouldBe expected
    }

    @Test
    fun `frame to markers`() {
        val f = SchemaProcessor.create("Temp")
        val marker = f.process(df.schema(), true)
        marker.isOpen shouldBe true
        f.generatedMarkers shouldBe listOf(marker)
    }

    @Test
    fun `generate derived interface`() {
        val codeGen = CodeGenerator.create()
        val schema = df.dropNulls().schema()
        val code = codeGen.generate(
            schema, "ValidPerson", true, true, isOpen = true, MarkerVisibility.IMPLICIT_PUBLIC,
            listOf(
                MarkersExtractor.get<Person>()
            )
        ).code.declarations
        val packageName = "org.jetbrains.kotlinx.dataframe"
        val expected = """
            @DataSchema
            interface ValidPerson : $personClassName {
                override val city: kotlin.String
                override val weight: kotlin.Int
            }
            
            val $packageName.ColumnsContainer<ValidPerson>.city: $packageName.DataColumn<kotlin.String> @JvmName("ValidPerson_city") get() = this["city"] as $packageName.DataColumn<kotlin.String>
            val $packageName.DataRow<ValidPerson>.city: kotlin.String @JvmName("ValidPerson_city") get() = this["city"] as kotlin.String
            val $packageName.ColumnsContainer<ValidPerson>.weight: $packageName.DataColumn<kotlin.Int> @JvmName("ValidPerson_weight") get() = this["weight"] as $packageName.DataColumn<kotlin.Int>
            val $packageName.DataRow<ValidPerson>.weight: kotlin.Int @JvmName("ValidPerson_weight") get() = this["weight"] as kotlin.Int
        """.trimIndent()
        code shouldBe expected
    }

    @Test
    fun `empty interface with properties`() {
        val codeGen = CodeGenerator.create(useFqNames = false)
        val code = codeGen.generate(df.schema(), "Person", false, true, true).code.declarations
        val expected = """
            @DataSchema
            interface Person
            
        """.trimIndent() + "\n" + expectedProperties("Person", "Person")
        code shouldBe expected
    }

    @Test
    fun `interface with fields`() {
        val repl = CodeGenerator.create()
        val code = repl.generate(typed.schema(), "DataType", true, false, false).code.declarations
        code shouldBe """
            @DataSchema(isOpen = false)
            interface DataType {
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
        val code = repl.generate(typed.schema(), "DataType", true, true, false, MarkerVisibility.INTERNAL).code.declarations
        val packageName = "org.jetbrains.kotlinx.dataframe"
        code shouldBe """
            @DataSchema(isOpen = false)
            internal interface DataType {
                val age: kotlin.Int
                val city: kotlin.String?
                val name: kotlin.String
                val weight: kotlin.Int?
            }
            
            internal val $packageName.ColumnsContainer<DataType>.age: $packageName.DataColumn<kotlin.Int> @JvmName("DataType_age") get() = this["age"] as $packageName.DataColumn<kotlin.Int>
            internal val $packageName.DataRow<DataType>.age: kotlin.Int @JvmName("DataType_age") get() = this["age"] as kotlin.Int
            internal val $packageName.ColumnsContainer<DataType>.city: $packageName.DataColumn<kotlin.String?> @JvmName("DataType_city") get() = this["city"] as $packageName.DataColumn<kotlin.String?>
            internal val $packageName.DataRow<DataType>.city: kotlin.String? @JvmName("DataType_city") get() = this["city"] as kotlin.String?
            internal val $packageName.ColumnsContainer<DataType>.name: $packageName.DataColumn<kotlin.String> @JvmName("DataType_name") get() = this["name"] as $packageName.DataColumn<kotlin.String>
            internal val $packageName.DataRow<DataType>.name: kotlin.String @JvmName("DataType_name") get() = this["name"] as kotlin.String
            internal val $packageName.ColumnsContainer<DataType>.weight: $packageName.DataColumn<kotlin.Int?> @JvmName("DataType_weight") get() = this["weight"] as $packageName.DataColumn<kotlin.Int?>
            internal val $packageName.DataRow<DataType>.weight: kotlin.Int? @JvmName("DataType_weight") get() = this["weight"] as kotlin.Int?
        """.trimIndent()
    }

    @Test
    fun `declaration with explicit public visibility`() {
        val repl = CodeGenerator.create()
        val code = repl.generate(typed.schema(), "DataType", true, true, false, MarkerVisibility.EXPLICIT_PUBLIC).code.declarations
        val packageName = "org.jetbrains.kotlinx.dataframe"
        code shouldBe """
            @DataSchema(isOpen = false)
            public interface DataType {
                public val age: kotlin.Int
                public val city: kotlin.String?
                public val name: kotlin.String
                public val weight: kotlin.Int?
            }
            
            public val $packageName.ColumnsContainer<DataType>.age: $packageName.DataColumn<kotlin.Int> @JvmName("DataType_age") get() = this["age"] as $packageName.DataColumn<kotlin.Int>
            public val $packageName.DataRow<DataType>.age: kotlin.Int @JvmName("DataType_age") get() = this["age"] as kotlin.Int
            public val $packageName.ColumnsContainer<DataType>.city: $packageName.DataColumn<kotlin.String?> @JvmName("DataType_city") get() = this["city"] as $packageName.DataColumn<kotlin.String?>
            public val $packageName.DataRow<DataType>.city: kotlin.String? @JvmName("DataType_city") get() = this["city"] as kotlin.String?
            public val $packageName.ColumnsContainer<DataType>.name: $packageName.DataColumn<kotlin.String> @JvmName("DataType_name") get() = this["name"] as $packageName.DataColumn<kotlin.String>
            public val $packageName.DataRow<DataType>.name: kotlin.String @JvmName("DataType_name") get() = this["name"] as kotlin.String
            public val $packageName.ColumnsContainer<DataType>.weight: $packageName.DataColumn<kotlin.Int?> @JvmName("DataType_weight") get() = this["weight"] as $packageName.DataColumn<kotlin.Int?>
            public val $packageName.DataRow<DataType>.weight: kotlin.Int? @JvmName("DataType_weight") get() = this["weight"] as kotlin.Int?
        """.trimIndent()
    }

    @Test
    fun `column starts with number`() {
        val df = dataFrameOf("1a", "-b", "?c")(1, 2, 3)
        val repl = CodeGenerator.create()
        val declarations = repl.generate(df.schema(), "DataType", false, true, false).code.declarations
        df.columnNames().forEach {
            val matches = "`$it`".toRegex().findAll(declarations).toList()
            matches.size shouldBe 2
        }
    }

    @Test
    fun generateApi() {
        val generator = CodeGenerator.create()
        val valueCount = generator.generate<ValueCount>(InterfaceGenerationMode.None, extensionProperties = true)
        println(valueCount.declarations)
        println()
        val describe = generator.generate<ColumnDescription>(InterfaceGenerationMode.None, extensionProperties = true)
        println(describe.declarations)
    }

    @Test
    fun patterns() {
        """^[\d]""".toRegex().matches("3fds")
    }
}
