package org.jetbrains.kotlinx.dataframe.annotations

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

public annotation class OptInRefine

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FILE, AnnotationTarget.EXPRESSION)
public annotation class DisableInterpretation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.EXPRESSION)
public annotation class Import
