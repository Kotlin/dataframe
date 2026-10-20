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

/**
 * Returns `true` if this column is a [ColumnGroup],
 * i.e., its [kind][DataColumn.kind] is [ColumnKind.Group].
 *
 * This function has a contract: when it returns `true`, the compiler smart-casts this column
 * to [ColumnGroup]`<*>`.
 *
 * See also [isFrameColumn], [isValueColumn].
 *
 * ### Example
 * ```kotlin
 * val col: AnyCol = df["name"]
 * if (col.isColumnGroup()) {
 *     // `col` is a `ColumnGroup<*>` here
 *     col.columnNames()
 * }
 * ```
 *
 * @return `true` if this column is a [ColumnGroup], `false` otherwise.
 */
public fun AnyCol.isColumnGroup(): Boolean {
    contract { returns(true) implies (this@isColumnGroup is ColumnGroup<*>) }
    return kind() == ColumnKind.Group
}

/**
 * Returns `true` if this column is a [FrameColumn],
 * i.e., its [kind][DataColumn.kind] is [ColumnKind.Frame].
 *
 * This function has a contract: when it returns `true`, the compiler smart-casts this column
 * to [FrameColumn]`<*>`.
 *
 * See also [isColumnGroup], [isValueColumn].
 *
 * ### Example
 * ```kotlin
 * val col: AnyCol = df["orders"]
 * if (col.isFrameColumn()) {
 *     // `col` is a `FrameColumn<*>` here
 *     col.schema.value
 * }
 * ```
 *
 * @return `true` if this column is a [FrameColumn], `false` otherwise.
 */
public fun AnyCol.isFrameColumn(): Boolean {
    contract { returns(true) implies (this@isFrameColumn is FrameColumn<*>) }
    return kind() == ColumnKind.Frame
}

/**
 * Returns `true` if this column is a [ValueColumn],
 * i.e., its [kind][DataColumn.kind] is [ColumnKind.Value].
 *
 * This function has a contract: when it returns `true`, the compiler smart-casts this column
 * to [ValueColumn]`<*>`.
 *
 * See also [isColumnGroup], [isFrameColumn].
 *
 * ### Example
 * ```kotlin
 * val col: AnyCol = df["age"]
 * if (col.isValueColumn()) {
 *     // `col` is a `ValueColumn<*>` here
 *     col.distinct()
 * }
 * ```
 *
 * @return `true` if this column is a [ValueColumn], `false` otherwise.
 */
public fun AnyCol.isValueColumn(): Boolean {
    contract { returns(true) implies (this@isValueColumn is ValueColumn<*>) }
    return kind() == ColumnKind.Value
}

/**
 * Returns `true` if the [type][DataColumn.type] of this column is a subtype of the given [type].
 *
 * Nullability is respected: a column of a nullable type (like `Int?`)
 * is not a subtype of a non-nullable type (like `Int`),
 * while a column of a non-nullable type is a subtype of its nullable counterpart.
 *
 * Unlike [isType], this check allows subtypes:
 * a column of type `Int` is a subtype of `Number`, but it is not of type `Number`.
 *
 * See also [isSubtypeOf] with a reified type argument, [isType].
 *
 * ### Examples
 * ```kotlin
 * // `age: Int`
 * df["age"].isSubtypeOf(typeOf<Number>()) // true
 * df["age"].isSubtypeOf(typeOf<Int?>()) // true
 * df["age"].isSubtypeOf(typeOf<String>()) // false
 *
 * // `weight: Int?`
 * df["weight"].isSubtypeOf(typeOf<Int>()) // false
 * df["weight"].isSubtypeOf(typeOf<Number?>()) // true
 * ```
 *
 * @param [type] the [KType] to check the column type against.
 * @return `true` if the column type is a subtype of [type], `false` otherwise.
 */
public fun AnyCol.isSubtypeOf(type: KType): Boolean =
    this.type.isSubtypeOf(type) && (!this.type.isMarkedNullable || type.isMarkedNullable)

