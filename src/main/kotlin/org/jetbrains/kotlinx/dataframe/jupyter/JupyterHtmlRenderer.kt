package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.getDefaultFooter
import org.jetbrains.kotlinx.dataframe.io.toHTML
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration

internal class JupyterHtmlRenderer(
    val display: DisplayConfiguration,
    val builder: JupyterIntegration.Builder,
)

internal inline fun <reified T : Any> JupyterHtmlRenderer.render(
    crossinline getDf: (T) -> DataFrame<*>,
    noinline getFooter: (DataFrame<*>) -> String = ::getDefaultFooter,
    crossinline modifyConfig: T.(DisplayConfiguration) -> DisplayConfiguration = { it }
) = builder.renderWithHost<T> { host, value ->
    val contextRenderer = JupyterCellRenderer(this.notebook, host)
    getDf(value).toHTML(value.modifyConfig(display), includeInit = true, contextRenderer, getFooter).toJupyter()
}
