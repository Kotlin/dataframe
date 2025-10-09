package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.annotations.RequiredByIntellijPlugin

/**
 * Allows for disabling the rows limit when generating a DISPLAY output in Jupyter.
 */
@RequiredByIntellijPlugin
public data class DisableRowsLimitWrapper(public val value: AnyFrame)
