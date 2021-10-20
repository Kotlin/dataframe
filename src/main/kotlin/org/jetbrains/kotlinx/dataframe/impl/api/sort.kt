package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.GroupedDataFrame
import org.jetbrains.kotlinx.dataframe.api.SortColumnsSelector
import org.jetbrains.kotlinx.dataframe.api.SortReceiver
import org.jetbrains.kotlinx.dataframe.api.asGroupedDataFrame
import org.jetbrains.kotlinx.dataframe.api.frameColumn
import org.jetbrains.kotlinx.dataframe.api.typed
import org.jetbrains.kotlinx.dataframe.api.typedFrames
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.assertIsComparable
import org.jetbrains.kotlinx.dataframe.impl.columns.resolve
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns

internal fun <T, G> GroupedDataFrame<T, G>.sortByImpl(selector: SortColumnsSelector<G, *>): GroupedDataFrame<T, G> {
    return toDataFrame()
        .update { groups }
        .with { it?.sortByImpl(UnresolvedColumnsPolicy.Skip, selector) }
        .sortByImpl(UnresolvedColumnsPolicy.Skip, selector as SortColumnsSelector<T, *>)
        .asGroupedDataFrame { it.frameColumn(groups.name()).typedFrames() }
}

internal fun <T, C> DataFrame<T>.sortByImpl(
    unresolvedColumnsPolicy: UnresolvedColumnsPolicy = UnresolvedColumnsPolicy.Fail,
    selector: SortColumnsSelector<T, C>
): DataFrame<T> {
    val columns = getSortColumns(selector, unresolvedColumnsPolicy)

    val compChain = columns.map {
        when (it.direction) {
            SortDirection.Asc -> it.column.createComparator(it.nullsLast)
            SortDirection.Desc -> it.column.createComparator(it.nullsLast).reversed()
        }
    }.reduce { a, b -> a.then(b) }

    val permutation = (0 until nrow()).sortedWith(compChain)

    return this[permutation]
}

internal fun AnyCol.createComparator(nullsLast: Boolean): java.util.Comparator<Int> {
    assertIsComparable()

    val valueComparator = Comparator<Any?> { left, right ->
        (left as Comparable<Any?>).compareTo(right)
    }

    val comparatorWithNulls = if (nullsLast) nullsLast(valueComparator) else nullsFirst(valueComparator)
    return Comparator { left, right -> comparatorWithNulls.compare(get(left), get(right)) }
}

@JvmName("toColumnSetForSort")
internal fun <T, C> SortColumnsSelector<T, C>.toColumns(): ColumnSet<C> = toColumns {
    class SortReceiverImpl<T>(df: DataFrame<T>, allowMissingColumns: Boolean) :
        DataFrameReceiver<T>(df, allowMissingColumns),
        SortReceiver<T>

    SortReceiverImpl(
        it.df.typed(),
        it.allowMissingColumns
    )
}

internal fun <T, C> DataFrame<T>.getSortColumns(
    selector: SortColumnsSelector<T, C>,
    unresolvedColumnsPolicy: UnresolvedColumnsPolicy
): List<SortColumnDescriptor<*>> {
    return selector.toColumns().resolve(this, unresolvedColumnsPolicy)
        .map {
            when (val col = it.data) {
                is SortColumnDescriptor<*> -> col
                else -> SortColumnDescriptor(col)
            }
        }
}

internal enum class SortFlag { Reversed, NullsLast }

internal fun <C> ColumnSet<C>.addFlag(flag: SortFlag) = ColumnsWithSortFlag(this, flag)

internal fun <C> ColumnWithPath<C>.addFlag(flag: SortFlag): ColumnWithPath<C> {
    val col = data
    return when (col) {
        is SortColumnDescriptor -> {
            when (flag) {
                SortFlag.Reversed -> SortColumnDescriptor(col.column, col.direction.reversed(), col.nullsLast)
                SortFlag.NullsLast -> SortColumnDescriptor(col.column, col.direction, true)
            }
        }
        else -> {
            when (flag) {
                SortFlag.Reversed -> SortColumnDescriptor(col, SortDirection.Desc)
                SortFlag.NullsLast -> SortColumnDescriptor(col, SortDirection.Asc, true)
            }
        }
    }.addPath(path, df)
}

internal class ColumnsWithSortFlag<C>(val column: ColumnSet<C>, val flag: SortFlag) : ColumnSet<C> {
    override fun resolve(context: ColumnResolutionContext) = column.resolve(context).map { it.addFlag(flag) }
}

internal class SortColumnDescriptor<C>(
    val column: DataColumn<C>,
    val direction: SortDirection = SortDirection.Asc,
    val nullsLast: Boolean = false
) : DataColumn<C> by column

internal enum class SortDirection { Asc, Desc }

internal fun SortDirection.reversed(): SortDirection = when (this) {
    SortDirection.Asc -> SortDirection.Desc
    SortDirection.Desc -> SortDirection.Asc
}
