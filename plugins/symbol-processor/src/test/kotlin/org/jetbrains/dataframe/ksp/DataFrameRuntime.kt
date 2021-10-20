package org.jetbrains.dataframe.ksp

import com.tschuchort.compiletesting.SourceFile

internal val annotations = SourceFile.kotlin("Annotations.kt", """
    package ${DataFrameNames.DATAFRAME_PACKAGE}.annotations

    annotation class DataSchema(val isOpen: Boolean = true)
    annotation class ColumnName(val name: String)
""".trimIndent())

internal val dataColumn = SourceFile.kotlin("DataColumn.kt", """
    package ${DataFrameNames.DATAFRAME_PACKAGE}.columns

    interface ColumnGroup<out T>
""".trimIndent())

internal val dataFrame = SourceFile.kotlin("DataFrame.kt", """
    package ${DataFrameNames.DATAFRAME_PACKAGE}

    import ${DataFrameNames.DATAFRAME_PACKAGE}.columns.*

    interface DataColumn<out T>
    interface DataFrameBase<out T> {
        operator fun get(columnName: String): DataColumn<*>
    }
    interface DataFrame<out T>
""".trimIndent())

internal val dataRow = SourceFile.kotlin("DataRow.kt", """
    package ${DataFrameNames.DATAFRAME_PACKAGE}

    interface DataRow<out T> {
        operator fun get(name: String): Any?
    }
""".trimIndent())
