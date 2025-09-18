package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.*

private val a = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
    "Alice", "Cooper", 15, "London", 54, true,
    "Bob", "Dylan", 45, "Dubai", 87, true,
    "Charlie", "Daniels", 20, "Moscow", null, false,
    "Charlie", "Chaplin", 40, "Milan", null, true,
    "Bob", "Marley", 30, "Tokyo", 68, true,
    "Alice", "Wolf", 20, null, 55, false,
    "Charlie", "Byrd", 30, "Moscow", 90, true,
).group { firstName and lastName }.into("name")
