package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.junit.Test

class ExprTests : ColumnsSelectionDslTests() {

    @Test
    fun expr() {
        listOf(
            df.select { age },
            df.select { expr(age.name) { age } },
            df.select { expr { age } named age },
        ).shouldAllBeEqual()

        df.select { age + 1 }.alsoDebug()
        df.select { it.mapToColumn(age.name) { age } }.alsoDebug()

        df.select { it.mapToColumn(age) { age } }.alsoDebug()
    }

}
