package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl
public interface WithoutNullsColumnsSelectionDsl {

    /**
     * ## (Children) Without Nulls
     * Returns a new [ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][ColumnGroup], `withoutNulls` is named `childrenWithoutNulls` to avoid confusion.
     *
     * #### For Example:
     *
     * `df.`[select][DataFrame.select]` { `[all][ColumnsSelectionDsl.all]`().`[nameContains][ColumnsSelectionDsl.childrenNameContains]`("middleName").`[withoutNulls][ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[withoutNulls][ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::userData).`[childrenWithoutNulls][SingleColumn.childrenWithoutNulls]`() }`
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
     * ## (Children) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `childrenWithoutNulls` to avoid confusion.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.childrenNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::userData).`[childrenWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenWithoutNulls]`() }`
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
     * ## (Children) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `childrenWithoutNulls` to avoid confusion.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.childrenNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::userData).`[childrenWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun ColumnsSelectionDsl<*>.withoutNulls(): ColumnSet<Any> =
        asSingleColumn().childrenWithoutNulls()

    /**
     * ## (Children) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `childrenWithoutNulls` to avoid confusion.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.childrenNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::userData).`[childrenWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun SingleColumn<DataRow<*>>.childrenWithoutNulls(): ColumnSet<Any> =
        ensureIsColGroup().allColumnsInternal().withoutNulls()

    /**
     * ## (Children) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `childrenWithoutNulls` to avoid confusion.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.childrenNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::userData).`[childrenWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun String.childrenWithoutNulls(): ColumnSet<Any> =
        columnGroup(this).childrenWithoutNulls()

    /**
     * ## (Children) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `childrenWithoutNulls` to avoid confusion.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.childrenNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::userData).`[childrenWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun KProperty<DataRow<*>>.childrenWithoutNulls(): ColumnSet<Any> =
        columnGroup(this).childrenWithoutNulls()

    /**
     * ## (Children) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `childrenWithoutNulls` to avoid confusion.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.childrenNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::userData).`[childrenWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun ColumnPath.childrenWithoutNulls(): ColumnSet<Any> =
        columnGroup(this).childrenWithoutNulls()
}
// endregion
