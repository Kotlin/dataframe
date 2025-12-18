package org.jetbrains.kotlinx.dataframe.api

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.splitDefault
import org.jetbrains.kotlinx.dataframe.impl.api.splitImpl
import org.jetbrains.kotlinx.dataframe.impl.api.withRowCellImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.getListType
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.SPLIT_STR
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Interpretable("Split0")
public fun <T, C> DataFrame<T>.split(columns: ColumnsSelector<T, C?>): Split<T, C> = Split(this, columns)

public fun <T> DataFrame<T>.split(vararg columns: String): Split<T, Any> = split { columns.toColumnSet() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C> DataFrame<T>.split(vararg columns: ColumnReference<C?>): Split<T, C> = split { columns.toColumnSet() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C> DataFrame<T>.split(vararg columns: KProperty<C?>): Split<T, C> = split { columns.toColumnSet() }

public class Split<T, C>(
    @PublishedApi
    internal val df: DataFrame<T>,
    @PublishedApi
    internal val columns: ColumnsSelector<T, C?>,
) {
    public fun <P> cast(): Split<T, P> = this as Split<T, P>

    override fun toString(): String = "Split(df=$df, columns=$columns)"
}

public data class SplitWithTransform<T, C, R>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C?>,
    internal val inward: Boolean,
    internal val tartypeOf: KType,
    internal val default: R? = null,
    internal val transform: DataRow<T>.(C) -> Iterable<R>,
)

public typealias ColumnNamesGenerator<C> = ColumnWithPath<C>.(extraColumnIndex: Int) -> String

// region default

@Interpretable("SplitDefault")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.default(value: R?): SplitWithTransform<T, C, R> =
    by { it }.default(value)

@Deprecated(SPLIT_STR, ReplaceWith("""by(",").default(value)"""))
public fun <T> Split<T, String>.default(value: String?): SplitWithTransform<T, String, String> =
    by { it.splitDefault() }.default(value)

@Interpretable("SplitWithTransformDefault")
public fun <T, C, R> SplitWithTransform<T, C, R>.default(value: R?): SplitWithTransform<T, C, R> = copy(default = value)

// endregion

// region by

@Interpretable("ByIterable")
public inline fun <T, C, reified R> Split<T, C>.by(
    noinline splitter: DataRow<T>.(C) -> Iterable<R>,
): SplitWithTransform<T, C, R> = by(typeOf<R>(), splitter)

@Interpretable("ByCharDelimiters")
public fun <T, C> Split<T, C>.by(
    vararg delimiters: Char,
    trim: Boolean = true,
    ignoreCase: Boolean = false,
    limit: Int = 0,
): SplitWithTransform<T, C, String> =
    by {
        it.toString().split(*delimiters, ignoreCase = ignoreCase, limit = limit).let {
            if (trim) it.map { it.trim() } else it
        }
    }

/**
 * Example:
 * ```
 * dataFrameOf("str" to listOf("1    2 3     4"))
 *   .split("str").by("\s+".toRegex())
 *   // when the list of explicitly specified columnNames is not long enough (or none at all),
 *   // names for additional columns are generates
 *   .into()
 * ```
 * Result:
 * ```
 *    split1 split2 split3 split4
 *         1      2      3      4
 * ```
 */
@Interpretable("ByRegex")
public fun <T, C> Split<T, C>.by(
    regex: Regex,
    trim: Boolean = true,
    limit: Int = 0,
): SplitWithTransform<T, C, String> =
    by {
        it.toString().split(regex, limit = limit).let {
            if (trim) it.map { it.trim() } else it
        }
    }

@Interpretable("ByStringDelimiters")
public fun <T, C> Split<T, C>.by(
    vararg delimiters: String,
    trim: Boolean = true,
    ignoreCase: Boolean = false,
    limit: Int = 0,
): SplitWithTransform<T, C, String> =
    by {
        it.toString().split(*delimiters, ignoreCase = ignoreCase, limit = limit).let {
            if (trim) it.map { it.trim() } else it
        }
    }

@PublishedApi
internal inline fun <T, C, R> Split<T, C>.by(
    type: KType,
    crossinline splitter: DataRow<T>.(C) -> Iterable<R>,
): SplitWithTransform<T, C, R> =
    SplitWithTransform(df, columns, false, type) {
        if (it == null) emptyList() else splitter(it).asList()
    }

// endregion

// region match

/**
 * Creates new String columns according to MatchResult [capturing groups](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/-match-result/group-values.html),
 * excluding the first group which is entire matched String.
 * Example:
 * ```
 * dataFrameOf("str" to listOf("100 ml", "1 L"))
 *      .split { "str"<String>() }.match("(\d+)\s*(ml|l|L)").into("volume", "unit")
 * ```
 * Created columns will be nullable if [regex] doesn't match some rows or there are nulls in original column
 * Check [Split.by] overload with regex parameter if you're looking to split String value by [Regex] delimiter
 */
@Interpretable("MatchStringRegex")
public fun <T, C : String?> Split<T, C>.match(
    @Language("RegExp") regex: String,
): SplitWithTransform<T, C, String?> = match(regex.toRegex())

/**
 * Creates new String columns according to MatchResult [capturing groups](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/-match-result/group-values.html),
 * excluding the first group which is entire matched String.
 * Example:
 * ```
 * dataFrameOf("str" to listOf("100 ml", "1 L"))
 *      .split { "str"<String>() }.match("(\d+)\s*(ml|l|L)").into("volume", "unit")
 * ```
 * Created columns will be nullable if [regex] doesn't match some rows or there are nulls in original column
 * Check [Split.by][org.jetbrains.kotlinx.dataframe.api.Split.by] overload with regex parameter if you're looking to split String value by [Regex] delimiter
 */
@Interpretable("MatchRegex")
public fun <T, C : String?> Split<T, C>.match(regex: Regex): SplitWithTransform<T, C, String?> =
    by {
        it?.let {
            regex.matchEntire(it)
                ?.groups
                ?.drop(1)
                ?.map { it?.value }
        } ?: emptyList<String>()
    }

// endregion

internal fun <T, C> Split<T, C>.toDataFrame(): DataFrame<T> =
    by {
        when (it) {
            is List<*> -> it
            is AnyFrame -> it.rows()
            else -> listOf(it)
        }
    }.into()

// region into

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    firstName: ColumnAccessor<*>,
    vararg otherNames: ColumnAccessor<*>,
): DataFrame<T> = into(listOf(firstName.name()) + otherNames.map { it.name() })

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    firstName: KProperty<*>,
    vararg otherNames: KProperty<*>,
): DataFrame<T> = into(listOf(firstName.columnName) + otherNames.map { it.columnName })

