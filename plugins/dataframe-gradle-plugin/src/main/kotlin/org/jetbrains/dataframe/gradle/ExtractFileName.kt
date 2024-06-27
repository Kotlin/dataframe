package org.jetbrains.dataframe.gradle

import java.io.File
import java.net.MalformedURLException
import java.net.URL

internal fun extractFileName(url: URL): String? =
    url.path
        .takeIf { it.isNotEmpty() }
        ?.substringAfterLast("/")
        ?.substringBeforeLast(".")

internal fun extractFileName(file: File): String = file.nameWithoutExtension

internal fun extractFileName(path: String): String? =
    try {
        val url = URL(path)
        extractFileName(url)
    } catch (e: MalformedURLException) {
        val file = File(path)
        extractFileName(file)
    }
