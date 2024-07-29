package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.renderValueForHtml
import org.jetbrains.kotlinx.dataframe.io.tooltipLimit

public data class RenderedContent(
    val truncatedContent: String,
    val textLength: Int,
    val fullContent: String?,
    val isFormatted: Boolean,
) {
    public companion object {

        public fun media(html: String): RenderedContent = RenderedContent(html, 0, null, false)

        public fun textWithLength(str: String, len: Int): RenderedContent = RenderedContent(str, len, null, false)

        public fun text(str: String): RenderedContent = RenderedContent(str, str.length, null, false)

        public fun truncatedText(str: String, fullText: String): RenderedContent =
            RenderedContent(str, str.length, fullText, false)
    }

    val isTruncated: Boolean
        get() = fullContent != null

    public operator fun plus(other: RenderedContent): RenderedContent =
        RenderedContent(
            truncatedContent = truncatedContent + other.truncatedContent,
            textLength = textLength + other.textLength,
            fullContent = fullContent?.plus(other.fullContent) ?: other.fullContent,
            isFormatted = isFormatted || other.isFormatted,
        )
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

public abstract class ChainedCellRenderer(private val parent: CellRenderer) : CellRenderer {
    public abstract fun maybeContent(value: Any?, configuration: DisplayConfiguration): RenderedContent?

    public abstract fun maybeTooltip(value: Any?, configuration: DisplayConfiguration): String?

    public override fun content(value: Any?, configuration: DisplayConfiguration): RenderedContent =
        maybeContent(value, configuration) ?: parent.content(value, configuration)

    public override fun tooltip(value: Any?, configuration: DisplayConfiguration): String =
        maybeTooltip(value, configuration) ?: parent.tooltip(value, configuration)
}

public object DefaultCellRenderer : CellRenderer {
    public override fun content(value: Any?, configuration: DisplayConfiguration): RenderedContent =
        renderValueForHtml(value, configuration.cellContentLimit, configuration.decimalFormat)

    public override fun tooltip(value: Any?, configuration: DisplayConfiguration): String =
        renderValueForHtml(value, tooltipLimit, configuration.decimalFormat).truncatedContent
}
