package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class DistinctTests : ColumnsSelectionDslTests() {

    @Test
    fun distinct() {
        listOf(
            df.select { all() },
            df.select { all().distinct() },
            df.select { (all() and age and city).distinct() },
        ).shouldAllBeEqual()

        dfGroup
            .getColumnsWithPaths {
                cols(
                    name.firstName,
                    name.firstName,
                    name.firstName.firstName,
                ).distinct()
            }.let { cols ->
                cols.size shouldBe 2
                cols.first().path shouldBe pathOf("name", "firstName")
                cols.last().path shouldBe pathOf("name", "firstName", "firstName")
            }

        dfGroup
            .getColumnsWithPaths {
                cols(
                    "name"["firstName"],
                    "name"["firstName"],
                    "name"["firstName"]["firstName"],
                ).distinct()
            }.let { cols ->
                cols.size shouldBe 2
                cols.first().path shouldBe pathOf("name", "firstName")
                cols.last().path shouldBe pathOf("name", "firstName", "firstName")
            }

        shouldThrow<DuplicateColumnNamesException> {
            dfGroup.select {
                cols(
                    name.firstName,
                    name.firstName,
                    name.firstName.firstName,
                ).distinct()
            }
        }
    }
}
