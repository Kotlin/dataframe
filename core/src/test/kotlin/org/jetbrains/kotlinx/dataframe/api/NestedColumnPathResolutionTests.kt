package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.junit.Test

class NestedColumnPathResolutionTests: TestBase() {

    val deepCol = columnOf(1) named "deep"

    val deepNestedDf = dataFrameOf(
        "parent" to columnOf(
            "nested" to columnOf(
                deepCol,
            ),
        ),
    )

    @Test
    fun `ColumnPath nesting resolution`() {
        listOf(
            dataFrameOf(deepCol),
            deepNestedDf.select {
                pathOf("parent", "nested").col("deep")
            },
            deepNestedDf.select {
                pathOf("parent", "nested", "deep")
            },
            deepNestedDf.select {
                colGroup(pathOf("parent", "nested")).col("deep")
            },
            deepNestedDf.select {
                col(pathOf("parent", "nested", "deep"))
            },
        ).shouldAllBeEqual()
    }

}
