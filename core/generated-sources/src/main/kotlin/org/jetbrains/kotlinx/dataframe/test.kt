package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet

public fun main() {
    val df = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
        "Alice", "Cooper", 15, "London", 54, true,
        "Bob", "Dylan", 45, "Dubai", 87, true,
        "Charlie", "Daniels", 20, "Moscow", null, false,
        "Charlie", "Chaplin", 40, "Milan", null, true,
        "Bob", "Marley", 30, "Tokyo", 68, true,
        "Alice", "Wolf", 20, null, 55, false,
        "Charlie", "Byrd", 30, "Moscow", 90, true
    ).group("firstName", "lastName").into("name")

    fun ColumnsSelectionDsl<*>.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        asSingleColumn().ensureIsColumnGroup().asColumnSet().run {
            if (includeGroups) dfsInternal { true } else dfsInternal { !it.isColumnGroup() }
        }

    df.select {
        allDfs()
    }.print(title = true, columnTypes = true, borders = true)
}
