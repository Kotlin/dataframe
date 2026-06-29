@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

/*
 * Documentation URLs KDoc-snippets.
 *
 * Contains all links to Kotlin DataFrame documentation pages.
 *
 * Include it in KDoc with `@include [-class-]`.
 * For example: `For more information: {@include [DocumentationUrls.SomeUrl]}`.
 */
public interface DocumentationUrls {

    /** https://kotlin.github.io/dataframe */
    public typealias Url = Nothing

    /** [See Access APIs on the documentation website.]({@include [Url]}/apilevels.html) */
    public interface AccessApis {

        /** [See String API on the documentation website.]({@include [Url]}/stringapi.html) */
        public typealias StringApi = Nothing

        /** [See Extension Properties API on the documentation website.]({@include [Url]}/extensionpropertiesapi.html) */
        public typealias ExtensionPropertiesApi = Nothing
    }

    /** [See Column Selectors on the documentation website.]({@include [Url]}/columnselectors.html) */
    public typealias ColumnSelectors = Nothing

    /** [See Compiler Plugin on the documentation website.]({@include [Url]}/compiler-plugin.html) */
    public typealias CompilerPlugin = Nothing

    public interface DataRow {
        /** [See Row Expressions on the documentation website.]({@include [Url]}/datarow.html#row-expressions) */
        public typealias RowExpressions = Nothing

        /** [See RowExpression on the documentation website.]({@include [Url]}/datarow.html#rowexpression) */
        public typealias RowExpression = Nothing

        /** [See RowValueExpression on the documentation website.]({@include [Url]}/datarow.html#rowvalueexpression) */
        public typealias RowValueExpression = Nothing

        /** [See Row Conditions on the documentation website.]({@include [Url]}/datarow.html#row-conditions) */
        public typealias RowConditions = Nothing

        /** [See RowFilter on the documentation website.]({@include [Url]}/datarow.html#rowfilter) */
        public typealias RowFilter = Nothing

        /** [See RowValueFilter on the documentation website.]({@include [Url]}/datarow.html#rowvaluefilter) */
        public typealias RowValueFilter = Nothing
    }

    /** [See `drop` on the documentation website.]({@include [Url]}/drop.html) */
    public interface Drop {

        /** [See `dropNulls` on the documentation website.]({@include [Url]}/drop.html#dropnulls) */
        public typealias DropNulls = Nothing

        /** [See `dropNaNs` on the documentation website.]({@include [Url]}/drop.html#dropnans) */
        public typealias DropNaNs = Nothing

        /** [See `dropNA` on the documentation website.]({@include [Url]}/drop.html#dropna) */
        public typealias DropNA = Nothing
    }

    /** [See `fill` on the documentation website.]({@include [Url]}/fill.html) */
    public interface Fill {
        /** [See `fillNulls` on the documentation website.]({@include [Url]}/fill.html#fillnulls) */
        public typealias FillNulls = Nothing

        /** [See `fillNaNs` on the documentation website.]({@include [Url]}/fill.html#fillnans) */
        public typealias FillNaNs = Nothing

        /** [See `fillNA` on the documentation website.]({@include [Url]}/fill.html#fillna) */
        public typealias FillNA = Nothing
    }

    /** [See `NaN` and `NA` on the documentation website.]({@include [Url]}/nanAndNa.html) */
    public interface NanAndNa {

        /** [See `NaN` on the documentation website.]({@include [Url]}/nanAndNa.html#nan) */
        public typealias NaN = Nothing

        /** [See `NA` on the documentation website.]({@include [Url]}/nanAndNa.html#na) */
        public typealias NA = Nothing
    }

    /** [See `select` on the documentation website.]({@include [Url]}/select.html) */
    public typealias Select = Nothing

    /** [See `update` on the documentation website.]({@include [Url]}/update.html) */
    public typealias Update = Nothing

    /** [See `remove` on the documentation website.]({@include [Url]}/remove.html) */
    public typealias Remove = Nothing

    /** [See `distinct` on the documentation website.]({@include [Url]}/distinct.html) */
    public typealias Distinct = Nothing

    /** [See `distinctBy` on the documentation website.]({@include [Url]}/distinct.html#distinctby) */
    public typealias DistinctBy = Nothing

    /** <a href="{@include [Url]}/flatten.html">See `flatten` on the documentation website.</a> */
    public typealias Flatten = Nothing

    /** <a href="{@include [Url]}/cumsum.html">See `cumSum` on the documentation website.</a> */
    public typealias CumSum = Nothing

    /** [See `describe` on the documentation website.]({@include [Url]}/describe.html) */
    public typealias Describe = Nothing

