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
 * {@get [DESCRIPTION] Removes duplicated rows based on $[PHRASE_ENDING]=all columns.}
 *
 * The [rows][DataRow] in the resulting [DataFrame] are in the same order
 * as they were in the original [DataFrame].
 *
 * See also {@get [SEE_ALSO] [distinctBy] that removes duplicated rows based on the specified columns
 * and keeps all the columns in the resulting [DataFrame].}
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][SelectSelectingOptions].
 *
 * For more information:
 *
 * @include [DocumentationUrls.Distinct]
 *
 * @include [DocumentationUrls.DistinctBy]
 *
 * @get [DISTINCT_PARAM]
 *
 * @return {@get [DISTINCT_RETURN] A new [DataFrame] containing only distinct rows.}
 */
@ExcludeFromSources
@Suppress("ClassName")
private interface DistinctDocs {
    // Parameter of the function (the `@param` part of the KDoc)
    interface DISTINCT_PARAM

    // Value returned by the function (the `@return` part of the KDoc)
    interface DISTINCT_RETURN

    // Description of what the function does
    interface DESCRIPTION

    // Part of the description that can be customized for a specific function
    interface PHRASE_ENDING

    // Reference to a related function (see also)
    interface SEE_ALSO
}

/**
 * @include [DistinctDocs]
 * @set [DISTINCT_PARAM]
 */
public fun <T> DataFrame<T>.distinct(): DataFrame<T> = distinctBy { all() }

/**
 * @include [DistinctDocs]
 * @set [DESCRIPTION] Selects the specified columns and keeps only distinct rows based on these selected columns.
 * @set [DISTINCT_PARAM] @param [columns\] The [ColumnsSelector] used to select columns
 * that will be included in the resulting [DataFrame] and considered for evaluating distinct rows.
 * @set [DISTINCT_RETURN] A new [DataFrame] containing only selected columns and distinct rows.
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
 * @include [DistinctDocs]
 * @set [DESCRIPTION] Selects the specified columns and keeps only distinct rows based on these selected columns.
 * @set [DISTINCT_PARAM] @param [columns\] The names of the columns to select
 * and to consider for evaluating distinct rows.
 * @set [DISTINCT_RETURN] A new [DataFrame] containing only selected columns and distinct rows.
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
 * @include [DistinctDocs]
 * {@set [PHRASE_ENDING] the specified}
 * @set [SEE_ALSO] [distinct] that selects the specified columns
 * (if the columns are not specified, selects all columns)
 * and keeps only distinct rows based on these selected columns.
 * @set [DISTINCT_PARAM] @param [columns\]
 * The names of the columns to consider for evaluating distinct rows.
 */
public fun <T> DataFrame<T>.distinctBy(vararg columns: String): DataFrame<T> = distinctBy { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.distinctBy(vararg columns: AnyColumnReference): DataFrame<T> =
    distinctBy { columns.toColumnSet() }

/**
 * @include [DistinctDocs]
 * {@set [PHRASE_ENDING] the specified}
 * @set [SEE_ALSO] [distinct] that selects the specified columns
 * (if the columns are not specified, selects all columns)
 * and keeps only distinct rows based on these selected columns.
 * @set [DISTINCT_PARAM] @param [columns\] The [ColumnsSelector] used to select columns
 * that will be considered for evaluating distinct rows.
 */
public fun <T, C> DataFrame<T>.distinctBy(columns: ColumnsSelector<T, C>): DataFrame<T> {
    val cols = get(columns)
    val distinctIndices = indices.distinctBy { i -> cols.map { it[i] } }
    return this[distinctIndices]
}

// endregion

// region ColumnsSelectionDsl

/**
 * Distinct {@include [ColumnsSelectionDslLink]}.
 *
 * See [Grammar] for all functions in this interface.
 */
public interface DistinctColumnsSelectionDsl {

    /**
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
     * Returns a new [ColumnSet] from [this] [ColumnSet] containing only distinct columns (by path).
     * This is useful when you've selected the same column multiple times but only want it once.
     *
     * This doesn't solve [DuplicateColumnNamesException] if you've selected two columns with the same name.
     * For this, you'll need to [rename][ColumnsSelectionDsl.named] one of the columns.
     *
     * See also [Grammar], [named][ColumnsSelectionDsl.named], [simplify][ColumnsSelectionDsl.simplify].
     *
     * ### Examples
     * ```kotlin
     * df.select { (colsOf<Int>() and age).distinct() }
     * df.select { colsAtAnyDepth().nameStartsWith("order").distinct() }
     * ```
     *
     * @return A new [ColumnSet] containing only distinct columns (by path).
     */
    public fun <C> ColumnSet<C>.distinct(): ColumnSet<C> = DistinctColumnSet(this)
}

// endregion
