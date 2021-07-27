package org.jetbrains.dataframe.jupyter

import org.jetbrains.dataframe.images.Image
import org.jetbrains.dataframe.io.DisplayConfiguration
import org.jetbrains.dataframe.io.internallyRenderable
import org.jetbrains.dataframe.io.renderValueForHtml
import org.jetbrains.dataframe.io.tooltipLimit
import org.jetbrains.kotlinx.jupyter.api.MimeTypedResult
import org.jetbrains.kotlinx.jupyter.api.Notebook
import org.jetbrains.kotlinx.jupyter.api.Renderable
import org.jetbrains.kotlinx.jupyter.api.libraries.ExecutionHost

public data class RenderedContent(val content: String, val textLength: Int) {
    public companion object {
        public fun text(str: String): RenderedContent = RenderedContent(str, str.length)
    }

    public operator fun plus(other: RenderedContent): RenderedContent = RenderedContent(content + other.content, textLength + other.textLength)
}

public interface CellRenderer {
    /**
     * Returns [value] rendered to HTML text, or null if such rendering is impossible
     */
    public fun content(value: Any?, configuration: DisplayConfiguration): RenderedContent

    /**
     * Returns cell tooltip for this [value]
     */
    public fun tooltip(value: Any?, configuration: DisplayConfiguration): String
}

public abstract class ChainedCellRenderer(
    private val parent: CellRenderer,
) : CellRenderer {
    public abstract fun maybeContent(value: Any?, configuration: DisplayConfiguration): RenderedContent?
    public abstract fun maybeTooltip(value: Any?, configuration: DisplayConfiguration): String?

    public override fun content(value: Any?, configuration: DisplayConfiguration): RenderedContent {
        return maybeContent(value, configuration) ?: parent.content(value, configuration)
    }

    public override fun tooltip(value: Any?, configuration: DisplayConfiguration): String {
        return maybeTooltip(value, configuration) ?: parent.tooltip(value, configuration)
    }
}

public object DefaultCellRenderer : CellRenderer {
    public override fun content(value: Any?, configuration: DisplayConfiguration): RenderedContent {
        return renderValueForHtml(value, configuration.cellContentLimit)
    }

    public override fun tooltip(value: Any?, configuration: DisplayConfiguration): String {
        return renderValueForHtml(value, tooltipLimit).content
    }
}

public object ImageCellRenderer : ChainedCellRenderer(DefaultCellRenderer) {
    public override fun maybeContent(value: Any?, configuration: DisplayConfiguration): RenderedContent? {
        if (value is Image) {
            return RenderedContent("<img src=\"${value.url}\"/>", 0)
        } else return null
    }

    public override fun maybeTooltip(value: Any?, configuration: DisplayConfiguration): String? {
        return (value as? Image)?.url
    }
}

internal class JupyterCellRenderer(
    private val notebook: Notebook,
    private val host: ExecutionHost,
) : ChainedCellRenderer(ImageCellRenderer) {
    override fun maybeContent(value: Any?, configuration: DisplayConfiguration): RenderedContent? {
        val renderersProcessor = notebook.renderersProcessor
        if (internallyRenderable(value)) return null
        val renderedVal = renderersProcessor.renderValue(host, value)
        val finalVal = if (renderedVal is Renderable) renderedVal.render(notebook) else renderedVal
        if (finalVal is MimeTypedResult && "text/html" in finalVal) return RenderedContent(finalVal["text/html"] ?: "", 0)
        return renderValueForHtml(finalVal, configuration.cellContentLimit)
    }

    override fun maybeTooltip(value: Any?, configuration: DisplayConfiguration): String? {
        return null
    }
}