/**
 * Returns `true` if the [type][DataColumn.type] of this column is a subtype of [T].
 *
 * Nullability is respected: a column of a nullable type (like `Int?`)
 * is not a subtype of a non-nullable type (like `Int`),
 * while a column of a non-nullable type is a subtype of its nullable counterpart.
 *
 * Unlike [isType], this check allows subtypes:
 * a column of type `Int` is a subtype of `Number`, but it is not of type `Number`.
 *
 * See also [isSubtypeOf] with a [KType] argument, [isType].
 *
 * ### Examples
 * ```kotlin
 * // `age: Int`
 * df["age"].isSubtypeOf<Number>() // true
 * df["age"].isSubtypeOf<Int?>() // true
 * df["age"].isSubtypeOf<String>() // false
 *
 * // `weight: Int?`
 * df["weight"].isSubtypeOf<Int>() // false
 * df["weight"].isSubtypeOf<Number?>() // true
 * ```
 *
 * @param [T] the type to check the column type against.
 * @return `true` if the column type is a subtype of [T], `false` otherwise.
 */
public inline fun <reified T> AnyCol.isSubtypeOf(): Boolean = isSubtypeOf(typeOf<T>())

/**
 * Returns `true` only if the [type][DataColumn.type] of this column is exactly [T].
 *
 * Unlike [isSubtypeOf], no subtyping is allowed and nullability must match as well:
 * a column of type `Int` is neither of type `Number` nor of type `Int?`.
 *
 * See also [isSubtypeOf].
 *
 * ### Examples
 * ```kotlin
 * // `age: Int`
 * df["age"].isType<Int>() // true
 * df["age"].isType<Int?>() // false
 * df["age"].isType<Number>() // false
 * ```
 *
 * @param [T] the type to compare the column type with.
 * @return `true` if the column type is exactly [T], `false` otherwise.
 */
public inline fun <reified T> AnyCol.isType(): Boolean = type() == typeOf<T>()

/** Returns `true` when this column's type is a subtype of `Number?` */
public fun AnyCol.isNumber(): Boolean = isSubtypeOf<Number?>()

/** Returns `true` only when this column's type is exactly `Number` or `Number?`. */
public fun AnyCol.isMixedNumber(): Boolean = type().isMixedNumber()

/**
 * Returns `true` when this column has the (nullable) type of either:
 * [Byte], [Short], [Int], [Long], [Float], or [Double].
 */
public fun AnyCol.isPrimitiveNumber(): Boolean = type().isPrimitiveNumber()

/**
 * Returns `true` when this column has the (nullable) type of either:
 * [Byte], [Short], [Int], [Long], [Float], [Double], or [Number].
 *
 * Careful: Will return `true` if the column contains multiple number types that
 * might NOT be primitive.
 */
public fun AnyCol.isPrimitiveOrMixedNumber(): Boolean = type().isPrimitiveOrMixedNumber()

/**
 * Returns `true` when the class of this column's [type][DataColumn.type] is [List],
 * i.e., the column holds `List<*>` (or `List<*>?`) values.
 *
 * Only the [List] class itself is matched: a column of a [List] subtype (like `ArrayList<*>`)
 * or of another [Collection] type (like `Set<*>`) is not considered a list column.
 */
public fun AnyCol.isList(): Boolean = typeClass == List::class

/** @include [valuesAreComparable] */
@Deprecated(
    message = IS_COMPARABLE,
    replaceWith = ReplaceWith(IS_COMPARABLE_REPLACE, IS_INTER_COMPARABLE_IMPORT),
    level = DeprecationLevel.ERROR,
)
public fun AnyCol.isComparable(): Boolean = valuesAreComparable()

/**
 * Returns `true` if [this] column is intra-comparable (mutually comparable), i.e.,
 * its values can be compared with each other and thus ordered.
 *
 * If true, operations like [`min()`][DataColumn.min], [`max()`][DataColumn.max], [`median()`][DataColumn.median], etc.
 * will work.
 *
 * Technically, this means the values' common type `T(?)` is a subtype of [Comparable]`<in T>(?)`
 */
public fun AnyCol.valuesAreComparable(): Boolean = isValueColumn() && type().isIntraComparable()
