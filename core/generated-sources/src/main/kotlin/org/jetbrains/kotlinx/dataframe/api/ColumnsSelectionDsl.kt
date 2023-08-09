package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.performCheck
import kotlin.reflect.KProperty

/**
 * Referring to or expressing column(s) in the selection DSL can be done in several ways corresponding to all
 * [Access APIs][AccessApi]:
 * TODO: [Issue #286](https://github.com/Kotlin/dataframe/issues/286)
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
private interface CommonColumnSelectionDocs

/**
 *
 */
private interface CommonColumnSelectionExamples

/** [Columns Selection DSL][ColumnsSelectionDsl] */
internal interface ColumnsSelectionDslLink

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T> ColumnsSelectionDsl<T>.asSingleColumn(): SingleColumn<DataRow<T>> = this as SingleColumn<DataRow<T>>

/**
 * Referring to or expressing column(s) in the selection DSL can be done in several ways corresponding to all
 * [Access APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]:
 * TODO: [Issue #286](https://github.com/Kotlin/dataframe/issues/286)
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * Can be safely cast to [SingleColumn] across the library. It does not directly
 * implement it for DSL purposes.
 */
public interface ColumnsSelectionDsl<out T> : /* SingleColumn<DataRow<T>> */
    ColumnSelectionDsl<T>,

    // first {}, firstCol()
    FirstColumnsSelectionDsl,
    // last {}, lastCol()
    LastColumnsSelectionDsl,
    // single {}, singleCol()
    SingleColumnsSelectionDsl,

    // col(name), col(5)
    ColColumnsSelectionDsl,
    // valueCol(name), valueCol(5)
    ValueColColumnsSelectionDsl,
    // frameCol(name), frameCol(5)
    FrameColColumnsSelectionDsl,
    // colGroup(name), colGroup(5)
    ColGroupColumnsSelectionDsl,

    // cols {}, cols(), cols(colA, colB), cols(1, 5), cols(1..5)
    ColsColumnsSelectionDsl,

    // colA.."colB"
    ColumnRangeColumnsSelectionDsl,
    // valueCols {}, valueCols()
    ValueColsColumnsSelectionDsl,
    // colGroups {}, colGroups()
    ColGroupsColumnsSelectionDsl,
    // frameCols {}, frameCols()
    FrameColsColumnsSelectionDsl,
    // colsOfKind(Value, Frame) {}, colsOfKind(Value, Frame)
    ColsOfKindColumnsSelectionDsl,
    // all(), allAfter(colA), allBefore(colA), allFrom(colA), allUpTo(colA)
    AllColumnsSelectionDsl,
    // .recursively()
    RecursivelyColumnsSelectionDsl,
    // children {}, children()
    ChildrenColumnsSelectionDsl,
    // take(5), takeLastChildren(2), takeLastWhile {}, takeChildrenWhile {}
    TakeColumnsSelectionDsl,
    // drop(5), dropLastChildren(2), dropLastWhile {}, dropChildrenWhile {}
    DropColumnsSelectionDsl,
    // except(), allExcept {}
    ExceptColumnsSelectionDsl<T>,
    // nameContains(""), childrenNameContains(""), nameStartsWith(""), childrenNameEndsWith("")
    ColumnNameFiltersColumnsSelectionDsl,
    // withoutNulls(), childrenWithoutNulls()
    WithoutNullsColumnsSelectionDsl,
    // distinct()
    DistinctColumnsSelectionDsl,
    // none()
    NoneColumnsSelectionDsl,
    // colsOf<>(), colsOf<> {}
    ColsOfColumnsSelectionDsl,
    // roots()
    RootsColumnsSelectionDsl,
    // filter {}, filterChildren {}
    FilterColumnsSelectionDsl,
    // colSet and colB
    AndColumnsSelectionDsl<T>,
    // colA named "colB", colA into "colB"
    RenameColumnsSelectionDsl {

    /**
     * Invokes the given [ColumnsSelector] using this [ColumnsSelectionDsl].
     */
    public operator fun <C> ColumnsSelector<T, C>.invoke(): ColumnsResolver<C> =
        this(this@ColumnsSelectionDsl, this@ColumnsSelectionDsl)

    // TODO add docs `this { age } / it { age }`
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("invokeColumnsSelector")
    public operator fun <C> invoke(selection: ColumnsSelector<T, C>): ColumnsResolver<C> = selection()

    // region select
    // TODO due to String.invoke conflict this cannot be moved out

    /**
     * ## Select from [ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
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
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
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
     * ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][SingleColumn.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup `[{][SingleColumn.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][SingleColumn.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C, R> SingleColumn<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        createColumnSet { context ->
            this.ensureIsColGroup().resolveSingle(context)?.let { col ->
                require(col.isColumnGroup()) {
                    "Column ${col.path} is not a ColumnGroup and can thus not be selected from."
                }

                col.asColumnGroup()
                    .getColumnsWithPaths(selector as ColumnsSelector<*, R>)
                    .map { it.changePath(col.path + it.path) }
            } ?: emptyList()
        }

    /** ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup `[{][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` colA `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public operator fun <C, R> SingleColumn<DataRow<C>>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        select(selector)

    /**
     * ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[select][SingleColumn.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup)`[() `{`][SingleColumn.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[`}`][SingleColumn.select]` }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[select][SingleColumn.select]` { colA `[and][ColumnsSelectionDsl.and]` colB } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup.`[select][KProperty.select]` { colA `[and][ColumnsSelectionDsl.and]` colB } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup `[`{`][KProperty.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[`}`][KProperty.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public fun <C, R> KProperty<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /** ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup)`[() `{`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` colA `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB `[`}`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColGroup.`[asColumnGroup][kotlin.reflect.KProperty.asColumnGroup]`().`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { colA `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[select][kotlin.reflect.KProperty.select]` { colA `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup `[`{`][kotlin.reflect.KProperty.select]` colA `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB `[`}`][kotlin.reflect.KProperty.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public operator fun <C, R> KProperty<DataRow<C>>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        select(selector)

    /**
     * ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[select][String.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup" `[{][String.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][String.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public fun <R> String.select(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /** ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[select][kotlin.String.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup" `[{][kotlin.String.select]` colA `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB `[}][kotlin.String.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public operator fun <R> String.invoke(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        select(selector)

    /**
     * ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[select][ColumnPath.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"] `[{][ColumnPath.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][ColumnPath.select]` }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup").`[select][ColumnPath.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup")`[() {][ColumnPath.select]` someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() `[}][ColumnPath.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public fun <R> ColumnPath.select(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /** ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"] `[{][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` colA `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB `[}][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[pathOf][org.jetbrains.kotlinx.dataframe.api.pathOf]`("pathTo", "myColGroup").`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[pathOf][org.jetbrains.kotlinx.dataframe.api.pathOf]`("pathTo", "myColGroup")`[() {][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` someCol `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[}][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
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
 * ## Column Expression
 * Create a temporary new column by defining an expression to fill up each row.
 *
 * See [Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression] for more information.
 *
 * #### For example:
 *
 * `df.`[groupBy][DataFrame.groupBy]` { `[expr][ColumnsSelectionDsl.expr]` { firstName.`[length][String.length]` + lastName.`[length][String.length]` } `[named][named]` "nameLength" }`
 *
 * `df.`[sortBy][DataFrame.sortBy]` { `[expr][ColumnsSelectionDsl.expr]` { name.`[length][String.length]` }.`[desc][SortDsl.desc]`() }`
 *
 * @param [name] The name the temporary column. Will be empty by default.
 * @param [infer] [An enum][org.jetbrains.kotlinx.dataframe.api.Infer.Infer] that indicates how [DataColumn.type][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
 * Either [None][org.jetbrains.kotlinx.dataframe.api.Infer.None], [Nulls][org.jetbrains.kotlinx.dataframe.api.Infer.Nulls], or [Type][org.jetbrains.kotlinx.dataframe.api.Infer.Type]. By default: [Nulls][Infer.Nulls].
 * @param [expression] An [AddExpression] to define what each new row of the temporary column should contain.
 */
public inline fun <T, reified R> ColumnsSelectionDsl<T>.expr(
    name: String = "",
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(name, infer, expression)

/**
 * Checks the validity of this [SingleColumn],
 * by adding a check to see it's a [ColumnGroup] (so, a [SingleColumn]<[DataRow]<*>>)
 * and throwing an [IllegalArgumentException] if it's not.
 */
@Suppress("UNCHECKED_CAST")
internal fun <C> SingleColumn<DataRow<C>>.ensureIsColGroup(): SingleColumn<DataRow<C>> =
    performCheck { col: ColumnWithPath<*>? ->
        require(col?.isColumnGroup() != false) {
            "Attempted to perform a ColumnGroup operation on ${col?.kind()} ${col?.path}."
        }
    }

/** Checks the validity of this [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn],
 * by adding a check to see it's a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] (so, a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]<[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]<*>>)
 * and throwing an [IllegalArgumentException] if it's not. */
internal fun <C> ColumnAccessor<DataRow<C>>.ensureIsColGroup(): ColumnAccessor<DataRow<C>> =
    performCheck { col: ColumnWithPath<*>? ->
        require(col?.isColumnGroup() != false) {
            "Attempted to perform a ColumnGroup operation on ${col?.kind()} ${col?.path}."
        }
    }
