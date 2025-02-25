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
    val personsDf = dataFrameOf("name", "age", "city", "weight", "height")(
        "Alice", 15, "London", 99.5, "1.85",
        "Bob", 20, "Paris", 140.0, "1.35",
        "Charlie", 100, "Dubai", 75, "1.95",
        "Rose", 1, "Moscow", 45.3, "0.79",
        "Dylan", 35, "London", 23.4, "1.83",
        "Eve", 40, "Paris", 56.7, "1.85",
        "Frank", 55, "Dubai", 78.9, "1.35",
        "Grace", 29, "Moscow", 67.8, "1.65",
        "Hank", 60, "Paris", 80.2, "1.75",
        "Isla", 22, "London", 75.1, "1.85",
    )

    // scenario #0: all numerical columns
    val res0 = personsDf.groupBy { city }.sum()
    val sum01: Int? = res0.age[0]
    // TODO: Compilation error - actual type it(kotlin.Number & kotlin.Comparable<*>)
    // val sum02: Double? = res0.weight[0]

    // scenario #1: particular column
    val res1 = personsDf.groupBy { city }.sumFor { age }
    val sum11: Int? = res1.age[0]

    // scenario #1.1: particular column via sum
    val res11 = personsDf.groupBy { city }.sum { age }
    val sum111: Int? = res11.age[0]

   /* // scenario #2: particular column with new name - schema changes
    val res2 = personsDf.groupBy { city }.sum("age", name = "newAge")
    val sum21: Int? = res2.newAge[0]*/

    // scenario #3: create new column via expression
    val res3 = personsDf.groupBy { city }.sumOf("ageSum") { age * 10 }
    val sum3: Int? = res3.ageSum[0]

    return "OK"
}
