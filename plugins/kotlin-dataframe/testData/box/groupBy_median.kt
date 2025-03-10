import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*

fun box(): String {
    // multiple columns
    val personsDf = dataFrameOf("name", "age", "city", "weight", "height", "yearsToRetirement")(
        "Alice", 15, "London", 99.5, "1.85", 50,
        "Bob", 20, "Paris", 140.0, "1.35", 45,
        "Charlie", 100, "Dubai", 75.0, "1.95", 0,
        "Rose", 1, "Moscow", 45.33, "0.79", 64,
        "Dylan", 35, "London", 23.4, "1.83", 30,
        "Eve", 40, "Paris", 56.72, "1.85", 25,
        "Frank", 55, "Dubai", 78.9, "1.35", 10,
        "Grace", 29, "Moscow", 67.8, "1.65", 36,
        "Hank", 60, "Paris", 80.22, "1.75", 5,
        "Isla", 22, "London", 75.1, "1.85", 43,
    )

    // scenario #0: all numerical columns
    val res0 = personsDf.groupBy { city }.median()
    val median01: Int? = res0.age[0]
    val median02: Double? = res0.weight[0]
    res0.compareSchemas()

    // scenario #1: particular column
    val res1 = personsDf.groupBy { city }.medianFor { age }
    val median11: Int? = res1.age[0]
    res1.compareSchemas()

    // scenario #1.1: particular column via median
    val res11 = personsDf.groupBy { city }.median { age }
    val median111: Int? = res11.age[0]
    res11.compareSchemas()

    // scenario #2: particular column with new name - schema changes
    // TODO: not supported scenario
    // val res2 = personsDf.groupBy { city }.median("age", name = "newAge")
    // val median21: Int? = res2.newAge[0]

    // scenario #2.1: particular column with new name - schema changes but via columnSelector
    val res21 = personsDf.groupBy { city }.median("newAge") { age }
    val median211: Int?= res21.newAge[0]
    res21.compareSchemas()

    // scenario #2.2: two columns with new name - schema changes but via columnSelector
    // TODO: handle multiple columns https://github.com/Kotlin/dataframe/issues/1090
    val res22 = personsDf.groupBy { city }.median("newAge") { age and yearsToRetirement }
    val median221: Int? = res22.newAge[0]
    res22.compareSchemas()

    // scenario #3: create new column via expression
    val res3 = personsDf.groupBy { city }.medianOf("newAge") { age * 10 }
    val median3: Int? = res3.newAge[0]
    res3.compareSchemas()

    return "OK"
}

