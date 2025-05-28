package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.getTrueIndices
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataColumn

public inline fun <T> DataColumn<T>.filter(predicate: Predicate<T>): DataColumn<T> =
    indices
        .filter { predicate(get(it)) }
        .let { get(it) }

// endregion

// region DataFrame

public inline fun <T> DataFrame<T>.filter(predicate: RowFilter<T>): DataFrame<T> =
    indices().filter {
        val row = get(it)
        predicate(row, row)
    }.let { get(it) }

public fun <T> DataFrame<T>.filterBy(column: ColumnSelector<T, Boolean>): DataFrame<T> =
    getRows(getColumn(column).toList().getTrueIndices())

public fun <T> DataFrame<T>.filterBy(column: String): DataFrame<T> = filterBy { column.toColumnOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.filterBy(column: ColumnReference<Boolean>): DataFrame<T> = filterBy { column }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.filterBy(column: KProperty<Boolean>): DataFrame<T> = filterBy { column.toColumnAccessor() }

// endregion

internal fun <T, C> ColumnsSelector<T, C>.filter(predicate: (ColumnWithPath<C>) -> Boolean): ColumnsSelector<T, C> =
    { this@filter(it).asColumnSet().filter(predicate) }

// region ColumnsSelectionDsl

/**
 * ## Filter [ColumnSet] {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface FilterColumnsSelectionDsl {

    /**
     * ## Filter [ColumnSet] Grammar
     *
     * {@include [DslGrammarTemplate]}
     *
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ConditionDef]}
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}**` { `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_PART]}
     * {@set [DslGrammarTemplate.COLUMN_GROUP_PART]}
     */
    public interface Grammar {

        /** __`.`__[**`filter`**][ColumnsSelectionDsl.filter] */
        public interface ColumnSetName
    }

    /**
     * ## Filter [ColumnSet]
     *
     * Creates a subset of columns ([ColumnSet]) from the current [ColumnSet] that
     * adhere to the given [predicate].
     *
     * Aside from calling [filter][ColumnSet.filter] directly, you can also use the [get][ColumnsSelectionDsl.get] operator
     * in most cases. This function belongs to [cols][ColumnsSelectionDsl.cols] but operates identically.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[`remove`][DataFrame.remove]`  {  `[`all`][ColumnsSelectionDsl.all]`().`[`filter`][ColumnSet.filter]` { it.`[`hasNulls`][DataColumn.hasNulls]`() } }`
     *
     * `// and although this can be shortened to just the `[`colsOf`][colsOf]` call:`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][colsOf]`<`[`String`][String]`>().`[`filter`][ColumnSet.filter]`  { "e"  `[`in`][String.contains]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][SingleColumn.colsOf]`<`[`String`][String]`>()`[`[`][ColumnsSelectionDsl.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     * @param [predicate] A [ColumnFilter function][ColumnFilter] that takes a [ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.cols]
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.filter(predicate: ColumnFilter<C>): TransformableColumnSet<C> =
        colsInternal(predicate as ColumnFilter<*>) as TransformableColumnSet<C>
}

// endregion
