package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl
public interface ColumnRangeColumnsSelectionDsl {

    /**
     * ## Range of Columns
     * Creates a [ColumnSet] containing all columns from [this\] up to [endInclusive\].
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `{@getArg [CommonRangeOfColumnsDocs.Example]}` }`
     *
     * @param [endInclusive\] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet] containing all columns from [this\] to [endInclusive\].
     * @throws [IllegalArgumentException\] if the columns have different parents.
     */
    private interface CommonRangeOfColumnsDocs {

        /** Examples key */
        interface Example
    }

    /**
     * @include [CommonRangeOfColumnsDocs]
     * {@setArg [CommonRangeOfColumnsDocs.Example] "fromColumn".."toColumn"}
     */
    public operator fun String.rangeTo(endInclusive: String): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * @include [CommonRangeOfColumnsDocs]
     * {@setArg [CommonRangeOfColumnsDocs.Example] "fromColumn"..Type::toColumn}
     */
    public operator fun String.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * @include [CommonRangeOfColumnsDocs]
     * {@setArg [CommonRangeOfColumnsDocs.Example] "fromColumn"..toColumn}
     */
    public operator fun String.rangeTo(endInclusive: AnyColumnReference): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive)

    /**
     * @include [CommonRangeOfColumnsDocs]
     * {@setArg [CommonRangeOfColumnsDocs.Example] Type::fromColumn.."toColumn"}
     */
    public operator fun KProperty<*>.rangeTo(endInclusive: String): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * @include [CommonRangeOfColumnsDocs]
     * {@setArg [CommonRangeOfColumnsDocs.Example] Type::fromColumn..Type::toColumn}
     */
    public operator fun KProperty<*>.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * @include [CommonRangeOfColumnsDocs]
     * {@setArg [CommonRangeOfColumnsDocs.Example] Type::fromColumn..toColumn}
     */
    public operator fun KProperty<*>.rangeTo(endInclusive: AnyColumnReference): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive)

    /**
     * @include [CommonRangeOfColumnsDocs]
     * {@setArg [CommonRangeOfColumnsDocs.Example] fromColumn.."toColumn"}
     */
    public operator fun AnyColumnReference.rangeTo(endInclusive: String): ColumnSet<*> =
        rangeTo(endInclusive.toColumnAccessor())

    /**
     * @include [CommonRangeOfColumnsDocs]
     * {@setArg [CommonRangeOfColumnsDocs.Example] fromColumn..Type::toColumn}
     */
    public operator fun AnyColumnReference.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        rangeTo(endInclusive.toColumnAccessor())

    /**
     * @include [CommonRangeOfColumnsDocs]
     * {@setArg [CommonRangeOfColumnsDocs.Example] fromColumn..toColumn}
     */
    public operator fun AnyColumnReference.rangeTo(endInclusive: AnyColumnReference): ColumnSet<*> =
        object : ColumnSet<Any?> {

            private fun process(col: AnyColumnReference, context: ColumnResolutionContext): List<ColumnWithPath<Any?>> {
                val startPath = col.resolveSingle(context)!!.path
                val endPath = endInclusive.resolveSingle(context)!!.path
                val parentPath = startPath.parent()!!
                require(parentPath == endPath.parent()) { "Start and end columns have different parent column paths" }
                val parentCol = context.df.getColumnGroup(parentPath)
                val startIndex = parentCol.getColumnIndex(startPath.name)
                val endIndex = parentCol.getColumnIndex(endPath.name)
                return (startIndex..endIndex).map {
                    parentCol.getColumn(it).let {
                        it.addPath(parentPath + it.name)
                    }
                }
            }

            override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<Any?>> =
                process(this@rangeTo, context)
        }
}
// endregion
