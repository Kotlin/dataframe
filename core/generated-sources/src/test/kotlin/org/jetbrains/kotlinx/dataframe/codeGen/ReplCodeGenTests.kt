package org.jetbrains.kotlinx.dataframe.codeGen

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGenerator
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGeneratorImpl
import org.jetbrains.kotlinx.dataframe.impl.codeGen.join
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
        val code = repl.process(df).snippets.join()

        val marker = ReplCodeGeneratorImpl.markerInterfacePrefix
        val markerFull = Test1._DataFrameType::class.qualifiedName!!

        val expected =
            """
            @DataSchema
            interface $marker { }
            val $dfName<$marker>.age: $dataCol<$intName> by ColumnsScopeGeneratedPropertyDelegate("age")
            val $dfName<$marker>.city: $dataCol<$stringName?> by ColumnsScopeGeneratedPropertyDelegate("city")
            val $dfName<$marker>.name: $dataCol<$stringName> by ColumnsScopeGeneratedPropertyDelegate("name")
            val $dfName<$marker>.weight: $dataCol<$intName?> by ColumnsScopeGeneratedPropertyDelegate("weight")
            val $dfRowName<$marker>.age: $intName by DataRowGeneratedPropertyDelegate("age")
            val $dfRowName<$marker>.city: $stringName? by DataRowGeneratedPropertyDelegate("city")
            val $dfRowName<$marker>.name: $stringName by DataRowGeneratedPropertyDelegate("name")
            val $dfRowName<$marker>.weight: $intName? by DataRowGeneratedPropertyDelegate("weight")
            """.trimIndent()
        code shouldBe expected

        val code2 = repl.process<Test1._DataFrameType>()
        code2 shouldHaveSize 0

        val df3 = typed.filter { city != null }
        val code3 = repl.process(df3).snippets
        val marker3 = marker + "1"
        val expected3 =
            """
            @DataSchema
            interface $marker3 : $markerFull { }
            val $dfName<$marker3>.city: $dataCol<$stringName> by ColumnsScopeGeneratedPropertyDelegate("city")
            val $dfRowName<$marker3>.city: $stringName by DataRowGeneratedPropertyDelegate("city")
            """.trimIndent()

        code3.join() shouldBe expected3

        val code4 = repl.process<Test1._DataFrameType1>()
        code4 shouldHaveSize 0

        val df5 = typed.filter { weight != null }
        val code5 = repl.process(df5).snippets.join()
        val marker5 = marker + "2"
        val expected5 =
            """
            @DataSchema
            interface $marker5 : $markerFull { }
            val $dfName<$marker5>.weight: $dataCol<$intName> by ColumnsScopeGeneratedPropertyDelegate("weight")
            val $dfRowName<$marker5>.weight: $intName by DataRowGeneratedPropertyDelegate("weight")
            """.trimIndent()
        code5 shouldBe expected5

        val code6 = repl.process<Test1._DataFrameType2>()
        code6 shouldHaveSize 0
    }

    @Test
    fun `process markers union`() {
        val repl = ReplCodeGenerator.create()
        repl.process(typed.select { age and name })
        repl.process<Test2._DataFrameType>() shouldHaveSize 0
        repl.process(typed.select { city and weight })
        repl.process<Test2._DataFrameType1>() shouldHaveSize 0

        val expected =
            """
            @DataSchema
            interface ${Test2._DataFrameType2::class.simpleName!!} : ${Test2._DataFrameType::class.qualifiedName}, ${Test2._DataFrameType1::class.qualifiedName} { }
            
            """.trimIndent()

        val code = repl.process(typed).snippets.join().trimIndent()
        code shouldBe expected
    }

    @Test
    fun `process wrong marker inheritance`() {
        val repl = ReplCodeGenerator.create()
        repl.process(typed.select { age and name })
        repl.process<Test2._DataFrameType>() shouldHaveSize 0
        repl.process(typed.select { city and weight })
        // processed wrong marker (doesn't implement Test2.DataFrameType)
        repl.process<Test1._DataFrameType1>().let {
            it shouldHaveSize 4
            it.join().trim() shouldBe ""
        }

        val marker = Test2._DataFrameType2::class.simpleName!!
        val expected =
            """
            @DataSchema
            interface $marker : ${Test2._DataFrameType::class.qualifiedName} { }
            val $dfName<$marker>.city: $dataCol<$stringName?> by ColumnsScopeGeneratedPropertyDelegate("city")
            val $dfName<$marker>.weight: $dataCol<$intName?> by ColumnsScopeGeneratedPropertyDelegate("weight")
            val $dfRowName<$marker>.city: $stringName? by DataRowGeneratedPropertyDelegate("city")
            val $dfRowName<$marker>.weight: $intName? by DataRowGeneratedPropertyDelegate("weight")
            """.trimIndent()

        val code = repl.process(typed).snippets.join()
        code shouldBe expected
    }

    @Test
    fun `process overridden property`() {
        val repl = ReplCodeGenerator.create()
        repl.process<Test3.A>()
        repl.process<Test3.B>()
        repl.process<Test3.C>()
        val c = repl.process(Test3.df, Test3::df)
        c.snippets.shouldNotBeEmpty()
    }

    @Test
    fun `process diamond inheritance`() {
        val repl = ReplCodeGenerator.create()
        repl.process<Test3.A>()
        repl.process<Test3.B>()
        repl.process<Test3.D>()
        val c = repl.process(Test3.df, Test3::df)
        """val .*ColumnsScope<\w*>.x:""".toRegex().findAll(c.snippets.join()).count() shouldBe 1
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
        """val .*ColumnsScope<\w*>.a:""".toRegex().findAll(c.snippets.join()).count() shouldBe 1
    }
}
