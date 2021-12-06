package org.jetbrains.dataframe.gradle

object TestData {
    val csvSample = """
            name, age
            Alice, 15
            Bob,
        """.trimIndent()

    val csvName = "data.csv"

    val jsonSample = """{"name": "Test"}"""

    val jsonName = "test.json"

    val kotlinVersion = "1.6.0"
}
