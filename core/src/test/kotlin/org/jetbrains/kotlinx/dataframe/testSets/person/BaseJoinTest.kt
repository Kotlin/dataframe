package org.jetbrains.kotlinx.dataframe.testSets.person

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf

open class BaseJoinTest : BaseTest() {
    val df2 =
        dataFrameOf("name", "origin", "grade", "age")(
            "Alice",
            "London",
            3,
            "young",
            "Alice",
            "London",
            5,
            "old",
            "Bob",
            "Tokyo",
            4,
            "young",
            "Bob",
            "Paris",
            5,
            "old",
            "Charlie",
            "Moscow",
            1,
            "young",
            "Charlie",
            "Moscow",
            2,
            "old",
            "Bob",
            "Paris",
            4,
            null,
        )
    val typed2: DataFrame<Person2> = df2.cast()

    @DataSchema
    interface Person2 {
        val name: String
        val origin: String?
        val grade: Int
    }
}
