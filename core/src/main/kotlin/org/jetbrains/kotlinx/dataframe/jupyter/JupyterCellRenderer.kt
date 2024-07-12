package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.internallyRenderable
import org.jetbrains.kotlinx.dataframe.io.renderValueForHtml
import org.jetbrains.kotlinx.jupyter.api.MimeTypedResult
import org.jetbrains.kotlinx.jupyter.api.Notebook
import org.jetbrains.kotlinx.jupyter.api.Renderable
import org.jetbrains.kotlinx.jupyter.api.libraries.ExecutionHost

internal class JupyterCellRenderer(
    private val notebook: Notebook,
    private val host: ExecutionHost,
) : ChainedCellRenderer(DefaultCellRenderer) {
    override fun maybeContent(value: Any?, configuration: DisplayConfiguration): RenderedContent? {
        val renderersProcessor = notebook.renderersProcessor
        if (internallyRenderable(value)) return null
        val renderedVal = renderersProcessor.renderValue(host, value)
        val finalVal = if (renderedVal is Renderable) renderedVal.render(notebook) else renderedVal
        if (finalVal is MimeTypedResult && "text/html" in finalVal) return RenderedContent.media(
            finalVal["text/html"] ?: ""
        )
        return renderValueForHtml(finalVal, configuration.cellContentLimit, configuration.decimalFormat)
    }

    override fun maybeTooltip(value: Any?, configuration: DisplayConfiguration): String? {
        return null
    }
}
