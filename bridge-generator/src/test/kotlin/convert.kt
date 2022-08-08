import Prototype.Bridge
import Prototype.Function
import Prototype.Parameter
import Prototype.RefinedFunction
import Prototype.Type
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.toJson
import org.jetbrains.kotlinx.dataframe.io.writeJson
import org.junit.jupiter.api.Test
import java.io.File

val convert = dataFrameOf(
    Function("DataFrame<T>", "convert", Type("Convert<T, C>", false), listOf(
        Parameter("columns", Type("ColumnsSelector<T, C>", false), null),
    )),
    Function("DataFrame<T>", "convert", Type("Convert<T, C>", false), listOf(
        Parameter("columns", Type("KProperty<C>", true), null),
    )),
    Function("DataFrame<T>", "convert", Type("Convert<T, C>", false), listOf(
        Parameter("columns", Type("String", true), null),
    )),
    Function("DataFrame<T>", "convert", Type("Convert<T, C>", false), listOf(
        Parameter("columns", Type("ColumnReference<C>", true), null),
    )),
    Function("DataFrame<T>", "convert", Type("DataFrame<T>", false), listOf(
        Parameter("firstCol", Type("ColumnReference<C>", false), null),
        Parameter("cols", Type("ColumnReference<C>", true), null),
        Parameter("infer", Type("Infer", false), "Infer.Nulls"),
        Parameter("expression", Type("RowValueExpression<T, C, R>", false), null),
    )),
    Function("DataFrame<T>", "convert", Type("DataFrame<T>", false), listOf(
        Parameter("firstCol", Type("KProperty<C>", false), null),
        Parameter("cols", Type("KProperty<C>", true), null),
        Parameter("infer", Type("Infer", false), "Infer.Nulls"),
        Parameter("expression", Type("RowValueExpression<T, C, R>", false), null),
    )),
    Function("DataFrame<T>", "convert", Type("DataFrame<T>", false), listOf(
        Parameter("firstCol", Type("String", false), null),
        Parameter("cols", Type("String", true), null),
        Parameter("infer", Type("Infer", false), "Infer.Nulls"),
        Parameter("expression", Type("RowValueExpression<T, Any?, R>", false), null),
    )),
    Function("Convert<T, C>", "to", Type("DataFrame<T>", false), emptyList()),
)

class Convert {
    @Test
    fun `convert APIs`() {
        val convert = convert.appendReceiverAndId()
        val types = convert.collectUsedTypes(classes.take(1))
        val rawBridges = types.joinBridges(bridges, verify = false)
        val path = "convert_bridges.json"
        File(path).let {
            if (!it.exists()) {
                rawBridges.writeJson(it, prettyPrint = true)
            } else {
                val json = rawBridges.filter { it["converter"] == null }.toJson(prettyPrint = true)
                println(json)
            }
        }

        val editedBridges = DataFrame.readJson(path).cast<Bridge>(verify = true)
        val allBridges = bridges.concat(editedBridges).distinct().cast<Bridge>(verify = true)
        allBridges.writeJson("bridges.json", prettyPrint = true)

        val refine = convert
            .refine(allBridges)
            //.also { println(it.schema()) }
            .convertTo<RefinedFunction>()
        //.also { println(it.schema()) }
        //.cast<RefinedFunction>(verify = true)

    editedBridges.generateAtomsTests()
    refine.generateInterpreters()
}
