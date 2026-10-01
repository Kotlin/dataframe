package org.jetbrains.kotlinx.dataframe.plugin

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.compileTimeSchema
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.unfold
import org.junit.Ignore
import org.junit.Test

/**
 * The schema the compiler plugin derives for `unfold` and `toDataFrame` has to be the schema the call has at runtime.
 */
class UnfoldCompileTimeSchemaTests {
    data class Name(val firstName: String, val lastName: String)

    data class Score(val subject: String, val value: Int)

    data class Student(val name: Name, val age: Int, val scores: List<Score>)

    private fun students() =
        dataFrameOf(
            "id" to listOf(1, 2),
            "student" to listOf(
                Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
                Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
            ),
            "year" to listOf(2021, 2022),
        )

    @Test
    fun `unfold with maxDepth has the same schema at compile time and at runtime`() {
        val res = students().unfold(maxDepth = 1) { student }
        res.compileTimeSchema() shouldBe res.schema()
    }

    // TODO(#2115): enable this test once the issue is fixed, and remove the test below
    @Ignore("with roots, the compiler plugin derives an empty schema for unfold")
    @Test
    fun `unfold with roots has the same schema at compile time and at runtime`() {
        val res = students().unfold(Student::name, Student::age) { student }
        res.compileTimeSchema() shouldBe res.schema()
    }

    // TODO(#2115): this test fails once the issue is fixed. Remove it, enable the test above,
    //  and remove the sentence about the empty compile-time schema from `topics/unfold.md`
    //  and from the KDoc of `DataFrame.unfold` with `roots`
    @Test
    fun `unfold with roots has an empty schema at compile time`() {
        val res = students().unfold(Student::name, Student::age) { student }
        res.compileTimeSchema().columns.keys shouldBe emptySet()
        res.schema().columns.keys shouldBe setOf("id", "student", "year")
    }

    // TODO(#2114): enable this test once the issue is fixed
    @Ignore("at runtime, unfold turns a column of maps into a column group; the compiler plugin keeps it as it is")
    @Test
    fun `unfold of a column of maps has the same schema at compile time and at runtime`() {
        val res = dataFrameOf("scores" to listOf(mapOf("math" to 4), mapOf("music" to 5))).unfold { scores }
        res.compileTimeSchema() shouldBe res.schema()
    }

    // `unfold` reads the objects the way `toDataFrame` does, and the compiler plugin has the same problem there

    @Test
    fun `toDataFrame has the same schema at compile time and at runtime`() {
        val res = listOf(Student(Name("Alice", "Cooper"), 15, emptyList())).toDataFrame()
        res.compileTimeSchema() shouldBe res.schema()
    }

    // TODO(#2115): enable this test once the issue is fixed
    @Ignore("with properties, the compiler plugin derives an empty schema for toDataFrame")
    @Test
    fun `toDataFrame with properties has the same schema at compile time and at runtime`() {
        val res = listOf(Student(Name("Alice", "Cooper"), 15, emptyList())).toDataFrame(Student::age)
        res.compileTimeSchema() shouldBe res.schema()
    }
}
