package org.jetbrains.dataframe.ksp

import com.tschuchort.compiletesting.SourceFile
import io.kotest.assertions.asClue
import io.kotest.inspectors.forAtLeastOne
import io.kotest.inspectors.forExactly
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import org.jetbrains.dataframe.ksp.runner.KotlinCompileTestingCompilationResult
import org.jetbrains.dataframe.ksp.runner.KspCompilationTestRunner
import org.jetbrains.dataframe.ksp.runner.TestCompilationParameters
import org.junit.Before
import java.io.File
import kotlin.test.Test

@Suppress("unused")
class DataFrameSymbolProcessorTest {

    companion object {
        val imports =
            """
            import org.jetbrains.kotlinx.dataframe.annotations.*
            import org.jetbrains.kotlinx.dataframe.columns.*
            import org.jetbrains.kotlinx.dataframe.* 
            """.trimIndent()

        const val GENERATED_FILE = $$"Hello$Extensions.kt"
    }

    @Before
    fun setup() {
        KspCompilationTestRunner.compilationDir.deleteRecursively()
    }

    @Test
    fun `interface with backticked name`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports

                        class OuterClass

                        @DataSchema(isOpen = false)
                        interface `Hello Something` {
                            val name: String
                            val `test name`: NestedClass
                            val nullableProperty: Int?
                            val a: () -> Unit
                            val d: List<List<*>>
                            
                            class NestedClass
                        }

                        val ColumnsScope<`Hello Something`>.col1: DataColumn<String> get() = name
                        val ColumnsScope<`Hello Something`>.col2: DataColumn<`Hello Something`.NestedClass> get() = `test name`
                        val ColumnsScope<`Hello Something`>.col3: DataColumn<Int?> get() = nullableProperty
                        val ColumnsScope<`Hello Something`>.col4: DataColumn<() -> Unit> get() = a
                        val ColumnsScope<`Hello Something`>.col5: DataColumn<List<List<*>>> get() = d
                        
                        val DataRow<`Hello Something`>.row1: String get() = name
                        val DataRow<`Hello Something`>.row2: `Hello Something`.NestedClass get() = `test name`
                        val DataRow<`Hello Something`>.row3: Int? get() = nullableProperty
                        val DataRow<`Hello Something`>.row4: () -> Unit get() = a
                        val DataRow<`Hello Something`>.row5: List<List<*>> get() = d
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `all interface`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
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

                        val ColumnsScope<Hello>.col1: DataColumn<String> get() = name
                        val ColumnsScope<Hello>.col2: DataColumn<Hello.NestedClass> get() = `test name`
                        val ColumnsScope<Hello>.col3: DataColumn<Int?> get() = nullableProperty
                        val ColumnsScope<Hello>.col4: DataColumn<() -> Unit> get() = a
                        val ColumnsScope<Hello>.col5: DataColumn<List<List<*>>> get() = d
                        
                        val DataRow<Hello>.row1: String get() = name
                        val DataRow<Hello>.row2: Hello.NestedClass get() = `test name`
                        val DataRow<Hello>.row3: Int? get() = nullableProperty
                        val DataRow<Hello>.row4: () -> Unit get() = a
                        val DataRow<Hello>.row5: List<List<*>> get() = d
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `all data class`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
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

                        val ColumnsScope<Hello>.col1: DataColumn<String> get() = name
                        val ColumnsScope<Hello>.col2: DataColumn<Hello.InnerClass> get() = `test name`
                        val ColumnsScope<Hello>.col3: DataColumn<Int?> get() = nullableProperty
                        val ColumnsScope<Hello>.col4: DataColumn<() -> Unit> get() = a
                        val ColumnsScope<Hello>.col5: DataColumn<List<List<*>>> get() = d
                        val ColumnsScope<Hello>.col6: DataColumn<Hello.Nested> get() = nestedClass
                        
                        val DataRow<Hello>.row1: String get() = name
                        val DataRow<Hello>.row2: Hello.InnerClass get() = `test name`
                        val DataRow<Hello>.row3: Int? get() = nullableProperty
                        val DataRow<Hello>.row4: () -> Unit get() = a
                        val DataRow<Hello>.row5: List<List<*>> get() = d
                        val DataRow<Hello>.row6: Hello.Nested get() = nestedClass
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `all class`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
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

                        val ColumnsScope<Hello>.col1: DataColumn<String> get() = name
                        val ColumnsScope<Hello>.col2: DataColumn<Hello.InnerClass> get() = `test name`
                        val ColumnsScope<Hello>.col3: DataColumn<Int?> get() = nullableProperty
                        val ColumnsScope<Hello>.col4: DataColumn<() -> Unit> get() = a
                        val ColumnsScope<Hello>.col5: DataColumn<List<List<*>>> get() = d
                        val ColumnsScope<Hello>.col6: DataColumn<Hello.Nested> get() = nestedClass
                        
