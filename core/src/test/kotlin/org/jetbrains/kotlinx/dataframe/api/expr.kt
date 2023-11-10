package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.junit.Test

class ExprTests : ColumnsSelectionDslTests() {

    @Test
    fun expr() {
        listOf(
            df.select { age },
            df.select { expr(age.name) { age } },
        ).shouldAllBeEqual()
        // TODO
    }

}
