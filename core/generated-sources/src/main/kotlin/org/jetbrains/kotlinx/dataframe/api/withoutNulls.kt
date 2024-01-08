package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * See [Usage]
 */
public interface WithoutNullsColumnsSelectionDsl {

    /**
     * ## (Cols) Without Nulls Usage
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `columnSet: `[ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[String][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * `| `[KProperty][kotlin.reflect.KProperty]`<* | `[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>>` | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**withoutNulls**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnSet][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**withoutNulls**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a column group reference:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnGroup][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**colsWithoutNulls**][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.colsWithoutNulls]**`()`**
     *
     *
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     *
     *
     *
     *
     *
     */
    public interface Usage {

        /** [**withoutNulls**][ColumnsSelectionDsl.withoutNulls] */
        public interface PlainDslName

        /** .[**withoutNulls**][ColumnsSelectionDsl.withoutNulls] */
        public interface ColumnSetName

        /** .[**colsWithoutNulls**][ColumnsSelectionDsl.colsWithoutNulls] */
        public interface ColumnGroupName
    }

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Usage]
     *
     * #### For Example:
     *
     * `df.`[select][DataFrame.select]` { `[all][ColumnsSelectionDsl.all]`().`[nameContains][ColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[withoutNulls][ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][DataFrame.select]` { Type::userData.`[colsWithoutNulls][SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    private interface CommonWithoutNullsDocs {

        interface ExampleArg
    }

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[colsWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C?>.withoutNulls(): ColumnSet<C & Any> =
        transform { cols -> cols.filter { !it.hasNulls() } } as ColumnSet<C & Any>

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[colsWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun ColumnsSelectionDsl<*>.withoutNulls(): ColumnSet<Any> =
        asSingleColumn().colsWithoutNulls()

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[colsWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun SingleColumn<DataRow<*>>.colsWithoutNulls(): ColumnSet<Any> =
        ensureIsColumnGroup().allColumnsInternal().withoutNulls()

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[colsWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun String.colsWithoutNulls(): ColumnSet<Any> =
        columnGroup(this).colsWithoutNulls()

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[colsWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun KProperty<*>.colsWithoutNulls(): ColumnSet<Any> =
        columnGroup(this).colsWithoutNulls()

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[colsWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun ColumnPath.colsWithoutNulls(): ColumnSet<Any> =
        columnGroup(this).colsWithoutNulls()
}

// endregion
