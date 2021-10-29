package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.at
import org.jetbrains.kotlinx.dataframe.api.dfsOf
import org.jetbrains.kotlinx.dataframe.api.movingAverage
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.api.withValue
import org.jetbrains.kotlinx.dataframe.api.withNull
import org.junit.Test

class Modify : TestBase() {

    @Test
    fun update() {
        // SampleStart
        df.update { age }.with { it * 2 }
        df.update { dfsOf<String>() }.with { it.uppercase() }
        df.update { city }.where { name.firstName == "Alice" }.withValue("Paris")
        df.update { weight }.at(1..4).notNull { it / 2 }
        df.update { name.lastName and age }.at(1,3,4).withNull()
        df.update { age }.with { movingAverage(2) { age }.toInt() }
        // SampleEnd
    }
}
