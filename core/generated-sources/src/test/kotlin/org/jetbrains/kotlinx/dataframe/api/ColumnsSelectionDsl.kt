package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name

open class ColumnsSelectionDslTests : TestBase() {
    @DataSchema
    interface PersonWithFrame : Person {
        val frameCol: DataFrame<Person>
    }

    protected val frameCol by frameColumn<Person>()

    protected val dfWithFrames =
        df
            .add {
                expr { df } into frameCol
            }.convert { name }
            .to {
                val firstName by it.asColumnGroup().firstName
                val lastName by it.asColumnGroup().lastName

                val frameCol by it.map { df }.asFrameColumn()

                dataFrameOf(firstName, lastName, frameCol).asColumnGroup("name")
            }
}
