package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.DistinctDocs.DISTINCT_RETURN
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
 * ## The {@get NAME Distinct} Operation
 *
 * {@get DESCRIPTION It removes duplicated rows based on {@get PHRASE_ENDING}}.
 *
 * __NOTE:__ The [rows][DataRow] in the resulting [DataFrame] are in the same order
 * as they were in the original [DataFrame].
 *
 * {@get [DISTINCT_PARAM]}
 *
 * {@get [DISTINCT_RETURN] @return A new [DataFrame] containing only distinct rows.}
 *
 * @see [Selecting Columns][SelectSelectingOptions].
 * @see {@include [DocumentationUrls.Distinct]}
 * @see {@include [DocumentationUrls.DistinctBy]}
 */
@ExcludeFromSources
@Suppress("ClassName")
private interface DistinctDocs {
    interface DISTINCT_PARAM
    interface DISTINCT_RETURN
}

/**
 * {@include [DistinctDocs]}
 * {@set PHRASE_ENDING all columns}.
 * {@set [DistinctDocs.DISTINCT_PARAM]}
 */
public fun <T> DataFrame<T>.distinct(): DataFrame<T> = distinctBy { all() }

/**
 * {@include [DistinctDocs]}
 * {@set DESCRIPTION It selects the specified columns and keeps only distinct rows based on these selected columns}
 * {@set [DistinctDocs.DISTINCT_PARAM] @param [columns] The names of the columns to select
 * and to consider for evaluating distinct rows.}
 * {@set [DISTINCT_RETURN] @return A new [DataFrame] containing only selected columns and distinct rows.}
 */
@Refine
@Interpretable("Distinct0")
public fun <T, C> DataFrame<T>.distinct(columns: ColumnsSelector<T, C>): DataFrame<T> = select(columns).distinct()

/**
 * {@include [DistinctDocs]}
 * {@set DESCRIPTION It selects the specified columns and keeps only distinct rows based on these selected columns}
 * {@set [DistinctDocs.DISTINCT_PARAM] @param [columns] The names of the columns to select
 * and to consider for evaluating distinct rows.}
 * {@set [DISTINCT_RETURN] @return A new [DataFrame] containing only selected columns and distinct rows.}
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.distinct(vararg columns: KProperty<*>): DataFrame<T> =
    distinct {
        val set = columns.toColumnSet()
        set
    }

/**
 * {@include [DistinctDocs]}
 * {@set DESCRIPTION It selects the specified columns and keeps only distinct rows based on these selected columns}
 * {@set [DistinctDocs.DISTINCT_PARAM] @param [columns] The names of the columns to select
 * and to consider for evaluating distinct rows.}
 * {@set [DISTINCT_RETURN] @return A new [DataFrame] containing only selected columns and distinct rows.}
 */
public fun <T> DataFrame<T>.distinct(vararg columns: String): DataFrame<T> = distinct { columns.toColumnSet() }

/**
 * {@include [DistinctDocs]}
 * {@set DESCRIPTION It selects the specified columns and keeps only distinct rows based on these selected columns}
 * {@set [DistinctDocs.DISTINCT_PARAM] @param [columns] The names of the columns to select
 * and to consider for evaluating distinct rows.}
 * {@set [DISTINCT_RETURN] @return A new [DataFrame] containing only selected columns and distinct rows.}
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.distinct(vararg columns: AnyColumnReference): DataFrame<T> =
    distinct { columns.toColumnSet() }

/**
 * {@include [DistinctDocs]}
 * {@set NAME DistinctBy}
 * {@set PHRASE_ENDING the specified columns}.
 * {@set [DistinctDocs.DISTINCT_PARAM] @param [columns]
 * The names of the columns to consider for evaluating distinct rows.}
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.distinctBy(vararg columns: KProperty<*>): DataFrame<T> =
    distinctBy { columns.toColumnSet() }

/**
 * {@include [DistinctDocs]}
 * {@set NAME DistinctBy}
 * {@set PHRASE_ENDING the specified columns}.
 * {@set [DistinctDocs.DISTINCT_PARAM] @param [columns]
 * The names of the columns to consider for evaluating distinct rows.}
 */
public fun <T> DataFrame<T>.distinctBy(vararg columns: String): DataFrame<T> = distinctBy { columns.toColumnSet() }

/**
 * {@include [DistinctDocs]}
 * {@set NAME DistinctBy}
 * {@set PHRASE_ENDING the specified columns}.
 * {@set [DistinctDocs.DISTINCT_PARAM] @param [columns]
 * The names of the columns to consider for evaluating distinct rows.}
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.distinctBy(vararg columns: AnyColumnReference): DataFrame<T> =
    distinctBy { columns.toColumnSet() }

/**
 * {@include [DistinctDocs]}
 * {@set NAME DistinctBy}
 * {@set PHRASE_ENDING the specified columns}.
 * {@set [DistinctDocs.DISTINCT_PARAM] @param [columns]
 * The names of the columns to consider for evaluating distinct rows.}
 */
public fun <T, C> DataFrame<T>.distinctBy(columns: ColumnsSelector<T, C>): DataFrame<T> {
    val cols = get(columns)
    val distinctIndices = indices.distinctBy { i -> cols.map { it[i] } }
    return this[distinctIndices]
}

// endregion

// region ColumnsSelectionDsl

/**
 * ##### Distinct {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface DistinctColumnsSelectionDsl {

    /**
     * ## Distinct Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}**`()`**
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_PART]}
     * {@set [DslGrammarTemplate.COLUMN_GROUP_PART]}
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
