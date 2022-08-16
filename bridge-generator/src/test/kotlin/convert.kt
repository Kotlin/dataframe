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
)

val to = dataFrameOf(
    Function("Convert<T, C>", "to", Type("DataFrame<T>", false), emptyList()),
    Function("Convert<T, *>", "to", Type("DataFrame<T>", false), listOf(
        Parameter("type", Type("KType", false), null)
    )),
    Function("Convert<T, C>", "to", Type("DataFrame<T>", false), listOf(
        Parameter("columnConverter", Type("DataFrame<T>.(DataColumn<C>) -> AnyBaseCol", false), null)
    ))
)

class Convert {
    @Test
    fun `convert APIs`() {
        convert.generateAll("convert_bridges.json")
    }

    @Test
    fun `to APIs`() {
        to.generateAll("to_bridges.json")
    }
}

fun DataFrame<Function>.generateAll(batchBridgePath: String) {
    val functions = appendReceiverAndId()
    val types = functions.collectUsedTypes(classes.take(1))
    val rawBridges = types.joinBridges(bridges, verify = false)
    File(batchBridgePath).let {
        if (!it.exists()) {
            rawBridges.writeJson(it, prettyPrint = true)
        } else {
            val json = rawBridges.filter { it["converter"] == null }.toJson(prettyPrint = true)
            println(json)
        }
    }

    val editedBridges = DataFrame.readJson(batchBridgePath).cast<Bridge>(verify = true)
    val allBridges = bridges.concat(editedBridges).distinct().cast<Bridge>(verify = true)
    allBridges.writeJson("bridges.json", prettyPrint = true)

    val refine = functions
        .refine(allBridges)
        //.also { println(it.schema()) }
        .convertTo<RefinedFunction>()
    //.also { println(it.schema()) }
    //.cast<RefinedFunction>(verify = true)

    editedBridges.generateAtomsTests()
    refine.generateInterpreters()
}
