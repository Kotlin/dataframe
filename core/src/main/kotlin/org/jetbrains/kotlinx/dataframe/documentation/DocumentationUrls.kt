package org.jetbrains.kotlinx.dataframe.documentation

private interface DocumentationUrls {

    interface NameArg

    /** See {@includeArg [NameArg]} on the documentation website. */
    interface Text

    interface DataRow {

        /** [{@include [Text]}{@arg [NameArg] Row Expressions}](https://kotlin.github.io/dataframe/datarow.html#row-expressions) */
        interface RowExpressions

        /** [{@include [Text]}{@arg [NameArg] Row Conditions}](https://kotlin.github.io/dataframe/datarow.html#row-conditions) */
        interface RowConditions
    }

    /** [{@include [Text]}{@arg [NameArg] `update`}](https://kotlin.github.io/dataframe/update.html) */
    interface Update

    /** [{@include [Text]}{@arg [NameArg] `fill`}](https://kotlin.github.io/dataframe/fill.html) */
    interface Fill {

        /** [{@include [Text]}{@arg [NameArg] `fillNulls`}](https://kotlin.github.io/dataframe/fill.html#fillnulls) */
        interface FillNulls

        /** [{@include [Text]}{@arg [NameArg] `fillNaNs`}](https://kotlin.github.io/dataframe/fill.html#fillnans) */
        interface FillNaNs

        /** [{@include [Text]}{@arg [NameArg] `fillNA`}](https://kotlin.github.io/dataframe/fill.html#fillna) */
        interface FillNA
    }

    /** [{@include [Text]}{@arg [NameArg] Access APIs}](https://kotlin.github.io/dataframe/apilevels.html) */
    interface AccessApis {

        /** [{@include [Text]}{@arg [NameArg] String API}](https://kotlin.github.io/dataframe/stringapi.html) */
        interface StringApi

        /** [{@include [Text]}{@arg [NameArg] Column Accessors API}](https://kotlin.github.io/dataframe/columnaccessorsapi.html) */
        interface ColumnAccessorsApi

        /** [{@include [Text]}{@arg [NameArg] KProperties API}](https://kotlin.github.io/dataframe/kpropertiesapi.html) */
        interface KPropertiesApi

        /** [{@include [Text]}{@arg [NameArg] Extension Properties API}](https://kotlin.github.io/dataframe/extensionpropertiesapi.html) */
        interface ExtensionPropertiesApi
    }
}
