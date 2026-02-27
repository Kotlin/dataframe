package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.ColumnGroupDocs.ARGUMENT_1
import org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.ColumnGroupDocs.ARGUMENT_2
import org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.ColumnGroupDocs.RECEIVER_1
import org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.ColumnGroupDocs.RECEIVER_2
import org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.ColumnGroupDocs.RECEIVER_TYPE
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExceptKeepingStructure
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_EXCEPT
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_EXCEPT_REPLACE
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_EXCEPT_REPLACE_VARARG
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.EXCEPT_REPLACE
import org.jetbrains.kotlinx.dataframe.util.EXCEPT_REPLACE_VARARG
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
     *  {@include [DslGrammarTemplate.ColumnNoAccessorDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnsResolverDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}**`   {   `**{@include [DslGrammarTemplate.ColumnsSelectorRef]}**` \}`**
     *
     *  `| `{@include [PlainDslName]}**`(`**{@include [DslGrammarTemplate.ColumnRef]}**`,`**` ..`**`)`**
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
     *  {@include [Indent]}`| `{@include [ColumnGroupName]}**`(`**{@include [DslGrammarTemplate.ColumnNoAccessorRef]}**`,`**` ..`**`)`**
     *
     *  {@include [Indent]}`| `{@include [ColumnGroupExceptName]}**`  {  `**{@include [DslGrammarTemplate.ColumnsSelectorRef]}**`  \}  `**
     *
     *  {@include [Indent]}`| `{@include [ColumnGroupExceptName]}**`(`**{@include [DslGrammarTemplate.ColumnNoAccessorRef]}**`,`**` ..`**`)`**
     * }
     */
    public interface Grammar {

        /** [**`allExcept`**][ColumnsSelectionDsl.allExcept] */
        public typealias PlainDslName = Nothing

        /** [**`except`**][ColumnsSelectionDsl.except] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[**`allColsExcept`**][ColumnsSelectionDsl.allColsExcept] */
        public typealias ColumnGroupName = Nothing

        /** __`.`__[**`except`**][ColumnsSelectionDsl.except] */
        public typealias ColumnGroupExceptName = Nothing
    }

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the {@include [ColumnsSelectionDslLink]}.
     *
     * ### Check out: [Grammar]
     *
     * ### On [ColumnSets][ColumnSet]
     * This function can be explained the easiest with [ColumnSets][ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][colsOf]`<`[Int][Int]`>() `[except][ColumnSet.except]`  (age  `[and][ColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet] created by `age `[and][ColumnsSelectionDsl.and]` height` from the [ColumnSet] created by [colsOf][colsOf]`<`[Int][Int]`>()`.
     *
     * {@include [LineBreak]}
     * This operation can also be used to exclude columns from [Column Groups][ColumnGroup].
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
     * each time it is encountered (including inside [ColumnGroups][ColumnGroup]). You could say the receiver [ColumnSet]
     * is [simplified][ColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][ColumnSet.except]`(a.b)`
     *
     * `== `[cols][ColumnsSelectionDsl.cols]`(a).`[except][ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][ColumnsSelectionDsl]
     * Instead of having to write [all][ColumnsSelectionDsl.all]`() `[except][ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[allExcept][ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][ColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][ColumnGroup]: All Cols Except
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
     * columns inside the group, 'lifting' them out.
     *
     * ### On [ColumnGroups][ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup] in the selection.
     * In contrast to [allColsExcept][ColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][DataFrame.select]` { colGroup.`[except][SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]`(colGroup) `[except][ColumnSet.except]` colGroup.col }`
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
    @ExcludeFromSources
    private interface CommonExceptDocs {

        // Example argument
        typealias EXAMPLE = Nothing

        // Parameter argument
        typealias PARAM = Nothing
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
    @ExcludeFromSources
    private interface ColumnSetInfixDocs {

        // argument
        typealias ARGUMENT_1 = Nothing

        // argument
        typealias ARGUMENT_2 = Nothing
    }

    /**
     * @include [CommonExceptDocs]
     * {@set [CommonExceptDocs.EXAMPLE]
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>().`[except][ColumnSet.except]`{@get [ARGUMENT_1]} \}`
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[cols][ColumnsSelectionDsl.cols]`(name, age).`[except][ColumnSet.except]`{@get [ARGUMENT_2]} \}`
     * }
     */
    @ExcludeFromSources
    private interface ColumnSetVarargDocs {

        // argument
        typealias ARGUMENT_1 = Nothing

        // argument
        typealias ARGUMENT_2 = Nothing
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnSet<C>.except(other: KProperty<C>): ColumnSet<C> = except(column(other))

    /**
     * @include [ColumnSetVarargDocs]
     * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [KProperties][KProperty] referring to
     *  the columns (relative to the current scope) that need to be excluded from the [ColumnSet].
     * @set [ColumnSetVarargDocs.ARGUMENT_1] (Person::age, Person::height)
     * @set [ColumnSetVarargDocs.ARGUMENT_2] (Person::name)
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
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
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[allExcept][ColumnsSelectionDsl.allExcept]{@get [ARGUMENT_1]}` \}`
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[allExcept][ColumnsSelectionDsl.allExcept]{@get [ARGUMENT_2]}` \}`
     */
    @ExcludeFromSources
    private interface ColumnsSelectionDslDocs {

        // argument
        typealias ARGUMENT_1 = Nothing

        // argument
        typealias ARGUMENT_2 = Nothing
    }

    /**
     * @include [ColumnsSelectionDslDocs]
     * @set [CommonExceptDocs.PARAM] @param [selector\] A lambda in which you specify the columns that need to be
     *  excluded from the current selection. The scope of the selector is the same as the outer scope.
     * @set [ColumnsSelectionDslDocs.ARGUMENT_1] `  { "age"  `[and][ColumnsSelectionDsl.and]` height }`
     * @set [ColumnsSelectionDslDocs.ARGUMENT_2] ` { name.firstName }`
     */
    public fun <C> ColumnsSelectionDsl<C>.allExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        this.asSingleColumn().allColsExcept(selector)

    /**
     * {@comment No scoping issues, this function can exist for legacy purposes}
     * @include [ColumnsSelectionDslDocs]
     * @set [CommonExceptDocs.PARAM] @param [others\] A [ColumnsResolver] containing the columns that need to be
     *  excluded from the current selection.
     * @set [ColumnsSelectionDslDocs.ARGUMENT_1] `(age, height)`
     * @set [ColumnsSelectionDslDocs.ARGUMENT_2] `(name.firstName, name.middleName)`
     */
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    /**
     * @include [ColumnsSelectionDslDocs]
     * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [Strings][String] referring to
     *  the columns (relative to the current scope) that need to be excluded from the current selection.
     * @set [ColumnsSelectionDslDocs.ARGUMENT_1] `("age", "height")`
     * @set [ColumnsSelectionDslDocs.ARGUMENT_2] `("name")`
     */
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: String): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    /**
     * @include [ColumnsSelectionDslDocs]
     * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [KProperties][KProperty] referring to
     *  the columns (relative to the current scope) that need to be excluded from the current selection.
     * @set [ColumnsSelectionDslDocs.ARGUMENT_1] `(Person::age, Person::height)`
     * @set [ColumnsSelectionDslDocs.ARGUMENT_2] `(Person::name)`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    /**
     * @include [ColumnsSelectionDslDocs]
     * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [ColumnPaths][ColumnPath] referring to
     *  the columns (relative to the current scope) that need to be excluded from the current selection.
     * @set [ColumnsSelectionDslDocs.ARGUMENT_1] `(pathOf("age"), "userdata"["height"])`
     * @set [ColumnsSelectionDslDocs.ARGUMENT_2] `("name"["firstName"], "name"["middleName"])`
     */
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    // endregion

    // region SingleColumn
    // region allColsExcept

    /**
     * @include [CommonExceptDocs]
     * @set [CommonExceptDocs.EXAMPLE] {@comment <code> blocks are there to prevent double ``}
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `<code>{@get [RECEIVER_1]}</code>[allColsExcept][{@get [RECEIVER_TYPE]}.allColsExcept]<code>{@get [ARGUMENT_1]}</code>` \}`
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  { city  `[and][ColumnsSelectionDsl.and]` `<code>{@get [RECEIVER_2]}</code>[allColsExcept][{@get [RECEIVER_TYPE]}.allColsExcept]<code>{@get [ARGUMENT_2]}</code>` \}`
     */
    @Suppress("ClassName")
    @ExcludeFromSources
    private interface ColumnGroupDocs {

        // receiver
        typealias RECEIVER_1 = Nothing

        // receiver
        typealias RECEIVER_2 = Nothing

        // type
        typealias RECEIVER_TYPE = Nothing

        // argument
        typealias ARGUMENT_1 = Nothing

        // argument
        typealias ARGUMENT_2 = Nothing

        /**
         * @set [ColumnGroupDocs.RECEIVER_1] `userData.`
         * @set [ColumnGroupDocs.RECEIVER_2] `name.`
         * @set [ColumnGroupDocs.RECEIVER_TYPE] SingleColumn
         */
        typealias SingleColumnReceiverArgs = Nothing

        /**
         * @set [ColumnGroupDocs.RECEIVER_1] `"userData".`
         * @set [ColumnGroupDocs.RECEIVER_2] `"name".`
         * @set [ColumnGroupDocs.RECEIVER_TYPE] String
         */
        typealias StringReceiverArgs = Nothing

        /**
         * @set [ColumnGroupDocs.RECEIVER_1] `DataSchemaPerson::userData.`
         * @set [ColumnGroupDocs.RECEIVER_2] `Person::name.`
         * @set [ColumnGroupDocs.RECEIVER_TYPE] KProperty
         */
        typealias KPropertyReceiverArgs = Nothing

        /**
         * @set [ColumnGroupDocs.RECEIVER_1] `pathOf("userData").`
         * @set [ColumnGroupDocs.RECEIVER_2] `"pathTo"["myColGroup"].`
         * @set [ColumnGroupDocs.RECEIVER_TYPE] ColumnPath
         */
        typealias ColumnPathReceiverArgs = Nothing

        /**
         * @set [CommonExceptDocs.PARAM] @param [selector\] A lambda in which you specify the columns that need to be
         *  excluded from the current selection in [this\] column group. The other columns will be included in the selection
         *  by default. The scope of the selector is relative to the column group.
         * @set [ColumnGroupDocs.ARGUMENT_1] `  { "age"  `[and][ColumnsSelectionDsl.and]` height }`
         * @set [ColumnGroupDocs.ARGUMENT_2] ` { firstName }`
         */
        typealias SelectorArgs = Nothing

        /**
         * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [Strings][String] referring to
         *  the columns (relative to the column group) that need to be excluded from the current selection in [this\]
         *  column group. The other columns will be included in the selection by default.
         * @set [ColumnGroupDocs.ARGUMENT_1] `("age", "height")`
         * @set [ColumnGroupDocs.ARGUMENT_2] `("firstName", "middleName")`
         */
        typealias StringArgs = Nothing

        /**
         * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [KProperties][KProperty] referring to
         *  the columns (relative to the column group) that need to be excluded from the current selection in [this\]
         *  column group. The other columns will be included in the selection by default.
         * @set [ColumnGroupDocs.ARGUMENT_1] `(Person::age, Person::height)`
         * @set [ColumnGroupDocs.ARGUMENT_2] `(Person::firstName, Person::middleName)`
         */
        typealias KPropertyArgs = Nothing

        /**
         * @set [CommonExceptDocs.PARAM] @param [others\] Any number of [ColumnPaths][ColumnPath] referring to
         *  the columns (relative to the column group) that need to be excluded from the current selection in [this\]
         *  column group. The other columns will be included in the selection by default.
         * @set [ColumnGroupDocs.ARGUMENT_1] `(pathOf("age"), "extraData"["item1"])`
         * @set [ColumnGroupDocs.ARGUMENT_2] `(pathOf("firstName"), "middleNames"["first"])`
         */
        typealias ColumnPathArgs = Nothing
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
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun SingleColumn<DataRow<*>>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE_VARARG),
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.SingleColumnReceiverArgs]
     * @include [ColumnGroupDocs.ColumnPathArgs]
     */
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg other: ColumnPath): ColumnSet<*> =
        allColsExceptInternal(other.toColumnSet())

    // endregion

    // region except

    /**
     * @include [CommonExceptDocs]
     * @set [CommonExceptDocs.EXAMPLE] {@comment <code> blocks are there to prevent double ``}
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `<code>{@get [RECEIVER_1]}</code>[except][{@get [RECEIVER_TYPE]}.except]<code>{@get [ARGUMENT_1]}</code>` \}`
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  { city  `[and][ColumnsSelectionDsl.and]` `<code>{@get [RECEIVER_2]}</code>[except][{@get [RECEIVER_TYPE]}.except]<code>{@get [ARGUMENT_2]}</code>` \}`
     */
    @ExcludeFromSources
    private interface ColumnGroupExceptDocs : ColumnGroupDocs

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.SingleColumnReceiverArgs]
     * @include [ColumnGroupExceptDocs.SelectorArgs]
     */
    public fun <C> SingleColumn<DataRow<C>>.except(selector: ColumnsSelector<C, *>): SingleColumn<DataRow<C>> =
        exceptInternal(selector.toColumns())

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun <C> SingleColumn<DataRow<C>>.except(vararg others: ColumnsResolver<*>): SingleColumn<DataRow<C>> =
        except { others.toColumnSet() }

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.SingleColumnReceiverArgs]
     * @include [ColumnGroupExceptDocs.StringArgs]
     */
    public fun <C> SingleColumn<DataRow<C>>.except(vararg others: String): SingleColumn<DataRow<C>> =
        exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.SingleColumnReceiverArgs]
     * @include [ColumnGroupExceptDocs.KPropertyArgs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<C>>.except(vararg others: KProperty<*>): SingleColumn<DataRow<C>> =
        exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.SingleColumnReceiverArgs]
     * @include [ColumnGroupExceptDocs.ColumnPathArgs]
     */
    public fun <C> SingleColumn<DataRow<C>>.except(vararg others: ColumnPath): SingleColumn<DataRow<C>> =
        exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.StringReceiverArgs]
     * @include [ColumnGroupExceptDocs.SelectorArgs]
     */
    public fun String.except(selector: ColumnsSelector<*, *>): SingleColumn<DataRow<*>> =
        columnGroup(this).except(selector)

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun String.except(other: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        except { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun String.except(vararg others: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        except { others.toColumnSet() }

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.StringReceiverArgs]
     * @include [ColumnGroupExceptDocs.StringArgs]
     */
    public fun String.except(vararg others: String): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.StringReceiverArgs]
     * @include [ColumnGroupExceptDocs.KPropertyArgs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun String.except(vararg others: KProperty<*>): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.StringReceiverArgs]
     * @include [ColumnGroupExceptDocs.ColumnPathArgs]
     */
    public fun String.except(vararg others: ColumnPath): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupExceptDocs.SelectorArgs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.except(selector: ColumnsSelector<C, *>): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(selector.toColumns())

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    @AccessApiOverload
    public fun KProperty<*>.except(other: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        except { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    @AccessApiOverload
    public fun KProperty<*>.except(vararg others: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        except { others.toColumnSet() }

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupExceptDocs.StringArgs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.except(vararg others: String): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupExceptDocs.StringArgs]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowExceptNew")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<DataRow<C>>.except(vararg others: String): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupExceptDocs.KPropertyArgs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.except(vararg others: KProperty<*>): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupExceptDocs.KPropertyArgs]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowExceptNew")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<DataRow<C>>.except(vararg others: KProperty<*>): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupExceptDocs.ColumnPathArgs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.except(vararg others: ColumnPath): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupExceptDocs.ColumnPathArgs]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowExceptNew")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<DataRow<C>>.except(vararg others: ColumnPath): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.ColumnPathReceiverArgs]
     * @include [ColumnGroupExceptDocs.SelectorArgs]
     */
    public fun ColumnPath.except(selector: ColumnsSelector<*, *>): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(selector.toColumns<Any?, Any?>())

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun ColumnPath.except(other: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        except { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun ColumnPath.except(vararg others: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        except { others.toColumnSet() }

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.ColumnPathReceiverArgs]
     * @include [ColumnGroupExceptDocs.StringArgs]
     */
    public fun ColumnPath.except(vararg others: String): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.ColumnPathReceiverArgs]
     * @include [ColumnGroupExceptDocs.KPropertyArgs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun ColumnPath.except(vararg others: KProperty<*>): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupExceptDocs]
     * @include [ColumnGroupExceptDocs.ColumnPathReceiverArgs]
     * @include [ColumnGroupExceptDocs.ColumnPathArgs]
     */
    public fun ColumnPath.except(vararg others: ColumnPath): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    // endregion
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
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun String.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE_VARARG),
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun String.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.StringReceiverArgs]
     * @include [ColumnGroupDocs.ColumnPathArgs]
     */
    public fun String.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion

    // region KProperty

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupDocs.SelectorArgs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    @AccessApiOverload
    public fun KProperty<*>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    @AccessApiOverload
    public fun KProperty<*>.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupDocs.StringArgs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsExcept(vararg others: String): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupDocs.KPropertyArgs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.KPropertyReceiverArgs]
     * @include [ColumnGroupDocs.ColumnPathArgs]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
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
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun ColumnPath.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE_VARARG),
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

    /**
     * @include [ColumnGroupDocs]
     * @include [ColumnGroupDocs.ColumnPathReceiverArgs]
     * @include [ColumnGroupDocs.ColumnPathArgs]
     */
    public fun ColumnPath.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion
}

