package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.stubs.DataFrameToListNamedStub
import org.jetbrains.kotlinx.dataframe.stubs.DataFrameToListTypedStub

public inline fun <reified C> AnyFrame.writeClass(): DataFrameToListTypedStub {
    check(C::class.java.isInterface) { "Interface class is expected" }
    return DataFrameToListTypedStub(this, C::class)
}

public fun AnyFrame.writeClass(className: String): DataFrameToListNamedStub = DataFrameToListNamedStub(this, className)
