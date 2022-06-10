package org.jetbrains.kotlinx.dataframe.plugin

import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter.*
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.api.RenameClauseApproximation
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

public typealias ExpectedArgumentProvider<T> = PropertyDelegateProvider<Any?, ReadOnlyProperty<Arguments, T>>

public fun <T> AbstractInterpreter<T>.schema(
    name: ArgumentName? = null
): ExpectedArgumentProvider<PluginDataFrameSchema> = arg(name)

public fun <T> AbstractInterpreter<T>.varargString(
    name: ArgumentName? = null
): ExpectedArgumentProvider<List<String>> = arg(name)

public fun <T> AbstractInterpreter<T>.renameClause(
    name: ArgumentName? = null
): ExpectedArgumentProvider<RenameClauseApproximation> = arg(name)

public fun <T> AbstractInterpreter<T>.columnsSelector(
    name: ArgumentName? = null
): ExpectedArgumentProvider<List<String>> = arg(name)
