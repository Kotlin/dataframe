package org.jetbrains.kotlinx.dataframe.documentation

internal interface DocumentationUrls {

    /** https://kotlin.github.io/dataframe */
    interface Url

    /** [See Access APIs on the documentation website.](https://kotlin.github.io/dataframe/apilevels.html) */
    interface AccessApis {

        /** [See String API on the documentation website.](https://kotlin.github.io/dataframe/stringapi.html) */
        interface StringApi

        /** [See Column Accessors API on the documentation website.](https://kotlin.github.io/dataframe/columnaccessorsapi.html) */
        interface ColumnAccessorsApi

        /** [See KProperties API on the documentation website.](https://kotlin.github.io/dataframe/kpropertiesapi.html) */
        interface KPropertiesApi

        /** [See Extension Properties API on the documentation website.](https://kotlin.github.io/dataframe/extensionpropertiesapi.html) */
        interface ExtensionPropertiesApi
    }

    /** [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html) */
    interface ColumnSelectors

    interface DataRow {
        /** [See Row Expressions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-expressions) */
        interface RowExpressions

        /** [See Row Conditions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-conditions) */
        interface RowConditions
    }

    /** [See `drop` on the documentation website.](https://kotlin.github.io/dataframe/drop.html) */
    interface Drop {

        /** [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls) */
        interface DropNulls

        /** [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans) */
        interface DropNaNs

        /** [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna) */
        interface DropNA
    }

    /** [See `fill` on the documentation website.](https://kotlin.github.io/dataframe/fill.html) */
    interface Fill {
        /** [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls) */
        interface FillNulls

        /** [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans) */
        interface FillNaNs

        /** [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna) */
        interface FillNA
    }

    /** [See `NaN` and `NA` on the documentation website.](https://kotlin.github.io/dataframe/nanAndNa.html) */
    interface NanAndNa {

        /** [See `NaN` on the documentation website.](https://kotlin.github.io/dataframe/nanAndNa.html#nan) */
        interface NaN

        /** [See `NA` on the documentation website.](https://kotlin.github.io/dataframe/nanAndNa.html#na) */
        interface NA
    }

    /** [See `select` on the documentation website.](https://kotlin.github.io/dataframe/select.html) */
    interface Select

    /** [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html) */
    interface Update
}
