package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.convertToImpl
import kotlin.reflect.typeOf

public enum class ExtraColumns { Remove, Keep, Fail }

// region DataFrame

public inline fun <reified T> AnyFrame.convertTo(extraColumnsBehavior: ExtraColumns = ExtraColumns.Keep): DataFrame<T> = convertToImpl(typeOf<T>(), true, extraColumnsBehavior)

// endregion
