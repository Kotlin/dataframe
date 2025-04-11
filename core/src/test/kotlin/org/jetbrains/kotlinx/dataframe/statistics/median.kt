package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.mapToColumn
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.medianOf
import org.jetbrains.kotlinx.dataframe.api.rowMedianOf
import org.jetbrains.kotlinx.dataframe.statistics.myFun
import org.junit.Test
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class MedianTests {

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

    @Test
    fun `medianOf test`() {
        val d = personsDf.groupBy("city").medianOf("newAge") { "age"<Int>() * 10 }
        d["newAge"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `median of two columns`() {
        val df = dataFrameOf("a", "b", "c")(
            1, 4, "a",
            2, 6, "b",
            7, 7, "c",
        )
        df.median("a", "b") shouldBe 5.0
        df.median { "a"<Int>() and "b"<Int>() } shouldBe 5.0
        df.median("c") shouldBe "b"

        df.median { "c"<String>() } shouldBe "b"

        df.median({ "c"<String>() }) shouldBe "b"
        df.median<_, String> { "c"<String>() } shouldBe "b"
    }

    @Test
    fun `row median`() {
        val df = dataFrameOf("a", "b")(
            1, 3,
            2, 4,
            7, 7,
        )
        df.mapToColumn("", Infer.Type) { it.rowMedianOf<Int>() } shouldBe columnOf(2, 3, 7)
    }
}

fun <T> List<T>.myFun(): Int where T : Comparable<T> = TODO()
fun <T> List<T>.myFun(): Double where T : Comparable<T>, T : Number = TODO()

fun <T> myFun(list: List<T>): Int where T : Comparable<T> = TODO()
fun <T> myFun(list: List<T>): Double where T : Comparable<T>, T : Number = TODO()

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
//@JvmName("jnkjsdnf")
fun <T> myFun(get : () -> T): Int where T : Comparable<T> = TODO()

@JvmName("jnkjsdnf")
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun <T> myFun(get : () -> T): Double where T : Comparable<T>, T : Number = TODO()

fun main() {
val res1 = listOf(1, 2, 3).myFun()
val res2 = listOf("a", "b", "c").myFun()

val res3 = myFun(listOf(1, 2, 3))
val res4 = myFun(listOf("a", "b", "c"))

val res5 = myFun { 1 }
val res6 = myFun { "" }
val res7 = myFun<String> { "" }
}
