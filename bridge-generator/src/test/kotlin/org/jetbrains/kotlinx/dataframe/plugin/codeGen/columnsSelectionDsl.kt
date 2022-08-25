package org.jetbrains.kotlinx.dataframe.plugin.codeGen

import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.plugin.model.Function
import org.jetbrains.kotlinx.dataframe.plugin.model.Parameter
import org.jetbrains.kotlinx.dataframe.plugin.model.Type
import org.junit.jupiter.api.Test

val columnSelectionDsl = dataFrameOf(
        Function("ColumnSet<C>", "and", Type("ColumnSet<C>", vararg = false), listOf(
            Parameter("other", Type("ColumnSet<C>", vararg = false), null)
        )),
)

class ColumnSelectionDsl {
    @Test
    fun `selectors API`() {
        columnSelectionDsl.generateAll("columnSelectionDsl_bridges.json")
    }
}
