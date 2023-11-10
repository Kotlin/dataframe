package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.secondName
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

open class ColumnsSelectionDslTests : TestBase() {

    @DataSchema
    interface PersonWithFrame : Person {
        val frameCol: DataFrame<Person>
    }

    protected val frameCol by frameColumn<Person>()

    protected val dfWithFrames = df
        .add {
            expr { df } into frameCol
        }
        .convert { name }.to {
            val firstName by it.asColumnGroup().firstName
            val lastName by it.asColumnGroup().lastName

            val frameCol by it.map { df }.asFrameColumn()

            dataFrameOf(firstName, lastName, frameCol).asColumnGroup("name")
        }

    @Test
    fun `how does except work`() {
        dfGroup.select {
            age and name.exceptNew {
                firstName.secondName
            }
        }.alsoDebug()

        dfGroup.select {
            age and name.allColsExcept {
                firstName.secondName
            }
        }.alsoDebug()
    }

    @Test
    fun `allExcept with selector`() {
        listOf(
            df.select {
                name.firstName
            }.alsoDebug(),

            df.select {
                name.select { firstName }
            },
            df.select {
                name { firstName }
            },
            df.select {
                name.allColsExcept { lastName }
            },
//            df.select {
//                name - { lastName }
//            },
//            df.select {
//                name.remove { lastName }
//            },
//            df.remove {
//                name.lastName
//            },
//            df.remove {
//                name { lastName }
//            },
//            df.select {
//                name.except(name.lastName)
//            },
//            df.select {
//                name - (name.lastName and name.lastName)
//            },
        ).shouldAllBeEqual()
    }
}
