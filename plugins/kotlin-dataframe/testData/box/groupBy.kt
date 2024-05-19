import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.annotations.*

@DataSchema
data class Record(val a: String, val b: Int)

fun box(): String {
    val df = List(10) { Record(it.toString(), it) }.let { dataFrameOf(*it.toTypedArray()) }
    val df1 = df.groupBy { b }.aggregate { "123" into "fsdf" }
    return "OK"
}