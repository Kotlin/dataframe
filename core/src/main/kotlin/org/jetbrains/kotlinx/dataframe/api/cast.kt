@file:Suppress("UNCHECKED_CAST")

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.Check
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.api.convertToImpl
import kotlin.reflect.typeOf

@Check
public fun <T> AnyFrame.cast(): DataFrame<T> = this as DataFrame<T>

public inline fun <reified T> AnyFrame.cast(verify: Boolean = true): DataFrame<T> =
    if (verify) {
        convertToImpl(
            typeOf<T>(),
            allowConversion = false,
            ExcessiveColumns.Keep,
        ).cast()
    } else {
        cast()
    }

public inline fun <reified T> AnyFrame.castTo(
    @Suppress("UNUSED_PARAMETER") schemaFrom: DataFrame<T>,
    verify: Boolean = true,
): DataFrame<T> = cast<T>(verify = verify)

/**
 * With the compiler plugin, schema marker T of DataFrame can be a local type.
 * You cannot refer to it directly from your code, like a type argument for cast.
 * The example below shows a situation where you'd need to cast DataFrame<*> to DataFrame<plugin generated local type>.
 * This function helps by inferring type from [schemaFrom]
 * ```
 *
 * // parse listOf("b:1:abc", "c:2:bca")
 * private fun convert(data: List<String>)/*: DataFrame<plugin generated local type>*/ = data.map { it.split(":") }.toDataFrame {
 *      "part1" from { it[0] }
 *      "part2" from { it[1].toInt() }
 *      "part3" from { it[2] }
 * }
 *
 * fun serialize(data: List<String>, destination: File) {
 *      convert(data).writeJson(destination)
 * }
 *
 * fun deserializeAndUse(file: File) {
 *      val df = DataFrame.readJson(file).castTo(schemaFrom = ::convert)
 *      // Possible to use properties
 *      df.part1.print()
 * }
 * ```
 */
public inline fun <reified T> AnyFrame.castTo(
    @Suppress("UNUSED_PARAMETER") schemaFrom: Function<DataFrame<T>>,
    verify: Boolean = true,
): DataFrame<T> = cast<T>(verify = verify)

public fun <T> AnyRow.cast(): DataRow<T> = this as DataRow<T>

public inline fun <reified T> AnyRow.cast(verify: Boolean = true): DataRow<T> = df().cast<T>(verify)[0]

public fun <T> AnyCol.cast(): DataColumn<T> = this as DataColumn<T>

public fun <T> ValueColumn<*>.cast(): ValueColumn<T> = this as ValueColumn<T>

public fun <T> FrameColumn<*>.castFrameColumn(): FrameColumn<T> = this as FrameColumn<T>

public fun <T> ColumnGroup<*>.cast(): ColumnGroup<T> = this as ColumnGroup<T>

public fun <T> ColumnWithPath<*>.cast(): ColumnWithPath<T> = this as ColumnWithPath<T>

@Interpretable("ColumnAccessorCast")
public fun <T> ColumnAccessor<*>.cast(): ColumnAccessor<T> = this as ColumnAccessor<T>

@Interpretable("ColumnSetCast")
public fun <C> ColumnSet<*>.cast(): ColumnSet<C> = this as ColumnSet<C>

public fun <C> ColumnsResolver<*>.cast(): ColumnsResolver<C> = this as ColumnsResolver<C>

public fun <C> SingleColumn<*>.cast(): SingleColumn<C> = this as SingleColumn<C>

public fun <C> ColumnReference<*>.cast(): ColumnReference<C> = this as ColumnReference<C>

public fun <T, G> GroupBy<*, *>.cast(): GroupBy<T, G> = this as GroupBy<T, G>
