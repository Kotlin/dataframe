package org.jetbrains.dataframe.annotations

@Target(AnnotationTarget.PROPERTY)
annotation class ColumnName(val name: String)