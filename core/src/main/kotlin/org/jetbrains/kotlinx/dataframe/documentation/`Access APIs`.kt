package org.jetbrains.kotlinx.dataframe.documentation

/**
 * ## Access APIs
 *
 * By nature, dataframes are dynamic objects, column labels depend on the input source,
 * and also new columns could be added
 * or deleted while wrangling.
 * Kotlin, in contrast, is a statically typed language,
 * and all types are defined and verified
 * ahead of execution. That's why creating a flexible, handy, and,
 * at the same time, safe API to a dataframe is tricky.
 *
 * In `Kotlin DataFrame` we provide four different ways to access columns,
 * and, while they're essentially different, they
 * look pretty similar in the data wrangling DSL. These include:
 * @include [AnyApiLinks]
 *
 * For more information: {@include [DocumentationUrls.AccessApis]}
 *
 * @comment We can link to here whenever we want to explain the different access APIs.
 */
@Suppress("ClassName")
internal interface `Access APIs` {

    /**
     * - {@include [ExtensionPropertiesApiLink]}
     * - {@include [KPropertiesApiLink]}
     * - {@include [ColumnAccessorsApiLink]}
     * - {@include [StringApiLink]}
     */
    typealias AnyApiLinks = Nothing

    /**
     * String API.
     * In this [`Access APIs`], columns are accessed by a [String] representing their name.
     * Type-checking is done at runtime, name-checking too.
     *
     * For more information: {@include [DocumentationUrls.AccessApis.StringApi]}
     *
     * For example: {@comment This works if you include the test module when running KoDEx}
     * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.strings]
     */
    typealias StringApi = Nothing

    /** [String API][StringApi] */
    typealias StringApiLink = Nothing

    /**
     * Column Accessors API.
     * In this [`Access APIs`], every column has a descriptor;
     * a variable that represents its name and type.
     *
     * For more information: {@include [DocumentationUrls.AccessApis.ColumnAccessorsApi]}
     *
     * For example: {@comment This works if you include the test module when running KoDEx}
     * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.accessors3]
     */
    typealias ColumnAccessorsApi = Nothing

    /** [Column Accessors API][`Access APIs`.ColumnAccessorsApi] */
    typealias ColumnAccessorsApiLink = Nothing

    /**
     * KProperties API.
     * In this [`Access APIs`], columns accessed by the
     * [`KProperty`](https://kotlinlang.org/docs/reflection.html#property-references)
     * of some class.
     * The name and type of column should match the name and type of property, respectively.
     *
     * For more information: {@include [DocumentationUrls.AccessApis.KPropertiesApi]}
     *
     * For example: {@comment This works if you include the test module when running KoDEx}
     * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.kproperties1]
     */
    typealias KPropertiesApi = Nothing

    /** [KProperties API][KPropertiesApi] */
    typealias KPropertiesApiLink = Nothing

    /**
     * Extension Properties API.
     * In this [`Access APIs`], extension access properties are generated based on the dataframe schema.
     * The name and type of properties are inferred from the name and type of the corresponding columns.
     *
     * For more information: {@include [DocumentationUrls.AccessApis.ExtensionPropertiesApi]}
     *
     * For example: {@comment This works if you include the test module when running KoDEx}
     * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.extensionProperties1]
     */
    typealias ExtensionPropertiesApi = Nothing

    /** [Extension Properties API][ExtensionPropertiesApi] */
    typealias ExtensionPropertiesApiLink = Nothing
}

/** [Access API][`Access APIs`] */
internal typealias AccessApiLink = Nothing
