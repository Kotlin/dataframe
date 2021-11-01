package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.at
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dfsOf
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.length
import org.jetbrains.kotlinx.dataframe.api.lowercase
import org.jetbrains.kotlinx.dataframe.api.minus
import org.jetbrains.kotlinx.dataframe.api.movingAverage
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.nullToZero
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.shuffled
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.toFloat
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.api.withNull
import org.jetbrains.kotlinx.dataframe.api.withValue
import org.junit.Test

class Modify : TestBase() {

    @Test
    fun update() {
        // SampleStart
        df.update { age }.with { it * 2 }
        df.update { dfsOf<String>() }.with { it.uppercase() }
        df.update { city }.where { name.firstName == "Alice" }.withValue("Paris")
        df.update { weight }.at(1..4).notNull { it / 2 }
        df.update { name.lastName and age }.at(1, 3, 4).withNull()
        df.update { age }.with { movingAverage(2) { age }.toInt() }
        // SampleEnd
    }

    @Test
    fun convert() {
        // SampleStart
        df.convert { age }.with { it.toDouble() }
        df.convert { dfsOf<String>() }.with { it.toCharArray().toList() }
        // SampleEnd
    }

    @Test
    fun convertTo() {
        // SampleStart
        df.convert { age }.to<Double>()
        df.convert { numberCols() }.to<String>()
        df.convert { name.firstName and name.lastName }.to { it.length() }
        df.convert { weight }.toFloat()
        // SampleEnd
    }

    @Test
    fun replace() {
        // SampleStart
        df.replace { name }.with { name.firstName }
        df.replace { stringCols() }.with { it.lowercase() }
        df.replace { age }.with { 2021 - age named "year" }
        // SampleEnd
    }

    @Test
    fun shuffled() {
        // SampleStart
        df.shuffled()
        // SampleEnd
    }
    
    @Test
    fun fillNulls() {
        // SampleStart
        df.fillNulls { intCols() }.with { -1 }
        // same as
        df.update { intCols() }.where { it == null }.with { -1 }
        // SampleEnd
    }

    @Test
    fun nullToZero() {
        // SampleStart
        df.nullToZero { weight }
        // SampleEnd
    }
}
