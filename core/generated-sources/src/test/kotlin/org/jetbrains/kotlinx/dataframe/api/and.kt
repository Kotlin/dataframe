package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class AndTests : ColumnsSelectionDslTests() {
    @Test
    fun and() {
        df.select {
            age and name.select {
                firstName and lastName
            }
        }

        df.select {
            age and (
                name
                )
        }

        df.select {
            age and {
                name
            }
        }

        df.select {
            age and colGroup(Person::name).select {
                firstName and lastName
            }
        }

        df.select {
            it { it { it { age } } }
        }

        df.select {
            age and name
        }

        df.select {
            "age"<Int>() and name.firstName

//            select { this { age } }
        }
    }
}
