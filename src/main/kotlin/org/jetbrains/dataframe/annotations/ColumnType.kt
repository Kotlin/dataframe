package org.jetbrains.dataframe.annotations

import org.jetbrains.dataframe.AnyCol
import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
annotation class ColumnType(val type: KClass<out AnyCol>)