package org.jetbrains.kotlinx.dataframe.documentation

internal interface DocumentationUrls {

    interface NameArg

    /** See {@getArg [NameArg]} on the documentation website. */
    interface Text

    /** https://kotlin.github.io/dataframe */
    interface Url

    interface DataRow {

        /** [{@include [Text]}{@setArg [NameArg] Row Expressions}]({@include [Url]}/datarow.html#row-expressions) */
        interface RowExpressions

        /** [{@include [Text]}{@setArg [NameArg] Row Conditions}]({@include [Url]}/datarow.html#row-conditions) */
        interface RowConditions
    }

    /** [{@include [Text]}{@setArg [NameArg] `NaN` and `NA`}]({@include [Url]}/nanAndNa.html) */
    interface NanAndNa {

        /** [{@include [Text]}{@setArg [NameArg] `NaN`}]({@include [Url]}/nanAndNa.html#nan) */
        interface NaN

        /** [{@include [Text]}{@setArg [NameArg] `NA`}]({@include [Url]}/nanAndNa.html#na) */
        interface NA
    }

    /** [{@include [Text]}{@setArg [NameArg] `update`}]({@include [Url]}/update.html) */
    interface Update

    /** [{@include [Text]}{@setArg [NameArg] `fill`}]({@include [Url]}/fill.html) */
    interface Fill {

        /** [{@include [Text]}{@setArg [NameArg] `fillNulls`}]({@include [Url]}/fill.html#fillnulls) */
        interface FillNulls

        /** [{@include [Text]}{@setArg [NameArg] `fillNaNs`}]({@include [Url]}/fill.html#fillnans) */
        interface FillNaNs

        /** [{@include [Text]}{@setArg [NameArg] `fillNA`}]({@include [Url]}/fill.html#fillna) */
        interface FillNA
    }

    /** [{@include [Text]}{@setArg [NameArg] `drop`}]({@include [Url]}/drop.html) */
    interface Drop {

        /** [{@include [Text]}{@setArg [NameArg] `dropNulls`}]({@include [Url]}/drop.html#dropnulls) */
        interface DropNulls

        /** [{@include [Text]}{@setArg [NameArg] `dropNaNs`}]({@include [Url]}/drop.html#dropnans) */
        interface DropNaNs

        /** [{@include [Text]}{@setArg [NameArg] `dropNA`}]({@include [Url]}/drop.html#dropna) */
        interface DropNA
    }

    /** [{@include [Text]}{@setArg [NameArg] Access APIs}]({@include [Url]}/apilevels.html) */
    interface AccessApis {

        /** [{@include [Text]}{@setArg [NameArg] String API}]({@include [Url]}/stringapi.html) */
        interface StringApi

        /** [{@include [Text]}{@setArg [NameArg] Column Accessors API}]({@include [Url]}/columnaccessorsapi.html) */
        interface ColumnAccessorsApi

        /** [{@include [Text]}{@setArg [NameArg] KProperties API}]({@include [Url]}/kpropertiesapi.html) */
        interface KPropertiesApi

        /** [{@include [Text]}{@setArg [NameArg] Extension Properties API}]({@include [Url]}/extensionpropertiesapi.html) */
        interface ExtensionPropertiesApi
    }

    /** [{@include [Text]}{@setArg [NameArg] Column Selectors}]({@include [Url]}/columnselectors.html) */
    interface ColumnSelectors
}
