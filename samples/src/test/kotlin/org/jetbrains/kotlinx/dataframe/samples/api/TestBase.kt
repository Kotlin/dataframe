@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into

@Suppress("ktlint:standard:argument-list-wrapping")
interface TestBase {

    val peopleDf: DataFrame<Person>
        get() = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
            "Alice", "Cooper", 15, "London", 54, true,
            "Bob", "Dylan", 45, "Dubai", 87, true,
            "Charlie", "Daniels", 20, "Moscow", null, false,
            "Charlie", "Chaplin", 40, "Milan", null, true,
            "Bob", "Marley", 30, "Tokyo", 68, true,
            "Alice", "Wolf", 20, null, 55, false,
            "Charlie", "Byrd", 30, "Moscow", 90, true,
        ).group("firstName", "lastName").into("name")
            .cast<Person>(verify = false)

    @DataSchema
    interface Name {
        val firstName: String
        val lastName: String
    }

    @DataSchema
    interface Person {
        val age: Int
        val city: String?
        val name: DataRow<Name> // TODO Requires https://code.jetbrains.team/p/kt/repositories/kotlin/reviews/23694 to be merged
        val weight: Int?
        val isHappy: Boolean
    }

    infix fun <T, U : T> T.willBe(expected: U?) = shouldBe(expected)
}
