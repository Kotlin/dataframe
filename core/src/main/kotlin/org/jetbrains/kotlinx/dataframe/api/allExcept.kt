package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.removeAll
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_EXCEPT
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_EXCEPT_COLUMN_PATH
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_REPLACE
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_REPLACE_VARARG
import org.jetbrains.kotlinx.dataframe.util.ALL_EXCEPT_COLUMN_PATH
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## (All) (Cols) Except {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
@Suppress("ClassName")
public interface AllExceptColumnsSelectionDsl {

    /**
     * ## (All) (Cols) Except Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnsSelectorDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumNameDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnNoPathDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnsResolverDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}**`   {   `**{@include [DslGrammarTemplate.ColumnsSelectorRef]}**` \}`**
     *
     *  `| `{@include [PlainDslName]}**`(`**{@include [DslGrammarTemplate.ColumnNoPathRef]}**`,`**` ..`**`)`**
     * }
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}` [`**`  {  `**`] `{@include [DslGrammarTemplate.ColumnsResolverRef]}` [`**`  \}  `**`]`
     *
     *  {@include [Indent]}`| `{@include [ColumnSetName]}` `{@include [DslGrammarTemplate.ColumnRef]}
     *
     *  {@include [Indent]}`| `**`.`**{@include [ColumnSetName]}**`(`**{@include [DslGrammarTemplate.ColumnRef]}**`,`**` ..`**`)`**
     * }
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnGroupName]}**`  {  `**{@include [DslGrammarTemplate.ColumnsSelectorRef]}**`  \}  `**
     *
     *  {@include [Indent]}`| `{@include [ColumnGroupName]}**`(`**{@include [DslGrammarTemplate.ColumnNameRef]}**`,`**` ..`**`)`**
     * }
     */
    public interface Grammar {

        /** [**`allExcept`**][ColumnsSelectionDsl.allExcept] */
        public interface PlainDslName

        /** [**`except`**][ColumnsSelectionDsl.except] */
        public interface ColumnSetName

