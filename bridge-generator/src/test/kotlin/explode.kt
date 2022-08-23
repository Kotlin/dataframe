import Prototype.*
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.junit.jupiter.api.Test

val explode = dataFrameOf(
    Function("DataFrame<T>", "explode", Type("DataFrame<T>", false), listOf(
        Parameter("dropEmpty", Type("Boolean", false), "true"),
        Parameter("selector", Type("ColumnsSelector<T, *>", false), "receiver.columns()"),
    ))
)

class Explode {
    @Test
    fun `explode API`() {
        explode.generateAll("explode_bridges.json")
    }
}
