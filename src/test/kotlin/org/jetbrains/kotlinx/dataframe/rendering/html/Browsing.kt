package org.jetbrains.kotlinx.dataframe.rendering.html

import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.junit.Ignore
import org.junit.Test

class Browsing {

    @Ignore
    @Test
    fun test() {
        dataFrameOf("a", "b", "c").randomInt(100, 1..5)
            .groupBy("a").into("g")
            .browse()
    }
}
