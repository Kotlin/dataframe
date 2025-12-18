package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.jetbrains.kotlinx.dataframe.impl.columns.DistinctColumnSet
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * ## The Distinct Operation
 *
 * It removes duplicated rows based on all columns.
 *
 * __NOTE:__ The rows in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order as they were in the original [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 *
 *
 * @return A new DataFrame containing only distinct rows.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/distinct.html">See `distinct` on the documentation website.</a>
 * .
 *
 */
public fun <T> DataFrame<T>.distinct(): DataFrame<T> = distinctBy { all() }

/**
 * ## The Distinct Operation
 *
 * It removes duplicated rows based on the specified columns.
 *
 * __NOTE:__ The rows in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order as they were in the original [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * @param [columns][org.jetbrains.kotlinx.dataframe.columns]
 * The names of the columns to consider for evaluating distinct rows.
 *
 * @return A new DataFrame containing only distinct rows.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/distinct.html">See `distinct` on the documentation website.</a>
 * .
 */
@[Refine Interpretable("Distinct0")]
public fun <T, C> DataFrame<T>.distinct(columns: ColumnsSelector<T, C>): DataFrame<T> = select(columns).distinct()

/**
 * ## The Distinct Operation
 *
 * It removes duplicated rows based on the specified columns.
 *
 * __NOTE:__ The rows in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order as they were in the original [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * @param [columns][org.jetbrains.kotlinx.dataframe.columns]
 * The names of the columns to consider for evaluating distinct rows.
 *
 * @return A new DataFrame containing only distinct rows.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/distinct.html">See `distinct` on the documentation website.</a>
 * .
 */
@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> DataFrame<T>.distinct(vararg columns: KProperty<*>): DataFrame<T> =
    distinct {
        val set = columns.toColumnSet()
        set
    }

/**
 * ## The Distinct Operation
 *
 * It removes duplicated rows based on the specified columns.
 *
 * __NOTE:__ The rows in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order as they were in the original [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * @param [columns][org.jetbrains.kotlinx.dataframe.columns]
 * The names of the columns to consider for evaluating distinct rows.
 *
 * @return A new DataFrame containing only distinct rows.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/distinct.html">See `distinct` on the documentation website.</a>
 * .
 */
public fun <T> DataFrame<T>.distinct(vararg columns: String): DataFrame<T> = distinct { columns.toColumnSet() }

/**
 * ## The Distinct Operation
 *
 * It removes duplicated rows based on the specified columns.
 *
 * __NOTE:__ The rows in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order as they were in the original [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * @param [columns][org.jetbrains.kotlinx.dataframe.columns]
 * The names of the columns to consider for evaluating distinct rows.
 *
 * @return A new DataFrame containing only distinct rows.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/distinct.html">See `distinct` on the documentation website.</a>
 * .
 */
@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> DataFrame<T>.distinct(vararg columns: AnyColumnReference): DataFrame<T> =
    distinct { columns.toColumnSet() }

/**
 * ## The Distinct Operation
 *
 * It removes duplicated rows based on the specified columns.
 *
 * __NOTE:__ The rows in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order as they were in the original [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * @param [columns][org.jetbrains.kotlinx.dataframe.columns]
 * The names of the columns to consider for evaluating distinct rows.
 *
 * @return A new DataFrame containing only distinct rows.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/distinct.html">See `distinct` on the documentation website.</a>
 * .
 */
@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> DataFrame<T>.distinctBy(vararg columns: KProperty<*>): DataFrame<T> =
    distinctBy { columns.toColumnSet() }

/**
 * ## The Distinct Operation
 *
 * It removes duplicated rows based on the specified columns.
 *
 * __NOTE:__ The rows in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order as they were in the original [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * @param [columns][org.jetbrains.kotlinx.dataframe.columns]
 * The names of the columns to consider for evaluating distinct rows.
 *
 * @return A new DataFrame containing only distinct rows.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/distinct.html">See `distinct` on the documentation website.</a>
 * .
 */
public fun <T> DataFrame<T>.distinctBy(vararg columns: String): DataFrame<T> = distinctBy { columns.toColumnSet() }

/**
 * ## The Distinct Operation
 *
 * It removes duplicated rows based on the specified columns.
 *
 * __NOTE:__ The rows in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order as they were in the original [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * @param [columns][org.jetbrains.kotlinx.dataframe.columns]
 * The names of the columns to consider for evaluating distinct rows.
 *
 * @return A new DataFrame containing only distinct rows.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/distinct.html">See `distinct` on the documentation website.</a>
 * .
 */
@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> DataFrame<T>.distinctBy(vararg columns: AnyColumnReference): DataFrame<T> =
    distinctBy { columns.toColumnSet() }

/**
 * ## The Distinct Operation
 *
 * It removes duplicated rows based on the specified columns.
 *
 * __NOTE:__ The rows in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order as they were in the original [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * @param [columns][org.jetbrains.kotlinx.dataframe.columns]
 * The names of the columns to consider for evaluating distinct rows.
 *
 * @return A new DataFrame containing only distinct rows.
 *
 * @see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 * @see <a href="https://kotlin.github.io/dataframe/distinct.html">See `distinct` on the documentation website.</a>
 * .
 */
public fun <T, C> DataFrame<T>.distinctBy(columns: ColumnsSelector<T, C>): DataFrame<T> {
    val cols = get(columns)
    val distinctIndices = indices.distinctBy { i -> cols.map { it[i] } }
    return this[distinctIndices]
}

// endregion

// region ColumnsSelectionDsl

/**
 * ##### Distinct [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface DistinctColumnsSelectionDsl {

    /**
     * ## Distinct Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[`ColumnSet`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`distinct`**][org.jetbrains.kotlinx.dataframe.api.DistinctColumnsSelectionDsl.distinct]**`()`**
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
    public interface Grammar {

        /** __`.`__[**`distinct`**][ColumnsSelectionDsl.distinct] */
        public interface ColumnSetName
    }

    /**
     * ## Distinct
     * Returns a new [ColumnSet] from [this] [ColumnSet] containing only distinct columns (by path).
     * This is useful when you've selected the same column multiple times but only want it once.
     *
     * NOTE: This doesn't solve [DuplicateColumnNamesException] if you've selected two columns with the same name.
     * For this, you'll need to [rename][ColumnsSelectionDsl.named] one of the columns.
     *
     * ### Check out: [Grammar]
     *
     * #### For Example:
     * `df.`[select][DataFrame.select]` { (`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() `[and][ColumnsSelectionDsl.and]` age).`[distinct][ColumnSet.distinct]`() }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`().`[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order").`[distinct][ColumnSet.distinct]`() }`
     *
     * @return A new [ColumnSet] containing only distinct columns (by path).
     * @see ColumnsSelectionDsl.named
     * @see ColumnsSelectionDsl.simplify
     */
    public fun <C> ColumnSet<C>.distinct(): ColumnSet<C> = DistinctColumnSet(this)
}

// endregion
