package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.div
import org.jetbrains.kotlinx.dataframe.api.eq
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.getColumnIndex
import org.jetbrains.kotlinx.dataframe.api.gt
import org.jetbrains.kotlinx.dataframe.api.linearBg
import org.jetbrains.kotlinx.dataframe.api.lt
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.minus
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.neq
import org.jetbrains.kotlinx.dataframe.api.not
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.plus
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.times
import org.jetbrains.kotlinx.dataframe.api.unaryMinus
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.junit.Test
import java.math.BigDecimal

@Suppress("ktlint:standard:argument-list-wrapping")
class Modify : DataFrameSampleHelper("operations", "modify") {

    val df = peopleDf

    private fun getDf2() =
        dataFrameOf(
            "col1", "col2", "col3", "col4", "col5", "col6", "col7", "col8", "col9", "col10",
        )(
            45, 12, 78, 34, 90, 23, 67, 89, 56, 43,
            87, 34, 56, 78, 12, 45, 90, 23, 67, 89,
            23, 67, 89, 45, 78, 90, 12, 56, 34, 78,
            90, 45, 23, 67, 34, 78, 89, 12, 56, 23,
            12, 89, 45, 90, 56, 34, 78, 67, 23, 90,
            78, 56, 12, 23, 89, 67, 34, 90, 45, 12,
            34, 90, 67, 12, 45, 23, 56, 78, 89, 67,
            56, 23, 34, 89, 67, 12, 45, 34, 78, 90,
            89, 78, 90, 56, 23, 89, 67, 45, 12, 34,
            67, 45, 78, 12, 90, 56, 23, 89, 34, 78,
        )

