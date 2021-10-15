package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.Many
import kotlin.reflect.KType

private val listTypes = mutableMapOf<KType, KType>()

public fun getListType(valueType: KType): KType = listTypes.getOrPut(valueType) { Many::class.createTypeWithArgument(valueType) }
