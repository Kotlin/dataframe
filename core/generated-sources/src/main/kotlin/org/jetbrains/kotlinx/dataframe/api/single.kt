package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.CommonSingleDocs.Examples
import org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar
import org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar.PlainDslName
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableSingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.singleOrNullWithTransformerImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.nrow
import kotlin.reflect.KProperty

// region DataColumn

public fun <C> DataColumn<C>.single(): C = values.single()

// endregion

// region DataFrame

public fun <T> DataFrame<T>.single(): DataRow<T> =
    when (nrow) {
        0 -> throw NoSuchElementException("DataFrame has no rows. Use `singleOrNull`.")
        1 -> get(0)
        else -> throw IllegalArgumentException("DataFrame has more than one row.")
    }

public fun <T> DataFrame<T>.singleOrNull(): DataRow<T>? = rows().singleOrNull()

public fun <T> DataFrame<T>.single(predicate: RowExpression<T, Boolean>): DataRow<T> =
    rows().single { predicate(it, it) }

public fun <T> DataFrame<T>.singleOrNull(predicate: RowExpression<T, Boolean>): DataRow<T>? =
    rows().singleOrNull { predicate(it, it) }

// endregion

// region ColumnsSelectionDsl

/**
 * ## Single (Col) [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface SingleColumnsSelectionDsl {

    /**
     * ## Single (Col) Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[String][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * `| `[KProperty][kotlin.reflect.KProperty]`<* | `[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**single**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`** `]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnSet][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;**`.`**[**single**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`** `]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [Column Group (reference)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnGroup][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;**`.`**[**singleCol**][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.singleCol]` [` **`{ `**[condition][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`** `]`
     *
     *
     *
     *
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

        /** [**single**][ColumnsSelectionDsl.single] */
        public interface PlainDslName

        /** **`.`**[**single**][ColumnsSelectionDsl.single] */
        public interface ColumnSetName

        /** **`.`**[**singleCol**][ColumnsSelectionDsl.singleCol] */
        public interface ColumnGroupName
    }

    /**
     * ## Single (Col)
     * Returns the single column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     * If multiple columns adhere to it, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][DataFrame.select]` { `[single][ColumnsSelectionDsl.single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[singleCol][String.singleCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [condition] The optional [ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    private interface CommonSingleDocs {

        /** Examples key */
        interface Examples
    }

    /**
     * ## Single (Col)
     * Returns the single column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     * If multiple columns adhere to it, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[singleCol][kotlin.String.singleCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[single][ColumnSet.single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[single][ColumnSet.single]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    public fun <C> ColumnSet<C>.single(condition: ColumnFilter<C> = { true }): TransformableSingleColumn<C> =
        singleInternal(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     * If multiple columns adhere to it, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[singleCol][kotlin.String.singleCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[single][ColumnsSelectionDsl.single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    public fun ColumnsSelectionDsl<*>.single(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        asSingleColumn().singleCol(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     * If multiple columns adhere to it, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[singleCol][kotlin.String.singleCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[singleCol][SingleColumn.singleCol]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    public fun SingleColumn<DataRow<*>>.singleCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        this.ensureIsColumnGroup().asColumnSet().single(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     * If multiple columns adhere to it, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[singleCol][kotlin.String.singleCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[singleCol][String.singleCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    public fun String.singleCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        columnGroup(this).singleCol(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     * If multiple columns adhere to it, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[singleCol][kotlin.String.singleCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[singleCol][SingleColumn.singleCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[singleCol][KProperty.singleCol]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    public fun KProperty<*>.singleCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        columnGroup(this).singleCol(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     * If multiple columns adhere to it, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[singleCol][kotlin.String.singleCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[singleCol][ColumnPath.singleCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    public fun ColumnPath.singleCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        columnGroup(this).singleCol(condition)
}

@Suppress("UNCHECKED_CAST")
internal fun <C> ColumnsResolver<C>.singleInternal(condition: ColumnFilter<C> = { true }): TransformableSingleColumn<C> =
    (allColumnsInternal() as TransformableColumnSet<C>)
        .transform { listOf(it.single(condition)) }
        .singleOrNullWithTransformerImpl()

// endregion