    @Suppress("UNCHECKED_CAST")
    @Test
    fun formatExample_strings() {
        // SampleStart
        val ageMin = df.min { "age"<Int>() }
        val ageMax = df.max { "age"<Int>() }

        df
            .format().with { bold and textColor(black) and background(white) }
            .format("name").with { underline }
            .format { "name"["lastName"] }.with { italic }
            .format("isHappy").with {
                background(if (it as Boolean) green else red)
            }
            .format("weight").notNull().with { linearBg(it as Int, 50 to blue, 90 to red) }
            .format("age").perRowCol { row, col ->
                col as DataColumn<Int>
                textColor(
                    linear(value = col[row], from = ageMin to blue, to = ageMax to green),
                )
            }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun formatExample_properties() {
        // SampleStart
        val ageMin = df.age.min()
        val ageMax = df.age.max()

        df
            .format().with { bold and textColor(black) and background(white) }
            .format { name }.with { underline }
            .format { name.lastName }.with { italic }
            .format { isHappy }.with { background(if (it) green else red) }
            .format { weight }.notNull().linearBg(50 to FormattingDsl.blue, 90 to FormattingDsl.red)
            .format { age }.perRowCol { row, col ->
                textColor(
                    linear(value = col[row], from = ageMin to blue, to = ageMax to green),
                )
            }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun formatExampleNumbers() {
        val df2 = getDf2()
        // SampleStart
        df2.format().perRowCol { row, col ->
            val rowIndex = row.index()
            val colIndex = row.df().getColumnIndex(col)
            if ((rowIndex - colIndex) % 3 == 0) {
                background(darkGray) and textColor(white)
            } else {
                background(white) and textColor(black)
            }
        }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun columnArithmetics_kandy() {
        val df = dataFrameOf(
            "day" to columnOf("Monday", "Tuesday", "Wednesday"),
            "distanceMeters" to columnOf(1000, 2000, 3000),
        )
        // SampleStart
        df.plot {
            line {
                x(day)
                y((distanceMeters / 1000.0) named "distanceKm")
            }
        }
        // SampleEnd
    }

    @Test
    fun columnArithmetics_not() {
        // SampleStart
        df.select { !isHappy }
        // or
        !df.isHappy
        // SampleEnd
    }

    @Test
    fun columnArithmetics_addition() {
        val transactions = dataFrameOf(
            "id" to columnOf(1, 2, 3),
            "amount" to columnOf(10, 20, 30),
            "isSuccess" to columnOf(true, false, true),
        )

        // SampleStart
        transactions.amount + 10
        5.0 + transactions.amount
        // SampleEnd
    }

    @Test
    fun columnArithmetics_concatenation() {
        val weather = dataFrameOf(
            "day" to columnOf(1, 2, 3),
            "temperature" to columnOf(10, 20, 30),
        )

        // SampleStart
        weather.select { temperature + " °C" }
        // or
        weather.temperature + " °C"
        // SampleEnd
    }

    @Test
    fun columnArithmetics_column_minus_value() {
        val transactions = dataFrameOf(
            "id" to columnOf(1, 2, 3),
            "amount" to columnOf(10, 20, 30),
            "isSuccess" to columnOf(true, false, true),
        )

        // SampleStart
        transactions.amount - 10.0
        // SampleEnd
    }

    @Test
    fun columnArithmetics_value_minus_column() {
        val transactions = dataFrameOf(
            "id" to columnOf(1, 2, 3),
            "amount" to columnOf(10, 20, 30),
            "isSuccess" to columnOf(true, false, true),
        )

        // SampleStart
        100 - transactions.amount
        // SampleEnd
    }

    @Test
    fun columnArithmetics_unary_minus() {
        val transactions = dataFrameOf(
            "id" to columnOf(1, 2, 3),
            "amount" to columnOf(10, 20, 30),
            "isSuccess" to columnOf(true, false, true),
        )

        // SampleStart
        -transactions.amount
        // SampleEnd
    }

    @Test
    fun columnArithmetics_times() {
        val routes = dataFrameOf(
            "route" to columnOf("A", "B", "C"),
            "distanceKm" to columnOf(1.2, 3.5, 0.8),
        )
        val products = dataFrameOf(
            "product" to columnOf("A", "B", "C"),
            "price" to columnOf(BigDecimal("10.0"), BigDecimal("20.0"), BigDecimal("30.0")),
            "weightGrams" to columnOf(100, 200, 300),
        )

        // SampleStart
        routes.distanceKm * 1000.0
        products.price * BigDecimal("1.20")
        // SampleEnd
    }

    @Test
    fun columnArithmetics_column_div_value() {
        val products = dataFrameOf(
            "product" to columnOf("A", "B", "C"),
            "price" to columnOf(10.0, 20.0, 30.0),
            "weightGrams" to columnOf(100, 200, 300),
        )

        // SampleStart
        products.weightGrams / 1000.0
        // SampleEnd
    }

    @Test
    fun columnArithmetics_value_div_column() {
        val tasks = dataFrameOf(
            "task" to columnOf("A", "B", "C"),
            "hoursPerTask" to columnOf(1.0, 2.0, 3.0),
        )

        // SampleStart
        40 / tasks.hoursPerTask
        // SampleEnd
    }

    @Test
    fun columnArithmetics_eq() {
        val orders = dataFrameOf(
            "id" to columnOf("1", "2", "3"),
            "status" to columnOf("completed", "completed", "canceled"),
        )

        // SampleStart
        orders.status eq "canceled"
        // SampleEnd
    }

    @Test
    fun columnArithmetics_neq() {
        val orders = dataFrameOf(
            "id" to columnOf("1", "2", "3"),
            "status" to columnOf("completed", "completed", "canceled"),
        )

        // SampleStart
        orders.status neq "completed"
        // SampleEnd
    }

    @Test
    fun columnArithmetics_gt() {
        val orders = dataFrameOf(
            "id" to columnOf("1", "2", "3"),
            "status" to columnOf("completed", "completed", "canceled"),
            "cost" to columnOf(10.0, 200.0, 1500.0),
        )

        // SampleStart
        orders.cost gt 1000.0
        // SampleEnd
    }

    @Test
    fun columnArithmetics_lt() {
        val orders = dataFrameOf(
            "id" to columnOf("1", "2", "3"),
            "status" to columnOf("completed", "completed", "canceled"),
            "cost" to columnOf(10.0, 200.0, 1500.0),
        )

        // SampleStart
        orders.cost lt 20.0
        // SampleEnd
    }
}
