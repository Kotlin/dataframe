package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.annotations.IntellijPluginApi

/**
 * Allows for disabling the rows limit when generating a DISPLAY output in Jupyter.
 */
@IntellijPluginApi
public data class DisableRowsLimitWrapper(public val value: AnyFrame)
