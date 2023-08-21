package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.CommonFirstDocs.Examples
import org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Usage
import org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Usage.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Usage.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Usage.PlainDslName
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableSingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.singleOrNullWithTransformerImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.nrow
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

public fun <T> DataFrame<T>.first(predicate: RowFilter<T>): DataRow<T> = rows().first { predicate(it, it) }

public fun <T> DataFrame<T>.firstOrNull(predicate: RowFilter<T>): DataRow<T>? = rows().firstOrNull { predicate(it, it) }

// endregion

// region GroupBy

public fun <T, G> GroupBy<T, G>.first(): ReducedGroupBy<T, G> = reduce { firstOrNull() }

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
 * See [Usage].
 */
public interface FirstColumnsSelectionDsl {

    /**
     * ## First (Col) Usage
     *
     * @include [UsageTemplate]
     * {@setArg [UsageTemplate.DefinitionsArg]
     *  {@include [UsageTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ConditionDef]}
     * }
     *
     * {@setArg [UsageTemplate.PlainDslFunctionsArg]
     *  {@include [PlainDslName]}` [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `]`
     * }
     *
     * {@setArg [UsageTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [ColumnSetName]}` [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `]`
     * }
     *
     * {@setArg [UsageTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [ColumnGroupName]}` [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `]`
     * }
     */
    public interface Usage {

        /** [**first**][ColumnsSelectionDsl.first] */
        public interface PlainDslName

        /** .[**first**][ColumnsSelectionDsl.first] */
        public interface ColumnSetName

        /** .[**firstCol**][ColumnsSelectionDsl.firstCol] */
        public interface ColumnGroupName
    }

    /**
     * ## First (Col)
     * Returns the first ([transformable][TransformableSingleColumn]) column in this [ColumnSet] or [ColumnGroup]
     * that adheres to the given [condition\].
     * If no column adheres to the given [condition\], [NoSuchElementException] is thrown.
     *
     * NOTE: For [column groups][ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * Check out [Usage] for how to use [first]/[firstCol].
     *
     * #### Examples:
     *
     * `df.`[select][DataFrame.select]` { `[first][ColumnsSelectionDsl.first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[firstCol][String.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") }.`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [Examples]}
     *
     * @param [condition\] The optional [ColumnFilter] condition that the column must adhere to.
     * @return A ([transformable][TransformableSingleColumn]) [SingleColumn] containing the first column
     *   that adheres to the given [condition\].
     * @throws [NoSuchElementException\] if no column adheres to the given [condition\].
     * @see [ColumnsSelectionDsl.last\]
     */
    private interface CommonFirstDocs {

        /** Examples key */
        interface Examples
    }

    /**
     * @include [CommonFirstDocs]
     * @setArg [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[first][ColumnSet.first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[first][ColumnSet.first]`() }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.first(condition: ColumnFilter<C> = { true }): TransformableSingleColumn<C> =
        (allColumnsInternal() as TransformableColumnSet<C>)
            .transform { listOf(it.first(condition)) }
            .singleOrNullWithTransformerImpl()

    /**
     * @include [CommonFirstDocs]
     * @setArg [CommonFirstDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[first][ColumnsSelectionDsl.first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun ColumnsSelectionDsl<*>.first(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        asSingleColumn().firstCol(condition)

    /**
     * @include [CommonFirstDocs]
     * @setArg [CommonFirstDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[firstCol][SingleColumn.firstCol]`() }`
     */
    public fun SingleColumn<DataRow<*>>.firstCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        this.ensureIsColumnGroup().asColumnSet().first(condition)

    /**
     * @include [CommonFirstDocs]
     * @setArg [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[firstCol][String.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun String.firstCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        columnGroup(this).firstCol(condition)

    /**
     * @include [CommonFirstDocs]
     * @setArg [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[firstCol][SingleColumn.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[firstCol][SingleColumn.firstCol]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[firstCol][KProperty.firstCol]`() }`
     */
    public fun KProperty<*>.firstCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        columnGroup(this).firstCol(condition)

    /**
     * @include [CommonFirstDocs]
     * @setArg [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[firstCol][SingleColumn.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[firstCol][SingleColumn.firstCol]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[firstCol][KProperty.firstCol]`() }`
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("firstKPropertyDataRow")
    public fun KProperty<DataRow<*>>.firstCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        columnGroup(this).firstCol(condition)

    /**
     * @include [CommonFirstDocs]
     * @setArg [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[firstCol][ColumnPath.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun ColumnPath.firstCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        columnGroup(this).firstCol(condition)
}

// endregion
