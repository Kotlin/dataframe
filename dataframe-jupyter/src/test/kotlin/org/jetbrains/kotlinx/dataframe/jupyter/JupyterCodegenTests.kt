package org.jetbrains.kotlinx.dataframe.jupyter

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.isNotEmpty
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.jupyter.api.MimeTypedResult
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase
import org.junit.Test
import kotlin.reflect.typeOf

class JupyterCodegenTests : JupyterReplTestCase() {

    @Test
    fun `codegen adding column with generic type function`() {
        @Language("kts")
        val res1 = execRendered(
            """
            fun <T> AnyFrame.addValue(value: T) = add("value") { listOf(value) }
            val df = dataFrameOf("a")(1).addValue(2)
            """.trimIndent(),
        )
        res1 shouldBe Unit

        @Language("kts")
        val res2 = execRaw("df") as AnyFrame

        res2["value"].type shouldBe typeOf<List<Any?>>()
    }

    @Test
    fun `opt in experimental Instant`() {
        @Language("kts")
        val res1 = execRaw(
            """
            @file:OptIn(ExperimentalTime::class)

            import kotlin.time.ExperimentalTime
            
            val values: kotlin.time.Instant = kotlin.time.Clock.System.now()
            val df = dataFrameOf("a" to columnOf(values))
            df
            """.trimIndent(),
        )

        res1.shouldBeInstanceOf<AnyFrame>()
    }

    @Test
    fun `Don't inherit from data class`() {
        @Language("kts")
        val res1 = execRendered(
            """
            @DataSchema
            data class A(val a: Int)
            """.trimIndent(),
        )

        @Language("kts")
        val res2 = execRaw(
            """
            val df = dataFrameOf("a", "b")(1, 2)
            df
            """.trimIndent(),
        )

        (res2 as AnyFrame).should { it.isNotEmpty() }
    }

    @Test
    fun `Don't inherit from non open class`() {
        @Language("kts")
        val res1 = execRendered(
            """
            @DataSchema
            class A(val a: Int)
            """.trimIndent(),
        )

        @Language("kts")
        val res2 = execRaw(
            """
            val df = dataFrameOf("a", "b")(1, 2)
            df
            """.trimIndent(),
        )

        (res2 as AnyFrame).should { it.isNotEmpty() }
    }

    @Test
    fun `Don't inherit from open class`() {
        @Language("kts")
        val res1 = execRendered(
            """
            @DataSchema
            open class A(val a: Int)
            """.trimIndent(),
        )

        @Language("kts")
        val res2 = execRaw(
            """
            val df = dataFrameOf("a", "b")(1, 2)
            df
            """.trimIndent(),
        )

        (res2 as AnyFrame).should { it.isNotEmpty() }
    }

    @Test
    fun `Do inherit from open interface`() {
        @Language("kts")
        val res1 = execRendered(
            """
            @DataSchema
            interface A { val a: Int }
            """.trimIndent(),
        )

        @Language("kts")
        val res2 = execRaw(
            """
            val df = dataFrameOf("a", "b")(1, 2)
            df
            """.trimIndent(),
        )

        (res2 as AnyFrame).should { it.isNotEmpty() }
    }

    @Test
    fun `codegen for enumerated frames`() {
        @Language("kts")
        val res1 = execRendered(
            """
            val names = (0..2).map { it.toString() }
            val df = dataFrameOf(names)(1, 2, 3)
            """.trimIndent(),
        )
        res1 shouldBe Unit

        @Language("kts")
        val res2 = execRaw("df.`1`")
        res2.shouldBeInstanceOf<ValueColumn<*>>()
    }

    @Test
    fun `codegen for complex column names`() {
        @Language("kts")
        val res1 = execRendered(
            """
            val df = DataFrame.readDelimStr("[a], (b), {c}\n1, 2, 3")
            df
            """.trimIndent(),
        )
        res1.shouldBeInstanceOf<MimeTypedResult>()

        @Language("kts")
        val res2 = execRendered(
            """listOf(df.`{a}`[0], df.`(b)`[0], df.`{c}`[0])""",
        )
        res2 shouldBe listOf(1, 2, 3)
    }

    @Test
    fun `codegen for '$' that is interpolator in kotlin string literals`() {
        @Language("kts")
        val res1 = execRendered(
            """
            val df = DataFrame.readDelimStr("\${'$'}id\n1")
            df
            """.trimIndent(),
        )
        res1.shouldBeInstanceOf<MimeTypedResult>()

        @Language("kts")
        val res2 = execRendered(
            "listOf(df.`\$id`[0])",
        )
        res2 shouldBe listOf(1)
    }

    @Test
    fun `codegen for backtick that is forbidden in kotlin identifiers`() {
        @Language("kts")
        val res1 = execRendered(
            """
            val df = DataFrame.readDelimStr("Day`s\n1")
            df
            """.trimIndent(),
        )
        res1.shouldBeInstanceOf<MimeTypedResult>()
        println(res1.entries.joinToString())

        @Language("kts")
        val res2 = execRendered(
            "listOf(df.`Day's`[0])",
        )
        res2 shouldBe listOf(1)
    }

