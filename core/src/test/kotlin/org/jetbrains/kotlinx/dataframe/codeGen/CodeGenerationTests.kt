package org.jetbrains.kotlinx.dataframe.codeGen

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.default
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.generateDataClasses
import org.jetbrains.kotlinx.dataframe.api.generateInterfaces
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toCodeString
import org.jetbrains.kotlinx.dataframe.api.under
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGenerator
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGeneratorImpl
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters
import org.jetbrains.kotlinx.dataframe.testSets.person.BaseTest
import org.jetbrains.kotlinx.dataframe.testSets.person.Person
import org.junit.Test
import kotlin.test.assertEquals

class CodeGenerationTests : BaseTest() {

    @Test
    fun `generateInterfaces with PredefinedName and nested structures`() {
        val df = dataFrameOf("a", "b")(
            1,
            2,
        ).move("a", "b").under("group")
        val code = df.generateInterfaces("Marker", nestedMarkerNameProvider = MarkerNameProvider.PredefinedName)
        val expected =
            """
            @DataSchema
            interface Marker {
                val group: Marker1

                @DataSchema(isOpen = false)
                interface Marker1 {
                    val a: Int
                    val b: Int
                }
            }
            """.trimIndent()
        assertEquals(expected, code.value)
    }

    @Test
    fun `generateDataClasses with PredefinedName and nested structures`() {
        val df = dataFrameOf("a", "b")(
            1,
            2,
        ).move("a", "b").under("group")
        val code = df.generateDataClasses("Marker", nestedMarkerNameProvider = MarkerNameProvider.PredefinedName)
        val expected =
            """
            @DataSchema
            data class Marker(
                val group: Marker1
            ) {
                @DataSchema
                data class Marker1(
                    val a: Int,
                    val b: Int
                )
            }
            """.trimIndent()
        assertEquals(expected, code.value)
    }

    @Test
    fun `generateDataClasses with nested structures`() {
        val df = dataFrameOf("a", "b")(
            1,
            2,
        ).move("a", "b").under("group")
        val code = df.generateDataClasses("Marker")
        val expected =
            """
            @DataSchema
            data class Marker(
                val group: Group
            ) {
                @DataSchema
                data class Group(
                    val a: Int,
                    val b: Int
                )
            }
            """.trimIndent()
        assertEquals(expected, code.value)
    }

    @Test
    fun `generateDataClasses with FrameColumn structure`() {
        val df = dataFrameOf("personalInfo" to columnOf(dataFrameOf("age" to columnOf(19))))
        val code = df.generateDataClasses("Accounts")
        val expected =
            """
            @DataSchema
            data class Accounts(
                val personalInfo: List<PersonalInfo>
            ) {
                @DataSchema
                data class PersonalInfo(
                    val age: Int
                )
            }
            """.trimIndent()
        assertEquals(expected, code.value)
    }

    @Test
    fun `resolve name clash between nested marker and property`() {
        val df = dataFrameOf("Person", "b")(
            1,
            2,
        ).move("b").under("_person")

        val code = df.generateInterfaces("Marker")

        val expected = """
            @DataSchema
            interface Marker {
                val Person: Int
                val _person: Person1

                @DataSchema(isOpen = false)
                interface Person1 {
                    val b: Int
                }
            }
        """

        assertEquals(expected.trimIndent().trim(), code.value)
    }

    @Test
    fun `resolve name clash between nested marker and property with deep nesting`() {
        val df = dataFrameOf("Person", "b")(
            1,
            2,
        ).group("b").into { pathOf("bb", "_person") }

        val code = df.generateInterfaces("Marker")

        val expected = """
            @DataSchema
            interface Marker {
                val Person: Int
                val bb: Bb
            
                @DataSchema(isOpen = false)
                interface Person1 {
                    val b: Int
                }
            
                @DataSchema(isOpen = false)
                interface Bb {
                    val _person: Person1
                }
            }
        """

        assertEquals(expected.trimIndent().trim(), code.value)
    }