/**
 * Removes the columns in the "other" ColumnsResolver from the current ColumnSet while keeping the structure intact.
 * Returns a new ColumnSet with the remaining columns.
 *
 * @param other The ColumnsResolver containing the columns to be removed.
 * @return The new ColumnSet with the remaining columns.
 */
@Suppress("UNCHECKED_CAST")
internal fun <C> ColumnSet<C>.exceptInternal(other: ColumnsResolver<*>): ColumnSet<C> =
    createColumnSet { context ->
        val resolvedCols = this.resolve(context)
        val resolvedColsToExcept = other.resolve(context).toSet()
        resolvedCols.allColumnsExceptKeepingStructure(resolvedColsToExcept)
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

/**
 * Returns a new SingleColumn<DataRow<C>> that has the same structure as the receiver, but excludes columns
 * specified in the "other" ColumnsResolver.
 *
 * @param other The [ColumnsResolver] to use for excluding columns.
 * @return A new [SingleColumn] with the filtered columns excluded.
 */
@Suppress("UNCHECKED_CAST")
@JvmName("exceptInternalSingleColumn")
internal fun <C> SingleColumn<DataRow<C>>.exceptInternal(other: ColumnsResolver<*>): SingleColumn<DataRow<C>> =
    ensureIsColumnGroup().transformSingle { singleCol ->
        val columnsToExcept = singleCol
            .asColumnGroup()
            .getColumnsWithPaths { other }
            .map { it.changePath(singleCol.path + it.path) }
            .toSet()

        val newCols = listOf(singleCol).allColumnsExceptKeepingStructure(columnsToExcept)

        newCols as List<ColumnWithPath<DataRow<*>>>
    }.singleInternal() as SingleColumn<DataRow<C>>

// endregion
