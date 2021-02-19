package org.jetbrains.dataframe

inline fun <reified C> AnyFrame.writeClass() = DataFrameToListTypedStub(this, C::class)
fun AnyFrame.writeClass(className: String) = DataFrameToListNamedStub(this, className)