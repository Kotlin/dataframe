package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataColumn

public fun <T> DataColumn<T>.take(n: Int): DataColumn<T> =
    when {
        n == 0 -> get(emptyList())
        n >= size -> this
        else -> get(0 until n)
    }

public fun <T> DataColumn<T>.takeLast(n: Int = 1): DataColumn<T> = drop(size - n)

// endregion

// region DataFrame

/**
 * Returns a DataFrame containing first [n] rows.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.take(n: Int): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return getRows(0 until n.coerceAtMost(nrow))
}

/**
 * Returns a DataFrame containing last [n] rows.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.takeLast(n: Int = 1): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return drop((nrow - n).coerceAtLeast(0))
}

/**
 * Returns a DataFrame containing first rows that satisfy the given [predicate].
 */
public inline fun <T> DataFrame<T>.takeWhile(predicate: RowFilter<T>): DataFrame<T> =
    firstOrNull { !predicate(it, it) }?.let { take(it.index()) } ?: this

// endregion

// region ColumnsSelectionDsl

/**
 * ## Take [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface TakeColumnsSelectionDsl {

    /**
     * ## Take (Last) (Cols) (While) Grammar
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
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[`ColumnFilter`][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `number: `[`Int`][Int]
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**`take`**][ColumnsSelectionDsl.take]`(`[**`Last`**][ColumnsSelectionDsl.takeLast]`)`**`(`**[`number`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     *  `| `[**`take`**][ColumnsSelectionDsl.takeWhile]`(`[**`Last`**][ColumnsSelectionDsl.takeLastWhile]`)`[**`While`**][ColumnsSelectionDsl.takeWhile]**`  {  `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`take`**][ColumnsSelectionDsl.take]`(`[**`Last`**][ColumnSet.takeLast]`)`**`(`**[`number`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[**`take`**][ColumnsSelectionDsl.takeWhile]`(`[**`Last`**][ColumnsSelectionDsl.takeLastWhile]`)`[**`While`**][ColumnsSelectionDsl.takeWhile]**`  {  `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [Column Group (reference)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnGroup`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`take`**][ColumnsSelectionDsl.takeCols]`(`[**`Last`**][ColumnsSelectionDsl.takeLastCols]`)`[**`Cols`**][ColumnsSelectionDsl.takeCols]**`(`**[`number`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[**`take`**][ColumnsSelectionDsl.takeColsWhile]`(`[**`Last`**][ColumnsSelectionDsl.takeLastColsWhile]`)`[**`ColsWhile`**][ColumnsSelectionDsl.takeColsWhile]**`  {  `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
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

        /** [**`take`**][ColumnsSelectionDsl.take]`(`[**`Last`**][ColumnsSelectionDsl.takeLast]`)` */
        public typealias PlainDslName = Nothing

        /** __`.`__[**`take`**][ColumnsSelectionDsl.take]`(`[**`Last`**][ColumnSet.takeLast]`)` */
        public typealias ColumnSetName = Nothing

        /** __`.`__[**`take`**][ColumnsSelectionDsl.takeCols]`(`[**`Last`**][ColumnsSelectionDsl.takeLastCols]`)`[**`Cols`**][ColumnsSelectionDsl.takeCols] */
        public typealias ColumnGroupName = Nothing

        /** [**`take`**][ColumnsSelectionDsl.takeWhile]`(`[**`Last`**][ColumnsSelectionDsl.takeLastWhile]`)`[**`While`**][ColumnsSelectionDsl.takeWhile] */
        public typealias PlainDslWhileName = Nothing

        /** __`.`__[**`take`**][ColumnsSelectionDsl.takeWhile]`(`[**`Last`**][ColumnsSelectionDsl.takeLastWhile]`)`[**`While`**][ColumnsSelectionDsl.takeWhile] */
        public typealias ColumnSetWhileName = Nothing

        /** __`.`__[**`take`**][ColumnsSelectionDsl.takeColsWhile]`(`[**`Last`**][ColumnsSelectionDsl.takeLastColsWhile]`)`[**`ColsWhile`**][ColumnsSelectionDsl.takeColsWhile] */
        public typealias ColumnGroupWhileName = Nothing
    }

    // region take

    /**
     * ## Take (Cols)
     * This takes the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    private typealias CommonTakeFirstDocs = Nothing

    /**
     * ## Take (Cols)
     * This takes the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[take][ColumnSet.take]`(2) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[take][ColumnSet.take]`(2) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    @Interpretable("Take0")
    public fun <C> ColumnSet<C>.take(n: Int): ColumnSet<C> = transform { it.take(n) }

    /**
     * ## Take (Cols)
     * This takes the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[take][ColumnsSelectionDsl.take]`(5) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    @Interpretable("Take1")
    public fun ColumnsSelectionDsl<*>.take(n: Int): ColumnSet<*> = this.asSingleColumn().takeCols(n)

    /**
     * ## Take (Cols)
     * This takes the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    @Interpretable("Take2")
    public fun SingleColumn<DataRow<*>>.takeCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().take(n) }

    /**
     * ## Take (Cols)
     * This takes the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun String.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    /**
     * ## Take (Cols)
     * This takes the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeCols][SingleColumn.takeCols]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeCols][KProperty.takeCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    /**
     * ## Take (Cols)
     * This takes the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeCols][ColumnPath.takeCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun ColumnPath.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    // endregion

    // region takeLast

    /**
     * ## Take Last (Cols)
     * This takes the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    private typealias CommonTakeLastDocs = Nothing

    /**
     * ## Take Last (Cols)
     * This takes the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeLast][ColumnSet.takeLast]`(2) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeLast][ColumnSet.takeLast]`(2) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    @Interpretable("TakeLast0")
    public fun <C> ColumnSet<C>.takeLast(n: Int = 1): ColumnSet<C> = transform { it.takeLast(n) }

    /**
     * ## Take Last (Cols)
     * This takes the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[takeLast][ColumnsSelectionDsl.takeLast]`(5) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    @Interpretable("TakeLast1")
    public fun ColumnsSelectionDsl<*>.takeLast(n: Int = 1): ColumnSet<*> = asSingleColumn().takeLastCols(n)

    /**
     * ## Take Last (Cols)
     * This takes the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeLast][SingleColumn.takeLastCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    @Interpretable("TakeLast2")
    public fun SingleColumn<DataRow<*>>.takeLastCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeLast(n) }

    /**
     * ## Take Last (Cols)
     * This takes the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun String.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    /**
     * ## Take Last (Cols)
     * This takes the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeLastCols][KProperty.takeLastCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    /**
     * ## Take Last (Cols)
     * This takes the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeLastCols][ColumnPath.takeLastCols]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun ColumnPath.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    // endregion

    // region takeWhile

    /**
     * ## Take (Cols) While
     * This function takes the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeWhile`][ColumnSet.takeWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeWhile`][SingleColumn.takeColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeColsWhile`][String.takeColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    private typealias CommonTakeFirstWhileDocs = Nothing

    /**
     * ## Take (Cols) While
     * This function takes the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeWhile`][ColumnSet.takeWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeWhile`][SingleColumn.takeColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeColsWhile`][String.takeColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeWhile][ColumnSet.takeWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeWhile][ColumnSet.takeWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun <C> ColumnSet<C>.takeWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.takeWhile(predicate) }

    /**
     * ## Take (Cols) While
     * This function takes the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeWhile`][ColumnSet.takeWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeWhile`][SingleColumn.takeColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeColsWhile`][String.takeColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[takeWhile][ColumnsSelectionDsl.takeWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun ColumnsSelectionDsl<*>.takeWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        asSingleColumn().takeColsWhile(predicate)

    /**
     * ## Take (Cols) While
     * This function takes the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeWhile`][ColumnSet.takeWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeWhile`][SingleColumn.takeColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeColsWhile`][String.takeColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun SingleColumn<DataRow<*>>.takeColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeWhile(predicate) }

    /**
     * ## Take (Cols) While
     * This function takes the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeWhile`][ColumnSet.takeWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeWhile`][SingleColumn.takeColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeColsWhile`][String.takeColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeColsWhile][String.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun String.takeColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    /**
     * ## Take (Cols) While
     * This function takes the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeWhile`][ColumnSet.takeWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeWhile`][SingleColumn.takeColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeColsWhile`][String.takeColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeColsWhile][SingleColumn.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeColsWhile][KProperty.takeColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.takeColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    /**
     * ## Take (Cols) While
     * This function takes the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeWhile`][ColumnSet.takeWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeWhile`][SingleColumn.takeColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeColsWhile`][String.takeColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeColsWhile][ColumnPath.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun ColumnPath.takeColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    // endregion

    // region takeLastWhile

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeLastWhile`][ColumnSet.takeLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeLastWhile`][SingleColumn.takeLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeLastColsWhile`][String.takeLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    private typealias CommonTakeLastWhileDocs = Nothing

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeLastWhile`][ColumnSet.takeLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeLastWhile`][SingleColumn.takeLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeLastColsWhile`][String.takeLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeLastWhile][ColumnSet.takeLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeLastWhile][ColumnSet.takeLastWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun <C> ColumnSet<C>.takeLastWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.takeLastWhile(predicate) }

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeLastWhile`][ColumnSet.takeLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeLastWhile`][SingleColumn.takeLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeLastColsWhile`][String.takeLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[takeLastWhile][ColumnsSelectionDsl.takeLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun ColumnsSelectionDsl<*>.takeLastWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        asSingleColumn().takeLastColsWhile(predicate)

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeLastWhile`][ColumnSet.takeLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeLastWhile`][SingleColumn.takeLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeLastColsWhile`][String.takeLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeLastColsWhile][SingleColumn.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun SingleColumn<DataRow<*>>.takeLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeLastWhile(predicate) }

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeLastWhile`][ColumnSet.takeLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeLastWhile`][SingleColumn.takeLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeLastColsWhile`][String.takeLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeLastColsWhile][String.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun String.takeLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeLastWhile`][ColumnSet.takeLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeLastWhile`][SingleColumn.takeLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeLastColsWhile`][String.takeLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeLastColsWhile][SingleColumn.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeLastColsWhile][KProperty.takeLastColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.takeLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    /**
     * ## Take Last (Cols) While
     * This function takes the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`takeLastWhile`][ColumnSet.takeLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`takeLastWhile`][SingleColumn.takeLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`takeLastColsWhile`][String.takeLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeLastColsWhile][ColumnPath.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun ColumnPath.takeLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    // endregion
}

// endregion
