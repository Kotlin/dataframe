package org.jetbrains.kotlinx.dataframe.io

import com.github.kittinunf.fuel.httpGet
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType
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
            createColumnGuessingType(name, values)
        }.toDataFrame()
    }

    isEmpty() -> DataFrame.Empty
    else -> {
        val header = get(0).map { it.toString() }
        val data = drop(1)
        header.mapIndexed { colIndex, name ->
            val values = data.map { row ->
                if (row.size <= colIndex) null
                else row[colIndex]
            }
            createColumnGuessingType(name, values)
        }.toDataFrame()
    }
}

public fun isURL(path: String): Boolean = listOf("http:", "https:", "ftp:").any { path.startsWith(it) }

public fun isFile(url: URL): Boolean = url.protocol == "file"

public fun asFileOrNull(url: URL): File? = if (isFile(url)) File(url.path) else null

public fun urlAsFile(url: URL): File = File(url.path)

public fun isProtocolSupported(url: URL): Boolean = url.protocol in setOf("http", "https", "ftp")
