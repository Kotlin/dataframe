@file:OptIn(ExperimentalContracts::class)

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.isIntraComparable
import org.jetbrains.kotlinx.dataframe.impl.isMixedNumber
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveNumber
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveOrMixedNumber
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.typeClass
import org.jetbrains.kotlinx.dataframe.util.IS_COMPARABLE
import org.jetbrains.kotlinx.dataframe.util.IS_COMPARABLE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.IS_INTER_COMPARABLE_IMPORT
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
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

/** Returns `true` when this column's type is a subtype of `Number?` */
public fun AnyCol.isNumber(): Boolean = isSubtypeOf<Number?>()

/** Returns `true` only when this column's type is exactly `Number` or `Number?`. */
public fun AnyCol.isMixedNumber(): Boolean = type().isMixedNumber()

/**
 * Returns `true` when this column has the (nullable) type of either:
 * [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], or [<code>Double</code>][Double].
 */
public fun AnyCol.isPrimitiveNumber(): Boolean = type().isPrimitiveNumber()

/**
 * Returns `true` when this column has the (nullable) type of either:
 * [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], [<code>Double</code>][Double], or [<code>Number</code>][Number].
 *
 * Careful: Will return `true` if the column contains multiple number types that
 * might NOT be primitive.
 */
public fun AnyCol.isPrimitiveOrMixedNumber(): Boolean = type().isPrimitiveOrMixedNumber()

public fun AnyCol.isList(): Boolean = typeClass == List::class

/** Returns `true` if [<code>this</code>][this] column is intra-comparable (mutually comparable), i.e.,
 * its values can be compared with each other and thus ordered.
 *
 * If true, operations like [<code>`min()`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.min], [<code>`max()`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.max], [<code>`median()`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.median], etc.
 * will work.
 *
 * Technically, this means the values' common type `T(?)` is a subtype of [<code>Comparable</code>][Comparable]`<in T>(?)` */
@Deprecated(
    message = IS_COMPARABLE,
    replaceWith = ReplaceWith(IS_COMPARABLE_REPLACE, IS_INTER_COMPARABLE_IMPORT),
    level = DeprecationLevel.ERROR,
)
public fun AnyCol.isComparable(): Boolean = valuesAreComparable()

/**
 * Returns `true` if [<code>this</code>][this] column is intra-comparable (mutually comparable), i.e.,
 * its values can be compared with each other and thus ordered.
 *
 * If true, operations like [<code>`min()`</code>][DataColumn.min], [<code>`max()`</code>][DataColumn.max], [<code>`median()`</code>][DataColumn.median], etc.
 * will work.
 *
 * Technically, this means the values' common type `T(?)` is a subtype of [<code>Comparable</code>][Comparable]`<in T>(?)`
 */
public fun AnyCol.valuesAreComparable(): Boolean = isValueColumn() && type().isIntraComparable()
