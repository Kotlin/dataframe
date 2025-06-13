package org.jetbrains.kotlinx.dataframe.codeGen

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.asFrame
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGenerator
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGeneratorImpl
import org.jetbrains.kotlinx.dataframe.impl.codeGen.process
import org.jetbrains.kotlinx.dataframe.testSets.person.BaseTest
import org.jetbrains.kotlinx.dataframe.testSets.person.city
import org.jetbrains.kotlinx.dataframe.testSets.person.weight
import org.junit.Test

@Suppress("ktlint:standard:class-naming")
class ReplCodeGenTests : BaseTest() {

    val dfName = (ColumnsScope::class).simpleName!!
    val dfRowName = (DataRow::class).simpleName!!
    val dataCol = (DataColumn::class).simpleName!!
    val intName = Int::class.simpleName!!
    val stringName = String::class.simpleName!!

    class Test1 {
        @DataSchema
        interface _DataFrameType

        @DataSchema(isOpen = false)
        interface _DataFrameType1 : _DataFrameType

        @DataSchema(isOpen = false)
        interface _DataFrameType2 : _DataFrameType
    }

    class Test2 {
        @DataSchema
        interface _DataFrameType

        @DataSchema
        interface _DataFrameType1

        @DataSchema(isOpen = false)
        interface _DataFrameType2 :
            _DataFrameType,
            _DataFrameType1
    }

    object Test3 {
        @DataSchema
        interface A {
            val x: List<*>
        }

        @DataSchema
        interface B : A

        @DataSchema(isOpen = false)
        interface C : B {
            override val x: List<Int>
        }

        @DataSchema
        interface D : A

        val df = dataFrameOf("x")(listOf(1))
    }

    @Test
    fun `process derived markers`() {
        val repl = ReplCodeGenerator.create()
        val code = repl.process(df).declarations

        val marker = ReplCodeGeneratorImpl.markerInterfacePrefix
        val markerFull = Test1._DataFrameType::class.qualifiedName!!

        val expected =
            """
            @DataSchema
            interface $marker {
                val age: Int
                val city: String?
                val name: String
                val weight: Int?
            }
            
            val $dfName<$marker>.age: $dataCol<$intName> @JvmName("${marker}_age") get() = this["age"] as $dataCol<$intName>
            val $dfRowName<$marker>.age: $intName @JvmName("${marker}_age") get() = this["age"] as $intName
            val $dfName<$marker>.city: $dataCol<$stringName?> @JvmName("${marker}_city") get() = this["city"] as $dataCol<$stringName?>
            val $dfRowName<$marker>.city: $stringName? @JvmName("${marker}_city") get() = this["city"] as $stringName?
            val $dfName<$marker>.name: $dataCol<$stringName> @JvmName("${marker}_name") get() = this["name"] as $dataCol<$stringName>
            val $dfRowName<$marker>.name: $stringName @JvmName("${marker}_name") get() = this["name"] as $stringName
            val $dfName<$marker>.weight: $dataCol<$intName?> @JvmName("${marker}_weight") get() = this["weight"] as $dataCol<$intName?>
            val $dfRowName<$marker>.weight: $intName? @JvmName("${marker}_weight") get() = this["weight"] as $intName?
            """.trimIndent()
        code shouldBe expected

        val code2 = repl.process<Test1._DataFrameType>()
        code2 shouldBe ""

        val df3 = typed.filter { city != null }
        val code3 = repl.process(df3).declarations
        val marker3 = marker + "1"
        val expected3 =
            """
            @DataSchema
            interface $marker3 : $markerFull {
                override val city: String
            }
            
            val $dfName<$marker3>.city: $dataCol<$stringName> @JvmName("${marker3}_city") get() = this["city"] as $dataCol<$stringName>
            val $dfRowName<$marker3>.city: $stringName @JvmName("${marker3}_city") get() = this["city"] as $stringName
            """.trimIndent()

        code3 shouldBe expected3

        val code4 = repl.process<Test1._DataFrameType1>()
        code4 shouldBe ""

        val df5 = typed.filter { weight != null }
        val code5 = repl.process(df5).declarations
        val marker5 = marker + "2"
        val expected5 =
            """
            @DataSchema
            interface $marker5 : $markerFull {
                override val weight: Int
            }
            
            val $dfName<$marker5>.weight: $dataCol<$intName> @JvmName("${marker5}_weight") get() = this["weight"] as $dataCol<$intName>
            val $dfRowName<$marker5>.weight: $intName @JvmName("${marker5}_weight") get() = this["weight"] as $intName
            """.trimIndent()
        code5 shouldBe expected5

        val code6 = repl.process<Test1._DataFrameType2>()
        code6 shouldBe ""
    }

    @Test
    fun `process markers union`() {
        val repl = ReplCodeGenerator.create()
        repl.process(typed.select { age and name })
        repl.process<Test2._DataFrameType>() shouldBe ""
        repl.process(typed.select { city and weight })
        repl.process<Test2._DataFrameType1>() shouldBe ""

        val expected =
            """
            @DataSchema
            interface ${Test2._DataFrameType2::class.simpleName!!} : ${Test2._DataFrameType::class.qualifiedName}, ${Test2._DataFrameType1::class.qualifiedName} { }
            
            """.trimIndent()

        val code = repl.process(typed).declarations.trimIndent()
        code shouldBe expected
    }

