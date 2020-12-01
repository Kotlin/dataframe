package org.jetbrains.dataframe

inline fun <reified C> DataFrame<*>.toList() = DataFrameToListTypedStub(this, C::class)
fun DataFrame<*>.toList(className: String) = DataFrameToListNamedStub(this, className)