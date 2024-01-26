import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.annotations.*

fun `Dsl is evaluated to `(df: DataFrame<*>) {
    val df1 = df.add {
        "col1" from { 5 }
        expr { 5 } into "col2"
    }
    df1.col1
    df1.col2
}