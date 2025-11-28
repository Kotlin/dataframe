package org.jetbrains.kotlinx.dataframe.impl.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.isList
import org.jetbrains.kotlinx.dataframe.api.isValueColumn
import org.jetbrains.kotlinx.dataframe.api.rows
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

private fun Any?.toToonPrimitive(type: KType): ToonPrimitive<*> {
    val type = type.withNullability(false)
    return when {
        this == null -> ToonPrimitive.NULL
        type == typeOf<Boolean>() -> ToonPrimitive(this as Boolean)
        type.isSubtypeOf(typeOf<Number>()) -> ToonPrimitive(this as Number)
        type == typeOf<String>() -> ToonPrimitive(this as String)
        else -> ToonPrimitive(this)
    }
}

internal fun encodeToToonImpl(df: AnyFrame): ToonArray {
    val isFlat = df.columns().all { it.isValueColumn() }
    if (isFlat) {
        val header = df.columnNames()
        val types = df.columnTypes()

        val objectArray = df.rows().map { row ->
            header.indices.map { i ->
                row[i].toToonPrimitive(types[i])
            }
        }

        return ToonArray(
            header = header,
            objectArray = objectArray,
        )
    }

    val objects = df.rows().map { encodeToToonImpl(it) }
    return ToonArray(objects)
}

internal fun encodeToToonImpl(row: AnyRow): ToonObject {
    val values = row.df().columns().associate { col ->
        col.name() to when {
            col.isColumnGroup() -> encodeToToonImpl(col[row.index()])

            col.isFrameColumn() -> encodeToToonImpl(col[row.index()])

            col.isList() -> col.cast<List<*>?>()[row.index()]?.let {
                ToonArray(it.map { ToonPrimitive(it) })
            } ?: ToonPrimitive.NULL

            else -> col[row.index()].toToonPrimitive(col.type())
        }
    }
    return ToonObject(values)
}
