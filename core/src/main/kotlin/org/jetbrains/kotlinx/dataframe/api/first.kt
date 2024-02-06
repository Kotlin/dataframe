package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.CommonFirstDocs.Examples
import org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar
import org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar.PlainDslName
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
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableSingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.singleOrNullWithTransformerImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import java.io.File
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

/**
 * ## Submit Number
 * Submits the given number.
 *
 * ### For example
 * {@comment Giving an example of how the function can be called, default the argument to `5.0`}
 * ```kotlin
 * MyClass().submit(${[SubmitDocs.ExampleArg]=5.0}, File("file.json")) { println(it) }
 * ```
 *
 * ### Result
 * The number will be submitted to a JSON file like this:
 * ```json
 * {@includeFile (./submitted.json)}
 * ```
 * @get [ExtraInfoArg] {@comment Attempt to retrieve the [ExtraInfoArg] variable}
 * $[ParamArg] {@comment Attempt to retrieve the [ParamArg] variable using shorter notation}
 * @param location The file location to submit the number to.
 * @param onException What to do when an exception occurs.
 * @return `true` if the number was submitted successfully, `false` otherwise.
 */
@ExcludeFromSources
private interface SubmitDocs {

    /* Example argument, defaults to 5.0 */
    interface ExampleArg

    /* Optional extra info */
    interface ExtraInfoArg

    /* The param part */
    interface ParamArg
}

/**
 * @include [SubmitDocs]
 * @set [SubmitDocs.ParamArg] @param [number] The [Int] to submit.
 * @set [SubmitDocs.ExampleArg] 5{@comment Overriding the default example argument}
 * @comment While You can use block tags for multiline comments, most of the time, inline tags are clearer:
 * {@set [SubmitDocs.ExtraInfoArg]
 *  ### This function can also be used from Java:
 *  {@sample [Submitting.sample]}
 * }
 */
public fun submit(number: Int, location: File, onException: (e: Exception) -> Unit): Boolean =
    TODO() @Suppress("UNUSED_PARAMETER")

/** @include [SubmitDocs] {@set [SubmitDocs.ParamArg] @param [number] The [Double] to submit.} */
public fun submit(number: Double, location: File, onException: (e: Exception) -> Unit): Boolean = TODO()

// region ColumnsSelectionDsl

/**
 * ## First (Col) {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface FirstColumnsSelectionDsl {

    /**
     * ## First (Col) Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DefinitionsArg]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ConditionDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PlainDslFunctionsArg]
     *  {@include [PlainDslName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     * }
     *
     * {@set [DslGrammarTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [ColumnSetName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     * }
     *
     * {@set [DslGrammarTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [ColumnGroupName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     * }
     */
    public interface Grammar {

        /** [**first**][ColumnsSelectionDsl.first] */
        @ExcludeFromSources
        public interface PlainDslName

        /** .[**first**][ColumnsSelectionDsl.first] */
        @ExcludeFromSources
        public interface ColumnSetName

        /** .[**firstCol**][ColumnsSelectionDsl.firstCol] */
        @ExcludeFromSources
        public interface ColumnGroupName
    }

    /**
     * ## First (Col)
     *
     * Returns the first column from [this\] that adheres to the optional given [condition\].
     * If no column adheres to the given [condition\], [NoSuchElementException] is thrown.
     *
     * This function only looks at columns at the top-level.
     *
     * NOTE: For [column groups][ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][DataFrame.select]` { `[first][ColumnsSelectionDsl.first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[firstCol][String.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [Examples]}
     *
     * @param [condition\] The optional [ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn] containing the first column
     *   that adheres to the given [condition\].
     * @throws [NoSuchElementException\] if no column adheres to the given [condition\].
     * @see [ColumnsSelectionDsl.last\]
     */
    @ExcludeFromSources
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
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[firstCol][KProperty.firstCol]`() }`
     */
    public fun KProperty<*>.firstCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
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
