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
     * {@includeArg [CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    private interface CommonWithoutNullsDocs {

        interface ExampleArg
    }

    /**
     * @include [CommonWithoutNullsDocs]
     * @arg [CommonWithoutNullsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[withoutNulls][ColumnSet.withoutNulls]`() }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C?>.withoutNulls(): ColumnSet<C & Any> =
        transform { cols -> cols.filter { !it.hasNulls() } } as ColumnSet<C & Any>

    /**
     * @include [CommonWithoutNullsDocs]
     * @arg [CommonWithoutNullsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[withoutNulls][ColumnsSelectionDsl.childrenWithoutNulls]`() }`
     */
    public fun ColumnsSelectionDsl<*>.withoutNulls(): ColumnSet<Any> =
        asSingleColumn().childrenWithoutNulls()

    /**
     * @include [CommonWithoutNullsDocs]
     * @arg [CommonWithoutNullsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[childrenWithoutNulls][SingleColumn.childrenWithoutNulls]`() }`
     */
    public fun SingleColumn<DataRow<*>>.childrenWithoutNulls(): ColumnSet<Any> =
        ensureIsColGroup().allColumnsInternal().withoutNulls()

    /**
     * @include [CommonWithoutNullsDocs]
     * @arg [CommonWithoutNullsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[childrenWithoutNulls][String.childrenWithoutNulls]`() }`
     */
    public fun String.childrenWithoutNulls(): ColumnSet<Any> =
        columnGroup(this).childrenWithoutNulls()

    /**
     * @include [CommonWithoutNullsDocs]
     * @arg [CommonWithoutNullsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[childrenWithoutNulls][SingleColumn.childrenWithoutNulls]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[childrenWithoutNulls][KProperty.childrenWithoutNulls]`() }`
     */
    public fun KProperty<DataRow<*>>.childrenWithoutNulls(): ColumnSet<Any> =
        columnGroup(this).childrenWithoutNulls()

    /**
     * @include [CommonWithoutNullsDocs]
     * @arg [CommonWithoutNullsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[childrenWithoutNulls][ColumnPath.childrenWithoutNulls]`() }`
     */
    public fun ColumnPath.childrenWithoutNulls(): ColumnSet<Any> =
        columnGroup(this).childrenWithoutNulls()
}
// endregion
