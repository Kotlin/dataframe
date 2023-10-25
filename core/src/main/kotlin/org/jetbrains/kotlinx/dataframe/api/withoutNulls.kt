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
     * @include [UsageTemplate]
     * {@setArg [UsageTemplate.DefinitionsArg]
     *  {@include [UsageTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnGroupDef]}
     * }
     *
     * {@setArg [UsageTemplate.PlainDslFunctionsArg]
     *  {@include [PlainDslName]}**`()`**
     * }
     *
     * {@setArg [UsageTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [ColumnSetName]}**`()`**
     * }
     *
     * {@setArg [UsageTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [ColumnGroupName]}**`()`**
     * }
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
     * See [Usage] for how to use these functions.
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
     * @include [CommonWithoutNullsDocs]
     * @setArg [CommonWithoutNullsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[withoutNulls][ColumnSet.withoutNulls]`() }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C?>.withoutNulls(): ColumnSet<C & Any> =
        transform { cols -> cols.filter { !it.hasNulls() } } as ColumnSet<C & Any>

    /**
     * @include [CommonWithoutNullsDocs]
     * @setArg [CommonWithoutNullsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[withoutNulls][ColumnsSelectionDsl.colsWithoutNulls]`() }`
     */
    public fun ColumnsSelectionDsl<*>.withoutNulls(): ColumnSet<Any> =
        asSingleColumn().colsWithoutNulls()

    /**
     * @include [CommonWithoutNullsDocs]
     * @setArg [CommonWithoutNullsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsWithoutNulls][SingleColumn.colsWithoutNulls]`() }`
     */
    public fun SingleColumn<DataRow<*>>.colsWithoutNulls(): ColumnSet<Any> =
        ensureIsColumnGroup().allColumnsInternal().withoutNulls()

    /**
     * @include [CommonWithoutNullsDocs]
     * @setArg [CommonWithoutNullsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsWithoutNulls][String.colsWithoutNulls]`() }`
     */
    public fun String.colsWithoutNulls(): ColumnSet<Any> =
        columnGroup(this).colsWithoutNulls()

    /**
     * @include [CommonWithoutNullsDocs]
     * @setArg [CommonWithoutNullsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[colsWithoutNulls][KProperty.colsWithoutNulls]`() }`
     */
    public fun KProperty<*>.colsWithoutNulls(): ColumnSet<Any> =
        columnGroup(this).colsWithoutNulls()

    /**
     * @include [CommonWithoutNullsDocs]
     * @setArg [CommonWithoutNullsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[colsWithoutNulls][ColumnPath.colsWithoutNulls]`() }`
     */
    public fun ColumnPath.colsWithoutNulls(): ColumnSet<Any> =
        columnGroup(this).colsWithoutNulls()
}

// endregion