    @Test
    fun `process wrong marker inheritance`() {
        val repl = ReplCodeGenerator.create()
        repl.process(typed.select { age and name })
        repl.process<Test2._DataFrameType>() shouldBe ""
        repl.process(typed.select { city and weight })
        // processed wrong marker (doesn't implement Test2.DataFrameType)
        repl.process<Test1._DataFrameType1>() shouldBe ""

        val marker = Test2._DataFrameType2::class.simpleName!!
        val expected =
            """
            @DataSchema
            interface $marker : ${Test2._DataFrameType::class.qualifiedName} {
                val city: String?
                val weight: Int?
            }
            
            val $dfName<$marker>.city: $dataCol<$stringName?> @JvmName("${marker}_city") get() = this["city"] as $dataCol<$stringName?>
            val $dfRowName<$marker>.city: $stringName? @JvmName("${marker}_city") get() = this["city"] as $stringName?
            val $dfName<$marker>.weight: $dataCol<$intName?> @JvmName("${marker}_weight") get() = this["weight"] as $dataCol<$intName?>
            val $dfRowName<$marker>.weight: $intName? @JvmName("${marker}_weight") get() = this["weight"] as $intName?
            """.trimIndent()

        val code = repl.process(typed).declarations.trimIndent()
        code shouldBe expected
    }

    @Test
    fun `process overridden property`() {
        val repl = ReplCodeGenerator.create()
        repl.process<Test3.A>()
        repl.process<Test3.B>()
        repl.process<Test3.C>()
        val c = repl.process(Test3.df, Test3::df)
        c.declarations.shouldNotBeEmpty()
    }

    @Test
    fun `process diamond inheritance`() {
        val repl = ReplCodeGenerator.create()
        repl.process<Test3.A>()
        repl.process<Test3.B>()
        repl.process<Test3.D>()
        val c = repl.process(Test3.df, Test3::df)
        """val .*ColumnsScope<\w*>.x:""".toRegex().findAll(c.declarations).count() shouldBe 1
    }

    object Test4 {

        @DataSchema
        interface A {
            val a: Int?
        }

        @DataSchema
        interface B {
            val a: Int?
        }

        val df = dataFrameOf("a")(1)
    }

    @Test
    fun `process duplicate override`() {
        val repl = ReplCodeGenerator.create()
        repl.process<Test4.A>()
        repl.process<Test4.B>()
        val c = repl.process(Test4.df, Test4::df)
        """val .*ColumnsScope<\w*>.a:""".toRegex().findAll(c.declarations).count() shouldBe 1
    }

    object Test5 {
        @DataSchema(isOpen = false)
        interface _DataFrameType1 {
            val a: Int
            val b: Int
        }

        val ColumnsScope<_DataFrameType1>.a: DataColumn<Int>
            @JvmName("_DataFrameType1_a")
            get() = this["a"] as DataColumn<Int>
        val DataRow<_DataFrameType1>.a: Int
            @JvmName("_DataFrameType1_a")
            get() = this["a"] as Int
        val ColumnsScope<_DataFrameType1>.b: DataColumn<Int>
            @JvmName("_DataFrameType1_b")
            get() = this["b"] as DataColumn<Int>
        val DataRow<_DataFrameType1>.b: Int
            @JvmName("_DataFrameType1_b")
            get() = this["b"] as Int

        @DataSchema
        interface _DataFrameType {
            val col: String
            val leaf: _DataFrameType1
        }

        val df = dataFrameOf("col" to listOf("a"), "leaf" to listOf(dataFrameOf("a")(1).first()))
            .convert("leaf").cast<AnyRow>().asFrame { it.add("c") { 3 } }
    }

    @Test
    fun `process closed inheritance override`() {
        // if ReplCodeGenerator would generate schemas with isOpen = true or with fields = false, _DataFrameType2 could implement _DataFrameType
        // but with isOpen = false and fields = true _DataFrameType2 : _DataFrameType produces incorrect override that couldn't be compiled
        // so we avoid this relation
        val repl = ReplCodeGenerator.create()
        repl.process<Test5._DataFrameType>()
        repl.process<Test5._DataFrameType1>()
        val c = repl.process(Test5.df, Test5::df)
        c.declarations shouldBe
            """
            @DataSchema
            interface _DataFrameType3 {
                val a: Int
                val c: Int
            }

            val $dfName<_DataFrameType3>.a: $dataCol<Int> @JvmName("_DataFrameType3_a") get() = this["a"] as $dataCol<Int>
            val $dfRowName<_DataFrameType3>.a: Int @JvmName("_DataFrameType3_a") get() = this["a"] as Int
            val $dfName<_DataFrameType3>.c: $dataCol<Int> @JvmName("_DataFrameType3_c") get() = this["c"] as $dataCol<Int>
            val $dfRowName<_DataFrameType3>.c: Int @JvmName("_DataFrameType3_c") get() = this["c"] as Int

            @DataSchema
            interface _DataFrameType2 {
                val col: String
                val leaf: _DataFrameType3
            }

            val $dfName<_DataFrameType2>.col: $dataCol<String> @JvmName("_DataFrameType2_col") get() = this["col"] as $dataCol<String>
            val $dfRowName<_DataFrameType2>.col: String @JvmName("_DataFrameType2_col") get() = this["col"] as String
            val $dfName<_DataFrameType2>.leaf: ColumnGroup<_DataFrameType3> @JvmName("_DataFrameType2_leaf") get() = this["leaf"] as ColumnGroup<_DataFrameType3>
            val $dfRowName<_DataFrameType2>.leaf: $dfRowName<_DataFrameType3> @JvmName("_DataFrameType2_leaf") get() = this["leaf"] as $dfRowName<_DataFrameType3>
            """.trimIndent()
    }
}
