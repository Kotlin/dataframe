package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.annotations.RequiredByIntellijPlugin

/**
 * Allows for disabling the rows limit when generating a DISPLAY output in Jupyter.
 * Used in code that Kotlin Notebook sends to Jupyter Kernel / KotlinNotebookPluginUtils
 * @param [addHtml] needed to avoid sending HTML in these internal requests, because they won't be displayed anyway, only JSON part of these payloads is used to update "live" state of the widget
 */
@RequiredByIntellijPlugin
public data class DisableRowsLimitWrapper(public val value: AnyFrame, public val addHtml: Boolean = true)
