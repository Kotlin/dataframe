@file:OptIn(ExperimentalContracts::class)

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.primitiveNumberTypes
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.typeClass
import org.jetbrains.kotlinx.dataframe.util.IS_COMPARABLE
import org.jetbrains.kotlinx.dataframe.util.IS_COMPARABLE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.IS_INTER_COMPARABLE_IMPORT
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

public fun AnyCol.isColumnGroup(): Boolean {
    contract { returns(true) implies (this@isColumnGroup is ColumnGroup<*>) }
    return kind() == ColumnKind.Group
}

public fun AnyCol.isFrameColumn(): Boolean {
    contract { returns(true) implies (this@isFrameColumn is FrameColumn<*>) }
    return kind() == ColumnKind.Frame
}

public fun AnyCol.isValueColumn(): Boolean {
    contract { returns(true) implies (this@isValueColumn is ValueColumn<*>) }
    return kind() == ColumnKind.Value
}

public fun AnyCol.isSubtypeOf(type: KType): Boolean =
    this.type.isSubtypeOf(type) && (!this.type.isMarkedNullable || type.isMarkedNullable)

public inline fun <reified T> AnyCol.isSubtypeOf(): Boolean = isSubtypeOf(typeOf<T>())

public inline fun <reified T> AnyCol.isType(): Boolean = type() == typeOf<T>()

public fun AnyCol.isNumber(): Boolean = isSubtypeOf<Number?>()

public fun AnyCol.isBigNumber(): Boolean = isSubtypeOf<BigInteger?>() || isSubtypeOf<BigDecimal?>()

public fun AnyCol.isPrimitiveNumber(): Boolean = type().withNullability(false) in primitiveNumberTypes

public fun AnyCol.isList(): Boolean = typeClass == List::class

/** Returns `true` if [this] column is intra-comparable, i.e.
 * its values can be compared with each other and thus ordered.
 *
 * If true, operations like [`min()`][AnyCol.min], [`max()`][AnyCol.max], [`median()`][AnyCol.median], etc.
 * will work.
 *
 * Technically, this means the values' common type `T(?)` is a subtype of [Comparable]`<in T>(?)` */
@Deprecated(
    message = IS_COMPARABLE,
    replaceWith = ReplaceWith(IS_COMPARABLE_REPLACE, IS_INTER_COMPARABLE_IMPORT),
    level = DeprecationLevel.ERROR,
)
public fun AnyCol.isComparable(): Boolean = valuesAreComparable()

/**
 * Returns `true` if [this] column is intra-comparable, i.e.
 * its values can be compared with each other and thus ordered.
 *
 * If true, operations like [`min()`][AnyCol.min], [`max()`][AnyCol.max], [`median()`][AnyCol.median], etc.
 * will work.
 *
 * Technically, this means the values' common type `T(?)` is a subtype of [Comparable]`<in T>(?)`
 */
public fun AnyCol.valuesAreComparable(): Boolean =
    isValueColumn() &&
        isSubtypeOf(
            Comparable::class.createType(
                arguments = listOf(
                    KTypeProjection(KVariance.IN, type().withNullability(false)),
                ),
                nullable = hasNulls(),
            ),
        )

@PublishedApi
internal fun AnyCol.isPrimitive(): Boolean = typeClass.isPrimitive()

internal fun KClass<*>.isPrimitive(): Boolean =
    isSubclassOf(Number::class) ||
        this == String::class ||
        this == Char::class ||
        this == Array::class ||
        isSubclassOf(Collection::class)
