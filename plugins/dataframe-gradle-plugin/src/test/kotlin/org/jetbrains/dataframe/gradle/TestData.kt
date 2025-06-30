package org.jetbrains.dataframe.gradle

import org.jetbrains.kotlinx.dataframe.BuildConfig

object TestData {

    val csvSample =
        """
        name, age
        Alice, 15
        Bob,
        """.trimIndent()

    val csvName = "data.csv"

    val jsonSample = """{"name": "Test"}"""

    val jsonName = "test.json"

    val kotlinVersion = BuildConfig.KOTLIN_VERSION
}
