package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.Usage
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Referring to or expressing column(s) in the selection DSL can be done in several ways corresponding to all
 * [Access APIs][AccessApi]:
 * TODO: [Issue #286](https://github.com/Kotlin/dataframe/issues/286)
 *
 * @include [DocumentationUrls.ColumnSelectors]
 */
private interface CommonColumnSelectionDocs

/**
 * {@comment TODO}
 */
private interface CommonColumnSelectionExamples

/** [Columns Selection DSL][ColumnsSelectionDsl] */
internal interface ColumnsSelectionDslLink

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T> ColumnsSelectionDsl<T>.asSingleColumn(): SingleColumn<DataRow<T>> =
    this as SingleColumn<DataRow<T>>

/** Interface reserved for all extensions of [ColumnsSelectionDslExtension] (and [AtAnyDepthDsl]). */
public sealed interface ColumnsSelectionDslExtension<out T> {
    public val scope: Scope
}

public enum class Scope(public val kClass: KClass<*>) {
    COLUMNS_SELECTION_DSL(ColumnSelectionDsl::class),
    AT_ANY_DEPTH_DSL(AtAnyDepthDsl::class)
}

@PublishedApi
internal val <T> ColumnsSelectionDslExtension<T>.context: ColumnsSelectionDsl<T>
    get() = when (scope) {
        Scope.COLUMNS_SELECTION_DSL -> this as ColumnsSelectionDsl<T>
        Scope.AT_ANY_DEPTH_DSL -> (this as AtAnyDepthDsl<T>).context
    }

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
public annotation class ColumnsSelectionDslMarker

/**
 * @include [CommonColumnSelectionDocs]
 *
 * Can be safely cast to [SingleColumn] across the library. It does not directly
 * implement it for DSL purposes.
 *
 * See [Usage] for the DSL Grammar of the ColumnsSelectionDsl.
 */
@ColumnsSelectionDslMarker
public interface ColumnsSelectionDsl<out T> : /* SingleColumn<DataRow<T>> */
    ColumnSelectionDsl<T>,

    // first {}, firstCol()
    FirstColumnsSelectionDsl<T>,
    // last {}, lastCol()
    LastColumnsSelectionDsl<T>,
    // single {}, singleCol()
    SingleColumnsSelectionDsl<T>,

    // col(name), col(5), [5]
    ColColumnsSelectionDsl<T>,
    // valueCol(name), valueCol(5)
    ValueColColumnsSelectionDsl<T>,
    // frameCol(name), frameCol(5)
    FrameColColumnsSelectionDsl<T>,
    // colGroup(name), colGroup(5)
    ColGroupColumnsSelectionDsl<T>,

    // cols {}, cols(), cols(colA, colB), cols(1, 5), cols(1..5), [{}]
    ColsColumnsSelectionDsl<T>,

    // colA.."colB"
    ColumnRangeColumnsSelectionDsl<T>,

    // valueCols {}, valueCols()
    ValueColsColumnsSelectionDsl<T>,
    // frameCols {}, frameCols()
    FrameColsColumnsSelectionDsl<T>,
    // colGroups {}, colGroups()
    ColGroupsColumnsSelectionDsl<T>,
    // colsOfKind(Value, Frame) {}, colsOfKind(Value, Frame)
    ColsOfKindColumnsSelectionDsl<T>,

    // all(), allAfter(colA), allBefore(colA), allFrom(colA), allUpTo(colA)
    AllColumnsSelectionDsl<T>,
    // .atAnyDepth()
    AtAnyDepthColumnsSelectionDsl<T>,
    // TODO
    AtAnyDepth2ColumnsSelectionDsl<T>,
    // children {}, children()
    ChildrenColumnsSelectionDsl<T>,
    // take(5), takeLastChildren(2), takeLastWhile {}, takeChildrenWhile {}
    TakeColumnsSelectionDsl<T>,
    // drop(5), dropLastChildren(2), dropLastWhile {}, dropChildrenWhile {}
    DropColumnsSelectionDsl<T>,
    // except(), allExcept {}
    ExceptColumnsSelectionDsl<T>,
    // nameContains(""), childrenNameContains(""), nameStartsWith(""), childrenNameEndsWith("")
    ColumnNameFiltersColumnsSelectionDsl<T>,
    // withoutNulls(), childrenWithoutNulls()
    WithoutNullsColumnsSelectionDsl<T>,
    // distinct()
    DistinctColumnsSelectionDsl<T>,
    // none()
    NoneColumnsSelectionDsl<T>,
    // colsOf<>(), colsOf<> {}
    ColsOfColumnsSelectionDsl<T>,
    // roots()
    RootsColumnsSelectionDsl<T>,
    // filter {}, filterChildren {}
    FilterColumnsSelectionDsl<T>,
    // colSet and colB
    AndColumnsSelectionDsl<T>,
    // colA named "colB", colA into "colB"
    RenameColumnsSelectionDsl<T> {

    override val scope: Scope
        get() = Scope.COLUMNS_SELECTION_DSL

    /**
     * ## [ColumnsSelectionDsl] Usage
     *
     * @include [UsageTemplateColumnsSelectionDsl.UsageTemplate]
     *
     * {@setArg [UsageTemplate.DefinitionsArg]
     *  {@include [UsageTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ConditionDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.IndexDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnTypeDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.IndexRangeDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnKindDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnSelectorDef]}
     * }
     * {@comment -------------------------------------------------------------------------------------------- }
     * {@setArg [UsageTemplate.PlainDslFunctionsArg]
     *  `(`
     *  {@include [FirstColumnsSelectionDsl.Usage.PlainDslName]}
     *  `|` {@include [LastColumnsSelectionDsl.Usage.PlainDslName]}
     *  `|` {@include [SingleColumnsSelectionDsl.Usage.PlainDslName]}
     *  `) [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `] [ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  `|` `(`
     *  {@include [ColColumnsSelectionDsl.Usage.PlainDslName]}
     *  `|` {@include [ValueColColumnsSelectionDsl.Usage.PlainDslName]}
     *  `|` {@include [FrameColColumnsSelectionDsl.Usage.PlainDslName]}
     *  `|` {@include [ColGroupColumnsSelectionDsl.Usage.PlainDslName]}
     *  `)[`**`<`**{@include [UsageTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [UsageTemplate.ColumnRef]}` | `{@include [UsageTemplate.IndexRef]}**`)`**
     *
     *  `|` {@include [ColsColumnsSelectionDsl.Usage.PlainDslName]}`[`**`<`**{@include [UsageTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [UsageTemplate.ColumnRef]}`, .. | `{@include [UsageTemplate.IndexRef]}`, .. | `{@include [UsageTemplate.IndexRangeRef]}**`)`**
     *
     *  `|` **`this`**`/`**`it`** [**`[`**][cols]{@include [UsageTemplate.ColumnRef]}`, ..`[**`]`**][cols]
     *
     *  `|` `(` {@include [ColsColumnsSelectionDsl.Usage.PlainDslName]}` [ `**` { `**{@include [UsageTemplate.ConditionRef]}**` \\} `**`] |  `**`this`**`/`**`it`** [**`[`**][cols]**`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`**[**`]`**][cols]` )` `[ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  `|` `(`
     *  {@include [ValueColsColumnsSelectionDsl.Usage.PlainDslName]}
     *  `|` {@include [FrameColsColumnsSelectionDsl.Usage.PlainDslName]}
     *  `|` {@include [ColGroupsColumnsSelectionDsl.Usage.PlainDslName]}
     *  `) [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `] [ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  `|` {@include [ColsOfKindColumnsSelectionDsl.Usage.PlainDslName]}**`(`**{@include [UsageTemplate.ColumnKindRef]}`, ..`**`)`**` [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `]` `[ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  `|` {@include [UsageTemplate.ColumnRef]} {@include [ColumnRangeColumnsSelectionDsl.Usage.PlainDslName]} {@include [UsageTemplate.ColumnRef]}
     *
     *  `|` {@include [AllColumnsSelectionDsl.Usage.PlainDslName]}**`()`**` [ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  `|` **`all`**`(`{@include [AllColumnsSelectionDsl.Usage.Before]}`|`{@include [AllColumnsSelectionDsl.Usage.After]}`|`{@include [AllColumnsSelectionDsl.Usage.From]}`|`{@include [AllColumnsSelectionDsl.Usage.UpTo]}`)` `(` **`(`**{@include [UsageTemplate.ColumnRef]}**`)`** `|` **`{`** {@include [UsageTemplate.ColumnSelectorRef]} **`\\}`** `)`
     *
     *  `|` TODO
     * }
     * {@comment -------------------------------------------------------------------------------------------- }
     * {@setArg [UsageTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}`(`
     *  {@include [FirstColumnsSelectionDsl.Usage.ColumnSetName]}
     *  `|` {@include [LastColumnsSelectionDsl.Usage.ColumnSetName]}
     *  `|` {@include [SingleColumnsSelectionDsl.Usage.ColumnSetName]}
     *  `) [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `] [ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  {@include [Indent]}`|` `(`
     *  {@include [ColColumnsSelectionDsl.Usage.ColumnSetName]}
     *  `|` {@include [ValueColColumnsSelectionDsl.Usage.ColumnSetName]}
     *  `|` {@include [FrameColColumnsSelectionDsl.Usage.ColumnSetName]}
     *  `|` {@include [ColGroupColumnsSelectionDsl.Usage.ColumnSetName]}
     *  `)`**`(`**{@include [UsageTemplate.IndexRef]}**`)`**
     *  `|` [**`[`**][ColumnsSelectionDsl.col]{@include [UsageTemplate.IndexRef]}[**`]`**][ColumnsSelectionDsl.col]
     *
     *  {@include [Indent]}`|` {@include [ColsColumnsSelectionDsl.Usage.ColumnSetName]}**`(`**{@include [UsageTemplate.IndexRef]}`, .. | `{@include [UsageTemplate.IndexRangeRef]}**`)`**
     *
     *  {@include [Indent]}`|` [**`[`**][cols]{@include [UsageTemplate.IndexRef]}`, .. | `{@include [UsageTemplate.IndexRangeRef]}[**`]`**][cols]`
     *
     *  {@include [Indent]}`|` `(` {@include [ColsColumnsSelectionDsl.Usage.ColumnSetName]}` [ `**` { `**{@include [UsageTemplate.ConditionRef]}**` \\} `**`] | `[**`[`**][cols]**`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`**[**`]`**][cols]` )` `[ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  {@include [Indent]}`|` `(`
     *  {@include [ValueColColumnsSelectionDsl.Usage.ColumnSetName]}
     *  `|` {@include [FrameColColumnsSelectionDsl.Usage.ColumnSetName]}
     *  `|` {@include [ColGroupColumnsSelectionDsl.Usage.ColumnSetName]}
     *  `) [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `] [ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  {@include [Indent]}`|` {@include [ColsOfKindColumnsSelectionDsl.Usage.ColumnSetName]}**`(`**{@include [UsageTemplate.ColumnKindRef]}`, ..`**`)`**` [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `]` `[ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  {@include [Indent]}`|` {@include [AllColumnsSelectionDsl.Usage.ColumnSetName]}**`()`**` [ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  {@include [Indent]}`|` .**`all`**`(`{@include [AllColumnsSelectionDsl.Usage.Before]}`|`{@include [AllColumnsSelectionDsl.Usage.After]}`|`{@include [AllColumnsSelectionDsl.Usage.From]}`|`{@include [AllColumnsSelectionDsl.Usage.UpTo]}`)` `(` **`(`**{@include [UsageTemplate.ColumnRef]}**`)`** `|` **`{`** {@include [UsageTemplate.ColumnSelectorRef]} **`\\}`** `)`
     *  TODO debate whether these overloads make sense. They didn't exist in 0.9.0
     *
     *  {@include [Indent]}`|` TODO
     * }
     * {@comment -------------------------------------------------------------------------------------------- }
     * {@setArg [UsageTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}`(`
     *  {@include [FirstColumnsSelectionDsl.Usage.ColumnGroupName]}
     *  `|` {@include [LastColumnsSelectionDsl.Usage.ColumnGroupName]}
     *  `|` {@include [SingleColumnsSelectionDsl.Usage.ColumnGroupName]}
     *  `) [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `] [ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  {@include [Indent]}`| (`
     *  {@include [ColColumnsSelectionDsl.Usage.ColumnGroupName]}
     *  `|` {@include [ValueColColumnsSelectionDsl.Usage.ColumnGroupName]}
     *  `|` {@include [FrameColColumnsSelectionDsl.Usage.ColumnGroupName]}
     *  `|` {@include [ColGroupColumnsSelectionDsl.Usage.ColumnGroupName]}
     *  `)[`**`<`**{@include [UsageTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [UsageTemplate.ColumnRef]}` | `{@include [UsageTemplate.IndexRef]}**`)`**
     *
     *  {@include [Indent]}`|` {@include [ColsColumnsSelectionDsl.Usage.ColumnGroupName]}`[`**`<`**{@include [UsageTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [UsageTemplate.ColumnRef]}`, .. | `{@include [UsageTemplate.IndexRef]}`, .. | `{@include [UsageTemplate.IndexRangeRef]}**`)`**
     *
     *  {@include [Indent]}`|` [**`[`**][cols]{@include [UsageTemplate.ColumnRef]}`, ..`[**`]`**][cols]
     *
     *  {@include [Indent]}`|` `(` {@include [ColsColumnsSelectionDsl.Usage.ColumnGroupName]}` [ `**` { `**{@include [UsageTemplate.ConditionRef]}**` \\} `**`] | `[**`[`**][cols]**`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`**[**`]`**][cols]` )` `[ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  {@include [Indent]}`|` `(`
     *   {@include [ValueColColumnsSelectionDsl.Usage.ColumnGroupName]}
     *   `|` {@include [FrameColColumnsSelectionDsl.Usage.ColumnGroupName]}
     *   `|` {@include [ColGroupColumnsSelectionDsl.Usage.ColumnGroupName]}
     *   `) [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `] [ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  {@include [Indent]}`|` {@include [ColsOfKindColumnsSelectionDsl.Usage.ColumnGroupName]}**`(`**{@include [UsageTemplate.ColumnKindRef]}`, ..`**`)`**` [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `]` `[ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  {@include [Indent]}`|` {@include [AllColumnsSelectionDsl.Usage.ColumnGroupName]}**`()`**` [ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  {@include [Indent]}`|` .**`allCols`**`(`{@include [AllColumnsSelectionDsl.Usage.Before]}`|`{@include [AllColumnsSelectionDsl.Usage.After]}`|`{@include [AllColumnsSelectionDsl.Usage.From]}`|`{@include [AllColumnsSelectionDsl.Usage.UpTo]}`)` `(` **`(`**{@include [UsageTemplate.ColumnRef]}**`)`** `|` **`{`** {@include [UsageTemplate.ColumnSelectorRef]} **`\\}`** `)`
     *
     *  {@include [Indent]}`|` TODO
     * }
     */
    public interface Usage

    /**
     * Invokes the given [ColumnsSelector] using this [ColumnsSelectionDsl].
     */
    public operator fun <C> ColumnsSelector<T, C>.invoke(): ColumnsResolver<C> =
        this(this@ColumnsSelectionDsl, this@ColumnsSelectionDsl)

    // TODO add docs `this { age } / it { age }`
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("invokeColumnsSelector")
    public operator fun <C> invoke(selection: ColumnsSelector<T, C>): ColumnsResolver<C> = selection()

    /**
     * ## Columns by Index Range from List of Columns
     * Helper function to create a [ColumnSet] from a list of columns by specifying a range of indices.
     *
     * {@comment TODO find out usages of this function and add examples}
     */
    public operator fun <C> List<DataColumn<C>>.get(range: IntRange): ColumnSet<C> =
        ColumnsList(subList(range.first, range.last + 1))

    // region select
    // TODO due to String.invoke conflict this cannot be moved out

    /**
     * ## Select from [ColumnGroup]
     *
     * Perform a selection of columns using the {@include [ColumnsSelectionDslLink]} on
     * any [ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][SingleColumn.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol" `[{][String.select]` "colA" and `[expr][ColumnsSelectionDsl.expr]` { 0 } `[}][String.select]` }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[select][ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][DataColumn.asColumnGroup]`()`[() {][SingleColumn.select]` "colA" and "colB" `[}][SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonSelectDocs.ExampleArg]}
     *
     * {@include [LineBreak]}
     *
     * See also [except][ColumnsSelectionDsl.except]/[allExcept][ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector\] The [ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup] to select from.
     * @throws [IllegalArgumentException\] If [this\] is not a [ColumnGroup].
     * @return A [ColumnSet] containing the columns selected by [selector\].
     * @see [SingleColumn.except\]
     */
    private interface CommonSelectDocs {

        interface ExampleArg
    }

    /**
     * @include [CommonSelectDocs]
     * @setArg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][SingleColumn.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup `[{][SingleColumn.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][SingleColumn.select]` }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C, R> SingleColumn<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        createColumnSet { context ->
            this.ensureIsColumnGroup().resolveSingle(context)?.let { col ->
                require(col.isColumnGroup()) {
                    "Column ${col.path} is not a ColumnGroup and can thus not be selected from."
                }

                col.asColumnGroup()
                    .getColumnsWithPaths(selector as ColumnsSelector<*, R>)
                    .map { it.changePath(col.path + it.path) }
            } ?: emptyList()
        }

    /** @include [SingleColumn.select] */
    public operator fun <C, R> SingleColumn<DataRow<C>>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        select(selector)

    /**
     * @include [CommonSelectDocs]
     * @setArg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[select][SingleColumn.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup)`[() `{`][SingleColumn.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[`}`][SingleColumn.select]` }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[select][SingleColumn.select]` { colA `[and][ColumnsSelectionDsl.and]` colB } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup.`[select][KProperty.select]` { colA `[and][ColumnsSelectionDsl.and]` colB } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup `[`{`][KProperty.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[`}`][KProperty.select]` }`
     */
    public fun <C, R> KProperty<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /** @include [KProperty.select] */
    public operator fun <C, R> KProperty<DataRow<C>>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        select(selector)

    /**
     * @include [CommonSelectDocs]
     * @setArg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[select][String.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup" `[{][String.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][String.select]` }`
     */
    public fun <R> String.select(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /** @include [String.select] */
    public operator fun <R> String.invoke(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        select(selector)

    /**
     * @include [CommonSelectDocs]
     * @setArg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[select][ColumnPath.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"] `[{][ColumnPath.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][ColumnPath.select]` }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup").`[select][ColumnPath.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup")`[() {][ColumnPath.select]` someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() `[}][ColumnPath.select]` }`
     */
    public fun <R> ColumnPath.select(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /** @include [ColumnPath.select] */
    public operator fun <R> ColumnPath.invoke(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        select(selector)

    @Deprecated(
        message = "Nested select is reserved for ColumnsSelector/ColumnsSelectionDsl behavior. " +
            "Use myGroup.cols(\"col1\", \"col2\") to select columns by name from a ColumnGroup.",
        replaceWith = ReplaceWith("this.cols(*columns)"),
        level = DeprecationLevel.ERROR,
    )
    public fun SingleColumn<DataRow<*>>.select(vararg columns: String): ColumnSet<*> =
        select { columns.toColumnSet() }

    @Deprecated(
        message = "Nested select is reserved for ColumnsSelector/ColumnsSelectionDsl behavior. " +
            "Use myGroup.cols(col1, col2) to select columns by name from a ColumnGroup.",
        replaceWith = ReplaceWith("this.cols(*columns)"),
        level = DeprecationLevel.ERROR,
    )
    public fun <R> SingleColumn<DataRow<*>>.select(vararg columns: ColumnReference<R>): ColumnSet<R> =
        select { columns.toColumnSet() }

    @Deprecated(
        message = "Nested select is reserved for ColumnsSelector/ColumnsSelectionDsl behavior. " +
            "Use myGroup.cols(Type::col1, Type::col2) to select columns by name from a ColumnGroup.",
        replaceWith = ReplaceWith("this.cols(*columns)"),
        level = DeprecationLevel.ERROR,
    )
    public fun <R> SingleColumn<DataRow<*>>.select(vararg columns: KProperty<R>): ColumnSet<R> =
        select { columns.toColumnSet() }

    // endregion
}

/**
 * @include [ColumnExpression.CommonDocs]
 *
 * #### For example:
 *
 * `df.`[groupBy][DataFrame.groupBy]` { `[expr][ColumnsSelectionDsl.expr]` { firstName.`[length][String.length]` + lastName.`[length][String.length]` } `[named][named]` "nameLength" }`
 *
 * `df.`[sortBy][DataFrame.sortBy]` { `[expr][ColumnsSelectionDsl.expr]` { name.`[length][String.length]` }.`[desc][SortDsl.desc]`() }`
 *
 * @param [name] The name the temporary column. Will be empty by default.
 * @include [Infer.Param] By default: [Nulls][Infer.Nulls].
 * @param [expression] An [AddExpression] to define what each new row of the temporary column should contain.
 */
public inline fun <T, reified R> ColumnsSelectionDsl<T>.expr(
    name: String = "",
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(name, infer, expression)
