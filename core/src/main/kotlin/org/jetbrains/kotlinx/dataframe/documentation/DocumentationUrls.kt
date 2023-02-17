package org.jetbrains.kotlinx.dataframe.documentation

private interface DocumentationUrls {

    interface NameArg

    /** See {@includeArg [NameArg]} on the documentation website. */
    interface Text

    /** https://kotlin.github.io/dataframe */
    interface Url

    interface DataRow {

        /** [{@include [Text]}{@arg [NameArg] Row Expressions}]({@include [Url]}/datarow.html#row-expressions) */
        interface RowExpressions

        /** [{@include [Text]}{@arg [NameArg] Row Conditions}]({@include [Url]}/datarow.html#row-conditions) */
        interface RowConditions
    }

    /** [{@include [Text]}{@arg [NameArg] `update`}]({@include [Url]}/update.html) */
    interface Update

    /** [{@include [Text]}{@arg [NameArg] `fill`}]({@include [Url]}/fill.html) */
    interface Fill {

        /** [{@include [Text]}{@arg [NameArg] `fillNulls`}]({@include [Url]}/fill.html#fillnulls) */
        interface FillNulls

        /** [{@include [Text]}{@arg [NameArg] `fillNaNs`}]({@include [Url]}/fill.html#fillnans) */
        interface FillNaNs

        /** [{@include [Text]}{@arg [NameArg] `fillNA`}]({@include [Url]}/fill.html#fillna) */
        interface FillNA
    }

    /** [{@include [Text]}{@arg [NameArg] Access APIs}]({@include [Url]}/apilevels.html) */
    interface AccessApis {

        /** [{@include [Text]}{@arg [NameArg] String API}]({@include [Url]}/stringapi.html) */
        interface StringApi

        /** [{@include [Text]}{@arg [NameArg] Column Accessors API}]({@include [Url]}/columnaccessorsapi.html) */
        interface ColumnAccessorsApi

        /** [{@include [Text]}{@arg [NameArg] KProperties API}]({@include [Url]}/kpropertiesapi.html) */
        interface KPropertiesApi

        /** [{@include [Text]}{@arg [NameArg] Extension Properties API}]({@include [Url]}/extensionpropertiesapi.html) */
        interface ExtensionPropertiesApi
    }
}