    @Test
    fun `generateInterfaces with PredefinedName`() {
        val df = dataFrameOf("a", "b")(
            1,
            2,
        ).move("a", "b").under("group")
        val code = df.generateInterfaces("Marker", nestedMarkerNameProvider = MarkerNameProvider.PredefinedName)
        val expected =
            """
            @DataSchema
            interface Marker {
                val group: Marker1

                @DataSchema(isOpen = false)
                interface Marker1 {
                    val a: Int
                    val b: Int
                }
            }
            """.trimIndent()
        assertEquals(expected, code.value)
    }

    val personClassName = Person::class.qualifiedName!!

    val personShortName = Person::class.simpleName!!

    val dfName = (ColumnsScope::class).simpleName!!
    val dfRowName = (DataRow::class).simpleName!!
    val dataCol = (DataColumn::class).simpleName!!
    val dataRow = (DataRow::class).simpleName!!
    val colGroup = (ColumnGroup::class).simpleName!!
    val stringName = String::class.simpleName!!
    val intName = Int::class.simpleName!!

    fun expectedProperties(fullTypeName: String, shortTypeName: String, addNullable: Boolean = false) =
        buildString {
            appendLine(
                """val $dfName<$fullTypeName>.age: $dataCol<$intName> @JvmName("${shortTypeName}_age") get() = this["age"] as $dataCol<$intName>""",
            )
            appendLine(
                """val $dfRowName<$fullTypeName>.age: $intName @JvmName("${shortTypeName}_age") get() = this["age"] as $intName""",
            )
            if (addNullable) {
                appendLine(
                    """val $dfName<$fullTypeName?>.age: $dataCol<$intName?> @JvmName("Nullable${shortTypeName}_age") get() = this["age"] as $dataCol<$intName?>""",
                )
                appendLine(
                    """val $dfRowName<$fullTypeName?>.age: $intName? @JvmName("Nullable${shortTypeName}_age") get() = this["age"] as $intName?""",
                )
            }
            appendLine(
                """val $dfName<$fullTypeName>.city: $dataCol<$stringName?> @JvmName("${shortTypeName}_city") get() = this["city"] as $dataCol<$stringName?>""",
            )
            appendLine(
                """val $dfRowName<$fullTypeName>.city: $stringName? @JvmName("${shortTypeName}_city") get() = this["city"] as $stringName?""",
            )
            if (addNullable) {
                appendLine(
                    """val $dfName<$fullTypeName?>.city: $dataCol<$stringName?> @JvmName("Nullable${shortTypeName}_city") get() = this["city"] as $dataCol<$stringName?>""",
                )
                appendLine(
                    """val $dfRowName<$fullTypeName?>.city: $stringName? @JvmName("Nullable${shortTypeName}_city") get() = this["city"] as $stringName?""",
                )
            }
            appendLine(
                """val $dfName<$fullTypeName>.name: $dataCol<$stringName> @JvmName("${shortTypeName}_name") get() = this["name"] as $dataCol<$stringName>""",
            )
            appendLine(
                """val $dfRowName<$fullTypeName>.name: $stringName @JvmName("${shortTypeName}_name") get() = this["name"] as $stringName""",
            )
            if (addNullable) {
                appendLine(
                    """val $dfName<$fullTypeName?>.name: $dataCol<$stringName?> @JvmName("Nullable${shortTypeName}_name") get() = this["name"] as $dataCol<$stringName?>""",
                )
                appendLine(
                    """val $dfRowName<$fullTypeName?>.name: $stringName? @JvmName("Nullable${shortTypeName}_name") get() = this["name"] as $stringName?""",
                )
            }
            appendLine(
                """val $dfName<$fullTypeName>.weight: $dataCol<$intName?> @JvmName("${shortTypeName}_weight") get() = this["weight"] as $dataCol<$intName?>""",
            )
            append(
                """val $dfRowName<$fullTypeName>.weight: $intName? @JvmName("${shortTypeName}_weight") get() = this["weight"] as $intName?""",
            )
            if (addNullable) {
                appendLine("")
                appendLine(
                    """val $dfName<$fullTypeName?>.weight: $dataCol<$intName?> @JvmName("Nullable${shortTypeName}_weight") get() = this["weight"] as $dataCol<$intName?>""",
                )
                append(
                    """val $dfRowName<$fullTypeName?>.weight: $intName? @JvmName("Nullable${shortTypeName}_weight") get() = this["weight"] as $intName?""",
                )
            }
        }

