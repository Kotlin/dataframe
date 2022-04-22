package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.api.splitDefault
import org.jetbrains.kotlinx.dataframe.impl.api.splitImpl
import org.jetbrains.kotlinx.dataframe.impl.api.withRowCellImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.createTypeWithArgument
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public fun <T, C> DataFrame<T>.split(columns: ColumnsSelector<T, C?>): Split<T, C> =
    Split(this, columns)

public fun <T> DataFrame<T>.split(vararg columns: String): Split<T, Any> = split { columns.toColumns() }
public fun <T, C> DataFrame<T>.split(vararg columns: ColumnReference<C?>): Split<T, C> = split { columns.toColumns() }
public fun <T, C> DataFrame<T>.split(vararg columns: KProperty<C?>): Split<T, C> = split { columns.toColumns() }

public data class Split<T, C>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C?>,
) {
    public fun <P> cast(): Split<T, P> = this as Split<T, P>
}

public data class SplitWithTransform<T, C, R>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C?>,
    internal val inward: Boolean,
    internal val tartypeOf: KType,
    internal val default: R? = null,
    internal val transform: DataRow<T>.(C) -> Iterable<R>
)

public typealias ColumnNamesGenerator<C> = ColumnWithPath<C>.(extraColumnIndex: Int) -> String

// region default

public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.default(value: R?): SplitWithTransform<T, C, R> =
    by { it }.default(value)

public fun <T> Split<T, String>.default(value: String?): SplitWithTransform<T, String, String> =
    by { it.splitDefault() }.default(value)

public fun <T, C, R> SplitWithTransform<T, C, R>.default(value: R?): SplitWithTransform<T, C, R> = copy(default = value)

// endregion

// region by

public inline fun <T, C, reified R> Split<T, C>.by(noinline splitter: DataRow<T>.(C) -> Iterable<R>): SplitWithTransform<T, C, R> =
    by(typeOf<R>(), splitter)

public fun <T, C> Split<T, C>.by(
    vararg delimiters: Char,
    trim: Boolean = true,
    ignoreCase: Boolean = false,
    limit: Int = 0
): SplitWithTransform<T, C, String> = by {
    it.toString().split(*delimiters, ignoreCase = ignoreCase, limit = limit).let {
        if (trim) it.map { it.trim() }
        else it
    }
}

public fun <T, C> Split<T, C>.by(
    regex: Regex,
    trim: Boolean = true,
    limit: Int = 0
): SplitWithTransform<T, C, String> = by {
    it.toString().split(regex, limit = limit).let {
        if (trim) it.map { it.trim() }
        else it
    }
}

public fun <T, C> Split<T, C>.by(
    vararg delimiters: String,
    trim: Boolean = true,
    ignoreCase: Boolean = false,
    limit: Int = 0
): SplitWithTransform<T, C, String> = by {
    it.toString().split(*delimiters, ignoreCase = ignoreCase, limit = limit).let {
        if (trim) it.map { it.trim() }
        else it
    }
}

@PublishedApi
internal fun <T, C, R> Split<T, C>.by(
    type: KType,
    splitter: DataRow<T>.(C) -> Iterable<R>
): SplitWithTransform<T, C, R> {
    return SplitWithTransform(df, columns, false, type) {
        if (it == null) emptyList() else splitter(it).asList()
    }
}

// endregion

// region match

public fun <T, C : String?> Split<T, C>.match(regex: String): SplitWithTransform<T, C, String?> = match(regex.toRegex())

public fun <T, C : String?> Split<T, C>.match(regex: Regex): SplitWithTransform<T, C, String?> = by {
    it?.let { regex.matchEntire(it)?.groups?.drop(1)?.map { it?.value } } ?: emptyList<String>()
}

// endregion

internal fun <T, C> Split<T, C>.toDataFrame(): DataFrame<T> = by {
    when (it) {
        is List<*> -> it
        is AnyFrame -> it.rows()
        else -> listOf(it)
    }
}.into()

// region into

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    firstName: ColumnAccessor<*>,
    vararg otherNames: ColumnAccessor<*>
): DataFrame<T> =
    into(listOf(firstName.name()) + otherNames.map { it.name() })

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    firstName: KProperty<*>,
    vararg otherNames: KProperty<*>
): DataFrame<T> =
    into(listOf(firstName.columnName) + otherNames.map { it.columnName })

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> = into(names.toList(), extraNamesGenerator)

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    names: List<String>,
    extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> = splitImpl(this) { numberOfNewCols ->
    if (extraNamesGenerator != null && names.size < numberOfNewCols) {
        names + (1..(numberOfNewCols - names.size)).map { extraNamesGenerator(this, it) }
    } else names
}

