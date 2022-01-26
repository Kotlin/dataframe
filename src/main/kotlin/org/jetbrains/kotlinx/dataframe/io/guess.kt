package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.single
import java.io.File
import java.net.URL

public enum class SupportedFormats {
    CSV,
    JSON
}

internal fun guessFormat(file: File): SupportedFormats? = when (file.extension.toLowerCase()) {
    "json" -> SupportedFormats.JSON
    "csv" -> SupportedFormats.CSV
    else -> null
}

internal fun guessFormat(url: URL): SupportedFormats? = guessFormat(url.path)

internal fun guessFormat(url: String): SupportedFormats? = when {
    url.endsWith(".csv") -> SupportedFormats.CSV
    url.endsWith(".json") -> SupportedFormats.JSON
    else -> null
}

public fun DataFrame.Companion.read(file: File): AnyFrame = when (guessFormat(file)) {
    SupportedFormats.CSV -> readCSV(file)
    SupportedFormats.JSON -> readJson(file)
    else -> try {
        readCSV(file)
    } catch (e: Exception) {
        readJson(file)
    }
}

public fun DataRow.Companion.read(file: File): AnyRow = DataFrame.read(file).single()

public fun DataFrame.Companion.read(url: URL): AnyFrame = when (guessFormat(url)) {
    SupportedFormats.CSV -> readCSV(url)
    SupportedFormats.JSON -> readJson(url)
    else -> try {
        readCSV(url)
    } catch (e: Exception) {
        readJson(url)
    }
}

public fun DataRow.Companion.read(url: URL): AnyRow = DataFrame.read(url).single()

public fun DataFrame.Companion.read(path: String): AnyFrame = when (guessFormat(path)) {
    SupportedFormats.CSV -> readCSV(path)
    SupportedFormats.JSON -> readJson(path)
    else -> try {
        readCSV(path)
    } catch (e: Exception) {
        readJson(path)
    }
}

public fun DataRow.Companion.read(path: String): AnyRow = DataFrame.read(path).single()

public fun URL.readDataFrame(): AnyFrame = DataFrame.read(this)
public fun URL.readDataRow(): AnyRow = DataRow.read(this)

public fun File.readDataFrame(): AnyFrame = DataFrame.read(this)
public fun File.readDataRow(): AnyRow = DataRow.read(this)
