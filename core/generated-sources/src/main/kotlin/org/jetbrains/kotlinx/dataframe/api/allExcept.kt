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
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.impl.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_EXCEPT
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_REPLACE
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_REPLACE_VARARG
import kotlin.experimental.ExperimentalTypeInference
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

//    public operator fun ColumnReference<*>.not(): ColumnSet<Any?> =
//        with(this@AllExceptColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
//            allExcept(this@not)
//        }
//
//    public operator fun ColumnSet<*>.not(): ColumnSet<Any?> =
//        with(this@AllExceptColumnsSelectionDsl as ColumnsSelectionDsl<T>) {
//            allExcept(this@not)
//        }

    public infix fun <C> ColumnSet<C>.oldExcept(other: ColumnsResolver<*>): ColumnSet<C> =
        createColumnSet { context ->
            val resolvedCols = this@oldExcept.resolve(context)
            val resolvedColsToExcept = other.resolve(context)
            resolvedCols.allColumnsExceptAndUnpack(resolvedColsToExcept)
        } as ColumnSet<C>

    public infix fun <C> SingleColumn<DataRow<C>>.exceptNew(selector: ColumnsSelector<C, *>): SingleColumn<DataRow<*>> =
        this.ensureIsColumnGroup().transformSingle { singleCol ->

            val columnsToExcept = singleCol.asColumnGroup().getColumnsWithPaths(selector)
                .map { it.changePath(singleCol.path + it.path) }

            val newCols = listOf(singleCol).allColumnsExceptKeepingStructure(columnsToExcept)

            newCols as List<ColumnWithPath<DataRow<*>>>
        }.singleInternal()

    @Deprecated("Use allColsExcept instead", ReplaceWith("this.allColsExcept(selector)"))
    public fun <C> SingleColumn<DataRow<C>>.except(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allColsExcept(selector)

    @Deprecated("Use allColsExcept instead", ReplaceWith("this.allColsExcept { others.toColumnSet() } "))
    public fun SingleColumn<DataRow<*>>.except(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    @Deprecated("Use allColsExcept instead", ReplaceWith("this.allColsExcept(others)"))
    public fun SingleColumn<DataRow<*>>.except(vararg others: String): ColumnSet<*> =
        allColsExcept(*others)

    @Deprecated("Use allColsExcept instead", ReplaceWith("this.allColsExcept(others)"))
    public fun SingleColumn<DataRow<*>>.except(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExcept(*others)

    @Deprecated("Use allColsExcept instead", ReplaceWith("this.allColsExcept(others)"))
    public fun SingleColumn<DataRow<*>>.except(vararg others: ColumnPath): ColumnSet<*> =
        allColsExcept(*others)

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(others)"))
    public fun <C> ColumnsSelectionDsl<C>.except(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allExcept(selector)

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(others"))
    public fun ColumnsSelectionDsl<*>.except(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(*others)

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(others)"))
    public fun ColumnsSelectionDsl<*>.except(vararg others: String): ColumnSet<*> =
        allExcept(*others)

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(others)"))
    public fun ColumnsSelectionDsl<*>.except(vararg others: KProperty<*>): ColumnSet<*> =
        allExcept(*others)

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(others)"))
    public fun ColumnsSelectionDsl<*>.except(vararg others: ColumnPath): ColumnSet<*> =
        allExcept(*others)

    // endregion

    // region ColumnSet

    public infix fun <C> ColumnSet<C>.except(selector: () -> ColumnsResolver<*>): ColumnSet<C> =
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

    // no scoping issues, this can exist for legacy purposes
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: String): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    // endregion

    // region SingleColumn

    public fun <C> SingleColumn<DataRow<C>>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allColsExceptInternal(selector.toColumns())

    @Deprecated(ALL_COLS_EXCEPT, ReplaceWith(ALL_COLS_REPLACE), DeprecationLevel.ERROR)
    public fun SingleColumn<DataRow<*>>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(ALL_COLS_EXCEPT, ReplaceWith(ALL_COLS_REPLACE_VARARG), DeprecationLevel.ERROR)
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExceptInternal(others.toColumnSet())

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExceptInternal(others.toColumnSet())

    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg other: ColumnPath): ColumnSet<*> =
        allColsExceptInternal(other.toColumnSet())

    // reference and path

    // endregion

    // region String

    public fun String.allColsExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    @Deprecated(ALL_COLS_EXCEPT, ReplaceWith(ALL_COLS_REPLACE), DeprecationLevel.ERROR)
    public fun String.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(ALL_COLS_EXCEPT, ReplaceWith(ALL_COLS_REPLACE_VARARG), DeprecationLevel.ERROR)
    public fun String.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    public fun String.allColsExcept(vararg others: String): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    public fun String.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    public fun String.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion

    // region KProperty

    @OptIn(ExperimentalTypeInference::class)
    @OverloadResolutionByLambdaReturnType
    // TODO: [KT-64092](https://youtrack.jetbrains.com/issue/KT-64092/OVERLOADRESOLUTIONAMBIGUITY-caused-by-lambda-argument)
    public fun <C> KProperty<C>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowAllColsExcept")
    public fun <C> KProperty<DataRow<C>>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    @Deprecated(ALL_COLS_EXCEPT, ReplaceWith(ALL_COLS_REPLACE), DeprecationLevel.ERROR)
    public fun KProperty<*>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(ALL_COLS_EXCEPT, ReplaceWith(ALL_COLS_REPLACE_VARARG), DeprecationLevel.ERROR)
    public fun KProperty<*>.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    public fun KProperty<*>.allColsExcept(vararg others: String): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    public fun KProperty<*>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    public fun KProperty<*>.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion

    // region ColumnPath

    public fun ColumnPath.allColsExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    @Deprecated(ALL_COLS_EXCEPT, ReplaceWith(ALL_COLS_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnPath.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(ALL_COLS_EXCEPT, ReplaceWith(ALL_COLS_REPLACE_VARARG), DeprecationLevel.ERROR)
    public fun ColumnPath.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    public fun ColumnPath.allColsExcept(vararg others: String): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    public fun ColumnPath.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    public fun ColumnPath.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion

    // endregion

    private fun SingleColumn<DataRow<*>>.allColsExceptInternal(other: ColumnsResolver<*>) =
        selectInternal { all() except other }
}

// endregion
