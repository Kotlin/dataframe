package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.documentation.samples.ApiLevels as ApiLevelsSample

/**
 * By nature data frames are dynamic objects, column labels depend on the input source and also new columns could be added
 * or deleted while wrangling. Kotlin, in contrast, is a statically typed language and all types are defined and verified
 * ahead of execution. That's why creating a flexible, handy, and, at the same time, safe API to a data frame is tricky.
 *
 * In `Kotlin DataFrame` we provide four different ways to access columns, and, while they are essentially different, they
 * look pretty similar in the data wrangling DSL. These include:
 *  - [StringApi]
 *  - [ColumnAccessorsApi]
 *  - [KPropertiesApi]
 *  - [ExtensionPropertiesApi]
 *
 * For more information: {@include [DocumentationUrls.AccessApis]}
 *
 * @comment We can link to here whenever we want to explain the different access APIs.
 */
internal interface AccessApi {

    /** API:
     * - {@include [ExtensionPropertiesApiLink]}
     * - {@include [KPropertiesApiLink]}
     * - {@include [ColumnAccessorsApiLink]}
     * - {@include [StringApiLink]}
     * */
    interface AnyApiLinks

    /**
     * String API.
     * In this [AccessApi], columns are accessed by a [String] representing their name.
     * Type-checking is done at runtime, name-checking too.
     *
     * For more information: {@include [DocumentationUrls.AccessApis.StringApi]}
     *
     * For example:
     * @sample [ApiLevelsSample.strings]
     */
    interface StringApi

    /** [String API][StringApi] */
    interface StringApiLink

    /**
     * Column Accessors API.
     * In this [AccessApi], every column has a descriptor;
     * a variable that represents its name and type.
     *
     * For more information: {@include [DocumentationUrls.AccessApis.ColumnAccessorsApi]}
     *
     * For example:
     * @sample [ApiLevelsSample.accessors3]
     */
    interface ColumnAccessorsApi

    /** [Column Accessors API][AccessApi.ColumnAccessorsApi] */
    interface ColumnAccessorsApiLink

    /**
     * KProperties API.
     * In this [AccessApi], columns accessed by the
     * [`KProperty`](https://kotlinlang.org/docs/reflection.html#property-references)
     * of some class.
     * The name and type of column should match the name and type of property, respectively.
     *
     * For more information: {@include [DocumentationUrls.AccessApis.KPropertiesApi]}
     *
     * For example:
     * @sample [ApiLevelsSample.kproperties1]
     */
    interface KPropertiesApi

    /** [KProperties API][KPropertiesApi] */
    interface KPropertiesApiLink

    /**
     * Extension Properties API.
     * In this [AccessApi], extension access properties are generated based on the dataframe schema.
     * The name and type of properties are inferred from the name and type of the corresponding columns.
     *
     * For more information: {@include [DocumentationUrls.AccessApis.ExtensionPropertiesApi]}
     *
     * For example:
     * @sample [ApiLevelsSample.extensionProperties1]
     */
    interface ExtensionPropertiesApi

    /** [Extension Properties API][ExtensionPropertiesApi] */
    interface ExtensionPropertiesApiLink
}

/** [Access Api][AccessApi] */
internal interface AccessApiLink
