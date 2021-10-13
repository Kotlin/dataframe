package org.jetbrains.kotlinx.dataframe.annotations

import org.jetbrains.kotlinx.dataframe.AnyCol
import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
public annotation class ColumnType(val type: KClass<out AnyCol>)
