package org.jetbrains.kotlinx.dataframe.annotations

@Target(AnnotationTarget.CLASS)
public annotation class HasSchema(val schemaArg: Int)

public annotation class Interpretable(val interpreter: String)

public annotation class Refine(val id: String)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FILE, AnnotationTarget.EXPRESSION)
public annotation class DisableInterpretation
