package org.jetbrains.kotlinx.dataframe.api

import kotlin.reflect.typeOf
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.toSequenceImpl

// region DataFrame

public inline fun <reified T> DataFrame<T>.toList(): List<T> =
    toSequenceImpl(typeOf<T>()).toList() as List<T>

public inline fun <reified T> AnyFrame.toListOf(): List<T> =
    toSequenceImpl(typeOf<T>()).toList() as List<T>

// endregion
