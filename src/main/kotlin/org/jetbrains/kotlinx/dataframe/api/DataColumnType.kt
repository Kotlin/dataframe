package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyColumn
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.Many
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnWithParent
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.impl.isSubtypeWithNullabilityOf
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.typeClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

public fun AnyCol.isColumnGroup(): Boolean = kind() == ColumnKind.Group
public fun AnyCol.isFrameColumn(): Boolean = kind() == ColumnKind.Frame
public fun AnyCol.hasElementsOfType(type: KType): Boolean = typeOfElement().isSubtypeWithNullabilityOf(type)
public fun AnyCol.isSubtypeOf(type: KType): Boolean = this.type.isSubtypeOf(type) && (!this.type.isMarkedNullable || type.isMarkedNullable)
public inline fun <reified T> AnyCol.hasElementsOfType(): Boolean = hasElementsOfType(getType<T>())
public inline fun <reified T> AnyCol.isSubtypeOf(): Boolean = isSubtypeOf(getType<T>())
public inline fun <reified T> AnyCol.isType(): Boolean = type() == getType<T>()
public fun AnyCol.isNumber(): Boolean = hasElementsOfType<Number?>()
public fun AnyCol.isMany(): Boolean = typeClass == Many::class
public fun AnyCol.typeOfElement(): KType =
    if (isMany()) type.arguments[0].type ?: getType<Any?>()
    else type

public fun AnyCol.elementTypeIsNullable(): Boolean = typeOfElement().isMarkedNullable
public fun AnyCol.isComparable(): Boolean = isSubtypeOf<Comparable<*>?>()

// TODO: remove by checking that type of column is always inferred
public fun AnyCol.guessType(): DataColumn<*> = DataColumn.create(name, toList())

public fun AnyColumn.unbox(): AnyCol = when (this) {
    is ColumnWithPath<*> -> data.unbox()
    is ColumnWithParent<*> -> source.unbox()
    else -> this as AnyCol
}
