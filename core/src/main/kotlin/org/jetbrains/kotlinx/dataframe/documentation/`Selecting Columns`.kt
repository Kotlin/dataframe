package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDslLink
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslLink
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn

/** [Selecting Columns][`Selecting Columns`] */
@ExcludeFromSources
internal typealias SelectingColumnsLink = Nothing

/**
 * ## Selecting Columns
 *
 * Selecting columns for various [DataFrame] operations
 * can be done in the following ways:
 * ### 1. {@include [CSDslWithExampleLink]}
 * {@include [`Columns Selection DSL`.`Columns Selection DSL with Example`]}
 * > There's also a 'single column' variant used sometimes: {@include [CSDslSingleWithExampleLink]}.
 * ### 2. {@include [ColumnNamesWithExampleLink]}
 * {@include [`Column Names API`.`Column Names API with Example`]}
 */
@Suppress("ClassName")
internal interface `Selecting Columns` {

    /**
     * This can include [column groups][ColumnGroup] and nested columns.
     */
    @ExcludeFromSources
    typealias ColumnGroupsAndNestedColumnsMention = Nothing

    /*
     * The key for a @set that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    @ExcludeFromSources
    typealias OPERATION = Nothing

    /*
     * Operation receiver variable name
     */
    @ExcludeFromSources
    typealias RECEIVER = Nothing
    // Using <code>` notation to not create double `` when including

    /** {@set [OPERATION] <code>`operation`</code>} */
    @ExcludeFromSources
    typealias SetDefaultOperationArg = Nothing

    /** {@set [RECEIVER] <code>`df`</code>} */
    @ExcludeFromSources
    typealias SetDefaultReceiverArg = Nothing

    /**
     * Select or express columns using the {@include [ColumnsSelectionDslLink]}.
     *
     * This DSL is initiated by a [Columns Selector][ColumnsSelector] lambda,
     * which operates in the context of the {@include [ColumnsSelectionDslLink]} and
     * expects you to return a [SingleColumn] or [ColumnSet] (so, a [ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * Check out: [Columns Selection DSL Grammar][ColumnsSelectionDsl.DslGrammar]
     * {@include [LineBreak]}
     * @include [DocumentationUrls.ColumnSelectors]
     */
    interface `Columns Selection DSL` {

        /**
         * {@include [`Columns Selection DSL`]}
         *
         * #### For example:
         *
         * {@get [RECEIVER]}`.`{@get [OPERATION]}` { length `[and][ColumnsSelectionDsl.and]` age }`
         *
         * {@get [RECEIVER]}`.`{@get [OPERATION]}`  {  `[cols][ColumnsSelectionDsl.cols]`(1..5) }`
         *
         * {@get [RECEIVER]}`.`{@get [OPERATION]}`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
         *
         * @include [SetDefaultOperationArg]
         * @include [SetDefaultReceiverArg]
         */
        typealias `Columns Selection DSL with Example` = Nothing
    }

    /** [Columns Selection DSL][`Columns Selection DSL`] */
    @ExcludeFromSources
    typealias CSDslLink = Nothing

    /** [Columns Selection DSL][`Columns Selection DSL`.`Columns Selection DSL with Example`] */
    @ExcludeFromSources
    typealias CSDslWithExampleLink = Nothing

    /**
     * Select or express a single column using the Column Selection DSL.
     * (Any {@include [AccessApiLink]}).
     *
     * This DSL is initiated by a [Column Selector][ColumnSelector] lambda,
     * which operates in context of the {@include [ColumnSelectionDslLink]} and
     * expects you to return a [SingleColumn].
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into a single column.
     *
     *
     * {@include [LineBreak]}
     * @include [DocumentationUrls.ColumnSelectors]
     */
    interface `Column Selection DSL` {

        /**
         * {@include [`Column Selection DSL`]}
         *
         * #### For example:
         *
         * {@get [RECEIVER]}`.`{@get [OPERATION]}` { length }`
         *
         * {@get [RECEIVER]}`.`{@get [OPERATION]}`  {  `[col][ColumnsSelectionDsl.col]`(1) }`
         *
         * {@get [RECEIVER]}`.`{@get [OPERATION]}`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>().`[first][ColumnsSelectionDsl.first]`() }`
         *
         * @include [SetDefaultOperationArg]
         * @include [SetDefaultReceiverArg]
         */
        typealias `Columns Selection DSL with Example` = Nothing
    }

    /** [Column Selection DSL][`Column Selection DSL`] */
    @ExcludeFromSources
    typealias CSDslSingleLink = Nothing

    /** [Column Selection DSL][`Column Selection DSL`.`Columns Selection DSL with Example`] */
    @ExcludeFromSources
    typealias CSDslSingleWithExampleLink = Nothing

    /**
     * Select single or multiple columns using their names as [String]s.
     * ({@include [`Access APIs`.StringApiLink]}).
     */
    interface `Column Names API` {

        /**
         * {@include [`Column Names API`]}
         *
         * #### For example:
         *
         * {@get [RECEIVER]}`.`{@get [OPERATION]}`("length", "age")`
         *
         * @include [SetDefaultOperationArg]
         * @include [SetDefaultReceiverArg]
         */
        typealias `Column Names API with Example` = Nothing
    }

    /** [Column names][`Column Names API`] */
    @ExcludeFromSources
    typealias ColumnNamesLink = Nothing

    /** [Column names][`Column Names API`.`Column Names API with Example`] */
    @ExcludeFromSources
    typealias ColumnNamesWithExampleLink = Nothing
}
