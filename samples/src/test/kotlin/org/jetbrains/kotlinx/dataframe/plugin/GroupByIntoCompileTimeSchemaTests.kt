package org.jetbrains.kotlinx.dataframe.plugin

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.compileTimeSchema
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.minBy
import org.jetbrains.kotlinx.dataframe.api.schema
import org.junit.Test

/**
 * The schema the compiler plugin derives for `into` has to be the schema the call has at runtime.
 *
 * Pins the two interpretable overloads: `GroupBy.into(columnName)` (`GroupByInto`) and the shortcut
 * `ReducedGroupBy.into(columnName)` (`GroupByReduceInto`). The expression overloads
 * (`into(columnName) { expression }`) carry no `@Refine`/`@Interpretable`, so the plugin does not
 * refine them and there is no compile-time schema to pin.
 */
@Suppress("FunctionName", "TestFunctionName")
class GroupByIntoCompileTimeSchemaTests {
    @DataSchema
    class Df(val key: String, val value: Int, val nullable: Int?)

    @Suppress("ktlint:standard:argument-list-wrapping")
    private fun df() =
        dataFrameOf("key", "value", "nullable")(
            "a", 1, 1,
            "a", 2, null,
            "b", 3, 3,
        ).cast<Df>()

    @Test
    fun GroupByInto() {
        df().groupBy { key }.into("group").compileTimeSchema() shouldBe df().groupBy { key }.into("group").schema()
    }

    @Test
    fun GroupByReduceInto() {
        // The plugin types the reduced row's columns as nullable: it cannot assume that every group
        // has a selected row, so the compile-time schema equals the runtime schema of the case
        // where some group has no selected row
        df().groupBy { key }.minBy { value }.into("minValue")
            .compileTimeSchema() shouldBe df().groupBy { key }.first { value > 2 }.into("minValue").schema()
    }
}
