@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

internal interface DocumentationUrls {

    /** https://kotlin.github.io/dataframe */
    typealias Url = Nothing

    /** [See Access APIs on the documentation website.]({@include [Url]}/apilevels.html) */
    interface AccessApis {

        /** [See String API on the documentation website.]({@include [Url]}/stringapi.html) */
        typealias StringApi = Nothing

        /** [See Extension Properties API on the documentation website.]({@include [Url]}/extensionpropertiesapi.html) */
        typealias ExtensionPropertiesApi = Nothing
    }

    /** [See Column Selectors on the documentation website.]({@include [Url]}/columnselectors.html) */
    typealias ColumnSelectors = Nothing

    /** [See Compiler Plugin on the documentation website.]({@include [Url]}/compiler-plugin.html) */
    typealias CompilerPlugin = Nothing

    interface DataRow {
        /** [See Row Expressions on the documentation website.]({@include [Url]}/datarow.html#row-expressions) */
        typealias RowExpressions = Nothing

        /** [See Row Conditions on the documentation website.]({@include [Url]}/datarow.html#row-conditions) */
        typealias RowConditions = Nothing
    }

    /** [See `drop` on the documentation website.]({@include [Url]}/drop.html) */
    interface Drop {

        /** [See `dropNulls` on the documentation website.]({@include [Url]}/drop.html#dropnulls) */
        typealias DropNulls = Nothing

        /** [See `dropNaNs` on the documentation website.]({@include [Url]}/drop.html#dropnans) */
        typealias DropNaNs = Nothing

        /** [See `dropNA` on the documentation website.]({@include [Url]}/drop.html#dropna) */
        typealias DropNA = Nothing
    }

    /** [See `fill` on the documentation website.]({@include [Url]}/fill.html) */
    interface Fill {
        /** [See `fillNulls` on the documentation website.]({@include [Url]}/fill.html#fillnulls) */
        typealias FillNulls = Nothing

        /** [See `fillNaNs` on the documentation website.]({@include [Url]}/fill.html#fillnans) */
        typealias FillNaNs = Nothing

        /** [See `fillNA` on the documentation website.]({@include [Url]}/fill.html#fillna) */
        typealias FillNA = Nothing
    }

    /** [See `NaN` and `NA` on the documentation website.]({@include [Url]}/nanAndNa.html) */
    interface NanAndNa {

        /** [See `NaN` on the documentation website.]({@include [Url]}/nanAndNa.html#nan) */
        typealias NaN = Nothing

        /** [See `NA` on the documentation website.]({@include [Url]}/nanAndNa.html#na) */
        typealias NA = Nothing
    }

    /** [See `select` on the documentation website.]({@include [Url]}/select.html) */
    typealias Select = Nothing

    /** [See `update` on the documentation website.]({@include [Url]}/update.html) */
    typealias Update = Nothing

    /** [See `remove` on the documentation website.]({@include [Url]}/remove.html) */
    typealias Remove = Nothing

    /** [See `distinct` on the documentation website.]({@include [Url]}/distinct.html) */
    typealias Distinct = Nothing

    /** [See `distinctBy` on the documentation website.]({@include [Url]}/distinct.html#distinctby) */
    typealias DistinctBy = Nothing

    /** <a href="{@include [Url]}/flatten.html">See `flatten` on the documentation website.</a> */
    typealias Flatten = Nothing

    /** <a href="{@include [Url]}/cumsum.html">See `cumSum` on the documentation website.</a> */
    typealias CumSum = Nothing

    /** [See `describe` on the documentation website.]({@include [Url]}/describe.html) */
    typealias Describe = Nothing

    /** [See `move` on the documentation website.]({@include [Url]}/move.html) */
    typealias Move = Nothing

    /** [See `group` on the documentation website.]({@include [Url]}/group.html) */
    typealias Group = Nothing

