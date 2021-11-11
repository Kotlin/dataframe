package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.Many
import kotlin.reflect.KType

private val manyTypes = mutableMapOf<KType, KType>()

public fun getManyType(valueType: KType): KType = manyTypes.getOrPut(valueType) { Many::class.createTypeWithArgument(valueType) }
