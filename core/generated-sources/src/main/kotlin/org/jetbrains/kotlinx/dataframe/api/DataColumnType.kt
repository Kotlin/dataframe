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
 * Returns `true` if this column is a [<code>ColumnGroup</code>][ColumnGroup],
 * i.e., its [<code>kind</code>][DataColumn.kind] is [<code>ColumnKind.Group</code>][ColumnKind.Group].
 *
 * This function has a contract: when it returns `true`, the compiler smart-casts this column
 * to [<code>ColumnGroup</code>][ColumnGroup]`<*>`.
 *
 * See also [<code>isFrameColumn</code>][isFrameColumn], [<code>isValueColumn</code>][isValueColumn].
 *
 * Use [<code>asColumnGroup</code>][asColumnGroup] to cast a column to [<code>ColumnGroup</code>][ColumnGroup]`<*>` without the check.
 *
 * ### Examples
 *
 * ```kotlin
 * val name: AnyCol = df["name"]
 * if (name.isColumnGroup()) {
 *     // `name` is smart-cast to `ColumnGroup<*>` here
 *     name.columnNames() // returns [firstName, lastName]
 * }
 * ```
 *
 * @return `true` if this column is a [<code>ColumnGroup</code>][ColumnGroup], `false` otherwise.
 */
public fun AnyCol.isColumnGroup(): Boolean {
    contract { returns(true) implies (this@isColumnGroup is ColumnGroup<*>) }
    return kind() == ColumnKind.Group
}

/**
 * Returns `true` if this column is a [<code>FrameColumn</code>][FrameColumn],
 * i.e., its [<code>kind</code>][DataColumn.kind] is [<code>ColumnKind.Frame</code>][ColumnKind.Frame].
 *
 * This function has a contract: when it returns `true`, the compiler smart-casts this column
 * to [<code>FrameColumn</code>][FrameColumn]`<*>`.
 *
 * See also [<code>isColumnGroup</code>][isColumnGroup], [<code>isValueColumn</code>][isValueColumn].
 *
 * Use [<code>asFrameColumn</code>][asFrameColumn] to cast a column to [<code>FrameColumn</code>][FrameColumn]`<*>` without the check.
 *
 * ### Examples
 *
 * ```kotlin
 * val group: AnyCol = df.groupBy("city").toDataFrame()["group"]
 * if (group.isFrameColumn()) {
 *     // `group` is smart-cast to `FrameColumn<*>` here
 *     group.schema.value.columns.keys // returns [name, age, city, weight, isHappy]
 * }
 * ```
 *
 * @return `true` if this column is a [<code>FrameColumn</code>][FrameColumn], `false` otherwise.
 */
public fun AnyCol.isFrameColumn(): Boolean {
    contract { returns(true) implies (this@isFrameColumn is FrameColumn<*>) }
    return kind() == ColumnKind.Frame
}

/**
 * Returns `true` if this column is a [<code>ValueColumn</code>][ValueColumn],
 * i.e., its [<code>kind</code>][DataColumn.kind] is [<code>ColumnKind.Value</code>][ColumnKind.Value].
 *
 * This function has a contract: when it returns `true`, the compiler smart-casts this column
 * to [<code>ValueColumn</code>][ValueColumn]`<*>`.
 *
 * See also [<code>isColumnGroup</code>][isColumnGroup], [<code>isFrameColumn</code>][isFrameColumn].
 *
 * Use [<code>asValueColumn</code>][asValueColumn] to cast a column to [<code>ValueColumn</code>][ValueColumn]`<*>` without the check.
 *
 * ### Examples
 *
 * ```kotlin
 * val age: AnyCol = df["age"]
 * if (age.isValueColumn()) {
 *     // `age` is smart-cast to `ValueColumn<*>` here
 *     age.distinct() // returns a `ValueColumn` with 15, 45, 20, 40, 30, 18
 * }
 * ```
 *
 * @return `true` if this column is a [<code>ValueColumn</code>][ValueColumn], `false` otherwise.
 */
