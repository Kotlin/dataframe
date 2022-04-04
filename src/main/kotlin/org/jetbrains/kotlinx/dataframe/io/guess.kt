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
    TSV,
    JSON,
    ARROW,
    EXCEL,
}

internal fun guessFormat(file: File): SupportedFormats? = when (file.extension.toLowerCase()) {
    "json" -> SupportedFormats.JSON
    "csv" -> SupportedFormats.CSV
    "tsv" -> SupportedFormats.TSV
    "feather" -> SupportedFormats.ARROW
    "xls", "xlsx" -> SupportedFormats.EXCEL
    else -> null
}

internal fun guessFormat(url: URL): SupportedFormats? = guessFormat(url.path)

internal fun guessFormat(url: String): SupportedFormats? = when {
    url.endsWith(".csv") -> SupportedFormats.CSV
    url.endsWith(".tsv") -> SupportedFormats.TSV
    url.endsWith(".json") -> SupportedFormats.JSON
    url.endsWith(".feather") -> SupportedFormats.ARROW
    url.endsWith(".xls") || url.endsWith(".xlsx") -> SupportedFormats.EXCEL
    else -> null
}

public fun DataFrame.Companion.read(file: File): AnyFrame = when (guessFormat(file)) {
    SupportedFormats.CSV -> readCSV(file)
    SupportedFormats.TSV -> readTSV(file)
    SupportedFormats.JSON -> readJson(file)
    SupportedFormats.ARROW -> readArrow(file)
    SupportedFormats.EXCEL -> readExcel(file)
    null -> try {
        readExcel(file)
    } catch (e: Exception) {
        try {
            readCSV(file)
        } catch (e: Exception) {
            try {
                readTSV(file)
            } catch (e: Exception) {
                readJson(file)
            }
        }
    }
}

public fun DataRow.Companion.read(file: File): AnyRow = DataFrame.read(file).single()

public fun DataFrame.Companion.read(url: URL): AnyFrame = when (guessFormat(url)) {
    SupportedFormats.CSV -> readCSV(url)
    SupportedFormats.TSV -> readTSV(url)
    SupportedFormats.JSON -> readJson(url)
    SupportedFormats.ARROW -> readArrow(url)
    SupportedFormats.EXCEL -> readExcel(url)
    null -> try {
        readExcel(url)
    } catch (e: Exception) {
        try {
            readCSV(url)
        } catch (e: Exception) {
            try {
                readTSV(url)
            } catch (e: Exception) {
                readJson(url)
            }
        }
    }
}

public fun DataRow.Companion.read(url: URL): AnyRow = DataFrame.read(url).single()

public fun DataFrame.Companion.read(path: String): AnyFrame = when (guessFormat(path)) {
    SupportedFormats.CSV -> readCSV(path)
    SupportedFormats.TSV -> readTSV(path)
    SupportedFormats.JSON -> readJson(path)
    SupportedFormats.ARROW -> readArrow(path)
    SupportedFormats.EXCEL -> readExcel(path)
    null -> try {
        readExcel(path)
    } catch (e: Exception) {
        try {
            readCSV(path)
        } catch (e: Exception) {
            try {
                readTSV(path)
            } catch (e: Exception) {
                readJson(path)
            }
        }
    }
}

public fun DataRow.Companion.read(path: String): AnyRow = DataFrame.read(path).single()

public fun URL.readDataFrame(): AnyFrame = DataFrame.read(this)
public fun URL.readDataRow(): AnyRow = DataRow.read(this)

public fun File.readDataFrame(): AnyFrame = DataFrame.read(this)
public fun File.readDataRow(): AnyRow = DataRow.read(this)