        /** __`.`__[**`allColsExcept`**][ColumnsSelectionDsl.allColsExcept] */
        public interface ColumnGroupName
    }

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the {@include [ColumnsSelectionDslLink]} to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar]
     *
     * ### On [ColumnSets][ColumnSet]
     * This function can be explained the easiest with [ColumnSets][ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][colsOf]`<`[Int][Int]`>() `[except][ColumnSet.except]`  (age  `[and][ColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet] created by `age `[and][ColumnsSelectionDsl.and]` height` from the [ColumnSet] created by [colsOf][colsOf]`<`[Int][Int]`>()`.
     *
     * {@include [LineBreak]}
     * This operation can also be used to exclude columns originating from [Column Groups][ColumnGroup], although
     * they need to be directly included in the [ColumnSet], like when using [`colsAtAnyDepth`][ColumnsSelectionDsl.colsAtAnyDepth].
     *
     * For instance:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][DataColumn.name]`() } `[except][ColumnSet.except]` userData.age }`
     * {@include [LineBreak]}
     *
     * Note that the selection of columns to exclude from [column sets][ColumnSet] is always done relative to the outer
     * scope. Use the {@include [ExtensionPropertiesApiLink]} to prevent scoping issues if possible.
     * {@include [LineBreak]}
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][ColumnGroup]):
     *
     * [cols][ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][ColumnSet.except]`(a.b)`
     *
     * `== `[cols][ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][ColumnsSelectionDsl]
     * Instead of having to write [all][ColumnsSelectionDsl.all]`() `[except][ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[allExcept][ColumnsSelectionDsl.allExcept]`  { userData  `[and][ColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][ColumnGroup]
     * The variant of this function on [ColumnGroups][ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     * {@include [LineBreak]}
     * In other words:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[allColsExcept][SingleColumn.allColsExcept]`  { colA  `[and][ColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][ColumnsSelectionDsl.select]`  {  `[all][ColumnsSelectionDsl.all]`() `[except][ColumnSet.except]`  { colA  `[and][ColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[allCols][ColumnsSelectionDsl.allCols]`() `[except][ColumnSet.except]`  { myColGroup.colA  `[and][ColumnsSelectionDsl.and]` myColGroup.colB } }`
     * {@include [LineBreak]}
     * Also note the name change, similar to [allCols][ColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * {@get [EXAMPLE]}
     *
     * {@get [PARAM]}
     * @return A [ColumnSet] containing all columns in [this\] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    private interface CommonExceptDocs {

        // Example argument
        interface EXAMPLE

        // Parameter argument
        interface PARAM
    }

    // region ColumnSet

    /**
     * @include [CommonExceptDocs]
     * {@set [CommonExceptDocs.EXAMPLE]
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>() `[except][ColumnSet.except]` {@get [ARGUMENT_1]} \}`
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[cols][ColumnsSelectionDsl.cols]`(name, age) `[except][ColumnSet.except]` {@get [ARGUMENT_2]} \}`
     * }
     */
    private interface ColumnSetInfixDocs {

        // argument
        interface ARGUMENT_1

        // argument
        interface ARGUMENT_2
    }

    /**
     * @include [CommonExceptDocs]
     * {@set [CommonExceptDocs.EXAMPLE]
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>().`[except][ColumnSet.except]`{@get [ARGUMENT_1]} \}`
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[cols][ColumnsSelectionDsl.cols]`(name, age).`[except][ColumnSet.except]`{@get [ARGUMENT_2]} \}`
     * }
     */
    private interface ColumnSetVarargDocs {

        // argument
        interface ARGUMENT_1

        // argument
        interface ARGUMENT_2
    }

    /**
     * @include [ColumnSetInfixDocs]
     * @set [CommonExceptDocs.PARAM] @param [selector\] A lambda in which you specify the columns that need to be
     *   excluded from the [ColumnSet]. The scope of the selector is the same as the outer scope.
     * @set [ColumnSetInfixDocs.ARGUMENT_1] { "age" `[and][ColumnsSelectionDsl.and]` height }
     * @set [ColumnSetInfixDocs.ARGUMENT_2] { name.firstName }
     */
    public infix fun <C> ColumnSet<C>.except(selector: () -> ColumnsResolver<*>): ColumnSet<C> = except(selector())

    /**
     * @include [ColumnSetInfixDocs]
     * @set [CommonExceptDocs.PARAM] @param [other\] A [ColumnsResolver] containing the columns that need to be
     *   excluded from the [ColumnSet].
     * @set [ColumnSetInfixDocs.ARGUMENT_1] "age" `[and][ColumnsSelectionDsl.and]` height
     * @set [ColumnSetInfixDocs.ARGUMENT_2] name.firstName
     */
    public infix fun <C> ColumnSet<C>.except(other: ColumnsResolver<*>): ColumnSet<C> = exceptInternal(other)

    /**
     * @include [ColumnSetVarargDocs]
     * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [ColumnsResolvers][ColumnsResolver] containing
     *  the columns that need to be excluded from the [ColumnSet].
     * @set [ColumnSetVarargDocs.ARGUMENT_1] (age, userData.height)
     * @set [ColumnSetVarargDocs.ARGUMENT_2] (name.firstName, name.middleName)
     */
    public fun <C> ColumnSet<C>.except(vararg others: ColumnsResolver<*>): ColumnSet<C> = except(others.toColumnSet())

    /**
     * @include [ColumnSetInfixDocs]
     * @set [CommonExceptDocs.PARAM] @param [other\] A [String] referring to
     *  the column (relative to the current scope) that needs to be excluded from the [ColumnSet].
     * @set [ColumnSetInfixDocs.ARGUMENT_1] "age"
     * @set [ColumnSetInfixDocs.ARGUMENT_2] "name"
     */
    public infix fun <C> ColumnSet<C>.except(other: String): ColumnSet<C> = except(column<Any?>(other))

    /**
     * @include [ColumnSetVarargDocs]
     * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [Strings][String] referring to
     *  the columns (relative to the current scope) that need to be excluded from the [ColumnSet].
     * @set [ColumnSetVarargDocs.ARGUMENT_1] ("age", "height")
     * @set [ColumnSetVarargDocs.ARGUMENT_2] ("name")
     */
    public fun <C> ColumnSet<C>.except(vararg others: String): ColumnSet<C> = except(others.toColumnSet())

    /**
     * @include [ColumnSetInfixDocs]
     * @set [CommonExceptDocs.PARAM] @param [other\] A [KProperty] referring to
     *  the column (relative to the current scope) that needs to be excluded from the [ColumnSet].
     * @set [ColumnSetInfixDocs.ARGUMENT_1] Person::age
     * @set [ColumnSetInfixDocs.ARGUMENT_2] Person::name
     */
    public infix fun <C> ColumnSet<C>.except(other: KProperty<C>): ColumnSet<C> = except(column(other))

    /**
     * @include [ColumnSetVarargDocs]
     * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [KProperties][KProperty] referring to
     *  the columns (relative to the current scope) that need to be excluded from the [ColumnSet].
     * @set [ColumnSetVarargDocs.ARGUMENT_1] (Person::age, Person::height)
     * @set [ColumnSetVarargDocs.ARGUMENT_2] (Person::name)
     */
    public fun <C> ColumnSet<C>.except(vararg others: KProperty<C>): ColumnSet<C> = except(others.toColumnSet())

    /**
     * @include [ColumnSetInfixDocs]
     * @set [CommonExceptDocs.PARAM] @param [other\] A [ColumnPath] referring to
     *  the column (relative to the current scope) that needs to be excluded from the [ColumnSet].
     * @set [ColumnSetInfixDocs.ARGUMENT_1] "userdata"["age"]
     * @set [ColumnSetInfixDocs.ARGUMENT_2] pathOf("name", "firstName")
     */
    public infix fun <C> ColumnSet<C>.except(other: ColumnPath): ColumnSet<C> = except(column<Any?>(other))

    /**
     * @include [ColumnSetVarargDocs]
     * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [ColumnPaths][ColumnPath] referring to
     *  the columns (relative to the current scope) that need to be excluded from the [ColumnSet].
     * @set [ColumnSetVarargDocs.ARGUMENT_1] (pathOf("age"), "userdata"["height"])
     * @set [ColumnSetVarargDocs.ARGUMENT_2] ("name"["firstName"], "name"["middleName"])
     */
    public fun <C> ColumnSet<C>.except(vararg others: ColumnPath): ColumnSet<C> = except(others.toColumnSet())

    // endregion

    // region ColumnsSelectionDsl

    /**
     * @include [CommonExceptDocs]
     * @set [CommonExceptDocs.EXAMPLE]
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[allExcept][ColumnsSelectionDsl.allExcept]`{@get [ARGUMENT_1]} \}`
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[allExcept][ColumnsSelectionDsl.allExcept]`{@get [ARGUMENT_2]} \}`
     */
    private interface ColumnsSelectionDslDocs {

        // argument
        interface ARGUMENT_1

        // argument
        interface ARGUMENT_2
    }

    /**
     * @include [ColumnsSelectionDslDocs]
     * @set [CommonExceptDocs.PARAM] @param [selector\] A lambda in which you specify the columns that need to be
     *  excluded from the current selection. The scope of the selector is the same as the outer scope.
     * @set [ColumnsSelectionDslDocs.ARGUMENT_1] \ { "age"  `[and][ColumnsSelectionDsl.and]` height }
     * @set [ColumnsSelectionDslDocs.ARGUMENT_2] \ { name.firstName }
     */
    public fun <C> ColumnsSelectionDsl<C>.allExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        this.asSingleColumn().allColsExcept(selector)

    /**
     * {@comment No scoping issues, this function can exist for legacy purposes}
     * @include [ColumnsSelectionDslDocs]
     * @set [CommonExceptDocs.PARAM] @param [others\] A [ColumnsResolver] containing the columns that need to be
     *  excluded from the current selection.
     * @set [ColumnsSelectionDslDocs.ARGUMENT_1] (age, height)
     * @set [ColumnsSelectionDslDocs.ARGUMENT_2] (name.firstName, name.middleName)
     */
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    /**
     * @include [ColumnsSelectionDslDocs]
     * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [Strings][String] referring to
     *  the columns (relative to the current scope) that need to be excluded from the current selection.
     * @set [ColumnsSelectionDslDocs.ARGUMENT_1] ("age", "height")
     * @set [ColumnsSelectionDslDocs.ARGUMENT_2] ("name")
     */
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: String): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    /**
     * @include [ColumnsSelectionDslDocs]
     * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [KProperties][KProperty] referring to
     *  the columns (relative to the current scope) that need to be excluded from the current selection.
     * @set [ColumnsSelectionDslDocs.ARGUMENT_1] (Person::age, Person::height)
     * @set [ColumnsSelectionDslDocs.ARGUMENT_2] (Person::name)
     */
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    @Deprecated(message = ALL_EXCEPT_COLUMN_PATH, level = DeprecationLevel.ERROR)
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    // endregion

    // region SingleColumn

    /**
     * @include [CommonExceptDocs]
     * @set [CommonExceptDocs.EXAMPLE] {@comment <code> blocks are there to prevent double ``}
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `<code>{@get [RECEIVER_1]}</code>[allColsExcept][{@get [RECEIVER_TYPE]}.allColsExcept]<code>{@get [ARGUMENT_1]}</code>` \}`
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  { city  `[and][ColumnsSelectionDsl.and]` `<code>{@get [RECEIVER_2]}</code>[allColsExcept][{@get [RECEIVER_TYPE]}.allColsExcept]<code>{@get [ARGUMENT_2]}</code>` \}`
     */
    @Suppress("ClassName")
    private interface ColumnGroupDocs {

        // receiver
        interface RECEIVER_1

        // receiver
        interface RECEIVER_2

        // type
        interface RECEIVER_TYPE

        // argument
        interface ARGUMENT_1

        // argument
        interface ARGUMENT_2

        /**
         * @set [ColumnGroupDocs.RECEIVER_1] `userData.`
         * @set [ColumnGroupDocs.RECEIVER_2] `name.`
         * @set [ColumnGroupDocs.RECEIVER_TYPE] SingleColumn
         */
        interface SingleColumnReceiverArgs

        /**
         * @set [ColumnGroupDocs.RECEIVER_1] `"userData".`
         * @set [ColumnGroupDocs.RECEIVER_2] `"name".`
         * @set [ColumnGroupDocs.RECEIVER_TYPE] String
         */
        interface StringReceiverArgs

        /**
         * @set [ColumnGroupDocs.RECEIVER_1] `DataSchemaPerson::userData.`
         * @set [ColumnGroupDocs.RECEIVER_2] `Person::name.`
         * @set [ColumnGroupDocs.RECEIVER_TYPE] KProperty
         */
        interface KPropertyReceiverArgs

        /**
         * @set [ColumnGroupDocs.RECEIVER_1] `pathOf("userData").`
         * @set [ColumnGroupDocs.RECEIVER_2] `"pathTo"["myColGroup"].`
         * @set [ColumnGroupDocs.RECEIVER_TYPE] ColumnPath
         */
        interface ColumnPathReceiverArgs

        /**
         * @set [CommonExceptDocs.PARAM] @param [selector\] A lambda in which you specify the columns that need to be
         *  excluded from the current selection in [this\] column group. The other columns will be included in the selection
         *  by default. The scope of the selector is relative to the column group.
         * @set [ColumnGroupDocs.ARGUMENT_1] `  { "age"  `[and][ColumnsSelectionDsl.and]` height }`
         * @set [ColumnGroupDocs.ARGUMENT_2] ` { firstName }`
         */
        interface SelectorArgs

        /**
         * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [Strings][String] referring to
         *  the columns (relative to the column group) that need to be excluded from the current selection in [this\]
         *  column group. The other columns will be included in the selection by default.
         * @set [ColumnGroupDocs.ARGUMENT_1] `("age", "height")`
         * @set [ColumnGroupDocs.ARGUMENT_2] `("firstName", "middleName")`
         */
        interface StringArgs

        /**
         * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [KProperties][KProperty] referring to
         *  the columns (relative to the column group) that need to be excluded from the current selection in [this\]
         *  column group. The other columns will be included in the selection by default.
         * @set [ColumnGroupDocs.ARGUMENT_1] `(Person::age, Person::height)`
         * @set [ColumnGroupDocs.ARGUMENT_2] `(Person::firstName, Person::middleName)`
         */
        interface KPropertyArgs
    }

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.SingleColumnReceiverArgs]
     * @include [ColumnGroupDocs.SelectorArgs]
     */
    public fun <C> SingleColumn<DataRow<C>>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allColsExceptInternal(selector.toColumns())

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun SingleColumn<DataRow<*>>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.SingleColumnReceiverArgs]
     * @include [ColumnGroupDocs.StringArgs]
     */
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.SingleColumnReceiverArgs]
     * @include [ColumnGroupDocs.KPropertyArgs]
     */
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExceptInternal(others.toColumnSet())

    @Deprecated(message = ALL_COLS_EXCEPT_COLUMN_PATH, level = DeprecationLevel.ERROR)
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg other: ColumnPath): ColumnSet<*> =
        allColsExceptInternal(other.toColumnSet())

    // endregion

    // region String

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.StringReceiverArgs]
     * @include [ColumnGroupDocs.SelectorArgs]
     */
    public fun String.allColsExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun String.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun String.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.StringReceiverArgs]
     * @include [ColumnGroupDocs.StringArgs]
     */
    public fun String.allColsExcept(vararg others: String): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.StringReceiverArgs]
     * @include [ColumnGroupDocs.KPropertyArgs]
     */
    public fun String.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    @Deprecated(message = ALL_COLS_EXCEPT_COLUMN_PATH, level = DeprecationLevel.ERROR)
    public fun String.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion

    // region KProperty

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupDocs.SelectorArgs]
     */
    public fun <C> KProperty<C>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun KProperty<*>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun KProperty<*>.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupDocs.StringArgs]
     */
    public fun KProperty<*>.allColsExcept(vararg others: String): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupDocs.KPropertyArgs]
     */
    public fun KProperty<*>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    @Deprecated(message = ALL_COLS_EXCEPT_COLUMN_PATH, level = DeprecationLevel.ERROR)
    public fun KProperty<*>.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion

    // region ColumnPath

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.ColumnPathReceiverArgs]
     * @include [ColumnGroupDocs.SelectorArgs]
     */
    public fun ColumnPath.allColsExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun ColumnPath.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun ColumnPath.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.ColumnPathReceiverArgs]
     * @include [ColumnGroupDocs.StringArgs]
     */
    public fun ColumnPath.allColsExcept(vararg others: String): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.ColumnPathReceiverArgs]
     * @include [ColumnGroupDocs.KPropertyArgs]
     */
    public fun ColumnPath.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    @Deprecated(message = ALL_COLS_EXCEPT_COLUMN_PATH, level = DeprecationLevel.ERROR)
    public fun ColumnPath.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion
}

/**
 * Removes the columns in the "other" ColumnsResolver from the current ColumnSet.
 * Returns a new ColumnSet with the remaining columns.
 *
 * @param other The ColumnsResolver containing the columns to be removed.
 * @return The new ColumnSet with the remaining columns.
 */
@Suppress("UNCHECKED_CAST")
internal fun <C> ColumnSet<C>.exceptInternal(other: ColumnsResolver<*>): ColumnSet<C> =
    createColumnSet { context ->
        val resolvedCols = this.resolve(context)
        val resolvedColsToExcept = other.resolve(context)
        resolvedCols.removeAll(resolvedColsToExcept)
    } as ColumnSet<C>

/**
 * Returns a new ColumnSet that contains all columns from inside the receiver column group
 * except those specified in the "other" ColumnsResolver.
 *
 * @param other The ColumnsResolver containing the columns to be removed.
 * @return The new ColumnSet with the remaining columns.
 */
internal fun SingleColumn<DataRow<*>>.allColsExceptInternal(other: ColumnsResolver<*>): ColumnSet<Any?> =
    selectInternal { all().exceptInternal(other) }

// endregion