    /** [See `move` on the documentation website.]({@include [Url]}/move.html) */
    public typealias Move = Nothing

    /** [See `group` on the documentation website.]({@include [Url]}/group.html) */
    public typealias Group = Nothing

    /** [See `group` on the documentation website.]({@include [Url]}/ungroup.html) */
    public typealias Ungroup = Nothing

    /** [See `convert` on the documentation website.]({@include [Url]}/convert.html) */
    public typealias Convert = Nothing

    /** [See `convert` on the documentation website.]({@include [Url]}/corr.html) */
    public typealias Corr = Nothing

    /** [See `add` on the documentation website.]({@include [Url]}/add.html) */
    public typealias Add = Nothing

    /** [See `gather` on the documentation website.]({@include [Url]}/gather.html) */
    public typealias Gather = Nothing

    /** [See `filter` on the documentation website.]({@include [Url]}/filter.html) */
    public typealias Filter = Nothing

    /** [See `count` on the documentation website.]({@include [Url]}/count.html) */
    public typealias Count = Nothing

    /** [See `countDistinct` on the documentation website.]({@include [Url]}/countdistinct.html) */
    public typealias CountDistinct = Nothing

    /** [See `explode` on the documentation website.]({@include [Url]}/explode.html) */
    public typealias Explode = Nothing

    /** [See `Data Schemas/Data Classes Generation` on the documentation website.]({@include [Url]}/dataschemagenerationmethods.html) */
    public typealias DataSchemaGeneration = Nothing

    /** [See `format` on the documentation website.]({@include [Url]}/format.html) */
    public typealias Format = Nothing

    /** [See `insert` on the documentation website.]({@include [Url]}/insert.html) */
    public typealias Insert = Nothing

    /** [See `rename` on the documentation website.]({@include [Url]}/rename.html) */
    public typealias Rename = Nothing

    /** [See `groupBy` on the documentation website.]({@include [Url]}/groupby.html) */
    public typealias GroupBy = Nothing

    /** [See "`GroupBy` Transformation" on the documentation website.]({@include [Url]}/groupby.html#transformation) */
    public typealias GroupByTransformation = Nothing

    /** [See "`GroupBy` Reducing" on the documentation website.]({@include [Url]}/groupby.html#reducing) */
    public typealias GroupByReducing = Nothing

    /** [See "`GroupBy` Aggregation" on the documentation website.]({@include [Url]}/groupby.html#aggregation) */
    public typealias GroupByAggregation = Nothing

    /** [See "`groupBy` statistics" on the documentation website.]({@include [Url]}/summarystatistics.html#groupby-statistics) */
    public typealias GroupByStatistics = Nothing

    /** [See "`pivot` + `groupBy`" on the documentation website.]({@include [Url]}/pivot.html#pivot-groupby) */
    public typealias PivotGroupBy = Nothing

    /** [See `pivot` on the documentation website.]({@include [Url]}/pivot.html) */
    public typealias Pivot = Nothing

    /** [See `pivotMatches` on the documentation website.]({@include [Url]}/pivot.html#pivotmatches) */
    public typealias PivotMatches = Nothing

    /** [See `pivotCounts` on the documentation website.]({@include [Url]}/pivot.html#pivotcounts) */
    public typealias PivotCounts = Nothing

    /** [See "`pivot` statistics" on the documentation website.]({@include [Url]}/summarystatistics.html#pivot-statistics) */
    public typealias PivotStatistics = Nothing

    /** [See "Pivot` reducing" on the documentation website.]({@include [Url]}/pivot.html#reducing) */
    public typealias PivotReducing = Nothing

    /** [See "Pivot` Aggregation" on the documentation website.]({@include [Url]}/pivot.html#aggregation) */
    public typealias PivotAggregation = Nothing

    /** [See "`pivot` inside aggregation" on the documentation website.]({@include [Url]}/pivot.html#pivot-inside-aggregate) */
    public typealias PivotInsideAggregationStatistics = Nothing

    /** [See `join` on the documentation website.]({@include [Url]}/join.html) */
    public typealias Join = Nothing

    /** [See `joinWith` on the documentation website.]({@include [Url]}/joinWith.html) */
    public typealias JoinWith = Nothing

    /** [See "Summary statistics" on the documentation website.]({@include [Url]}/summarystatistics.html) */
    public typealias Statistics = Nothing

    /** [See `concat` on the documentation website.]({@include [Url]}/concat.html) */
    public typealias Concat = Nothing

    /** [Deephaven CSV](https://github.com/deephaven/deephaven-csv) */
    public typealias Deephaven = Nothing

    /** [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/) */
    public typealias ApacheCsv = Nothing
}
