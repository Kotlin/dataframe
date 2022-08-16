import Prototype.*
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
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
