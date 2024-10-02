package org.jetbrains.kotlinx.dataframe.impl.io

import com.github.kittinunf.fuel.httpGet
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL

internal fun isCompressed(fileOrUrl: String) = listOf("gz", "zip").contains(fileOrUrl.split(".").last())

internal fun isCompressed(file: File) = listOf("gz", "zip").contains(file.extension)

internal fun isCompressed(url: URL) = isCompressed(url.path)

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
