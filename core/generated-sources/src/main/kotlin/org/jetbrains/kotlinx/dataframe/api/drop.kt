package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
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

public inline fun <T> DataColumn<T>.drop(predicate: Predicate<T>): DataColumn<T> = filter { !predicate(it) }

public fun <T> DataColumn<T>.drop(n: Int): DataColumn<T> =
    when {
        n == 0 -> this
        n >= size -> get(emptyList())
        else -> get(n until size)
    }

public fun <T> DataColumn<T>.dropLast(n: Int = 1): DataColumn<T> = take(size - n)

// endregion

// region DataFrame

/**
 * Returns a DataFrame containing all rows except first [n] rows.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.drop(n: Int): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return getRows(n.coerceAtMost(nrow) until nrow)
}

/**
 * Returns a DataFrame containing all rows except last [n] rows.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.dropLast(n: Int = 1): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return take((nrow - n).coerceAtLeast(0))
}

/**
 * Returns a DataFrame containing all rows except rows that satisfy the given [predicate].
 */
public inline fun <T> DataFrame<T>.drop(predicate: RowFilter<T>): DataFrame<T> = filter { !predicate(it, it) }

/**
 * Returns a DataFrame containing all rows except first rows that satisfy the given [predicate].
 */
public inline fun <T> DataFrame<T>.dropWhile(predicate: RowFilter<T>): DataFrame<T> =
    firstOrNull { !predicate(it, it) }?.let { drop(it.index()) } ?: this

// endregion

// region ColumnsSelectionDsl

