package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test
import kotlin.reflect.typeOf

class ColsOfTests : ColumnsSelectionDslTests() {

    @Test
    fun `top level`() {
        listOf(
            df.select { city },

            df.select { colsOf<String?>() },
            df.select { all().colsOf<String?>() },
        ).shouldAllBeEqual()

        listOf(
            df.select { age },

            df.select { colsOf<Int?> { "a" in it.name } },
            df.select { all().colsOf<Int?> { "a" in it.name } },
        ).shouldAllBeEqual()
    }

    @Test
    fun `lower level`() {
        listOf(
            df.select { name { firstName and lastName } },

            df.select { name.colsOf<String>() },
            df.select { name.colsOf<String> { "Name" in it.name } },

            df.select { "name".colsOf<String>(typeOf<String>()) },
            df.select { "name".colsOf<String>(typeOf<String>()) { "Name" in it.name } },

            df.select { Person::name.colsOf<String>(typeOf<String>()) },
            df.select { Person::name.colsOf<String>(typeOf<String>()) { "Name" in it.name } },

            df.select { pathOf("name").colsOf<String>(typeOf<String>()) },
            df.select { pathOf("name").colsOf<String>(typeOf<String>()) { "Name" in it.name } },
        ).shouldAllBeEqual()
    }
}
