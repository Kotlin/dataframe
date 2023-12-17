package org.jetbrains.kotlinx.dataframe.puzzles

import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.toJavaLocalDate
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.junit.Test
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.round
import kotlin.random.Random

class DateTests {

    private val start = LocalDate(2015, 1, 1)
    private val end = LocalDate(2016, 1, 1)
    private val dti = (start..end).toList().toColumn("dti")
    private val random = Random(42)
    private val s = List(dti.size()) { random.nextDouble() }.toColumn("s")
    private val df = dataFrameOf(dti, s)

    @Test
    fun `sum for every Wednesday`() {
        round(df.filter { dti().dayOfWeek.ordinal == 2 }.sum { s }) shouldBe 29.0
        round(df.filter { "dti"<LocalDate>().dayOfWeek.ordinal == 2 }.sum { "s"<Double>() }) shouldBe 29.0
    }

    @Test
    fun `mean for each calendar month`() {
        val expected = dataFrameOf("month", "s")(
            Month.JANUARY, 0.4931329003584097,
            Month.FEBRUARY, 0.5712887136099648,
            Month.MARCH, 0.5263142140806993,
            Month.APRIL, 0.5125149149109348,
            Month.MAY, 0.5030292029672427,
            Month.JUNE, 0.4691575481416088,
            Month.JULY, 0.5533841389695694,
            Month.AUGUST, 0.5661103164088407,
            Month.SEPTEMBER, 0.44344455128172383,
            Month.OCTOBER, 0.41726495068242264,
            Month.NOVEMBER, 0.43862977969202627,
            Month.DECEMBER, 0.5130316016982762
        )

        df.groupBy { dti.map { it.month } named "month" }.mean() shouldBe expected
        df.groupBy { "dti"<LocalDate>().map { it.month } named "month" }.mean() shouldBe expected
    }

    @Test
    fun `find date on which highest value`() {
        val expected = dataFrameOf("month4", "dti", "month41")(
            1, LocalDate(2015, 2, 11), 1,
            2, LocalDate(2015, 8, 25), 2,
            3, LocalDate(2015, 9, 2), 3,
        )
        val month4 by column<Int>()
        val month41 by column<Int>()

        df.add("month4") {
            when (dti().monthNumber) {
                in 1..4 -> 1
                in 5..8 -> 2
                else -> 3
            }
        }.groupBy("month4").aggregate { maxBy(s) into "max" }.flatten()[month4, dti, month41] shouldBe expected

        df.add("month4") {
            when ("dti"<LocalDate>().monthNumber) {
                in 1..4 -> 1
                in 5..8 -> 2
                else -> 3
            }
        }.groupBy("month4").aggregate { maxBy("s") into "max" }.flatten()["month4", "dti", "month41"] shouldBe expected
    }

    @Test
    fun `create column consisting of the third Thursday in each month`() {
        val start = LocalDate(2015, 1, 1)
        val end = LocalDate(2015, 12, 31)

        val expected = columnOf(
            LocalDate(2015, 1, 15),
            LocalDate(2015, 2, 19),
            LocalDate(2015, 3, 19),
            LocalDate(2015, 4, 16),
            LocalDate(2015, 5, 14),
            LocalDate(2015, 6, 18),
            LocalDate(2015, 7, 16),
            LocalDate(2015, 8, 13),
            LocalDate(2015, 9, 17),
            LocalDate(2015, 10, 15),
            LocalDate(2015, 11, 19),
            LocalDate(2015, 12, 17),
        ).named("3thu")

        (start..end).toList().toColumn("3thu").filter {
            it.toJavaLocalDate()[WeekFields.of(Locale.ENGLISH).weekOfMonth()] == 3 &&
                it.dayOfWeek.value == 4
        } shouldBe expected
    }
}
