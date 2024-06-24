package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

class ColumnRangeTest : ColumnsSelectionDslTests() {
    @Test
    fun `top level columns`() {
        listOf(
            df.select { age and city and weight },
            df.select { "age".."weight" },
            df.select { "age"..Person::weight },
            df.select { "age"..weight },
            df.select { "age"..pathOf("weight") },
            df.select { Person::age.."weight" },
            df.select { Person::age..Person::weight },
            df.select { Person::age..weight },
            df.select { Person::age..pathOf("weight") },
            df.select { age.."weight" },
            df.select { age..Person::weight },
            df.select { age..weight },
            df.select { age..pathOf("weight") },
            df.select { pathOf("age").."weight" },
            df.select { pathOf("age")..Person::weight },
            df.select { pathOf("age")..weight },
            df.select { pathOf("age")..pathOf("weight") },
        ).shouldAllBeEqual()

        // range of single column
        df.select { age..age } shouldBe df.select { age }

        // wrong order
        shouldThrow<IllegalArgumentException> {
            df.select { weight..age }
        }
    }

    @Test
    fun `inside column group`() {
        listOf(
            df.select { name.firstName and name.lastName },
            df.select { name.firstName..name.lastName },
            df.select { name.firstName.."name"["lastName"] },
            df.select { "name"["firstName"]..name.lastName },
            df.select { "name"["firstName"].."name"["lastName"] },
        ).shouldAllBeEqual()

        // other parent
        shouldThrow<IllegalArgumentException> {
            df.select { name.firstName..age }
        }
    }
}
