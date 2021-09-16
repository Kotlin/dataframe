package org.jetbrains.dataframe.ksp

import com.tschuchort.compiletesting.SourceFile

internal val annotations = SourceFile.kotlin("Annotations.kt", """
    package org.jetbrains.dataframe.annotations

    annotation class DataSchema(val isOpen: Boolean)
    annotation class ColumnName(val name: String)
""".trimIndent())

internal val dataColumn = SourceFile.kotlin("DataColumn.kt", """
    package org.jetbrains.dataframe.columns

    interface DataColumn<out T>
    interface ColumnGroup<out T>
""".trimIndent())

internal val dataFrame = SourceFile.kotlin("DataFrame.kt", """
    package org.jetbrains.dataframe

    import org.jetbrains.dataframe.columns.*
        
    interface DataFrameBase<out T> {
        operator fun get(columnName: String): DataColumn<*>
    }
    interface DataRow<out T>
    interface DataFrame<out T>
""".trimIndent())

internal val dataRow = SourceFile.kotlin("DataRow.kt", """
    package org.jetbrains.dataframe

    interface DataRowBase<out T> {
        operator fun get(name: String): Any?
    }
""".trimIndent())