                        val DataRow<Hello>.row1: String get() = name
                        val DataRow<Hello>.row2: Hello.InnerClass get() = `test name`
                        val DataRow<Hello>.row3: Int? get() = nullableProperty
                        val DataRow<Hello>.row4: () -> Unit get() = a
                        val DataRow<Hello>.row5: List<List<*>> get() = d
                        val DataRow<Hello>.row6: Hello.Nested get() = nestedClass
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `multi-round data schema generation`() {
        useHostedFile(jetbrainsCsv) {
            val result = KspCompilationTestRunner.compile(
                TestCompilationParameters(
                    sources = listOf(
                        SourceFile.kotlin(
                            "MySources.kt",
                            """
                            @file:ImportDataSchema(name = "Repo", "$it")
                            
                            import org.jetbrains.kotlinx.dataframe.DataFrame
                            import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
                            import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
                            import org.jetbrains.kotlinx.dataframe.api.print
                            import org.jetbrains.kotlinx.dataframe.io.readJson
                            
                            @DataSchema
                            interface Repos {
                                val repositories: DataFrame<Repo>
                            }
                            
                            fun main() {
                                val df: DataFrame<Repos> = DataFrame.readJson("data/jetbrains_repositories.json") as DataFrame<Repos>
                                df.repositories[0].print()
                            }
                            
                            """.trimIndent(),
                        ),
                    ),
                ),
            )
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `functional type`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports

                        @DataSchema(isOpen = false)
                        interface Hello {
                            val a: () -> Unit?
                        }

                        val ColumnsScope<Hello>.test1: DataColumn<() -> Unit?> get() = a
                        val DataRow<Hello>.test2: () -> Unit? get() = a
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.inspectLines { codeLines ->
            codeLines.forOne {
                it
                    .shouldContain("ColumnsScope<Hello>.a")
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
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports

                        @DataSchema(isOpen = false)
                        interface Hello {
                            val a: suspend () -> Unit?
                        }

                        val ColumnsScope<Hello>.test1: DataColumn<suspend () -> Unit?> get() = a
                        val DataRow<Hello>.test2: suspend () -> Unit? get() = a
                        """.trimIndent(),
                    ),
                ),
            ),
        )

        result.inspectLines { codeLines ->
            codeLines.forOne {
                it
                    .shouldContain("ColumnsScope<Hello>.a")
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
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports

                        @DataSchema(isOpen = false)
                        interface Hello {
                            val a: (() -> String)?
                        }

                        val ColumnsScope<Hello>.test1: DataColumn<(() -> String)?> get() = a
                        val DataRow<Hello>.test2: (() -> String)? get() = a
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.inspectLines { codeLines ->
            codeLines.forOne {
                it
                    .shouldContain("ColumnsScope<Hello>.a")
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
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports

                        @DataSchema(isOpen = false)
                        interface Hello {
                            val a: (Int.() -> String)?
                        }

                        val ColumnsScope<Hello>.test1: DataColumn<(Int.() -> String)?> get() = a
                        val DataRow<Hello>.test2: (Int.() -> String)? get() = a
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.inspectLines { codeLines ->
            codeLines.forOne {
                it
                    .shouldContain("ColumnsScope<Hello>.a")
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
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports

                        @DataSchema(isOpen = false)
                        interface Hello {
                            val a: (a: String) -> Unit
                        }

                        val ColumnsScope<Hello>.test1: DataColumn<(a: String) -> Unit> get() = a
                        val DataRow<Hello>.test2: (a: String) -> Unit get() = a
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.inspectLines { codeLines ->
            codeLines.forOne {
                it
                    .shouldContain("ColumnsScope<Hello>.a")
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
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports

                        @DataSchema(isOpen = false)
                        interface Hello {
                            val a: Int
                            val b get() = a
                        }

                        val ColumnsScope<Hello>.test1: DataColumn<Int> get() = b
                        val DataRow<Hello>.test2: Int get() = b
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.kspGeneratedFiles.find { it.name == GENERATED_FILE }?.readText().asClue {
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `typealias`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports

                        class OuterClass
                        typealias A = OuterClass

                        @DataSchema(isOpen = false)
                        interface Hello {
                            val a: A
                        }

                        val ColumnsScope<Hello>.col1: DataColumn<A> get() = a
                        val DataRow<Hello>.row1: A get() = a
                        
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.kspGeneratedFiles.find { it.name == GENERATED_FILE }?.readText().asClue {
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `type annotated with dataschema rendered to column group`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports

                        @DataSchema
                        interface A

                        @DataSchema(isOpen = false)
                        interface Hello {
                            val a: A
                        }

                        val ColumnsScope<Hello>.col1: ColumnGroup<A> get() = a
                        val DataRow<Hello>.row1: DataRow<A> get() = a
                        
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.kspGeneratedFiles.find { it.name == GENERATED_FILE }?.readText().asClue {
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `type annotated with dataschema rendered to frame column`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports

                        @DataSchema
                        interface A

                        @DataSchema(isOpen = false)
                        interface Hello {
                            val a: List<A>
                        }

                        val ColumnsScope<Hello>.col1: DataColumn<DataFrame<A>> get() = a
                        val DataRow<Hello>.row1: DataFrame<A> get() = a
                        
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.kspGeneratedFiles.find { it.name == GENERATED_FILE }?.readText().asClue {
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `column name from annotation is used`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports

                        @DataSchema(isOpen = false)
                        interface Hello {
                            @ColumnName("test-name")
                            val `test name`: Int
                        }

                        val ColumnsScope<Hello>.test2: DataColumn<Int> get() = `test name`
                        val DataRow<Hello>.test4: Int get() = `test name`
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.inspectLines { codeLines ->
            codeLines.forExactly(4) {
                it.shouldContain("this[\"test-name\"]")
            }
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `jvm name`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports


                        @DataSchema(isOpen = false)
                        interface Hello {
                            val a: Int
                        }

                        val ColumnsScope<Hello>.col1: DataColumn<Int> get() = a
                        val DataRow<Hello>.row1: Int get() = a
                        
                        """.trimIndent(),
                    ),
                ),
            ),
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
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports

                        interface Marker

                        @DataSchema(isOpen = false)
                        interface Hello {
                            val a: DataRow<Marker>
                        }

                        val ColumnsScope<Hello>.col1: ColumnGroup<Marker> get() = a
                        val DataRow<Hello>.row1: DataRow<Marker> get() = a
                        
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.kspGeneratedFiles.find { it.name == GENERATED_FILE }?.readText().asClue {
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `DataFrame property`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        $imports

                        interface Marker

                        @DataSchema(isOpen = false)
                        interface Hello {
                            val a: DataFrame<Marker>
                        }

                        val ColumnsScope<Hello>.col1: DataColumn<DataFrame<Marker>> get() = a
                        val DataRow<Hello>.row1: DataFrame<Marker> get() = a
                        
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `extension accessible from same package`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        package org.example

                        $imports

                        @DataSchema(isOpen = false)
                        interface Hello {
                            val name: String
                        }

                        val ColumnsScope<Hello>.test1: DataColumn<String> get() = name
                        val DataRow<Hello>.test2: String get() = name
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `generic interface`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        package org.example

                        $imports

                        @DataSchema(isOpen = false)
                        interface Generic <T> {
                            val field: T
                        }

                        val <T> ColumnsScope<Generic<T>>.test1: DataColumn<T> get() = field
                        val <T> DataRow<Generic<T>>.test2: T get() = field
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `generic interface with upper bound`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        package org.example

                        $imports

                        @DataSchema(isOpen = false)
                        interface Generic <T : String> {
                            val field: T
                        }

                        val <T : String> ColumnsScope<Generic<T>>.test1: DataColumn<T> get() = field
                        val <T : String> DataRow<Generic<T>>.test2: T get() = field
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `generic interface with variance and user type in type parameters`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        package org.example

                        $imports

                        interface UpperBound 

                        @DataSchema(isOpen = false)
                        interface Generic <out T : UpperBound> {
                            val field: T
                        }

                        val <T : UpperBound> ColumnsScope<Generic<T>>.test1: DataColumn<T> get() = field
                        val <T : UpperBound> DataRow<Generic<T>>.test2: T get() = field
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `generic interface as supertype`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        package org.example

                        $imports

                        interface KeyValue<T> {
                            val key: String
                            val value: T
                        }
                        
                        @DataSchema
                        interface MySchema : KeyValue<Int>


                        val ColumnsScope<MySchema>.test1: DataColumn<String> get() = key
                        val DataRow<MySchema>.test2: Int get() = value
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `nested interface`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        package org.example

                        $imports
                        class A {
                            @DataSchema(isOpen = false)
                            interface Hello {
                                val name: String
                            }
                        }
                        
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `redeclaration in different scopes`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
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

                        
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        println(result.kspGeneratedFiles)
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `interface with internal visibility`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        package org.example

                        $imports

                        @DataSchema
                        internal interface Hello {
                            val name: Int
                        }
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.inspectLines { codeLines ->
            codeLines.forExactly(4) {
                it.shouldContain("""internal val """)
            }
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `interface with public visibility`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        package org.example

                        $imports

                        @DataSchema
                        public interface Hello {
                            val name: Int
                        }
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.inspectLines { codeLines ->
            codeLines.forExactly(4) {
                it.shouldContain("""public val""")
            }
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `interface with implicit visibility`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        package org.example

                        $imports

                        @DataSchema
                        interface Hello {
                            val name: Int
                        }
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.inspectLines { codeLines ->
            codeLines.forExactly(4) {
                it.shouldStartWith("""val """)
            }
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `private class`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        package org.example

                        $imports
                           
                        @DataSchema
                        private class Hello(val name: Int)
                        
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe false
    }

    @Test
    fun `effectively private interface`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        package org.example

                        $imports
                           
                        private class Outer {
                            @DataSchema
                            interface Hello {
                                val name: Int
                            }
                        }
                        
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe false
    }

    @Test
    fun `parent of interface is effectively private`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
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
                        
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe false
    }

    private val jetbrainsCsv = File("../../data/jetbrains_repositories.csv")

    @Test
    fun `imported schema resolved`() {
        useHostedFile(jetbrainsCsv) {
            val result = KspCompilationTestRunner.compile(
                TestCompilationParameters(
                    sources = listOf(
                        SourceFile.kotlin(
                            "MySources.kt",
                            """
                            @file:ImportDataSchema(
                                "Schema", 
                                "$it",
                                
                            )
                            package org.example
                            import org.jetbrains.kotlinx.dataframe.annotations.CsvOptions
                            import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema

                            fun resolve() = Schema.readCsv()
                            """.trimIndent(),
                        ),
                    ),
                ),
            )
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `io error on schema import`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                        @file:ImportDataSchema(
                            "Schema", 
                            "123",
                        )
                        package org.example
                        import org.jetbrains.kotlinx.dataframe.annotations.CsvOptions
                        import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
                        """.trimIndent(),
                    ),
                ),
            ),
        )
        result.successfulCompilation shouldBe false
    }

    @Test
    fun `normalization disabled`() {
        useHostedFile(jetbrainsCsv) {
            val result = KspCompilationTestRunner.compile(
                TestCompilationParameters(
                    sources = listOf(
                        SourceFile.kotlin(
                            "MySources.kt",
                            """
                            @file:ImportDataSchema(
                                "Schema", 
                                "$it",
                                normalizationDelimiters = []
                            )
                            package org.example
                            import org.jetbrains.kotlinx.dataframe.annotations.CsvOptions
                            import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
                            """.trimIndent(),
                        ),
                    ),
                ),
            )
            println(result.kspGeneratedFiles)
            result.inspectLines("Schema.Generated.kt") {
                it.forAtLeastOne { it shouldContain "full_name" }
            }
        }
    }

    @Test
    fun `normalization enabled`() {
        useHostedFile(jetbrainsCsv) {
            val result = KspCompilationTestRunner.compile(
                TestCompilationParameters(
                    sources = listOf(
                        SourceFile.kotlin(
                            "MySources.kt",
                            """
                            @file:ImportDataSchema(
                                "Schema", 
                                "$it",
                            )
                            package org.example
                            import org.jetbrains.kotlinx.dataframe.annotations.CsvOptions
                            import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
                            """.trimIndent(),
                        ),
                    ),
                ),
            )
            println(result.kspGeneratedFiles)
            result.inspectLines("Schema.Generated.kt") {
                it.forAtLeastOne { it shouldContain "fullName" }
            }
        }
    }

    private val petstoreYaml = File("../../dataframe-openapi-generator/src/test/resources/petstore.yaml")

    @Test
    fun `openApi yaml test`(): Unit =
        useHostedFile(petstoreYaml) {
            val result = KspCompilationTestRunner.compile(
                TestCompilationParameters(
                    sources = listOf(
                        SourceFile.kotlin(
                            "MySources.kt",
                            """
                            @file:ImportDataSchema(
                                "Petstore", 
                                "$it",
                                enableExperimentalOpenApi = true,
                            )
                            package org.example
                            import org.jetbrains.kotlinx.dataframe.annotations.CsvOptions
                            import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema

                            fun resolved() {
                                Petstore.Pet
                                Petstore.Error
                            }
                            """.trimIndent(),
                        ),
                    ),
                ),
            )
            println(result.kspGeneratedFiles)
            result.inspectLines("Petstore.Generated.kt") {
                it.forAtLeastOne { it shouldContain "Pet" }
                it.forAtLeastOne { it shouldContain "Error" }
            }
            result.inspectLines($$"org.example.Petstore.Pet$Extensions.kt") {
                it.forAtLeastOne { it shouldContain "tag" }
                it.forAtLeastOne { it shouldContain "id" }
            }
            result.inspectLines($$"org.example.Petstore.Error$Extensions.kt") {
                it.forAtLeastOne { it shouldContain "message" }
                it.forAtLeastOne { it shouldContain "code" }
            }
        }

    private val jetbrainsJson = File("../../data/jetbrains.json")

    @Test
    fun `non openApi json test`(): Unit =
        useHostedFile(jetbrainsJson) {
            val result = KspCompilationTestRunner.compile(
                TestCompilationParameters(
                    sources = listOf(
                        SourceFile.kotlin(
                            "MySources.kt",
                            """
                            @file:ImportDataSchema(
                                "JetBrains", 
                                "$it",
                            )
                            package org.example
                            import org.jetbrains.kotlinx.dataframe.annotations.CsvOptions
                            import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema

                            fun resolved() {
                                JetBrains
                            }
                            """.trimIndent(),
                        ),
                    ),
                ),
            )
            println(result.kspGeneratedFiles)
            result.inspectLines("JetBrains.Generated.kt") {
                (('1'..'5') + "").forEach { nr ->
                    it.forAtLeastOne {
                        it shouldContain "JetBrains$nr"
                    }
                }
            }
        }

    private val apiGuruMetricsJson = File("../../dataframe-openapi-generator/src/test/resources/apiGuruMetrics.json")

    @Test
    fun `non openApi json test 2`(): Unit =
        useHostedFile(apiGuruMetricsJson) {
            val result = KspCompilationTestRunner.compile(
                TestCompilationParameters(
                    sources = listOf(
                        SourceFile.kotlin(
                            "MySources.kt",
                            """
                            @file:ImportDataSchema(
                                "MetricsNoKeyValue", 
                                "$it",
                            )
                            package org.example
                            import org.jetbrains.kotlinx.dataframe.annotations.CsvOptions
                            import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema

                            fun resolved() {
                                MetricsNoKeyValue
                            }
                            """.trimIndent(),
                        ),
                    ),
                ),
            )
            println(result.kspGeneratedFiles)
            result.inspectLines("MetricsNoKeyValue.Generated.kt") {
                (('1'..'3') + "").forEach { nr ->
                    it.forAtLeastOne {
                        it shouldContain "MetricsNoKeyValue$nr"
                    }
                }
            }
        }

    private val petstoreJson = File("../../dataframe-openapi-generator/src/test/resources/petstore.json")

    @Test
    fun `openApi json test`(): Unit =
        useHostedFile(petstoreJson) {
            val result = KspCompilationTestRunner.compile(
                TestCompilationParameters(
                    sources = listOf(
                        SourceFile.kotlin(
                            "MySources.kt",
                            """
                            @file:ImportDataSchema(
                                path = "$it",
                                name = "Petstore",
                                enableExperimentalOpenApi = true,
                            )
                            package org.example
                            import org.jetbrains.kotlinx.dataframe.annotations.CsvOptions
                            import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema

                            fun resolved() {
                                Petstore.Pet
                                Petstore.Error
                            }
                            """.trimIndent(),
                        ),
                    ),
                ),
            )
            println(result.kspGeneratedFiles)
            result.inspectLines("Petstore.Generated.kt") {
                it.forAtLeastOne { it shouldContain "Pet" }
                it.forAtLeastOne { it shouldContain "Error" }
                it.forAtLeastOne { it shouldContain "readJson" }
                it.forAtLeastOne { it shouldContain "readJsonStr" }
            }
            result.inspectLines($$"org.example.Petstore.Pet$Extensions.kt") {
                it.forAtLeastOne { it shouldContain "tag" }
                it.forAtLeastOne { it shouldContain "id" }
            }
            result.inspectLines($$"org.example.Petstore.Error$Extensions.kt") {
                it.forAtLeastOne { it shouldContain "message" }
                it.forAtLeastOne { it shouldContain "code" }
            }
        }

    private fun KotlinCompileTestingCompilationResult.inspectLines(f: (List<String>) -> Unit) {
        inspectLines(GENERATED_FILE, f)
    }

    private fun KotlinCompileTestingCompilationResult.inspectLines(filename: String, f: (List<String>) -> Unit) {
        kspGeneratedFiles.single { it.name == filename }.readLines().asClue(f)
    }
}
