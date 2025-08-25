package org.jetbrains.kotlinx.dataframe.annotations

import kotlin.reflect.KClass

/**
 * Matches the type parameter of the annotated class to DataRow/DataFrame type parameter T.
 *
 * Annotate public API classes that implement DataRow/DataFrame interface to enable "extract schema/create column from values" operation:
 * ```kotlin
 * df.add {
 *   "col" from { it }
 * }
 * ```
 * Result before:
 * `col: DataColumn<AddDataRow<MySchema>>`
 *
 * Result after:
 *
 * ```
 * col:
 *   col1: Int
 *   col2: String
 * ```
 */
@Target(AnnotationTarget.CLASS)
public annotation class HasSchema(val schemaArg: Int)

/**
 * Compiler plugin will evaluate compile time value of the annotated function.
 * Needed because some function calls only serve as a part of overall compile time DataSchema evaluation
 * There's no need to update return type of such calls
 */
public annotation class Interpretable(val interpreter: String)

/**
 * Compiler plugin will replace return type of calls to the annotated function
 */
public annotation class Refine

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FILE, AnnotationTarget.EXPRESSION)
public annotation class DisableInterpretation

@Target(AnnotationTarget.PROPERTY)
public annotation class Order(val order: Int)

/**
 * For internal use
 * Compiler plugin materializes schemas as classes.
 * These classes have two kinds of properties:
 * 1. Scope properties that only serve as a reference for internal property resolution
 * 2. Schema properties that reflect dataframe structure
 * Scope properties need
 * to be excluded in IDE plugin and in [org.jetbrains.kotlinx.dataframe.codeGen.MarkersExtractor.get]
 * This annotation serves to distinguish between the two where needed
 */
@Target(AnnotationTarget.PROPERTY)
public annotation class ScopeProperty

@Target(AnnotationTarget.FUNCTION)
internal annotation class Check

/**
 * One of the design goals of the library is typed access to columns.
 * That's why all operations that have "column" parameters have 4 overloads: https://kotlin.github.io/dataframe/apilevels.html
 * In Kotlin Notebook and in Gradle project with the compiler plugin, Column Accessors API and KProperties API become redundant and
 * clutter API scope of DataFrame. This annotation indicates such functions so that they can be excluded from public API
 */
@Target(AnnotationTarget.FUNCTION)
internal annotation class AccessApiOverload

/**
 * Provides argument to `ToSpecificType` interpreter - to what type compiler plugin should convert selected columns
 */
@Target(AnnotationTarget.FUNCTION)
public annotation class Converter(val klass: KClass<*>, val nullable: Boolean = false)