@[Refine Interpretable("SplitWithTransformInto0")]
public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null,
): DataFrame<T> = into(names.toList(), extraNamesGenerator)

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    names: List<String>,
    extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null,
): DataFrame<T> =
    splitImpl(this) { numberOfNewCols ->
        if (extraNamesGenerator != null && names.size < numberOfNewCols) {
            names + (1..(numberOfNewCols - names.size)).map { extraNamesGenerator(this, it) }
        } else {
            names
        }
    }

@[Refine Interpretable("SplitIterableInto")]
public fun <T, C : Iterable<*>> Split<T, C>.into(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> = by { it }.into(names.toList(), extraNamesGenerator)

@JvmName("splitDataFrameInto")
public fun <T, C> Split<T, DataFrame<C>>.into(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<DataFrame<C>>? = null,
): DataFrame<T> = by { it.rows() }.into(names.toList(), extraNamesGenerator)

@[Refine Interpretable("SplitPair")]
public fun <T, A, B> Split<T, Pair<A, B>>.into(firstCol: String, secondCol: String): DataFrame<T> =
    by { listOf(it.first, it.second) }.into(firstCol, secondCol)

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public inline fun <T, reified A, reified B> Split<T, Pair<A, B>>.into(
    firstCol: ColumnAccessor<A>,
    secondCol: ColumnAccessor<B>,
): DataFrame<T> = by { listOf(it.first, it.second) }.into(firstCol, secondCol)

@Deprecated(SPLIT_STR, ReplaceWith("""by(",").into(*names, extraNamesGenerator = extraNamesGenerator)"""))
@JvmName("intoTC")
public fun <T> Split<T, String>.into(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<String>.(extraColumnIndex: Int) -> String)? = null,
): DataFrame<T> = by { it.splitDefault() }.into(names.toList(), extraNamesGenerator)

