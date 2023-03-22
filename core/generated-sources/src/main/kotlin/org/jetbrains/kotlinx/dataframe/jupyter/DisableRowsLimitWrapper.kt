package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.AnyFrame

/**
 * Allows for disabling the rows limit when generating a DISPLAY output in Jupyter.
 */
public data class DisableRowsLimitWrapper(public val value: AnyFrame)
