package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyMany
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Many
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.createTypeWithArgument
import org.jetbrains.kotlinx.dataframe.emptyMany
import org.jetbrains.kotlinx.dataframe.getType
import org.jetbrains.kotlinx.dataframe.impl.ColumnDataCollector
import org.jetbrains.kotlinx.dataframe.impl.api.ColumnToInsert
import org.jetbrains.kotlinx.dataframe.impl.api.insertImpl
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator
import org.jetbrains.kotlinx.dataframe.toColumnAccessor
import org.jetbrains.kotlinx.dataframe.toMany
import kotlin.reflect.KProperty
import kotlin.reflect.KType

public fun <T, C> DataFrame<T>.split(selector: ColumnsSelector<T, C?>): Split<T, C> =
    SplitClause(this, selector)

public fun <T> DataFrame<T>.split(column: String): Split<T, Any> = split { column.toColumnAccessor() }
public fun <T, C> DataFrame<T>.split(column: ColumnReference<C?>): Split<T, C> = split { column }
public fun <T, C> DataFrame<T>.split(column: KProperty<C?>): Split<T, C> = split { column.toColumnAccessor() }

public interface Split<out T, out C> {

    public fun by(vararg delimiters: String, trim: Boolean = true, ignoreCase: Boolean = false, limit: Int = 0): SplitWithTransform<T, C, String> = with {
        it.toString().split(*delimiters, ignoreCase = ignoreCase, limit = limit).let {
            if (trim) it.map { it.trim() }
            else it
        }
    }
}

public typealias ColumnNamesGenerator<C> = ColumnWithPath<C>.(extraColumnIndex: Int) -> String

public interface SplitWithTransform<out T, out C, in R> {

    public fun intoRows(dropEmpty: Boolean = true): DataFrame<T>

    public fun inplace(): DataFrame<T>

    public fun inward(vararg names: String, extraNamesGenerator: ColumnNamesGenerator<C>? = null): DataFrame<T> = inward(names.toList(), extraNamesGenerator)

    public fun inward(names: Iterable<String>, extraNamesGenerator: ColumnNamesGenerator<C>? = null): DataFrame<T>
}

public class SplitClause<T, C>(
    public val df: DataFrame<T>,
    public val columns: ColumnsSelector<T, C?>
) : Split<T, C>

public inline fun <T, C, reified R> Split<T, C>.with(noinline splitter: (C) -> Iterable<R>): SplitWithTransform<T, C, R> = with(
    getType<R>(), splitter
)

@PublishedApi
internal fun <T, C, R> Split<T, C>.with(type: KType, splitter: (C) -> Iterable<R>): SplitWithTransform<T, C, R> {
    require(this is SplitClause<T, C>)
    return SplitClauseWithTransform(df, columns, false, type) {
        if (it == null) emptyMany() else splitter(it).toMany()
    }
}

public data class SplitClauseWithTransform<T, C, R>(
    val df: DataFrame<T>,
    val columns: ColumnsSelector<T, C?>,
    val inward: Boolean,
    val targetType: KType,
    val transform: (C) -> Iterable<R>
) : SplitWithTransform<T, C, R> {

    override fun intoRows(dropEmpty: Boolean): DataFrame<T> = df.explode(dropEmpty, columns)

    override fun inplace(): DataFrame<T> = df.convert(columns).with(Many::class.createTypeWithArgument(targetType)) { if (it == null) emptyMany() else transform(it).toMany() }

    override fun inward(names: Iterable<String>, extraNamesGenerator: ColumnNamesGenerator<C>?): DataFrame<T> = copy(inward = true).into(names.toList(), extraNamesGenerator)
}

public class FrameSplit<T, C>(
    public val df: DataFrame<T>,
    public val columns: ColumnSelector<T, DataFrame<C>?>
)

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    firstName: ColumnReference<*>,
    vararg otherNames: ColumnReference<*>
): DataFrame<T> =
    into(listOf(firstName.name()) + otherNames.map { it.name() })

public fun <T, C, R> SplitWithTransform<T, C, R>.intoMany(
    namesProvider: (ColumnWithPath<C>, numberOfNewColumns: Int) -> List<String>
): DataFrame<T> =
    doSplitCols(this as SplitClauseWithTransform<T, C, R>, namesProvider)

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> = into(names.toList(), extraNamesGenerator)

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    names: List<String>,
    extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> = intoMany { col, numberOfNewCols ->
    if (extraNamesGenerator != null && names.size < numberOfNewCols) {
        names + (1..(numberOfNewCols - names.size)).map { extraNamesGenerator(col, it) }
    } else names
}

internal fun valueToList(value: Any?, splitStrings: Boolean = true): List<Any?> = when (value) {
    null -> emptyList()
    is AnyMany -> value
    is AnyFrame -> value.rows().toList()
    else -> if (splitStrings) value.toString().split(",").map { it.trim() } else listOf(value)
}

public fun <T, C, R> doSplitCols(
    clause: SplitClauseWithTransform<T, C, R>,
    columnNamesGenerator: ColumnWithPath<C>.(Int) -> List<String>
): DataFrame<T> {
    val nameGenerator = clause.df.nameGenerator()
    val nrow = clause.df.nrow()

    val removeResult = clause.df.removeImpl(clause.columns)

    val toInsert = removeResult.removedColumns.flatMap { node ->

        val column = node.toColumnWithPath<C>(clause.df)
        val columnCollectors = mutableListOf<ColumnDataCollector>()
        for (row in 0 until nrow) {
            val value = clause.transform(column.data[row])
            val list = valueToList(value)
            for (j in list.indices) {
                if (columnCollectors.size <= j) {
                    val collector = createDataCollector(nrow)
                    repeat(row) { collector.add(null) }
                    columnCollectors.add(collector)
                }
                columnCollectors[j].add(list[j])
            }
            for (j in list.size until columnCollectors.size)
                columnCollectors[j].add(null)
        }

        var names = columnNamesGenerator(column, columnCollectors.size)
        if (names.size < columnCollectors.size) {
            names = names + (1..(columnCollectors.size - names.size)).map { "splitted$it" }
        }

        columnCollectors.mapIndexed { i, col ->

            val name = nameGenerator.addUnique(names[i])
            val sourcePath = node.pathFromRoot()
            val path = if (clause.inward) sourcePath + name else sourcePath.dropLast(1) + name
            val data = col.toColumn(name)
            ColumnToInsert(path, data, node)
        }
    }

    return removeResult.df.insertImpl(toInsert)
}

@JvmName("intoRowsTC")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.intoRows(dropEmpty: Boolean = true): DataFrame<T> = with { it }.intoRows(dropEmpty)

@JvmName("intoRowsFrame")
public fun <T> Split<T, AnyFrame>.intoRows(dropEmpty: Boolean = true): DataFrame<T> = with { it.rows() }.intoRows(dropEmpty)

@JvmName("inplaceTC")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inplace(): DataFrame<T> = with { it }.inplace()

public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inward(
    vararg names: String,
    noinline extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> =
    with { it }.inward(names.toList(), extraNamesGenerator)

public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.into(
    vararg names: String,
    noinline extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> =
    with { it }.into(names.toList(), extraNamesGenerator)

@JvmName("intoTC")
public fun <T> Split<T, String>.into(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<String>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> =
    with { it.splitDefault() }.into(names.toList(), extraNamesGenerator)

internal fun String.splitDefault() = split(",").map { it.trim() }
