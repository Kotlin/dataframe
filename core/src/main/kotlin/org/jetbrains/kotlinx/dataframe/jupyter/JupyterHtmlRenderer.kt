package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.initHtml
import org.jetbrains.kotlinx.dataframe.io.toHTML
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import org.jetbrains.kotlinx.jupyter.api.renderHtmlAsIFrameIfNeeded

internal class JupyterHtmlRenderer(
    val display: DisplayConfiguration,
    val builder: JupyterIntegration.Builder,
)

internal inline fun <reified T : Any> JupyterHtmlRenderer.render(
    crossinline getDf: (T) -> DataFrame<*>,
    noinline getFooter: (T) -> String,
    crossinline modifyConfig: T.(DisplayConfiguration) -> DisplayConfiguration = { it }
) = builder.renderWithHost<T> { host, value ->
    val contextRenderer = JupyterCellRenderer(this.notebook, host)
    val reifiedDisplayConfiguration = value.modifyConfig(display)
    val footer = getFooter(value)
    val html = getDf(value).toHTML(
        reifiedDisplayConfiguration,
        extraHtml = initHtml(
            includeJs = reifiedDisplayConfiguration.isolatedOutputs,
            includeCss = true,
            useDarkColorScheme = reifiedDisplayConfiguration.useDarkColorScheme
        ),
        contextRenderer
    ) { footer }

    notebook.renderHtmlAsIFrameIfNeeded(html)
}