/**
 * ## Drop [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface DropColumnsSelectionDsl {

    /**
     * ## Drop (Last) (Cols) (While) Grammar
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
     *  [**`drop`**][ColumnsSelectionDsl.drop]`(`[**`Last`**][ColumnsSelectionDsl.dropLast]`)`**`(`**[`number`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     *  `| `[**`drop`**][ColumnsSelectionDsl.dropWhile]`(`[**`Last`**][ColumnsSelectionDsl.dropLastWhile]`)`[**`While`**][ColumnsSelectionDsl.dropWhile]**`  {  `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`drop`**][ColumnsSelectionDsl.drop]`(`[**`Last`**][ColumnSet.dropLast]`)`**`(`**[`number`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[**`drop`**][ColumnsSelectionDsl.dropWhile]`(`[**`Last`**][ColumnsSelectionDsl.dropLastWhile]`)`[**`While`**][ColumnsSelectionDsl.dropWhile]**`  {  `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`drop`**][ColumnsSelectionDsl.dropCols]`(`[**`Last`**][ColumnsSelectionDsl.dropLastCols]`)`[**`Cols`**][ColumnsSelectionDsl.dropCols]**`(`**[`number`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[**`drop`**][ColumnsSelectionDsl.dropColsWhile]`(`[**`Last`**][ColumnsSelectionDsl.dropLastColsWhile]`)`[**`ColsWhile`**][ColumnsSelectionDsl.dropColsWhile]**`  {  `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
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

        /** [**`drop`**][ColumnsSelectionDsl.drop]`(`[**`Last`**][ColumnsSelectionDsl.dropLast]`)` */
        public interface PlainDslName

        /** __`.`__[**`drop`**][ColumnsSelectionDsl.drop]`(`[**`Last`**][ColumnSet.dropLast]`)` */
        public interface ColumnSetName

        /** __`.`__[**`drop`**][ColumnsSelectionDsl.dropCols]`(`[**`Last`**][ColumnsSelectionDsl.dropLastCols]`)`[**`Cols`**][ColumnsSelectionDsl.dropCols] */
        public interface ColumnGroupName

        /** [**`drop`**][ColumnsSelectionDsl.dropWhile]`(`[**`Last`**][ColumnsSelectionDsl.dropLastWhile]`)`[**`While`**][ColumnsSelectionDsl.dropWhile] */
        public interface PlainDslWhileName

        /** __`.`__[**`drop`**][ColumnsSelectionDsl.dropWhile]`(`[**`Last`**][ColumnsSelectionDsl.dropLastWhile]`)`[**`While`**][ColumnsSelectionDsl.dropWhile] */
        public interface ColumnSetWhileName

        /** __`.`__[**`drop`**][ColumnsSelectionDsl.dropColsWhile]`(`[**`Last`**][ColumnsSelectionDsl.dropLastColsWhile]`)`[**`ColsWhile`**][ColumnsSelectionDsl.dropColsWhile] */
        public interface ColumnGroupWhileName
    }

    // region drop

    /**
     * ## Drop (Cols)
     * This drops the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropCols][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropCols][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    private interface CommonDropFirstDocs

    /**
     * ## Drop (Cols)
     * This drops the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropCols][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropCols][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[drop][ColumnSet.drop]`(2) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[drop][ColumnSet.drop]`(2) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    @Interpretable("Drop0")
    public fun <C> ColumnSet<C>.drop(n: Int): ColumnSet<C> = transform { it.drop(n) }

    /**
     * ## Drop (Cols)
     * This drops the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropCols][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropCols][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[drop][ColumnsSelectionDsl.drop]`(5) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    @Interpretable("Drop1")
    public fun ColumnsSelectionDsl<*>.drop(n: Int): ColumnSet<*> = asSingleColumn().dropCols(n)

    /**
     * ## Drop (Cols)
     * This drops the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropCols][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropCols][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropCols][SingleColumn.dropCols]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    @Interpretable("Drop2")
    public fun SingleColumn<DataRow<*>>.dropCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().drop(n) }

    /**
     * ## Drop (Cols)
     * This drops the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropCols][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropCols][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropCols][String.dropCols]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun String.dropCols(n: Int): ColumnSet<*> = columnGroup(this).dropCols(n)

    /**
     * ## Drop (Cols)
     * This drops the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropCols][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropCols][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropCols][KProperty.dropCols]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.dropCols(n: Int): ColumnSet<*> = columnGroup(this).dropCols(n)

    /**
     * ## Drop (Cols)
     * This drops the first [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropCols][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropCols][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropCols][ColumnPath.dropCols]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun ColumnPath.dropCols(n: Int): ColumnSet<*> = columnGroup(this).dropCols(n)

    // endregion

    // region dropLast

    /**
     * ## Drop Last (Cols)
     * This drops the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastCols][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastCols][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    private interface CommonDropLastDocs

    /**
     * ## Drop Last (Cols)
     * This drops the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastCols][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastCols][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropLast][ColumnSet.dropLast]`(2) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropLast][ColumnSet.dropLast]`() }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    @Interpretable("DropLast0")
    public fun <C> ColumnSet<C>.dropLast(n: Int = 1): ColumnSet<C> = transform { it.dropLast(n) }

    /**
     * ## Drop Last (Cols)
     * This drops the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastCols][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastCols][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[dropLast][ColumnsSelectionDsl.dropLast]`(5) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    @Interpretable("DropLast1")
    public fun ColumnsSelectionDsl<*>.dropLast(n: Int = 1): ColumnSet<*> = this.asSingleColumn().dropLastCols(n)

    /**
     * ## Drop Last (Cols)
     * This drops the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastCols][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastCols][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropLastCols][SingleColumn.dropLastCols]`() }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    @Interpretable("DropLast2")
    public fun SingleColumn<DataRow<*>>.dropLastCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().dropLast(n) }

    /**
     * ## Drop Last (Cols)
     * This drops the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastCols][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastCols][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropLastCols][String.dropLastCols]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun String.dropLastCols(n: Int): ColumnSet<*> = columnGroup(this).dropLastCols(n)

    /**
     * ## Drop Last (Cols)
     * This drops the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastCols][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastCols][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropLastCols][KProperty.dropLastCols]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.dropLastCols(n: Int): ColumnSet<*> = columnGroup(this).dropLastCols(n)

    /**
     * ## Drop Last (Cols)
     * This drops the last [n] columns from [this] collecting
     * the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastCols][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastCols][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropLastCols][ColumnPath.dropLastCols]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun ColumnPath.dropLastCols(n: Int): ColumnSet<*> = columnGroup(this).dropLastCols(n)

    // endregion

    // region dropWhile

    /**
     * ## Drop (Cols) While
     * This function drops the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropWhile`][ColumnSet.dropWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropWhile`][SingleColumn.dropColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropColsWhile`][String.dropColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    private interface CommonDropWhileDocs

    /**
     * ## Drop (Cols) While
     * This function drops the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropWhile`][ColumnSet.dropWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropWhile`][SingleColumn.dropColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropColsWhile`][String.dropColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropWhile][ColumnSet.dropWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropWhile][ColumnSet.dropWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun <C> ColumnSet<C>.dropWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.dropWhile(predicate) }

    /**
     * ## Drop (Cols) While
     * This function drops the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropWhile`][ColumnSet.dropWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropWhile`][SingleColumn.dropColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropColsWhile`][String.dropColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[dropWhile][ColumnsSelectionDsl.dropWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun ColumnsSelectionDsl<*>.dropWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.asSingleColumn().dropColsWhile(predicate)

    /**
     * ## Drop (Cols) While
     * This function drops the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropWhile`][ColumnSet.dropWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropWhile`][SingleColumn.dropColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropColsWhile`][String.dropColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropColsWhile][SingleColumn.dropColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun SingleColumn<DataRow<*>>.dropColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().dropWhile(predicate) }

    /**
     * ## Drop (Cols) While
     * This function drops the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropWhile`][ColumnSet.dropWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropWhile`][SingleColumn.dropColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropColsWhile`][String.dropColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropColsWhile][String.dropColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun String.dropColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropColsWhile(predicate)

    /**
     * ## Drop (Cols) While
     * This function drops the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropWhile`][ColumnSet.dropWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropWhile`][SingleColumn.dropColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropColsWhile`][String.dropColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropColsWhile][KProperty.dropColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.dropColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropColsWhile(predicate)

    /**
     * ## Drop (Cols) While
     * This function drops the first columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropWhile`][ColumnSet.dropWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropWhile`][SingleColumn.dropColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropColsWhile`][String.dropColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropColsWhile][ColumnPath.dropColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun ColumnPath.dropColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropColsWhile(predicate)

    // endregion

    // region dropLastWhile

    /**
     * ## Drop Last (Cols) While
     * This function drops the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropLastWhile`][ColumnSet.dropLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropLastWhile`][SingleColumn.dropLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropLastColsWhile`][String.dropLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    private interface CommonDropLastWhileDocs

    /**
     * ## Drop Last (Cols) While
     * This function drops the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropLastWhile`][ColumnSet.dropLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropLastWhile`][SingleColumn.dropLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropLastColsWhile`][String.dropLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropLastWhile][ColumnSet.dropLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropLastWhile][ColumnSet.dropLastWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun <C> ColumnSet<C>.dropLastWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.dropLastWhile(predicate) }

    /**
     * ## Drop Last (Cols) While
     * This function drops the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropLastWhile`][ColumnSet.dropLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropLastWhile`][SingleColumn.dropLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropLastColsWhile`][String.dropLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[dropLastWhile][ColumnsSelectionDsl.dropLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun ColumnsSelectionDsl<*>.dropLastWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.asSingleColumn().dropLastColsWhile(predicate)

    /**
     * ## Drop Last (Cols) While
     * This function drops the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropLastWhile`][ColumnSet.dropLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropLastWhile`][SingleColumn.dropLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropLastColsWhile`][String.dropLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropLastColsWhile][SingleColumn.dropLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun SingleColumn<DataRow<*>>.dropLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().dropLastWhile(predicate) }

    /**
     * ## Drop Last (Cols) While
     * This function drops the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropLastWhile`][ColumnSet.dropLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropLastWhile`][SingleColumn.dropLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropLastColsWhile`][String.dropLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropLastColsWhile][String.dropLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun String.dropLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropLastColsWhile(predicate)

    /**
     * ## Drop Last (Cols) While
     * This function drops the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropLastWhile`][ColumnSet.dropLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropLastWhile`][SingleColumn.dropLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropLastColsWhile`][String.dropLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[dropLastColsWhile][SingleColumn.dropLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropLastColsWhile][KProperty.dropLastColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.dropLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropLastColsWhile(predicate)

    /**
     * ## Drop Last (Cols) While
     * This function drops the last columns from [this] adhering to the
     * given [predicate] collecting the result into a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * ### Check out: [Usage]
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[`dropLastWhile`][ColumnSet.dropLastWhile]` { "my" `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`dropLastWhile`][SingleColumn.dropLastColsWhile]` { it.`[`any`][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`dropLastColsWhile`][String.dropLastColsWhile]` { it.`[`kind`][ColumnWithPath.kind]`() == `[`ColumnKind.Value`][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropLastColsWhile][ColumnPath.dropLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun ColumnPath.dropLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropLastColsWhile(predicate)

    // endregion
}

// endregion