// endregion

// region inward

public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    names: Iterable<String>,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> = copy(inward = true).into(names.toList(), extraNamesGenerator)

@[Refine Interpretable("SplitWithTransformInward0")]
public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> = inward(names.toList(), extraNamesGenerator)

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    firstName: ColumnAccessor<*>,
    vararg otherNames: ColumnAccessor<*>,
): DataFrame<T> = inward(listOf(firstName.name()) + otherNames.map { it.name() })

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    firstName: KProperty<*>,
    vararg otherNames: KProperty<*>,
): DataFrame<T> = inward(listOf(firstName.columnName) + otherNames.map { it.columnName })

@[Refine Interpretable("SplitIterableInward")]
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inward(
    vararg names: String,
    noinline extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> = by { it }.inward(names.toList(), extraNamesGenerator)

@JvmName("splitDataFrameInward")
public fun <T, C : DataFrame<R>, R> Split<T, C>.inward(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null,
): DataFrame<T> = by { it.rows() }.inward(names.toList(), extraNamesGenerator)

@[Refine Interpretable("SplitPairInward")]
public fun <T, A, B> Split<T, Pair<A, B>>.inward(firstCol: String, secondCol: String): DataFrame<T> =
    by { listOf(it.first, it.second) }.inward(firstCol, secondCol)

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public inline fun <T, reified A, reified B> Split<T, Pair<A, B>>.inward(
    firstCol: ColumnAccessor<A>,
    secondCol: ColumnAccessor<B>,
): DataFrame<T> = by { listOf(it.first, it.second) }.inward(firstCol, secondCol)

@Deprecated(SPLIT_STR, ReplaceWith("""by(",").inward(*names, extraNamesGenerator = extraNamesGenerator)"""))
@JvmName("inwardTC")
public fun <T> Split<T, String>.inward(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<String>.(extraColumnIndex: Int) -> String)? = null,
): DataFrame<T> = by { it.splitDefault() }.inward(names.toList(), extraNamesGenerator)

// endregion

// region intoColumns

@[Refine Interpretable("SplitAnyFrameIntoColumns")]
public fun <T, C : AnyFrame> Split<T, C>.intoColumns(): DataFrame<T> =
    df.convert(columns).with {
        when {
            it == null -> null
            it.isEmpty() -> DataRow.empty
            else -> it.implode { all() }.single()
        }
    }

// endregion

// region intoRows

@JvmName("intoRowsTC")
@[Refine Interpretable("SplitIntoRows")]
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.intoRows(dropEmpty: Boolean = true): DataFrame<T> =
    by { it }
        .intoRows(dropEmpty)

@JvmName("intoRowsFrame")
@[Refine Interpretable("SplitAnyFrameRows")]
public fun <T, C : AnyFrame> Split<T, C>.intoRows(dropEmpty: Boolean = true): DataFrame<T> =
    by { it.rows() }.intoRows(dropEmpty)

internal inline fun <T, C, R> Convert<T, C?>.splitInplace(
    type: KType,
    crossinline transform: DataRow<T>.(C) -> Iterable<R>,
) = withRowCellImpl(getListType(type), Infer.None) { if (it == null) emptyList() else transform(it).asList() }

@[Refine Interpretable("SplitWithTransformIntoRows")]
public fun <T, C, R> SplitWithTransform<T, C, R>.intoRows(dropEmpty: Boolean = true): DataFrame<T> {
    val paths = df.getColumnPaths(columns).toColumnSet()
    return df.convert { paths as ColumnSet<C?> }.splitInplace(tartypeOf, transform).explode(dropEmpty) { paths }
}

// endregion

// region inplace

@JvmName("inplaceTC")
@[Refine Interpretable("SplitInplace")]
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inplace(): DataFrame<T> = by { it }.inplace()

@[Refine Interpretable("SplitWithTransformInplace")]
public fun <T, C, R> SplitWithTransform<T, C, R>.inplace(): DataFrame<T> =
    df.convert(columns).splitInplace(tartypeOf, transform)

// endregion

// region DataColumn

public fun DataColumn<Iterable<*>>.splitInto(vararg names: String): AnyFrame =
    toDataFrame().split { this@splitInto }.into(*names)

// endregion
