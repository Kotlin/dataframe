package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow

internal fun AnyFrame.toJson(prettyPrint: Boolean = false): String {
    val jsonClass = try {
        Class.forName("org.jetbrains.kotlinx.dataframe.io.JsonKt")
    } catch (_: ClassNotFoundException) {
        error(
            "Encountered a DataFrame when writing to an Excel cell. This needs to be converted to JSON, so the dataframe-json dependency is required.",
        )
    }
    return jsonClass.getMethod("toJson", AnyFrame::class.java, Boolean::class.java)
        .invoke(null, this, prettyPrint) as String
}

internal fun AnyRow.toJson(prettyPrint: Boolean = false): String {
    val jsonClass = try {
        Class.forName("org.jetbrains.kotlinx.dataframe.io.JsonKt")
    } catch (_: ClassNotFoundException) {
        error(
            "Encountered a DataRow when writing to an Excel cell. This needs to be converted to JSON, so the dataframe-json dependency is required.",
        )
    }
    return jsonClass.getMethod("toJson", AnyRow::class.java, Boolean::class.java)
        .invoke(null, this, prettyPrint) as String
}
