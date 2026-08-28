package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.DistinctDocs.DESCRIPTION
import org.jetbrains.kotlinx.dataframe.api.DistinctDocs.DISTINCT_PARAM
import org.jetbrains.kotlinx.dataframe.api.DistinctDocs.DISTINCT_RETURN
import org.jetbrains.kotlinx.dataframe.api.DistinctDocs.PHRASE_ENDING
import org.jetbrains.kotlinx.dataframe.api.DistinctDocs.SEE_ALSO
import org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.jetbrains.kotlinx.dataframe.impl.columns.DistinctColumnSet
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * Removes duplicated rows based on all columns.
 *
 * The [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order
 * as they were in the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>distinctBy</code>][org.jetbrains.kotlinx.dataframe.api.distinctBy] that removes duplicated rows based on the specified columns
 * and keeps all the columns in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `distinct` on the documentation website.](https://kotlin.github.io/dataframe/distinct.html) [See `distinctBy` on the documentation website.](https://kotlin.github.io/dataframe/distinct.html#distinctby)
 *
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] containing only distinct rows.
 */
public fun <T> DataFrame<T>.distinct(): DataFrame<T> = distinctBy { all() }

/**
 * Selects the specified columns and keeps only distinct rows based on these selected columns.
 *
 * The [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order
 * as they were in the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>distinctBy</code>][org.jetbrains.kotlinx.dataframe.api.distinctBy] that removes duplicated rows based on the specified columns
 * and keeps all the columns in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `distinct` on the documentation website.](https://kotlin.github.io/dataframe/distinct.html) [See `distinctBy` on the documentation website.](https://kotlin.github.io/dataframe/distinct.html#distinctby)
 *
 * @param [columns] The [<code>ColumnsSelector</code>][ColumnsSelector] used to select columns
 * that will be included in the resulting [<code>DataFrame</code>][DataFrame] and considered for evaluating distinct rows.
 * @return A new [<code>DataFrame</code>][DataFrame] containing only selected columns and distinct rows.
 */
@Refine
@Interpretable("Distinct0")
public fun <T, C> DataFrame<T>.distinct(columns: ColumnsSelector<T, C>): DataFrame<T> = select(columns).distinct()

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.distinct(vararg columns: KProperty<*>): DataFrame<T> =
    distinct {
        val set = columns.toColumnSet()
        set
    }

/**
 * Selects the specified columns and keeps only distinct rows based on these selected columns.
 *
 * The [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order
 * as they were in the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>distinctBy</code>][org.jetbrains.kotlinx.dataframe.api.distinctBy] that removes duplicated rows based on the specified columns
 * and keeps all the columns in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `distinct` on the documentation website.](https://kotlin.github.io/dataframe/distinct.html) [See `distinctBy` on the documentation website.](https://kotlin.github.io/dataframe/distinct.html#distinctby)
 *
 * @param [columns] The names of the columns to select
 * and to consider for evaluating distinct rows.
 * @return A new [<code>DataFrame</code>][DataFrame] containing only selected columns and distinct rows.
 */
public fun <T> DataFrame<T>.distinct(vararg columns: String): DataFrame<T> = distinct { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.distinct(vararg columns: AnyColumnReference): DataFrame<T> =
    distinct { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.distinctBy(vararg columns: KProperty<*>): DataFrame<T> =
    distinctBy { columns.toColumnSet() }

/**
 * Removes duplicated rows based on the specified columns.
 *
 * The [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order
 * as they were in the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>distinct</code>][distinct] that selects the specified columns
 * (if the columns are not specified, selects all columns)
 * and keeps only distinct rows based on these selected columns.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `distinct` on the documentation website.](https://kotlin.github.io/dataframe/distinct.html) [See `distinctBy` on the documentation website.](https://kotlin.github.io/dataframe/distinct.html#distinctby)
 *
 * @param [columns]
 * The names of the columns to consider for evaluating distinct rows.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] containing only distinct rows.
 *
 */
public fun <T> DataFrame<T>.distinctBy(vararg columns: String): DataFrame<T> = distinctBy { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.distinctBy(vararg columns: AnyColumnReference): DataFrame<T> =
    distinctBy { columns.toColumnSet() }

/**
 * Removes duplicated rows based on the specified columns.
 *
 * The [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] are in the same order
 * as they were in the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also [<code>distinct</code>][distinct] that selects the specified columns
 * (if the columns are not specified, selects all columns)
 * and keeps only distinct rows based on these selected columns.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `distinct` on the documentation website.](https://kotlin.github.io/dataframe/distinct.html) [See `distinctBy` on the documentation website.](https://kotlin.github.io/dataframe/distinct.html#distinctby)
 *
 * @param [columns] The [<code>ColumnsSelector</code>][ColumnsSelector] used to select columns
 * that will be considered for evaluating distinct rows.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] containing only distinct rows.
 *
 */
public fun <T, C> DataFrame<T>.distinctBy(columns: ColumnsSelector<T, C>): DataFrame<T> {
    val cols = get(columns)
    val distinctIndices = indices.distinctBy { i -> cols.map { it[i] } }
    return this[distinctIndices]
}

// endregion

// region ColumnsSelectionDsl

/**
 * Distinct [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface DistinctColumnsSelectionDsl {

    /**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[<code>`ColumnSet`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`distinct`**</code>][org.jetbrains.kotlinx.dataframe.api.DistinctColumnsSelectionDsl.distinct]**`()`**
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

        /** __`.`__[<code>**`distinct`**</code>][ColumnsSelectionDsl.distinct] */
        public typealias ColumnSetName = Nothing
    }

    /**
     * Returns a new [<code>ColumnSet</code>][ColumnSet] from [<code>this</code>][this] [<code>ColumnSet</code>][ColumnSet] containing only distinct columns (by path).
     * This is useful when you've selected the same column multiple times but only want it once.
     *
     * This doesn't solve [<code>DuplicateColumnNamesException</code>][DuplicateColumnNamesException] if you've selected two columns with the same name.
     * For this, you'll need to [<code>rename</code>][ColumnsSelectionDsl.named] one of the columns.
     *
     * For more information: [See `distinct` in the Columns Selection DSL on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#distinct)
     *
     * See also [<code>Grammar</code>][Grammar], [<code>named</code>][ColumnsSelectionDsl.named], [<code>simplify</code>][ColumnsSelectionDsl.simplify].
     *
     * ### Examples
     * ```kotlin
     * df.select { (colsOf<Int>() and age).distinct() }
     * df.select { colsAtAnyDepth().nameStartsWith("order").distinct() }
     * ```
     *
     * @return A new [<code>ColumnSet</code>][ColumnSet] containing only distinct columns (by path).
     */
    public fun <C> ColumnSet<C>.distinct(): ColumnSet<C> = DistinctColumnSet(this)
}

// endregion
