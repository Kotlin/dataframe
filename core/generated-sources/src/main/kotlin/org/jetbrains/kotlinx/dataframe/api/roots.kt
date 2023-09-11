package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.roots
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl
public interface RootsColumnsSelectionDsl<out T> : ColumnsSelectionDslExtension<T> {

    @Deprecated("Use roots() instead", ReplaceWith("roots()"))
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
     *
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.roots(): ColumnSet<C> = rootsInternal(scope) as ColumnSet<C>

    /** ## Roots
     *
     * Returns a sub-set of columns that are roots of the trees of columns.
     *
     * In practice, this means that if a column in [this] is a child of another column in [this],
     * it will not be included in the result.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a single [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] it will run on the children of that group,
     * else it simply runs on the columns in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     */
    public fun SingleColumn<DataRow<*>>.roots(): ColumnSet<*> = this.ensureIsColumnGroup().rootsInternal(scope)

    /** todo */
    public fun ColumnsSelectionDsl<*>.roots(): ColumnSet<*> = asSingleColumn().roots()

    /** ## Roots
     *
     * Returns a sub-set of columns that are roots of the trees of columns.
     *
     * In practice, this means that if a column in [this] is a child of another column in [this],
     * it will not be included in the result.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a single [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] it will run on the children of that group,
     * else it simply runs on the columns in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     */
    public fun String.roots(): ColumnSet<*> = columnGroup(this).roots()

    /** ## Roots
     *
     * Returns a sub-set of columns that are roots of the trees of columns.
     *
     * In practice, this means that if a column in [this] is a child of another column in [this],
     * it will not be included in the result.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a single [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] it will run on the children of that group,
     * else it simply runs on the columns in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     */
    public fun KProperty<DataRow<*>>.roots(): ColumnSet<*> = columnGroup(this).roots()

    /** ## Roots
     *
     * Returns a sub-set of columns that are roots of the trees of columns.
     *
     * In practice, this means that if a column in [this] is a child of another column in [this],
     * it will not be included in the result.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a single [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] it will run on the children of that group,
     * else it simply runs on the columns in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     */
    public fun ColumnPath.roots(): ColumnSet<*> = columnGroup(this).roots()
}

internal fun ColumnsResolver<*>.rootsInternal(scope: Scope?): ColumnSet<*> =
    allColumnsInternal(scope).transform { it.roots() }

// endregion
