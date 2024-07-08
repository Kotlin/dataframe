package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.jupyter.api.JupyterClientType
import org.jetbrains.kotlinx.jupyter.api.JupyterClientType.DATALORE
import org.jetbrains.kotlinx.jupyter.api.JupyterClientType.KOTLIN_NOTEBOOK
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelVersion

private const val UPDATING_DATALORE_URL = "https://github.com/Kotlin/kotlin-jupyter/tree/master#datalore"
private const val UPDATING_KOTLIN_NOTEBOOK_URL = "https://github.com/Kotlin/kotlin-jupyter#kotlin-notebook"
private const val UPDATING = "https://github.com/Kotlin/kotlin-jupyter/tree/master#updating"

internal fun getKernelUpdateMessage(
    kernelVersion: KotlinKernelVersion,
    minKernelVersion: String,
    clientType: JupyterClientType,
): String = buildString {
    append("Your Kotlin Jupyter kernel version appears to be out of date (version $kernelVersion). ")
    appendLine("Please update it to version $minKernelVersion or newer to be able to use DataFrame.")
    append("Follow the instructions at: ")

    when (clientType) {
        DATALORE -> appendLine(UPDATING_DATALORE_URL)
        KOTLIN_NOTEBOOK -> appendLine(UPDATING_KOTLIN_NOTEBOOK_URL)
        else -> appendLine(UPDATING)
    }
}
