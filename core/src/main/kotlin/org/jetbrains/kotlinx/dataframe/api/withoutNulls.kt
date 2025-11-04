package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Without Nulls {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface WithoutNullsColumnsSelectionDsl {

    /**
     * ## (Cols) Without Nulls Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}**`()`**
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}**`()`**
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnGroupName]}**`()`**
     * }
     */
    public interface Grammar {

        /** [**`withoutNulls`**][ColumnsSelectionDsl.withoutNulls] */
        public typealias PlainDslName = Nothing

        /** __`.`__[**`withoutNulls`**][ColumnsSelectionDsl.withoutNulls] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[**`colsWithoutNulls`**][ColumnsSelectionDsl.colsWithoutNulls] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet] that contains only columns in [this\] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][DataFrame.select]`  {  `[all][ColumnsSelectionDsl.all]`().`[nameContains][ColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][DataFrame.select]`  {  `[withoutNulls][ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][DataFrame.select]` { Type::userData.`[colsWithoutNulls][SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@get [CommonWithoutNullsDocs.EXAMPLE]]}
     *
     * @return A [ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    private interface CommonWithoutNullsDocs {

        typealias EXAMPLE = Nothing
    }

    /**
     * @include [CommonWithoutNullsDocs]
     * @set [CommonWithoutNullsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[withoutNulls][ColumnSet.withoutNulls]`() }`
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("WithoutNulls0")
    public fun <C> ColumnSet<C?>.withoutNulls(): ColumnSet<C & Any> =
        transform { cols -> cols.filter { !it.hasNulls() } } as ColumnSet<C & Any>

    /**
     * @include [CommonWithoutNullsDocs]
     * @set [CommonWithoutNullsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[withoutNulls][ColumnsSelectionDsl.colsWithoutNulls]`() }`
     */
    @Interpretable("WithoutNulls1")
    public fun ColumnsSelectionDsl<*>.withoutNulls(): ColumnSet<Any> = asSingleColumn().colsWithoutNulls()

    /**
     * @include [CommonWithoutNullsDocs]
     * @set [CommonWithoutNullsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsWithoutNulls][SingleColumn.colsWithoutNulls]`() }`
     */
    @Interpretable("WithoutNulls2")
    public fun SingleColumn<DataRow<*>>.colsWithoutNulls(): ColumnSet<Any> =
        ensureIsColumnGroup().allColumnsInternal().withoutNulls()

    /**
     * @include [CommonWithoutNullsDocs]
     * @set [CommonWithoutNullsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsWithoutNulls][String.colsWithoutNulls]`() }`
     */
    public fun String.colsWithoutNulls(): ColumnSet<Any> = columnGroup(this).colsWithoutNulls()

    /**
     * @include [CommonWithoutNullsDocs]
     * @set [CommonWithoutNullsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[colsWithoutNulls][KProperty.colsWithoutNulls]`() }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsWithoutNulls(): ColumnSet<Any> = columnGroup(this).colsWithoutNulls()

    /**
     * @include [CommonWithoutNullsDocs]
     * @set [CommonWithoutNullsDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[colsWithoutNulls][ColumnPath.colsWithoutNulls]`() }`
     */
    public fun ColumnPath.colsWithoutNulls(): ColumnSet<Any> = columnGroup(this).colsWithoutNulls()
}

// endregion
