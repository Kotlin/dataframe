package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.cumProdByteImpl
import org.jetbrains.kotlinx.dataframe.impl.api.cumProdDoubleImpl
import org.jetbrains.kotlinx.dataframe.impl.api.cumProdFloatImpl
import org.jetbrains.kotlinx.dataframe.impl.api.cumProdIntImpl
import org.jetbrains.kotlinx.dataframe.impl.api.cumProdLongImpl
import org.jetbrains.kotlinx.dataframe.impl.api.cumProdShortImpl

// Adapted from the cumulative-operation documentation in cumSum.kt.

/**
 * Computes the cumulative product from the first value to the last value in this [DataColumn].
 *
 * Supports [Byte], [Short], [Int], [Long], [Float], and [Double] columns.
 * [Byte] and [Short] are promoted to [Int], as in [cumSum]; other element types are preserved.
 * The result preserves the name, row count, and declared nullability, including empty columns.
 * The default value is preserved, with [Byte] and [Short] defaults promoted to [Int].
 * Mixed [Number] columns and arbitrary-precision numbers are outside this API.
 *
 * With [skipNA\] set to `true`, input `null` and `NaN` remain at their original positions and
 * are ignored when computing subsequent products. With `false`, the first missing input representation
 * is repeated at every subsequent position. Nullable floating-point columns retain `null` as `null`.
 *
 * Integer multiplication uses ordinary Kotlin arithmetic and wraps on overflow; it is not checked.
 * Floating-point multiplication follows IEEE 754, including rounding, underflow, overflow, and signed zeros.
 * A `NaN` produced by multiplication, such as infinity times zero, remains in the accumulated product
 * even when missing input values are skipped.
 *
 * For example, `2, null, 3, 4` becomes `2, null, 6, 24` with skipping and
 * `2, null, null, null` without skipping.
 *
 * @param [skipNA\] Whether to skip missing input values when computing subsequent products (default: `true`).
 * @return A new column containing the cumulative products.
 * @see [cumSum]
 * @see {@include [DocumentationUrls.CumProdOnColumn]}
 */
@ExcludeFromSources
private interface CumProdDocs

/** @include [CumProdDocs] */
@JvmName("cumProdByte")
public fun DataColumn<Byte>.cumProd(skipNA: Boolean = true): DataColumn<Int> = cumProdByteImpl(skipNA).cast()

/** @include [CumProdDocs] */
@JvmName("cumProdNullableByte")
public fun DataColumn<Byte?>.cumProd(skipNA: Boolean = true): DataColumn<Int?> = cumProdByteImpl(skipNA)

/** @include [CumProdDocs] */
@JvmName("cumProdShort")
public fun DataColumn<Short>.cumProd(skipNA: Boolean = true): DataColumn<Int> = cumProdShortImpl(skipNA).cast()

/** @include [CumProdDocs] */
@JvmName("cumProdNullableShort")
public fun DataColumn<Short?>.cumProd(skipNA: Boolean = true): DataColumn<Int?> = cumProdShortImpl(skipNA)

/** @include [CumProdDocs] */
@JvmName("cumProdInt")
public fun DataColumn<Int>.cumProd(skipNA: Boolean = true): DataColumn<Int> = cumProdIntImpl(skipNA).cast()

/** @include [CumProdDocs] */
@JvmName("cumProdNullableInt")
public fun DataColumn<Int?>.cumProd(skipNA: Boolean = true): DataColumn<Int?> = cumProdIntImpl(skipNA)

/** @include [CumProdDocs] */
@JvmName("cumProdLong")
public fun DataColumn<Long>.cumProd(skipNA: Boolean = true): DataColumn<Long> = cumProdLongImpl(skipNA).cast()

/** @include [CumProdDocs] */
@JvmName("cumProdNullableLong")
public fun DataColumn<Long?>.cumProd(skipNA: Boolean = true): DataColumn<Long?> = cumProdLongImpl(skipNA)

/** @include [CumProdDocs] */
@JvmName("cumProdFloat")
public fun DataColumn<Float>.cumProd(skipNA: Boolean = true): DataColumn<Float> = cumProdFloatImpl(skipNA).cast()

/** @include [CumProdDocs] */
@JvmName("cumProdNullableFloat")
public fun DataColumn<Float?>.cumProd(skipNA: Boolean = true): DataColumn<Float?> = cumProdFloatImpl(skipNA)

/** @include [CumProdDocs] */
@JvmName("cumProdDouble")
public fun DataColumn<Double>.cumProd(skipNA: Boolean = true): DataColumn<Double> = cumProdDoubleImpl(skipNA).cast()

/** @include [CumProdDocs] */
@JvmName("cumProdNullableDouble")
public fun DataColumn<Double?>.cumProd(skipNA: Boolean = true): DataColumn<Double?> = cumProdDoubleImpl(skipNA)
