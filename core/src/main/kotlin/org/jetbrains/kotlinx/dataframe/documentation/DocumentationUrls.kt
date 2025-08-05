@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

internal interface DocumentationUrls {

    /** https://kotlin.github.io/dataframe */
    interface Url

    /** [See Access APIs on the documentation website.]({@include [Url]}/apilevels.html) */
    interface AccessApis {

        /** [See String API on the documentation website.]({@include [Url]}/stringapi.html) */
        interface StringApi

        /** [See Column Accessors API on the documentation website.]({@include [Url]}/columnaccessorsapi.html) */
        interface ColumnAccessorsApi

        /** [See KProperties API on the documentation website.]({@include [Url]}/kpropertiesapi.html) */
        interface KPropertiesApi

        /** [See Extension Properties API on the documentation website.]({@include [Url]}/extensionpropertiesapi.html) */
        interface ExtensionPropertiesApi
    }

    /** [See Column Selectors on the documentation website.]({@include [Url]}/columnselectors.html) */
    interface ColumnSelectors

    /** [See Compiler Plugin on the documentation website.]({@include [Url]}/compiler-plugin.html) */
    interface CompilerPlugin

    interface DataRow {
        /** [See Row Expressions on the documentation website.]({@include [Url]}/datarow.html#row-expressions) */
        interface RowExpressions

        /** [See Row Conditions on the documentation website.]({@include [Url]}/datarow.html#row-conditions) */
        interface RowConditions
    }

    /** [See `drop` on the documentation website.]({@include [Url]}/drop.html) */
    interface Drop {

        /** [See `dropNulls` on the documentation website.]({@include [Url]}/drop.html#dropnulls) */
        interface DropNulls

        /** [See `dropNaNs` on the documentation website.]({@include [Url]}/drop.html#dropnans) */
        interface DropNaNs

        /** [See `dropNA` on the documentation website.]({@include [Url]}/drop.html#dropna) */
        interface DropNA
    }

    /** [See `fill` on the documentation website.]({@include [Url]}/fill.html) */
    interface Fill {
        /** [See `fillNulls` on the documentation website.]({@include [Url]}/fill.html#fillnulls) */
        interface FillNulls

        /** [See `fillNaNs` on the documentation website.]({@include [Url]}/fill.html#fillnans) */
        interface FillNaNs

        /** [See `fillNA` on the documentation website.]({@include [Url]}/fill.html#fillna) */
        interface FillNA
    }

    /** [See `format` on the documentation website.]({@include [Url]}/format.html) */
    interface Format

    /** [See `NaN` and `NA` on the documentation website.]({@include [Url]}/nanAndNa.html) */
    interface NanAndNa {

        /** [See `NaN` on the documentation website.]({@include [Url]}/nanAndNa.html#nan) */
        interface NaN

        /** [See `NA` on the documentation website.]({@include [Url]}/nanAndNa.html#na) */
        interface NA
    }

    /** [See `select` on the documentation website.]({@include [Url]}/select.html) */
    interface Select

    /** [See `update` on the documentation website.]({@include [Url]}/update.html) */
    interface Update

    /** [See `remove` on the documentation website.]({@include [Url]}/remove.html) */
    interface Remove

    /** <a href="{@include [Url]}/distinct.html">See `distinct` on the documentation website.</a> */
    interface Distinct

    /** <a href="{@include [Url]}/flatten.html">See `flatten` on the documentation website.</a> */
    interface Flatten

    /** <a href="{@include [Url]}/cumsum.html">See `cumSum` on the documentation website.</a> */
    interface CumSum

    /** [See `describe` on the documentation website.]({@include [Url]}/describe.html) */
    interface Describe

    /** [See `move` on the documentation website.]({@include [Url]}/move.html) */
    interface Move

    /** [See `group` on the documentation website.]({@include [Url]}/group.html) */
    interface Group

    /** [See `group` on the documentation website.]({@include [Url]}/ungroup.html) */
    interface Ungroup

    /** [See `convert` on the documentation website.]({@include [Url]}/convert.html) */
    interface Convert

    /** [See `convert` on the documentation website.]({@include [Url]}/corr.html) */
    interface Corr

    /** [See `add` on the documentation website.]({@include [Url]}/add.html) */
    interface Add

    /** [See `gather` on the documentation website.]({@include [Url]}/gather.html) */
    interface Gather

    /** [See `filter` on the documentation website.]({@include [Url]}/filter.html) */
    interface Filter

    /** [See `explode` on the documentation website.]({@include [Url]}/explode.html) */
    interface Explode

    /** [See `Data Schemas/Data Classes Generation` on the documentation website.]({@include [Url]}/dataschemagenerationmethods.html) */
    interface DataSchemaGeneration
}
