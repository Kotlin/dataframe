package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.roots
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.util.TOP_MESSAGE
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

public interface RootsColumnsSelectionDsl {

    @Deprecated(TOP_MESSAGE, ReplaceWith("roots()"), DeprecationLevel.ERROR)
    public fun <C> ColumnSet<C>.top(): ColumnSet<C> = roots()

    /**
     * ## Roots
     *
     * Returns a sub-set of columns that are roots of the trees of columns.
     *
     * In practice, this means that if a column in [this] is a child of another column in [this],
     * it will not be included in the result.
     *
     * If [this] is a [SingleColumn] containing a single [ColumnGroup] it will run on the children of that group,
     * else it simply runs on the columns in the [ColumnSet] itself.
     *
     * {@comment TODO add helpful examples}
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.roots(): ColumnSet<C> = rootsInternal() as ColumnSet<C>

    /** @include [ColumnSet.roots] */
    public fun SingleColumn<DataRow<*>>.roots(): ColumnSet<*> = this.ensureIsColumnGroup().rootsInternal()

    /** todo */
    public fun ColumnsSelectionDsl<*>.roots(): ColumnSet<*> = asSingleColumn().roots()

    /** @include [ColumnSet.roots] */
    public fun String.roots(): ColumnSet<*> = columnGroup(this).roots()

    /** @include [ColumnSet.roots] */
    public fun KProperty<DataRow<*>>.roots(): ColumnSet<*> = columnGroup(this).roots()

    /** @include [ColumnSet.roots] */
    public fun ColumnPath.roots(): ColumnSet<*> = columnGroup(this).roots()
}

internal fun ColumnsResolver<*>.rootsInternal(): ColumnSet<*> =
    allColumnsInternal().transform { it.roots() }

// endregion