    @Test
    fun `codegen for chars that is forbidden in JVM identifiers`() {
        val forbiddenChar = ";"

        @Language("kts")
        val res1 = execRendered(
            """
            val df = DataFrame.readDelimStr("Test$forbiddenChar\n1")
            df
            """.trimIndent(),
        )
        res1.shouldBeInstanceOf<MimeTypedResult>()
        println(res1.entries.joinToString())

        @Language("kts")
        val res2 = execRendered(
            "listOf(df.`Test `[0])",
        )
        res2 shouldBe listOf(1)
    }

    @Test
    fun `codegen for chars that is forbidden in JVM identifiers 1`() {
        val forbiddenChar = "\\\\"

        @Language("kts")
        val res1 = execRendered(
            """
            val df = DataFrame.readDelimStr("Test$forbiddenChar\n1")
            df
            """.trimIndent(),
        )
        res1.shouldBeInstanceOf<MimeTypedResult>()
        println(res1.entries.joinToString())

        @Language("kts")
        val res2 = execRendered(
            "listOf(df.`Test `[0])",
        )
        res2 shouldBe listOf(1)
    }

    @Test
    fun `generic interface`() {
        @Language("kts")
        val res1 = execRendered(
            """
            @DataSchema
            interface Generic<T> {
                val field: T
            }
            """.trimIndent(),
        )
        res1.shouldBeInstanceOf<Unit>()

        @Language("kts")
        val res2 = execRendered(
            """
            val <T> ColumnsContainer<Generic<T>>.test1: DataColumn<T> get() = field
            val <T> DataRow<Generic<T>>.test2: T get() = field
            """.trimIndent(),
        )
        res2.shouldBeInstanceOf<Unit>()
    }

    @Test
    fun `generic interface with upper bound`() {
        @Language("kts")
        val res1 = execRendered(
            """
            @DataSchema
            interface Generic <T : String> {
                val field: T
            }
            """.trimIndent(),
        )
        res1.shouldBeInstanceOf<Unit>()

        @Language("kts")
        val res2 = execRendered(
            """
            val <T : String> ColumnsContainer<Generic<T>>.test1: DataColumn<T> get() = field
            val <T : String> DataRow<Generic<T>>.test2: T get() = field
            """.trimIndent(),
        )
        res2.shouldBeInstanceOf<Unit>()
    }

    @Test
    fun `generic interface with variance and user type in type parameters`() {
        @Language("kts")
        val res1 = execRendered(
            """
            interface UpperBound

            @DataSchema(isOpen = false)
            interface Generic <out T : UpperBound> {
                val field: T
            }
            """.trimIndent(),
        )
        res1.shouldBeInstanceOf<Unit>()

        @Language("kts")
        val res2 = execRendered(
            """
            val <T : UpperBound> ColumnsContainer<Generic<T>>.test1: DataColumn<T> get() = field
            val <T : UpperBound> DataRow<Generic<T>>.test2: T get() = field
            """.trimIndent(),
        )
        res2.shouldBeInstanceOf<Unit>()
    }

    @Test
    fun `type converter does not conflict with other type converters`() {
        @Language("kts")
        val anotherTypeConverter =
            """
            notebook.fieldsHandlersProcessor.register(
                FieldHandlerFactory.createUpdateHandler<ByteArray>(TypeDetection.RUNTIME) { _, prop ->
                     execute(prop.name + ".toList()").name
                },
                ProcessingPriority.LOW
            )
            """.trimIndent()
        execEx(anotherTypeConverter)
        execEx("val x = ByteArray(1)")
        val res1 = execRaw("x")
        res1.shouldBeInstanceOf<List<*>>()
    }

    @Test
    fun `generate a new marker when dataframe marker is not a data schema so that columns are accessible with extensions`() {
        @Language("kts")
        val a = execRendered(
            """
            enum class State {
                Idle, Productive, Maintenance
            }

            class Event(val toolId: String, val state: State, val timestamp: Long)

            val tool1 = "tool_1"
            val tool2 = "tool_2"
            val tool3 = "tool_3"
            val events = listOf(
                Event(tool1, State.Idle, 0),
                Event(tool1, State.Productive, 5),
                Event(tool2, State.Idle, 0),
                Event(tool2, State.Maintenance, 10),
                Event(tool2, State.Idle, 20),
                Event(tool3, State.Idle, 0),
                Event(tool3, State.Productive, 25),
            ).toDataFrame()
            """.trimIndent(),
        )
        shouldNotThrowAny {
            @Language("kts")
            val b = execRendered(
                """
                events.toolId
                events.state
                events.timestamp
                """.trimIndent(),
            )
        }
    }
}
