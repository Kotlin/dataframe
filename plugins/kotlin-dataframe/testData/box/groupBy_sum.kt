import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*

fun box(): String {
    // simple cases on one column
    val df = dataFrameOf("a")(1, 1, 2, 3, 3).groupBy { a }.sum()
    val i: Int = df.a[0]

    // add a new column via expression
    val df1 = dataFrameOf("a")(1, 1, 2, 3, 3).groupBy { a }.sumOf("mySum") { a / 2 }
    val i1: Int? = df1.mySum[0]

    // multiple columns
    val personsDf = dataFrameOf("name", "age", "city", "weight")(
        "Alice", 15, "London", 99.5,
        "Bob", 20, "Paris", 140.0,
        "Charlie", 100, "Dubai", 75,
        "Rose", 1, "Moscow", 45.3,
        "Dylan", 35, "London", 23.4,
        "Eve", 40, "Paris", 56.7,
        "Frank", 55, "Dubai", 78.9,
        "Grace", 29, "Moscow", 67.8,
        "Hank", 60, "Paris", 80.2,
        "Isla", 22, "London", 75.1,
    )

    // all numerical columns
    val res0 = personsDf.groupBy { city }.sum()
    val sum01: Int? = res0.age[0]
    // TODO: Compilation error - actual type it(kotlin.Number & kotlin.Comparable<*>)
    // val sum02: Double? = res0.weight[0]

    // particular column
    val res1 = personsDf.groupBy { city }.sum { age }
    val sum1: Int? = res1.age[0]

    // add a new column via expression
    val res2 = personsDf.groupBy { city }.sumOf("ageSum") { age }
    val sum2: Int? = res2.ageSum[0]

    // sumFor
    val res3 = personsDf.groupBy { city }.sumFor { age }
    val sum3: Int? = res3.age[0]

    return "OK"
}
