package org.jetbrains.kotlinx.dataframe.samples.guides

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

class MigrationTo10Samples : DataFrameSampleHelper("migrationTo1_0", "guides") {

    @DataSchema
    interface BigNumbers {
        val bigIntCol: BigInteger?
        val bigDecimalCol: BigDecimal?
    }

    private val df: DataFrame<BigNumbers> = dataFrameOf(
        "bigIntCol" to listOf(BigInteger.valueOf(100), null, BigInteger.valueOf(250)),
        "bigDecimalCol" to listOf(BigDecimal("120.5"), BigDecimal("310.0"), null),
    ).cast()

    @Test
    fun bigNumbersFilter() {
        // SampleStart
        df.filter { bigDecimalCol?.let { it > BigDecimal.valueOf(150.0) } == true }
        // SampleEnd
    }
}
