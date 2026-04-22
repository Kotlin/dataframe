package org.jetbrains.kotlinx.dataframe.util

import kotlin.reflect.KType

@Suppress("INVISIBLE_REFERENCE")
internal fun renderType(type: KType?): String = org.jetbrains.kotlinx.dataframe.impl.renderType(type)
