package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

class ValueColsTests : ColumnsSelectionDslTests() {

    @Test
    fun `valueCols exceptions`() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".valueCols() }
        }
    }

    @Test
    fun `valueCol at top-level`() {
        listOf(
            df.select { cols(age, city, weight, isHappy) },

            df.select { all().valueCols() },
            df.select { valueCols() },
        ).shouldAllBeEqual()

        listOf(
            df.select { age },

            df.select { age }.select { all() },
            df.select { age }.select { valueCols() },
            df.select { age }.select { valueCols().all() },
            df.select { age }.select { all().valueCols() },
        ).shouldAllBeEqual()

        listOf(
            df.select { cols(age, weight) },
            df.select { valueCols { "e" in it.name() } },
            df.select { all().valueCols { "e" in it.name() } },
        ).shouldAllBeEqual()
    }

    @Test
    fun `valueCols at lower level`() {
        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.valueCols { "Name" in it.name() } },
            df.select { name.colsOf<String>().valueCols { "Name" in it.name() } },
            df.select { "name".valueCols { "Name" in it.name() } },
            df.select { Person::name.valueCols { "Name" in it.name() } },
            df.select { pathOf("name").valueCols { "Name" in it.name() } },
            df.select { it["name"].asColumnGroup().valueCols { "Name" in it.name() } },
        ).shouldAllBeEqual()
    }
}
