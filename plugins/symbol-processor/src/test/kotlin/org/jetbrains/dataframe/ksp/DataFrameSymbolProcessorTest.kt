package org.jetbrains.dataframe.ksp

import com.tschuchort.compiletesting.SourceFile
import io.kotest.assertions.asClue
import io.kotest.inspectors.forExactly
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import org.jetbrains.dataframe.ksp.runner.KotlinCompileTestingCompilationResult
import org.jetbrains.dataframe.ksp.runner.KspCompilationTestRunner
import org.jetbrains.dataframe.ksp.runner.TestCompilationParameters
import org.junit.Before
import kotlin.test.Test

class DataFrameSymbolProcessorTest {

    companion object {
        val imports = """
            import org.jetbrains.kotlinx.dataframe.annotations.*
            import org.jetbrains.kotlinx.dataframe.columns.*
            import org.jetbrains.kotlinx.dataframe.* 
        """.trimIndent()

        const val generatedFile = "Hello${'$'}Extensions.kt"
    }

    @Before
    fun setup(){
        KspCompilationTestRunner.compilationDir.deleteRecursively()
    }

    @Test
    fun `all interface`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
            sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                class OuterClass

                @DataSchema(isOpen = false)
                interface Hello {
                    val name: String
                    val `test name`: NestedClass
                    val nullableProperty: Int?
                    val a: () -> Unit
                    val d: List<List<*>>
                    
                    class NestedClass
                }

                val ColumnsContainer<Hello>.col1: DataColumn<String> get() = name
                val ColumnsContainer<Hello>.col2: DataColumn<Hello.NestedClass> get() = `test name`
                val ColumnsContainer<Hello>.col3: DataColumn<Int?> get() = nullableProperty
                val ColumnsContainer<Hello>.col4: DataColumn<() -> Unit> get() = a
                val ColumnsContainer<Hello>.col5: DataColumn<List<List<*>>> get() = d
                