public fun <T, C : Iterable<*>> Split<T, C>.into(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> =
    by { it }.into(names.toList(), extraNamesGenerator)

@JvmName("splitDataFrameInto")
public fun <T, C> Split<T, DataFrame<C>>.into(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<DataFrame<C>>? = null
): DataFrame<T> =
    by { it.rows() }.into(names.toList(), extraNamesGenerator)

public fun <T, A, B> Split<T, Pair<A, B>>.into(
    firstCol: String,
    secondCol: String
): DataFrame<T> =
    by { listOf(it.first, it.second) }.into(firstCol, secondCol)

public inline fun <T, reified A, reified B> Split<T, Pair<A, B>>.into(
    firstCol: ColumnAccessor<A>,
    secondCol: ColumnAccessor<B>
): DataFrame<T> =
    by { listOf(it.first, it.second) }.into(firstCol, secondCol)

@JvmName("intoTC")
public fun <T> Split<T, String>.into(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<String>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> =
    by { it.splitDefault() }.into(names.toList(), extraNamesGenerator)

// endregion

// region inward

public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    names: Iterable<String>,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> =
    copy(inward = true).into(names.toList(), extraNamesGenerator)

public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> = inward(names.toList(), extraNamesGenerator)

public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    firstName: ColumnAccessor<*>,
    vararg otherNames: ColumnAccessor<*>
): DataFrame<T> =
    inward(listOf(firstName.name()) + otherNames.map { it.name() })

public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    firstName: KProperty<*>,
    vararg otherNames: KProperty<*>
): DataFrame<T> =
    inward(listOf(firstName.columnName) + otherNames.map { it.columnName })

public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inward(
    vararg names: String,
    noinline extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> =
    by { it }.inward(names.toList(), extraNamesGenerator)

@JvmName("splitDataFrameInward")
public fun <T, C : DataFrame<R>, R> Split<T, C>.inward(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> =
    by { it.rows() }.inward(names.toList(), extraNamesGenerator)

public fun <T, A, B> Split<T, Pair<A, B>>.inward(
    firstCol: String,
    secondCol: String
): DataFrame<T> =
    by { listOf(it.first, it.second) }.inward(firstCol, secondCol)

public inline fun <T, reified A, reified B> Split<T, Pair<A, B>>.inward(
    firstCol: ColumnAccessor<A>,
    secondCol: ColumnAccessor<B>
): DataFrame<T> =
    by { listOf(it.first, it.second) }.inward(firstCol, secondCol)

@JvmName("inwardTC")
public fun <T> Split<T, String>.inward(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<String>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> =
    by { it.splitDefault() }.inward(names.toList(), extraNamesGenerator)

// endregion

// region intoColumns

public fun <T, C : AnyFrame> Split<T, C>.intoColumns(): DataFrame<T> {
    return df.convert(columns).with {
        when {
            it == null -> null
            it.isEmpty() -> DataRow.empty
            else -> it.implode { all() }.single()
        }
    }
}

// endregion

// region intoRows

@JvmName("intoRowsTC")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.intoRows(dropEmpty: Boolean = true): DataFrame<T> =
    by { it }
        .intoRows(dropEmpty)

@JvmName("intoRowsFrame")
public fun <T, C : AnyFrame> Split<T, C>.intoRows(dropEmpty: Boolean = true): DataFrame<T> =
    by { it.rows() }.intoRows(dropEmpty)

internal fun <T, C, R> Convert<T, C?>.splitInplace(tartypeOf: KType, transform: DataRow<T>.(C) -> Iterable<R>) =
    withRowCellImpl(List::class.createTypeWithArgument(tartypeOf)) { if (it == null) emptyList() else transform(it).asList() }

public fun <T, C, R> SplitWithTransform<T, C, R>.intoRows(dropEmpty: Boolean = true): DataFrame<T> {
    val paths = df.getColumnPaths(columns).toColumnSet()
    return df.convert { paths as ColumnSet<C?> }.splitInplace(tartypeOf, transform).explode(dropEmpty) { paths }
}

// endregion

// region inplace

@JvmName("inplaceTC")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inplace(): DataFrame<T> = by { it }.inplace()

public fun <T, C, R> SplitWithTransform<T, C, R>.inplace(): DataFrame<T> = df.convert(columns).splitInplace(tartypeOf, transform)

// endregion
