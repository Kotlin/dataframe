package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.impl.api.requireImpl
import kotlin.reflect.typeOf

/**
 * Resolves [column] in this [DataFrame] and checks that its runtime type is a subtype of [C].
 * Throws if the column can't be resolved or if its type doesn't match.
 */
@Refine
@Interpretable("Require0")
public inline fun <T, reified C> DataFrame<T>.require(noinline column: ColumnSelector<T, C>): DataFrame<T> =
    requireImpl(column, typeOf<C>())
