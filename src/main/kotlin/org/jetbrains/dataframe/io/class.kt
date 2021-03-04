package org.jetbrains.dataframe.io

import org.jetbrains.dataframe.AnyFrame
import org.jetbrains.dataframe.stubs.DataFrameToListNamedStub
import org.jetbrains.dataframe.stubs.DataFrameToListTypedStub

inline fun <reified C> AnyFrame.writeClass() = DataFrameToListTypedStub(this, C::class)
fun AnyFrame.writeClass(className: String) = DataFrameToListNamedStub(this, className)