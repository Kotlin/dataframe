package org.jetbrains.dataframe.stubs

import org.jetbrains.dataframe.AnyFrame
import kotlin.reflect.KClass

data class DataFrameToListTypedStub(val df: AnyFrame, val interfaceClass: KClass<*>)