    @Test
    fun `generate marker interface`() {
        val codeGen = ReplCodeGenerator.create()
        val generated = codeGen.process(df, ::df)
        val typeName = ReplCodeGeneratorImpl.markerInterfacePrefix
        val expectedDeclaration =
            """
            @DataSchema
            interface $typeName {
                val age: Int
                val city: String?
                val name: String
                val weight: Int?
            }
            
            """.trimIndent() + "\n" + expectedProperties(typeName, typeName)

        val expectedConverter = "it.cast<$typeName>()"

        assertEquals(expectedDeclaration, generated.declarations)
        assertEquals(expectedConverter, generated.typeCastGenerator("it"))

        val rowGenerated = codeGen.process(df[0], ::typedRow)
        rowGenerated.hasDeclarations shouldBe true
        rowGenerated.hasCaster shouldBe true
    }

    val row: AnyRow? = null

    val typedRow: DataRow<Person> = typed[0]

    @Test
    fun `generate marker interface for row`() {
        val property = ::row
        val generated = ReplCodeGenerator.create().process(df[0], property)
        val typeName = ReplCodeGeneratorImpl.markerInterfacePrefix
        val expectedDeclaration =
            """
            @DataSchema
            interface $typeName {
                val age: Int
                val city: String?
                val name: String
                val weight: Int?
            }
            
            """.trimIndent() + "\n" + expectedProperties(typeName, typeName)

        val expectedConverter = "it.cast<$typeName>()"

        assertEquals(expectedDeclaration, generated.declarations)
        assertEquals(expectedConverter, generated.typeCastGenerator("it"))
    }

    @Test
    fun `generate marker interface for nested dataframe`() {
        val property = ::df
        val grouped = df.move { name and city }.under("nameAndCity")
        val generated = ReplCodeGenerator.create().process(grouped, property)
        val nestedType = "NameAndCity"
        val type2 = ReplCodeGeneratorImpl.markerInterfacePrefix
        val declaration1 =
            """
            @DataSchema(isOpen = false)
            interface $nestedType {
                val city: String?
                val name: String
            }

            val $dfName<$nestedType>.city: $dataCol<$stringName?> @JvmName("${nestedType}_city") get() = this["city"] as $dataCol<$stringName?>
            val $dfRowName<$nestedType>.city: $stringName? @JvmName("${nestedType}_city") get() = this["city"] as $stringName?
            val $dfName<$nestedType>.name: $dataCol<$stringName> @JvmName("${nestedType}_name") get() = this["name"] as $dataCol<$stringName>
            val $dfRowName<$nestedType>.name: $stringName @JvmName("${nestedType}_name") get() = this["name"] as $stringName

            """.trimIndent()

        val declaration2 =
            """
            @DataSchema
            interface $type2 {
                val age: Int
                val nameAndCity: $nestedType
                val weight: Int?
            }

            val $dfName<$type2>.age: $dataCol<$intName> @JvmName("${type2}_age") get() = this["age"] as $dataCol<$intName>
            val $dfRowName<$type2>.age: $intName @JvmName("${type2}_age") get() = this["age"] as $intName
            val $dfName<$type2>.nameAndCity: $colGroup<$nestedType> @JvmName("${type2}_nameAndCity") get() = this["nameAndCity"] as $colGroup<$nestedType>
            val $dfRowName<$type2>.nameAndCity: $dataRow<$nestedType> @JvmName("${type2}_nameAndCity") get() = this["nameAndCity"] as $dataRow<$nestedType>
            val $dfName<$type2>.weight: $dataCol<$intName?> @JvmName("${type2}_weight") get() = this["weight"] as $dataCol<$intName?>
            val $dfRowName<$type2>.weight: $intName? @JvmName("${type2}_weight") get() = this["weight"] as $intName?
            """.trimIndent()

        val expectedConverter = "it.cast<$type2>()"

        assertEquals(declaration1 + "\n" + declaration2, generated.declarations)
        generated.typeCastGenerator("it") shouldBe expectedConverter
    }

