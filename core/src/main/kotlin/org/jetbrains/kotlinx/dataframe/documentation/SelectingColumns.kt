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

/** [Selecting Columns][SelectingColumns] */
@ExcludeFromSources
internal typealias SelectingColumnsLink = Nothing

/*
 * Selecting Columns KDoc-topic.
 * Link to it with `@include [SelectingColumnsLink]`.
 */

/**
 * ## Selecting Columns
 *
 * Selecting columns for various [DataFrame] operations
 * can be done in the following ways:
 * ### 1. {@include [CSDslWithExampleLink]}
 * {@include [ColumnsSelectionDSL.ColumnsSelectionDSLWithExample]}
 * > There's also a 'single column' variant used sometimes: {@include [CSDslSingleWithExampleLink]}.
 * ### 2. {@include [ColumnNamesWithExampleLink]}
 * {@include [ColumnNamesAPI.ColumnNamesApiWithExample]}
 */
internal interface SelectingColumns {

    /*
     * Note about column groups and nested columns KDoc-snippet.
     * Paste it into KDoc using `@include [ColumnGroupsAndNestedColumnsSnippet]`.
     */

    /**
     * This can include [column groups][ColumnGroup] and nested columns.
     */
    @ExcludeFromSources
    typealias ColumnGroupsAndNestedColumnsSnippet = Nothing

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

    /*
     * Columns Selection DSL KDoc-topic.
     * Link to it with `@include [CSDslLink]`
     * or paste it into KDoc with `@include [`Columns Selection DSL`]`.
     */

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
    interface ColumnsSelectionDSL {

        /*
         * Columns Selection DSL with example KDoc-topic.
         * Link to it with `@include [CSDslWithExampleLink]`
         * or paste it into KDoc with `@include [`Columns Selection DSL with Example`]`
         */

        /**
         * {@include [ColumnsSelectionDSL]}
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
        typealias ColumnsSelectionDSLWithExample = Nothing
    }

    /** [Columns Selection DSL][ColumnsSelectionDSL] */
    @ExcludeFromSources
    typealias CSDslLink = Nothing

    /** [Columns Selection DSL][ColumnsSelectionDSL.ColumnsSelectionDSLWithExample] */
    @ExcludeFromSources
    typealias CSDslWithExampleLink = Nothing

    /*
     * Column Selection DSL KDoc-topic.
     * Link to it with `@include [CSDslSingleLink]`
     * or paste it into KDoc with `@include [`Column Selection DSL`]`.
     */

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
    interface ColumnSelectionDSL {

        /*
         * Column Selection DSL with example KDoc-topic.
         * Link to it with `@include [CSDslSingleWithExampleLink]`
         * or paste it into KDoc with `@include [`Column Selection DSL with Example`]`
         */

        /**
         * {@include [ColumnSelectionDSL]}
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
        typealias ColumnsSelectionDSLWithExample = Nothing
    }

    /** [Column Selection DSL][ColumnSelectionDSL] */
    @ExcludeFromSources
    typealias CSDslSingleLink = Nothing

    /** [Column Selection DSL][ColumnSelectionDSL.ColumnsSelectionDSLWithExample] */
    @ExcludeFromSources
    typealias CSDslSingleWithExampleLink = Nothing

    /*
     * Column Names API KDoc-topic.
     * Link to it with `@include [ColumnNamesLink]`
     * or paste it into KDoc with `@include [`Column Names API`]`.
     */

    /**
     * Select single or multiple columns using their names as [String]s.
     * ({@include [AccessAPIs.StringApiLink]}).
     */
    interface ColumnNamesAPI {

        /*
         * CColumn Names API with Example KDoc-topic.
         * Link to it with `@include [ColumnNamesWithExampleLink]`
         * or paste it into KDoc with `@include [`Column Names API with Example`]`.
         */

        /**
         * {@include [ColumnNamesAPI]}
         *
         * #### For example:
         *
         * {@get [RECEIVER]}`.`{@get [OPERATION]}`("length", "age")`
         *
         * @include [SetDefaultOperationArg]
         * @include [SetDefaultReceiverArg]
         */
        typealias ColumnNamesApiWithExample = Nothing
    }

    /** [Column names][ColumnNamesAPI] */
    @ExcludeFromSources
    typealias ColumnNamesLink = Nothing

    /** [Column names][ColumnNamesAPI.ColumnNamesApiWithExample] */
    @ExcludeFromSources
    typealias ColumnNamesWithExampleLink = Nothing
}
