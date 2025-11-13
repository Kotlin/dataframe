package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.singleOrNullWithTransformerImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataColumn

public fun <T> DataColumn<T>.last(): T = get(size - 1)

public fun <T> DataColumn<T>.lastOrNull(): T? = if (size > 0) last() else null

public inline fun <T> DataColumn<T>.last(predicate: (T) -> Boolean): T = values.last(predicate)

public inline fun <T> DataColumn<T>.lastOrNull(predicate: (T) -> Boolean): T? = values.lastOrNull(predicate)

// endregion

// region DataFrame

public inline fun <T> DataFrame<T>.lastOrNull(predicate: RowFilter<T>): DataRow<T>? =
    rowsReversed().firstOrNull { predicate(it, it) }

public inline fun <T> DataFrame<T>.last(predicate: RowFilter<T>): DataRow<T> =
    rowsReversed().first {
        predicate(it, it)
    }

public fun <T> DataFrame<T>.lastOrNull(): DataRow<T>? = if (nrow > 0) get(nrow - 1) else null

public fun <T> DataFrame<T>.last(): DataRow<T> {
    if (nrow == 0) {
        throw NoSuchElementException("DataFrame has no rows. Use `lastOrNull`.")
    }
    return get(nrow - 1)
}

// endregion

// region GroupBy

@Interpretable("GroupByReducePredicate")
public fun <T, G> GroupBy<T, G>.last(): ReducedGroupBy<T, G> = reduce { lastOrNull() }

@Interpretable("GroupByReducePredicate")
public fun <T, G> GroupBy<T, G>.last(predicate: RowFilter<G>): ReducedGroupBy<T, G> = reduce { lastOrNull(predicate) }

// endregion

// region Pivot

public fun <T> Pivot<T>.last(): ReducedPivot<T> = reduce { lastOrNull() }

public fun <T> Pivot<T>.last(predicate: RowFilter<T>): ReducedPivot<T> = reduce { lastOrNull(predicate) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.last(): ReducedPivotGroupBy<T> = reduce { lastOrNull() }

public fun <T> PivotGroupBy<T>.last(predicate: RowFilter<T>): ReducedPivotGroupBy<T> = reduce { lastOrNull(predicate) }

// endregion

// region ColumnsSelectionDsl

/**
 * # Last (Col) {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface LastColumnsSelectionDsl {

    /**
     * ## Last (Col) Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ConditionDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnGroupName]}`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     */
    public interface Grammar {

        /** [**`last`**][ColumnsSelectionDsl.last] */
        public typealias PlainDslName = Nothing

        /** __`.`__[**`last`**][ColumnsSelectionDsl.last] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[**`lastCol`**][ColumnsSelectionDsl.lastCol] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Last (Col)
     * Returns the last column from [this\] that adheres to the optional given [condition\].
     * If no column adheres to the given [condition\], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnGroup], `last` is named `lastCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][DataFrame.select]`  {  `[last][ColumnsSelectionDsl.last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[lastCol][String.lastCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * #### Examples for this overload:
     *
     * {@get [Examples]}
     *
     * @param [condition\] The optional [ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn] containing the last column
     *   that adheres to the given [condition\].
     * @throws [NoSuchElementException\] if no column adheres to the given [condition\].
     * @see [ColumnsSelectionDsl.first\]
     */
    private interface CommonLastDocs {

        /** Examples key */
        typealias Examples = Nothing
    }

    /**
     * @include [CommonLastDocs]
     * @set [CommonLastDocs.Examples]
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[last][ColumnSet.last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[last][ColumnSet.last]`() }`
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("Last0")
    public fun <C> ColumnSet<C>.last(condition: ColumnFilter<C> = { true }): SingleColumn<C> =
        (allColumnsInternal() as TransformableColumnSet<C>)
            .transform { listOf(it.last(condition)) }
            .singleOrNullWithTransformerImpl()

    /**
     * @include [CommonLastDocs]
     * @set [CommonLastDocs.Examples]
     *
     * `df.`[select][DataFrame.select]`  {  `[last][ColumnsSelectionDsl.last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    @Interpretable("Last1")
    public fun ColumnsSelectionDsl<*>.last(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        asSingleColumn().lastCol(condition)

    /**
     * @include [CommonLastDocs]
     * @set [CommonLastDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[lastCol][SingleColumn.lastCol]`() }`
     */
    @Interpretable("Last2")
    public fun SingleColumn<DataRow<*>>.lastCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        this.ensureIsColumnGroup().asColumnSet().last(condition)

    /**
     * @include [CommonLastDocs]
     * @set [CommonLastDocs.Examples]
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[lastCol][String.lastCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun String.lastCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).lastCol(condition)

    /**
     * @include [CommonLastDocs]
     * @set [CommonLastDocs.Examples]
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[lastCol][SingleColumn.lastCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[lastCol][KProperty.lastCol]`() }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.lastCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).lastCol(condition)

    /**
     * @include [CommonLastDocs]
     * @set [CommonLastDocs.Examples]
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[lastCol][ColumnPath.lastCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun ColumnPath.lastCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).lastCol(condition)
}

// endregion
