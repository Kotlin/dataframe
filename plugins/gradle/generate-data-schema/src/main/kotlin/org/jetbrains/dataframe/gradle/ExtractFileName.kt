package org.jetbrains.dataframe.gradle

import java.io.File
import java.net.MalformedURLException
import java.net.URL

fun extractFileName(url: URL): String? {
    return url.path.takeIf { it.isNotEmpty() }
        ?.substringAfterLast("/")
        ?.substringBeforeLast(".")
}

fun extractFileName(file: File): String {
    return file.nameWithoutExtension
}

fun extractFileName(path: String): String? {
    return try {
        val url = URL(path)
        extractFileName(url)
    } catch (e: MalformedURLException) {
        val file = File(path)
        extractFileName(file)
    }
}
