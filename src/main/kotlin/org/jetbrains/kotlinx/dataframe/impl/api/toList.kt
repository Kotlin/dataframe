package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asIterable
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.asFrameColumn
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

@PublishedApi
internal fun AnyFrame.toListImpl(type: KType): List<Any> {
    val clazz = type.jvmErasure
    require(clazz.isData) { "Class `$clazz` is not a data class. `toList` is supported only for data classes." }

    val constructor = clazz.primaryConstructor
    require(constructor != null) { "Class `$clazz` doesn't have a primary constructor" }

    val columnNames = clazz.memberProperties.map {
        it.name to it.columnName
    }.toMap()

    val convertedColumns = constructor.parameters.map {
        require(it.name != null) { "Parameter name can not be null. Parameter = $it" }
        val columnName = columnNames[it.name]

        check(columnName != null) { "Can not find member property for parameter ${it.name}" }
        val index = getColumnIndex(columnName)

        check(index >= 0) { "Can not find column `$columnName` in DataFrame" }

        val column = getColumn(index)
        val convertedColumn = if (column.type != it.type) {
            when (column.kind) {
                ColumnKind.Frame -> {
                    val col: AnyCol = if (it.type.jvmErasure == List::class) {
                        val elementType = it.type.arguments[0].type
                        require(elementType != null) { "FrameColumn can not be converted to type `List<*>`" }
                        column.asFrameColumn().map { it?.toListImpl(elementType) }
                    } else error("FrameColumn can not be converted to type `${it.type}`")
                    col
                }
                ColumnKind.Group -> {
                    DataColumn.createValueColumn(column.name(), column.asColumnGroup().df.toListImpl(it.type))
                }
                ColumnKind.Value -> {
                    column.convertTo(it.type)
                }
            }
        } else column
        convertedColumn
    }

    return asIterable().map { row ->
        val parameters = convertedColumns.map {
            row[it]
        }.toTypedArray()
        constructor.call(*parameters)
    }
}
