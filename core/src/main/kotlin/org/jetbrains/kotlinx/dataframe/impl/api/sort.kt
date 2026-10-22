package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.SortColumnsSelector
import org.jetbrains.kotlinx.dataframe.api.asGroupBy
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.castFrameColumn
import org.jetbrains.kotlinx.dataframe.api.getFrameColumn
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.ValueColumnInternal
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.assertIsComparable
import org.jetbrains.kotlinx.dataframe.impl.columns.internalValueColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.missing.MissingColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.resolve
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.nrow

@Suppress("RemoveExplicitTypeArguments")
internal fun <T, G> GroupBy<T, G>.sortByImpl(columns: SortColumnsSelector<G, *>): GroupBy<T, G> {
    if (groups.size() == 0 && groups.schema.value.columns.isNotEmpty()) {
        try {
            // With no groups to sort, resolve the selector against the preserved group schema
            DataFrame.empty(groups.schema.value).cast<G>().getSortColumns(columns, UnresolvedColumnsPolicy.Fail)
        } catch (e: Exception) {
            throw IllegalStateException("Can not sort GroupBy: ${e.message}", e)
        }
    }

    val sorted = try {
        toDataFrame()
            // sort the individual groups by the columns specified
            .update { groups }
            .with { it.sortByImpl(UnresolvedColumnsPolicy.Fail, columns) }
    } catch (e: IllegalStateException) {
        val cause = e.cause ?: e
        throw IllegalStateException("Can not sort GroupBy: ${cause.message}", cause)
    }

    return sorted.asGroupBy { it.getFrameColumn(groups.name()).castFrameColumn<G>() }
}

internal fun <T, C> DataFrame<T>.sortByImpl(
    unresolvedColumnsPolicy: UnresolvedColumnsPolicy = UnresolvedColumnsPolicy.Fail,
    columns: SortColumnsSelector<T, C>,
): DataFrame<T> {
    val sortColumns = getSortColumns(columns, unresolvedColumnsPolicy)
    if (sortColumns.isEmpty()) return this

    val compChain = sortColumns.map {
        when (it.direction) {
            SortDirection.Asc -> it.column.createComparator(it.nullsLast)
            SortDirection.Desc -> it.column.createComparator(it.nullsLast).reversed()
        }
    }.reduce { a, b -> a.then(b) }

    val permutation = (0 until nrow).sortedWith(compChain)

    return this[permutation]
}

@Suppress("UNCHECKED_CAST")
internal fun AnyCol.createComparator(nullsLast: Boolean): java.util.Comparator<Int> {
    assertIsComparable()

    val valueComparator = Comparator<Any?> { left, right ->
        (left as Comparable<Any?>).compareTo(right)
    }

    val comparatorWithNulls = if (nullsLast) nullsLast(valueComparator) else nullsFirst(valueComparator)
    return Comparator { left, right -> comparatorWithNulls.compare(get(left), get(right)) }
}

internal fun <T, C> DataFrame<T>.getSortColumns(
    columns: SortColumnsSelector<T, C>,
    unresolvedColumnsPolicy: UnresolvedColumnsPolicy,
): List<SortColumnDescriptor<*>> =
    columns.toColumnSet().resolve(this, unresolvedColumnsPolicy)
        // can appear using [DataColumn<R>?.check] with UnresolvedColumnsPolicy.Skip
        .filterNot { it.data is MissingColumnGroup<*> }
        .map {
            when (val col = it.data) {
                is SortColumnDescriptor<*> -> col
                is ValueColumn<*> -> SortColumnDescriptor(col)
                else -> throw IllegalStateException("Can not use ${col.kind} as sort column")
            }
        }

internal enum class SortFlag { Reversed, NullsLast }

internal fun <C> ColumnsResolver<C>.addFlag(flag: SortFlag): ColumnSetWithSortFlag<C> =
    ColumnSetWithSortFlag(this, flag)

internal fun <C> ColumnWithPath<C>.addFlag(flag: SortFlag): ColumnWithPath<C> {
    val col = data
    return when (col) {
        is SortColumnDescriptor -> {
            when (flag) {
                SortFlag.Reversed -> SortColumnDescriptor(col.column, col.direction.reversed(), col.nullsLast)
                SortFlag.NullsLast -> SortColumnDescriptor(col.column, col.direction, true)
            }
        }

        is ValueColumn -> {
            when (flag) {
                SortFlag.Reversed -> SortColumnDescriptor(col, SortDirection.Desc)
                SortFlag.NullsLast -> SortColumnDescriptor(col, SortDirection.Asc, true)
            }
        }

        else -> throw IllegalArgumentException("Can not apply sort flag to column kind ${col.kind}")
    }.addPath(path)
}

internal class ColumnSetWithSortFlag<C>(val column: ColumnsResolver<C>, val flag: SortFlag) : ColumnSet<C> {
    override fun resolve(context: ColumnResolutionContext) = column.resolve(context).map { it.addFlag(flag) }
}

internal class SortColumnDescriptor<C>(
    val column: ValueColumn<C>,
    val direction: SortDirection = SortDirection.Asc,
    val nullsLast: Boolean = false,
) : ValueColumnInternal<C> by column.internalValueColumn()

internal enum class SortDirection { Asc, Desc }

internal fun SortDirection.reversed(): SortDirection =
    when (this) {
        SortDirection.Asc -> SortDirection.Desc
        SortDirection.Desc -> SortDirection.Asc
    }
