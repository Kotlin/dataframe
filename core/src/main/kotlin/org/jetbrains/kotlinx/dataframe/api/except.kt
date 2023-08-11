package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExceptKeepingStructure
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl
public interface ExceptColumnsSelectionDsl<out T> {
    // region except

    /** TODO tbd */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.colsExcept(predicate: ColumnFilter<C>): TransformableColumnSet<C> =
        colsInternal { !predicate(it as ColumnWithPath<C>) } as TransformableColumnSet<C>

    /** TODO tbd */
    public fun SingleColumn<DataRow<*>>.colsExcept(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
        colsInternal { !predicate(it) }

    // TODO Same as select and cols but then inverted

    // region ColumnsSelector

    public infix fun <C> ColumnSet<C>.except(selector: ColumnsSelector<T, *>): ColumnSet<C> =
        except(selector.toColumns()) as ColumnSet<C>

    // TODO TBD
    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(selector)"), DeprecationLevel.WARNING)
    public infix fun <C> SingleColumn<DataRow<C>>.except(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allExcept(selector)

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(selector)"), DeprecationLevel.WARNING)
    public infix fun <C> ColumnsSelectionDsl<C>.except(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allExcept(selector)

    public infix fun <C> SingleColumn<DataRow<C>>.exceptNew(selector: ColumnsSelector<C, *>): SingleColumn<DataRow<*>> =
        this.ensureIsColumnGroup().transformSingle { singleCol ->

            val columnsToExcept = singleCol.asColumnGroup().getColumnsWithPaths(selector)
                .map { it.changePath(singleCol.path + it.path) }

            val newCols = listOf(singleCol).allColumnsExceptKeepingStructure(columnsToExcept)

            newCols as List<ColumnWithPath<DataRow<*>>>
        }.singleInternal()

    public fun <C> SingleColumn<DataRow<C>>.allExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        createColumnSet { context ->
            this.ensureIsColumnGroup().resolveSingle(context)?.let { col ->
                require(col.isColumnGroup()) {
                    "Column ${col.path} is not a ColumnGroup and can thus not be excepted from."
                }

                val allCols = col.asColumnGroup()
                    .getColumnsWithPaths { all() }

                val columnsToExcept = col.asColumnGroup()
                    .getColumnsWithPaths(selector as ColumnsSelector<*, *>)

                allCols.allColumnsExceptKeepingStructure(columnsToExcept)
                    .map { it.changePath(col.path + it.path) }
            } ?: emptyList()
        }

    public fun <C> ColumnsSelectionDsl<C>.allExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        this.asSingleColumn().allExcept(selector)

    public fun String.allExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allExcept(selector)

    public fun <C> KProperty<DataRow<C>>.allExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allExcept(selector)

    public fun ColumnPath.allExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allExcept(selector)

    /** TODO tbd */
    public operator fun <C> SingleColumn<DataRow<C>>.minus(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allExcept(selector)

    // endregion

    // region ColumnsResolver

    public infix fun <C> ColumnSet<C>.except(other: ColumnsResolver<*>): ColumnSet<C> =
        createColumnSet { context ->
            this@except
                .resolve(context)
                .allColumnsExceptKeepingStructure(other.resolve(context))
        } as ColumnSet<C>

    public fun <C> ColumnSet<C>.except(vararg other: ColumnsResolver<*>): ColumnSet<C> =
        except(other.toColumnSet())

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(other)"))
    public fun SingleColumn<DataRow<*>>.except(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(*other)

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(other)"))
    public fun ColumnsSelectionDsl<*>.except(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(*other)

    public infix fun SingleColumn<DataRow<*>>.allExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().allColumnsInternal().except(other)

    public fun SingleColumn<DataRow<*>>.allExcept(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(other.toColumnSet())

    public infix fun ColumnsSelectionDsl<*>.allExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        this.asSingleColumn().allExcept(other)

    public fun ColumnsSelectionDsl<*>.allExcept(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(other.toColumnSet())

    /** TODO tbd */
    public operator fun SingleColumn<DataRow<*>>.minus(other: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(other)

    public infix fun String.allExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        columnGroup(this).allExcept(other)

    public fun String.allExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun KProperty<DataRow<*>>.allExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        columnGroup(this).allExcept(other)

    public fun KProperty<DataRow<*>>.allExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun ColumnPath.allExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        columnGroup(this).allExcept(other)

    public fun ColumnPath.allExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    // endregion

    // region String

    public infix fun <C> ColumnSet<C>.except(other: String): ColumnSet<C> =
        except(column<Any?>(other))

    public fun <C> ColumnSet<C>.except(vararg others: String): ColumnSet<C> =
        except(others.toColumnSet())

    public infix fun SingleColumn<DataRow<*>>.allExcept(other: String): ColumnSet<*> =
        allExcept(column<Any?>(other))

    public fun SingleColumn<DataRow<*>>.allExcept(vararg others: String): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public fun String.allExcept(other: String): ColumnSet<*> =
        columnGroup(this).allExcept(other)

    public fun String.allExcept(vararg others: String): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun KProperty<DataRow<*>>.allExcept(other: String): ColumnSet<*> =
        columnGroup(this).allExcept(other)

    public fun KProperty<DataRow<*>>.allExcept(vararg others: String): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun ColumnPath.allExcept(other: String): ColumnSet<*> =
        columnGroup(this).allExcept(other)

    public fun ColumnPath.allExcept(vararg others: String): ColumnSet<*> =
        allExcept(others.toColumnSet())

    // endregion

    // region KProperty

    public infix fun <C> ColumnSet<C>.except(other: KProperty<C>): ColumnSet<C> =
        except(column(other))

    public fun <C> ColumnSet<C>.except(vararg others: KProperty<C>): ColumnSet<C> =
        except(others.toColumnSet())

    public infix fun SingleColumn<DataRow<*>>.allExcept(other: KProperty<*>): ColumnSet<*> =
        allExcept(column(other))

    public fun SingleColumn<DataRow<*>>.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public fun String.allExcept(other: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allExcept(other)

    public fun String.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun KProperty<DataRow<*>>.allExcept(other: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allExcept(other)

    public fun KProperty<DataRow<*>>.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun ColumnPath.allExcept(other: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allExcept(other)

    public fun ColumnPath.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    // endregion

    // region ColumnPath

    public infix fun <C> ColumnSet<C>.except(other: ColumnPath): ColumnSet<C> =
        except(column<Any?>(other))

    public fun <C> ColumnSet<C>.except(vararg others: ColumnPath): ColumnSet<C> =
        except(others.toColumnSet())

    public infix fun SingleColumn<DataRow<*>>.allExcept(other: ColumnPath): ColumnSet<*> =
        allExcept(column<Any?>(other))

    public fun SingleColumn<DataRow<*>>.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public fun String.allExcept(other: ColumnPath): ColumnSet<*> =
        columnGroup(this).allExcept(other)

    public fun String.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun KProperty<DataRow<*>>.allExcept(other: ColumnPath): ColumnSet<*> =
        columnGroup(this).allExcept(other)

    public fun KProperty<DataRow<*>>.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun ColumnPath.allExcept(other: ColumnPath): ColumnSet<*> =
        columnGroup(this).allExcept(other)

    public fun ColumnPath.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        allExcept(others.toColumnSet())

    // endregion

    // endregion
}
// endregion
