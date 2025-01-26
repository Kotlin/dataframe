import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*

data class Nested(val some_double: Double)

data class Record(val my_user: String, val age: Int, val nested_type: Nested)

fun box(): String {
    val df = listOf(Record("112", 42, Nested(3.0))).toDataFrame(maxDepth = 1)
    val df1 = df.renameToCamelCase()
    df1.nestedType.someDouble
    df1.myUser
    return "OK"
}
