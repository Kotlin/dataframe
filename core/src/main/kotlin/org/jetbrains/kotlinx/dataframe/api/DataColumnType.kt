package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.isNothing
import org.jetbrains.kotlinx.dataframe.impl.projectTo
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.typeClass
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

public fun AnyCol.isColumnGroup(): Boolean = kind() == ColumnKind.Group

public fun AnyCol.isFrameColumn(): Boolean = kind() == ColumnKind.Frame

public fun AnyCol.isValueColumn(): Boolean = kind() == ColumnKind.Value

public fun AnyCol.isSubtypeOf(type: KType): Boolean =
    this.type.isSubtypeOf(type) && (!this.type.isMarkedNullable || type.isMarkedNullable)

public inline fun <reified T> AnyCol.isSubtypeOf(): Boolean = isSubtypeOf(typeOf<T>())

public inline fun <reified T> AnyCol.isType(): Boolean = type() == typeOf<T>()

public fun AnyCol.isNumber(): Boolean = isSubtypeOf<Number?>()

public fun AnyCol.isList(): Boolean = typeClass == List::class

/**
 * Returns `true` if [this] column is comparable, i.e. its type is a subtype of [Comparable] and its
 * type argument is not [Nothing].
 */
public fun AnyCol.isComparable(): Boolean =
    isSubtypeOf<Comparable<*>?>() &&
        type().projectTo(Comparable::class).arguments[0].let {
            it != KTypeProjection.STAR &&
                it.type?.isNothing != true
        }

@PublishedApi
internal fun AnyCol.isPrimitive(): Boolean = typeClass.isPrimitive()

internal fun KClass<*>.isPrimitive(): Boolean =
    isSubclassOf(Number::class) ||
        this == String::class ||
        this == Char::class ||
        this == Array::class ||
        isSubclassOf(Collection::class)
