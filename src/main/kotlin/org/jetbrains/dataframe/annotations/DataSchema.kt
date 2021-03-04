package org.jetbrains.dataframe.annotations

@Target(AnnotationTarget.CLASS)
annotation class DataSchema(val isOpen: Boolean = true)