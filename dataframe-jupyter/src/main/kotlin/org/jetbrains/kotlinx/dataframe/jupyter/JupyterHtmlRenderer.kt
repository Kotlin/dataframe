package org.jetbrains.kotlinx.dataframe.jupyter

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.addAll
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.impl.io.BufferedImageEncoder
import org.jetbrains.kotlinx.dataframe.impl.io.DataframeConvertableEncoder
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.COLUMNS
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.KOTLIN_DATAFRAME
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.NCOL
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.NROW
import org.jetbrains.kotlinx.dataframe.impl.io.encodeFrame
import org.jetbrains.kotlinx.dataframe.io.Base64ImageEncodingOptions
import org.jetbrains.kotlinx.dataframe.io.CustomEncoder
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.toHTML
import org.jetbrains.kotlinx.dataframe.io.toJsonWithMetadata
import org.jetbrains.kotlinx.dataframe.io.toStaticHtml
import org.jetbrains.kotlinx.dataframe.jupyter.KotlinNotebookPluginUtils.convertToDataFrame
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.jupyter.api.HtmlData
import org.jetbrains.kotlinx.jupyter.api.JupyterClientType
import org.jetbrains.kotlinx.jupyter.api.KotlinKernelVersion
import org.jetbrains.kotlinx.jupyter.api.MimeTypedResult
import org.jetbrains.kotlinx.jupyter.api.Notebook
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import org.jetbrains.kotlinx.jupyter.api.mimeResult
import org.jetbrains.kotlinx.jupyter.api.outputs.isIsolatedHtml
import org.jetbrains.kotlinx.jupyter.api.renderHtmlAsIFrameIfNeeded

/** Starting from this version, dataframe integration will respond with additional data for rendering in Kotlin Notebooks plugin. */
private const val MIN_KERNEL_VERSION_FOR_NEW_TABLES_UI = "0.11.0.311"
private const val MIN_IDE_VERSION_SUPPORT_JSON_WITH_METADATA = 241
private const val MIN_IDE_VERSION_SUPPORT_IMAGE_VIEWER = 242
private const val MIN_IDE_VERSION_SUPPORT_DATAFRAME_CONVERTABLE = 243

internal class JupyterHtmlRenderer(val display: DisplayConfiguration, val builder: JupyterIntegration.Builder)

@OptIn(ExperimentalSerializationApi::class)
internal inline fun <reified T : Any> JupyterHtmlRenderer.render(
    noinline getFooter: (T) -> String,
    crossinline modifyConfig: T.(DisplayConfiguration) -> DisplayConfiguration = { it },
    applyRowsLimit: Boolean = true,
) = builder.renderWithHost<T> { host, value ->
    val contextRenderer = JupyterCellRenderer(this.notebook, host)
    val reifiedDisplayConfiguration = value.modifyConfig(display)
    val footer = getFooter(value)

    val df = convertToDataFrame(value)

    val limit = if (applyRowsLimit) {
        reifiedDisplayConfiguration.rowsLimit ?: df.rowsCount()
    } else {
        df.rowsCount()
    }

    val html = DataFrameHtmlData
        .tableDefinitions(
            includeJs = reifiedDisplayConfiguration.isolatedOutputs,
            includeCss = true,
        ).plus(
            df.toHTML(
                // is added later to make sure it's put outside of potential iFrames
                configuration = reifiedDisplayConfiguration.copy(enableFallbackStaticTables = false),
                cellRenderer = contextRenderer,
            ) { footer },
        ).toJupyterHtmlData()

    // Generates a static version of the table which can be displayed in GitHub previews etc.
    val staticHtml = df.toStaticHtml(reifiedDisplayConfiguration, DefaultCellRenderer).toJupyterHtmlData()

    if (notebook.kernelVersion >= KotlinKernelVersion.from(MIN_KERNEL_VERSION_FOR_NEW_TABLES_UI)!!) {
        val ideBuildNumber = KotlinNotebookPluginUtils.getKotlinNotebookIDEBuildNumber()

        // TODO Do we need to handle the improved meta data here as well?
        val jsonEncodedDf = when {
            !ideBuildNumber.supportsDynamicNestedTables() -> {
                buildJsonObject {
                    put(NROW, df.rowsCount())
                    put(NCOL, df.columnsCount())
                    putJsonArray(COLUMNS) { addAll(df.columnNames()) }
                    put(KOTLIN_DATAFRAME, encodeFrame(df.take(limit)))
                }.toString()
            }

            else -> {
                val encoders = buildList<CustomEncoder> {
                    if (ideBuildNumber.supportsDataFrameConvertableValues()) {
                        add(DataframeConvertableEncoder(this))
                    }
                    if (ideBuildNumber.supportsImageViewer()) {
                        add(BufferedImageEncoder(Base64ImageEncodingOptions()))
                    }
                }

                df.toJsonWithMetadata(
                    rowLimit = limit,
                    nestedRowLimit = reifiedDisplayConfiguration.rowsLimit,
                    customEncoders = encoders,
                )
            }
        }

        notebook.renderAsIFrameAsNeeded(html, staticHtml, jsonEncodedDf)
    } else {
        notebook.renderHtmlAsIFrameIfNeeded(html)
    }
}

private fun KotlinNotebookPluginUtils.IdeBuildNumber?.supportsDynamicNestedTables() =
    this != null && majorVersion >= MIN_IDE_VERSION_SUPPORT_JSON_WITH_METADATA

private fun KotlinNotebookPluginUtils.IdeBuildNumber?.supportsImageViewer() =
    this != null && majorVersion >= MIN_IDE_VERSION_SUPPORT_IMAGE_VIEWER

private fun KotlinNotebookPluginUtils.IdeBuildNumber?.supportsDataFrameConvertableValues() =
    this != null && majorVersion >= MIN_IDE_VERSION_SUPPORT_DATAFRAME_CONVERTABLE

internal fun Notebook.renderAsIFrameAsNeeded(
    data: HtmlData,
    staticData: HtmlData,
    jsonEncodedDf: String,
): MimeTypedResult {
    val textHtml = if (jupyterClientType == JupyterClientType.KOTLIN_NOTEBOOK) {
        data.generateIframePlaneText(currentColorScheme) +
            staticData.toString(currentColorScheme)
    } else {
        (data + staticData).toString(currentColorScheme)
    }

    return mimeResult(
        "text/html" to textHtml,
        "application/kotlindataframe+json" to jsonEncodedDf,
    ).also { it.isIsolatedHtml = false }
}

internal fun DataFrameHtmlData.toJupyterHtmlData() = HtmlData(style, body, script)