    @Test
    fun `generate extension properties`() {
        val personClass = (Person::class).qualifiedName!!
        val expected =
            """
            @DataSchema
            interface $personClass { }
            """.trimIndent() + "\n" + expectedProperties(personClassName, personShortName, addNullable = true)

        val code = CodeGenerator
            .create(useFqNames = false)
            .generate<Person>(InterfaceGenerationMode.NoFields, extensionProperties = true)
            .declarations
        assertEquals(expected, code)
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
            schema = schema,
            name = "ValidPerson",
            fields = true,
            extensionProperties = true,
            isOpen = true,
            visibility = MarkerVisibility.IMPLICIT_PUBLIC,
            knownMarkers = listOf(MarkersExtractor.get<Person>()),
        ).code.declarations
        val packageName = "org.jetbrains.kotlinx.dataframe"
        val expected =
            """
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
        assertEquals(expected, code)
    }

    @Test
    fun `empty interface with properties`() {
        val codeGen = CodeGenerator.create(useFqNames = false)
        val code = codeGen.generate(df.schema(), "Person", false, true, true).code.declarations
        val expected =
            """
            @DataSchema
            interface Person { }
            """.trimIndent() + "\n\n" + expectedProperties("Person", "Person")
        assertEquals(expected, code)
    }

    @Test
    fun `interface with fields`() {
        val repl = CodeGenerator.create()
        val code = repl.generate(typed.schema(), "DataType", true, false, false).code.declarations
        val expected =
            """
            @DataSchema(isOpen = false)
            interface DataType {
                val age: kotlin.Int
                val city: kotlin.String?
                val name: kotlin.String
                val weight: kotlin.Int?
            }
            """.trimIndent()
        assertEquals(expected, code)
    }

    @Test
    fun `declaration with internal visibility`() {
        val repl = CodeGenerator.create()
        val code =
            repl.generate(typed.schema(), "DataType", true, true, false, MarkerVisibility.INTERNAL).code.declarations
        val packageName = "org.jetbrains.kotlinx.dataframe"
        val expected =
            """
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
        assertEquals(expected, code)
    }

    @Test
    fun `declaration with explicit public visibility`() {
        val repl = CodeGenerator.create()
        val code = repl.generate(
            typed.schema(),
            "DataType",
            true,
            true,
            false,
            MarkerVisibility.EXPLICIT_PUBLIC,
        ).code.declarations
        val packageName = "org.jetbrains.kotlinx.dataframe"
        val expected = """
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
            """

        assertEquals(expected.trimIndent(), code)
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
    fun `check name normalization for generated data classes`() {
        val code = dataFrameOf("my_name")(1).generateDataClasses()
        val expected =
            """
            @DataSchema
            data class DataEntry(
                @ColumnName("my_name")
                val myName: Int
            )
            """.trimIndent()
        assertEquals(expected, code.value)
    }

    @Test
    fun patterns() {
        """^[\d]""".toRegex().matches("3fds")
    }

    @Test
    fun shortenKotlinCollections() {
        val df = dataFrameOf("a" to columnOf(listOf("abc")))
        val expected = """
            @DataSchema
            data class DataEntry(
                val a: List<String>
            )
        """.trimIndent()
        assertEquals(expected, df.generateDataClasses().value)
    }

    // region Tests for generateX functions

    @Test
    fun `check method generateDataClasses`() {
        val df = typed.groupBy { name }.toDataFrame()
        val code1 = df.generateDataClasses()
        val code2 = df.schema().generateDataClasses("Person")

        val expected = CodeGenerator.create(useFqNames = false).generate(
            schema = df.schema(),
            name = "Person",
            fields = true,
            extensionProperties = false,
            isOpen = false,
            asDataClass = true,
            visibility = MarkerVisibility.IMPLICIT_PUBLIC,
            fieldNameNormalizer = NameNormalizer.default,
        ).code.declarations.toCodeString()

        code1 shouldBe expected
        code2 shouldBe expected
    }

    @Test
    fun `DataFrame generateInterfaces`() {
        val code = typed.generateInterfaces()

        val expected = CodeGenerator.create(useFqNames = false).generate(
            schema = df.schema(),
            name = "Person",
            fields = true,
            extensionProperties = false,
            isOpen = true,
            asDataClass = false,
            visibility = MarkerVisibility.IMPLICIT_PUBLIC,
            fieldNameNormalizer = NameNormalizer.default,
        ).code.declarations.toCodeString()

        code.value shouldBe expected.value
    }

    @Test
    fun `DataFrame generateInterfaces - with marker name`() {
        val code = typed.generateInterfaces("CustomInterface")

        val expected = CodeGenerator.create(useFqNames = false).generate(
            schema = df.schema(),
            name = "CustomInterface",
            fields = true,
            extensionProperties = false,
            isOpen = true,
            asDataClass = false,
            visibility = MarkerVisibility.IMPLICIT_PUBLIC,
            fieldNameNormalizer = NameNormalizer.default,
        ).code.declarations.toCodeString()

        code.value shouldBe expected.value
    }

    @Test
    fun `DataFrame generateDataClasses - with custom parameters`() {
        val code = typed.generateDataClasses(
            markerName = "CustomDataClass",
            extensionProperties = true,
            visibility = MarkerVisibility.INTERNAL,
            useFqNames = true,
        )

        val expected = CodeGenerator.create(useFqNames = true).generate(
            schema = df.schema(),
            name = "CustomDataClass",
            fields = true,
            extensionProperties = true,
            isOpen = true,
            asDataClass = true,
            visibility = MarkerVisibility.INTERNAL,
            fieldNameNormalizer = NameNormalizer.default,
        ).code.declarations.toCodeString()

        code.value shouldBe expected.value
    }

    @Test
    fun `DataFrameSchema generateInterfaces`() {
        val schema = typed.schema()
        val code = schema.generateInterfaces("SchemaInterface")

        val expected = CodeGenerator.create(useFqNames = false).generate(
            schema = schema,
            name = "SchemaInterface",
            fields = true,
            extensionProperties = false,
            isOpen = true,
            asDataClass = false,
            visibility = MarkerVisibility.IMPLICIT_PUBLIC,
            fieldNameNormalizer = NameNormalizer.default,
        ).code.declarations.toCodeString()

        code.value shouldBe expected.value
    }

    @Test
    fun `DataFrameSchema generateDataClasses - with default parameters`() {
        val schema = typed.schema()
        val code = schema.generateDataClasses("SchemaDataClass")

        val expected = CodeGenerator.create(useFqNames = false).generate(
            schema = schema,
            name = "SchemaDataClass",
            fields = true,
            extensionProperties = false,
            isOpen = false,
            asDataClass = true,
            visibility = MarkerVisibility.IMPLICIT_PUBLIC,
            fieldNameNormalizer = NameNormalizer.default,
        ).code.declarations.toCodeString()

        code.value shouldBe expected.value
    }

    @Test
    fun `DataFrameSchema generateDataClasses - with custom parameters`() {
        val schema = typed.schema()
        val code = schema.generateDataClasses(
            markerName = "SchemaDataClass",
            extensionProperties = true,
            visibility = MarkerVisibility.EXPLICIT_PUBLIC,
            useFqNames = false,
        )

        val expected = CodeGenerator.create(useFqNames = false).generate(
            schema = schema,
            name = "SchemaDataClass",
            fields = true,
            extensionProperties = true,
            isOpen = false,
            asDataClass = true,
            visibility = MarkerVisibility.EXPLICIT_PUBLIC,
            fieldNameNormalizer = NameNormalizer.default,
        ).code.declarations.toCodeString()

        code.value shouldBe expected.value
    }

    @Test
    fun `DataFrame generateDataClasses - with name normalizer`() {
        val dfWithSpecialNames = dataFrameOf("my_column", "another column", "third-column")(1, "test", 3.14)
        val nameNormalizer = NameNormalizer { it.toCamelCaseByDelimiters() + "1" }
        val code = dfWithSpecialNames.generateDataClasses(
            nameNormalizer = nameNormalizer,
        )

        val expected = CodeGenerator.create(useFqNames = false).generate(
            schema = dfWithSpecialNames.schema(),
            name = "DataEntry",
            fields = true,
            extensionProperties = false,
            isOpen = false,
            asDataClass = true,
            visibility = MarkerVisibility.IMPLICIT_PUBLIC,
            fieldNameNormalizer = nameNormalizer,
        ).code.declarations.toCodeString()

        code.value shouldBe expected.value
    }

    // endregion
}
