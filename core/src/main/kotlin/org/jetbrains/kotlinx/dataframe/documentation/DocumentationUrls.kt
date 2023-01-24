package org.jetbrains.kotlinx.dataframe.documentation

internal interface DocumentationUrls {

    /** [See `fill` documentation.](https://kotlin.github.io/dataframe/fill.html) */
    interface Fill {

        /** [See `fillNulls` documentation](https://kotlin.github.io/dataframe/fill.html#fillnulls) */
        interface FillNulls

        /** [See `fillNaNs` documentation](https://kotlin.github.io/dataframe/fill.html#fillnans) */
        interface FillNaNs

        /** [See `fillNA` documentation](https://kotlin.github.io/dataframe/fill.html#fillna) */
        interface FillNA
    }

    /** [See Access APIs documentation.](https://kotlin.github.io/dataframe/apilevels.html) */
    interface AccessApis {

        /** [See String API documentation.](https://kotlin.github.io/dataframe/stringapi.html) */
        interface StringApi

        /** [See Column Accessors API documentation.](https://kotlin.github.io/dataframe/columnaccessorsapi.html) */
        interface ColumnAccessorsApi

        /** [See KProperties API documentation.](https://kotlin.github.io/dataframe/kpropertiesapi.html) */
        interface KPropertiesApi

        /** [See Extension Properties API documentation.](https://kotlin.github.io/dataframe/extensionpropertiesapi.html) */
        interface ExtensionPropertiesApi
    }
}