public fun AnyCol.isValueColumn(): Boolean {
    contract { returns(true) implies (this@isValueColumn is ValueColumn<*>) }
    return kind() == ColumnKind.Value
}

/**
 * Returns `true` if the [<code>type</code>][DataColumn.type] of this column is a subtype of the given [<code>type</code>][type].
 *
 * Nullability is respected: a column of a nullable type (like `Int?`)
 * is not a subtype of a non-nullable type (like `Int`),
 * while a column of a non-nullable type is a subtype of its nullable counterpart.
 *
 * Unlike [<code>isType</code>][isType], this check allows subtypes:
 * a column of type `Int` is a subtype of `Number`, but it is not of type `Number`.
 *
 * See also [<code>isSubtypeOf</code>][isSubtypeOf] with a reified type argument, [<code>isType</code>][isType].
 *
 * ### Examples
 *
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
 * @param [type] the [<code>KType</code>][KType] to check the column type against.
 * @return `true` if the column type is a subtype of [<code>type</code>][type], `false` otherwise.
 */
public fun AnyCol.isSubtypeOf(type: KType): Boolean =
    this.type.isSubtypeOf(type) && (!this.type.isMarkedNullable || type.isMarkedNullable)

/**
 * Returns `true` if the [<code>type</code>][DataColumn.type] of this column is a subtype of [<code>T</code>][T].
 *
 * Nullability is respected: a column of a nullable type (like `Int?`)
 * is not a subtype of a non-nullable type (like `Int`),
 * while a column of a non-nullable type is a subtype of its nullable counterpart.
 *
 * Unlike [<code>isType</code>][isType], this check allows subtypes:
 * a column of type `Int` is a subtype of `Number`, but it is not of type `Number`.
 *
 * See also [<code>isSubtypeOf</code>][isSubtypeOf] with a [<code>KType</code>][KType] argument, [<code>isType</code>][isType].
 *
 * ### Examples
 *
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
 * @return `true` if the column type is a subtype of [<code>T</code>][T], `false` otherwise.
 */
public inline fun <reified T> AnyCol.isSubtypeOf(): Boolean = isSubtypeOf(typeOf<T>())

/**
 * Returns `true` only if the [<code>type</code>][DataColumn.type] of this column is exactly [<code>T</code>][T].
 *
 * Unlike [<code>isSubtypeOf</code>][isSubtypeOf], no subtyping is allowed and nullability must match as well:
 * a column of type `Int` is neither of type `Number` nor of type `Int?`.
 *
 * See also [<code>isSubtypeOf</code>][isSubtypeOf].
 *
 * ### Examples
 *
 * ```kotlin
 * // `age: Int`
 * df["age"].isType<Int>() // true
 * df["age"].isType<Int?>() // false
 * df["age"].isType<Number>() // false
 * ```
 *
 * @param [T] the type to compare the column type with.
 * @return `true` if the column type is exactly [<code>T</code>][T], `false` otherwise.
 */
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

/**
 * Returns `true` when the class of this column's [<code>type</code>][DataColumn.type] is [<code>List</code>][List],
 * i.e., the column holds `List<*>` (or `List<*>?`) values.
 *
 * Only the [<code>List</code>][List] class itself is matched: a column of a [<code>List</code>][List] subtype (like `ArrayList<*>`)
 * or of another [<code>Collection</code>][Collection] type (like `Set<*>`) is not considered a list column.
 *
 * ### Examples
 *
 * ```kotlin
 * val ages = df.groupBy("city").aggregate { this["age"].toList() into "ages" }
 *
 * ages["ages"].isList() // true, `ages` holds lists of ages
 * ages["city"].isList() // false
 * ```
 *
 * @return `true` if the column type class is [<code>List</code>][List], `false` otherwise.
 */
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
