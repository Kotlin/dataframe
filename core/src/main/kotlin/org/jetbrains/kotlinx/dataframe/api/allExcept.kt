package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.addParentPath
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExceptKeepingStructure
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.impl.getColumnsWithPaths
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

public interface AllExceptColumnsSelectionDsl<out T> {

    // region except

//    /** TODO tbd */
//    @Suppress("UNCHECKED_CAST")
//    public fun <C> ColumnSet<C>.colsExcept(predicate: ColumnFilter<C>): TransformableColumnSet<C> =
//        colsInternal { !predicate(it as ColumnWithPath<C>) } as TransformableColumnSet<C>
//
//    /** TODO tbd */
//    public fun SingleColumn<DataRow<*>>.colsExcept(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
//        colsInternal { !predicate(it) }

    // TODO Same as select and cols but then inverted

    // region deprecated and experiments

    // TODO TBD
    @Deprecated("Use allExcept instead", ReplaceWith("this.allColsExcept(selector)"), DeprecationLevel.WARNING)
    public infix fun <C> SingleColumn<DataRow<C>>.except(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allColsExcept(selector)

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

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(other)"))
    public fun SingleColumn<DataRow<*>>.except(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept(*other)

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(other)"))
    public fun ColumnsSelectionDsl<*>.except(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(*other)

    /** TODO tbd */
    public operator fun SingleColumn<DataRow<*>>.minus(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept(other)

    /** TODO tbd */
    public operator fun <C> SingleColumn<DataRow<C>>.minus(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allColsExcept(selector)

    // endregion

    // region ColumnSet

    public infix fun <C> ColumnSet<C>.except(selector: ColumnsSelector<*, *>): ColumnSet<C> =
        except(selector.toColumns<Any?, _>())

    public infix fun <C> ColumnSet<C>.except(other: ColumnsResolver<*>): ColumnSet<C> =
        createColumnSet { context ->
            val resolvedCols = this@except.resolve(context)
            val resolvedColsToExcept = other.resolve(context)
            resolvedCols.allColumnsExceptKeepingStructure(resolvedColsToExcept)
        } as ColumnSet<C>

    public fun <C> ColumnSet<C>.except(vararg other: ColumnsResolver<*>): ColumnSet<C> =
        except(other.toColumnSet())

    public infix fun <C> ColumnSet<C>.except(other: String): ColumnSet<C> =
        except(column<Any?>(other))

    public fun <C> ColumnSet<C>.except(vararg others: String): ColumnSet<C> =
        except(others.toColumnSet())

    public infix fun <C> ColumnSet<C>.except(other: KProperty<C>): ColumnSet<C> =
        except(column(other))

    public fun <C> ColumnSet<C>.except(vararg others: KProperty<C>): ColumnSet<C> =
        except(others.toColumnSet())

    public infix fun <C> ColumnSet<C>.except(other: ColumnPath): ColumnSet<C> =
        except(column<Any?>(other))

    public fun <C> ColumnSet<C>.except(vararg others: ColumnPath): ColumnSet<C> =
        except(others.toColumnSet())

    // endregion

    // region ColumnsSelectionDsl

    public fun <C> ColumnsSelectionDsl<C>.allExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        this.asSingleColumn().allColsExcept(selector)

    public fun ColumnsSelectionDsl<*>.allExcept(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        asSingleColumn().allColsExcept(other.toColumnSet())

    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: String): ColumnSet<*> =
        asSingleColumn().allColsExcept(others.toColumnSet())

    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        asSingleColumn().allColsExcept(others.toColumnSet())

    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsExcept(others.toColumnSet())

    // endregion

    // region SingleColumn

    public infix fun <C> SingleColumn<DataRow<C>>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allColsExceptInternal(selector.toColumns(), false)

    public infix fun SingleColumn<DataRow<*>>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExceptInternal(other, true)

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept(other.toColumnSet())

    public infix fun SingleColumn<DataRow<*>>.allColsExcept(other: String): ColumnSet<*> =
        allColsExcept(column<Any?>(other))

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun SingleColumn<DataRow<*>>.allColsExcept(other: KProperty<*>): ColumnSet<*> =
        allColsExcept(column(other))

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun SingleColumn<DataRow<*>>.allColsExcept(other: ColumnPath): ColumnSet<*> =
        allColsExcept(column<Any?>(other))

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    // endregion

    // region String

    public fun String.allColsExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    public infix fun String.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun String.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public fun String.allColsExcept(other: String): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun String.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public fun String.allColsExcept(other: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun String.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public fun String.allColsExcept(other: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun String.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    // endregion

    // region KProperty

    public fun <C> KProperty<DataRow<C>>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    public infix fun KProperty<*>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun KProperty<*>.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun KProperty<*>.allColsExcept(other: String): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun KProperty<*>.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun KProperty<*>.allColsExcept(other: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun KProperty<*>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun KProperty<*>.allColsExcept(other: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun KProperty<*>.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    // endregion

    // region ColumnPath

    public fun ColumnPath.allColsExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    public infix fun ColumnPath.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun ColumnPath.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun ColumnPath.allColsExcept(other: String): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun ColumnPath.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun ColumnPath.allColsExcept(other: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun ColumnPath.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public infix fun ColumnPath.allColsExcept(other: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExcept(other)

    public fun ColumnPath.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    // endregion

    // endregion

    private fun SingleColumn<DataRow<*>>.allColsExceptInternal(other: ColumnsResolver<*>, allowFullPaths: Boolean) =
        createColumnSet { context ->
            this.ensureIsColumnGroup().resolveSingle(context)?.let { col ->
                require(col.isColumnGroup()) {
                    "Column ${col.path} is not a ColumnGroup and can thus not be excepted from."
                }

                val allCols = col.asColumnGroup()
                    .getColumnsWithPaths { all() }

                // try to resolve all columns to except relative to the current column
                try {
                    val columnsToExcept = col.asColumnGroup()
                        .getColumnsWithPaths(context.unresolvedColumnsPolicy) { other }

                    allCols.allColumnsExceptKeepingStructure(columnsToExcept)
                        .map { it.changePath(col.path + it.path) }
                } catch (e: IllegalStateException) {

                    // if allowed, attempt to resole all columns to except absolutely too if relative failed
                    if (allowFullPaths) {
                        val allColsAbsolute = allCols.map { it.addParentPath(col.path) }

                        val columnsToExcept =
                            (this@AllExceptColumnsSelectionDsl as ColumnsSelectionDsl<T>) { other }
                                .resolve(context)

                        allColsAbsolute.allColumnsExceptKeepingStructure(columnsToExcept)
                    } else {
                        throw e
                    }
                }
            } ?: emptyList()
        }
}


// endregion
