package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * ## The Select Operation
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with only the columns selected by [columns].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][SelectSelectingOptions].
 *
 * For more information: [See `select` on the documentation website.](https://kotlin.github.io/dataframe/select.html)
 */
internal interface Select {

    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
     *
     *
     * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
     * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
     * for specifying columns type- and name-safe.
     *
     * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.select]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.select]`("length", "age")`
     *
     *
     *
     */
    typealias SelectSelectingOptions = Nothing
}

/**
 * ## The Select Operation
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with only the columns selected by [columns].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `select` on the documentation website.](https://kotlin.github.io/dataframe/select.html)
 * ### This Select Overload
 *
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.select]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("Select0")
public fun <T> DataFrame<T>.select(columns: ColumnsSelector<T, *>): DataFrame<T> = get(columns).toDataFrame().cast()

/**
 * ## The Select Operation
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with only the columns selected by [columns].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `select` on the documentation website.](https://kotlin.github.io/dataframe/select.html)
 * ### This Select Overload
 *
 * @param [columns] The [<code>KProperties</code>][KProperty] used to select the columns of this [<code>DataFrame</code>][DataFrame].
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.select(vararg columns: KProperty<*>): DataFrame<T> = select { columns.toColumnSet() }

/**
 * ## The Select Operation
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with only the columns selected by [columns].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `select` on the documentation website.](https://kotlin.github.io/dataframe/select.html)
 * ### This Select Overload
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.select]`("length", "age")`
 *
 *
 *
 * @param [columns] The [<code>Column Names</code>][String] used to select the columns of this [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("SelectString")
public fun <T> DataFrame<T>.select(vararg columns: String): DataFrame<T> = select { columns.toColumnSet() }

/**
 * ## The Select Operation
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with only the columns selected by [columns].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `select` on the documentation website.](https://kotlin.github.io/dataframe/select.html)
 * ### This Select Overload
 *
 * @param [columns] The [<code>Column Accessors</code>][ColumnReference] used to select the columns of this [<code>DataFrame</code>][DataFrame].
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.select(vararg columns: AnyColumnReference): DataFrame<T> = select { columns.toColumnSet() }

// endregion

// region ColumnsSelectionDsl
// NOTE: invoke overloads are inside ColumnsSelectionDsl.kt due to conflicts

/**
 * ## Select [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface SelectColumnsSelectionDsl {

    /**
     * ## Select from [<code>ColumnGroup</code>][ColumnGroup] Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[<code>`ColumnSet`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[<code>`SingleColumn`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>`DataRow`</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `colsSelector: `[<code>`ColumnsSelector`</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector]
     *
     *
     *
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>Column Group (reference)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnGroup`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`select`**</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]**`  {  `**[<code>`colsSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]**` }`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[<code>**`{`**</code>][ColumnsSelectionDsl.select]` `[<code>`colsSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]` `[<code>**`}`**</code>][ColumnsSelectionDsl.select]
     *
     *
     *
     *
     *
     *
     *
     */
    public interface Grammar {

        /** __`.`__[<code>**`select`**</code>][ColumnsSelectionDsl.select] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Select from [<code>ColumnGroup</code>][ColumnGroup]
     *
     * Perform a selection of columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [<code>ColumnGroup</code>][ColumnGroup]. This is more powerful than the [<code>cols</code>][ColumnsSelectionDsl.cols] filter, because now all
     * operations of the DSL are at your disposal.
     *
     * The scope of the new DSL instance is relative to
     * the [<code>ColumnGroup</code>][ColumnGroup] you are selecting from.
     *
     * The [<code>invoke</code>][ColumnsSelectionDsl.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * For more information: [See Select from Column Group on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#select-from-column-group)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColGroup.`[<code>select</code>][SingleColumn.select]`  { someCol  `[<code>and</code>][ColumnsSelectionDsl.and]` `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { "myGroupCol"  `[<code>{</code>][String.select]`  "colA" and  `[<code>expr</code>][ColumnsSelectionDsl.expr]`  { 0 }  `[<code>}</code>][String.select]` }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>select</code>][ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { it["myGroupCol"].`[<code>asColumnGroup</code>][DataColumn.asColumnGroup]`()`[<code>() {</code>][SingleColumn.select]`  "colA" and "colB"  `[<code>}</code>][SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [<code>except</code>][ColumnsSelectionDsl.except]/[<code>allExcept</code>][ColumnsSelectionDsl.allColsExcept] for the inverted operation of this function.
     *
     * @param [selector] The [<code>ColumnsSelector</code>][ColumnsSelector] to use for the selection.
     * @receiver The [<code>ColumnGroup</code>][ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [<code>ColumnGroup</code>][ColumnGroup].
     * @return A [<code>ColumnSet</code>][ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    private interface CommonSelectDocs {

        typealias EXAMPLE = Nothing
    }

    /**
     * ## Select from [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than the [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] filter, because now all
     * operations of the DSL are at your disposal.
     *
     * The scope of the new DSL instance is relative to
     * the [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] you are selecting from.
     *
     * The [<code>invoke</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * For more information: [See Select from Column Group on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#select-from-column-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  { someCol  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "myGroupCol"  `[<code>{</code>][kotlin.String.select]`  "colA" and  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]`  { 0 }  `[<code>}</code>][kotlin.String.select]` }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[<code>() {</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  "colA" and "colB"  `[<code>}</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColGroup.`[<code>select</code>][SingleColumn.select]`  { someCol  `[<code>and</code>][ColumnsSelectionDsl.and]` `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { myColGroup  `[<code>`{`</code>][SingleColumn.select]`  colA  `[<code>and</code>][ColumnsSelectionDsl.and]`  colB  `[<code>`}`</code>][SingleColumn.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]/[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] for the inverted operation of this function.
     *
     * @param [selector] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    @Interpretable("NestedSelect")
    public fun <C, R> SingleColumn<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        selectInternal(selector)

    /**
     * ## Select from [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than the [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] filter, because now all
     * operations of the DSL are at your disposal.
     *
     * The scope of the new DSL instance is relative to
     * the [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] you are selecting from.
     *
     * The [<code>invoke</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * For more information: [See Select from Column Group on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#select-from-column-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  { someCol  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "myGroupCol"  `[<code>{</code>][kotlin.String.select]`  "colA" and  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]`  { 0 }  `[<code>}</code>][kotlin.String.select]` }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[<code>() {</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  "colA" and "colB"  `[<code>}</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColGroup.`[<code>select</code>][KProperty.select]`  { someCol  `[<code>and</code>][ColumnsSelectionDsl.and]` `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { DataSchemaType::myColGroup  `[<code>`{`</code>][KProperty.select]`  colA  `[<code>and</code>][ColumnsSelectionDsl.and]`  colB  `[<code>`}`</code>][KProperty.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]/[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] for the inverted operation of this function.
     *
     * @param [selector] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C, R> KProperty<C>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /**
     * ## Select from [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than the [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] filter, because now all
     * operations of the DSL are at your disposal.
     *
     * The scope of the new DSL instance is relative to
     * the [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] you are selecting from.
     *
     * The [<code>invoke</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * For more information: [See Select from Column Group on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#select-from-column-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  { someCol  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "myGroupCol"  `[<code>{</code>][kotlin.String.select]`  "colA" and  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]`  { 0 }  `[<code>}</code>][kotlin.String.select]` }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[<code>() {</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  "colA" and "colB"  `[<code>}</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColGroup".`[<code>select</code>][String.select]`  { someCol  `[<code>and</code>][ColumnsSelectionDsl.and]` `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { "myColGroup"  `[<code>`{`</code>][String.select]`  colA  `[<code>and</code>][ColumnsSelectionDsl.and]`  colB  `[<code>`}`</code>][String.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]/[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] for the inverted operation of this function.
     *
     * @param [selector] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    @Interpretable("StringSelect")
    public fun <R> String.select(selector: ColumnsSelector<*, R>): ColumnSet<R> = columnGroup(this).select(selector)

    /**
     * ## Select from [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than the [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] filter, because now all
     * operations of the DSL are at your disposal.
     *
     * The scope of the new DSL instance is relative to
     * the [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] you are selecting from.
     *
     * The [<code>invoke</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * For more information: [See Select from Column Group on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#select-from-column-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  { someCol  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "myGroupCol"  `[<code>{</code>][kotlin.String.select]`  "colA" and  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]`  { 0 }  `[<code>}</code>][kotlin.String.select]` }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[<code>() {</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  "colA" and "colB"  `[<code>}</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColGroup"].`[<code>select</code>][ColumnPath.select]`  { someCol  `[<code>and</code>][ColumnsSelectionDsl.and]` `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { "pathTo"["myColGroup"]  `[<code>`{`</code>][ColumnPath.select]`  colA  `[<code>and</code>][ColumnsSelectionDsl.and]`  colB  `[<code>`}`</code>][ColumnPath.select]` }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>pathOf</code>][pathOf]`("pathTo", "myColGroup").`[<code>select</code>][ColumnPath.select]`  { someCol  `[<code>and</code>][ColumnsSelectionDsl.and]` `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>pathOf</code>][pathOf]`("pathTo", "myColGroup")`[<code>`() {`</code>][ColumnPath.select]`  someCol  `[<code>and</code>][ColumnsSelectionDsl.and]` `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>() `[<code>`}`</code>][ColumnPath.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]/[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] for the inverted operation of this function.
     *
     * @param [selector] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    @Interpretable("ColumnPathSelect")
    public fun <R> ColumnPath.select(selector: ColumnsSelector<*, R>): ColumnSet<R> = columnGroup(this).select(selector)
}

internal fun <C, R> SingleColumn<DataRow<C>>.selectInternal(selector: ColumnsSelector<C, R>): ColumnSet<R> =
    createColumnSet { context ->
        this.ensureIsColumnGroup().resolveSingle(context)?.let { col ->
            require(col.isColumnGroup()) {
                "Column ${col.path} is not a ColumnGroup and can thus not be selected from."
            }

            col.getColumnsWithPaths(selector as ColumnsSelector<*, R>)
                .map { it.changePath(col.path + it.path) }
        } ?: emptyList()
    }
// endregion
