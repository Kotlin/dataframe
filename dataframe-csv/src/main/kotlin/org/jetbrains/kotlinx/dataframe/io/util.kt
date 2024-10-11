package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.api.ParserOptions

/**
 * You can add a default column type to the `colTypes` parameter
 * by setting the key to [DEFAULT_COL_TYPE] and the value to the desired type.
 */
public const val DEFAULT_COL_TYPE: String = ".default"

/**
 * Default strings that are considered null.
 */
public val defaultNullStrings: Set<String> =
    setOf("", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil")

public val defaultDelimParserOptions: ParserOptions =
    ParserOptions(nullStrings = defaultNullStrings)
