package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.getColumnIndex
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.linearBg
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

@Suppress("ktlint:standard:argument-list-wrapping")
class Modify : DataFrameSampleHelper("operations", "modify") {

    val df = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
        "Alice", "Cooper", 15, "London", 54, true,
        "Bob", "Dylan", 45, "Dubai", 87, true,
        "Charlie", "Daniels", 20, "Moscow", null, false,
        "Charlie", "Chaplin", 40, "Milan", null, true,
        "Bob", "Marley", 30, "Tokyo", 68, true,
        "Alice", "Wolf", 20, null, 55, false,
        "Charlie", "Byrd", 30, "Moscow", 90, true,
    ).group("firstName", "lastName").into("name").cast<TestBase.Person>()

    val df2 = dataFrameOf(
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
}
