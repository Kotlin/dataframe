package org.jetbrains.kotlinx.dataframe.io

import com.github.kittinunf.fuel.httpGet
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.guessColumnType
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL

internal fun catchHttpResponse(url: URL, body: (InputStream) -> AnyFrame): AnyFrame {
    try {
        return url.openStream().use(body)
    } catch (e: IOException) {
        if (e.message?.startsWith("Server returned HTTP response code") == true) {
            val (_, response, _) = url.toString().httpGet().responseString()
            try {
                return DataFrame.readJsonStr(response.data.decodeToString())
            } catch (e2: Exception) {
                throw e
            }
        }
        throw e
    }
}

public fun <T> List<List<T>>.toDataFrame(containsColumns: Boolean = false): AnyFrame = when {
    containsColumns -> {
        mapNotNull {
            if (it.isEmpty()) return@mapNotNull null
            val name = it[0].toString()
            val values = it.drop(1)
            guessColumnType(name, values)
        }.toDataFrame()
    }
    isEmpty() -> emptyDataFrame(0)
    else -> {
        val header = get(0).map { it.toString() }
        val data = drop(1)
        header.mapIndexed { colIndex, name ->
            val values = data.map { row ->
                if (row.size <= colIndex) null
                else row[colIndex]
            }
            guessColumnType(name, values)
        }.toDataFrame()
    }
}

internal fun String.isURL(): Boolean = listOf("http:", "https:", "ftp:").any { startsWith(it) }

internal fun URL.isFile(): Boolean = protocol == "file"

internal fun URL.asFileOrNull(): File? = if (isFile()) File(path) else null

internal fun URL.asFile(): File = asFileOrNull()!!

internal fun URL.isProtocolSupported(): Boolean = protocol in setOf("http", "https", "ftp")
