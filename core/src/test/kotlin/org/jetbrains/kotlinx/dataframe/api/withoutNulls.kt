package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class WithoutNullsTests : ColumnsSelectionDslTests() {
    @Test
    fun `top level`() {
        listOf(
            df.select { name and age and isHappy },
            df.select { withoutNulls() },
            df.select { all().withoutNulls() },
        ).shouldAllBeEqual()
    }

    @Test
    fun `lower level`() {
        listOf(
            dfGroup.select { name.firstName.firstName },
            dfGroup.select { name.firstName.colsWithoutNulls() },
            dfGroup.select { "name"["firstName"].colsWithoutNulls() },
            dfGroup.select { name { "firstName".colsWithoutNulls() } },
            dfGroup.select { name { Name2::firstName.colsWithoutNulls() } },
        ).shouldAllBeEqual()
    }
}