    /** [See `group` on the documentation website.]({@include [Url]}/ungroup.html) */
    typealias Ungroup = Nothing

    /** [See `convert` on the documentation website.]({@include [Url]}/convert.html) */
    typealias Convert = Nothing

    /** [See `convert` on the documentation website.]({@include [Url]}/corr.html) */
    typealias Corr = Nothing

    /** [See `add` on the documentation website.]({@include [Url]}/add.html) */
    typealias Add = Nothing

    /** [See `gather` on the documentation website.]({@include [Url]}/gather.html) */
    typealias Gather = Nothing

    /** [See `filter` on the documentation website.]({@include [Url]}/filter.html) */
    typealias Filter = Nothing

    /** [See `count` on the documentation website.]({@include [Url]}/count.html) */
    typealias Count = Nothing

    /** [See `countDistinct` on the documentation website.]({@include [Url]}/countdistinct.html) */
    typealias CountDistinct = Nothing

    /** [See `explode` on the documentation website.]({@include [Url]}/explode.html) */
    typealias Explode = Nothing

    /** [See `Data Schemas/Data Classes Generation` on the documentation website.]({@include [Url]}/dataschemagenerationmethods.html) */
    typealias DataSchemaGeneration = Nothing

    /** [See `format` on the documentation website.]({@include [Url]}/format.html) */
    typealias Format = Nothing

    /** [See `insert` on the documentation website.]({@include [Url]}/insert.html) */
    typealias Insert = Nothing

    /** [See `rename` on the documentation website.]({@include [Url]}/rename.html) */
    typealias Rename = Nothing

    /** [See `groupBy` on the documentation website.]({@include [Url]}/groupby.html) */
    typealias GroupBy = Nothing

    /** [See "`GroupBy` Transformation" on the documentation website.]({@include [Url]}/groupby.html#transformation) */
    typealias GroupByTransformation = Nothing

    /** [See "`GroupBy` Reducing" on the documentation website.]({@include [Url]}/groupby.html#reducing) */
    typealias GroupByReducing = Nothing

    /** [See "`GroupBy` Aggregation" on the documentation website.]({@include [Url]}/groupby.html#aggregation) */
    typealias GroupByAggregation = Nothing

    /** [See "`groupBy` statistics" on the documentation website.]({@include [Url]}/summarystatistics.html#groupby-statistics) */
    typealias GroupByStatistics = Nothing

    /** [See "`pivot` + `groupBy`" on the documentation website.]({@include [Url]}/groupby.html#pivot-groupby) */
    typealias PivotGroupBy = Nothing

    /** [See `pivot` on the documentation website.]({@include [Url]}/pivot.html) */
    typealias Pivot = Nothing

    /** [See `pivotMatches` on the documentation website.]({@include [Url]}/pivot.html#pivotmatches) */
    typealias PivotMatches = Nothing

    /** [See `pivotCounts` on the documentation website.]({@include [Url]}/pivot.html#pivotcounts) */
    typealias PivotCounts = Nothing

    /** [See "`pivot` statistics" on the documentation website.]({@include [Url]}/summarystatistics.html#pivot-statistics) */
    typealias PivotStatistics = Nothing

    /** [See "Pivot` reducing" on the documentation website.]({@include [Url]}/pivot.html#reducing) */
    typealias PivotReducing = Nothing

    /** [See "Pivot` Aggregation" on the documentation website.]({@include [Url]}/pivot.html#aggregation) */
    typealias PivotAggregation = Nothing

    /** [See "`pivot` inside aggregation" on the documentation website.]({@include [Url]}/pivot.html#pivot-inside-aggregate) */
    typealias PivotInsideAggregationStatistics = Nothing

    /** [See `join` on the documentation website.]({@include [Url]}/join.html) */
    typealias Join = Nothing

    /** [See `joinWith` on the documentation website.]({@include [Url]}/joinWith.html) */
    typealias JoinWith = Nothing
}
