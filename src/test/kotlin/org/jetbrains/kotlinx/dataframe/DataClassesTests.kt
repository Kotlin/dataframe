package org.jetbrains.kotlinx.dataframe

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toListOf
import org.jetbrains.kotlinx.dataframe.api.values
import org.junit.Test

class DataClassesTests {

    @Test
    fun convertDataClasses() {
        data class Record(val sex: String, val grade: Int, val count: Int)

        data class PivotedRecord(val grade: Int, val male: Int, val female: Int)

        listOf(
            Record("male", 5, 10),
            Record("male", 6, 15),
            Record("female", 5, 20),
            Record("female", 6, 15)
        )
            .toDataFrame()
            .pivot(Record::sex).groupBy(Record::grade).values(Record::count)
            .toListOf<PivotedRecord>() shouldBe
            listOf(
                PivotedRecord(5, 10, 20),
                PivotedRecord(6, 15, 15)
            )
    }
}
