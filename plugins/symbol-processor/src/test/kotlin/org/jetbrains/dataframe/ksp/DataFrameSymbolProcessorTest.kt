package org.jetbrains.dataframe.ksp

import com.tschuchort.compiletesting.SourceFile
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlin.codegen.state.ReceiverTypeAndTypeParameters
import kotlin.test.Test

class DataFrameSymbolProcessorTest {

    @Test
    fun `all`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
            sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                class OuterClass

                @DataSchema(isOpen = false)
                interface Hello {
                    val name: String
                    val `test name`: InnerClass
                    val nullableProperty: Int?
                    val a: () -> Unit
                    val d: List<List<*>>
                    
                    class InnerClass
                }

                val DataFrameBase<Hello>.col1: DataColumn<String> get() = name
                val DataFrameBase<Hello>.col2: DataColumn<Hello.InnerClass> get() = `test name`
                val DataFrameBase<Hello>.col3: DataColumn<Int?> get() = nullableProperty
                val DataFrameBase<Hello>.col4: DataColumn<() -> Unit> get() = a
                val DataFrameBase<Hello>.col5: DataColumn<List<List<*>>> get() = d
                
                val DataRowBase<Hello>.row1: String get() = name
                val DataRowBase<Hello>.row2: Hello.InnerClass get() = `test name`
                val DataRowBase<Hello>.row3: Int? get() = nullableProperty
                val DataRowBase<Hello>.row4: () -> Unit get() = a
                val DataRowBase<Hello>.row5: List<List<*>> get() = d
            """.trimIndent()))
        ))
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `functional type`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: () -> Unit?
                }

                val DataFrameBase<Hello>.test1: DataColumn<() -> Unit?> get() = a
                val DataRowBase<Hello>.test2: () -> Unit? get() = a
            """.trimIndent()))
            ))
        result.kspGeneratedFiles.find { it.name == "Hello${'$'}Extensions.kt" }?.readText()
            ?.shouldContain("DataFrameBase<Hello>.a: org.jetbrains.dataframe.columns.DataColumn<kotlin.Function0<kotlin.Unit?>>")
            ?.shouldContain("DataRowBase<Hello>.a: kotlin.Function0<kotlin.Unit?>")
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `suspend functional type`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: suspend () -> Unit?
                }

                val DataFrameBase<Hello>.test1: DataColumn<suspend () -> Unit?> get() = a
                val DataRowBase<Hello>.test2: suspend () -> Unit? get() = a
            """.trimIndent()))
            ))
        result.kspGeneratedFiles.find { it.name == "Hello${'$'}Extensions.kt" }?.readText()
            ?.shouldContain("DataFrameBase<Hello>.a: org.jetbrains.dataframe.columns.DataColumn<kotlin.coroutines.SuspendFunction0<kotlin.Unit?>>")
            ?.shouldContain("DataRowBase<Hello>.a: kotlin.coroutines.SuspendFunction0<kotlin.Unit?>")
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `nullable functional type`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: (() -> String)?
                }

                val DataFrameBase<Hello>.test1: DataColumn<(() -> String)?> get() = a
                val DataRowBase<Hello>.test2: (() -> String)? get() = a
            """.trimIndent()))
            ))
        result.kspGeneratedFiles.find { it.name == "Hello${'$'}Extensions.kt" }?.readText()
            ?.shouldContain("DataFrameBase<Hello>.a: org.jetbrains.dataframe.columns.DataColumn<kotlin.Function0<kotlin.String>?>")
            ?.shouldContain("DataRowBase<Hello>.a: kotlin.Function0<kotlin.String>?")
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `functional type with receiver`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: (Int.() -> String)?
                }

                val DataFrameBase<Hello>.test1: DataColumn<(Int.() -> String)?> get() = a
                val DataRowBase<Hello>.test2: (Int.() -> String)? get() = a
            """.trimIndent()))
            ))
        result.kspGeneratedFiles.find { it.name == "Hello${'$'}Extensions.kt" }?.readText()
            ?.shouldContain("DataFrameBase<Hello>.a: org.jetbrains.dataframe.columns.DataColumn<kotlin.Function1<kotlin.Int, kotlin.String>?>")
            ?.shouldContain("DataRowBase<Hello>.a: kotlin.Function1<kotlin.Int, kotlin.String>?")
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `inferred type`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: Int
                    val b get() = a
                }

                val DataFrameBase<Hello>.test1: DataColumn<Int> get() = b
                val DataRowBase<Hello>.test2: Int get() = b
            """.trimIndent()))
            ))
        result.kspGeneratedFiles.find { it.name == "Hello${'$'}Extensions.kt" }?.readText()
            ?.shouldContain("DataFrameBase<Hello>.b: org.jetbrains.dataframe.columns.DataColumn<kotlin.Int>")
            ?.shouldContain("DataRowBase<Hello>.b: kotlin.Int")
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `named lambda parameter`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: (a: String) -> Unit
                }

                val DataFrameBase<Hello>.test1: DataColumn<(a: String) -> Unit> get() = a
                val DataRowBase<Hello>.test2: (a: String) -> Unit get() = a
            """.trimIndent()))
            ))
        result.kspGeneratedFiles.find { it.name == "Hello${'$'}Extensions.kt" }?.readText()
            ?.shouldContain("DataFrameBase<Hello>.a: org.jetbrains.dataframe.columns.DataColumn<kotlin.Function1<kotlin.String, kotlin.Unit>>")
            ?.shouldContain("DataRowBase<Hello>.a: kotlin.Function1<kotlin.String, kotlin.Unit>")
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `typealias`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                class OuterClass
                typealias A = OuterClass

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: A
                }

                val DataFrameBase<Hello>.col1: DataColumn<A> get() = a
                val DataRowBase<Hello>.row1: A get() = a
                
            """.trimIndent()))
            ))
        result.kspGeneratedFiles.find { it.name == "Hello${'$'}Extensions.kt" }?.readText()
            ?.shouldContain("DataFrameBase<Hello>.a: org.jetbrains.dataframe.columns.DataColumn<A>")
            ?.shouldContain("DataRowBase<Hello>.a: A")
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `column name from annotation is used`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                @DataSchema(isOpen = false)
                interface Hello {
                    @ColumnName("test-name")
                    val `test name`: Int
                }

                val DataFrameBase<Hello>.test2: DataColumn<Int> get() = `test name`
                val DataRowBase<Hello>.test4: Int get() = `test name`
            """.trimIndent()))
            ))
        result.kspGeneratedFiles.find { it.name == "Hello${'$'}Extensions.kt" }?.readText()?.shouldContain("this[\"test-name\"]")
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `jvm name`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*


                @DataSchema(isOpen = false)
                interface Hello {
                    val a: Int
                }

                val DataFrameBase<Hello>.col1: DataColumn<Int> get() = a
                val DataRowBase<Hello>.row1: Int get() = a
                
            """.trimIndent()))
            ))
        result.kspGeneratedFiles.find { it.name == "Hello${'$'}Extensions.kt" }?.readText()
            ?.shouldContain("""DataFrameBase<Hello>.a: org.jetbrains.dataframe.columns.DataColumn<kotlin.Int> @JvmName("Hello_a")""")
            ?.shouldContain("""DataRowBase<Hello>.a: kotlin.Int @JvmName("Hello_a")""")
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `DataRow property`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                interface Marker

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: DataRow<Marker>
                }

                val DataFrameBase<Hello>.col1: ColumnGroup<Marker> get() = a
                val DataRowBase<Hello>.row1: DataRow<Marker> get() = a
                
            """.trimIndent()))
            ))
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `DataFrame property`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                interface Marker

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: DataFrame<Marker>
                }

                val DataFrameBase<Hello>.col1: DataColumn<DataFrame<Marker>> get() = a
                val DataRowBase<Hello>.row1: DataFrame<Marker> get() = a
                
            """.trimIndent()))
            ))
        result.successfulCompilation shouldBe true
    }


    @Test
    fun `extension accessible from same package`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                package org.example

                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                @DataSchema(isOpen = false)
                interface Hello {
                    val name: String
                }

                val DataFrameBase<Hello>.test1: DataColumn<String> get() = name
                val DataRowBase<Hello>.test2: String get() = name
            """.trimIndent()))
            ))
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `interface with type parameters`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                package org.example

                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                @DataSchema(isOpen = false)
                interface Hello <T> {
                    val name: T
                }
            """.trimIndent()))
            ))
        result.successfulCompilation shouldBe false
    }
}
