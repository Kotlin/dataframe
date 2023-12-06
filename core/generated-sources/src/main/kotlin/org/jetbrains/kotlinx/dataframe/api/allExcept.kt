package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExceptAndUnpack
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExceptKeepingStructure
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.isMissingColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.impl.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.io.renderToString
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

public interface AllExceptColumnsSelectionDsl<out T> {

    // region except

    /**
     * ## All (Except) Usage
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `columnSet: `[ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[String][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * `| `[KProperty][kotlin.reflect.KProperty]`<*>` | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnSet][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSetDef]
     *
     *  
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a column group reference:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnGroup][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnGroupDef]
     *
     *  
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    public interface Usage

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

    public operator fun ColumnReference<*>.not(): ColumnSet<Any?> =
        with(this@AllExceptColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
            allExcept(this@not)
        }

    public operator fun ColumnSet<*>.not(): ColumnSet<Any?> =
        with(this@AllExceptColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
            allExcept(this@not)
        }

    public infix fun <C> ColumnSet<C>.oldExcept(other: ColumnsResolver<*>): ColumnSet<C> =
        createColumnSet { context ->
            val resolvedCols = this@oldExcept.resolve(context)
            val resolvedColsToExcept = other.resolve(context)
            resolvedCols.allColumnsExceptAndUnpack(resolvedColsToExcept)
        } as ColumnSet<C>

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

    public infix fun <C> ColumnSet<C>.except(selector: () -> ColumnsResolver<*>): ColumnSet<C> =
//        except(selector.toColumns<Any?, _>())
        except(selector())

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

    public fun <C> SingleColumn<DataRow<C>>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allColsExceptInternal(selector.toColumns())

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExceptInternal(other.toColumnSet())

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    // reference and path

    // endregion

    // region String

    public fun String.allColsExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    public fun String.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(others.toColumnSet())

    public fun String.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public fun String.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    // endregion

    // region KProperty

    public fun <C> KProperty<DataRow<C>>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    public fun KProperty<*>.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(others.toColumnSet())

    public fun KProperty<*>.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public fun KProperty<*>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    // endregion

    // region ColumnPath

    public fun ColumnPath.allColsExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    public fun ColumnPath.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        columnGroup(this).allColsExcept(others.toColumnSet())

    public fun ColumnPath.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    public fun ColumnPath.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExcept(others.toColumnSet())

    // endregion

    // endregion

    private fun SingleColumn<DataRow<*>>.allColsExceptInternal(other: ColumnsResolver<*>) =
        createColumnSet { context ->
            val col = this.ensureIsColumnGroup().resolveSingle(context)
                ?: return@createColumnSet emptyList()
            val colGroup = col.asColumnGroup()
            val colPath = col.path

            val parentScope = (this@AllExceptColumnsSelectionDsl as ColumnsSelectionDsl<T>)
                .asSingleColumn()
            val parentCol = parentScope.ensureIsColumnGroup().resolveSingle(context)
                ?: return@createColumnSet emptyList()
            val parentColGroup = parentCol.asColumnGroup()

            val allCols = colGroup.getColumnsWithPaths { all() }

            val colsToExceptRelativeToParent = parentColGroup
                .getColumnsWithPaths(UnresolvedColumnsPolicy.Skip) { other }

            val colsToExceptRelativeToCol = colGroup
                .getColumnsWithPaths(UnresolvedColumnsPolicy.Skip) { other }

            // throw exceptions for columns that weren't in this or parent scope
            (colsToExceptRelativeToParent + colsToExceptRelativeToCol)
                .groupBy { it.path }
                .forEach { (path, cols) ->
                    if (cols.all { it.data.isMissingColumn() }) {
                        val columnTitles = parentColGroup.renderToString(0, 0, columnTypes = true).trim(' ', '\n', '.')
                        throw IllegalArgumentException(
                            "Column ${(colPath + path).joinToString()} and ${path.joinToString()} not found among columns: $columnTitles."
                        )
                    }
                }

            val colsToExcept = colsToExceptRelativeToCol +
                colsToExceptRelativeToParent.mapNotNull { // adjust the path to be relative to the current column
                    for (i in colPath.indices) {
                        if (colPath[i] != it.path.getOrNull(i)) {
                           return@mapNotNull null
                        }
                    }
                    val newPath = it.path.dropFirst(colPath.size)
                    if (newPath.isEmpty()) null else it.changePath(newPath)
                }

            allCols.allColumnsExceptKeepingStructure(
                colsToExcept
                    .distinctBy { it.path }
                    .filterNot { it.data.isMissingColumn() }
            ).map { it.changePath(col.path + it.path) }

            // try to resolve all columns to except relative to the current column
//                try {
//                    val columnsToExcept = colGroup
//                        .getColumnsWithPaths(context.unresolvedColumnsPolicy) { other }
//
//                    allCols.allColumnsExceptKeepingStructure(columnsToExcept)
//                        .map { it.changePath(col.path + it.path) }
//                } catch (e: IllegalStateException) {
//                    // if allowed, attempt to resole all columns to except absolutely too if relative failed
//                    if (allowFullPaths) {
//                        val allColsAbsolute = allCols.map { it.addParentPath(col.path) }
//                        val columnsToExcept = other.resolve(context)
//                        allColsAbsolute.allColumnsExceptKeepingStructure(columnsToExcept)
//                    } else {
//                        throw e
//                    }
//                }

        }
}

// endregion
