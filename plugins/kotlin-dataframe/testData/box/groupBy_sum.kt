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
    val res0 = personsDf.groupBy { city }.sum()
    val sum01: Int? = res0.age[0]
    // TODO: Compilation error - actual type it(kotlin.Number & kotlin.Comparable<*>)
    // `val sum02: Double? = res0.weight[0]
    res0.compareSchemas()

    // scenario #1: particular column
    val res1 = personsDf.groupBy { city }.sumFor { age }
    val sum11: Int? = res1.age[0]
    res1.compareSchemas()

    // scenario #1.1: particular column via sum
    val res11 = personsDf.groupBy { city }.sum { age }
    val sum111: Int? = res11.age[0]
    res11.compareSchemas()

    // scenario #2: particular column with new name - schema changes
    // TODO: not supported scenario for String API
    // val res2 = personsDf.groupBy { city }.sum("age", name = "newAge")
    // val sum21: Int? = res2.newAge[0]

    // scenario #2.1: particular column with new name - schema changes but via columnSelector
    val res21 =  personsDf.groupBy { city }.sum("newAge") { age }
    val sum211: Int? = res21.newAge[0]
    res21.compareSchemas()

    // scenario #2.2: two columns with new name - schema changes but via columnSelector
    // TODO: partially supported scenario - we are taking type from the first column
    val res22 = personsDf.groupBy { city }.sum("newAge") { age and yearsToRetirement }
    val sum221: Int? = res22.newAge[0]
    res22.compareSchemas()

    // scenario #3: create new column via expression
    val res3 = personsDf.groupBy { city }.sumOf("newAge") { age * 10 }
    val sum3: Int? = res3.newAge[0]

// TODO: expression has type Number, not a particular Int or Double
/* Comparison result: None
Runtime:
city: String
newAge: Number
Compile:
city: String
newAge: Int? */
    // res3.compareSchemas()

    // scenario #3.1: create new column via expression on Double column
    // CANNOT_INFER_PARAMETER_TYPE: Cannot infer type for this parameter
    // val res31 = personsDf.groupBy { city }.sumOf("newAge") { weight * 10 }
    // val sum31: Double? = res31.newAge[0]
    // res31.compareSchemas()

    return "OK"
}
