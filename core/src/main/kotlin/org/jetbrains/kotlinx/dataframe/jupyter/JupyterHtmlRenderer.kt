package org.jetbrains.kotlinx.dataframe.jupyter

import com.beust.klaxon.json
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.io.*
import org.jetbrains.kotlinx.dataframe.io.initHtml
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.jupyter.api.*
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration

internal class JupyterHtmlRenderer(
    val display: DisplayConfiguration,
    val builder: JupyterIntegration.Builder,
)

internal inline fun <reified T : Any> JupyterHtmlRenderer.render(
    noinline getFooter: (T) -> String,
    crossinline modifyConfig: T.(DisplayConfiguration) -> DisplayConfiguration = { it },
    applyRowsLimit: Boolean = true
) = builder.renderWithHost<T> { host, value ->
    val contextRenderer = JupyterCellRenderer(this.notebook, host)
    val reifiedDisplayConfiguration = value.modifyConfig(display)
    val footer = getFooter(value)

    val df = convertToDataFrame(value)

    val limit = if (applyRowsLimit) {
        reifiedDisplayConfiguration.rowsLimit ?: df.nrow
    } else {
        df.nrow
    }

    val jsonEncodedDf = json {
        obj(
            "nrow" to df.size.nrow,
            "ncol" to df.size.ncol,
            "columns" to df.columnNames(),
            "kotlin_dataframe" to encodeFrame(df.rows().take(limit).toDataFrame())
        )
    }.toJsonString()
    val html = df.toHTML(
        reifiedDisplayConfiguration,
        extraHtml = initHtml(
            includeJs = reifiedDisplayConfiguration.isolatedOutputs,
            includeCss = true,
            useDarkColorScheme = reifiedDisplayConfiguration.useDarkColorScheme
        ),
        contextRenderer
    ) { footer }

    notebook.renderAsIFrameAsNeeded(html, jsonEncodedDf)
}

internal fun Notebook.renderAsIFrameAsNeeded(data: HtmlData, jsonEncodedDf: String): MimeTypedResult {
    val textHtml = if (jupyterClientType == JupyterClientType.KOTLIN_NOTEBOOK) {
        data.generateIframePlaneText(currentColorScheme)
    } else {
        data.toString(currentColorScheme)
    }

    return mimeResult(
        "text/html" to textHtml,
        "application/json" to jsonEncodedDf
    ).also { it.isolatedHtml = false }
}
