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
 * It removes duplicated rows based on {@get PHRASE_ENDING}.
 *
 * __NOTE:__ The rows in the resulting [DataFrame] are in the same order as they were in the original [DataFrame].
 *
 * {@get [DISTINCT_PARAM] @param [columns]
 * The names of the columns to consider for evaluating distinct rows.}
 *
 * @return A new DataFrame containing only distinct rows.
 *
 * @see [Selecting Columns][SelectSelectingOptions].
 * @see {@include [DocumentationUrls.Distinct]}
 */
@ExcludeFromSources
@Suppress("ClassName")
private interface DistinctDocs {
    interface DISTINCT_PARAM
}

/**
 * {@include [DistinctDocs]}
 * {@set PHRASE_ENDING all columns}.
 * {@set [DistinctDocs.DISTINCT_PARAM]}
 */
public fun <T> DataFrame<T>.distinct(): DataFrame<T> = distinctBy { all() }

/**
 * {@include [DistinctDocs]}
 * {@set PHRASE_ENDING the specified columns}.
 */
@[Refine Interpretable("Distinct0")]
public fun <T, C> DataFrame<T>.distinct(columns: ColumnsSelector<T, C>): DataFrame<T> = select(columns).distinct()

/**
 * {@include [DistinctDocs]}
 * {@set PHRASE_ENDING the specified columns}.
 */
@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> DataFrame<T>.distinct(vararg columns: KProperty<*>): DataFrame<T> =
    distinct {
        val set = columns.toColumnSet()
        set
    }

/**
 * {@include [DistinctDocs]}
 * {@set PHRASE_ENDING the specified columns}.
 */
public fun <T> DataFrame<T>.distinct(vararg columns: String): DataFrame<T> = distinct { columns.toColumnSet() }

/**
 * {@include [DistinctDocs]}
 * {@set PHRASE_ENDING the specified columns}.
 */
@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> DataFrame<T>.distinct(vararg columns: AnyColumnReference): DataFrame<T> =
    distinct { columns.toColumnSet() }

/**
 * {@include [DistinctDocs]}
 * {@set PHRASE_ENDING the specified columns}.
 */
@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> DataFrame<T>.distinctBy(vararg columns: KProperty<*>): DataFrame<T> =
    distinctBy { columns.toColumnSet() }

/**
 * {@include [DistinctDocs]}
 * {@set PHRASE_ENDING the specified columns}.
 */
public fun <T> DataFrame<T>.distinctBy(vararg columns: String): DataFrame<T> = distinctBy { columns.toColumnSet() }

/**
 * {@include [DistinctDocs]}
 * {@set PHRASE_ENDING the specified columns}.
 */
@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> DataFrame<T>.distinctBy(vararg columns: AnyColumnReference): DataFrame<T> =
    distinctBy { columns.toColumnSet() }

/**
 * {@include [DistinctDocs]}
 * {@set PHRASE_ENDING the specified columns}.
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
