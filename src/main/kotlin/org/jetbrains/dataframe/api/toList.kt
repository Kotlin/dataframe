package org.jetbrains.dataframe

inline fun <reified C> AnyFrame.toList() = DataFrameToListTypedStub(this, C::class)
fun AnyFrame.toList(className: String) = DataFrameToListNamedStub(this, className)