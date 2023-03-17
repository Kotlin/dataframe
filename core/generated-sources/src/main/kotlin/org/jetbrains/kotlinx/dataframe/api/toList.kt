package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.toListImpl
import kotlin.reflect.typeOf

// region DataFrame

public inline fun <reified T> DataFrame<T>.toList(): List<T> = toListImpl(typeOf<T>()) as List<T>

public inline fun <reified T> AnyFrame.toListOf(): List<T> = toListImpl(typeOf<T>()) as List<T>

// endregion
