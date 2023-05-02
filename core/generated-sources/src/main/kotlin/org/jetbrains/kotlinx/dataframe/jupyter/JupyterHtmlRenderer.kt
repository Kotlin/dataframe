package org.jetbrains.kotlinx.dataframe.jupyter

import com.beust.klaxon.json
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.io.*
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.jupyter.api.*
import org.jetbrains.kotlinx.jupyter.api.HtmlData
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration

/** Starting from this version, dataframe integration will respond with additional data for rendering in Kotlin Notebooks plugin. */
private const val MIN_KERNEL_VERSION_FOR_NEW_TABLES_UI = "0.11.0.311"

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

    val html = (
        DataFrameHtmlData.tableDefinitions(
            includeJs = reifiedDisplayConfiguration.isolatedOutputs,
            includeCss = true
        ) + df.toHTML(
            reifiedDisplayConfiguration,
            contextRenderer
        ) { footer }
        ).toJupyterHtmlData()

    if (notebook.kernelVersion >= KotlinKernelVersion.from(MIN_KERNEL_VERSION_FOR_NEW_TABLES_UI)!!) {
        val jsonEncodedDf = json {
            obj(
                "nrow" to df.size.nrow,
                "ncol" to df.size.ncol,
                "columns" to df.columnNames(),
                "kotlin_dataframe" to encodeFrame(df.rows().take(limit).toDataFrame())
            )
        }.toJsonString()
        notebook.renderAsIFrameAsNeeded(html, jsonEncodedDf)
    } else {
        notebook.renderHtmlAsIFrameIfNeeded(html)
    }
}

internal fun Notebook.renderAsIFrameAsNeeded(data: HtmlData, jsonEncodedDf: String): MimeTypedResult {
    val textHtml = if (jupyterClientType == JupyterClientType.KOTLIN_NOTEBOOK) {
        data.generateIframePlaneText(currentColorScheme)
    } else {
        data.toString(currentColorScheme)
    }

    return mimeResult(
        "text/html" to textHtml,
        "application/kotlindataframe+json" to jsonEncodedDf
    ).also { it.isolatedHtml = false }
}

internal fun DataFrameHtmlData.toJupyterHtmlData() = HtmlData(style, body, script)
