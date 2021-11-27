package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.typeClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

public fun AnyCol.isColumnGroup(): Boolean = kind() == ColumnKind.Group
public fun AnyCol.isFrameColumn(): Boolean = kind() == ColumnKind.Frame
public fun AnyCol.isValueColumn(): Boolean = kind() == ColumnKind.Value

public fun AnyCol.isSubtypeOf(type: KType): Boolean = this.type.isSubtypeOf(type) && (!this.type.isMarkedNullable || type.isMarkedNullable)
public inline fun <reified T> AnyCol.isSubtypeOf(): Boolean = isSubtypeOf(getType<T>())
public inline fun <reified T> AnyCol.isType(): Boolean = type() == getType<T>()
public fun AnyCol.isNumber(): Boolean = isSubtypeOf<Number?>()
public fun AnyCol.isList(): Boolean = typeClass == List::class
public fun AnyCol.typeOfElement(): KType =
    if (isList()) type.arguments[0].type ?: getType<Any?>()
    else type

public fun AnyCol.elementTypeIsNullable(): Boolean = typeOfElement().isMarkedNullable
public fun AnyCol.isComparable(): Boolean = isSubtypeOf<Comparable<*>?>()

public fun AnyCol.inferType(): DataColumn<*> = DataColumn.createWithTypeInference(name, toList())