                val DataRow<Hello>.row1: String get() = name
                val DataRow<Hello>.row2: Hello.NestedClass get() = `test name`
                val DataRow<Hello>.row3: Int? get() = nullableProperty
                val DataRow<Hello>.row4: () -> Unit get() = a
                val DataRow<Hello>.row5: List<List<*>> get() = d
            """.trimIndent()))
        )
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `all data class`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                class OuterClass

                @DataSchema(isOpen = false)
                data class Hello(
                    val name: String,
                    val `test name`: InnerClass,
                    val nestedClass: Nested,
                    val nullableProperty: Int?,
                ) {
                    val a: () -> Unit = TODO()
                    val d: List<List<*>> = TODO() 
                    inner class InnerClass
                    class Nested
                }

                val ColumnsContainer<Hello>.col1: DataColumn<String> get() = name
                val ColumnsContainer<Hello>.col2: DataColumn<Hello.InnerClass> get() = `test name`
                val ColumnsContainer<Hello>.col3: DataColumn<Int?> get() = nullableProperty
                val ColumnsContainer<Hello>.col4: DataColumn<() -> Unit> get() = a
                val ColumnsContainer<Hello>.col5: DataColumn<List<List<*>>> get() = d
                val ColumnsContainer<Hello>.col6: DataColumn<Hello.Nested> get() = nestedClass
                
                val DataRow<Hello>.row1: String get() = name
                val DataRow<Hello>.row2: Hello.InnerClass get() = `test name`
                val DataRow<Hello>.row3: Int? get() = nullableProperty
                val DataRow<Hello>.row4: () -> Unit get() = a
                val DataRow<Hello>.row5: List<List<*>> get() = d
                val DataRow<Hello>.row6: Hello.Nested get() = nestedClass
            """.trimIndent()))
            )
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `all class`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                class OuterClass

                @DataSchema(isOpen = false)
                class Hello(
                    val name: String,
                    val `test name`: InnerClass,
                    val nestedClass: Nested,
                    val nullableProperty: Int?,
                    justParameter: Int
                ) {
                    val a: () -> Unit = TODO()
                    val d: List<List<*>> = TODO() 
                    inner class InnerClass
                    class Nested
                }

                val ColumnsContainer<Hello>.col1: DataColumn<String> get() = name
                val ColumnsContainer<Hello>.col2: DataColumn<Hello.InnerClass> get() = `test name`
                val ColumnsContainer<Hello>.col3: DataColumn<Int?> get() = nullableProperty
                val ColumnsContainer<Hello>.col4: DataColumn<() -> Unit> get() = a
                val ColumnsContainer<Hello>.col5: DataColumn<List<List<*>>> get() = d
                val ColumnsContainer<Hello>.col6: DataColumn<Hello.Nested> get() = nestedClass
                
                val DataRow<Hello>.row1: String get() = name
                val DataRow<Hello>.row2: Hello.InnerClass get() = `test name`
                val DataRow<Hello>.row3: Int? get() = nullableProperty
                val DataRow<Hello>.row4: () -> Unit get() = a
                val DataRow<Hello>.row5: List<List<*>> get() = d
                val DataRow<Hello>.row6: Hello.Nested get() = nestedClass
            """.trimIndent()))
            )
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `functional type`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: () -> Unit?
                }

                val ColumnsContainer<Hello>.test1: DataColumn<() -> Unit?> get() = a
                val DataRow<Hello>.test2: () -> Unit? get() = a
            """.trimIndent()))
            )
        )
        result.inspectLines { codeLines ->
            codeLines.forOne {
                it
                    .shouldContain("ColumnsContainer<Hello>.a")
                    .shouldContain("DataColumn<kotlin.Function0<kotlin.Unit?>>")
            }
            codeLines.forOne {
                it
                    .shouldContain("DataRow<Hello>.a")
                    .shouldContain("kotlin.Function0<kotlin.Unit?>")
            }
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `suspend functional type`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: suspend () -> Unit?
                }

                val ColumnsContainer<Hello>.test1: DataColumn<suspend () -> Unit?> get() = a
                val DataRow<Hello>.test2: suspend () -> Unit? get() = a
            """.trimIndent()))
            )
        )

        result.inspectLines { codeLines ->
            codeLines.forOne {
                it
                    .shouldContain("ColumnsContainer<Hello>.a")
                    .shouldContain("DataColumn<kotlin.coroutines.SuspendFunction0<kotlin.Unit?>>")
            }
            codeLines.forOne {
                it
                    .shouldContain("DataRow<Hello>.a")
                    .shouldContain("kotlin.coroutines.SuspendFunction0<kotlin.Unit?>")
            }
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `nullable functional type`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: (() -> String)?
                }

                val ColumnsContainer<Hello>.test1: DataColumn<(() -> String)?> get() = a
                val DataRow<Hello>.test2: (() -> String)? get() = a
            """.trimIndent()))
            )
        )
        result.inspectLines { codeLines ->
            codeLines.forOne {
                it
                    .shouldContain("ColumnsContainer<Hello>.a")
                    .shouldContain("DataColumn<kotlin.Function0<kotlin.String>?>")
            }
            codeLines.forOne {
                it
                    .shouldContain("DataRow<Hello>.a")
                    .shouldContain("kotlin.Function0<kotlin.String>?")
            }
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `functional type with receiver`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: (Int.() -> String)?
                }

                val ColumnsContainer<Hello>.test1: DataColumn<(Int.() -> String)?> get() = a
                val DataRow<Hello>.test2: (Int.() -> String)? get() = a
            """.trimIndent()))
            )
        )
        result.inspectLines { codeLines ->
            codeLines.forOne {
                it
                    .shouldContain("ColumnsContainer<Hello>.a")
                    .shouldContain("DataColumn<kotlin.Function1<kotlin.Int, kotlin.String>?>")
            }
            codeLines.forOne {
                it
                    .shouldContain("DataRow<Hello>.a")
                    .shouldContain("kotlin.Function1<kotlin.Int, kotlin.String>?")
            }
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `named lambda parameter`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: (a: String) -> Unit
                }

                val ColumnsContainer<Hello>.test1: DataColumn<(a: String) -> Unit> get() = a
                val DataRow<Hello>.test2: (a: String) -> Unit get() = a
            """.trimIndent()))
            )
        )
        result.inspectLines { codeLines ->
            codeLines.forOne {
                it
                    .shouldContain("ColumnsContainer<Hello>.a")
                    .shouldContain("DataColumn<kotlin.Function1<kotlin.String, kotlin.Unit>>")
            }
            codeLines.forOne {
                it
                    .shouldContain("DataRow<Hello>.a")
                    .shouldContain("kotlin.Function1<kotlin.String, kotlin.Unit>")
            }
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `inferred type`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: Int
                    val b get() = a
                }

                val ColumnsContainer<Hello>.test1: DataColumn<Int> get() = b
                val DataRow<Hello>.test2: Int get() = b
            """.trimIndent()))
            )
        )
        result.kspGeneratedFiles.find { it.name == generatedFile }?.readText().asClue {
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `typealias`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                class OuterClass
                typealias A = OuterClass

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: A
                }

                val ColumnsContainer<Hello>.col1: DataColumn<A> get() = a
                val DataRow<Hello>.row1: A get() = a
                
            """.trimIndent()))
            )
        )
        result.kspGeneratedFiles.find { it.name == generatedFile }?.readText().asClue {
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `type annotated with dataschema rendered to column group`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                @DataSchema
                interface A

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: A
                }

                val ColumnsContainer<Hello>.col1: ColumnGroup<A> get() = a
                val DataRow<Hello>.row1: DataRow<A> get() = a
                
            """.trimIndent()))
            )
        )
        result.kspGeneratedFiles.find { it.name == generatedFile }?.readText().asClue {
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `type annotated with dataschema rendered to frame column`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                @DataSchema
                interface A

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: List<A>
                }

                val ColumnsContainer<Hello>.col1: DataColumn<DataFrame<A>> get() = a
                val DataRow<Hello>.row1: DataFrame<A> get() = a
                
            """.trimIndent()))
            )
        )
        result.kspGeneratedFiles.find { it.name == generatedFile }?.readText().asClue {
            result.successfulCompilation shouldBe true
        }
    }


    @Test
    fun `column name from annotation is used`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                @DataSchema(isOpen = false)
                interface Hello {
                    @ColumnName("test-name")
                    val `test name`: Int
                }

                val ColumnsContainer<Hello>.test2: DataColumn<Int> get() = `test name`
                val DataRow<Hello>.test4: Int get() = `test name`
            """.trimIndent()))
            )
        )
        result.inspectLines { codeLines ->
            codeLines.forExactly(2) {
                it.shouldContain("this[\"test-name\"]")
            }
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `jvm name`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports


                @DataSchema(isOpen = false)
                interface Hello {
                    val a: Int
                }

                val ColumnsContainer<Hello>.col1: DataColumn<Int> get() = a
                val DataRow<Hello>.row1: Int get() = a
                
            """.trimIndent()))
            )
        )
        result.inspectLines { codeLines ->
            codeLines.forExactly(2) {
                it.shouldContain("@JvmName(\"Hello_a\")")
            }
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `DataRow property`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                interface Marker

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: DataRow<Marker>
                }

                val ColumnsContainer<Hello>.col1: ColumnGroup<Marker> get() = a
                val DataRow<Hello>.row1: DataRow<Marker> get() = a
                
            """.trimIndent()))
            )
        )
        result.kspGeneratedFiles.find { it.name == generatedFile }?.readText().asClue {
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `DataFrame property`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                $imports

                interface Marker

                @DataSchema(isOpen = false)
                interface Hello {
                    val a: DataFrame<Marker>
                }

                val ColumnsContainer<Hello>.col1: DataColumn<DataFrame<Marker>> get() = a
                val DataRow<Hello>.row1: DataFrame<Marker> get() = a
                
            """.trimIndent()))
            )
        )
        result.successfulCompilation shouldBe true
    }


    @Test
    fun `extension accessible from same package`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                package org.example

                $imports

                @DataSchema(isOpen = false)
                interface Hello {
                    val name: String
                }

                val ColumnsContainer<Hello>.test1: DataColumn<String> get() = name
                val DataRow<Hello>.test2: String get() = name
            """.trimIndent()))
            )
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `generic interface`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                package org.example

                $imports

                @DataSchema(isOpen = false)
                interface Generic <T> {
                    val field: T
                }

                val <T> ColumnsContainer<Generic<T>>.test1: DataColumn<T> get() = field
                val <T> DataRow<Generic<T>>.test2: T get() = field
            """.trimIndent()))
            )
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `generic interface with upper bound`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                package org.example

                $imports

                @DataSchema(isOpen = false)
                interface Generic <T : String> {
                    val field: T
                }

                val <T : String> ColumnsContainer<Generic<T>>.test1: DataColumn<T> get() = field
                val <T : String> DataRow<Generic<T>>.test2: T get() = field
            """.trimIndent()))
            )
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `generic interface with variance and user type in type parameters`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                package org.example

                $imports

                interface UpperBound 

                @DataSchema(isOpen = false)
                interface Generic <out T : UpperBound> {
                    val field: T
                }

                val <T : UpperBound> ColumnsContainer<Generic<T>>.test1: DataColumn<T> get() = field
                val <T : UpperBound> DataRow<Generic<T>>.test2: T get() = field
            """.trimIndent()))
            )
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `nested interface`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                package org.example

                $imports
                class A {
                    @DataSchema(isOpen = false)
                    interface Hello {
                        val name: String
                    }
                }
               
            """.trimIndent()))
            )
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `redeclaration in different scopes`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                package org.example

                $imports
                class A {
                    @DataSchema(isOpen = false)
                    interface Hello {
                        val name: String
                    }
                }

               class B {
                    @DataSchema(isOpen = false)
                    interface Hello {
                        val name: String
                    }
                }

               
            """.trimIndent()))
            )
        )
        println(result.kspGeneratedFiles)
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `interface with internal visibility`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                package org.example

                $imports

                @DataSchema
                internal interface Hello {
                    val name: Int
                }
            """.trimIndent()))
            )
        )
        result.inspectLines { codeLines ->
            codeLines.forExactly(2) {
                it.shouldContain("""internal val """)
            }
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `interface with public visibility`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                package org.example

                $imports

                @DataSchema
                public interface Hello {
                    val name: Int
                }
            """.trimIndent()))
            )
        )
        result.inspectLines { codeLines ->
            codeLines.forExactly(2) {
                it.shouldContain("""public val""")
            }
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `interface with implicit visibility`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                package org.example

                $imports

                @DataSchema
                interface Hello {
                    val name: Int
                }
            """.trimIndent()))
            )
        )
        result.inspectLines { codeLines ->
            codeLines.forExactly(2) {
                it.shouldStartWith("""val """)
            }
            result.successfulCompilation shouldBe true
        }
    }


    @Test
    fun `private class`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                package org.example

                $imports
                   
                @DataSchema
                private class Hello(val name: Int)
               
            """.trimIndent()))
            )
        )
        result.successfulCompilation shouldBe false
    }

    @Test
    fun `effectively private interface`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                package org.example

                $imports
                   
                private class Outer {
                    @DataSchema
                    interface Hello {
                        val name: Int
                    }
                }
               
            """.trimIndent()))
            )
        )
        result.successfulCompilation shouldBe false
    }

    @Test
    fun `parent of interface is effectively private`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(SourceFile.kotlin("MySources.kt", """
                package org.example

                $imports
                   
                private class Outer {
                    class Outer1 {
                        @DataSchema
                        interface Hello {
                            val name: Int
                        }
                    }
                }
               
            """.trimIndent()))
            )
        )
        result.successfulCompilation shouldBe false
    }

    private fun KotlinCompileTestingCompilationResult.inspectLines(f: (List<String>) -> Unit) {
        inspectLines(generatedFile, f)
    }

    private fun KotlinCompileTestingCompilationResult.inspectLines(filename: String, f: (List<String>) -> Unit) {
        kspGeneratedFiles.single { it.name == filename }.readLines().asClue(f)
    }
}
