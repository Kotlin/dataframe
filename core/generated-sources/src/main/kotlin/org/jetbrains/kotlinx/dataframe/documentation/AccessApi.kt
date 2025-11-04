package org.jetbrains.kotlinx.dataframe.documentation

/**
 * ## Access APIs
 *
 * By nature, dataframes are dynamic objects, column labels depend on the input source and also new columns could be added
 * or deleted while wrangling. Kotlin, in contrast, is a statically typed language and all types are defined and verified
 * ahead of execution. That's why creating a flexible, handy, and, at the same time, safe API to a dataframe is tricky.
 *
 * In `Kotlin DataFrame` we provide four different ways to access columns, and, while they're essentially different, they
 * look pretty similar in the data wrangling DSL. These include:
 * - [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi]
 * - [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * - [Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]
 * - [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]
 *
 * For more information: [See Access APIs on the documentation website.](https://kotlin.github.io/dataframe/apilevels.html)
 *
 */
internal interface AccessApi {

    /**
     * - [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi]
     * - [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
     * - [Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]
     * - [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]
     */
    typealias AnyApiLinks = Nothing

    /**
     * String API.
     * In this [AccessApi], columns are accessed by a [String] representing their name.
     * Type-checking is done at runtime, name-checking too.
     *
     * For more information: [See String API on the documentation website.](https://kotlin.github.io/dataframe/stringapi.html)
     *
     * For example:
     * ```kotlin
     * DataFrame.read("titanic.csv")
     *     .add("lastName") { "name"<String>().split(",").last() }
     *     .dropNulls("age")
     *     .filter {
     *         "survived"<Boolean>() &&
     *             "home"<String>().endsWith("NY") &&
     *             "age"<Int>() in 10..20
     *     }
     * ```
     */
    typealias StringApi = Nothing

    /** [String API][StringApi] */
    typealias StringApiLink = Nothing

    /**
     * Column Accessors API.
     * In this [AccessApi], every column has a descriptor;
     * a variable that represents its name and type.
     *
     * For more information: [See Column Accessors API on the documentation website.](https://kotlin.github.io/dataframe/columnaccessorsapi.html)
     *
     * For example:
     * ```kotlin
     * val survived by column<Boolean>()
     * val home by column<String>()
     * val age by column<Int?>()
     * val name by column<String>()
     * val lastName by column<String>()
     *
     * DataFrame.read("titanic.csv")
     *     .add(lastName) { name().split(",").last() }
     *     .dropNulls { age }
     *     .filter { survived() && home().endsWith("NY") && age()!! in 10..20 }
     * ```
     */
    typealias ColumnAccessorsApi = Nothing

    /** [Column Accessors API][AccessApi.ColumnAccessorsApi] */
    typealias ColumnAccessorsApiLink = Nothing

    /**
     * KProperties API.
     * In this [AccessApi], columns accessed by the
     * [`KProperty`](https://kotlinlang.org/docs/reflection.html#property-references)
     * of some class.
     * The name and type of column should match the name and type of property, respectively.
     *
     * For more information: [See KProperties API on the documentation website.](https://kotlin.github.io/dataframe/kpropertiesapi.html)
     *
     * For example:
     * ```kotlin
     * data class Passenger(
     *     val survived: Boolean,
     *     val home: String,
     *     val age: Int,
     *     val lastName: String
     * )
     *
     * val passengers = DataFrame.read("titanic.csv")
     *     .add(Passenger::lastName) { "name"<String>().split(",").last() }
     *     .dropNulls(Passenger::age)
     *     .filter {
     *         it[Passenger::survived] &&
     *             it[Passenger::home].endsWith("NY") &&
     *             it[Passenger::age] in 10..20
     *     }
     *     .toListOf<Passenger>()
     * ```
     */
    typealias KPropertiesApi = Nothing

    /** [KProperties API][KPropertiesApi] */
    typealias KPropertiesApiLink = Nothing

    /**
     * Extension Properties API.
     * In this [AccessApi], extension access properties are generated based on the dataframe schema.
     * The name and type of properties are inferred from the name and type of the corresponding columns.
     *
     * For more information: [See Extension Properties API on the documentation website.](https://kotlin.github.io/dataframe/extensionpropertiesapi.html)
     *
     * For example:
     * ```kotlin
     * val df /* : AnyFrame */ = DataFrame.read("titanic.csv")
     * ```
     */
    typealias ExtensionPropertiesApi = Nothing

    /** [Extension Properties API][ExtensionPropertiesApi] */
    typealias ExtensionPropertiesApiLink = Nothing
}

/** [Access API][AccessApi] */
internal typealias AccessApiLink = Nothing
