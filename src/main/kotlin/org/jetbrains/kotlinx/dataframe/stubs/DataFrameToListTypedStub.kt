package org.jetbrains.kotlinx.dataframe.stubs

import org.jetbrains.kotlinx.dataframe.AnyFrame
import kotlin.reflect.KClass

public data class DataFrameToListTypedStub(val df: AnyFrame, val interfaceClass: KClass<*>)
