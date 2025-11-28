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

public fun <T> DataColumn<T>.first(): T = get(0)

public fun <T> DataColumn<T>.firstOrNull(): T? = if (size > 0) first() else null

public fun <T> DataColumn<T>.first(predicate: (T) -> Boolean): T = values.first(predicate)

public fun <T> DataColumn<T>.firstOrNull(predicate: (T) -> Boolean): T? = values.firstOrNull(predicate)

// endregion

// region DataFrame

public fun <T> DataFrame<T>.first(): DataRow<T> {
    if (nrow == 0) {
        throw NoSuchElementException("DataFrame has no rows. Use `firstOrNull`.")
    }
    return get(0)
}

public fun <T> DataFrame<T>.firstOrNull(): DataRow<T>? = if (nrow > 0) first() else null

public inline fun <T> DataFrame<T>.first(predicate: RowFilter<T>): DataRow<T> =
    rows().first {
        predicate(it, it)
    }

public inline fun <T> DataFrame<T>.firstOrNull(predicate: RowFilter<T>): DataRow<T>? =
    rows().firstOrNull {
        predicate(it, it)
    }

// endregion

// region GroupBy

@Interpretable("GroupByReducePredicate")
public fun <T, G> GroupBy<T, G>.first(): ReducedGroupBy<T, G> = reduce { firstOrNull() }

@Interpretable("GroupByReducePredicate")
public fun <T, G> GroupBy<T, G>.first(predicate: RowFilter<G>): ReducedGroupBy<T, G> = reduce { firstOrNull(predicate) }

// endregion

// region Pivot

public fun <T> Pivot<T>.first(): ReducedPivot<T> = reduce { firstOrNull() }

public fun <T> Pivot<T>.first(predicate: RowFilter<T>): ReducedPivot<T> = reduce { firstOrNull(predicate) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.first(): ReducedPivotGroupBy<T> = reduce { firstOrNull() }

public fun <T> PivotGroupBy<T>.first(predicate: RowFilter<T>): ReducedPivotGroupBy<T> =
    reduce { firstOrNull(predicate) }

// endregion

// region ColumnsSelectionDsl

/**
 * ## First (Col) [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface FirstColumnsSelectionDsl {

    /**
     * ## First (Col) Grammar
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
     *  [**`first`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`first`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`firstCol`**][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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

        /** [**`first`**][ColumnsSelectionDsl.first] */
        public interface PlainDslName

        /** __`.`__[**`first`**][ColumnsSelectionDsl.first] */
        public interface ColumnSetName

        /** __`.`__[**`firstCol`**][ColumnsSelectionDsl.firstCol] */
        public interface ColumnGroupName
    }

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][DataFrame.select]`  {  `[first][ColumnsSelectionDsl.first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[firstCol][String.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [condition] The optional [ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    private interface CommonFirstDocs {

        /** Examples key */
        interface Examples
    }

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[firstCol][kotlin.String.firstCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[first][ColumnSet.first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[first][ColumnSet.first]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("First0")
    public fun <C> ColumnSet<C>.first(condition: ColumnFilter<C> = { true }): SingleColumn<C> =
        (allColumnsInternal() as TransformableColumnSet<C>)
            .transform { listOf(it.first(condition)) }
            .singleOrNullWithTransformerImpl()

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[firstCol][kotlin.String.firstCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[first][ColumnsSelectionDsl.first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    @Interpretable("First1")
    public fun ColumnsSelectionDsl<*>.first(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        asSingleColumn().firstCol(condition)

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[firstCol][kotlin.String.firstCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[firstCol][SingleColumn.firstCol]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    @Interpretable("First2")
    public fun SingleColumn<DataRow<*>>.firstCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        this.ensureIsColumnGroup().asColumnSet().first(condition)

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[firstCol][kotlin.String.firstCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[firstCol][String.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    public fun String.firstCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).firstCol(condition)

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[firstCol][kotlin.String.firstCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[firstCol][SingleColumn.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[firstCol][KProperty.firstCol]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.firstCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).firstCol(condition)

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[firstCol][kotlin.String.firstCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[firstCol][ColumnPath.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    public fun ColumnPath.firstCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).firstCol(condition)
}

// endregion
